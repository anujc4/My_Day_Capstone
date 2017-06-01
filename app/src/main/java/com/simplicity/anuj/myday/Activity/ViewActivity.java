package com.simplicity.anuj.myday.Activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.simplicity.anuj.myday.Adapter.ViewEntryImagesAdapter;
import com.simplicity.anuj.myday.Adapter.ViewEntryVideosAdapter;
import com.simplicity.anuj.myday.Data.JournalContentProvider;
import com.simplicity.anuj.myday.MultimediaUtility.FileCreator;
import com.simplicity.anuj.myday.R;
import com.simplicity.anuj.myday.Utility.DateFormatter;
import com.simplicity.anuj.myday.Utility.Utils;
import com.simplicity.anuj.myday.Weather.WeatherMainFragment;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ViewActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_PHOTO = 1001;
    private static final int REQUEST_TAKE_VIDEO = 2001;
    private static final int LOADER_ID_FOR_ENTRY_DETAILS = 201;
    private static final int LOADER_ID_FOR_IMAGE_DETAILS = 202;
    private static final int LOADER_ID_FOR_VIDEO_DETAILS = 203;
    static boolean textChanged = false;
    private static int _ID;
    private final String LOG_TAG = ViewActivity.class.getSimpleName();
    String mCurrentPhotoPath;
    String mCurrentVideoPath;
    private Context mContext;
    private TextView CurrentDayTextView;
    private TextView CurrentDateTextView;
    private EditText editTextAddEntry;
    private EditText editTitleAddEntry;
    private ImageButton AddImagefromCameraButton;
    private ImageButton AddVideofromCameraButton;
    private ImageButton GeotagButton;
    private ImageButton WeatherButton;
    private FrameLayout mFrameLayoutWeatherFragmentContainer;
    private FrameLayout mFrameLayoutLocationFragmentContainer;
    private FrameLayout mFrameLayoutViewWeather;
    private WeatherMainFragment mWeatherMainFragment;
    private RecyclerView mRecyclerViewImages;
    private RecyclerView mRecyclerViewVideos;
    private FrameLayout mFrameLayoutMap;
    private ViewEntryImagesAdapter mViewEntryImagesAdapter;
    private ViewEntryVideosAdapter mViewEntryVideosAdapter;
    private MapFragment mapFragment;

    private Bundle mBundleViewProperties;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = this;


        CurrentDayTextView = (TextView) findViewById(R.id.CurrentDayTextView);
        CurrentDateTextView = (TextView) findViewById(R.id.CurrentTimeTextView);
        editTextAddEntry = (EditText) findViewById(R.id.editText);
        editTitleAddEntry = (EditText) findViewById(R.id.TitleEditText);

        AddImagefromCameraButton = (ImageButton) findViewById(R.id.add_image_from_camera);
        AddVideofromCameraButton = (ImageButton) findViewById(R.id.add_video_from_camera);


        //Location Component
        mFrameLayoutLocationFragmentContainer = (FrameLayout) findViewById(R.id.frame_layout_view_location_container);
        GeotagButton = (ImageButton) findViewById(R.id.switch_geo_location_button);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment_map_location);
        mFrameLayoutMap = (FrameLayout) findViewById(R.id.frame_layout_view_location_map);


        mRecyclerViewImages = (RecyclerView) findViewById(R.id.add_entry_images_recycler_view);
        mRecyclerViewImages.setVisibility(View.GONE);
        mViewEntryImagesAdapter = new ViewEntryImagesAdapter(mContext);
        mRecyclerViewImages.setAdapter(mViewEntryImagesAdapter);
//        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3,1);
//        mRecyclerViewImages.setLayoutManager(manager);
////        LinearLayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
////        mRecyclerViewImages.setLayoutManager(manager);

        mRecyclerViewVideos = (RecyclerView) findViewById(R.id.add_entry_videos_recycler_view);
        mRecyclerViewVideos.setVisibility(View.GONE);
        mViewEntryVideosAdapter = new ViewEntryVideosAdapter(mContext);
        mRecyclerViewVideos.setAdapter(mViewEntryVideosAdapter);
        LinearLayoutManager manager1 = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerViewVideos.setLayoutManager(manager1);


        mBundleViewProperties = new Bundle();


        //Weather Component
        WeatherButton = (ImageButton) findViewById(R.id.view_weather_data);
        mFrameLayoutWeatherFragmentContainer = (FrameLayout) findViewById(R.id.frame_layout_view_weather_container);
        mFrameLayoutViewWeather = (FrameLayout) findViewById(R.id.frame_layout_view_weather_data);


        Intent intent = getIntent();
        _ID = intent.getIntExtra("ID", -1);
        if (_ID == -1)
            Log.d(LOG_TAG, "Invalid Value Returned from Intent");
        else
            Log.d(LOG_TAG, "ID returned is " + _ID);

        editTextAddEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textChanged = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        //Geotag Map Async Task
        new AsyncTask<Integer, Void, Integer>() {
            @Override
            protected Integer doInBackground(Integer... params) {
                int result = -1;
                Cursor cursor = getContentResolver().query(
                        JournalContentProvider.ContentProviderCreator.JOURNAL
                        , new String[]{Utils._ID_JOURNAL, Utils.HAS_LOCATION_JOURNAL},
                        "_id = ?",
                        new String[]{String.valueOf(_ID)},
                        null);
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getInt(1);
                    cursor.close();
                }
                return result;
            }

            @Override
            protected void onPostExecute(Integer integer) {
//                super.onPostExecute(integer);
                if (integer == -1) {
                    GeotagButton.setVisibility(View.GONE);
                } else {
                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    int height = metrics.heightPixels;
                    int width = metrics.widthPixels;
                    FrameLayout.LayoutParams mLayoutParams = new FrameLayout.LayoutParams(width * 4 / 6, height * 5 / 8, 4);
                    mLayoutParams.setMargins(width / 6, height / 8, 0, 0);
                    mFrameLayoutMap.setLayoutParams(mLayoutParams);
                    GeotagButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Cursor mCursor = getContentResolver().query(JournalContentProvider.LocationContentProviderCreator.LOCATION,
                                    null,
                                    "_id_main=?",
                                    new String[]{String.valueOf(_ID)},
                                    null);
                            if (mCursor != null && mCursor.moveToFirst()) {
                                final double latitude = mCursor.getFloat(Utils.LATITUDE_INDEX);
                                final double longitude = mCursor.getFloat(Utils.LONGITUDE_INDEX);
                                mCursor.close();
                                mFrameLayoutLocationFragmentContainer.setVisibility(VISIBLE);
                                mapFragment.getMapAsync(new OnMapReadyCallback() {
                                    @Override
                                    public void onMapReady(GoogleMap googleMap) {
                                        LatLng mLatLng = new LatLng(latitude, longitude);
                                        CameraPosition position = CameraPosition.builder()
                                                .target(mLatLng)
                                                .zoom(14)
                                                .tilt(30)
                                                .build();
                                        googleMap.addMarker(new MarkerOptions().position(mLatLng));
                                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 500, null);
                                    }
                                });
                            }
                        }
                    });

                    mFrameLayoutLocationFragmentContainer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mFrameLayoutLocationFragmentContainer.getVisibility() == VISIBLE) {
                                getFragmentManager().beginTransaction()
                                        .remove(mapFragment)
                                        .commit();
                                mFrameLayoutLocationFragmentContainer.setVisibility(GONE);
                            }
                        }
                    });
                }
            }
        }.execute(_ID);


        //Weather Functionality Async Task
        new AsyncTask<Integer, Void, Cursor>()

        {
            @Override
            protected Cursor doInBackground(Integer... params) {
                int recordedWeather = -1;
                Cursor cursor = getContentResolver().query(JournalContentProvider.ContentProviderCreator.JOURNAL,
                        null,
                        "_id=?",
                        new String[]{String.valueOf(_ID)},
                        null);
                if (cursor != null && cursor.moveToFirst()) {
                    recordedWeather = cursor.getInt(Utils.HAS_WEATHER_INDEX);
                    if (recordedWeather == -1)
                        return null;
                    else
                        cursor.close();
                }

                Cursor cursorForWeather = getContentResolver().query(JournalContentProvider.WeatherContentProviderCreator.WEATHER,
                        null,
                        "_id_main=?",
                        new String[]{String.valueOf(_ID)},
                        null);

                if (cursorForWeather != null) {
                    Log.e(LOG_TAG, cursorForWeather.toString() + "   lll  " + cursorForWeather.getCount());
                    return cursorForWeather;
                } else {
                    cursorForWeather.close();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                super.onPostExecute(cursor);

                mWeatherMainFragment = new WeatherMainFragment();
                //Set the Weather Frame Layout to center of the Screen
                DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
                int top = (int) ((displayMetrics.heightPixels / displayMetrics.density) / 10);
                int sides = (int) ((displayMetrics.widthPixels / displayMetrics.density) / 10);
                int bottom = (int) ((displayMetrics.heightPixels / displayMetrics.density) / 5);
                FrameLayout.LayoutParams tempParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                tempParams.setMargins(sides, top, sides, bottom);
                mFrameLayoutViewWeather.setLayoutParams(tempParams);

                if (cursor != null) {
                    cursor.moveToFirst();
                    final Bundle weatherBundle = new Bundle();
                    weatherBundle.putInt(Utils.WEATHER_CONDITION_ID, cursor.getInt(Utils.WEATHER_CONDITION_INDEX));
                    weatherBundle.putString(Utils.WEATHER_MAIN_WEATHER, cursor.getString(Utils.WEATHER_MAIN_INDEX));
                    weatherBundle.putString(Utils.WEATHER_DESCRIPTION_WEATHER, cursor.getString(Utils.WEATHER_DESCRIPTION_INDEX));
                    weatherBundle.putString(Utils.MAIN_TEMP_WEATHER, cursor.getString(Utils.MAIN_TEMP_INDEX));
                    weatherBundle.putString(Utils.MAIN_PRESSURE_WEATHER, cursor.getString(Utils.MAIN_PRESSURE_INDEX));
                    weatherBundle.putString(Utils.MAIN_HUMIDITY_WEATHER, cursor.getString(Utils.MAIN_HUMIDITY_INDEX));
                    weatherBundle.putString(Utils.MAIN_TEMP_MIN_WEATHER, cursor.getString(Utils.MAIN_TEMP_MIN_INDEX));
                    weatherBundle.putString(Utils.MAIN_TEMP_MAX_WEATHER, cursor.getString(Utils.MAIN_TEMP_MAX_INDEX));
                    weatherBundle.putString(Utils.CLOUDS_WEATHER, cursor.getString(Utils.CLOUDS_INDEX));
                    weatherBundle.putString(Utils.NAME_WEATHER, cursor.getString(Utils.NAME_INDEX));
                    Log.e(LOG_TAG + "hg", weatherBundle.toString());

                    WeatherButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e(LOG_TAG, "Invoked");
                            if (mFrameLayoutWeatherFragmentContainer.getVisibility() == View.GONE && !mWeatherMainFragment.isAdded()) {
                                mWeatherMainFragment.setArguments(weatherBundle);
                                mFrameLayoutWeatherFragmentContainer.setVisibility(VISIBLE);
                                getFragmentManager().beginTransaction()
                                        .add(R.id.frame_layout_view_weather_data, mWeatherMainFragment)
                                        .commit();
                            }
                        }

                    });
                    mFrameLayoutWeatherFragmentContainer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mWeatherMainFragment.isAdded() && mFrameLayoutWeatherFragmentContainer.getVisibility() == VISIBLE) {
                                mFrameLayoutWeatherFragmentContainer.setVisibility(GONE);
                                getFragmentManager().beginTransaction()
                                        .remove(mWeatherMainFragment)
                                        .commit();
                            }
                        }
                    });
                } else
                    WeatherButton.setEnabled(false);
            }
        }.execute(_ID);

        AddImagefromCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRecyclerViewImages.getVisibility() != View.VISIBLE)
                    mRecyclerViewImages.setVisibility(View.VISIBLE);
                Intent takeMediaIntent = null;
                takeMediaIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                FileCreator creator = new FileCreator(mContext);
                File photoFile = null;
                Uri photoURI = null;
                if (takeMediaIntent.resolveActivity(getPackageManager()) != null) {
                    try {
                        photoFile = creator.createImageFile();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        Toast.makeText(mContext, "Some Error Occoured.", Toast.LENGTH_SHORT).show();
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        try {
                            photoURI = FileProvider.getUriForFile(mContext, "com.anuj.simplicity.myday.fileprovider", photoFile);
                        } catch (IllegalArgumentException e) {
                            Log.e(LOG_TAG, "File Outside Path Provided Error");
                            e.printStackTrace();
                        }
                        if (photoURI != null) {
                            mCurrentPhotoPath = photoFile.getAbsolutePath();
                            takeMediaIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takeMediaIntent, REQUEST_TAKE_PHOTO);
                        }
                    }
                }
            }
        });

        AddVideofromCameraButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (mRecyclerViewVideos.getVisibility() != View.VISIBLE)
                    mRecyclerViewVideos.setVisibility(View.VISIBLE);
                FileCreator creator = new FileCreator(mContext);
                Intent takeMediaIntent = null;
                takeMediaIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                File videoFile = null;
                Uri videoURI = null;
                if (takeMediaIntent.resolveActivity(getPackageManager()) != null) {
                    videoFile = creator.createVideoFile();
                    // Continue only if the File was successfully created
                    if (videoFile != null) {
                        try {
                            videoURI = FileProvider.getUriForFile(mContext, "com.anuj.simplicity.myday.fileprovider", videoFile);
                        } catch (IllegalArgumentException e) {
                            Log.e(LOG_TAG, "File Outside Path Provided Error");
                            e.printStackTrace();
                        }
                        if (videoURI != null) {
                            mCurrentVideoPath = videoFile.getAbsolutePath();
                            takeMediaIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI);
                            startActivityForResult(takeMediaIntent, REQUEST_TAKE_VIDEO);
                        }
                    }
                }
            }
        });

        PopulateEntry mPopulateEntry = new PopulateEntry();
        PopulateImages mPopulateImages = new PopulateImages();
        PopulateVideos mPopulateVideos = new PopulateVideos();

        getSupportLoaderManager().initLoader(LOADER_ID_FOR_IMAGE_DETAILS, null, mPopulateImages);

        getSupportLoaderManager().initLoader(LOADER_ID_FOR_ENTRY_DETAILS, null, mPopulateEntry);

        getSupportLoaderManager().initLoader(LOADER_ID_FOR_VIDEO_DETAILS, null, mPopulateVideos);

        //Going to update entry into database now
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabInsertEntry);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mFrameLayoutWeatherFragmentContainer.getVisibility() == VISIBLE && mWeatherMainFragment.isAdded()) {
            Log.e(LOG_TAG, "Weather");
            mFrameLayoutWeatherFragmentContainer.setVisibility(GONE);
            getFragmentManager().beginTransaction()
                    .remove(mWeatherMainFragment)
                    .commit();
        } else if (mFrameLayoutLocationFragmentContainer.getVisibility() == VISIBLE) {
            Log.e(LOG_TAG, "Map");
            mFrameLayoutLocationFragmentContainer.setVisibility(View.GONE);
            getFragmentManager().beginTransaction()
                    .remove(mapFragment)
                    .commit();
        } else {
            if (textChanged)
                updateEntry();
            supportFinishAfterTransition();
            super.onBackPressed();
        }
    }

    private void updateEntry() {
        final String TITLE = editTitleAddEntry.getText().toString();
        final String ENTRY = editTextAddEntry.getText().toString();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                ContentValues mContentValues = new ContentValues();
                DateFormatter mDateFormatter = new DateFormatter();
                String time = mDateFormatter.getTime();
                String date = mDateFormatter.getDate();
                mContentValues.put(Utils.DATE_MODIFIED_JOURNAL, date);
                mContentValues.put(Utils.TIME_MODIFIED_JOURNAL, time);
                mContentValues.put(Utils.TITLE_JOURNAL, TITLE);
                mContentValues.put(Utils.ENTRY_JOURNAL, ENTRY);

                getContentResolver().update(JournalContentProvider.ContentProviderCreator.JOURNAL,
                        mContentValues,
                        "_id=?",
                        new String[]{String.valueOf(_ID)});
                return null;
            }
        }.execute();
        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_view_info) {
            String message =
                    null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                message = "Title: " + mBundleViewProperties.getString("title")
                        + "\n"
                        + "\nDate Created: " + mBundleViewProperties.getString("date_created")
                        + "\nTime Created: " + mBundleViewProperties.getString("time_created")
                        + "\n"
                        + "\nDate Modified: " + mBundleViewProperties.getString("date_modified")
                        + "\nTime Modified: " + mBundleViewProperties.getString("time_modified");
            }
            new AlertDialog.Builder(mContext)
                    .setTitle("Properties")
                    .setMessage(message)
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //IMAGE
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    ContentValues mMediaContentValues = new ContentValues();
                    mMediaContentValues.put(Utils._ID_MAIN_IMAGE, _ID);
                    mMediaContentValues.put(Utils.IMAGE_PATH, mCurrentPhotoPath);
                    getContentResolver().insert(JournalContentProvider.ImageContentProviderCreator.IMAGE, mMediaContentValues);
                    return null;
                }
            }.execute();
            mRecyclerViewImages.getLayoutManager().onItemsChanged(mRecyclerViewImages);
            mViewEntryImagesAdapter.notifyDataSetChanged();
            Log.e(LOG_TAG, "ADDED TO mImageArray" + mCurrentPhotoPath);
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_CANCELED) {
            File mVideoFile = new File(mCurrentPhotoPath);
            mVideoFile.delete();
        }

        //VIDEO
        if (requestCode == REQUEST_TAKE_VIDEO && resultCode == RESULT_OK) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    ContentValues mMediaContentValues = new ContentValues();
                    mMediaContentValues.put(Utils._ID_MAIN_VIDEO, _ID);
                    mMediaContentValues.put(Utils.VIDEO_PATH, mCurrentVideoPath);
                    getContentResolver().insert(JournalContentProvider.VideoContentProviderCreator.VIDEO, mMediaContentValues);
                    return null;
                }
            }.execute();
            mViewEntryVideosAdapter.notifyDataSetChanged();
            Log.e(LOG_TAG, "Added to mVideoArray" + mCurrentVideoPath);
        } else if (requestCode == REQUEST_TAKE_VIDEO && resultCode == RESULT_CANCELED) {
            File mVideoFile = new File(mCurrentVideoPath);
            mVideoFile.delete();
        }

    }

    private class PopulateEntry implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(
                    mContext,
                    JournalContentProvider.ContentProviderCreator.JOURNAL,
                    null,
                    "_id=?",
                    new String[]{String.valueOf(_ID)},
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            if (cursor != null && cursor.moveToFirst()) {
                SimpleDateFormat timeCreatedSource = new SimpleDateFormat("HH:mm:ss a");
                SimpleDateFormat timeCreatedDestination = new SimpleDateFormat("HH:mm a");
                String time_created = cursor.getString(Utils.TIME_CREATED_INDEX);
                String time_created_destination;
                Typeface title_font = Typeface.createFromAsset(getAssets(), "fonts/adlanta.ttf");
                Typeface entry_font = Typeface.createFromAsset(getAssets(), "fonts/adlanta_light.ttf");
                try {
                    Date timeCreatedDate = timeCreatedSource.parse(time_created);
                    time_created_destination = timeCreatedDestination.format(timeCreatedDate);
                    CurrentDateTextView.setText(time_created_destination);

                } catch (ParseException e) {
                    CurrentDateTextView.setText(cursor.getString(Utils.TIME_CREATED_INDEX));
                    e.printStackTrace();
                }
                editTextAddEntry.setTypeface(entry_font);
                editTitleAddEntry.setTypeface(title_font);
                CurrentDateTextView.setTypeface(title_font);
                CurrentDayTextView.setTypeface(title_font);
                editTextAddEntry.setText(cursor.getString(Utils.ENTRY_INDEX));
                editTitleAddEntry.setText(cursor.getString(Utils.TITLE_INDEX));
                CurrentDayTextView.setText(cursor.getString(Utils.DATE_CREATED_INDEX));


                //Set Data to Bundle now
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mBundleViewProperties.putInt("has_data", 1);
                    mBundleViewProperties.putString("title", cursor.getString(Utils.TITLE_INDEX));
                    mBundleViewProperties.putString("date_created", cursor.getString(Utils.DATE_CREATED_INDEX));
                    mBundleViewProperties.putString("time_created", cursor.getString(Utils.TIME_CREATED_INDEX));
                    mBundleViewProperties.putString("date_modified", cursor.getString(Utils.DATE_MODIFIED_INDEX));
                    mBundleViewProperties.putString("time_modified", cursor.getString(Utils.TIME_MODIFIED_INDEX));
                }

                cursor.close();
            } else
                Log.e(LOG_TAG, "Data Cursor Null");
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            loader.reset();

        }
    }

    private class PopulateImages implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.e(LOG_TAG, String.valueOf(_ID));
            return new CursorLoader(
                    mContext,
                    JournalContentProvider.ImageContentProviderCreator.IMAGE,
                    null,
                    "_id_main= ?",
                    new String[]{String.valueOf(_ID)},
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//            Log.e("LOG", "onLoadFinished Invoked");
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                int count = cursor.getCount();
                if (count % 3 == 0) {
                    StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                    manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
                    mRecyclerViewImages.setLayoutManager(manager);
                } else if ((count % 2 == 0)) {
                    StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                    manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
                    mRecyclerViewImages.setLayoutManager(manager);
                } else {
                    LinearLayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                    mRecyclerViewImages.setLayoutManager(manager);
                }
//                Log.e(LOG_TAG, "COUNT IMAGES " + cursor.getCount());
                mRecyclerViewImages.setVisibility(View.VISIBLE);
                mViewEntryImagesAdapter.swapCursor(cursor);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mViewEntryImagesAdapter.swapCursor(null);
            loader.reset();
        }
    }

    private class PopulateVideos implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.e(LOG_TAG, String.valueOf(_ID));
            return new CursorLoader(
                    mContext,
                    JournalContentProvider.VideoContentProviderCreator.VIDEO,
                    null,
                    "_id_main= ?",
                    new String[]{String.valueOf(_ID)},
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            if (cursor.getCount() != 0) {
//                Log.e(LOG_TAG, "COUNT VIDEOS " + cursor.getCount());
                mRecyclerViewVideos.setVisibility(View.VISIBLE);
                cursor.moveToFirst();
                mViewEntryVideosAdapter.swapCursor(cursor);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mViewEntryVideosAdapter.swapCursor(null);
            loader.reset();
        }
    }

}
