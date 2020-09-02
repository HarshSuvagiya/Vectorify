package com.scorpion.vectorial.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.facebook.ads.AdSize;
import com.scorpion.vectorial.AdUtils.FBInterstitial;
import com.scorpion.vectorial.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EditorActivity extends AppCompatActivity {

    SeekBar sizeSeekbar;
    SeekBar rotationSeekbar;
    ImageView foreground;
    ImageView background;
    ImageView left;
    ImageView up;
    ImageView down;
    ImageView right;
    ImageView alignVertical;
    ImageView alignHorizontal;
    ImageView refresh;
    ImageView download;
    ImageView setWallpaper;
    int progressFinal;
    int leftMargin;
    int topMargin;
    int rightMargin;
    int bottomMargin;
    RelativeLayout relativeLayout;
    ArrayList<Drawable> drawables = new ArrayList<>();
    int height;
    int width;
    int foregroundItem,backgroundColor = Color.BLACK,tintColor = Color.RED;
    boolean isGradientApplied;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        init();
        getScreenResolution();
        try {
            getDrawable();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sizeSeekbar.setMax(width);
        sizeSeekbar.setProgress(width);
        progressFinal = width;
//        foreground.setImageResource(drawa[2]);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                storeImage(getBitmapFromView(relativeLayout));
            }
        });

        foregroundItem = getIntent().getIntExtra("foreground",0);
        backgroundColor = getIntent().getIntExtra("background",0);
        tintColor = getIntent().getIntExtra("tint",0);
        isGradientApplied = getIntent().getBooleanExtra("isGradientApplied",false);

        Drawable unwrappedDrawable = AppCompatResources.getDrawable(this, foregroundItem);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, tintColor);
        foreground.setImageResource(foregroundItem);
        background.setBackgroundColor(backgroundColor);

        if (isGradientApplied)
            background.setImageDrawable(MainActivity.gradientDrawable);

        buttonClick();

        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
        loadBannerFirst(EditorActivity.this,adContainer);

    }

    public static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }
    public void loadBannerFirst(Activity act, LinearLayout laybanner){

        //banner ad
        com.facebook.ads.AdView adViewfb = new com.facebook.ads.AdView(act, getString(R.string.banner_ad_unit_id), AdSize.BANNER_HEIGHT_50);

        // Add the ad view to your activity layout
        laybanner.addView(adViewfb);
        // Request an ad
        adViewfb.loadAd();
    }
    private void getScreenResolution() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        Log.e("DEVICEH", String.valueOf(height));
        Log.e("DEVICEW", String.valueOf(width));
    }

    public void init() {
        sizeSeekbar = findViewById(R.id.sizeSeekbar);
        foreground = findViewById(R.id.foreground);
        background = findViewById(R.id.background);
        left = findViewById(R.id.left);
        up = findViewById(R.id.up);
        down = findViewById(R.id.down);
        right = findViewById(R.id.right);
        alignVertical = findViewById(R.id.alignVertical);
        alignHorizontal = findViewById(R.id.alignHorizontal);
        refresh = findViewById(R.id.refresh);
        relativeLayout = findViewById(R.id.relativeLayout);
        download = findViewById(R.id.download);
        rotationSeekbar = findViewById(R.id.rotationSeekbar);
        setWallpaper = findViewById(R.id.setWallpaper);
    }
    @Override
    public void onBackPressed() {
        FBInterstitial.getInstance().displayFBInterstitial(EditorActivity.this, new FBInterstitial.FbCallback() {
            public void callbackCall() {
                finish();
            }
        });
    }
    public void buttonClick() {

        sizeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressFinal = progress;
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(progress, progress);
                layoutParams.setMargins(rightMargin, bottomMargin, leftMargin, topMargin);
                foreground.setLayoutParams(layoutParams);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        rotationSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                foreground.setRotation((float)progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        setWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWallPaper();
            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftMargin += 50;
                if (rightMargin > 50)
                    rightMargin -= 50;
                setParams();
            }
        });

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topMargin += 50;
                if (bottomMargin > 50)
                    bottomMargin -= 50;
                setParams();
            }
        });
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomMargin += 50;
                if (topMargin > 50)
                    topMargin -= 50;
                setParams();
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rightMargin += 50;
                if (leftMargin > 50)
                    leftMargin -= 50;
                setParams();
            }
        });

        alignHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftMargin = 0;
                rightMargin = 0;
                setParams();
            }
        });
        alignVertical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topMargin = 0;
                bottomMargin = 0;
                setParams();
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftMargin = 0;
                rightMargin = 0;
                topMargin = 0;
                bottomMargin = 0;
                setParams();
                sizeSeekbar.setProgress(width);
                rotationSeekbar.setProgress(0);
            }
        });
    }

    public void setParams() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(progressFinal, progressFinal);
        layoutParams.setMargins(rightMargin, bottomMargin, leftMargin, topMargin);
        foreground.setLayoutParams(layoutParams);
    }

    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Toast.makeText(this, "Saved successfully!!!", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/"
                + getString(R.string.app_name));

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName = "MI_" + timeStamp + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    public void getDrawable() throws IOException {
        AssetManager am = getAssets();
        String[] files = am.list("zodiac");
        for (String file : files) {
            Drawable d = Drawable.createFromStream(getResources().getAssets().open("zodiac/" + "zodiac_cancer"), null);
            drawables.add(d);
        }
    }

    public void setWallPaper(){
        WallpaperManager manager = WallpaperManager.getInstance(getApplicationContext());
        try{
            manager.setBitmap(getBitmapFromView(relativeLayout));
            Toast.makeText(this, "Wallpaper set!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
        }
    }
}
