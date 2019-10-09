/**
 * 
 */
package com.infinity.android.keeper.data.model.utils;

import com.google.common.base.Strings;

/**
 * @author joshiroh
 */
public enum EntryType {
    PERSONAL("PERSONAL"),
    PROFESSIONAL("PROFESSIONAL");

    private String type;

    /**
     * Constructor
     * @param typeName
     */
    private EntryType(final String typeName) {
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
    public static EntryType fromString(final String text) {
        if (!Strings.isNullOrEmpty(text)) {
            for (EntryType value : EntryType.values()) {
                if (text.equalsIgnoreCase(value.type)) {
                    return value;
                }
            }
        }
        return null;
    }
}
