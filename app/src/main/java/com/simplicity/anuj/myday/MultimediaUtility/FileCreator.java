package com.simplicity.anuj.myday.MultimediaUtility;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by anujc on 23-05-2017.
 */

public class FileCreator {
    private static final int REQUEST_TAKE_VIDEO = 2001;
    private static final String LOG_TAG = FileCreator.class.getSimpleName();
    private Context context;

    //Constructor
    public FileCreator(Context context) {
        this.context = context;
    }


    public File createVideoFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String videoFileName = "MYDAY_" + timeStamp + "_";
        File storageDir = null;
        File video = null;
        try {
            SharedPreferences preferences = context.getSharedPreferences("com.simplicity.anuj.myday.FileDirectory", 0);
            String defaultFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath();
            String path = preferences.getString("file_dir_video", defaultFolder);
            storageDir = new File(path);
            Log.e(LOG_TAG, path);
            video = File.createTempFile(
                    videoFileName,  /* prefix */
                    ".mp4",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            Log.e(LOG_TAG, "Unable to create File");
            e.printStackTrace();
        } catch (SecurityException s) {
            Log.e(LOG_TAG, "Unable to create File due to missing Permissions");
            s.printStackTrace();
        }
        // Save a file: path for use with ACTION_VIEW intents
        if (video != null) {
            return video;
        }
        return null;
    }


    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "MYDAY_" + timeStamp + "_";
        File storageDir = null;
        File image = null;
        try {
            SharedPreferences preferences = context.getSharedPreferences("com.simplicity.anuj.myday.FileDirectory", 0);
            String defaultFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
            String path = preferences.getString("file_dir_image", defaultFolder);
            storageDir = new File(path);
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            Log.e(LOG_TAG, "Unable to create File");
            e.printStackTrace();
        } catch (SecurityException s) {
            Log.e(LOG_TAG, "Unable to create File due to missing Permissions");
            s.printStackTrace();
        }
        // Save a file: path for use with ACTION_VIEW intents
        if (image != null) {
            return image;
        }
        return null;
    }
}
