/**
 * 
 */
package com.infinity.android.keeper.data.model.utils;

import com.google.common.base.Strings;

/**
 * @author joshiroh
 */
public enum EntrySubType {
    BANKING("BANKING"),
    EMAIL("EMAIL"),
    SOCIAL("SOCIAL"),
    PRIVATE("PRIVATE"),
    PHONE("PHONE"),
    OTHER("OTHER");

    private String type;

    /**
     * Constructor
     * @param typeName
     */
    private EntrySubType(final String typeName) {
        this.type = typeName;
    }

    /**
     * Get type of account balance
     * @return string
     */
    public String getTypeString() {
        return type;
    }

    /**
     * Convert string to enum.
     * @param text
     * @return balanceType
     */
    public static EntrySubType fromString(final String text) {
        if (!Strings.isNullOrEmpty(text)) {
            for (EntrySubType value : EntrySubType.values()) {
                if (text.equalsIgnoreCase(value.type)) {
                    return value;
                }
            }
        }
        return null;
    }
}
