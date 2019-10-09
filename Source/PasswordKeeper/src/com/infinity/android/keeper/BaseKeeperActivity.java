/**
 * 
 */
package com.infinity.android.keeper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.common.base.Throwables;
import com.infinity.android.keeper.utils.KeeperUtils;
import com.infinity.android.keeper.view.LoginKeeperActivity;

/**
 * @author joshiroh
 *
 */
public abstract class BaseKeeperActivity extends Activity {

    public static final int NO_HEADER_MENU = -1;
    protected BaseKeeperActivity appContext;
    private RelativeLayout headerContainerLayout;
    private LinearLayout menuContainerLayout;
    private LinearLayout pageContainerLayout;
    protected LayoutInflater inflater;
    private boolean isPaused = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = this;
        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        KeeperUtils.setAppContext(appContext);
        super.setContentView(R.layout.base_layout);
        headerContainerLayout = (RelativeLayout) findViewById(R.id.headerContainer);
        menuContainerLayout = (LinearLayout) findViewById(R.id.menuContainer);
        pageContainerLayout = (LinearLayout) findViewById(R.id.pageContainer);

        /*
         * Disable screen capture
         */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE, android.view.WindowManager.LayoutParams.FLAG_SECURE);
        }
    }

    @Override
    public void setContentView(final int layoutResID) {
        View v = inflater.inflate(layoutResID, null);
        pageContainerLayout.removeAllViews();
        pageContainerLayout.addView(v);
    }

    /**
     * Add Page header view
     * @param headerView
     */
    protected void addPageHeaderView(final View headerView) {
        headerContainerLayout.removeAllViews();
        headerContainerLayout.addView(headerView);
    }

    /**
     * Add bottom menu bar view
     * @param menuBarView
     */
    protected void addPageMenuBarLayout(final View menuBarView) {
        menuContainerLayout.setVisibility(View.VISIBLE);
        menuContainerLayout.removeAllViews();
        menuBarView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        menuContainerLayout.addView(menuBarView);
    }

    @Override
    protected void onPause() {
        isPaused = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isPaused && KeeperUtils.isUserLoggedin()) {
            Intent loginIntent = new Intent(this, LoginKeeperActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            View parentView = findViewById(android.R.id.content).getRootView();
            KeeperUtils.unbindAndClearViews(parentView);
            cleanup();
        } catch(Exception e) {
            Log.d("BaseKeeperActivity", "onDestroy() : " + Throwables.getStackTraceAsString(e));
        }
        super.onDestroy();
    }

    /**
     * @return LayoutInflater
     */
    public LayoutInflater getInflater() {
        return inflater;
    }

    public abstract void cleanup();
}
