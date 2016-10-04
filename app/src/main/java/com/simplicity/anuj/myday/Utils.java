package com.simplicity.anuj.myday;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by anuj on 9/30/2016.
 */
public class Utils {

    public static final int _ID = 0;
    public static final int DATE_CREATED= 1;
    public static final int TIME_CREATED = 2;
    public static final int DATE_MODIFIED =3;
    public static final int TIME_MODIFIED = 4;
    public static final int LATITUDE = 5;
    public static final int LONGITUDE = 6;
    public static final int TITLE = 7;
    public static final int ENTRY = 8;
    public static final int IMAGE_PATH = 9;

    public static Context c;

    public Utils() {
    }

    public Utils(Context c) {
        Utils.c = c;
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }






}
