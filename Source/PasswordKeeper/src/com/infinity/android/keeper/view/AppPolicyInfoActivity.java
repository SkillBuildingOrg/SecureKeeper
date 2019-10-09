/**
 * 
 */
package com.infinity.android.keeper.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.infinity.android.keeper.BaseKeeperActivity;
import com.infinity.android.keeper.R;
import com.infinity.android.keeper.adaptors.LegalPolicyWebViewclient;
import com.infinity.android.keeper.utils.KeeperUtils;

/**
 * @author joshiroh
 *
 */
public final class AppPolicyInfoActivity extends BaseKeeperActivity {


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_policy_info);

        final View headerView = KeeperUtils.getPageTitleView(appContext, getString(R.string.page_policy_info));
        addPageHeaderView(headerView);

        String fileName = "file:///android_asset/legal/privacy_policy.html";

        WebView webView = (WebView)findViewById(R.id.legal_content_view);
        webView.setWebViewClient(new LegalPolicyWebViewclient(this));
        WebSettings webSettings = webView.getSettings();
        // Setting size because of Horizontal scrolling of content in Finnish Localization
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
    }

    @Override
    public void onBackPressed() {
        if(KeeperUtils.isUserLoggedin()) {
            Intent listIntent = new Intent(this, KeeperListActivity.class);
            startActivity(listIntent);
        }
        super.onBackPressed();
    }

    @Override
    public void cleanup() {
        // TODO Auto-generated method stub

    }

}
