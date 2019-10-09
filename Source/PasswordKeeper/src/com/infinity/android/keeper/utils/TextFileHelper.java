/**
 * 
 */

package com.infinity.android.keeper.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;

/**
 * @author joshiroh
 */
/*
 * This class is for text file CRUD. You can just make flashMessage() a comment if you do not need it.
 */
public class TextFileHelper {
    // name of our text file
    public static final String BACKUP_FILE = "secure_backup_file.txt";
    private String filePath;

    /*
     * Create a text file.
     */
    public boolean createTextFile(final String secureData) {
        boolean isSuccess = false;
        File rootPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "sekeep");
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }
        File dataFile = new File(rootPath, BACKUP_FILE);
        try {
            FileOutputStream mOutput = new FileOutputStream(dataFile, false);
            mOutput.write(secureData.getBytes());
            mOutput.close();
            isSuccess = true;
        } catch (FileNotFoundException e) {
            Log.d("CreateFile", "FileNotFoundException : " + Throwables.getStackTraceAsString(e));
        } catch (IOException e) {
            Log.d("CreateFile", "IOException : " + Throwables.getStackTraceAsString(e));
        }
        filePath = dataFile.getAbsolutePath();
        return isSuccess;
    }

    /**
     * Get File Path
     * 
     * @return filePath
     */
    public final String getFilePathInfo() {
        return !Strings.isNullOrEmpty(filePath) ? filePath : Configs.EMPTY_STRING;
    }

    /**
     * Example method call for this is,
     File downloadDir = new File(Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()); getFilesFromDir(downloadDir);
     * 
     * @param filesFromSD
     */
    public void getFilesFromDir(final File filesFromSD) {
        File listAllFiles[] = filesFromSD.listFiles();
        if (listAllFiles != null && listAllFiles.length > 0) {
            for (File currentFile : listAllFiles) {
                if (currentFile.isDirectory()) {
                    getFilesFromDir(currentFile);
                } else {
                    if (currentFile.getName().endsWith("")) {
                        // File absolute path
                        Log.e("File path", currentFile.getAbsolutePath());
                        // File Name
                        Log.e("File path", currentFile.getName());

                    }
                }
            }
        }
    }


}
