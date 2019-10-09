/**
 * 
 */

package com.infinity.android.keeper.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.infinity.android.keeper.BaseKeeperActivity;
import com.infinity.android.keeper.R;
import com.infinity.android.keeper.data.model.utils.EntrySubType;
import com.infinity.android.keeper.data.model.utils.EntryType;
/**
 * @author joshiroh
 */
public final class KeeperUtils {
    private static Context appContext;
    private static boolean isUserLoggedin = false;

    private KeeperUtils() {
        /*
         * Private constructor to restrict creating object of the class
         */
    }

    /**
     * Update login status
     * @param newStatus
     */
    public static final void updateLoginStatus(final boolean newStatus) {
        isUserLoggedin = newStatus;
    }

    /**
     * @return isLoggedin
     */
    public static final boolean isUserLoggedin() {
        return isUserLoggedin;
    }

    /**
     * Set application context
     * 
     * @param context
     */
    public static final void setAppContext(final Context context) {
        appContext = context.getApplicationContext();
    }

    /**
     * Get application context
     * 
     * @return appContext
     */
    public static final Context getAppContext() {
        return appContext;
    }

    /**
     * Hide soft keyboard
     * 
     * @param context
     * @param editText
     */
    public static final void hideKeyboard(final Context context, final View editText) {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * Call this on parent view when activity is finished Unbind drawables associated with views & clear views to free the occupied memory
     * 
     * @param parentView
     */
    public static final void unbindAndClearViews(final View parentView) {
        if (null != parentView) {
            try {
                if (null != parentView.getBackground()) {
                    parentView.getBackground().setCallback(null);
                    parentView.setBackgroundResource(0);
                }
                if (parentView instanceof ViewGroup) {
                    int childCount = ((ViewGroup)parentView).getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        unbindAndClearViews(((ViewGroup)parentView).getChildAt(i));
                    }
                    if (!(parentView instanceof AdapterView)) {
                        ((ViewGroup)parentView).removeAllViews();
                    }
                }
            } catch (Exception e) {
                // Catch for generic cleanup failure. No handling required.
                Log.d("KeeperUtils", "unbindAndClearViews() : " + Throwables.getStackTraceAsString(e));
            }
        }
    }

    /**
     * Show Alert dialog for the given error message.
     * 
     * @return
     */
    public static final AlertDialog showErrorAlertMessage(final BaseKeeperActivity context, final String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle(R.string.alert_title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.button_ok, new OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
        alert.setCancelable(false);
        return alert;
    }

    /**
     * Get current date & time formatted as, EEE, d MMM yyyy, HH:mm
     * 
     * @return formattedDate
     */
    public static final String getCurrentTime() {
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm", Locale.US);
        return df.format(Calendar.getInstance().getTime());
    }

    /**
     * Display custom toast for adding record successfully
     * 
     * @param context
     * @param toastMsg
     * @return toast
     */
    public static final Toast displaySuccessToast(final BaseKeeperActivity context, final String toastMsg) {
        Toast toast = new Toast(context.getApplicationContext());
        try {
            LayoutInflater inflater = LayoutInflater.from(context);
            View mainLayout = inflater.inflate(R.layout.layout_custom_toast, null);
            View rootLayout = mainLayout.findViewById(R.id.toast_layout_root);
            TextView text = (TextView)mainLayout.findViewById(R.id.text);
            hideKeyboard(context, text);
            if (!Strings.isNullOrEmpty(toastMsg)) {
                text.setText(toastMsg);
            }
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(rootLayout);
            toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.CENTER, 0, 0);
            toast.show();
        } catch (Exception ex) {
            Log.d("displayCustomToast", Throwables.getStackTraceAsString(ex));
        }
        return toast;
    }

    /**
     * Ask user to enter password to proceed.
     */
    public static final AlertDialog promptForPassword(final BaseKeeperActivity context, final LinearLayout passwordLayout, final DialogInterface.OnClickListener positiveListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle(R.string.alert_title);
        builder.setView(passwordLayout);

        builder.setPositiveButton(R.string.button_ok, positiveListener);
        builder.setNegativeButton(R.string.button_cancel, new OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                hideKeyboard(context, passwordLayout);
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
        alert.setCancelable(false);
        return alert;
    }

    /**
     * Check if given email id is valid format
     * 
     * @param emailId
     * @return isValid
     */
    public static final boolean isValidEmailId(final String emailId) {
        if (!Strings.isNullOrEmpty(emailId)) {
            return Patterns.EMAIL_ADDRESS.matcher(emailId).matches();
        }
        return false;
    }

    /**
     * Check if given email id is valid format
     * 
     * @param emailId
     * @return isValid
     */
    public static final boolean isValidPhoneNumber(final String number) {
        if (!Strings.isNullOrEmpty(number)) {
            return Patterns.PHONE.matcher(number).matches();
        }
        return false;
    }

    /**
     * Show error message in edit text field
     * 
     * @param editField
     * @param message
     */
    public static final void showErrorInEditText(final EditText editField, final String message) {
        int ecolor = Color.RED; // whatever color you want
        ForegroundColorSpan fgcspan = new ForegroundColorSpan(ecolor);
        SpannableStringBuilder ssbuilder = new SpannableStringBuilder(message);
        ssbuilder.setSpan(fgcspan, 0, message.length(), 0);
        editField.setError(ssbuilder);
    }

    /**
     * Creates page title view and adds to the given parent Header. It returns the menu view to handle the click events.
     * 
     * @param context
     * @param headerText
     * @param drawableId
     * @param clickListener for menu image click on header<br>
     *            (If drawableId is set then set this listener to handle click events on menuImage)
     * @return headerLayout
     */
    public static final View getPageTitleView(final Context context, final String headerText) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View headerLayout = inflater.inflate(R.layout.layout_page_title, null);
        TextView headerTextView = (TextView)headerLayout.findViewById(R.id.headerTitleView);
        headerTextView.setText(headerText);
        LinearLayout menuButton = (LinearLayout)headerLayout.findViewById(R.id.headerMenuContainer);
        return headerLayout;
    }

    /**
     * Bottom menu bar for the page.
     * @param context
     * @return menuBar
     */
    public static final View getDefaultMenuBarView(final BaseKeeperActivity context) {
        LayoutInflater inflater = context.getInflater();
        View pageMenuBar = inflater.inflate(R.layout.layout_menu_bar, null);
        return pageMenuBar;
    }

    /**
     * Header menu Image view
     * @param context
     * @param drawableId
     * @return
     */
    public static final ImageView getHeaderMenuImage(final BaseKeeperActivity context, final int drawableId) {
        final ImageView menuImage = new ImageView(context);
        menuImage.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        menuImage.setScaleType(ScaleType.CENTER);
        int defaultPadding = (int)context.getResources().getDimension(R.dimen.default_padding);
        menuImage.setPadding(defaultPadding, 0, defaultPadding/2, 0);
        menuImage.setImageResource(drawableId);
        return menuImage;
    }

    /**
     * Get category information string
     * @param category
     * @param subType
     * @return info
     */
    public static String getCategoryInfo(final EntryType category, final EntrySubType subType) {
        StringBuilder info = new StringBuilder();
        if(null != category) {
            switch (category) {
                case PERSONAL:
                    info.append(appContext.getString(R.string.spinner_personal));
                    break;
                case PROFESSIONAL:
                    info.append(appContext.getString(R.string.spinner_professional));
                    break;
            }
        }
        if(null != subType) {
            if(info.length() > 0) {
                info.append(", ");
            }

            switch (subType) {
                case BANKING:
                    info.append(appContext.getString(R.string.spinner_banking));
                    break;
                case EMAIL:
                    info.append(appContext.getString(R.string.spinner_email));
                    break;
                case OTHER:
                    info.append(appContext.getString(R.string.spinner_others));
                    break;
                case PHONE:
                    info.append(appContext.getString(R.string.spinner_phone));
                    break;
                case PRIVATE:
                    info.append(appContext.getString(R.string.spinner_private));
                    break;
                case SOCIAL:
                    info.append(appContext.getString(R.string.spinner_social));
                    break;
            }
        }
        return info.toString();
    }
}
