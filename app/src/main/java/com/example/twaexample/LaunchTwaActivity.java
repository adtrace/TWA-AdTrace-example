package com.example.twaexample;


import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.androidbrowserhelper.trusted.QualityEnforcer;
import com.google.androidbrowserhelper.trusted.TwaLauncher;
import com.google.androidbrowserhelper.trusted.TwaProviderPicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.browser.customtabs.CustomTabsCallback;
import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsServiceConnection;
import androidx.browser.customtabs.CustomTabsSession;
import androidx.browser.trusted.TrustedWebActivityIntentBuilder;

import io.adtrace.sdk.AdTrace;
import io.adtrace.sdk.AdTraceAttribution;
import io.adtrace.sdk.OnDeviceIdsRead;

public class LaunchTwaActivity extends Activity {
    /**
     * IMPORTANT NOTE:
     * As the main reason for launching TWA using custom methods and overriding some methods was to enable the app to
     * activate lifecycle dependent methods (e.g. AdTrace), there are different ways to do so.
     * the issue was fixed using `CustomTabsServiceConnection` that will allow the app to preserve its lifecycle and
     * avoid destroying it.
     * <p>
     * NOTE also:
     * As it is necessary for AdTrace Events to trigger from your server you need to send AdID through query params here on launch.
     * do not forget to include it in the implementation of your app! not useless to mention that you can trigger launch method in AdID callbacks.
     */

    private static final Uri LAUNCH_URI =
            Uri.parse("https://google.com/");

    private final TrustedWebActivityIntentBuilder builder = new TrustedWebActivityIntentBuilder(
            LAUNCH_URI);


    private CustomTabsCallback mCustomTabsCallback = new QualityEnforcer();
    /**
     * A bag to put all TwaLauncher in so we can dispose all at once.
     */
    private List<TwaLauncher> launchers = new ArrayList<>();

    private final CustomTabsServiceConnection customTabsServiceConnection = new CustomTabsServiceConnection() {
        CustomTabsSession mSession;
        private final static int SESSION_ID = 45;  // An arbitrary constant.

        @Override
        public void onCustomTabsServiceConnected(ComponentName name,
                                                 CustomTabsClient client) {
            mSession = client.newSession(null, SESSION_ID);

            if (mSession == null) {
                Toast.makeText(LaunchTwaActivity.this,
                        "Couldn't get session from provider.", Toast.LENGTH_LONG).show();
            }

            Intent intent = builder.build(mSession).getIntent();
            intent.putExtra(Intent.EXTRA_REFERRER,
                    Uri.parse("android-app://com.google.androidbrowserhelper?twa=true"));
            startActivity(intent);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mSession = null;
        }
    };

    private boolean serviceBound = false;

    private String gpsAdId = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_twa);
        handleLaunching();
    }


    private static final long LAUNCH_TIMEOUT = 1000 * 5;// milliseconds
    private boolean isWaitingForLaunch = true;

    private void handleLaunching() {

        CountDownTimer timeoutTimer = new CountDownTimer(LAUNCH_TIMEOUT, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                Toast.makeText(LaunchTwaActivity.this,"attribution took too long! Launching...",Toast.LENGTH_SHORT).show();
                launchTWAwithOutAdTraceAdid();
            }
        };

        AdTrace.getGoogleAdId(this, new OnDeviceIdsRead() {
            @Override
            public void onGoogleAdIdRead(String googleAdId) {
                gpsAdId = googleAdId;
            }
        });

        // get attribution object
        AdTraceAttribution attribution = AdTrace.getAttribution();
        // check if attribution is retrieved already!
        if (attribution != null) {
            Toast.makeText(this,"attribution already exist! Launching...",Toast.LENGTH_SHORT).show();
            // already got the attribution no need to do anything!
            Log.w("testtag123", "if (attribution !=null)");
            Log.w("testtag123", "Attribution exists! time to launch!");
            Log.w("testtag123", String.format("%s", attribution));
            launchTWAwithMyCustomUri();
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
                    launchTWAwithMyCustomUri();
                }
            });
        }

    }

    private void launchTWAwithMyCustomUri() {
        if (isWaitingForLaunch) {
            isWaitingForLaunch = false;
            // use adtrace adid in attribution or anything else
            Uri mycustomUri = LAUNCH_URI;// or whatever you want
            launch(mycustomUri);
        }
    }

    private void launchTWAwithOutAdTraceAdid() {
        // sometimes due to the user connection or infrastructure, attribution may not respond as expected
        // in that case its better to launch the app to get a better ux
        if (isWaitingForLaunch) {
            isWaitingForLaunch = false;
            // use googleAdId instead or anything
            Uri mycustomUri = LAUNCH_URI;// or whatever you want
            launch(mycustomUri);
        }
    }

    /**
     * Launches a Trusted Web Activity without any customizations
     *
     * @param view the source of the event invoking this method.
     */
    public void launch(View view) {
        TwaLauncher launcher = new TwaLauncher(this);
        launcher.launch(LAUNCH_URI);
        launchers.add(launcher);
    }

    public void launch(Uri uri) {
        TwaLauncher launcher = new TwaLauncher(this);
        launcher.launch(uri);
        launchers.add(launcher);
    }

    /**
     * Launches a Trusted Web Activity where navigations to non-validate domains will open in a Custom
     * Tab where the toolbar color has been customized.
     *
     * @param view the source of the event invoking this method.
     */
    public void launchWithCustomColors(View view) {
        TrustedWebActivityIntentBuilder builder = new TrustedWebActivityIntentBuilder(LAUNCH_URI)
                .setNavigationBarColor(Color.RED)
                .setToolbarColor(Color.BLUE);


        TwaLauncher launcher = new TwaLauncher(this);
        launcher.launch(builder, mCustomTabsCallback, null, null);
        launchers.add(launcher);
    }

    /**
     * Opens a Trusted Web Activity where multiple domains are validated to open in full screen.
     *
     * @param view the source of the event invoking this method.
     */
    public void launcherWithMultipleOrigins(View view) {
        List<String> origins = Arrays.asList(
                "https://www.wikipedia.org/",
                "https://www.example.com/"
        );

        TrustedWebActivityIntentBuilder builder = new TrustedWebActivityIntentBuilder(LAUNCH_URI)
                .setAdditionalTrustedOrigins(origins);


        TwaLauncher launcher = new TwaLauncher(this);
        launcher.launch(builder, mCustomTabsCallback, null, null);
        launchers.add(launcher);
    }

    /**
     * Open a Trusted Web Activity where the loaded URL will receive a customized Referrer.
     *
     * @param view the source of the event invoking this method.
     */
    public void launchWithCustomReferrer(View view) {
        // The ergonomics will be improved here, since we're basically replicating the work of
        // TwaLauncher, see https://github.com/GoogleChrome/android-browser-helper/issues/13.

        TwaProviderPicker.Action action = TwaProviderPicker.pickProvider(getPackageManager());
        if (!serviceBound) {
            CustomTabsClient
                    .bindCustomTabsService(this, action.provider, customTabsServiceConnection);
            serviceBound = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (TwaLauncher launcher : launchers) {
            launcher.destroy();
        }
        if (serviceBound) {
            unbindService(customTabsServiceConnection);
            serviceBound = false;
        }
    }


}
