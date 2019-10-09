/**
 * 
 */

package com.infinity.android.keeper.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author joshiroh
 */
public final class IdleTimeoutReciever extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d("IdleTimeoutReciever", "AutoLogout Event received for timeout notification.");
        // POST Event for auto logout
    }
}
