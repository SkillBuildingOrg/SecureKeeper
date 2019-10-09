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
import android.widget.TextView;

import com.google.common.base.Strings;
import com.infinity.android.keeper.BaseKeeperActivity;
import com.infinity.android.keeper.R;
import com.infinity.android.keeper.manager.KeeperManager;
import com.infinity.android.keeper.utils.Configs;
import com.infinity.android.keeper.utils.KeeperUtils;

/**
 * @author joshiroh
 *
 */
public final class LoginKeeperActivity extends BaseKeeperActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login_page);

        final EditText enterAccessToken = (EditText) findViewById(R.id.enterPassword);
        final Button loginButton = (Button) findViewById(R.id.buttonLogin);
        final TextView forgotPasswordLink = (TextView) findViewById(R.id.forgotPasswordText);

        forgotPasswordLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent viewListIntent = new Intent(LoginKeeperActivity.this, ForgetKeeperActivity.class);
                startActivity(viewListIntent);
                finish();
            }
        });

        enterAccessToken.addTextChangedListener(new TextWatcher() {
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
                loginButton.setEnabled(!Strings.isNullOrEmpty(token) && token.length() >= Configs.MIN_TOKEN_LENGTH);
            }
        });

        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                KeeperUtils.hideKeyboard(appContext, enterAccessToken);
                final String accessToken = enterAccessToken.getText().toString();
                if(KeeperManager.getInstance().isKeeperTokenValid(accessToken)) {
                    KeeperUtils.updateLoginStatus(true);
                    Intent viewListIntent = new Intent(LoginKeeperActivity.this, KeeperListActivity.class);
                    startActivity(viewListIntent);
                    finish();
                } else {
                    KeeperUtils.showErrorAlertMessage(appContext, getString(R.string.alert_msg_incorrect_token));
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        KeeperUtils.updateLoginStatus(false);
    }

    @Override
    public void cleanup() {
        // No handling required here.
    }
}
