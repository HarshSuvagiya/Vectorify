package com.scorpion.vectorial.AdUtils;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.scorpion.vectorial.R;

public class FBInterstitial {
    private static FBInterstitial mInstance;
    public InterstitialAd interstitial;
    boolean isloading = false;
    boolean isshow = true;
    FbCallback myCallback1;
    public static boolean isPurchase = false;
    public interface FbCallback {
        void callbackCall();
    }

    public static FBInterstitial getInstance() {
        if (mInstance == null) {
            mInstance = new FBInterstitial();
        }
        return mInstance;
    }

    public void loadFBInterstitial(Context activity) {
        this.interstitial = new InterstitialAd(activity,activity.getString(R.string.interstitial_ad_unit_id));
        this.interstitial.loadAd();
        this.isloading = true;
        this.interstitial.setAdListener(new InterstitialAdListener() {
            public void onAdClicked(Ad ad) {
            }

            public void onInterstitialDisplayed(Ad ad) {
            }

            public void onLoggingImpression(Ad ad) {
            }

            public void onInterstitialDismissed(Ad ad) {
                try {
                    if (FBInterstitial.this.myCallback1 != null) {
                        FBInterstitial.this.myCallback1.callbackCall();
                        FBInterstitial.this.myCallback1 = null;
                    }
                    FBInterstitial.this.interstitial.loadAd();
                    FBInterstitial.this.setdelay();
                    Log.d("AdStatus", "Ad Load");
                    FBInterstitial.this.isloading = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void onError(Ad ad, AdError adError) {
                FBInterstitial fBInterstitial = FBInterstitial.this;
                fBInterstitial.isloading = false;
                fBInterstitial.setdelay();
                Log.d("AdStatusvideo", adError.getErrorCode() + " " + adError.getErrorMessage());
            }

            public void onAdLoaded(Ad ad) {
                FBInterstitial.this.isloading = false;
                Log.d("AdStatus", "Ad loaded");
            }
        });
    }

    public void displayFBInterstitial(Context activity, FbCallback fbCallback) {
        try {
            this.myCallback1 = fbCallback;
            if (isPurchase) {
                if (this.myCallback1 != null) {
                    this.myCallback1.callbackCall();
                    this.myCallback1 = null;
                }
            } else if (!this.isshow) {
                if (this.myCallback1 != null) {
                    this.myCallback1.callbackCall();
                    this.myCallback1 = null;
                }
            } else if (this.interstitial != null) {
                if (this.interstitial.isAdLoaded()) {
                    Handler h=new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            interstitial.show();
                        }
                    },1500);
                } else if (!this.isloading) {
                    this.interstitial.loadAd();
                    this.isloading = true;
                    if (this.myCallback1 != null) {
                        this.myCallback1.callbackCall();
                        this.myCallback1 = null;
                    }
                } else if (this.myCallback1 != null) {
                    this.myCallback1.callbackCall();
                    this.myCallback1 = null;
                }
            } else if (this.myCallback1 != null) {
                this.myCallback1.callbackCall();
                this.myCallback1 = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setdelay() {
        this.isshow = false;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                FBInterstitial.this.isshow = true;
            }
        }, 0);
    }
}
