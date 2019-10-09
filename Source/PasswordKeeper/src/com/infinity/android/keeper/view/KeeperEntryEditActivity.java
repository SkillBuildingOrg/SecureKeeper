/**
 * 
 */

package com.infinity.android.keeper.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.common.base.Strings;
import com.infinity.android.keeper.BaseKeeperActivity;
import com.infinity.android.keeper.R;
import com.infinity.android.keeper.data.model.AdditionalInfo;
import com.infinity.android.keeper.data.model.KeeperEntry;
import com.infinity.android.keeper.data.model.utils.EntrySubType;
import com.infinity.android.keeper.data.model.utils.EntryType;
import com.infinity.android.keeper.listeners.EntryUpdateListener;
import com.infinity.android.keeper.manager.UpdateManager;
import com.infinity.android.keeper.utils.Configs;
import com.infinity.android.keeper.utils.EntryUpdateType;
import com.infinity.android.keeper.utils.KeeperUtils;
import com.infinity.android.keeper.view.fragments.KeyValueView;

/**
 * @author joshiroh
 */
public final class KeeperEntryEditActivity extends BaseKeeperActivity {
    private UpdateManager updateManager;
    private String titleValue;
    private String descriptionValue;
    private String secreteKeyValue;
    private EntryUpdateType entryUpdateType;
    private EntryType categoryType;
    private EntrySubType entrySubtype;
    private long recordedEntryId = -1;
    private List<AdditionalInfo> additionalInfoList;
    private int infoCounter = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateManager = UpdateManager.getInstance();
        entryUpdateType = (EntryUpdateType)getIntent().getSerializableExtra(Configs.KEY_UPDATE_TYPE);
        KeeperEntry keeperEntry = null;
        if (entryUpdateType == EntryUpdateType.EDIT_ENTRY) {
            recordedEntryId = getIntent().getLongExtra(Configs.KEY_ENTRY_ID, -1);
            if (recordedEntryId != -1) {
                keeperEntry = updateManager.getEntry(recordedEntryId);
            }
        }

        setContentView(R.layout.layout_edit_entry);
        final int titleTextResourceId = (entryUpdateType == EntryUpdateType.EDIT_ENTRY ? R.string.page_edit_entry : R.string.page_add_new_entry);

        final View headerView = KeeperUtils.getPageTitleView(appContext, getString(titleTextResourceId));
        addPageHeaderView(headerView);
        final LinearLayout headerMenuContainer = (LinearLayout)headerView.findViewById(R.id.headerMenuContainer);
        final ImageView saveEntryImageView = KeeperUtils.getHeaderMenuImage(appContext, R.drawable.menu_save);
        headerMenuContainer.addView(saveEntryImageView);

        final EditText enterTitleText = (EditText)findViewById(R.id.enterEntryTitle);
        final EditText enterDescriptionText = (EditText)findViewById(R.id.enterDescription);
        final EditText enterPasscodeText = (EditText)findViewById(R.id.enterEntryPasscode);
        final Button buttonSaveEntry = (Button)findViewById(R.id.buttonSaveEntry);
        final LinearLayout listKeyValueLayout = (LinearLayout)findViewById(R.id.keyValueListContainer);
        final Spinner categorySpinner = (Spinner)findViewById(R.id.spinnerEntryCategory);
        final Spinner subTypeSpinner = (Spinner)findViewById(R.id.spinnerEntrySubtype);

        saveEntryImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                KeeperUtils.hideKeyboard(appContext, enterTitleText);
                if(!Strings.isNullOrEmpty(titleValue) && titleValue.length() >= Configs.MIN_TOKEN_LENGTH) {
                    saveEditedEntry(listKeyValueLayout);
                } else {
                    KeeperUtils.showErrorInEditText(enterTitleText, getString(R.string.alert_blankormin_chars_field));
                    enterTitleText.requestFocus();
                }
            }
        });

        if (null != keeperEntry) {
            titleValue = keeperEntry.getTitle();
            descriptionValue = keeperEntry.getDescription();
            secreteKeyValue = keeperEntry.getSecreteKey();
            additionalInfoList = keeperEntry.getAdditionalInfoList();
            enterTitleText.setText(titleValue);
            enterDescriptionText.setText(descriptionValue);
            enterPasscodeText.setText(secreteKeyValue);

            buttonSaveEntry.setEnabled(!Strings.isNullOrEmpty(titleValue) && titleValue.length() >= Configs.MIN_TOKEN_LENGTH);

            categoryType = keeperEntry.getEntryType();
            entrySubtype = keeperEntry.getEntrySubType();
        }

        initializeCategorySpinner(categorySpinner, categoryType);
        initializeSubtypeSpinner(subTypeSpinner, entrySubtype);

        enterTitleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
            }

            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                String result = s.toString().trim();
                buttonSaveEntry.setEnabled(result.length() >= Configs.MIN_TOKEN_LENGTH);
                titleValue = result;
            }
        });

        enterDescriptionText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
            }

            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                descriptionValue = s.toString().trim();
            }
        });

        enterPasscodeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
            }

            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                secreteKeyValue = s.toString().trim();
            }
        });

        buttonSaveEntry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                KeeperUtils.hideKeyboard(appContext, enterTitleText);
                saveEditedEntry(listKeyValueLayout);
            }
        });

        final Button buttonAddMore = (Button)findViewById(R.id.btnTextAddMoreInfo);
        infoCounter = 0;
        if (null != additionalInfoList && !additionalInfoList.isEmpty()) {
            for (AdditionalInfo info : additionalInfoList) {
                infoCounter++;
                listKeyValueLayout.addView(new KeyValueView(this, new EntryUpdateListener() {
                    @Override
                    public boolean updateState(final boolean isTextAdded) {
                        buttonAddMore.setVisibility(isTextAdded ? View.VISIBLE : View.GONE);
                        return true;
                    }
                }, info.getKey(), info.getValue()));
            }
        }

        if(infoCounter < 15) {
            infoCounter++;
            listKeyValueLayout.addView(new KeyValueView(this, new EntryUpdateListener() {
                @Override
                public boolean updateState(final boolean isTextAdded) {
                    buttonAddMore.setVisibility(isTextAdded ? View.VISIBLE : View.GONE);
                    return true;
                }
            }, Configs.EMPTY_STRING, Configs.EMPTY_STRING));
        } else {
            buttonAddMore.setVisibility(View.GONE);
        }
        buttonAddMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                buttonAddMore.setVisibility(View.GONE);
                KeeperUtils.hideKeyboard(appContext, enterTitleText);
                if(infoCounter < 15) {
                    infoCounter++;
                    listKeyValueLayout.addView(new KeyValueView(appContext, new EntryUpdateListener() {
                        @Override
                        public boolean updateState(final boolean isTextAdded) {
                            if(infoCounter >= 15) {
                                buttonAddMore.setVisibility(View.GONE);
                            } else {
                                buttonAddMore.setVisibility(isTextAdded ? View.VISIBLE : View.GONE);
                            }
                            return true;
                        }
                    }));
                }
            }
        });
    }

    /**
     * Save edited entry
     * @param infoLayout
     * @return isSuccess
     */
    private boolean saveEditedEntry(final LinearLayout infoLayout) {
        long entryId = recordedEntryId != -1 ? recordedEntryId : updateManager.getNewEntryId();
        Log.d("EDIT/ADD ENTRY", " entryId : " + entryId);
        final int childCount = infoLayout.getChildCount();
        List<AdditionalInfo> newInfoList = null;
        if (childCount > 0) {
            newInfoList = new ArrayList<AdditionalInfo>();
            for (int i = 0; i < childCount; i++) {
                final KeyValueView view = (KeyValueView)infoLayout.getChildAt(i);
                if (!view.isValuesEmpty()) {
                    newInfoList.add(new AdditionalInfo(view.getKeyText(), view.getValueText()));
                }
            }
        }

        KeeperEntry newEntry = new KeeperEntry(entryId, titleValue, descriptionValue, secreteKeyValue, KeeperUtils.getCurrentTime(), newInfoList, categoryType
                .getTypeString(), entrySubtype.getTypeString());
        if (entryUpdateType == EntryUpdateType.EDIT_ENTRY) {
            updateManager.modifyExistingEntry(recordedEntryId, newEntry);
        } else {
            updateManager.addNewEntry(newEntry);
        }
        KeeperUtils.displaySuccessToast(appContext, getString(R.string.text_toast_success_entry_add));
        onBackPressed();
        return true;
    }

    /**
     * Initialize category spinner
     * 
     * @param categorySpinner
     * @param type
     */
    private void initializeCategorySpinner(final Spinner categorySpinner, final EntryType type) {
        categorySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                switch (position) {
                    case 0:
                        categoryType = EntryType.PERSONAL;
                        break;
                    case 1:
                        categoryType = EntryType.PROFESSIONAL;
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
            }
        });

        int selectedIndex = 0;
        if (null != type) {
            switch (type) {
                case PERSONAL:
                    selectedIndex = 0;
                    break;
                case PROFESSIONAL:
                    selectedIndex = 1;
                    break;

            }
        } else {
            categoryType = EntryType.PERSONAL;
        }
        categorySpinner.setSelection(selectedIndex);
    }

    /**
     * Initialize category spinner
     * 
     * @param categorySpinner
     * @param type
     */
    private void initializeSubtypeSpinner(final Spinner subTypeSpinner, final EntrySubType type) {
        subTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                switch (position) {
                    case 0:
                        entrySubtype = EntrySubType.OTHER;
                        break;
                    case 1:
                        entrySubtype = EntrySubType.BANKING;
                        break;
                    case 2:
                        entrySubtype = EntrySubType.EMAIL;
                        break;
                    case 3:
                        entrySubtype = EntrySubType.SOCIAL;
                        break;
                    case 4:
                        entrySubtype = EntrySubType.PRIVATE;
                        break;
                    case 5:
                        entrySubtype = EntrySubType.PHONE;
                        break;
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
            }
        });

        int selectedIndex = 0;
        if (null != type) {
            switch (type) {
                case OTHER:
                    selectedIndex = 0;
                    break;
                case BANKING:
                    selectedIndex = 1;
                    break;
                case EMAIL:
                    selectedIndex = 2;
                    break;
                case SOCIAL:
                    selectedIndex = 3;
                    break;
                case PRIVATE:
                    selectedIndex = 4;
                    break;
                case PHONE:
                    selectedIndex = 5;
                    break;
                default:
                    selectedIndex = 0;
                    break;
            }
        } else {
            entrySubtype = EntrySubType.OTHER;
        }
        subTypeSpinner.setSelection(selectedIndex);
    }

    @Override
    public void onBackPressed() {
        Intent listEntryIntent = new Intent(KeeperEntryEditActivity.this, KeeperListActivity.class);
        startActivity(listEntryIntent);
        super.onBackPressed();
    }

    @Override
    public void cleanup() {
        updateManager = null;
    }
}
