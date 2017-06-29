package com.popolam.apps.exchangeratesapp.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Project MOSST Code
 * Created by Serhiy Plekhov on 16.06.2017.
 */

public class FileUtil {
    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static File getFilesDir(Context context){

        if (isExternalStorageWritable()){
            return context.getExternalFilesDir(null);
        } else {
            return context.getFilesDir();
        }
    }

    public static File getCacheDir(Context context){
        File filesDir = getFilesDir(context);
        File cacheDir = new File(filesDir, "cache");
        if (!cacheDir.exists()){
            if (!cacheDir.mkdirs()){
                return filesDir;
            }
        }
        return cacheDir;
    }
}
