package com.scorpion.vectorial.AdUtils;

import android.app.Application;

import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;

public class Applicationclass extends Application {

	private static Applicationclass instance;

	@Override
	public void onCreate() {

		super.onCreate();
		instance = this;
		AudienceNetworkAds.initialize(this);
		AdSettings.addTestDevice("f1966ccd-b60d-4b3f-b658-5b48451b399f");
		FBInterstitial.getInstance().loadFBInterstitial(this);

	}

}
