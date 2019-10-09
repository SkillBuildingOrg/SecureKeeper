/**
 * 
 */

package com.infinity.android.keeper.view.fragments;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.infinity.android.keeper.R;
import com.infinity.android.keeper.listeners.EntryUpdateListener;
import com.infinity.android.keeper.utils.Configs;

/**
 * @author joshiroh
 */
public final class KeyValueView extends LinearLayout {

    private EditText keyText;
    private EditText valueText;
    private EntryUpdateListener listener;
    private boolean shouldAddNew = false;

    /**
     * Constructor
     * @param context
     * @param listener
     */
    public KeyValueView(final Context context, final EntryUpdateListener listener) {
        this(context);
        this.listener = listener;
    }

    public KeyValueView(final Context context, final EntryUpdateListener listener, final String key, final String value) {
        this(context);
        this.listener = listener;
        keyText.setText(key);
        valueText.setText(value);
    }

    /**
     * Constructor
     * @param context
     */
    private KeyValueView(final Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_input_key_value, this, true);
        keyText = (EditText)findViewById(R.id.enterEntryKey);
        valueText = (EditText)findViewById(R.id.enterEntryValue);

        keyText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
            }

            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if(!shouldAddNew && s.toString().trim().length() > 0) {
                    shouldAddNew = true;
                    listener.updateState(shouldAddNew);
                } else if(isValuesEmpty()) {
                    shouldAddNew = false;
                    listener.updateState(shouldAddNew);
                }
            }
        });

        valueText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
            }

            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if(!shouldAddNew && s.toString().trim().length() > 0) {
                    shouldAddNew = true;
                    listener.updateState(shouldAddNew);
                } else if(isValuesEmpty()) {
                    shouldAddNew = false;
                    listener.updateState(shouldAddNew);
                }
            }
        });
        requestAutoFocus();
    }

    /**
     * Request focus for KEY field when added.
     */
    public final void requestAutoFocus() {
        if(null != keyText) {
            keyText.requestFocus();
        }
    }

    /**
     * Get KEY text
     * 
     * @return key
     */
    public final String getKeyText() {
        return (null != keyText ? keyText.getText().toString().trim() : Configs.EMPTY_STRING);
    }

    /**
     * Set KEY text
     * @param key
     */
    public final void setKeyText(final String key) {
        keyText.setText(key);
    }

    /**
     * Set VALUE text
     * @param value
     */
    public final void setValueText(final String value) {
        valueText.setText(value);
    }

    /**
     * @return VALUE
     */
    public final String getValueText() {
        return (null != valueText ? valueText.getText().toString().trim() : Configs.EMPTY_STRING);
    }

    /**
     * Check if both KEY & VALUE are empty
     * @return isEmpty
     */
    public final boolean isValuesEmpty() {
        return (getValueText().trim().isEmpty() && getKeyText().trim().isEmpty());
    }
}
