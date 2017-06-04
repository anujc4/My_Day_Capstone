package com.simplicity.anuj.myday.Activity;

import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.db.chart.Tools;
import com.db.chart.animation.Animation;
import com.db.chart.model.LineSet;
import com.db.chart.renderer.AxisRenderer;
import com.db.chart.tooltip.Tooltip;
import com.db.chart.view.LineChartView;
import com.facebook.stetho.Stetho;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.simplicity.anuj.myday.Adapter.JournalAdapter;
import com.simplicity.anuj.myday.Chart.ChartCreator;
import com.simplicity.anuj.myday.Chart.OnChartDataLoadFinishedListener;
import com.simplicity.anuj.myday.Data.JournalContentProvider;
import com.simplicity.anuj.myday.Identity.FirebaseSignInActivity;
import com.simplicity.anuj.myday.IntroActivityUtils.IntroActivity;
import com.simplicity.anuj.myday.R;
import com.simplicity.anuj.myday.Settings.Settings;
import com.simplicity.anuj.myday.Utility.ItemClickListener;
import com.simplicity.anuj.myday.Utility.Utils;

import java.io.File;
import java.util.Arrays;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor>, ActivityCompat.OnRequestPermissionsResultCallback, ItemClickListener, OnChartDataLoadFinishedListener {

    static final int PERMISSION_CODE = 100;
    static final int REQUEST_AUTHENTICATION_CODE = 200;
    private static final int LOADER_ID = 1;
    private static final int ENTRY_MADE = 201;
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String PermissionsList[] = {
            "android.permission.CAMERA",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"
    };
    String[] labels;
    float[] values;
    private FirebaseAnalytics mFirebaseAnalytics;
    private TextView placeholderTextView;
    private Context mContext;
    private TextView emptyTextView;
    private ImageView emptyImageView;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private JournalAdapter mJournalAdapter;
    private FloatingActionButton fab;
    private LineChartView lineChartView;
    private ImageView mCollapsingToobarScrimImageView;
    private ChartCreator creator;
    private TextView displayNameTextView;
    private TextView emailTextView;
    private ImageView displayPictureImageView;
    private ContentValues deletedRowJournal;
    private ContentValues deletedRowLocation;
    private ContentValues deletedRowWeather;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_activity_main);
        setSupportActionBar(toolbar);

        mContext = this;

        //Initialize Calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/adlanta.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        //Ask Permissions
        ActivityCompat.requestPermissions(this, PermissionsList, PERMISSION_CODE);

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
        t.start();

        Stetho.initializeWithDefaults(this);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //Obtain the FirebaseCrashReporting instance
        FirebaseCrash.report(new Exception("MyDay is now enabled Error Reporting."));

        fab = (FloatingActionButton) findViewById(R.id.fab);
        lineChartView = (LineChartView) findViewById(R.id.line_chart);
        mCollapsingToobarScrimImageView = (ImageView) findViewById(R.id.collapsing_toolbar_scrim);
        placeholderTextView = (TextView) findViewById(R.id.time_line_placeholder);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mJournalAdapter = new JournalAdapter(this);
        mJournalAdapter.setClickListener(this);
        mRecyclerView.setAdapter(mJournalAdapter);
        emptyTextView = (TextView) findViewById(R.id.empty_message_text_view);
        emptyImageView = (ImageView) findViewById(R.id.empty_message_image_view);
        Typeface empty_font = Typeface.createFromAsset(getAssets(), "fonts/south_gardens.ttf");
        emptyTextView.setTypeface(empty_font);

        deletedRowJournal = new ContentValues();
        deletedRowLocation = new ContentValues();
        deletedRowWeather = new ContentValues();


        final View parentLayout = findViewById(R.id.coordinator_layout_activity_main);
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START | ItemTouchHelper.END | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final Snackbar mSnackbar = Snackbar.make(parentLayout, "Entry Successfully Deleted.", Snackbar.LENGTH_LONG);
                int _ID = (int) viewHolder.itemView.getTag(R.id._ID);

                new AsyncTask<Integer, Void, Integer>() {
                    @Override
                    protected Integer doInBackground(Integer... params) {
                        Log.e(LOG_TAG, String.valueOf(params[0]));
                        Cursor c_journal = getContentResolver().query(JournalContentProvider.ContentProviderCreator.JOURNAL
                                , null
                                , "_id=?"
                                , new String[]{String.valueOf(params[0])}
                                , null);
                        Cursor c_location = getContentResolver().query(JournalContentProvider.LocationContentProviderCreator.LOCATION
                                , null
                                , "_id_main=?"
                                , new String[]{String.valueOf(params[0])}
                                , null);
                        Cursor c_weather = getContentResolver().query(JournalContentProvider.WeatherContentProviderCreator.WEATHER
                                , null
                                , "_id_main=?"
                                , new String[]{String.valueOf(params[0])}
                                , null);

                        if (c_journal != null && c_location != null && c_weather != null
                                && c_journal.moveToFirst() && c_location.moveToFirst() && c_weather.moveToFirst()) {
                            DatabaseUtils.cursorRowToContentValues(c_journal, deletedRowJournal);
                            DatabaseUtils.cursorRowToContentValues(c_location, deletedRowLocation);
                            DatabaseUtils.cursorRowToContentValues(c_weather, deletedRowWeather);

                            int row_deleted_journal = getContentResolver().delete(JournalContentProvider.ContentProviderCreator.JOURNAL,
                                    Utils._ID_JOURNAL + "=?",
                                    new String[]{String.valueOf(params[0])});
                            int row_deleted_location = getContentResolver().delete(JournalContentProvider.LocationContentProviderCreator.LOCATION,
                                    Utils._ID_MAIN_LOCATION + "=?",
                                    new String[]{String.valueOf(params[0])});
                            int row_deleted_weather = getContentResolver().delete(JournalContentProvider.WeatherContentProviderCreator.WEATHER,
                                    Utils._ID_MAIN_WEATHER + "=?",
                                    new String[]{String.valueOf(params[0])});
                            Log.e(LOG_TAG, row_deleted_journal + " entries Deleted Successfully");
                            return params[0];
                        } else Log.e(LOG_TAG, "ERROR in deleting from Database.");
                        return -1;
                    }

                    @Override
                    protected void onPostExecute(Integer pos) {
                        super.onPostExecute(pos);
                        if (pos != -1) {
                            mJournalAdapter.notifyDataSetChanged();
                        }
                    }
                }.execute(_ID);


                //Add UNDO action
                mSnackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e(LOG_TAG, "Inserting Back");
                        if (deletedRowJournal != null && deletedRowLocation != null && deletedRowWeather != null) {
                            new AsyncTask<ContentValues, Void, Integer>() {
                                @Override
                                protected Integer doInBackground(ContentValues... params) {
                                    getContentResolver().insert(JournalContentProvider.ContentProviderCreator.JOURNAL, params[0]);
                                    getContentResolver().insert(JournalContentProvider.LocationContentProviderCreator.LOCATION, params[1]);
                                    getContentResolver().insert(JournalContentProvider.WeatherContentProviderCreator.WEATHER, params[2]);
                                    return 1;
                                }

                                @Override
                                protected void onPostExecute(Integer pos) {
                                    super.onPostExecute(pos);
                                    mJournalAdapter.notifyDataSetChanged();
                                }
                            }.execute(deletedRowJournal, deletedRowLocation, deletedRowWeather);
                        }

                    }
                });
                mSnackbar.setActionTextColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
                mSnackbar.show();
            }
        };

        ItemTouchHelper mHelper = new ItemTouchHelper(callback);
        mHelper.attachToRecyclerView(mRecyclerView);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setRemoveDuration(200);
        mRecyclerView.setItemAnimator(itemAnimator);

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AddEntryActivity.class);
//                startActivityForResult(intent, ENTRY_MADE);
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
        View header = navigationView.getHeaderView(0);
        displayNameTextView = (TextView) header.findViewById(R.id.nav_header_main_text_view);
        emailTextView = (TextView) header.findViewById(R.id.nav_header_sub_text_view);
        displayPictureImageView = (ImageView) header.findViewById(R.id.nav_header_display_picture);

        creator = new ChartCreator(mContext, this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
        SharedPreferences preferences = getSharedPreferences("com.simplicity.anuj.myday.Identity", MODE_PRIVATE);
        if (preferences.getBoolean("userSignedIn", false)) {
            String photoUrl = preferences.getString("photoUrl", null);
            if (photoUrl != null) {
                Glide.with(mContext)
                        .load(photoUrl)
                        .error(R.mipmap.ic_launcher)
                        .into(displayPictureImageView);
            }

            String name = preferences.getString("displayName", null);
            if (name != null)
                displayNameTextView.setText(name);

            String email = preferences.getString("email", null);
            if (email != null)
                emailTextView.setText(email);
        } else {
            Log.e(LOG_TAG, "User has Signed Out");
        }
    }

    @Override
    public void onClick(View view, int position) {
        Intent intent = new Intent(mContext, ViewActivity.class);
        intent.putExtra("ID", (int) view.getTag(R.id._ID));

        String transitionName = getString(R.string.transition_string);
        View viewStart = findViewById(R.id.card_view);
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        viewStart,   // Starting view
                        transitionName    // The String
                );
        startActivity(intent, options.toBundle());
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
        if (id == R.id.nav_marked) {
            intent = new Intent(this, MarkedActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_calender) {
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
            intent.putExtra(Intent.EXTRA_TEXT,
                    "http://play.google.com/store/apps/details?id=com.simplicity.anuj.myday");
            intent.setType("text/plain");
            startActivity(intent);
        } else if (id == R.id.nav_review) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=com.simplicity.anuj.myday"));
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
                "time_stamp DESC"             // Default sort order
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() == 0) {
            emptyTextView.setVisibility(View.VISIBLE);
            emptyImageView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(GONE);
            mJournalAdapter.swapCursor(null);
        } else {
            data.moveToFirst();
            emptyTextView.setVisibility(GONE);
            emptyImageView.setVisibility(GONE);
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
        switch (requestCode) {
            case REQUEST_AUTHENTICATION_CODE:
                if (resultCode == RESULT_OK)
                    Toast.makeText(this, "Awesome! Now you can use all features of the My Day", Toast.LENGTH_SHORT).show();
                else if (resultCode == RESULT_CANCELED)
                    Toast.makeText(this, "You have to Sign-in if you want to use all features of the Application", Toast.LENGTH_LONG).show();
                break;

            case ENTRY_MADE:
                if (resultCode == RESULT_OK)
                    getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            String MyDayFolder = Environment.getExternalStorageDirectory().toString();
            File AppDirectory = new File(MyDayFolder + "/MyDay");
            if (!AppDirectory.exists() && !AppDirectory.isDirectory()) {
                final String path = AppDirectory.getAbsolutePath();
                try {
                    boolean success = AppDirectory.mkdirs();
                    if (!success)
                        Log.e(LOG_TAG, "Failed to create folder.");
                    else {
                        Log.e(LOG_TAG, "Created folder");
                        SharedPreferences fileDirPreferences = getSharedPreferences("com.simplicity.anuj.myday.FileDirectory", MODE_PRIVATE);
                        SharedPreferences.Editor editor = fileDirPreferences.edit();
                        editor.putString("file_dir_image", path);
                        editor.putString("file_dir_video", path);
                        editor.apply();
                    }
                } catch (SecurityException e) {
                    Log.e(LOG_TAG, "Permission not granted yet..");
                    ActivityCompat.requestPermissions((Activity) mContext, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"}, PERMISSION_CODE);
                    e.printStackTrace();
                }
            } else Log.e(LOG_TAG, "Already Exists");
        }
    }

    //Chart Data is ready
    @Override
    public void onCompleted() {
        labels = creator.getLabels();
        values = creator.getValues();

        Log.e(LOG_TAG, "LABELS : " + Arrays.toString(labels) + "\nVALUES : " + Arrays.toString(values));

        if (labels != null && values != null && labels.length == values.length) {
            Tooltip mTip = new Tooltip(mContext, R.layout.linechart_three_tooltip, R.id.value);
            mTip.setVerticalAlignment(Tooltip.Alignment.BOTTOM_TOP);
            mTip.setDimensions((int) Tools.fromDpToPx(58), (int) Tools.fromDpToPx(25));
            mTip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f),
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1f)).setDuration(200);
            mTip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 0),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f),
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 0f)).setDuration(200);
            mTip.setPivotX(Tools.fromDpToPx(65) / 2);
            mTip.setPivotY(Tools.fromDpToPx(25));
            lineChartView.setTooltips(mTip);
            LineSet dataset = new LineSet(labels, values);
            dataset.setColor(Color.parseColor("#FF4081"))
                    .setFill(Color.parseColor("#E1BEE7"))
                    .setDotsColor(Color.parseColor("#FF4081"))
                    .setThickness(4)
                    .setDashed(new float[]{10f, 10f});
            lineChartView.addData(dataset);
            lineChartView.setBorderSpacing(Tools.fromDpToPx(20))
                    .setAxisBorderValues(0, 20, 1)
                    .setYLabels(AxisRenderer.LabelPosition.NONE)
                    .setLabelsColor(Color.parseColor("#FFFFFF"))
                    .setFontSize(35)
                    .setTypeface(Typeface.createFromAsset(getAssets(), "fonts/adlanta_light.ttf"))
                    .setXAxis(false)
                    .setYAxis(false);
            lineChartView.setVisibility(View.VISIBLE);
            placeholderTextView.setVisibility(View.VISIBLE);
            mCollapsingToobarScrimImageView.setVisibility(View.INVISIBLE);
            Animation anim = new Animation().setEasing(new BounceInterpolator());
            lineChartView.show(anim);
        }
    }

    @Override
    public void noData() {
        lineChartView.setVisibility(View.INVISIBLE);
        placeholderTextView.setVisibility(View.INVISIBLE);
        mCollapsingToobarScrimImageView.setVisibility(View.VISIBLE);
    }
}




