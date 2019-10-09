/**
 * 
 */

package com.infinity.android.keeper.manager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.base.Strings;
import com.google.gson.reflect.TypeToken;
import com.infinity.android.keeper.data.SecureKeeperPrefs;
import com.infinity.android.keeper.data.model.ExportData;
import com.infinity.android.keeper.data.model.KeeperEntry;
import com.infinity.android.keeper.data.model.ProfileInfo;
import com.infinity.android.keeper.data.model.utils.EntryType;
import com.infinity.android.keeper.utils.Configs;
import com.infinity.android.keeper.utils.KeeperUtils;

/**
 * @author joshiroh Data Update manager.<br>
 *         Takes care of data updates for the performed tasks like, Edit/Delete/Create entry.
 */
public final class UpdateManager extends BaseManager {
    private static final String KEEPER_ENTRY_LIST_DATA = "KEEPER+DATA+";
    private static final String KEEPER_LAST_ID_USED = "KEEPER+ID+USED";
    private static final String KEEPER_PROFILE_DATA = "KEEPER+CONFIG+PROFILE+DATA";

    private static final Type TYPE_LIST_ENTRY = new TypeToken<List<KeeperEntry>>() {}.getType();
    private static final Type TYPE_KEEPER_ENTRY = new TypeToken<KeeperEntry>() {}.getType();
    private static final Type TYPE_KEEPER_PROFILE = new TypeToken<ProfileInfo>() {}.getType();
    private static final Type TYPE_IMPORT_DATA = new TypeToken<ExportData>() {}.getType();

    private static SecureKeeperPrefs keeperPrefs;
    private static UpdateManager updateManager;

    /**
     * Singleton implementation
     */
    private UpdateManager() {
        keeperPrefs = getKeeperPrefs();
    }

    /**
     * Get singleton instance of UpdateManager
     * @return updateManager
     */
    public static UpdateManager getInstance() {
        if(null == updateManager) {
            updateManager = new UpdateManager();
        }
        return updateManager;
    }

    /**
     * Get share prefs instance
     * 
     * @return sharedPrefs
     */
    private SecureKeeperPrefs getKeeperPrefs() {
        if (null == keeperPrefs) {
            keeperPrefs = new SecureKeeperPrefs(KeeperUtils.getAppContext(), BaseManager.KEEPER_PREFS);
        }
        return keeperPrefs;
    }

    /**
     * Get the stored profile information of user.
     * @return profileInfo
     */
    public synchronized final ProfileInfo getProfileInformation() {
        String storedData = getKeeperPrefs().getString(KEEPER_PROFILE_DATA, null);
        if(!Strings.isNullOrEmpty(storedData)) {
            return getGson().fromJson(storedData, TYPE_KEEPER_PROFILE);
        }
        return null;
    }

    /**
     * Update user profile data
     * @param updatedInfo
     */
    public synchronized final void updateProfileData(final ProfileInfo updatedInfo) {
        if(null != updatedInfo) {
            getKeeperPrefs().putString(KEEPER_PROFILE_DATA, getGson().toJson(updatedInfo));
        } else {
            getKeeperPrefs().putString(KEEPER_PROFILE_DATA, null);
        }
    }

    /**
     * Generate new entry ID
     * @return ID
     */
    public synchronized final long getNewEntryId() {
        int oldEntryId = getKeeperPrefs().getInt(KEEPER_LAST_ID_USED, 1983 + (new Random()).nextInt());
        oldEntryId = oldEntryId + 3;
        getKeeperPrefs().putInt(KEEPER_LAST_ID_USED, oldEntryId);
        return oldEntryId;
    }

    /**
     * Get list of stored entries of given entry type.
     * @param entryType
     * @return entryList
     */
    public synchronized final List<KeeperEntry> getStoredEntries(final EntryType type) {
        String storedData = getKeeperPrefs().getString(KEEPER_ENTRY_LIST_DATA, null);
        List<KeeperEntry> entryList = null;
        if(!Strings.isNullOrEmpty(storedData)) {
            entryList = getGson().fromJson(storedData, TYPE_LIST_ENTRY);
        }
        if(null != type && null != entryList && !entryList.isEmpty()) {
            KeeperEntry entry;
            for(int index = entryList.size() - 1; index >= 0; index--) {
                entry = entryList.get(index);
                if(entry.getEntryType() != type) {
                    entryList.remove(index);
                }
            }

        }
        return entryList;
    }

    /**
     * Get keeper entry
     * @param entryId
     * @return keeperEntry
     */
    public synchronized final KeeperEntry getEntry(final long entryId) {
        String storedData = getKeeperPrefs().getString(KEEPER_ENTRY_LIST_DATA, null);
        List<KeeperEntry> entryList = null;
        if(!Strings.isNullOrEmpty(storedData)) {
            entryList = getGson().fromJson(storedData, TYPE_LIST_ENTRY);
            if(null != entryList && !entryList.isEmpty()) {
                for(KeeperEntry entry : entryList) {
                    if(entryId == entry.getEntryId()) {
                        String json = getGson().toJson(entry);
                        return getGson().fromJson(json, TYPE_KEEPER_ENTRY);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Store given entry in the preferences
     * 
     * @param entry
     */
    public synchronized final void addNewEntry(final KeeperEntry entry) {
        String storedData = getKeeperPrefs().getString(KEEPER_ENTRY_LIST_DATA, null);
        List<KeeperEntry> entryList;
        if(Strings.isNullOrEmpty(storedData)) {
            entryList = new ArrayList<KeeperEntry>();
        } else {
            entryList = getGson().fromJson(storedData, TYPE_LIST_ENTRY);
        }
        entryList.add(entry);
        getKeeperPrefs().putString(KEEPER_ENTRY_LIST_DATA, getGson().toJson(entryList));
    }

    /**
     * Delete the entry with given ID, from storage.
     * @param entryId
     * @return isSuccess
     */
    public synchronized final boolean removeEntry(final long entryId) {
        boolean isSuccess = false;
        String storedData = getKeeperPrefs().getString(KEEPER_ENTRY_LIST_DATA, null);
        List<KeeperEntry> entryList = null;
        if(!Strings.isNullOrEmpty(storedData)) {
            entryList = getGson().fromJson(storedData, TYPE_LIST_ENTRY);
            if(null != entryList && !entryList.isEmpty()) {
                for(KeeperEntry entry : entryList) {
                    if(entryId == entry.getEntryId()) {
                        entryList.remove(entry);
                        isSuccess = true;
                        break;
                    }
                }
                getKeeperPrefs().putString(KEEPER_ENTRY_LIST_DATA, getGson().toJson(entryList));
            }
        }

        return isSuccess;
    }

    /**
     * Delete the entry with given ID, from storage.
     * @param entryId
     * @param modifiedEntry
     * @return isSuccess
     */
    public synchronized final boolean modifyExistingEntry(final long entryId, final KeeperEntry modifiedEntry) {
        boolean isSuccess = false;
        String storedData = getKeeperPrefs().getString(KEEPER_ENTRY_LIST_DATA, null);
        List<KeeperEntry> entryList = null;
        if(!Strings.isNullOrEmpty(storedData)) {
            entryList = getGson().fromJson(storedData, TYPE_LIST_ENTRY);
            if(null != entryList && !entryList.isEmpty() && null != modifiedEntry) {
                for(KeeperEntry entry : entryList) {
                    if(entryId == entry.getEntryId()) {
                        final int index = entryList.indexOf(entry);
                        entryList.remove(entry);
                        entryList.add(index, modifiedEntry);
                        isSuccess = true;
                        break;
                    }
                }
                getKeeperPrefs().putString(KEEPER_ENTRY_LIST_DATA, getGson().toJson(entryList));
            }
        }

        return isSuccess;
    }

    /**
     * Export data for backup
     * @return exportData
     */
    public synchronized final String exportData() {
        final String storedData = getKeeperPrefs().getString(KEEPER_ENTRY_LIST_DATA, Configs.EMPTY_STRING);
        ExportData exportData = new ExportData(getNewEntryId(), KeeperUtils.getCurrentTime(), storedData);
        return getKeeperPrefs().toKey(getGson().toJson(exportData));
    }

    /**
     * Import data for backup
     * @return exportData
     */
    public synchronized final boolean importData(final String value) {
        boolean isSuccess = false;
        if(!Strings.isNullOrEmpty(value)) {
            String backupData = getKeeperPrefs().fromKey(value);
            ExportData importDataModel = null;
            try {
                importDataModel = getGson().fromJson(backupData, TYPE_IMPORT_DATA);

                final String storedData = getKeeperPrefs().getString(KEEPER_ENTRY_LIST_DATA, null);
                List<KeeperEntry> entryList;
                if(Strings.isNullOrEmpty(storedData)) {
                    entryList = new ArrayList<KeeperEntry>();
                } else {
                    entryList = getGson().fromJson(storedData, TYPE_LIST_ENTRY);
                }

                final String importedData = importDataModel.getEntryListData();
                if(!Strings.isNullOrEmpty(importedData)) {
                    final List<KeeperEntry> importedList = getGson().fromJson(importedData, TYPE_LIST_ENTRY);
                    for(KeeperEntry importEntry : importedList) {
                        importEntry.updateModifiedTime();
                        importEntry.updateEntryId(getNewEntryId());
                        entryList.add(importEntry);
                    }
                }
                getKeeperPrefs().putString(KEEPER_ENTRY_LIST_DATA, getGson().toJson(entryList));

                isSuccess = true;
            } catch(Exception e) {
                isSuccess = false;
            }
        }
        return isSuccess;
    }
}
