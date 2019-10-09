/**
 * 
 */
package com.infinity.android.keeper.view;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.common.base.Throwables;
import com.infinity.android.keeper.BaseKeeperActivity;
import com.infinity.android.keeper.R;
import com.infinity.android.keeper.utils.KeeperUtils;

/**
 * @author joshiroh
 *
 */
public final class AboutAppActivity extends BaseKeeperActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_about_us);

        final TextView appVersionInfo = (TextView) findViewById(R.id.appVersionInfo);
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            Log.d("VersionName", "" + Throwables.getStackTraceAsString(e));
        }
        if(null != pInfo) {
            appVersionInfo.setText(getString(R.string.text_label_app_version) + " " + pInfo.versionName);
        } else {
            appVersionInfo.setVisibility(View.GONE);
        }

        final TextView policyLinkText = (TextView) findViewById(R.id.policyLinkText);
        policyLinkText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent privacyIntent = new Intent(AboutAppActivity.this, AppPolicyInfoActivity.class);
                startActivity(privacyIntent);
                finish();
            }
        });

        final TextView ratingLinkText = (TextView) findViewById(R.id.ratingLinkText);
        ratingLinkText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                Uri uri = Uri.parse("market://details?id=" + appContext.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + appContext.getPackageName())));
                }
            }
        });

        KeeperUtils.initActionBar(appContext, R.string.page_about_us, true);
    }

    @Override
    public void onBackPressed() {
        Intent listIntent = new Intent(this, KeeperListActivity.class);
        startActivity(listIntent);
        super.onBackPressed();
    }

    /* (non-Javadoc)
     * @see com.infinity.android.keeper.BaseKeeperActivity#cleanup()
     */
    @Override
    public void cleanup() {
        // TODO Auto-generated method stub

    }

}
