/**
 * 
 */

package com.infinity.android.keeper.adaptors;

import java.util.List;
import java.util.Locale;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.infinity.android.keeper.BaseKeeperActivity;
import com.infinity.android.keeper.R;
import com.infinity.android.keeper.data.model.KeeperEntry;
import com.infinity.android.keeper.utils.Configs;
import com.infinity.android.keeper.utils.KeeperUtils;

/**
 * @author joshiroh
 */
public final class EntryAdapter extends BaseAdapter {
    private List<KeeperEntry> entryList;
    private BaseKeeperActivity context;
    private LayoutInflater layoutInflater;

    /**
     * Constructor
     * 
     * @param context
     * @param entryList
     */
    public EntryAdapter(final BaseKeeperActivity context, final List<KeeperEntry> entryList) {
        this.context = context;
        this.entryList = entryList;
        layoutInflater = LayoutInflater.from(context);
    }

    /**
     * Set list
     * 
     * @param entryList
     */
    public final void setEntryList(final List<KeeperEntry> entryList) {
        this.entryList = entryList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return (null != entryList ? entryList.size() : 0);
    }

    @Override
    public KeeperEntry getItem(final int position) {
        return (null != entryList ? entryList.get(position) : null);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        EntryHolder holder;
        View adapterView = null;

        final KeeperEntry entry = entryList.get(position);
        if (null == convertView) {
            adapterView = layoutInflater.inflate(R.layout.list_entry_view, null);
            holder = new EntryHolder();
        } else {
            adapterView = convertView;
            holder = (EntryHolder)adapterView.getTag();
        }

        holder.alphabetView = (TextView)adapterView.findViewById(R.id.alphabetColorView);
        holder.alphabetBg = (ImageView)adapterView.findViewById(R.id.bgAlphabet);
        holder.titleView = (TextView)adapterView.findViewById(R.id.titleTextView);
        holder.categoryTypeView = (TextView)adapterView.findViewById(R.id.categoryTextView);
        holder.descriptionView = (TextView)adapterView.findViewById(R.id.descriptionTextView);

        String titleText = entry.getTitle();
        updateAlphabetView(holder.alphabetBg, holder.alphabetView, Configs.EMPTY_STRING + titleText.charAt(0));
        holder.titleView.setText(titleText);
        holder.categoryTypeView.setText(KeeperUtils.getCategoryInfo(entry.getEntryType(), entry.getEntrySubType()));

        if (!Strings.isNullOrEmpty(entry.getDescription())) {
            holder.descriptionView.setText(entry.getDescription());
        } else {
            holder.descriptionView.setText(R.string.text_label_no_description);
        }

        adapterView.setTag(holder);
        return adapterView;
    }

    /**
     * Update background color for the textview for given alphabet
     * 
     * @param view
     * @param alphabet
     */
    private void updateAlphabetView(final ImageView background, final TextView view, final String alphabet) {
        view.setText(alphabet);
        background.setBackgroundColor(KeeperUtils.getColorResourceByName(Configs.COLOR_ALPHABET + alphabet.toLowerCase(Locale.getDefault())));
    }

    /**
     * Perform cleanup
     */
    public void cleanup() {
        layoutInflater = null;
        context = null;
    }

    /**
     * Holder class
     * 
     * @author joshiroh
     */
    static class EntryHolder {
        ImageView alphabetBg;
        TextView alphabetView;
        TextView titleView;
        TextView categoryTypeView;
        TextView descriptionView;
    }
}
