package com.example.twaexample;


import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.androidbrowserhelper.trusted.LauncherActivity;

import io.adtrace.sdk.AdTrace;
import io.adtrace.sdk.OnDeviceIdsRead;

public class LaunchTwaActivity extends LauncherActivity {
    private String gpsAdId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // `super.onCreate()` may have called `finish()`. In this case, we don't do any work.
        if (isFinishing()) {
            return;
        }

        AdTrace.getGoogleAdId(this, new OnDeviceIdsRead() {
            @Override
            public void onGoogleAdIdRead(String googleAdId) {
                gpsAdId = googleAdId;
                launchTwa();

            }
        });


    }

    @Override
    protected boolean shouldLaunchImmediately() {
        // launchImmediately() returns `false` so we can wait until Firebase Analytics is ready
        // and then launch the Trusted Web Activity with `launch()`.
        return false;
    }

    @Override
    protected Uri getLaunchingUrl() {
        Uri uri = super.getLaunchingUrl();
        // Attach the Firebase instance Id to the launchUrl. This example uses "appInstanceId" as
        // the parameter name.
        return uri.buildUpon()
                .appendQueryParameter("gps_ad_id", gpsAdId)
                .build();
    }
}
