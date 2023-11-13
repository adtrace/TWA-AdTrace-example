package com.example.twaexample;


import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.androidbrowserhelper.trusted.LauncherActivity;

import io.adtrace.sdk.AdTrace;
import io.adtrace.sdk.AdTraceAttribution;
import io.adtrace.sdk.OnDeviceIdsRead;

public class LaunchTwaActivity extends LauncherActivity {
    private String gpsAdId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleLaunch();
        // `super.onCreate()` may have called `finish()`. In this case, we don't do any work.
        if (isFinishing()) {
            return;
        }
    }

    @Override
    protected boolean shouldLaunchImmediately() {
        return false;
    }

    private static final long LAUNCH_TIMEOUT = 1000 * 3;// milliseconds
    private boolean isWaitingForLaunch = true;
    private void handleLaunch(){
        new CountDownTimer(LAUNCH_TIMEOUT, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                Toast.makeText(LaunchTwaActivity.this,"attribution took too long! Launching...",Toast.LENGTH_SHORT).show();
                launchIt();
            }
        }.start();
        AdTrace.getGoogleAdId(this, new OnDeviceIdsRead() {
            @Override
            public void onGoogleAdIdRead(String googleAdId) {
                gpsAdId = googleAdId;
            }
        });
        AdTraceAttribution attribution = AdTrace.getAttribution();
        if (attribution != null) {
            Toast.makeText(this,"attribution already exist! Launching...",Toast.LENGTH_SHORT).show();
            // already got the attribution no need to do anything!
            Log.w("testtag123", "if (attribution !=null)");
            Log.w("testtag123", "Attribution exists! time to launch!");
            Log.w("testtag123", String.format("%s", attribution));
            launchIt();
        } else {
            Log.d("testtag123", "attribution is not retrieved yet!");
            GlobalApplication application = (GlobalApplication) getApplication();
            application.setAdtraceAttributionChangeListener(new AdTraceAttributionChangeListener() {
                @Override
                public void onAdTraceAttributionChangeListener(AdTraceAttribution adTraceAttribution) {
                    Toast.makeText(LaunchTwaActivity.this,"attribution got from Server! Launching...",Toast.LENGTH_SHORT).show();

                    Log.w("testtag123", "onAdTraceAttributionChangeListener");
                    Log.w("testtag123", String.format("%s", adTraceAttribution));
                    Log.w("testtag123", "Attribution exists! time to launch!");
                    launchIt();
                }
            });
        }
    }

    private void launchIt(){
        if (isWaitingForLaunch){
            isWaitingForLaunch = false;
            launchTwa();
        }
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
