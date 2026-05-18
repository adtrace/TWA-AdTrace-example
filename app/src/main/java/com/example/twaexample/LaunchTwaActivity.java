package com.example.twaexample;

import android.net.Uri;
import android.os.Bundle;

import io.adtrace.sdk.AdTrace;

public class LaunchTwaActivity extends com.google.androidbrowserhelper.trusted.LauncherActivity {

    private String mGoogleAdId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdTrace.getGoogleAdId(this, googleAdId -> {
            mGoogleAdId = googleAdId != null ? googleAdId : "";
            launchTwa();
        });
    }

    @Override
    protected boolean shouldLaunchImmediately() {
        return false;
    }

    @Override
    protected Uri getLaunchingUrl() {
        Uri base = Uri.parse("YOUR_WEB_PAGE_BASE_URL");
        if (mGoogleAdId == null) {
            mGoogleAdId = "";
        }
        return base.buildUpon()
                .appendQueryParameter("gps_adid", mGoogleAdId)
                .build();
    }
}
