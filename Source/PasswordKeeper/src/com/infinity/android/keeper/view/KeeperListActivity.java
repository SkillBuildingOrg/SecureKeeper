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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
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
    private static final int MENU_VIEW_PROFILE = 101;
    private static final int MENU_EXPORT_DATA = 102;
    private static final int MENU_IMPORT_DATA = 103;
    private static final int MENU_ABOUT_APP = 105;

    private EntryAdapter entryAdapter;
    private ListView entryListView;
    private List<KeeperEntry> keeperEntryList;
    private List<ImageView> menuBarImageList;
    private String importedFilePath = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_keeper_list);

        importedFilePath = getIntent().getStringExtra(FileBrowserActivity.EXTRA_FILE_PATH);

        /*
         * Initialize page header
         */
        final View headerView = KeeperUtils.getPageTitleView(appContext, getString(R.string.page_entry_list));
        addPageHeaderView(headerView);
        final LinearLayout headerMenuContainer = (LinearLayout)headerView.findViewById(R.id.headerMenuContainer);
        final ImageView addNewImage = KeeperUtils.getHeaderMenuImage(appContext, R.drawable.menu_add);
        final ImageView menuOptionsImage = KeeperUtils.getHeaderMenuImage(appContext, R.drawable.menu_options);
        headerMenuContainer.addView(addNewImage);
        headerMenuContainer.addView(menuOptionsImage);
        addNewImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent addEntryIntent = new Intent(KeeperListActivity.this, KeeperEntryEditActivity.class);
                addEntryIntent.putExtra(Configs.KEY_UPDATE_TYPE, EntryUpdateType.ADD_NEW_ENTRY);
                startActivity(addEntryIntent);
                finish();
            }
        });

        menuOptionsImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                KeeperUtils.hideKeyboard(appContext, v);
                openContextMenu(v);
            }
        });
        registerForContextMenu(menuOptionsImage);

        final View pageMenuBar = KeeperUtils.getDefaultMenuBarView(appContext);
        initializePageMenuBar(pageMenuBar);
        addPageMenuBarLayout(pageMenuBar);

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
        initializeEntryList(keeperEntryList);
        if(!Strings.isNullOrEmpty(importedFilePath)) {
            initiateDataImport(importedFilePath);
        }
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
                initializeEntryList(UpdateManager.getInstance().getStoredEntries(null));
                setSelectedMenuBarId(R.id.menuBarAll);
            }
        });

        personalCategoryMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                initializeEntryList(UpdateManager.getInstance().getStoredEntries(EntryType.PERSONAL));
                setSelectedMenuBarId(R.id.menuBarPersonal);
            }
        });

        professionalCategoryMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                initializeEntryList(UpdateManager.getInstance().getStoredEntries(EntryType.PROFESSIONAL));
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
     * @param selectedId
     */
    private void setSelectedMenuBarId(final int selectedId) {
        if(null != menuBarImageList && !menuBarImageList.isEmpty()) {
            for(ImageView view : menuBarImageList) {
                switch (view.getId()) {
                    case R.id.menuBarAll:
                        if(view.getId() == selectedId) {
                            view.setImageResource(R.drawable.menu_bar_all_selected);
                        } else {
                            view.setImageResource(R.drawable.menu_bar_all);
                        }
                        break;
                    case R.id.menuBarPersonal:
                        if(view.getId() == selectedId) {
                            view.setImageResource(R.drawable.menu_bar_personal_selected);
                        } else {
                            view.setImageResource(R.drawable.menu_bar_personal);
                        }
                        break;
                    case R.id.menuBarProfessional:
                        if(view.getId() == selectedId) {
                            view.setImageResource(R.drawable.menu_bar_professional_selected);
                        } else {
                            view.setImageResource(R.drawable.menu_bar_professional);
                        }
                        break;
                    case R.id.menuBarSearch:
                        if(view.getId() == selectedId) {
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
    private void initializeEntryList(final List<KeeperEntry> recordList) {
        final TextView noRecordsFoundView = (TextView)findViewById(R.id.noRecordsFoundText);
        final View separater = findViewById(R.id.separaterToHide);
        if (null != recordList && !recordList.isEmpty()) {
            entryListView.setVisibility(View.VISIBLE);
            noRecordsFoundView.setVisibility(View.GONE);
            separater.setVisibility(View.VISIBLE);
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
            separater.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE, MENU_VIEW_PROFILE, Menu.NONE, getString(R.string.menu_view_profile));
        menu.add(Menu.NONE, MENU_EXPORT_DATA, Menu.NONE, getString(R.string.menu_export_data));
        menu.add(Menu.NONE, MENU_IMPORT_DATA, Menu.NONE, getString(R.string.menu_import_data));
        menu.add(Menu.NONE, MENU_ABOUT_APP, Menu.NONE, getString(R.string.menu_about_us));
    }

    /**
     * Navigate to Search screen
     */
    private void navigateToSearchScreen() {
        Intent searchIntent = new Intent(KeeperListActivity.this, SearchKeeperActivity.class);
        startActivity(searchIntent);
        finish();
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case MENU_VIEW_PROFILE:
                Intent profileIntent = new Intent(KeeperListActivity.this, KeeperProfileActivity.class);
                startActivity(profileIntent);
                finish();
                break;
            case MENU_EXPORT_DATA:
                List<KeeperEntry> storedList = UpdateManager.getInstance().getStoredEntries(null);
                if(null != storedList && !storedList.isEmpty()) {
                    promptForPassword(true);
                } else {
                    KeeperUtils.showErrorAlertMessage(appContext, getString(R.string.alert_msg_no_data_export));
                }
                break;
            case MENU_IMPORT_DATA:
                promptForPassword(false);
                break;
            case MENU_ABOUT_APP:
                Intent aboutIntent = new Intent(this, AboutAppActivity.class);
                startActivity(aboutIntent);
                finish();
                break;
        }
        return super.onContextItemSelected(item);
    }


    private void initiateDataImport(final String filePath) {
        File selectedFile = new File(filePath);
        //Read text from file
        StringBuilder importText = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(selectedFile));
            String line;
            while ((line = br.readLine()) != null) {
                importText.append(line);
            }
            br.close();
        }
        catch (IOException e) {
            Log.d("IOException", ""+Throwables.getStackTraceAsString(e));
        }
        if(!Strings.isNullOrEmpty(importText.toString()) && UpdateManager.getInstance().importData(importText.toString())) {
            setSelectedMenuBarId(R.id.menuBarAll);
            initializeEntryList(UpdateManager.getInstance().getStoredEntries(null));
            KeeperUtils.showErrorAlertMessage(appContext, getString(R.string.text_toast_success_import));
        } else {
            KeeperUtils.showErrorAlertMessage(appContext, getString(R.string.text_toast_failure_import));
        }
    }

    /**
     * Prompt for password to initiate Import/Export operations.
     * @param isExport true if operation is "Export", else false for "Import"
     */
    private void promptForPassword(final boolean isExport) {
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
                    if(isExport) {
                        TextFileHelper helper = new TextFileHelper();
                        if (helper.createTextFile(UpdateManager.getInstance().exportData())) {
                            KeeperUtils.showErrorAlertMessage(appContext, getString(R.string.alert_msg_export_success) + " "+helper.getFilePathInfo());
                        } else {
                            KeeperUtils.showErrorAlertMessage(appContext, getString(R.string.alert_msg_export_failed));
                        }
                    } else {
                        Intent intent = new Intent(appContext, FileBrowserActivity.class);
                        startActivity(intent);
                        finish();
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
