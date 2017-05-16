package com.simplicity.anuj.myday.Activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

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
import com.simplicity.anuj.myday.R;
import com.simplicity.anuj.myday.Utility.DateFormatter;
import com.simplicity.anuj.myday.Utility.Utils;
import com.simplicity.anuj.myday.Weather.WeatherMainFragment;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.support.v4.content.FileProvider.getUriForFile;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ViewActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_PHOTO = 1001;
    private static final int LOADER_ID_FOR_ENTRY_DETAILS = 201;
    private static final int LOADER_ID_FOR_IMAGE_DETAILS = 202;
    private static final int LOADER_ID_FOR_VIDEO_DETAILS = 203;
    static boolean textChanged = false;
    private static int _ID;
    private final String LOG_TAG = ViewActivity.class.getSimpleName();
    Context mContext;
    TextView CurrentDayTextView;
    TextView CurrentDateTextView;
    EditText editTextAddEntry;
    EditText editTitleAddEntry;
    ImageButton AddImagefromCameraButton;
    ImageButton GeotagButton;
    ImageButton WeatherButton;
    FrameLayout FrameLayoutAddEntry;
    FrameLayout FrameLayoutViewWeather;
    WeatherMainFragment mWeatherMainFragment;
    RecyclerView mRecyclerViewImages;
    RecyclerView mRecyclerViewVideos;
    FrameLayout mFrameLayoutMap;
    ImageButton CloseButtonMap;
    ViewEntryImagesAdapter mViewEntryImagesAdapter;
    ViewEntryVideosAdapter mViewEntryVideosAdapter;
    boolean hasFirstImage;
    String mCurrentPhotoPath;
    String firstImage;
    MapFragment mapFragment;

    PersistableBundle mBundleViewProperties;

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

//        Slide slide = new Slide(Gravity.BOTTOM);
//        slide.addTarget(R.id.editText);
//        slide.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in));
//        slide.setDuration(300);
//        getWindow().setEnterTransition(slide);

        AddImagefromCameraButton = (ImageButton) findViewById(R.id.add_image_from_camera);
        GeotagButton = (ImageButton) findViewById(R.id.switch_geo_location_button);
        WeatherButton = (ImageButton) findViewById(R.id.view_weather_data);
        FrameLayoutAddEntry = (FrameLayout) findViewById(R.id.frame_layout_add_entry);
        FrameLayoutViewWeather = (FrameLayout) findViewById(R.id.frame_layout_view_weather_data);
        FrameLayoutViewWeather.setVisibility(View.GONE);
        mWeatherMainFragment = new WeatherMainFragment();

        mRecyclerViewImages = (RecyclerView) findViewById(R.id.add_entry_images_recycler_view);
        mRecyclerViewImages.setVisibility(View.GONE);
        mViewEntryImagesAdapter = new ViewEntryImagesAdapter(mContext);
        mRecyclerViewImages.setAdapter(mViewEntryImagesAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerViewImages.setLayoutManager(manager);

        mRecyclerViewVideos = (RecyclerView) findViewById(R.id.add_entry_videos_recycler_view);
        mRecyclerViewVideos.setVisibility(View.GONE);
        mViewEntryVideosAdapter = new ViewEntryVideosAdapter(mContext);
        mRecyclerViewVideos.setAdapter(mViewEntryVideosAdapter);
        LinearLayoutManager manager1 = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerViewVideos.setLayoutManager(manager1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBundleViewProperties = new PersistableBundle();
        }


        mFrameLayoutMap = (FrameLayout) findViewById(R.id.frame_layout_view_location_map);
        mFrameLayoutMap.setVisibility(View.GONE);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment_map_location);
        CloseButtonMap = (ImageButton) findViewById(R.id.close_map);

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

        //For Geotag Button
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
                if (integer == -1) {
                    GeotagButton.setVisibility(View.GONE);
                } else {
                    GeotagButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DisplayMetrics metrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(metrics);
                            int height = metrics.heightPixels;
                            int width = metrics.widthPixels;

                            FrameLayout.LayoutParams mLayoutParams = new FrameLayout.LayoutParams(width * 4 / 6, height * 5 / 8, 4);
                            mLayoutParams.setMargins(width / 6, height / 8, 0, 0);
                            mFrameLayoutMap.setLayoutParams(mLayoutParams);
                            mFrameLayoutMap.setVisibility(View.VISIBLE);

                            Cursor mCursor = getContentResolver().query(JournalContentProvider.LocationContentProviderCreator.LOCATION,
                                    null,
                                    "_id_main=?",
                                    new String[]{String.valueOf(_ID)},
                                    null);
                            assert mCursor != null;
                            mCursor.moveToFirst();
                            final double latitude = mCursor.getFloat(Utils.LATITUDE_INDEX);
                            final double longitude = mCursor.getFloat(Utils.LONGITUDE_INDEX);
                            mCursor.close();
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
                    });
                }
                super.onPostExecute(integer);
            }
        }.execute(_ID);

        //Set the Frame Layout to center of the Screen
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int top = (int) ((displayMetrics.heightPixels / displayMetrics.density) / 10);
        int sides = (int) ((displayMetrics.widthPixels / displayMetrics.density) / 10);
        int bottom = (int) ((displayMetrics.heightPixels / displayMetrics.density) / 5);
        FrameLayout.LayoutParams tempParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tempParams.setMargins(sides, top, sides, bottom);
        FrameLayoutViewWeather.setLayoutParams(tempParams);

        new AsyncTask<Integer, Void, Cursor>() {
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

                    WeatherButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!mWeatherMainFragment.isVisible()) {
                                mWeatherMainFragment.setArguments(weatherBundle);
                                FrameLayoutViewWeather.setVisibility(View.VISIBLE);
                                getFragmentManager().beginTransaction()
                                        .add(R.id.frame_layout_view_weather_data, mWeatherMainFragment)
                                        .commit();
                            } else {
                                FrameLayoutViewWeather.setVisibility(GONE);
                                getFragmentManager().beginTransaction()
                                        .remove(mWeatherMainFragment)
                                        .commit();

                            }
                        }

                    });
                    FrameLayoutAddEntry.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mWeatherMainFragment.isVisible()) {
                                FrameLayoutViewWeather.setVisibility(GONE);
                                getFragmentManager().beginTransaction()
                                        .remove(mWeatherMainFragment)
                                        .commit();
                            }
                            if (mFrameLayoutMap.getVisibility() == VISIBLE) {
                                mFrameLayoutMap.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        }.execute(_ID);


        CloseButtonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFrameLayoutMap.setVisibility(View.GONE);
            }
        });

        AddImagefromCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRecyclerViewImages.getVisibility() != View.VISIBLE)
                    mRecyclerViewImages.setVisibility(View.VISIBLE);
                dispatchTakePictureIntent();
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
                if (textChanged)
                    updateEntry();
            }
        });
    }


    @Override
    public void onBackPressed() {
        //TODO Error resolution in this Part
        Log.e(LOG_TAG, "Invoked");
        if (textChanged) {
            updateEntry();
            super.onBackPressed();
        } else if (mWeatherMainFragment.isVisible() || mWeatherMainFragment.isAdded()) {
            Log.e(LOG_TAG, "Weather");
            FrameLayoutViewWeather.setVisibility(GONE);
            getFragmentManager().beginTransaction()
                    .remove(mWeatherMainFragment)
                    .commit();
        } else if (mapFragment.isVisible() || mapFragment.isAdded()) {
            Log.e(LOG_TAG, "Map");
            mFrameLayoutMap.setVisibility(View.GONE);
        } else {
            Log.e(LOG_TAG, "Back");
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
                if (hasFirstImage)
                    mContentValues.put(Utils.THUMB_PATH_JOURNAL, firstImage);

                getContentResolver().update(JournalContentProvider.ContentProviderCreator.JOURNAL,
                        mContentValues,
                        "_id=?",
                        new String[]{String.valueOf(_ID)});
                return null;
            }
        }.execute();

    }

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        // Create the File where the photo should go
        File photoFile = null;
        Uri photoURI = null;
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                try {
                    photoURI = getUriForFile(this, "com.anuj.simplicity.myday.fileprovider", photoFile);
                } catch (IllegalArgumentException e) {
                    Log.e(LOG_TAG, "File Outside Path Provided Error");
                    e.printStackTrace();
                }
                if (photoURI != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        }
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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "MYDAY_" + timeStamp + "_";
        File image = null;
        File storageDir = null;
        try {
            SharedPreferences preferences = getSharedPreferences("com.simplicity.anuj.myday.FileDirectory", 0);
            String defaultFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
            String path = preferences.getString("file_dir_image", defaultFolder);
            storageDir = new File(path);
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            Log.e(LOG_TAG, "Unable to create File");
            e.printStackTrace();
        } catch (SecurityException s) {
            Log.e(LOG_TAG, "Unable to create File due to missing Permissions");
            s.printStackTrace();
        }
        // Save a file: path for use with ACTION_VIEW intents
        if (image != null) {
            if (!hasFirstImage) {
                firstImage = image.getAbsolutePath();
                hasFirstImage = true;
            }
            mCurrentPhotoPath = image.getAbsolutePath();
            return image;
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            ContentValues mMediaContentValues = new ContentValues();
            mMediaContentValues.put(Utils._ID_MAIN_MULTIMEDIA, _ID);
            mMediaContentValues.put(Utils.IMAGE_PATH_MULTIMEDIA, mCurrentPhotoPath);
            getContentResolver().insert(JournalContentProvider.MultimediaContentProviderCreator.MULTIMEDIA, mMediaContentValues);
            mViewEntryImagesAdapter.notifyDataSetChanged();

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
                    JournalContentProvider.MultimediaContentProviderCreator.MULTIMEDIA,
                    new String[]{Utils._ID_JOURNAL, Utils._ID_MAIN_MULTIMEDIA, Utils.IMAGE_PATH_MULTIMEDIA},
                    "_id_main= ?",
                    new String[]{String.valueOf(_ID)},
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            Log.e("LOG", "onLoadFinished Invoked");
            if (cursor.getCount() != 0) {
                Log.e(LOG_TAG, "COUNT IMAGES " + cursor.getCount());
                mRecyclerViewImages.setVisibility(View.VISIBLE);
                cursor.moveToFirst();
                mViewEntryImagesAdapter.swapCursor(cursor);
            } else {
                Log.e(LOG_TAG, "Cursor of images was returned empty");
                hasFirstImage = false;
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
                    JournalContentProvider.MultimediaContentProviderCreator.MULTIMEDIA,
                    new String[]{Utils._ID_JOURNAL, Utils._ID_MAIN_MULTIMEDIA, Utils.VIDEO_PATH_MULTIMEDIA},
                    "_id_main= ?",
                    new String[]{String.valueOf(_ID)},
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            if (cursor.getCount() != 0) {
                Log.e(LOG_TAG, "COUNT VIDEOS " + cursor.getCount());
                mRecyclerViewImages.setVisibility(View.VISIBLE);
                cursor.moveToFirst();
                mViewEntryVideosAdapter.swapCursor(cursor);
            } else {
                Log.e(LOG_TAG, "Cursor of videos was returned empty");
                hasFirstImage = false;
            }

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mViewEntryVideosAdapter.swapCursor(null);
            loader.reset();
        }
    }
}
