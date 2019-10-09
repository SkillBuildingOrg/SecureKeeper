/**
 * 
 */
package com.infinity.android.keeper.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.infinity.android.keeper.BaseKeeperActivity;
import com.infinity.android.keeper.R;
import com.infinity.android.keeper.manager.KeeperManager;
import com.infinity.android.keeper.utils.KeeperUtils;

/**
 * @author joshiroh
 *
 */
public final class KeeperTutorialActivity extends BaseKeeperActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_tutorial_page);

        Button continueButton = (Button) findViewById(R.id.buttonStep3);
        continueButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                KeeperManager.getInstance().setUpdatedPolicyInUse();
                Intent configIntent = new Intent(KeeperTutorialActivity.this, EditProfileActivity.class);
                startActivity(configIntent);
                finish();
            }
        });

        final TextView policyLinkText = (TextView) findViewById(R.id.policyLinkText);
        policyLinkText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent privacyIntent = new Intent(KeeperTutorialActivity.this, AppPolicyInfoActivity.class);
                startActivity(privacyIntent);
            }
        });

        KeeperUtils.initActionBar(appContext, R.string.page_entry_list, false);
    }

    /* (non-Javadoc)
     * @see com.infinity.android.keeper.BaseKeeperActivity#cleanup()
     */
    @Override
    public void cleanup() {
        // TODO Auto-generated method stub

    }

}
