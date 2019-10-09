/**
 * 
 */
package com.infinity.android.keeper.adaptors;

import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.infinity.android.keeper.BaseKeeperActivity;


/**
 * @author joshiroh
 *
 */
public final class LegalPolicyWebViewclient extends WebViewClient {
    private BaseKeeperActivity context;

    /**
     * Default constructor
     * @param context
     */
    public LegalPolicyWebViewclient(final BaseKeeperActivity context) {
        this.context = context;
    }

    @Override
    public final boolean shouldOverrideUrlLoading(final WebView view, final String type) {
        if (type.startsWith("tel:")) {
            final Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse(type));
            context.startActivity(callIntent);
        } else {
            final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(type));
            context.startActivity(browserIntent);
        }
        return true;
    }
}
