/**
 * 
 */
package com.infinity.android.keeper.manager;

import com.google.gson.Gson;

/**
 * @author joshiroh
 * 
 * Base class for Manager functionality.<br>
 * Define common functionality for all manager classes here.
 *
 */
public abstract class BaseManager {
    public static final String KEEPER_PREFS = "Keeper+SharedInformation";
    /*
     * GSon instance
     */
    private static Gson gson;

    /**
     * @return gson instance
     */
    public static Gson getGson() {
        if (null == gson) {
            gson = new Gson();
        }
        return gson;
    }
}
