/**
 * 
 */

package com.infinity.android.keeper.view;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Throwables;
import com.infinity.android.keeper.BaseKeeperActivity;
import com.infinity.android.keeper.R;
import com.infinity.android.keeper.data.model.AdditionalInfo;
import com.infinity.android.keeper.data.model.ProfileInfo;
import com.infinity.android.keeper.manager.UpdateManager;
import com.infinity.android.keeper.utils.Configs;
import com.infinity.android.keeper.utils.KeeperUtils;

/**
 * @author joshiroh
 */
public final class PasswordRecoveryActivity extends BaseKeeperActivity {

    private Button submitButton;
    private LinearLayout question1Layout;
    private LinearLayout question2Layout;
    private LinearLayout question3Layout;
    private LinearLayout question4Layout;
    private EditText enterAnswer1;
    private EditText enterAnswer2;
    private EditText enterAnswer3;
    private EditText enterAnswer4;
    private TextView descriptionView;
    private UpdateManager updateManager;
    private boolean isEditMode;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_recovery_question);

        isEditMode = getIntent().getBooleanExtra(Configs.KEY_ENTRY_ID, false);

        updateManager = UpdateManager.getInstance();

        submitButton = (Button)findViewById(R.id.buttonSubmitAnswers);

        question1Layout = (LinearLayout)findViewById(R.id.question1Layout);
        question2Layout = (LinearLayout)findViewById(R.id.question2Layout);
        question3Layout = (LinearLayout)findViewById(R.id.question3Layout);
        question4Layout = (LinearLayout)findViewById(R.id.question4Layout);

        enterAnswer1 = (EditText)findViewById(R.id.enterAnswer1);
        enterAnswer2 = (EditText)findViewById(R.id.enterAnswer2);
        enterAnswer3 = (EditText)findViewById(R.id.enterAnswer3);
        enterAnswer4 = (EditText)findViewById(R.id.enterAnswer4);

        descriptionView = (TextView) findViewById(R.id.descriptionTextView);

        initUI();

        KeeperUtils.initActionBar(appContext, R.string.page_recovery_questions, true);
    }

    /**
     * Initialize UI
     */
    private void initUI() {
        if (isEditMode) {
            question1Layout.setVisibility(View.VISIBLE);
            question2Layout.setVisibility(View.VISIBLE);
            question3Layout.setVisibility(View.VISIBLE);
            submitButton.setText(getString(R.string.button_submit_answers));
            descriptionView.setText(R.string.text_recovery_description_editmode);
            ProfileInfo profileInfo = updateManager.getProfileInformation();
            List<AdditionalInfo> list = null != profileInfo ? profileInfo.getSecurityQuestions() : null;
            if (null != list && !list.isEmpty()) {
                try {
                    enterAnswer1.setText(list.get(0).getValue());
                    enterAnswer2.setText(list.get(1).getValue());
                    enterAnswer3.setText(list.get(2).getValue());
                } catch (IndexOutOfBoundsException  e) {
                    Log.d("initUI", "" + Throwables.getStackTraceAsString(e));
                }
            }
        } else {
            descriptionView.setText(R.string.text_recovery_description_submitmode);
            question1Layout.setVisibility(View.VISIBLE);
            question2Layout.setVisibility(View.GONE);
            question3Layout.setVisibility(View.GONE);
            question4Layout.setVisibility(View.GONE);
            submitButton.setText(getString(R.string.button_next));
        }

        submitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String answer1 = enterAnswer1.getText().toString().trim();
                final String answer2 = enterAnswer2.getText().toString().trim();
                final String answer3 = enterAnswer3.getText().toString().trim();
                final String answer4 = enterAnswer4.getText().toString().trim();

                if (isFieldEmpty(enterAnswer1)) {
                    return;
                }

                if (isEditMode) {
                    if (isFieldEmpty(enterAnswer2)) {
                        return;
                    } else if (isFieldEmpty(enterAnswer3)) {
                        return;
                    } else if(answer3.length() != 4) {
                        KeeperUtils.showErrorInEditText(enterAnswer3, getString(R.string.alert_msg_incorrect_ssn));
                        return;
                    }

                    ProfileInfo profileInfo = updateManager.getProfileInformation();
                    profileInfo.resetQuestions();
                    profileInfo.addSecurityQuestionInfo(new AdditionalInfo(getString(R.string.text_recovery_question1), answer1));
                    profileInfo.addSecurityQuestionInfo(new AdditionalInfo(getString(R.string.text_recovery_question2), answer2));
                    profileInfo.addSecurityQuestionInfo(new AdditionalInfo(getString(R.string.text_recovery_question3), answer3));
                    updateManager.updateProfileData(profileInfo);
                    KeeperUtils.displaySuccessToast(appContext, getString(R.string.text_toast_recovery_options_set));
                    onBackPressed();
                } else { // Submit answers mode
                    if (question2Layout.getVisibility() == View.GONE) {
                        question2Layout.setVisibility(View.VISIBLE);
                        submitButton.setText(getString(R.string.button_next));
                        enterAnswer2.requestFocus();
                    } else if (question3Layout.getVisibility() == View.GONE) {
                        if (isFieldEmpty(enterAnswer2)) {
                            return;
                        } else {
                            question3Layout.setVisibility(View.VISIBLE);
                            submitButton.setText(getString(R.string.button_next));
                            enterAnswer3.requestFocus();
                        }
                    } else if (question4Layout.getVisibility() == View.GONE) {
                        if (isFieldEmpty(enterAnswer2) || isFieldEmpty(enterAnswer3)) {
                            return;
                        } else {
                            if (answer3.length() != 4) {
                                KeeperUtils.showErrorInEditText(enterAnswer3, getString(R.string.alert_msg_incorrect_ssn));
                            } else {
                                question4Layout.setVisibility(View.VISIBLE);
                                submitButton.setText(getString(R.string.button_submit_answers));
                                enterAnswer4.requestFocus();
                            }
                        }
                    } else if (!isFieldEmpty(enterAnswer1) && !isFieldEmpty(enterAnswer2) && !isFieldEmpty(enterAnswer3) && !isFieldEmpty(enterAnswer4)) {
                        if (KeeperUtils.isValidEmailId(answer4)) {
                            if (isValidAnswers()) {
                                Intent viewListIntent = new Intent(appContext, ForgetKeeperActivity.class);
                                startActivity(viewListIntent);
                                finish();
                            } else {
                                KeeperUtils.showErrorAlertMessage(appContext, getString(R.string.alert_text_recovery_failed));
                            }
                        } else {
                            KeeperUtils.showErrorInEditText(enterAnswer4, getString(R.string.alert_msg_incorrect_emailid));
                        }
                    }
                }
            }
        });
    }

    /**
     * Is given edit field empty
     * 
     * @param editField
     * @return isEmpty
     */
    private boolean isFieldEmpty(final EditText editField) {
        if (editField.getText().toString().trim().length() == 0) {
            KeeperUtils.showErrorInEditText(editField, getString(R.string.alert_blank_field));
            editField.setText(Configs.EMPTY_STRING);
            return true;
        }
        return false;
    }

    private boolean isValidAnswers() {
        boolean isValid = true;

        final String answer1 = enterAnswer1.getText().toString().trim();
        final String answer2 = enterAnswer2.getText().toString().trim();
        final String answer3 = enterAnswer3.getText().toString().trim();
        final String answer4 = enterAnswer4.getText().toString().trim();

        ProfileInfo profileInfo = updateManager.getProfileInformation();
        List<AdditionalInfo> answersList = profileInfo.getSecurityQuestions();
        if (null == answersList || answersList.isEmpty()) {
            isValid = false;
        } else if (!answer1.equalsIgnoreCase(answersList.get(0).getValue())) {
            isValid = false;
        } else if (!answer2.equalsIgnoreCase(answersList.get(1).getValue())) {
            isValid = false;
        } else if (!answer4.equalsIgnoreCase(profileInfo.getUserMailId())) {
            isValid = false;
        } else if (!answer3.equalsIgnoreCase(answersList.get(2).getValue())) {
            isValid = false;
        }
        return isValid;
    }

    @Override
    public void onBackPressed() {
        if (KeeperUtils.isUserLoggedin()) {
            Intent listIntent = new Intent(this, KeeperListActivity.class);
            startActivity(listIntent);
        } else {
            Intent listIntent = new Intent(this, LoginKeeperActivity.class);
            startActivity(listIntent);
        }
        super.onBackPressed();
    }

    /*
     * (non-Javadoc)
     * @see com.infinity.android.keeper.BaseKeeperActivity#cleanup()
     */
    @Override
    public void cleanup() {
    }
}
