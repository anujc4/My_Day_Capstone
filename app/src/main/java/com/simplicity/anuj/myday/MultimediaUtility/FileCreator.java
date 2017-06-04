package com.simplicity.anuj.myday.MultimediaUtility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by anujc on 23-05-2017.
 */

public class FileCreator {
    private static final int REQUEST_TAKE_VIDEO = 2001;
    private static final String LOG_TAG = FileCreator.class.getSimpleName();
    private static final int PERMISSION_CODE = 100;
    private final String PermissionsList[] = {
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"
    };
    private Context context;

    //Constructor
    public FileCreator(Context context) {
        this.context = context;
    }


    public File createVideoFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String videoFileName = "MYDAY_" + timeStamp + "_";
        File storageDir = null;
        File video = null;
        int try_count = 2;
        while (true) {
            try {
                SharedPreferences preferences = context.getSharedPreferences("com.simplicity.anuj.myday.FileDirectory", MODE_PRIVATE);
                String defaultFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath();
                String path = preferences.getString("file_dir_video", defaultFolder);
                storageDir = new File(path);
//                Log.e(LOG_TAG, path);
                video = File.createTempFile(
                        videoFileName,  /* prefix */
                        ".mp4",         /* suffix */
                        storageDir      /* directory */
                );
                break;
            } catch (IOException e) {
                if (--try_count <= 0)
                    break;
                Log.e(LOG_TAG, "Unable to create File");
                e.printStackTrace();
            } catch (SecurityException s) {
                Log.e(LOG_TAG, "Unable to create File due to missing Permissions");
                s.printStackTrace();
                if (--try_count <= 0)
                    break;
                createMyDayHomeDirectory();
            }
        }
        // Save a file: path for use with ACTION_VIEW intents
        if (video != null) {
            return video;
        }
        return null;
    }


    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "MYDAY_" + timeStamp + "_";
        File storageDir = null;
        File image = null;
        int try_count = 2;
        while (true) {
            try {
                SharedPreferences preferences = context.getSharedPreferences("com.simplicity.anuj.myday.FileDirectory", MODE_PRIVATE);
                String defaultFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
                String path = preferences.getString("file_dir_image", defaultFolder);
                storageDir = new File(path);
                if (storageDir.isDirectory() && storageDir.exists())
                    image = File.createTempFile(
                            imageFileName,  /* prefix */
                            ".jpg",         /* suffix */
                            storageDir      /* directory */
                    );
                break;
            } catch (IOException e) {
                if (--try_count <= 0)
                    break;
                Log.e(LOG_TAG, "Unable to create File");
                createMyDayHomeDirectory();
                e.printStackTrace();
            } catch (SecurityException s) {
                Log.e(LOG_TAG, "Unable to create File due to missing Permissions");
                s.printStackTrace();
                if (--try_count <= 0)
                    break;
                createMyDayHomeDirectory();
            }
        }
        // Save a file: path for use with ACTION_VIEW intents
        if (image != null) {
            return image;
        }
        return null;
    }


    private void createMyDayHomeDirectory() {
        SharedPreferences checkDir = context.getSharedPreferences("com.simplicity.anuj.myday.FileDirectory", MODE_PRIVATE);
        Log.e(LOG_TAG, "Creating Folder Again");
        if (!checkDir.contains("file_dir_image") && !checkDir.contains("file_dir_video")) {
            String MyDayFolder = Environment.getExternalStorageDirectory().toString();
            File AppDirectory = new File(MyDayFolder + "/MyDay");
            final String path = AppDirectory.getAbsolutePath();
            try {
                boolean success = AppDirectory.mkdirs();
                if (success) {
                    Log.e(LOG_TAG, "Created Dirs");
                    SharedPreferences fileDirPreferences = context.getSharedPreferences("com.simplicity.anuj.myday.FileDirectory", MODE_PRIVATE);
                    SharedPreferences.Editor editor = fileDirPreferences.edit();
                    editor.putString("file_dir_image", path);
                    editor.putString("file_dir_video", path);
                    editor.apply();
                }
            } catch (SecurityException e) {
                e.printStackTrace();
                ActivityCompat.requestPermissions((Activity) context, PermissionsList, PERMISSION_CODE);
            }
        }
    }
}
