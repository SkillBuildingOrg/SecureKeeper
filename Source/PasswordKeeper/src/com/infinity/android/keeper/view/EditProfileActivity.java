/**
 * 
 */
package com.infinity.android.keeper.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.common.base.Strings;
import com.infinity.android.keeper.BaseKeeperActivity;
import com.infinity.android.keeper.R;
import com.infinity.android.keeper.data.model.ProfileInfo;
import com.infinity.android.keeper.manager.UpdateManager;
import com.infinity.android.keeper.utils.Configs;
import com.infinity.android.keeper.utils.KeeperUtils;

/**
 * @author joshiroh
 *
 */
public final class EditProfileActivity extends BaseKeeperActivity {

    /*
     * Check if the activity is launched to edit existing profile or config profile during first launch
     */
    private boolean isEditMode = false;

    private String userName;
    private String userSurname;
    private String emailId;
    private String altPhoneNumber;
    private String phoneNumber;
    private String city;
    private String state;
    private String country;

    private LinearLayout personalInfoContainer;
    private LinearLayout locationInfoContainer;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_edit_profile);
        addPageHeaderView(KeeperUtils.getPageTitleView(appContext, getString(R.string.page_edit_profile)));
        personalInfoContainer = (LinearLayout) findViewById(R.id.profileInfoContainer);
        locationInfoContainer = (LinearLayout) findViewById(R.id.profileLocationContainer);

        personalInfoContainer.setVisibility(View.VISIBLE);
        locationInfoContainer.setVisibility(View.GONE);

        final EditText enterName = (EditText) findViewById(R.id.enterProfileUserName);
        final EditText enterSurname = (EditText) findViewById(R.id.enterProfileUserSurname);
        final EditText enterEmailId = (EditText) findViewById(R.id.enterProfileUserEmailId);
        final EditText enterAltPhoneNumber = (EditText) findViewById(R.id.enterProfileAlternatePhone);
        final EditText enterPhoneNumber = (EditText) findViewById(R.id.enterProfilePhoneNumber);
        final EditText enterCity = (EditText) findViewById(R.id.enterProfileCity);
        final EditText enterState = (EditText) findViewById(R.id.enterProfileState);
        final EditText enterCountry = (EditText) findViewById(R.id.enterProfileCountry);

        final Button btnNext = (Button) findViewById(R.id.buttonSaveProfile);
        btnNext.setText(R.string.button_next);

        isEditMode = getIntent().getBooleanExtra(Configs.KEY_EDIT_PROFILE_MODE, false);

        final ProfileInfo info = UpdateManager.getInstance().getProfileInformation();
        if(isEditMode && null != info) {
            String name = info.getUserName();
            String emailId = info.getUserMailId();

            enterName.setText(!Strings.isNullOrEmpty(name) ? name : Configs.EMPTY_STRING);
            enterSurname.setText(!Strings.isNullOrEmpty(info.getSurname()) ? info.getSurname() : Configs.EMPTY_STRING);
            enterEmailId.setText(!Strings.isNullOrEmpty(emailId) ? emailId : Configs.EMPTY_STRING);
            enterAltPhoneNumber.setText(!Strings.isNullOrEmpty(info.getAlternatePhoneNumber()) ? info.getAlternatePhoneNumber() : Configs.EMPTY_STRING);
            enterPhoneNumber.setText(!Strings.isNullOrEmpty(info.getPhoneNumber()) ? info.getPhoneNumber() : Configs.EMPTY_STRING);
            enterCity.setText(!Strings.isNullOrEmpty(info.getLocationCity()) ? info.getLocationCity() : Configs.EMPTY_STRING);
            enterState.setText(!Strings.isNullOrEmpty(info.getLocationState()) ? info.getLocationState() : Configs.EMPTY_STRING);
            enterCountry.setText(!Strings.isNullOrEmpty(info.getLocationCountry()) ? info.getLocationCountry() : Configs.EMPTY_STRING);
        }

        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                KeeperUtils.hideKeyboard(appContext, enterEmailId);
                if(personalInfoContainer.getVisibility() == View.VISIBLE) {
                    userName = enterName.getText().toString().trim();
                    userSurname = enterSurname.getText().toString().trim();
                    emailId = enterEmailId.getText().toString().trim();
                    altPhoneNumber = enterAltPhoneNumber.getText().toString().trim();
                    phoneNumber = enterPhoneNumber.getText().toString().trim();

                    if(Strings.isNullOrEmpty(userName)) {
                        KeeperUtils.showErrorInEditText(enterName, getString(R.string.alert_blank_field));
                        return;
                    } else if(Strings.isNullOrEmpty(emailId)) {
                        KeeperUtils.showErrorInEditText(enterEmailId, getString(R.string.alert_blank_field));
                        return;
                    } else if(!KeeperUtils.isValidEmailId(emailId)) {
                        KeeperUtils.showErrorInEditText(enterEmailId, getString(R.string.alert_msg_incorrect_emailid));
                        return;
                    } else if(!Strings.isNullOrEmpty(altPhoneNumber) && !KeeperUtils.isValidPhoneNumber(altPhoneNumber)) {
                        KeeperUtils.showErrorInEditText(enterAltPhoneNumber, getString(R.string.alert_msg_incorrect_phonenumber));
                        return;
                    } else if(!KeeperUtils.isValidPhoneNumber(phoneNumber)) {
                        KeeperUtils.showErrorInEditText(enterPhoneNumber, getString(R.string.alert_msg_incorrect_phonenumber));
                        return;
                    }
                    personalInfoContainer.setVisibility(View.GONE);
                    locationInfoContainer.setVisibility(View.VISIBLE);
                } else {
                    city = enterCity.getText().toString().trim();
                    state = enterState.getText().toString().trim();
                    country = enterCountry.getText().toString().trim();

                    ProfileInfo updatedInfo = new ProfileInfo(userName, userSurname, phoneNumber, country, state, city, emailId, altPhoneNumber);
                    UpdateManager.getInstance().updateProfileData(updatedInfo);

                    if(isEditMode) {
                        Intent profileIntent = new Intent(EditProfileActivity.this, KeeperProfileActivity.class);
                        startActivity(profileIntent);
                    } else {
                        Intent configIntent = new Intent(EditProfileActivity.this, CreateKeeperActivity.class);
                        startActivity(configIntent);
                    }
                    KeeperUtils.displaySuccessToast(appContext, getString(R.string.text_toast_success_profile_update));
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(personalInfoContainer.getVisibility() == View.GONE) {
            personalInfoContainer.setVisibility(View.VISIBLE);
            locationInfoContainer.setVisibility(View.GONE);
            return;
        } else if(isEditMode) {
            Intent profileIntent = new Intent(this, KeeperProfileActivity.class);
            startActivity(profileIntent);
        } else {
            Intent tutorialIntent = new Intent(this, KeeperTutorialActivity.class);
            startActivity(tutorialIntent);
        }
        finish();
    }

    /* (non-Javadoc)
     * @see com.infinity.android.keeper.BaseKeeperActivity#cleanup()
     */
    @Override
    public void cleanup() {
        // TODO Auto-generated method stub

    }

}
