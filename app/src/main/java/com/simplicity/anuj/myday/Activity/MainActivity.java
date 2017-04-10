package com.simplicity.anuj.myday.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.simplicity.anuj.myday.Adapter.JournalAdapter;
import com.simplicity.anuj.myday.Data.JournalContentProvider;
import com.simplicity.anuj.myday.IntroActivityUtils.IntroActivity;
import com.simplicity.anuj.myday.R;
import com.simplicity.anuj.myday.Utility.ItemClickListener;
import com.simplicity.anuj.myday.Utility.Utils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor>, ActivityCompat.OnRequestPermissionsResultCallback, ItemClickListener {


    //Loader ID
    private static final int LOADER_ID = 1;
    private static final int PERMISSION_CODE = 100 ;
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    //LIST OF PERMISSIONS NEEDED FROM USER
    private final String PermissionsList[] = {
            "android.permission.CAMERA",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"
    };
    Context mContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private JournalAdapter mJournalAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setExitTransition(new Explode());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Stetho.initializeWithDefaults(this);

        mContext = this;

        //TODO Delete in Final Build
//        mContext.deleteDatabase("JournalDatabaseCreator.db");



        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mJournalAdapter = new JournalAdapter(this);
        mJournalAdapter.setClickListener(this);
        mRecyclerView.setAdapter(mJournalAdapter);

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
                int pos;
                try {
                    pos = (int) viewHolder.itemView.getTag();
                    getContentResolver().delete(JournalContentProvider.ContentProviderCreator.JOURNAL,
                            Utils._ID_JOURNAL + "=?",
                            new String[]{String.valueOf(pos)});
//                Log.d(LOG_TAG, String.valueOf(pos));
                    mJournalAdapter.notifyDataSetChanged();
                } catch (NullPointerException e) {
                    Log.e(LOG_TAG, e.getMessage());
                    e.printStackTrace();
                    Toast.makeText(mContext, "Error: Couldn't fetch the Item.", Toast.LENGTH_SHORT).show();
                }
            }
        };
        ItemTouchHelper mHelper = new ItemTouchHelper(callback);
        mHelper.attachToRecyclerView(mRecyclerView);

        Utils utils = new Utils();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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


        //TODO Setup Notifications in This Part

        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());
                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);
                //  If the activity has never started before...
                if (isFirstStart) {
                    //  Launch app intro
                    Intent i = new Intent(MainActivity.this, IntroActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(i);
                    //  Make a new preferences editor
                    SharedPreferences.Editor editor = getPrefs.edit();
                    //  Edit preference to make it false because we don't want this to run again
                    editor.putBoolean("firstStart", false);
                    //  Apply changes
                    editor.apply();
                }
            }
        });

        // Start the thread
        t.start();

        Thread AskPermissions = new Thread(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext,Manifest.permission_group.LOCATION)){
                    //If the user has denied the permission previously your code will come to this block
                    //Here you can explain why you need this permission
                    //Explain here why you need this permission
                }

                //And finally ask for the permission
                ActivityCompat.requestPermissions((Activity) mContext,PermissionsList, PERMISSION_CODE);
            }
        });
        AskPermissions.start();
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onClick(View view, int position) {
        Intent intent = new Intent(mContext, ViewActivity.class);
        intent.putExtra("ID", (int) view.getTag());
        startActivity(intent);
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
        if (id==R.id.action_settings)
        {
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

        if (id == R.id.nav_gallery) {
            // Handle the camera action
        } else if (id == R.id.nav_calender) {
            intent = new Intent(this, CalenderActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_settings) {
            intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {
            //TODO Send the Link to APP from here
        } else if (id == R.id.nav_help) {
            intent = new Intent(this,IntroActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            intent = new Intent(this, AboutMeActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Uri mDataUrl = JournalContentProvider.ContentProviderCreator.JOURNAL;
        // Returns a new CursorLoader
//        Log.e(LOG_TAG,"CALLED");
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
        data.moveToFirst();
        mJournalAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mJournalAdapter.swapCursor(null);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if(requestCode != PERMISSION_CODE) {
            //Displaying another toast if permission is not granted
            Toast.makeText(this, "You have Denied Some Permissions. Please review from settings.", Toast.LENGTH_LONG).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}




