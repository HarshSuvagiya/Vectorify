package com.scorpion.vectorial;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class FileUtils {

    public static String defaultPath;
    public static String tempFiles;

    public FileUtils(Context context) {
        String appName = context.getString(R.string.app_name);
        defaultPath =
                Environment.getExternalStorageDirectory() + File.separator + appName;
        tempFiles = context.getFilesDir().getPath() + "/temp";
        createFoldersifNotExist();
    }

    private static void createFoldersifNotExist() {

        File defaultfile = new File(defaultPath);
        File tempfile = new File(tempFiles);

        if (!defaultfile.exists()) {
            if (defaultfile.mkdir()){
                Log.i("TAG", "createFoldersifNotExist: default created");
            }
        }
        if (!tempfile.exists()) {
            if (tempfile.mkdir()){
                Log.i("TAG", "createFoldersifNotExist: temp created");
            }
        }

    }

    public static String getDefaultPath() {
        return defaultPath + File.separator;
    }

    public static String getTempFiles() {
        return tempFiles + File.separator;
    }
}
