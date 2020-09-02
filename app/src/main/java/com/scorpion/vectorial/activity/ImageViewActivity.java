package com.scorpion.vectorial.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.scorpion.vectorial.AdUtils.FBInterstitial;
import com.scorpion.vectorial.R;

import java.io.File;
import java.io.IOException;

public class ImageViewActivity extends AppCompatActivity {

    String path;
    ImageView imageView;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        path = getIntent().getStringExtra("imageUrl");
        imageView = findViewById(R.id.imageView);
        imageView.setImageURI(Uri.parse(path));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_image_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                delete();
                return true;
            case R.id.share:
                share();
                return true;
            case R.id.setWallpaper:
                setWallpaper();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void delete() {
        builder = new AlertDialog.Builder(ImageViewActivity.this);
        builder.setMessage("Do you want to delete this image ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        File fdelete = new File(path);
                        if (fdelete.exists()) {
                            if (fdelete.delete()) {
                                finish();
                            } else {
                                dialog.cancel();
                            }
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle("Delete");
        alert.show();
    }

    public void share() {
        Intent share = new Intent("android.intent.action.SEND");
        share.setType("video/*");
        Uri pathUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", new File(path));

        share.putExtra("android.intent.extra.STREAM", pathUri);
        startActivity(Intent.createChooser(share, "Share"));
    }

    public void setWallpaper(){
        WallpaperManager manager = WallpaperManager.getInstance(getApplicationContext());
        try{
            manager.setBitmap(BitmapFactory.decodeFile(path));
            Toast.makeText(this, "Wallpaper set!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        FBInterstitial.getInstance().displayFBInterstitial(ImageViewActivity.this, new FBInterstitial.FbCallback() {
            public void callbackCall() {
              finish();
            }
        });
    }
}
