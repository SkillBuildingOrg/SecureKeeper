/**
 * 
 */
package com.infinity.android.keeper.manager;

import com.google.common.base.Strings;
import com.infinity.android.keeper.data.SecureKeeperPrefs;
import com.infinity.android.keeper.utils.Configs;
import com.infinity.android.keeper.utils.KeeperUtils;

/**
 * @author joshiroh<br>
 * 
 * Data manager.<br>
 * Takes care of collection & formatting/sorting of data required for the UI.
 */
public final class KeeperManager extends BaseManager {
    private static final String KEEPER_LOGIN = "KEEPER+LOGIN";
    private static final String KEEPER_FORGET = "KEEPER+FORGET";

    private static KeeperManager keeperManager;
    private static SecureKeeperPrefs keeperPrefs;

    /**
     * Singleton implementation
     */
    private KeeperManager() {
        keeperPrefs = getKeeperPrefs();
    }

    /**
     * Get singleton instance of KeeperManager
     */
    public static final KeeperManager getInstance() {
        if(null == keeperManager) {
            keeperManager = new KeeperManager();
        }
        return keeperManager;
    }

    /**
     * Get share prefs instance
     * @return sharedPrefs
     */
    private SecureKeeperPrefs getKeeperPrefs() {
        if(null == keeperPrefs) {
            keeperPrefs = new SecureKeeperPrefs(KeeperUtils.getAppContext(), BaseManager.KEEPER_PREFS);
        }
        return keeperPrefs;
    }

    /**
     * Save Access token for next validation.
     * @param accessToken
     * @return isSuccess
     */
    public final void storeKeeperToken(final String accessToken) {
        getKeeperPrefs().putString(KEEPER_LOGIN, accessToken);
    }

    /**
     * Checks if given token is valid access token
     * @param token
     * @return isValid
     */
    public final boolean isKeeperTokenValid(final String token) {
        if(!Strings.isNullOrEmpty(token)) {
            return token.equals(getKeeperPrefs().getString(KEEPER_LOGIN, Configs.EMPTY_STRING));
        }
        return false;
    }

    /**
     * Create password reset token.
     * @return token
     */
    public final String createResetToken() {
        SecureKeeperPrefs prefs = getKeeperPrefs();
        String currentToken = KEEPER_LOGIN + prefs.getString(KEEPER_LOGIN, Configs.EMPTY_STRING);
        prefs.putString(KEEPER_FORGET, currentToken);
        return prefs.toKey(currentToken);
    }

    /**
     * Checks if the password reset token is valid.
     * @param token
     * @return isValid
     */
    public final boolean isValidResetToken(final String token) {
        if(!Strings.isNullOrEmpty(token)) {
            String resetToken = getKeeperPrefs().fromKey(token);
            return resetToken.equals(getKeeperPrefs().getString(KEEPER_FORGET, null));
        }
        return false;
    }

    /**
     * Delete password reset token
     */
    public final void deleteResetToken() {
        getKeeperPrefs().putString(KEEPER_FORGET, null);
    }

    /**
     * Check if token access for the Keeper is configured or not.
     * @return isConfigured
     */
    public final boolean isKeeperConfigured() {
        return (null != getKeeperPrefs().getString(KEEPER_LOGIN, null));
    }
}
