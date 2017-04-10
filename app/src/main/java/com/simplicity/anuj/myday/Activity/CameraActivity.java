package com.simplicity.anuj.myday.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.simplicity.anuj.myday.R;

public class CameraActivity extends Activity implements CameraActivityFragment.VideoPathTransfer {

    private static final String LOG_TAG = CameraActivity.class.getCanonicalName();
    String path;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AddEntryActivity.class);
        if (path != null) {
            intent.putExtra("path", path);
        } else {
            intent.putExtra("path", "null");
        }
        startActivity(intent);
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
