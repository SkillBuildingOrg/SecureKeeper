/**
 * 
 */

package com.infinity.android.keeper.data.model;

import java.util.List;
import java.util.Locale;

import com.google.common.base.Strings;
import com.infinity.android.keeper.data.model.utils.EntrySubType;
import com.infinity.android.keeper.data.model.utils.EntryType;
import com.infinity.android.keeper.utils.Configs;
import com.infinity.android.keeper.utils.KeeperUtils;

/**
 * @author joshiroh
 */
public final class KeeperEntry {
    private long entryId;
    private String title;
    private String description;
    private String secreteKey;
    private String modifiedTime;
    private List<AdditionalInfo> additionalInfoList;
    private String entryType;
    private String entrySubType;

    /**
     * Constructor
     * @param entryId
     * @param title
     * @param description
     * @param token
     * @param time
     * @param additionalInfoList
     * @param entryType
     */
    public KeeperEntry(final long entryId, final String title, final String description, final String token, final String time, final List<AdditionalInfo> additionalInfoList,
            final String entryType, final String entrySubType) {
        this.entryId = entryId;
        this.title = !Strings.isNullOrEmpty(title) ? title.trim() : title;
        this.description = description;
        this.secreteKey = token;
        this.modifiedTime = time;
        this.additionalInfoList = additionalInfoList;
        this.entryType = entryType;
        this.entrySubType = entrySubType;
    }


    /**
     * @return the entrySubType
     */
    public final EntrySubType getEntrySubType() {
        if(Strings.isNullOrEmpty(entrySubType)) {
            entrySubType = EntrySubType.OTHER.getTypeString();
        } else {

        }
        return EntrySubType.fromString(entrySubType);
    }

    /**
     * @return the entryType
     */
    public final EntryType getEntryType() {
        if(Strings.isNullOrEmpty(entryType)) {
            entryType = EntryType.PERSONAL.getTypeString();
        }
        return EntryType.fromString(entryType);
    }

    /**
     * @return the additionalInfoList
     */
    public final List<AdditionalInfo> getAdditionalInfoList() {
        return additionalInfoList;
    }

    /**
     * @return the entryId
     */
    public final long getEntryId() {
        return entryId;
    }

    /**
     * Set new entry id
     * @param newId
     */
    public final void updateEntryId(final long newId) {
        entryId = newId;
    }

    /**
     * @return the title
     */
    public final String getTitle() {
        return title;
    }

    /**
     * @return the description
     */
    public final String getDescription() {
        return description;
    }

    /**
     * @return the secreteKey
     */
    public final String getSecreteKey() {
        return secreteKey;
    }

    /**
     * @return the modifiedTime
     */
    public final String getModifiedTime() {
        return modifiedTime;
    }

    /**
     * Update modified time to current one
     */
    public final void updateModifiedTime() {
        modifiedTime = KeeperUtils.getCurrentTime();
    }

    /**
     * Check if the entry is valid result for search text
     * @param text
     * @return isValid
     */
    public boolean isValidSearchResult(final String text) {
        boolean isValid = false;
        final Locale defLocale = Locale.getDefault();
        final String lowerCaseText = text.toLowerCase(defLocale);
        if(!Strings.isNullOrEmpty(title) && title.toLowerCase(defLocale).contains(lowerCaseText)) {
            isValid = true;
        } else if(!Strings.isNullOrEmpty(description) && description.toLowerCase(defLocale).contains(lowerCaseText)) {
            isValid = true;
        } else if(null != additionalInfoList && !additionalInfoList.isEmpty()) {
            String key;
            String value;
            for(AdditionalInfo info : additionalInfoList) {
                key = null != info.getKey() ? info.getKey().toLowerCase(defLocale) : Configs.EMPTY_STRING;
                value = null != info.getValue() ? info.getValue().toLowerCase(defLocale) : Configs.EMPTY_STRING;
                if(key.contains(lowerCaseText) || value.contains(lowerCaseText)) {
                    isValid = true;
                    break;
                }
            }
        }
        return isValid;
    }
}
