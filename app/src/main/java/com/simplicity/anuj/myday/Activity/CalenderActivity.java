package com.simplicity.anuj.myday.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.simplicity.anuj.myday.Adapter.CalenderViewSearchEntryAdapter;
import com.simplicity.anuj.myday.Data.JournalContentProvider;
import com.simplicity.anuj.myday.Data.JournalContract;
import com.simplicity.anuj.myday.Identity.FirebaseSignInActivity;
import com.simplicity.anuj.myday.IntroActivityUtils.IntroActivity;
import com.simplicity.anuj.myday.R;
import com.simplicity.anuj.myday.Settings.Settings;
import com.simplicity.anuj.myday.Utility.DateFormatter;
import com.simplicity.anuj.myday.Utility.ItemClickListener;

import java.util.Date;

import static com.simplicity.anuj.myday.Activity.MainActivity.PERMISSION_CODE;
import static com.simplicity.anuj.myday.Activity.MainActivity.REQUEST_AUTHENTICATION_CODE;

public class CalenderActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor>, CalendarView.OnDateChangeListener, ItemClickListener {

    private final String LOG_TAG = CalenderActivity.class.getCanonicalName();
    private final int LoaderID = 1001;
    CalendarView mCalendarView;
    RecyclerView mRecyclerViewCalender;
    CalenderViewSearchEntryAdapter mEntryAdapter;
    Context mContext;
    DateFormatter formatter;
    Toolbar toolbar;
    private TextView displayNameTextView;
    private TextView emailTextView;
    private ImageView displayPictureImageView;
    private TextView mTodayTextView;
    private TextView mScheduleTextView;
    private TextView what_sthisTextView;
    private TextView nothingFoundTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mContext = this;
        formatter = new DateFormatter();
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        mCalendarView.setMaxDate(new Date().getTime());
        mTodayTextView = (TextView) findViewById(R.id.today_text_view);
        mScheduleTextView = (TextView) findViewById(R.id.schedule_text_view);
        what_sthisTextView = (TextView) findViewById(R.id.whats_this_text_view);
        mRecyclerViewCalender = (RecyclerView) findViewById(R.id.calender_view_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerViewCalender.setLayoutManager(manager);
        mEntryAdapter = new CalenderViewSearchEntryAdapter(this);
        mEntryAdapter.setClickListener(this);
        mRecyclerViewCalender.setAdapter(mEntryAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerViewCalender.getContext(), manager.getOrientation());
        mRecyclerViewCalender.addItemDecoration(dividerItemDecoration);

        nothingFoundTextView = (TextView) findViewById(R.id.calender_view_nothing_found);
        Typeface font_south_gardens = Typeface.createFromAsset(getAssets(), "fonts/south_gardens.ttf");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_calender);
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

        what_sthisTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setTitle("Calender Search")
                        .setMessage("My Day lets you search for an entry by date. Just choose a date from the Calender to find your entry.")
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });

        int i = formatter.getYear();
        int i1 = formatter.getMonth();
        int i2 = formatter.getDayOfMonth();
        String date = String.valueOf(i2) + "-" + formatter.getMonth(i1) + "-" + String.valueOf(i);
        Bundle b = new Bundle();
        b.putString("date", date);
        mTodayTextView.setText(getString(R.string.today) + "  " + String.valueOf(i2) + " " + formatter.getMonth(i1));
        getSupportLoaderManager().initLoader(LoaderID, b, this);
        mCalendarView.setOnDateChangeListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_calender);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AddEntryActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String date;
        if (args != null) {
            date = args.getString("date");
            return new CursorLoader(
                    this,
                    JournalContentProvider.ContentProviderCreator.JOURNAL,
                    null,
                    JournalContract.DATE_CREATED + "=?",
                    new String[]{date},
                    "_id DESC"
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        nothingFoundTextView.setVisibility(View.VISIBLE);
        if (data != null) {
            mEntryAdapter.swapCursor(data);
            mScheduleTextView.setText(getString(R.string.found) + " " + data.getCount() + " " + getString(R.string.entries));
            Log.e(LOG_TAG, "Entries Found " + data.getCount());
            if (data.getCount() > 0)
                nothingFoundTextView.setVisibility(View.GONE);
        } else {
            Log.e(LOG_TAG, "Cursor was Returned empty");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mEntryAdapter.swapCursor(null);
    }


    @Override
    protected void onStart() {
        super.onStart();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_calender);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_calender);
        if (id == R.id.nav_settings) {
            intent = new Intent(this, Settings.class);
            startActivity(intent);
        } else if (id == R.id.nav_marked) {
            intent = new Intent(this, MarkedActivity.class);
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
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != PERMISSION_CODE) {
            Toast.makeText(this, "You have Denied Some Permissions. Please review from settings.", Toast.LENGTH_LONG).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
        String date = String.valueOf(dayOfMonth) + "-" + formatter.getMonth(month) + "-" + String.valueOf(year);
        mTodayTextView.setText(getString(R.string.today) + "  " + String.valueOf(dayOfMonth) + " " + formatter.getMonth(month));
        toolbar.setTitle(formatter.getMonth(month) + " " + String.valueOf(year));
        Log.e(LOG_TAG, date);
        Bundle b = new Bundle();
        b.putString("date", date);
        getSupportLoaderManager().restartLoader(LoaderID, b, this);
    }

    @Override
    public void onClick(View view, int position) {
        Intent intent = new Intent(mContext, ViewActivity.class);
        intent.putExtra("ID", (int) view.getTag());
        startActivity(intent);
    }
}
