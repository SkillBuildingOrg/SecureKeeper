/**
 * 
 */
package com.infinity.android.keeper.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;

import com.infinity.android.keeper.BaseKeeperActivity;
import com.infinity.android.keeper.R;
import com.infinity.android.keeper.manager.KeeperManager;

/**
 * @author joshiroh
 *
 */
public final class SplashActivity extends BaseKeeperActivity {
    // Splash screen timer
    private static final int SPLASH_TIME_OUT = 1400;
    private boolean isAppLaunchComplete = true;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LayoutInflater.from(appContext).inflate(R.layout.layout_splash, null));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAppLaunchComplete) {
                    finishSplashActivity();
                }
            }
        }, SPLASH_TIME_OUT);
    }

    /**
     * End splash screen and move to next.
     */
    private void finishSplashActivity() {
        Intent startupIntent;
        if(!KeeperManager.getInstance().isKeeperConfigured()) {
            startupIntent = new Intent(SplashActivity.this, KeeperTutorialActivity.class);
        } else {
            startupIntent = new Intent(SplashActivity.this, LoginKeeperActivity.class);
        }
        startActivity(startupIntent);
        finish();
    }

    @Override
    public void cleanup() {
        isAppLaunchComplete = false;
    }
}
