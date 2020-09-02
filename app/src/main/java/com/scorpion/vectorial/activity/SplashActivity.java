package com.scorpion.vectorial.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import com.scorpion.vectorial.R;

public class SplashActivity extends AppCompatActivity {

    RelativeLayout rlt_splash_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_splash);
        rlt_splash_logo = (RelativeLayout) findViewById(R.id.rlt_splash_logo);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        rlt_splash_logo.startAnimation(alphaAnimation);
        alphaAnimation.setDuration(1500);
        alphaAnimation.setInterpolator(new DecelerateInterpolator());
        alphaAnimation.setFillAfter(true);
        launchActivity();
    }

    private void launchActivity() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent;
                intent = new Intent(SplashActivity.this, StartActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

}
