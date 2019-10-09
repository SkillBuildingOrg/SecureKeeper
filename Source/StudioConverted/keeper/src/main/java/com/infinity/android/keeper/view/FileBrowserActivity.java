/**
 * 
 */

package com.infinity.android.keeper.view;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.infinity.android.keeper.BaseKeeperActivity;
import com.infinity.android.keeper.R;
import com.infinity.android.keeper.utils.KeeperUtils;

/**
 * @author joshiroh
 */
public final class FileBrowserActivity extends BaseKeeperActivity {

    public final static String EXTRA_FILE_PATH = "file_path";
    public final static String EXTRA_SHOW_HIDDEN_FILES = "show_hidden_files";
    public final static String EXTRA_ACCEPTED_FILE_EXTENSIONS = "accepted_file_extensions";
    private final static String DEFAULT_INITIAL_DIRECTORY = "/";

    private File Directory;
    private ArrayList<File> Files;
    protected FilePickerListAdapter Adapter;
    private boolean ShowHiddenFiles = false;
    private String[] acceptedFileExtensions;
    private ListView browseListView;
    private TextView noFilesDirectory;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_file_browser);

        browseListView = (ListView)findViewById(R.id.browserListView);
        noFilesDirectory = (TextView) findViewById(R.id.noFilesFoundText);

        // Set initial directory
        Directory = new File(DEFAULT_INITIAL_DIRECTORY);

        // Initialize the ArrayList
        Files = new ArrayList<File>();

        // Set the ListAdapter
        Adapter = new FilePickerListAdapter(this, Files);
        browseListView.setAdapter(Adapter);
        browseListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                File newFile = Adapter.getItem(position);
                if (newFile.isFile()) {
                    Intent intent = new Intent(appContext, KeeperListActivity.class);
                    intent.putExtra(EXTRA_FILE_PATH, newFile.getAbsolutePath());
                    startActivity(intent);
                    finish();
                } else {
                    Directory = newFile;
                    refreshFilesList();
                }
            }
        });

        // Initialize the extensions array to allow any file extensions
        acceptedFileExtensions = new String[] {};

        // Get intent extras
        if (getIntent().hasExtra(EXTRA_FILE_PATH)) {
            Directory = new File(getIntent().getStringExtra(EXTRA_FILE_PATH));
        }

        if (getIntent().hasExtra(EXTRA_SHOW_HIDDEN_FILES)) {
            ShowHiddenFiles = getIntent().getBooleanExtra(EXTRA_SHOW_HIDDEN_FILES, false);
        }

        if (getIntent().hasExtra(EXTRA_ACCEPTED_FILE_EXTENSIONS)) {
            ArrayList<String> collection = getIntent().getStringArrayListExtra(EXTRA_ACCEPTED_FILE_EXTENSIONS);
            acceptedFileExtensions = collection.toArray(new String[collection.size()]);
        }

        KeeperUtils.initActionBar(appContext, R.string.page_file_backup, true);
    }

    @Override
    protected void onResume() {
        refreshFilesList();
        super.onResume();
    }

    protected void refreshFilesList() {
        Files.clear();
        ExtensionFilenameFilter filter = new ExtensionFilenameFilter(acceptedFileExtensions);
        File[] files = Directory.listFiles(filter);
        if (files != null && files.length > 0) {
            noFilesDirectory.setVisibility(View.GONE);
            for (File f : files) {
                if (f.isHidden() && !ShowHiddenFiles) {
                    continue;
                }
                Files.add(f);
            }
            Collections.sort(Files, new FileComparator());
        } else {
            noFilesDirectory.setVisibility(View.VISIBLE);
        }
        Adapter.notifyDataSetChanged();
        browseListView.setSelectionAfterHeaderView();
    }

    @Override
    public void onBackPressed() {
        if (Directory.getParentFile() != null) {
            Directory = Directory.getParentFile();
            refreshFilesList();
            return;
        } else {
            Intent intent = new Intent(appContext, KeeperListActivity.class);
            startActivity(intent);
        }
        super.onBackPressed();
    }

    private class FilePickerListAdapter extends ArrayAdapter<File> {
        private List<File> mObjects;

        public FilePickerListAdapter(final Context context, final List<File> objects) {
            super(context, R.layout.file_list_item, android.R.id.text1, objects);
            mObjects = objects;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            View row = null;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.file_list_item, parent, false);
            } else {
                row = convertView;
            }

            File object = mObjects.get(position);

            ImageView imageView = (ImageView)row.findViewById(R.id.file_picker_image);
            TextView textView = (TextView)row.findViewById(R.id.file_picker_text);
            textView.setSingleLine(true);
            textView.setText(object.getName());

            if (object.isFile()) {
                imageView.setImageResource(R.drawable.icon_file);
            } else {
                imageView.setImageResource(R.drawable.icon_folder);
            }

            return row;
        }

        @Override
        public File getItem(final int position) {
            return mObjects.get(position);
        }

        @Override
        public int getCount() {
            return (null != mObjects ? mObjects.size() : 0);
        }
    }

    private class FileComparator implements Comparator<File> {
        @Override
        public int compare(final File f1, final File f2) {
            if (f1 == f2) {
                return 0;
            }
            if (f1.isDirectory() && f2.isFile()) {
                // Show directories above files
                return -1;
            }
            if (f1.isFile() && f2.isDirectory()) {
                // Show files below directories
                return 1;
            }
            // Sort the directories alphabetically
            return f1.getName().compareToIgnoreCase(f2.getName());
        }
    }

    private class ExtensionFilenameFilter implements FilenameFilter {
        private String[] Extensions;

        public ExtensionFilenameFilter(final String[] extensions) {
            super();
            Extensions = extensions;
        }

        @Override
        public boolean accept(final File dir, final String filename) {
            if (new File(dir, filename).isDirectory()) {
                // Accept all directory names
                return true;
            }
            if (Extensions != null && Extensions.length > 0) {
                for (int i = 0; i < Extensions.length; i++) {
                    if (filename.endsWith(Extensions[i])) {
                        // The filename ends with the extension
                        return true;
                    }
                }
                // The filename did not match any of the extensions
                return false;
            }
            // No extensions has been set. Accept all file extensions.
            return true;
        }
    }

    /*
     * (non-Javadoc)
     * @see com.infinity.android.keeper.BaseKeeperActivity#cleanup()
     */
    @Override
    public void cleanup() {
    }

}
