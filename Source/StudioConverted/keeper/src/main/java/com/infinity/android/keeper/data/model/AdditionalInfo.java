/**
 * 
 */

package com.infinity.android.keeper.data.model;

/**
 * @author joshiroh
 */
public final class AdditionalInfo {
    private String key;
    private String value;

    /**
     * Constructor
     * @param key
     * @param value
     */
    public AdditionalInfo(final String key, final String value) {
        this.key = key;
        this.value = value;
    }
    /**
     * @return the key
     */
    public final String getKey() {
        return key;
    }

    /**
     * @return the value
     */
    public final String getValue() {
        return value;
    }
}
