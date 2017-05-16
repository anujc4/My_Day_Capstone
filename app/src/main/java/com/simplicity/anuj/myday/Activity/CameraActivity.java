package com.simplicity.anuj.myday.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.simplicity.anuj.myday.R;

public class CameraActivity extends Activity implements CameraActivityFragment.VideoPathTransfer {

    private static final String LOG_TAG = CameraActivity.class.getCanonicalName();
    String path;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if (path != null) {
            Log.e(LOG_TAG + "SUCCESS", path);
            setResult(RESULT_OK, intent);
            intent.putExtra("video_path", path);
        } else {
            Log.e(LOG_TAG, "FAIL");
            setResult(RESULT_CANCELED, intent);
            intent.putExtra("video_path", "null");
        }
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, CameraActivityFragment.newInstance())
                    .commit();
        }
    }

    @Override
    public void onDataPass(String path) {
        this.path = path;
    }
}
