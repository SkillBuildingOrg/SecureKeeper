/**
 * 
 */
package com.infinity.android.keeper.view;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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
public final class ForgetKeeperActivity extends BaseKeeperActivity {

    private RadioGroup radioGroup;
    private RadioButton buttonResetByEmail;
    private RadioButton buttonResetByPhone;
    private Button buttonGetToken;
    private EditText enterResetToken;
    private Button bttonSubmitToken;
    private LinearLayout resetContainer;
    private LinearLayout submitContainer;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_forgot_password);
        addPageHeaderView(KeeperUtils.getPageTitleView(appContext, getString(R.string.page_forget_password)));

        resetContainer = (LinearLayout) findViewById(R.id.resetOptionsContainer);
        submitContainer = (LinearLayout) findViewById(R.id.submitContainer);

        radioGroup = (RadioGroup) findViewById(R.id.resetTokenGroup);
        buttonResetByEmail = (RadioButton) findViewById(R.id.resetByEmail);
        buttonResetByPhone = (RadioButton) findViewById(R.id.resetByPhoneNumber);
        buttonGetToken = (Button) findViewById(R.id.buttonSendToken);

        buttonGetToken.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                //                int selectedRadioId = radioGroup.getCheckedRadioButtonId();
                //                if(selectedRadioId == buttonResetByEmail.getId()) {
                //                    // Send email
                //                } else if(selectedRadioId == buttonResetByPhone.getId())
                {
                    // Send SMS
                    ProfileInfo profile = UpdateManager.getInstance().getProfileInformation();
                    String number = profile.getPhoneNumber();
                    String altNumber = profile.getAlternatePhoneNumber();
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(number, null, KeeperManager.getInstance().createResetToken(), null, null);
                    if(!Strings.isNullOrEmpty(altNumber)) {
                        smsManager.sendTextMessage(altNumber, null, KeeperManager.getInstance().createResetToken(), null, null);
                    }
                }
                KeeperUtils.displaySuccessToast(appContext, getString(R.string.text_toast_token_sent_phone));
                resetContainer.setVisibility(View.GONE);
                submitContainer.setVisibility(View.VISIBLE);
            }
        });

        enterResetToken = (EditText) findViewById(R.id.enterResettoken);
        bttonSubmitToken = (Button) findViewById(R.id.buttonSubmitToken);
        bttonSubmitToken.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                String token = enterResetToken.getText().toString().trim();
                if(token.length() == 0) {
                    KeeperUtils.showErrorInEditText(enterResetToken, getString(R.string.alert_blank_field));
                } else {
                    if(!KeeperManager.getInstance().isValidResetToken(token)) {
                        enterResetToken.setText(Configs.EMPTY_STRING);
                        KeeperUtils.showErrorAlertMessage(appContext, getString(R.string.alert_invalid_reset_token));
                    } else {
                        KeeperManager.getInstance().deleteResetToken();
                        Intent profileIntent = new Intent(ForgetKeeperActivity.this, CreateKeeperActivity.class);
                        profileIntent.putExtra(Configs.KEY_CHANGE_PASSWORD, false);
                        startActivity(profileIntent);
                        finish();
                    }
                }
            }
        });
    }

    @Override
    public void cleanup() {
    }

    @Override
    public void onBackPressed() {
        Intent viewListIntent = new Intent(ForgetKeeperActivity.this, LoginKeeperActivity.class);
        startActivity(viewListIntent);
        super.onBackPressed();
    }
}
