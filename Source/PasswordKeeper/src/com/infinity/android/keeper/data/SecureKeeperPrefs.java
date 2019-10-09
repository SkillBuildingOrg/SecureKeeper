/**
 *
 */

package com.infinity.android.keeper.data;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Base64;
import android.util.Log;

import com.google.common.base.Throwables;

/**
 * Performs the encryption and decryption operations while storing the data in shared preferences. Currently supported only for Strings, boolean, integer, long and float values.
 *
 * @author joshiroh
 */
public final class SecureKeeperPrefs {
    private static final String TAG = "SharedPreferences";
    private final String CHARSET = "UTF-8";
    private char[] SECRET = null;
    private byte[] SALT = null;
    private SharedPreferences prefsDeligate;

    /**
     * Default constructor
     *
     * @param context
     * @param prefsDeligate
     */
    public SecureKeeperPrefs(final Context context, final String prefName) {
        this.prefsDeligate = context.getApplicationContext().getSharedPreferences(prefName, Context.MODE_PRIVATE);
        setSecreteKey(context.getPackageName() + ".AJCPJ." + prefName + "." + TAG);
        setSaltKey(prefName + ".AJCPJ." + context.getPackageName() + "." + TAG);
    }

    /**
     * Set new secrete key
     *
     * @param key
     */
    private final void setSecreteKey(final String key) {
        SECRET = key.toCharArray();
    }

    /**
     * Set new salt key. This must be in UTF-8.
     *
     * @param salt
     */
    private final void setSaltKey(final String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.reset();
            SALT = md.digest(salt.getBytes(CHARSET));
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, Throwables.getStackTraceAsString(e));
        } catch (NoSuchAlgorithmException e) {
            Log.d(TAG, Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * @param value
     * @return encrypted value
     */
    private String encrypt(final String value) {
        try {
            final byte[] bytes = value != null ? value.getBytes(CHARSET) : new byte[0];
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec(SECRET));
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
            return new String(Base64.encode(pbeCipher.doFinal(bytes), Base64.NO_WRAP), CHARSET);
        } catch (Exception e) {
            Log.e(TAG, "Value could not encrypted. Value stored as plaintext.\n" + e.getMessage());
            return value;
        }
    }

    /**
     * @param value
     * @return decrypted value
     */
    private String decrypt(final String value) {
        try {
            final byte[] bytes = value != null ? Base64.decode(value, Base64.DEFAULT) : new byte[0];
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec(SECRET));
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
            return new String(pbeCipher.doFinal(bytes), CHARSET);
        } catch (Exception e) {
            Log.e(TAG, "Value could not decrypted. Value stored as plaintext.\n" + e.getMessage());
            return value;
        }
    }

    /**
     * Register for Shared preferences change listener
     * @param listener
     */
    public void registerOnSharedPreferenceChangeListener(final OnSharedPreferenceChangeListener listener) {
        prefsDeligate.registerOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Unregister shared preferences change listener
     * @param listener
     */
    public void unregisterOnSharedPreferenceChangeListener(final OnSharedPreferenceChangeListener listener) {
        prefsDeligate.unregisterOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Convert normal string key to Secure key
     *
     * @param key
     * @return valueKey
     */
    public final String toKey(final String key) {
        return encrypt(key);
    }

    /**
     * Convert secure key to normal value
     * @param key
     * @return valueKey
     */
    public final String fromKey(final String key) {
        return decrypt(key);
    }

    /**
     * Get all values in Map
     *
     * @return map
     */
    public Map<String, String> getAll() {
        final Map<String, ?> encryptedMap = prefsDeligate.getAll();
        final Map<String, String> decryptedMap = new HashMap<String, String>(encryptedMap.size());
        for (Entry<String, ?> entry : encryptedMap.entrySet()) {
            try {
                decryptedMap.put(decrypt(entry.getKey()), decrypt(entry.getValue().toString()));
            } catch (Exception e) {
                // Ignore unencrypted key/value pairs
                Log.d(TAG, Throwables.getStackTraceAsString(e));
            }
        }
        return decryptedMap;
    }

    /**
     * Check if sharedPref contains given key
     *
     * @param key
     * @return isPresent
     */
    public final boolean containskey(final String key) {
        return prefsDeligate.contains(key);
    }

    /**
     * Clear all saved shared preferences data
     */
    public final void clearPreferences() {
        if (null != prefsDeligate) {
            prefsDeligate.edit().clear().commit();
        }
    }

    /**
     * Remove entry for given key from the shared preferences storage
     *
     * @param key
     */
    public final void removeValue(final String key) {
        if (null != prefsDeligate) {
            prefsDeligate.edit().remove(toKey(key)).commit();
        }
    }

    /**
     * Put string value in the shared preferences
     *
     * @param key
     * @param value
     */
    public final void putString(final String key, final String value) {
        if (null == value) {
            removeValue(key);
        } else {
            prefsDeligate.edit().putString(toKey(key), encrypt(value)).commit();
        }
    }

    /**
     * Get string value from shared preferences
     *
     * @param key
     * @param defaultValue
     * @return stringValue
     */
    public final String getString(final String key, final String defaultValue) {
        if (null != prefsDeligate && prefsDeligate.contains(toKey(key))) {
            String secureVal = prefsDeligate.getString(toKey(key), null);
            return (null != secureVal ? decrypt(secureVal) : defaultValue);
        }
        return defaultValue;
    }

    /**
     * Put int value
     * @param key
     * @param value
     */
    public final void putInt(final String key, final int value) {
        prefsDeligate.edit().putString(toKey(key), encrypt(Integer.toString(value))).commit();
    }

    /**
     * Get int value for key
     * @param key
     * @param defaultValue
     * @return intValue
     */
    public final int getInt(final String key, final int defaultValue) {
        int returnVlaue = defaultValue;
        try {
            returnVlaue = Integer.parseInt(getString(key, Integer.toString(defaultValue)));
        } catch (NumberFormatException e) {
            // Default value will be returned in case of exception.
            Log.d(TAG, Throwables.getStackTraceAsString(e));
        }
        return returnVlaue;
    }

    /**
     * Put float value
     * @param key
     * @param value
     */
    public final void putFloat(final String key, final float value) {
        prefsDeligate.edit().putString(toKey(key), encrypt(Float.toString(value))).commit();
    }

    /**
     * Get float value
     * @param key
     * @param defaultValue
     * @return floatValue
     */
    public final float getFloat(final String key, final float defaultValue) {
        float returnVlaue = defaultValue;
        try {
            returnVlaue = Float.parseFloat(getString(key, Float.toString(defaultValue)));
        } catch (NumberFormatException e) {
            // Default value will be returned in case of exception.
            Log.d(TAG, Throwables.getStackTraceAsString(e));
        }
        return returnVlaue;
    }

    /**
     * Put boolean value
     * @param key
     * @param value
     */
    public final void putBoolean(final String key, final boolean value) {
        prefsDeligate.edit().putString(toKey(key), encrypt(Boolean.toString(value))).commit();
    }

    /**
     * Get boolean value
     * @param key
     * @param defaultValue
     * @return boolValue
     */
    public final boolean getBoolean(final String key, final boolean defaultValue) {
        boolean returnVlaue = defaultValue;
        try {
            returnVlaue = Boolean.parseBoolean(getString(key, Boolean.toString(defaultValue)));
        } catch (NumberFormatException e) {
            // Default value will be returned in case of exception.
            Log.d(TAG, Throwables.getStackTraceAsString(e));
        }
        return returnVlaue;
    }

    /**
     * Put string set
     * @param key
     * @param values
     */
    public final void putStringSet(final String key, final Set<String> values) {
        final Set<String> encryptedValues = new HashSet<String>(
                values.size());
        for (String value : values) {
            encryptedValues.add(encrypt(value));
        }
        prefsDeligate.edit().putStringSet(toKey(key), encryptedValues).commit();
    }

    /**
     * Get string set
     * @param key
     * @param defaultValues
     * @return stringSet
     */
    public final Set<String> getStringSet(final String key, final Set<String> defaultValues) {
        final Set<String> encryptedSet = prefsDeligate.getStringSet(toKey(key), null);
        if (encryptedSet == null) {
            return defaultValues;
        }
        final Set<String> decryptedSet = new HashSet<String>(encryptedSet.size());
        for (String encryptedValue : encryptedSet) {
            decryptedSet.add(decrypt(encryptedValue));
        }
        return decryptedSet;
    }
}
