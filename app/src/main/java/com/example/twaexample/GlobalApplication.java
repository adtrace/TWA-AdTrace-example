package com.example.twaexample;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.adtrace.sdk.AdTrace;
import io.adtrace.sdk.AdTraceConfig;
import io.adtrace.sdk.LogLevel;
import io.adtrace.sdk.OnDeviceIdsRead;

public class GlobalApplication extends Application {

    private final String adtraceAppToken = "123abcxyz";


    @Override
    public void onCreate() {
        super.onCreate();
        AdTraceConfig adTraceConfig = new AdTraceConfig(this, adtraceAppToken, AdTraceConfig.ENVIRONMENT_SANDBOX);
        adTraceConfig.setLogLevel(LogLevel.VERBOSE);
        AdTrace.onCreate(adTraceConfig);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                AdTrace.onResume();
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                AdTrace.onPause();
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });

        // todo: Getting GPS AdId from AdTrace SDK
        AdTrace.getGoogleAdId(this, new OnDeviceIdsRead() {
            @Override
            public void onGoogleAdIdRead(String googleAdId) {
                // after device id read
            }
        });
    }
}
