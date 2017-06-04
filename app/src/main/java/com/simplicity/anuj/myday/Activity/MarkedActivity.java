package com.simplicity.anuj.myday.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.simplicity.anuj.myday.Adapter.JournalAdapter;
import com.simplicity.anuj.myday.Data.JournalContentProvider;
import com.simplicity.anuj.myday.Identity.FirebaseSignInActivity;
import com.simplicity.anuj.myday.IntroActivityUtils.IntroActivity;
import com.simplicity.anuj.myday.R;
import com.simplicity.anuj.myday.Settings.Settings;
import com.simplicity.anuj.myday.Utility.ItemClickListener;
import com.simplicity.anuj.myday.Utility.Utils;

import static android.view.View.GONE;
import static com.simplicity.anuj.myday.Activity.MainActivity.REQUEST_AUTHENTICATION_CODE;

public class MarkedActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor>, ItemClickListener {

    private static final String LOG_TAG = MarkedActivity.class.getSimpleName();
    private final int LOADER_ID = 1004;
    JournalAdapter mAdapter;
    private Context mContext;
    private TextView displayNameTextView;
    private TextView emailTextView;
    private ImageView displayPictureImageView;
    private RecyclerView mMarkedRecyclerView;
    private TextView mEmptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marked);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;

        mMarkedRecyclerView = (RecyclerView) findViewById(R.id.marked_activity_recycler_view);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new JournalAdapter(this);
        mMarkedRecyclerView.setAdapter(mAdapter);
        mMarkedRecyclerView.setLayoutManager(manager);
        mAdapter.setClickListener(this);
        mEmptyTextView = (TextView) findViewById(R.id.marked_activity_text_view);

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

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri mDataUrl = JournalContentProvider.ContentProviderCreator.JOURNAL;
        return new CursorLoader(
                this,   // Parent activity context
                mDataUrl,        // Table to query
                null,     // Projection to return
                Utils.IS_MARKED + " = ?",
                new String[]{String.valueOf(1)},
                "time_stamp DESC"             // Default sort order
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() == 0) {
            mEmptyTextView.setVisibility(View.VISIBLE);
            mMarkedRecyclerView.setVisibility(GONE);
            mAdapter.swapCursor(null);
        } else {
            data.moveToFirst();
            mEmptyTextView.setVisibility(GONE);
            mMarkedRecyclerView.setVisibility(View.VISIBLE);
            mAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
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
}
