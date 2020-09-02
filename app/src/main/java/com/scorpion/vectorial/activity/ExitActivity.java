package com.scorpion.vectorial.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.scorpion.vectorial.R;

import java.util.ArrayList;
import java.util.List;

public class ExitActivity extends AppCompatActivity implements Animation.AnimationListener {

    private NativeAd nativeAd;
    TextView btnYes, btnNo,btnrate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);

        btnYes = findViewById(R.id.btnYes);
        btnNo = findViewById(R.id.btnNo);
        btnrate=findViewById(R.id.btnrate);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ExitActivity.this, StartActivity.class));
                finish();
            }
        });

        btnrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("market://details?id="
                        + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(goToMarket);
            }
        });
                loadNativeAd();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ExitActivity.this, StartActivity.class));
        finish();
    }


    private void loadNativeAd() {

//        final CustomTextView customTextView = (CustomTextView) findViewById(R.id.txt_adload);
        this.nativeAd = new NativeAd(getApplicationContext(),getResources().getString(R.string.native_ad_unit_Id));
        this.nativeAd.setAdListener(new NativeAdListener() {
            public void onAdClicked(Ad ad) {
            }

            public void onError(Ad ad, AdError adError) {

            }

            public void onLoggingImpression(Ad ad) {
            }

            public void onMediaDownloaded(Ad ad) {
            }

            public void onAdLoaded(Ad ad) {
                inflateAd(nativeAd);
//                customTextView.setVisibility(8);
            }
        });
        this.nativeAd.loadAd();
    }

    public void inflateAd(NativeAd nativeAd2) {
        nativeAd2.unregisterView();
        NativeAdLayout nativeAdLayout = (NativeAdLayout) findViewById(R.id.native_ad_container);
        int i = 0;
        LinearLayout adView = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.fbnative_ad, nativeAdLayout, false);
        nativeAdLayout.addView(adView);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(getApplicationContext(), nativeAd2, nativeAdLayout);
        linearLayout.removeAllViews();
        linearLayout.addView(adOptionsView, 0);
        AdIconView adIconView = (AdIconView) adView.findViewById(R.id.native_ad_icon);
        TextView textView = (TextView) adView.findViewById(R.id.native_ad_title);
        com.facebook.ads.MediaView mediaView = (com.facebook.ads.MediaView) adView.findViewById(R.id.native_ad_media);
        TextView textView2 = (TextView) adView.findViewById(R.id.native_ad_sponsored_label);
        Button button = (Button) adView.findViewById(R.id.native_ad_call_to_action);
        textView.setText(nativeAd2.getAdvertiserName());
        ((TextView) adView.findViewById(R.id.native_ad_body)).setText(nativeAd2.getAdBodyText());
        ((TextView) adView.findViewById(R.id.native_ad_social_context)).setText(nativeAd2.getAdSocialContext());
        if (!nativeAd2.hasCallToAction()) {
            i = 4;
        }
        button.setVisibility(i);
        button.setText(nativeAd2.getAdCallToAction());
        textView2.setText(nativeAd2.getSponsoredTranslation());
        ArrayList arrayList = new ArrayList();
        arrayList.add(textView);
        arrayList.add(button);
        nativeAd2.registerViewForInteraction((View) adView, mediaView, (com.facebook.ads.MediaView) adIconView, (List<View>) arrayList);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
