/**
 * 
 */
package com.infinity.android.keeper.view;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.infinity.android.keeper.BaseKeeperActivity;
import com.infinity.android.keeper.R;
import com.infinity.android.keeper.adaptors.EntryAdapter;
import com.infinity.android.keeper.data.model.KeeperEntry;
import com.infinity.android.keeper.data.model.utils.EntrySubType;
import com.infinity.android.keeper.data.model.utils.EntryType;
import com.infinity.android.keeper.manager.UpdateManager;
import com.infinity.android.keeper.utils.Configs;
import com.infinity.android.keeper.utils.KeeperUtils;

/**
 * @author joshiroh
 *
 */
public final class SearchKeeperActivity extends BaseKeeperActivity {

    private ListView entryListView;
    private EntryAdapter entryAdapter;
    private TextView noRecordsFoundText;
    private EditText enterSearchTextInput;
    private Spinner categorySpinner;
    private Spinner subTypeSpinner;
    private EntryType selectedCategory;
    private EntrySubType selectedSubtype;
    private String prevSearchText;
    private List<KeeperEntry> entrySearchList;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_search_entry);

        prevSearchText = Configs.EMPTY_STRING;

        entryListView = (ListView) findViewById(R.id.entryListView);
        noRecordsFoundText = (TextView) findViewById(R.id.noRecordsFoundText);
        enterSearchTextInput = (EditText) findViewById(R.id.enterSearchTextInput);
        categorySpinner = (Spinner) findViewById(R.id.spinnerEntryCategory);
        subTypeSpinner = (Spinner) findViewById(R.id.spinnerEntrySubtype);

        initializeCategorySpinner(categorySpinner, null);
        initializeSubtypeSpinner(subTypeSpinner, null);

        entrySearchList = UpdateManager.getInstance().getStoredEntries(null);
        updateSearchListView(entrySearchList, true);

        enterSearchTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
            }
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                updateSearchResults(s.toString().trim());
            }
        });

        KeeperUtils.initActionBar(appContext, R.string.hint_search_text, true);
    }

    /**
     * update search results for the selected filters & search text
     * @param searchText
     */
    private void updateSearchResults(final String searchText) {
        if(!Strings.isNullOrEmpty(searchText)) {
            if(!searchText.contains(prevSearchText) || Strings.isNullOrEmpty(prevSearchText)) {
                entrySearchList = UpdateManager.getInstance().getStoredEntries(selectedCategory);
            }
            if(null != entrySearchList && !entrySearchList.isEmpty()) {
                KeeperEntry entry;
                for(int index = entrySearchList.size() - 1; index >= 0; index--) {
                    entry = entrySearchList.get(index);
                    if((null != selectedSubtype && entry.getEntrySubType() != selectedSubtype) || !entry.isValidSearchResult(searchText.trim())) {
                        entrySearchList.remove(index);
                    }
                }
            }
            prevSearchText = searchText;
            updateSearchListView(entrySearchList, false);
        } else {
            prevSearchText = Configs.EMPTY_STRING;
            updateSearchListView(UpdateManager.getInstance().getStoredEntries(null), true);
        }
    }

    /**
     * Update search results.
     * @param recordList
     * @param searchText
     */
    private void updateSearchListView(final List<KeeperEntry> recordList, final boolean isEmptySearch) {
        if(null != recordList && !recordList.isEmpty() && !isEmptySearch) {
            entryListView.setVisibility(View.VISIBLE);
            noRecordsFoundText.setVisibility(View.GONE);
            if(null == entryAdapter) {
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
                    if(null != selectedEntry) {
                        long entryId = selectedEntry.getEntryId();
                        Intent viewEntryIntent = new Intent(SearchKeeperActivity.this, KeeperEntryDetailsActivity.class);
                        viewEntryIntent.putExtra(Configs.KEY_ENTRY_ID, entryId);
                        startActivity(viewEntryIntent);
                        finish();
                    }
                }
            });
        } else {
            entryListView.setVisibility(View.GONE);
            noRecordsFoundText.setVisibility(View.VISIBLE);
            noRecordsFoundText.setText(isEmptySearch ? R.string.text_label_initiate_search : R.string.text_label_no_search_results_found);
        }
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
                        selectedCategory = null;
                        break;
                    case 1:
                        selectedCategory = EntryType.PERSONAL;
                        break;
                    case 2:
                        selectedCategory = EntryType.PROFESSIONAL;
                        break;
                }
                entrySearchList = UpdateManager.getInstance().getStoredEntries(selectedCategory);
                prevSearchText = Configs.EMPTY_STRING;
                updateSearchResults(enterSearchTextInput.getText().toString());
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
            }
        });

        int selectedIndex = 0;
        if (null != type) {
            switch (type) {
                case PERSONAL:
                    selectedIndex = 1;
                    break;
                case PROFESSIONAL:
                    selectedIndex = 2;
                    break;

            }
        } else {
            selectedCategory = EntryType.PERSONAL;
            selectedIndex = 1;
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
                        selectedSubtype = null;
                        break;
                    case 1:
                        selectedSubtype = EntrySubType.OTHER;
                        break;
                    case 2:
                        selectedSubtype = EntrySubType.BANKING;
                        break;
                    case 3:
                        selectedSubtype = EntrySubType.EMAIL;
                        break;
                    case 4:
                        selectedSubtype = EntrySubType.SOCIAL;
                        break;
                    case 5:
                        selectedSubtype = EntrySubType.PRIVATE;
                        break;
                    case 6:
                        selectedSubtype = EntrySubType.PHONE;
                        break;
                }
                prevSearchText = Configs.EMPTY_STRING;
                updateSearchResults(enterSearchTextInput.getText().toString());
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
            }
        });

        int selectedIndex = 0;
        if (null != type) {
            switch (type) {
                case OTHER:
                    selectedIndex = 1;
                    break;
                case BANKING:
                    selectedIndex = 2;
                    break;
                case EMAIL:
                    selectedIndex = 3;
                    break;
                case SOCIAL:
                    selectedIndex = 4;
                    break;
                case PRIVATE:
                    selectedIndex = 5;
                    break;
                case PHONE:
                    selectedIndex = 6;
                    break;
                default:
                    selectedIndex = 0;
                    break;
            }
        } else {
            selectedSubtype = null;
            selectedIndex = 0;
        }
        subTypeSpinner.setSelection(selectedIndex);
    }

    @Override
    public void onBackPressed() {
        Intent listEntryIntent = new Intent(SearchKeeperActivity.this, KeeperListActivity.class);
        startActivity(listEntryIntent);
        super.onBackPressed();
    }

    /* (non-Javadoc)
     * @see com.infinity.android.keeper.BaseKeeperActivity#cleanup()
     */
    @Override
    public void cleanup() {

    }

}
