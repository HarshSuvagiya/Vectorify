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
		AdSettings.addTestDevice("5c661a62-7d10-4f8c-8c2b-4bbced23b73a");
		FBInterstitial.getInstance().loadFBInterstitial(this);

	}

}
