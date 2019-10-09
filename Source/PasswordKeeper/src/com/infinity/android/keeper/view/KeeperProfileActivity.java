/**
 * 
 */
package com.infinity.android.keeper.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.infinity.android.keeper.BaseKeeperActivity;
import com.infinity.android.keeper.R;
import com.infinity.android.keeper.data.model.ProfileInfo;
import com.infinity.android.keeper.manager.KeeperManager;
import com.infinity.android.keeper.manager.UpdateManager;
import com.infinity.android.keeper.utils.Configs;
import com.infinity.android.keeper.utils.KeeperUtils;

/**
 * @author joshiroh
 *
 */
public final class KeeperProfileActivity extends BaseKeeperActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_keeper_profile);

        final ProfileInfo profileInfo = UpdateManager.getInstance().getProfileInformation();
        if(null != profileInfo) {
            String userName = profileInfo.getFullUserName();
            if(!Strings.isNullOrEmpty(userName)) {
                TextView userNameTextView = (TextView) findViewById(R.id.userNameTextView);
                userNameTextView.setText(userName);
            }

            String userLocation = profileInfo.getUserLocationDetails();
            if(!Strings.isNullOrEmpty(userLocation)) {
                TextView userLocationTextView = (TextView) findViewById(R.id.locationTextView);
                userLocationTextView.setText(userLocation);
            }

            if(!Strings.isNullOrEmpty(profileInfo.getUserMailId())) {
                TextView userEmailTextView = (TextView) findViewById(R.id.userEmailId);
                userEmailTextView.setText(profileInfo.getUserMailId());
            }

            if(!Strings.isNullOrEmpty(profileInfo.getAlternatePhoneNumber())) {
                TextView userAltPhoneNumberTextView = (TextView) findViewById(R.id.alternatePhoneNumber);
                userAltPhoneNumberTextView.setText(profileInfo.getAlternatePhoneNumber());
            }

            if(!Strings.isNullOrEmpty(profileInfo.getPhoneNumber())) {
                TextView userPhoneNumberTextView = (TextView) findViewById(R.id.userPhoneNumber);
                userPhoneNumberTextView.setText(profileInfo.getPhoneNumber());
            }
        }

        final Button buttonChangePasscode = (Button) findViewById(R.id.buttonChangePassword);
        buttonChangePasscode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent profileIntent = new Intent(KeeperProfileActivity.this, CreateKeeperActivity.class);
                profileIntent.putExtra(Configs.KEY_CHANGE_PASSWORD, true);
                startActivity(profileIntent);
                finish();
            }
        });

        final ImageView editProfileIcon = (ImageView) findViewById(R.id.editProfileIcon);
        editProfileIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                confirmEditProfile();
            }
        });
    }

    /**
     * Show password prompt to enable edit profile flow
     */
    private void confirmEditProfile() {
        final LayoutInflater inflater = this.getLayoutInflater();
        final LinearLayout passwordLayout = (LinearLayout)inflater.inflate(R.layout.layout_password_prompt, null);
        final EditText enterPassword = (EditText) passwordLayout.findViewById(R.id.enter_password_prompt);
        final DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                KeeperUtils.hideKeyboard(appContext, enterPassword);
                dialog.dismiss();
                String token = enterPassword.getText().toString().trim();
                if(KeeperManager.getInstance().isKeeperTokenValid(token)) {
                    Intent editEntryIntent = new Intent(KeeperProfileActivity.this, EditProfileActivity.class);
                    editEntryIntent.putExtra(Configs.KEY_EDIT_PROFILE_MODE, true);
                    startActivity(editEntryIntent);
                    finish();
                } else {
                    KeeperUtils.showErrorAlertMessage(appContext, getString(R.string.alert_msg_incorrect_token));
                }
            }
        };
        KeeperUtils.promptForPassword(appContext, passwordLayout, positiveListener);
    }

    @Override
    public void onBackPressed() {
        Intent listEntryIntent = new Intent(KeeperProfileActivity.this, KeeperListActivity.class);
        startActivity(listEntryIntent);
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
