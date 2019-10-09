/**
 * 
 */

package com.infinity.android.keeper.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.infinity.android.keeper.BaseKeeperActivity;
import com.infinity.android.keeper.R;
import com.infinity.android.keeper.adaptors.EntryAdapter;
import com.infinity.android.keeper.data.model.KeeperEntry;
import com.infinity.android.keeper.data.model.ProfileInfo;
import com.infinity.android.keeper.data.model.utils.EntryType;
import com.infinity.android.keeper.manager.KeeperManager;
import com.infinity.android.keeper.manager.UpdateManager;
import com.infinity.android.keeper.utils.Configs;
import com.infinity.android.keeper.utils.EntryUpdateType;
import com.infinity.android.keeper.utils.KeeperUtils;
import com.infinity.android.keeper.utils.TextFileHelper;

/**
 * @author joshiroh
 */
public final class KeeperListActivity extends BaseKeeperActivity {
    private static final int MENU_EXPORT_DATA = 102;
    private static final int MENU_IMPORT_DATA = 103;
    private static final int MENU_UPDATE_RECOVERY = 104;

    private EntryAdapter entryAdapter;
    private ListView entryListView;
    private List<KeeperEntry> keeperEntryList;
    private List<ImageView> menuBarImageList;
    private String importedFilePath = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_keeper_list);
        KeeperManager.getInstance().deleteResetToken();
        importedFilePath = getIntent().getStringExtra(FileBrowserActivity.EXTRA_FILE_PATH);

        final View pageMenuBar = KeeperUtils.getDefaultMenuBarView(appContext);
        initializePageMenuBar(pageMenuBar);
        addPageMenuBarLayout(pageMenuBar);

        final ImageView addNewImage = (ImageView)findViewById(R.id.addNewEntryButton);
        addNewImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent addEntryIntent = new Intent(KeeperListActivity.this, KeeperEntryEditActivity.class);
                addEntryIntent.putExtra(Configs.KEY_UPDATE_TYPE, EntryUpdateType.ADD_NEW_ENTRY);
                startActivity(addEntryIntent);
                finish();
            }
        });

        entryListView = (ListView)findViewById(R.id.entryListView);

        keeperEntryList = UpdateManager.getInstance().getStoredEntries(null);
        entryAdapter = new EntryAdapter(this, keeperEntryList);
        entryListView.setAdapter(entryAdapter);
        entryListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                KeeperEntry selectedEntry = entryAdapter.getItem(position);
                if (null != selectedEntry) {
                    long entryId = selectedEntry.getEntryId();
                    Intent viewEntryIntent = new Intent(KeeperListActivity.this, KeeperEntryDetailsActivity.class);
                    viewEntryIntent.putExtra(Configs.KEY_ENTRY_ID, entryId);
                    startActivity(viewEntryIntent);
                    finish();
                }
            }
        });
        initializeEntryList(null);
        if (!Strings.isNullOrEmpty(importedFilePath)) {
            initiateDataImport(importedFilePath);
        }

        ProfileInfo info = UpdateManager.getInstance().getProfileInformation();
        if (null != info && null == info.getSecurityQuestions()) {
            KeeperUtils.showAlertForRecoveryOptions(appContext);
        }

        KeeperUtils.initActionBar(appContext, R.string.page_entry_list, true);
    }

    /**
     * Init page menu bar actions
     * 
     * @param pageMenuBar
     */
    private void initializePageMenuBar(final View pageMenuBar) {
        final ImageView allCategoryMenu = (ImageView)pageMenuBar.findViewById(R.id.menuBarAll);
        final ImageView personalCategoryMenu = (ImageView)pageMenuBar.findViewById(R.id.menuBarPersonal);
        final ImageView professionalCategoryMenu = (ImageView)pageMenuBar.findViewById(R.id.menuBarProfessional);
        final ImageView searchCategoryMenu = (ImageView)pageMenuBar.findViewById(R.id.menuBarSearch);

        allCategoryMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                initializeEntryList(null);
                setSelectedMenuBarId(R.id.menuBarAll);
            }
        });

        personalCategoryMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                initializeEntryList(EntryType.PERSONAL);
                setSelectedMenuBarId(R.id.menuBarPersonal);
            }
        });

        professionalCategoryMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                initializeEntryList(EntryType.PROFESSIONAL);
                setSelectedMenuBarId(R.id.menuBarProfessional);
            }
        });

        searchCategoryMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                setSelectedMenuBarId(R.id.menuBarSearch);
                navigateToSearchScreen();
            }
        });

        menuBarImageList = new ArrayList<ImageView>();
        menuBarImageList.add(allCategoryMenu);
        menuBarImageList.add(personalCategoryMenu);
        menuBarImageList.add(professionalCategoryMenu);
        menuBarImageList.add(searchCategoryMenu);
        setSelectedMenuBarId(R.id.menuBarAll);
    }

    /**
     * Set selected menu bar ID indicator
     * 
     * @param selectedId
     */
    private void setSelectedMenuBarId(final int selectedId) {
        if (null != menuBarImageList && !menuBarImageList.isEmpty()) {
            for (ImageView view : menuBarImageList) {
                switch (view.getId()) {
                    case R.id.menuBarAll:
                        if (view.getId() == selectedId) {
                            view.setImageResource(R.drawable.menu_bar_all_selected);
                        } else {
                            view.setImageResource(R.drawable.menu_bar_all);
                        }
                        break;
                    case R.id.menuBarPersonal:
                        if (view.getId() == selectedId) {
                            view.setImageResource(R.drawable.menu_bar_personal_selected);
                        } else {
                            view.setImageResource(R.drawable.menu_bar_personal);
                        }
                        break;
                    case R.id.menuBarProfessional:
                        if (view.getId() == selectedId) {
                            view.setImageResource(R.drawable.menu_bar_professional_selected);
                        } else {
                            view.setImageResource(R.drawable.menu_bar_professional);
                        }
                        break;
                    case R.id.menuBarSearch:
                        if (view.getId() == selectedId) {
                            view.setImageResource(R.drawable.menu_bar_search_selected);
                        } else {
                            view.setImageResource(R.drawable.menu_bar_search);
                        }
                        break;
                }

            }
        }
    }

    /**
     * Update list of records to be displayed.
     * 
     * @param recordList
     */
    private void initializeEntryList(final EntryType type) {
        final TextView noRecordsFoundView = (TextView)findViewById(R.id.noRecordsFoundText);
        final List<KeeperEntry> recordList = UpdateManager.getInstance().getStoredEntries(type);
        if (null != recordList && !recordList.isEmpty()) {
            entryListView.setVisibility(View.VISIBLE);
            noRecordsFoundView.setVisibility(View.GONE);
            if (null == entryAdapter) {
                entryAdapter = new EntryAdapter(this, recordList);
                entryListView.setAdapter(entryAdapter);
            } else {
                entryAdapter.setEntryList(recordList);
                entryAdapter.notifyDataSetChanged();
            }
            entryListView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                    KeeperEntry selectedEntry = entryAdapter.getItem(position);
                    if (null != selectedEntry) {
                        long entryId = selectedEntry.getEntryId();
                        Intent viewEntryIntent = new Intent(KeeperListActivity.this, KeeperEntryDetailsActivity.class);
                        viewEntryIntent.putExtra(Configs.KEY_ENTRY_ID, entryId);
                        startActivity(viewEntryIntent);
                        finish();
                    }
                }
            });
        } else {
            entryListView.setVisibility(View.GONE);
            noRecordsFoundView.setVisibility(View.VISIBLE);
            if (null != type) {
                switch (type) {
                    case PERSONAL:
                        noRecordsFoundView.setText(R.string.text_label_no_record_found_personal);
                        break;
                    case PROFESSIONAL:
                        noRecordsFoundView.setText(R.string.text_label_no_record_found_prof);
                        break;
                    default:
                        noRecordsFoundView.setText(R.string.text_label_no_record_found);
                        break;
                }
            } else {
                noRecordsFoundView.setText(R.string.text_label_no_record_found);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_keeper_list_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_view_profile:
                Intent profileIntent = new Intent(KeeperListActivity.this, KeeperProfileActivity.class);
                startActivity(profileIntent);
                finish();
                break;
            case R.id.action_export_data:
                List<KeeperEntry> storedList = UpdateManager.getInstance().getStoredEntries(null);
                if (null != storedList && !storedList.isEmpty()) {
                    promptForPassword(MENU_EXPORT_DATA);
                } else {
                    KeeperUtils.showErrorAlertMessage(appContext, getString(R.string.alert_msg_no_data_export));
                }
                break;
            case R.id.action_import_data:
                promptForPassword(MENU_IMPORT_DATA);
                break;
            case R.id.action_security_backup:
                Log.d("RecoveryOptions", "Update recovery options");
                promptForPassword(MENU_UPDATE_RECOVERY);
                break;
            case R.id.action_about_us:
                Intent aboutIntent = new Intent(this, AboutAppActivity.class);
                startActivity(aboutIntent);
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * Navigate to Search screen
     */
    private void navigateToSearchScreen() {
        Intent searchIntent = new Intent(KeeperListActivity.this, SearchKeeperActivity.class);
        startActivity(searchIntent);
        finish();
    }

    /**
     * Initialize data import
     * @param filePath
     */
    private void initiateDataImport(final String filePath) {
        File selectedFile = new File(filePath);
        // Read text from file
        StringBuilder importText = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(selectedFile));
            String line;
            while ((line = br.readLine()) != null) {
                importText.append(line);
            }
            br.close();
        } catch (IOException e) {
            Log.d("IOException", "" + Throwables.getStackTraceAsString(e));
        }
        if (!Strings.isNullOrEmpty(importText.toString()) && UpdateManager.getInstance().importData(importText.toString())) {
            setSelectedMenuBarId(R.id.menuBarAll);
            initializeEntryList(null);
            KeeperUtils.showErrorAlertMessage(appContext, getString(R.string.text_toast_success_import));
        } else {
            KeeperUtils.showErrorAlertMessage(appContext, getString(R.string.text_toast_failure_import));
        }
    }

    /**
     * Prompt for password to initiate Import/Export operations.
     * 
     * @param isExport true if operation is "Export", else false for "Import"
     */
    private void promptForPassword(final int menuId) {
        final LayoutInflater inflater = this.getLayoutInflater();
        final LinearLayout passwordLayout = (LinearLayout)inflater.inflate(R.layout.layout_password_prompt, null);
        final EditText enterPassword = (EditText)passwordLayout.findViewById(R.id.enter_password_prompt);
        final DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                KeeperUtils.hideKeyboard(appContext, enterPassword);
                dialog.dismiss();
                String token = enterPassword.getText().toString().trim();
                if (KeeperManager.getInstance().isKeeperTokenValid(token)) {
                    switch (menuId) {
                        case MENU_EXPORT_DATA:
                            TextFileHelper helper = new TextFileHelper();
                            if (helper.createTextFile(UpdateManager.getInstance().exportData())) {
                                KeeperUtils.showErrorAlertMessage(appContext, getString(R.string.alert_msg_export_success) + " " + helper.getFilePathInfo());
                            } else {
                                KeeperUtils.showErrorAlertMessage(appContext, getString(R.string.alert_msg_export_failed));
                            }
                            break;
                        case MENU_IMPORT_DATA:
                            Intent intent = new Intent(appContext, FileBrowserActivity.class);
                            startActivity(intent);
                            finish();
                            break;
                        case MENU_UPDATE_RECOVERY:
                            Intent recoveryIntent = new Intent(KeeperListActivity.this, PasswordRecoveryActivity.class);
                            recoveryIntent.putExtra(Configs.KEY_ENTRY_ID, true);
                            startActivity(recoveryIntent);
                            finish();
                            break;
                    }
                } else {
                    KeeperUtils.showErrorAlertMessage(appContext, getString(R.string.alert_msg_incorrect_token));
                }
            }
        };
        KeeperUtils.promptForPassword(this, passwordLayout, positiveListener);
    }

    @Override
    public void cleanup() {
        if (null != entryAdapter) {
            entryAdapter.cleanup();
        }
        entryAdapter = null;
    }

    @Override
    public void onBackPressed() {
        KeeperUtils.updateLoginStatus(false);
        super.onBackPressed();
    }

}
