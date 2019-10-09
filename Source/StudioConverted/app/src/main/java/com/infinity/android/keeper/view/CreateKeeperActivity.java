/**
 * 
 */

package com.infinity.android.keeper.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.infinity.android.keeper.BaseKeeperActivity;
import com.infinity.android.keeper.R;
import com.infinity.android.keeper.manager.KeeperManager;
import com.infinity.android.keeper.utils.Configs;
import com.infinity.android.keeper.utils.KeeperUtils;

/**
 * @author joshiroh
 */
public final class CreateKeeperActivity extends BaseKeeperActivity {

    private String accessToken;
    private boolean isChangePassword;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_config_keeper);

        isChangePassword = getIntent().getBooleanExtra(Configs.KEY_CHANGE_PASSWORD, false);

        final LinearLayout changePasswordContainer = (LinearLayout)findViewById(R.id.changePasswordContainer);
        final LinearLayout createPasswordContainer = (LinearLayout)findViewById(R.id.createPasswordContainer);
        final LinearLayout confirmContainer = (LinearLayout)findViewById(R.id.confirmContainer);

        if (!isChangePassword) {
            changePasswordContainer.setVisibility(View.GONE);
            createPasswordContainer.setVisibility(View.VISIBLE);
        } else {
            changePasswordContainer.setVisibility(View.VISIBLE);
            createPasswordContainer.setVisibility(View.GONE);
        }

        final EditText oldAccessCode = (EditText)findViewById(R.id.enterOldPassword);
        final Button confirmOldCodeButton = (Button)findViewById(R.id.buttonConfirmOldPassword);

        oldAccessCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                // No handling required for now.
            }

            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
                // No handling required for now.
            }

            @Override
            public void afterTextChanged(final Editable s) {
                String token = s.toString().trim();
                confirmOldCodeButton.setEnabled(!Strings.isNullOrEmpty(token) && token.length() >= Configs.MIN_TOKEN_LENGTH);
            }
        });

        confirmOldCodeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                KeeperUtils.hideKeyboard(appContext, oldAccessCode);
                if (KeeperManager.getInstance().isKeeperTokenValid(oldAccessCode.getText().toString().trim())) {
                    changePasswordContainer.setVisibility(View.GONE);
                    createPasswordContainer.setVisibility(View.VISIBLE);
                } else {
                    KeeperUtils.showErrorAlertMessage(appContext, getString(R.string.alert_msg_incorrect_token));
                }
            }
        });

        final EditText enterAccessCode = (EditText)findViewById(R.id.enterPassword);
        final Button nextButton = (Button)findViewById(R.id.buttonNext);
        final Button confirmButton = (Button)findViewById(R.id.buttonConfirm);

        enterAccessCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                // No handling required for now.
            }

            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
                // No handling required for now.
            }

            @Override
            public void afterTextChanged(final Editable s) {
                accessToken = s.toString().trim();
                nextButton.setEnabled(!Strings.isNullOrEmpty(accessToken) && accessToken.length() >= Configs.MIN_TOKEN_LENGTH);
            }
        });

        final EditText confirmAccessCode = (EditText)findViewById(R.id.confirmPassword);
        final TextView errorPasswordConfirm = (TextView) findViewById(R.id.errorPasswordConfirm);
        confirmAccessCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                // No handling required for now.
            }

            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
                // No handling required for now.
            }

            @Override
            public void afterTextChanged(final Editable s) {
                String token = s.toString().trim();
                final boolean isEnabled = !Strings.isNullOrEmpty(accessToken) && token.equals(accessToken);
                confirmButton.setEnabled(isEnabled);
                if(isEnabled) {
                    errorPasswordConfirm.setVisibility(View.GONE);
                    KeeperUtils.hideKeyboard(appContext, confirmAccessCode);
                } else {
                    errorPasswordConfirm.setVisibility(View.VISIBLE);
                }
            }
        });

        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                accessToken = enterAccessCode.getText().toString();
                if(!KeeperManager.getInstance().isKeeperTokenValid(accessToken.trim())) {
                    confirmContainer.setVisibility(View.VISIBLE);
                    nextButton.setVisibility(View.GONE);
                    confirmAccessCode.requestFocus();
                } else {
                    KeeperUtils.showErrorAlertMessage(appContext, getString(R.string.alert_msg_same_password));
                }
            }
        });

        confirmButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                KeeperUtils.hideKeyboard(appContext, enterAccessCode);
                KeeperManager.getInstance().storeKeeperToken(accessToken);
                if (isChangePassword) {
                    KeeperUtils.displaySuccessToast(appContext, getString(R.string.text_toast_success_new_password));
                }
                Intent startupIntent = new Intent(CreateKeeperActivity.this, LoginKeeperActivity.class);
                startActivity(startupIntent);
                finish();
            }
        });

        KeeperUtils.initActionBar(appContext, R.string.page_entry_list, false);
    }

    @Override
    public void cleanup() {
        accessToken = null;
    }

    @Override
    public void onBackPressed() {
        if (isChangePassword) {
            Intent profileIntent = new Intent(CreateKeeperActivity.this, KeeperProfileActivity.class);
            startActivity(profileIntent);
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
