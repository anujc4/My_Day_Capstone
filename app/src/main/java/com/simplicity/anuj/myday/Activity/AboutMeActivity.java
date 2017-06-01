package com.simplicity.anuj.myday.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.simplicity.anuj.myday.Data.JournalContentProvider;
import com.simplicity.anuj.myday.R;
import com.simplicity.anuj.myday.Testing.FakeDataForJournal;

import static android.widget.Toast.makeText;

public class AboutMeActivity extends AppCompatActivity {

    private int i = 6;
    private TextView versionTextView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        versionTextView = (TextView) findViewById(R.id.version_text_view);
        PackageInfo pInfo;
        String version;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = "Beta Version";
            e.printStackTrace();
        }
        versionTextView.setText(version);
        versionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i == 0) {
                    --i;
                    new AlertDialog.Builder(context)
                            .setTitle("WARNING")
                            .setMessage("This is a developer feature. Proceed at your own risk.")
                            .setPositiveButton("Delete DB", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteDB();
                                    ;
                                }
                            })
                            .setNegativeButton("Fake Data", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new FakeDataForJournal(context);
                                    makeText(context, "Fake Data is Inserted.", Toast.LENGTH_LONG).show();
                                }
                            })
                            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();


                } else {
                    i--;
                }
            }
        });
    }

    void deleteDB() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                context.getContentResolver()
                        .delete(JournalContentProvider.ContentProviderCreator.JOURNAL, null, null);
                context.getContentResolver()
                        .delete(JournalContentProvider.ImageContentProviderCreator.IMAGE, null, null);
                context.getContentResolver()
                        .delete(JournalContentProvider.VideoContentProviderCreator.VIDEO, null, null);
                context.getContentResolver()
                        .delete(JournalContentProvider.LocationContentProviderCreator.LOCATION, null, null);
                context.getContentResolver()
                        .delete(JournalContentProvider.WeatherContentProviderCreator.WEATHER, null, null);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (aBoolean)
                    makeText(context, "All Data is Deleted!", Toast.LENGTH_LONG).show();
                else
                    makeText(context, "Some Error Occurred.", Toast.LENGTH_LONG).show();
            }
        }.execute();
    }

}
