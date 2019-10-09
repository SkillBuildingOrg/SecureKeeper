/**
 * 
 */
package com.infinity.android.keeper.view;

import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.infinity.android.keeper.BaseKeeperActivity;
import com.infinity.android.keeper.R;
import com.infinity.android.keeper.data.model.AdditionalInfo;
import com.infinity.android.keeper.data.model.KeeperEntry;
import com.infinity.android.keeper.manager.KeeperManager;
import com.infinity.android.keeper.manager.UpdateManager;
import com.infinity.android.keeper.utils.Configs;
import com.infinity.android.keeper.utils.EntryUpdateType;
import com.infinity.android.keeper.utils.KeeperUtils;

/**
 * @author joshiroh
 *
 */
public final class KeeperEntryDetailsActivity extends BaseKeeperActivity {
    private final int MENU_EDIT = 100;
    private final int MENU_DELETE = 101;

    private long entryId;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_view_entry_details);

        entryId = getIntent().getLongExtra(Configs.KEY_ENTRY_ID, -1);
        initializeUI(UpdateManager.getInstance().getEntry(entryId));

        KeeperUtils.initActionBar(appContext, R.string.page_entry_details, true);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_entry_details_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
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
                    switch(item.getItemId()) {
                        case R.id.action_edit_entry:
                            /*
                             * Handle edit flow, prompt for password and then edit the entry
                             */
                            executeEditEntry(entryId);
                            break;
                        case R.id.action_delete_entry:
                            /*
                             * Handle Delete flow, prompt for password and then delete the entry
                             */
                            executeDeleteEntry(entryId);
                            break;
                    }
                } else {
                    KeeperUtils.showErrorAlertMessage(appContext, getString(R.string.alert_msg_incorrect_token));
                }
            }
        };
        KeeperUtils.promptForPassword(appContext, passwordLayout, positiveListener);

        return true;
    }

    /**
     * Initialize screen UI
     * @param selectedEntry
     */
    private void initializeUI(final KeeperEntry selectedEntry) {
        final TextView titleView = (TextView)findViewById(R.id.viewTitleText);
        final TextView categoryView = (TextView)findViewById(R.id.categoryDescriptionText);
        final TextView lastModifiedView = (TextView)findViewById(R.id.viewLastUpdatedText);
        final TextView descriptionView = (TextView)findViewById(R.id.viewDescriptionText);
        final TextView secreteKeyView = (TextView)findViewById(R.id.viewSecreteKeyText);
        final LinearLayout keyValueListContainer = (LinearLayout) findViewById(R.id.viewKeyValueListContainer);

        lastModifiedView.setText(selectedEntry.getModifiedTime());
        titleView.setText(selectedEntry.getTitle());

        final String categoryDesc = KeeperUtils.getCategoryInfo(selectedEntry.getEntryType(), selectedEntry.getEntrySubType());
        if(!Strings.isNullOrEmpty(categoryDesc)) {
            categoryView.setVisibility(View.VISIBLE);
            categoryView.setText("(" + categoryDesc + ")");
        } else {
            categoryView.setVisibility(View.GONE);
        }

        if(!Strings.isNullOrEmpty(selectedEntry.getDescription())) {
            descriptionView.setText(selectedEntry.getDescription());
        }
        if(!Strings.isNullOrEmpty(selectedEntry.getSecreteKey())) {
            secreteKeyView.setText(selectedEntry.getSecreteKey());
        }

        final LinearLayout secreteContainer = (LinearLayout)findViewById(R.id.secreteContainer);
        final Button buttonViewSecrete = (Button)findViewById(R.id.buttonViewSecreteKey);
        buttonViewSecrete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                KeeperUtils.hideKeyboard(appContext, v);
                buttonViewSecrete.setVisibility(View.GONE);
                secreteContainer.setVisibility(View.VISIBLE);
            }
        });

        addExtraInfo(keyValueListContainer, selectedEntry.getAdditionalInfoList());
    }

    /**
     * Include view for additional information in the entry
     * @param parent
     * @param infoList
     */
    private void addExtraInfo(final LinearLayout parent, final List<AdditionalInfo> infoList) {
        final TextView additionInfoTitle = (TextView) findViewById(R.id.additionalInfoTextView);
        if(null != infoList && !infoList.isEmpty()) {
            parent.setVisibility(View.VISIBLE);
            additionInfoTitle.setVisibility(View.VISIBLE);
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for(AdditionalInfo info : infoList) {
                LinearLayout containerView = (LinearLayout)inflater.inflate(R.layout.view_additional_info, parent, false);
                TextView keyView = (TextView) containerView.findViewById(R.id.viewEntryKey);
                TextView valueView = (TextView) containerView.findViewById(R.id.viewEntryValue);
                String key = info.getKey();
                String value = info.getValue();
                keyView.setText(!Strings.isNullOrEmpty(key) ? key : getString(R.string.text_label_no_data_set));
                valueView.setText(!Strings.isNullOrEmpty(value) ? value : getString(R.string.text_label_no_data_set));
                parent.addView(containerView);
            }
        } else {
            additionInfoTitle.setVisibility(View.GONE);
            parent.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE, MENU_EDIT, Menu.NONE, getString(R.string.menu_edit));
        menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, getString(R.string.menu_delete));
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        if(item.getItemId() == MENU_EDIT || item.getItemId() == MENU_DELETE) {
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
                        switch (item.getItemId()) {
                            case MENU_EDIT:
                                /*
                                 * Handle edit flow, prompt for password and then edit the entry
                                 */
                                executeEditEntry(entryId);
                                break;
                            case MENU_DELETE:
                                /*
                                 * Handle Delete flow, prompt for password and then delete the entry
                                 */
                                executeDeleteEntry(entryId);
                                break;
                        }
                    } else {
                        KeeperUtils.showErrorAlertMessage(appContext, getString(R.string.alert_msg_incorrect_token));
                    }
                }
            };
            KeeperUtils.promptForPassword(appContext, passwordLayout, positiveListener);
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Delete selected entry
     * @param recordId
     */
    private void executeDeleteEntry(final long recordId) {
        UpdateManager.getInstance().removeEntry(recordId);
        KeeperUtils.displaySuccessToast(appContext, getString(R.string.text_toast_success_entry_delete));
        onBackPressed();
    }

    /**
     * Edit entry
     * @param recordId
     */
    private void executeEditEntry(final long recordId) {
        Intent viewEntryIntent = new Intent(KeeperEntryDetailsActivity.this, KeeperEntryEditActivity.class);
        viewEntryIntent.putExtra(Configs.KEY_ENTRY_ID, recordId);
        viewEntryIntent.putExtra(Configs.KEY_UPDATE_TYPE, EntryUpdateType.EDIT_ENTRY);
        startActivity(viewEntryIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent listEntryIntent = new Intent(KeeperEntryDetailsActivity.this, KeeperListActivity.class);
        startActivity(listEntryIntent);
        super.onBackPressed();
    }

    @Override
    public void cleanup() {
        // No handling required for now

    }
}
