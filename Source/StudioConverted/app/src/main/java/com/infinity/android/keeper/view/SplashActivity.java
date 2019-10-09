/**
 * 
 */
package com.infinity.android.keeper.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.infinity.android.keeper.BaseKeeperActivity;
import com.infinity.android.keeper.R;
import com.infinity.android.keeper.adaptors.LegalPolicyWebViewclient;
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
            if(KeeperManager.getInstance().isUsingUpdatedPolicy()) {
                startupIntent = new Intent(SplashActivity.this, LoginKeeperActivity.class);
            } else {
                showPolicyUpdateDialog();
                return;
            }

        }
        startActivity(startupIntent);
        finish();
    }

    @Override
    public void cleanup() {
        isAppLaunchComplete = false;
    }

    /**
     * Show Policy update dialog
     */
    private void showPolicyUpdateDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        alert.setCancelable(false);
        alert.setTitle(R.string.alert_policy_update);

        String fileName = "file:///android_asset/legal/privacy_policy.html";

        WebView webView = new WebView(this);
        webView.setWebViewClient(new LegalPolicyWebViewclient(this));
        WebSettings webSettings = webView.getSettings();
        webSettings.setDefaultFontSize(13);
        webSettings.setJavaScriptEnabled(false);
        webView.loadUrl(fileName);

        webView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                // Disabling copy-paste for long click
                return true;
            }
        });
        webView.setLongClickable(false);
        alert.setView(webView);
        alert.setIcon(R.drawable.app_icon);
        alert.setPositiveButton(R.string.button_accept, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(final DialogInterface dialog, final int id){
                KeeperManager.getInstance().setUpdatedPolicyInUse();
                finishSplashActivity();
            }
        });
        alert.setNegativeButton(R.string.button_decline, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(final DialogInterface dialog, final int id){
                onBackPressed();
            }
        });
        alert.show();
    }
}
