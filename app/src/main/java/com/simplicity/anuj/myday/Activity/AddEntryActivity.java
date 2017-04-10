package com.simplicity.anuj.myday.Activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.simplicity.anuj.myday.Adapter.CameraPicsAdapter;
import com.simplicity.anuj.myday.LocationService.LocationFetcher;
import com.simplicity.anuj.myday.R;
import com.simplicity.anuj.myday.Utility.CommitDatabase;
import com.simplicity.anuj.myday.Utility.DateFormatter;
import com.simplicity.anuj.myday.Utility.Utils;
import com.simplicity.anuj.myday.Weather.GetWeatherResponse;
import com.simplicity.anuj.myday.Weather.Main;
import com.simplicity.anuj.myday.Weather.Weather;
import com.simplicity.anuj.myday.Weather.WeatherProvider;
import com.simplicity.anuj.myday.Weather.Wind;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.v4.content.FileProvider.getUriForFile;
import static android.view.View.GONE;

public class AddEntryActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1001;
    private static boolean recordLocation;
    //Hold the Attributes to be entered in the Journal Database
    final ContentValues mWeatherContentValues = new ContentValues();
    final ContentValues mLocationContentValues = new ContentValues();
    final ContentValues mJournalContentValues = new ContentValues();
    final ContentValues mMediaContentValues = new ContentValues();
    private final String LOG_TAG = AddEntryActivity.class.getCanonicalName();
    DateFormatter mDateFormatter;
    String mCurrentPhotoPath;
    TextView CurrentDayTextView;
    TextView CurrentDateTextView;
    TextView HintTextView;
    EditText editTextAddEntry;
    EditText editTitleAddEntry;
    ImageButton AddImagefromCameraButton;
    ImageButton GeotagButton;
    ImageButton AddVideoFromCameraButton;
    ImageButton WeatherButton;
    FrameLayout mFrameLayout;

    RecyclerView mRecyclerView;
    CameraPicsAdapter mCameraPicsAdapter;

    LocationFetcher mLocationFetcher;

    Context mContext;
    ArrayList<String> mImageArray;
    ArrayList<String> mVideoArray;
    String firstImage;
    boolean hasThumbImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mContext = this;
        firstImage = null;
        hasThumbImage = false;
        mImageArray = new ArrayList<>();
        mVideoArray = new ArrayList<>();
        recordLocation = true;

        mLocationFetcher = new LocationFetcher(mContext);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CurrentDayTextView = (TextView) findViewById(R.id.CurrentDayTextView);
        CurrentDateTextView = (TextView) findViewById(R.id.CurrentTimeTextView);
        HintTextView = (TextView) findViewById(R.id.HintTextView);
        editTextAddEntry = (EditText) findViewById(R.id.editText);
        editTitleAddEntry = (EditText) findViewById(R.id.TitleEditText);
        AddImagefromCameraButton = (ImageButton) findViewById(R.id.add_image_from_camera);
        AddVideoFromCameraButton = (ImageButton) findViewById(R.id.add_video_from_camera);
        WeatherButton = (ImageButton) findViewById(R.id.view_weather_data);
        GeotagButton = (ImageButton) findViewById(R.id.switch_geo_location_button);
        mFrameLayout = (FrameLayout) findViewById(R.id.frame_layout_view_location_map);
        mFrameLayout.setVisibility(GONE);
        mRecyclerView = (RecyclerView) findViewById(R.id.add_entry_images_recycler_view);
        mRecyclerView.setVisibility(GONE);
        mCameraPicsAdapter = new CameraPicsAdapter(mImageArray, mContext);
        LinearLayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mCameraPicsAdapter);


        mDateFormatter = new DateFormatter();

        Intent intent = getIntent();
        if (intent.hasExtra("path")) {
            restoreState();
        }

        AddImagefromCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView.setVisibility(View.VISIBLE);
                dispatchTakePictureIntent();
            }
        });

        AddVideoFromCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CameraActivity.class);
                startActivity(intent);
            }
        });

        WeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        GeotagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (recordLocation) {
                    recordLocation = false;
                    GeotagButton.setBackgroundResource(R.drawable.ic_geotag_disable);
                } else {
                    recordLocation = true;
                    GeotagButton.setBackgroundResource(R.drawable.ic_action_name);
                }
            }
        });

        editTextAddEntry.setHint("ENTRY");
        editTextAddEntry.setGravity(Gravity.TOP);
        editTitleAddEntry.setHint("TITLE");

        CurrentDateTextView.setText(mDateFormatter.getDate());
        CurrentDayTextView.setText(mDateFormatter.getDay());

        Call<GetWeatherResponse> call = WeatherProvider.getWeatherAPI()
                .getWeather(String.valueOf(mLocationFetcher.fetchLatitude()), String.valueOf(mLocationFetcher.fetchLongitude()), Utils.API_KEY);

        call.enqueue(new Callback<GetWeatherResponse>() {
            @Override
            public void onResponse(Call<GetWeatherResponse> call, Response<GetWeatherResponse> response) {
                List<Weather> tempWeather = response.body().getWeather();
                Main tempMain = response.body().getMain();
                Wind tempWind = response.body().getWind();

                mWeatherContentValues.put(Utils.WEATHER_MAIN_WEATHER, tempWeather.get(0).getMain());
                mWeatherContentValues.put(Utils.WEATHER_DESCRIPTION_WEATHER, tempWeather.get(0).getDescription());
                mWeatherContentValues.put(Utils.MAIN_TEMP_WEATHER, String.valueOf(tempMain.getTemp()));
                mWeatherContentValues.put(Utils.MAIN_PRESSURE_WEATHER, String.valueOf(tempMain.getPressure()));
                mWeatherContentValues.put(Utils.MAIN_HUMIDITY_WEATHER, String.valueOf(tempMain.getHumidity()));
                mWeatherContentValues.put(Utils.MAIN_TEMP_MIN_WEATHER, String.valueOf(tempMain.getTempMin()));
                mWeatherContentValues.put(Utils.MAIN_TEMP_MAX_WEATHER, String.valueOf(tempMain.getTempMax()));
                mWeatherContentValues.put(Utils.CLOUDS_WEATHER, String.valueOf(tempWind.getSpeed()));
                mWeatherContentValues.put(Utils.NAME_WEATHER, response.body().getName());
            }

            @Override
            public void onFailure(Call<GetWeatherResponse> call, Throwable t) {
                mJournalContentValues.put(Utils.HAS_WEATHER_JOURNAL, -1);
            }
        });

        //Going to add entry into database now
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabInsertEntry);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEntry();
            }
        });
    }


    private void restoreState() {
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("editTitleAddEntry", editTitleAddEntry.getText().toString());
        outState.putString("editTextAddEntry", editTextAddEntry.getText().toString());
        outState.putBoolean("recordLocation", recordLocation);
        outState.putStringArrayList("mImageArray", mImageArray);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        editTitleAddEntry.setText(savedInstanceState.getString("editTitleAddEntry"));
        editTextAddEntry.setText(savedInstanceState.getString("editTextAddEntry"));
        recordLocation = savedInstanceState.getBoolean("recordLocation");
        mImageArray = savedInstanceState.getStringArrayList("mImageArray");
    }

    @Override
    public void onBackPressed() {
        addEntry();
        super.onBackPressed();
    }

    private void addEntry() {

        //Content Values for Journal Table
        String TITLE = editTitleAddEntry.getText().toString();
        if (TITLE.isEmpty()) {
            TITLE = "Journal Entry";
        }
        mJournalContentValues.put(Utils.TITLE_JOURNAL, TITLE);

        String ENTRY = editTextAddEntry.getText().toString();
        mJournalContentValues.put(Utils.ENTRY_JOURNAL, ENTRY);

        String time = mDateFormatter.getTime();
        String date = mDateFormatter.getDate();

        mJournalContentValues.put(Utils.DATE_CREATED_JOURNAL, date);
        mJournalContentValues.put(Utils.TIME_CREATED_JOURNAL, time);
        mJournalContentValues.put(Utils.DATE_MODIFIED_JOURNAL, date);
        mJournalContentValues.put(Utils.TIME_MODIFIED_JOURNAL, time);

        //Geo-Location Details in Database Entry
        if (mLocationFetcher.GPSGenerated && recordLocation) {
            mJournalContentValues.put(Utils.HAS_LOCATION_JOURNAL, 1);
            mLocationContentValues.put(Utils.LATITUDE_LOCATION, mLocationFetcher.fetchLatitude());
            mLocationContentValues.put(Utils.LONGITUDE_LOCATION, mLocationFetcher.fetchLongitude());
        } else
            mJournalContentValues.put(Utils.HAS_LOCATION_JOURNAL, -1);

        if (hasThumbImage)
            mJournalContentValues.put(Utils.THUMB_PATH_JOURNAL, firstImage);
        else
            mJournalContentValues.put(Utils.THUMB_PATH_JOURNAL, "null");


        if (!mImageArray.isEmpty()) {
            for (String element : mImageArray)
                mMediaContentValues.put(Utils.IMAGE_PATH_MULTIMEDIA, element);
        }

        //Call the Async Task to commit to database
        new CommitDatabase(mContext).execute(mJournalContentValues, mLocationContentValues, mMediaContentValues, mWeatherContentValues);

        Toast.makeText(mContext, "Entry Added Successfully", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(mContext, MainActivity.class);
        startActivity(i);
    }


    private void dispatchTakePictureIntent() {

        Intent takeMediaIntent = null;
        takeMediaIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        // Create the File where the photo should go
        File photoFile = null;
        Uri photoURI = null;
        if (takeMediaIntent.resolveActivity(getPackageManager()) != null) {
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
                    takeMediaIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takeMediaIntent, REQUEST_TAKE_PHOTO);
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "MYDAY_" + timeStamp + "_";
        File storageDir = null;
        File image = null;
        try {
            storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
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
            if (!hasThumbImage) {
                firstImage = image.getAbsolutePath();
                Log.e(LOG_TAG, firstImage);
                hasThumbImage = true;
            }
            mCurrentPhotoPath = image.getAbsolutePath();
            return image;
        }
        return null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLocationFetcher.onStartInvoked();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocationFetcher.onStopInvoked();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationFetcher.onPauseInvoked();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocationFetcher.onResumeInvoked();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(LOG_TAG, "PERMISSIONS GRANTED");
            } else {
                Log.e(LOG_TAG, "PERMISSIONS DENIED");
                Toast.makeText(mContext, "Sorry. You must give permission to use this feature. Please try again.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG_TAG, "onActivityResult Invoked");
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            mImageArray.add(mCurrentPhotoPath);
            Log.e(LOG_TAG, "ADDED TO mImageArray" + mCurrentPhotoPath);
        }
    }
}
