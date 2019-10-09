/**
 * 
 */
package com.infinity.android.keeper.view;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
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

        final View headerView = KeeperUtils.getPageTitleView(appContext, getString(R.string.page_about_us));
        addPageHeaderView(headerView);

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
