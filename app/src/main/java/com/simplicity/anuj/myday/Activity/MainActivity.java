package com.simplicity.anuj.myday.Activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.simplicity.anuj.myday.Adapter.JournalAdapter;
import com.simplicity.anuj.myday.Data.JournalContentProvider;
import com.simplicity.anuj.myday.Identity.FirebaseSignInActivity;
import com.simplicity.anuj.myday.IntroActivityUtils.IntroActivity;
import com.simplicity.anuj.myday.R;
import com.simplicity.anuj.myday.Settings.Settings;
import com.simplicity.anuj.myday.Utility.ItemClickListener;
import com.simplicity.anuj.myday.Utility.Utils;

import java.io.File;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor>, ActivityCompat.OnRequestPermissionsResultCallback, ItemClickListener {


    private FirebaseAnalytics mFirebaseAnalytics;

    //Loader ID
    private static final int LOADER_ID = 1;

    private static final int PERMISSION_CODE = 100;
    private static final int REQUEST_AUTHENTICATION_CODE = 200;
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    //LIST OF PERMISSIONS NEEDED FROM USER
    private final String PermissionsList[] = {
            "android.permission.CAMERA",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"
    };
    private Context mContext;
    private TextView emptyTextView;
    private ImageView emptyImageView;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private JournalAdapter mJournalAdapter;
    private FloatingActionButton fab;

    GoogleApiClient mGoogleApiClientDrive;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Thread AskPermissions = new Thread(new Runnable() {
            @Override
            public void run() {
                ActivityCompat.requestPermissions((Activity) mContext, PermissionsList, PERMISSION_CODE);
            }
        });
        AskPermissions.start();

        Stetho.initializeWithDefaults(this);
        //MultiDex.install(this);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //Obtain the FirebaseCrashReporting instance
        FirebaseCrash.report(new Exception("MyDay is now enabled Error Reporting."));

        //Initialize Calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/adlanta.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        //Create App Directory
        String MyDayFolder = Environment.getExternalStorageDirectory().toString();
        File AppDirectory = new File(MyDayFolder + "/MyDay");
        final String path = AppDirectory.getAbsolutePath();
        try {
            AppDirectory.mkdirs();
        } catch (SecurityException e) {
            e.printStackTrace();
        } finally {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    SharedPreferences fileDirPreferences = getSharedPreferences("com.simplicity.anuj.myday.FileDirectory", MODE_PRIVATE);
                    SharedPreferences.Editor editor = fileDirPreferences.edit();
                    editor.putString("file_dir_image", path);
                    editor.putString("file_dir_video", path);
                    editor.commit();
                    return null;
                }
            }.execute();
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mJournalAdapter = new JournalAdapter(this);
        mJournalAdapter.setClickListener(this);
        mRecyclerView.setAdapter(mJournalAdapter);
        emptyTextView = (TextView) findViewById(R.id.empty_message_text_view);
        emptyImageView = (ImageView) findViewById(R.id.empty_message_image_view);
        Typeface empty_font = Typeface.createFromAsset(getAssets(), "fonts/south_gardens.ttf");
        emptyTextView.setTypeface(empty_font);

        final View parentLayout = findViewById(R.id.coordinator_layout_activity_main);
        final Snackbar mSnackbar = Snackbar.make(parentLayout, "Entry Successfully Deleted.", Snackbar.LENGTH_SHORT);

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START | ItemTouchHelper.END | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final RecyclerView.ViewHolder tempViewHolder = viewHolder;

                //Add a Callback to Handle Deletion
                final BaseTransientBottomBar.BaseCallback<Snackbar> snackBarBaseCallback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        switch (event) {
                            case Snackbar.BaseCallback.DISMISS_EVENT_TIMEOUT: {
                                try {
                                    final int pos = (int) tempViewHolder.itemView.getTag();
                                    getContentResolver().delete(JournalContentProvider.ContentProviderCreator.JOURNAL,
                                            Utils._ID_JOURNAL + "=?",
                                            new String[]{String.valueOf(pos)});
                                    Log.e(LOG_TAG, "Entry Deleted Successfully");
                                    mJournalAdapter.notifyDataSetChanged();
                                } catch (NullPointerException e) {
                                    Log.e(LOG_TAG, e.getMessage());
                                    e.printStackTrace();
                                    Toast.makeText(mContext, "Error: Couldn't fetch the Item.", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            }
                            default:
                                Log.e(LOG_TAG, "Some Other Event Detected in Callback.");
                        }
                        super.onDismissed(transientBottomBar, event);
                    }
                };
                mSnackbar.addCallback(snackBarBaseCallback);

                //Add UNDO action
                mSnackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSnackbar.removeCallback(snackBarBaseCallback);
                        mJournalAdapter.notifyDataSetChanged();
                    }
                });
                mSnackbar.setActionTextColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
                mSnackbar.show();
            }
        };

        ItemTouchHelper mHelper = new ItemTouchHelper(callback);
        mHelper.attachToRecyclerView(mRecyclerView);

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        final Intent intent = new Intent(this, AddEntryActivity.class);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);
                if (isFirstStart) {
                    Intent i = new Intent(MainActivity.this, IntroActivity.class);
                    startActivity(i);
                    SharedPreferences.Editor editor = getPrefs.edit();
                    editor.putBoolean("firstStart", false);
                    editor.apply();
                }
            }
        });

        // Start the thread
        t.start();

//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... params) {
//                SharedPreferences getPrefs = PreferenceManager
//                        .getDefaultSharedPreferences(getBaseContext());
//                boolean isFirstStartSignedIn = getPrefs.getBoolean("USERSIGNEDIN", true);
//                if (isFirstStartSignedIn) {
//                    Intent i = new Intent(MainActivity.this, FirebaseSignInActivity.class);
//                    startActivityForResult(intent, REQUEST_AUTHENTICATION_CODE);
//                    //  Make a new preferences editor
//                    SharedPreferences.Editor editor = getPrefs.edit();
//                    editor.putBoolean("USERSIGNEDIN", false);
//                    editor.apply();
//                }
//                return null;
//            }
//        }.execute();


    }

    @Override
    public void onClick(View view, int position) {
        Intent intent = new Intent(mContext, ViewActivity.class);
        intent.putExtra("ID", (int) view.getTag());
        Bundle bundle = ActivityOptions
                .makeSceneTransitionAnimation(this)
                .toBundle();
        startActivity(intent, bundle);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        if (id == R.id.nav_calender) {
            intent = new Intent(this, CalenderActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            intent = new Intent(this, Settings.class);
            startActivity(intent);
        } else if (id == R.id.nav_person) {
            intent = new Intent(this, FirebaseSignInActivity.class);
            startActivityForResult(intent, REQUEST_AUTHENTICATION_CODE);
        } else if (id == R.id.nav_help) {
            intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            intent = new Intent(this, AboutMeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, "Request dev to send a copy of App :) :) :)");
            intent.setType("text/plain");
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Uri mDataUrl = JournalContentProvider.ContentProviderCreator.JOURNAL;
        return new CursorLoader(
                this,   // Parent activity context
                mDataUrl,        // Table to query
                null,     // Projection to return
                null,            // No selection clause
                null,            // No selection arguments
                "_id DESC"             // Default sort order
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() == 0) {
            emptyTextView.setVisibility(View.VISIBLE);
            emptyImageView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            mJournalAdapter.swapCursor(null);
        } else {
            data.moveToFirst();
            emptyTextView.setVisibility(View.GONE);
            emptyImageView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mJournalAdapter.swapCursor(data);
        }

    }

    @Override
    public void onLoaderReset(Loader loader) {
        mJournalAdapter.swapCursor(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_AUTHENTICATION_CODE) {
            if (resultCode == RESULT_OK)
                Toast.makeText(this, "Congrats! Now you can use all features of the Application", Toast.LENGTH_SHORT).show();
            else if (resultCode == RESULT_CANCELED)
                Toast.makeText(this, "You have to Sign-in if you want to use all features of the Application", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != PERMISSION_CODE) {
            Toast.makeText(this, "You have Denied Some Permissions. Please review from settings.", Toast.LENGTH_LONG).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}




