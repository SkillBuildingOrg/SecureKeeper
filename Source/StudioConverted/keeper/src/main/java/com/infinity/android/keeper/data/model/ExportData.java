/**
 * 
 */

package com.infinity.android.keeper.data.model;

/**
 * @author joshiroh
 */
public final class ExportData {
    private String entryListData;
    private String backupTime;
    private long backupId;

    public ExportData(final long backupId, final String backupTime, final String entryListData) {
        this.backupId = backupId;
        this.backupTime = backupTime;
        this.entryListData = entryListData;
    }

    /**
     * @return the entryListData
     */
    public final String getEntryListData() {
        return entryListData;
    }

    /**
     * @return the backupTime
     */
    public final String getBackupTime() {
        return backupTime;
    }

    /**
     * @return the backupId
     */
    public final long getBackupId() {
        return backupId;
    }

}
