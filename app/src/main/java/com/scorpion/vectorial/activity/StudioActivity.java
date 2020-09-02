package com.scorpion.vectorial.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.LinearLayout;

import com.facebook.ads.AdSize;
import com.scorpion.vectorial.R;
import com.scorpion.vectorial.adapter.StudioAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class StudioActivity extends AppCompatActivity {

    ArrayList<File> videopaths = new ArrayList<>();
    StudioAdapter studioAdapter;
    RecyclerView videoListRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studio);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
        loadBannerFirst(StudioActivity.this,adContainer);

        videoListRecycler = findViewById(R.id.imageListRecycler);
    }
    public void loadBannerFirst(Activity act, LinearLayout laybanner){

        //banner ad
        com.facebook.ads.AdView adViewfb = new com.facebook.ads.AdView(act, getString(R.string.banner_ad_unit_id), AdSize.BANNER_HEIGHT_50);

        // Add the ad view to your activity layout
        laybanner.addView(adViewfb);
        // Request an ad
        adViewfb.loadAd();
    }

    @Override
    public void onResume() {
        super.onResume();
        videopaths.clear();
        new dataloader().execute();
        if (studioAdapter!=null)
            studioAdapter.notifyDataSetChanged();
    }

    public class dataloader extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getAllImages();
            Collections.sort(videopaths, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    long s11 = o1.lastModified();
                    long s12 = o2.lastModified();
                    return Long.valueOf(s12).compareTo(s11);
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            studioAdapter = new StudioAdapter(videopaths,StudioActivity.this);
            videoListRecycler.setLayoutManager(new GridLayoutManager(StudioActivity.this,3));
            videoListRecycler.setAdapter(studioAdapter);
        }
    }

    private void getAllImages() {
        String path = Environment.getExternalStorageDirectory() + File.separator + getString(R.string.app_name);
        Log.e("path123",path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getAbsolutePath().contains(".jpg")) {
                    videopaths.add(file);
                }
            }
        }
    }
}
