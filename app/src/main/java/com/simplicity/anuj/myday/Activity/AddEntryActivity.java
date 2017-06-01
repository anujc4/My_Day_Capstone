package com.simplicity.anuj.myday.Activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.simplicity.anuj.myday.Adapter.CameraPicsAdapter;
import com.simplicity.anuj.myday.Adapter.CameraVideoAdapter;
import com.simplicity.anuj.myday.Data.JournalContentProvider;
import com.simplicity.anuj.myday.LocationService.LocationFetcher;
import com.simplicity.anuj.myday.LocationService.LocationStatusListener;
import com.simplicity.anuj.myday.MultimediaUtility.FileCreator;
import com.simplicity.anuj.myday.R;
import com.simplicity.anuj.myday.Utility.CommitDatabase;
import com.simplicity.anuj.myday.Utility.DateFormatter;
import com.simplicity.anuj.myday.Utility.IDTransfer;
import com.simplicity.anuj.myday.Utility.Utils;
import com.simplicity.anuj.myday.Weather.FetchWeatherData;
import com.simplicity.anuj.myday.Weather.GetWeatherResponse;
import com.simplicity.anuj.myday.Weather.Main;
import com.simplicity.anuj.myday.Weather.Weather;
import com.simplicity.anuj.myday.Weather.WeatherCallback;
import com.simplicity.anuj.myday.Weather.WeatherMainFragment;
import com.simplicity.anuj.myday.Weather.Wind;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class AddEntryActivity extends AppCompatActivity implements IDTransfer {

    static final int REQUEST_TAKE_PHOTO = 1001;
    static final int REQUEST_TAKE_VIDEO = 2001;
    static Response<GetWeatherResponse> response;
    static long _id_main;
    private static boolean recordLocation;
    final ContentValues mWeatherContentValues = new ContentValues();
    final ContentValues mLocationContentValues = new ContentValues();
    final ContentValues mJournalContentValues = new ContentValues();
    private final String LOG_TAG = AddEntryActivity.class.getSimpleName();
    DateFormatter mDateFormatter;
    String mCurrentPhotoPath;
    String mCurrentVideoPath;
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
    FrameLayout mFrameLayoutWeather;
    FrameLayout mFrameLayoutWeatherFragmentContainer;
    WeatherMainFragment viewWeatherData;
    RecyclerView mRecyclerViewImages;
    RecyclerView mRecyclerViewVideos;
    CameraPicsAdapter mCameraPicsAdapter;
    CameraVideoAdapter mCameraVideoAdapter;
    LocationFetcher mLocationFetcher;
    FetchWeatherData fetchWeatherData;
    Context mContext;
    ArrayList<String> mImageArray;
    ArrayList<String> mVideoArray;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mContext = this;
        intent = getIntent();
        mImageArray = new ArrayList<>();
        mVideoArray = new ArrayList<>();
        recordLocation = true;

        mLocationFetcher = new LocationFetcher(this);

        ActionBar bar = getSupportActionBar();
        if (bar != null)
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
        mFrameLayoutWeatherFragmentContainer = (FrameLayout) findViewById(R.id.frame_layout_view_weather_container);
        mRecyclerViewImages = (RecyclerView) findViewById(R.id.add_entry_images_recycler_view);
        mRecyclerViewImages.setVisibility(GONE);
        mRecyclerViewVideos = (RecyclerView) findViewById(R.id.add_entry_videos_recycler_view);
        mRecyclerViewVideos.setVisibility(GONE);

        mCameraPicsAdapter = new CameraPicsAdapter(mImageArray, mContext);
        LinearLayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerViewImages.setLayoutManager(manager);
        mRecyclerViewImages.setAdapter(mCameraPicsAdapter);

        mCameraVideoAdapter = new CameraVideoAdapter(mVideoArray, mContext);
        LinearLayoutManager manager1 = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerViewVideos.setLayoutManager(manager1);
        mRecyclerViewVideos.setAdapter(mCameraVideoAdapter);

        mFrameLayoutWeather = (FrameLayout) findViewById(R.id.frame_layout_view_weather_data);
        //Set the Frame Layout to center of the Screen
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int top = (int) ((displayMetrics.heightPixels / displayMetrics.density) / 10);
        int sides = (int) ((displayMetrics.widthPixels / displayMetrics.density) / 10);
        int bottom = (int) ((displayMetrics.heightPixels / displayMetrics.density) / 5);
        FrameLayout.LayoutParams tempParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tempParams.setMargins(sides, top, sides, bottom);
        mFrameLayoutWeather.setLayoutParams(tempParams);


        viewWeatherData = new WeatherMainFragment();

        mDateFormatter = new DateFormatter();

        Typeface title_font = Typeface.createFromAsset(getAssets(), "fonts/adlanta.ttf");
        Typeface entry_font = Typeface.createFromAsset(getAssets(), "fonts/adlanta_light.ttf");
        editTitleAddEntry.setTypeface(title_font);
        editTextAddEntry.setTypeface(entry_font);
        CurrentDayTextView.setTypeface(title_font);
        CurrentDayTextView.setTypeface(title_font);

        Log.e(LOG_TAG, String.valueOf(mLocationFetcher.fetchLatitude()));
        mLocationFetcher.setOnLocationChangedListener(new LocationStatusListener() {
            @Override
            public void onLocationReceived() {
                fetchWeatherData = new FetchWeatherData(mLocationFetcher);
                fetchWeatherData.setFetchWeatherListener(new WeatherCallback() {
                    @Override
                    public void onEventCompleted() {
                        Log.e(LOG_TAG, "Succeded to get Weather Data.");
                        response = fetchWeatherData.fetchAPI();
                        Log.e(LOG_TAG, response.toString());
                    }

                    @Override
                    public void onEventFailed() {
                        response = null;
                        Log.e(LOG_TAG, "FAILED TO GET");
                    }
                });
            }
        });

        AddImagefromCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerViewImages.setVisibility(VISIBLE);
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

        AddVideoFromCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerViewVideos.setVisibility(VISIBLE);
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


        WeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFrameLayoutWeatherFragmentContainer.getVisibility() == GONE) {
                    //If Fragment is not Visible, make it visible and Add the Fragment to View
                    final Bundle weatherBundle = new Bundle();
                    if (response != null) {
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                List<Weather> tempWeather = response.body().getWeather();
                                Main tempMain = response.body().getMain();
                                Wind tempWind = response.body().getWind();
                                weatherBundle.putInt(Utils.WEATHER_CONDITION_ID, tempWeather.get(0).getId());
                                weatherBundle.putString(Utils.WEATHER_MAIN_WEATHER, tempWeather.get(0).getMain());
                                weatherBundle.putString(Utils.WEATHER_DESCRIPTION_WEATHER, tempWeather.get(0).getDescription());
                                weatherBundle.putString(Utils.MAIN_TEMP_WEATHER, String.valueOf(tempMain.getTemp()));
                                weatherBundle.putString(Utils.MAIN_PRESSURE_WEATHER, String.valueOf(tempMain.getPressure()));
                                weatherBundle.putString(Utils.MAIN_HUMIDITY_WEATHER, String.valueOf(tempMain.getHumidity()));
                                weatherBundle.putString(Utils.MAIN_TEMP_MIN_WEATHER, String.valueOf(tempMain.getTempMin()));
                                weatherBundle.putString(Utils.MAIN_TEMP_MAX_WEATHER, String.valueOf(tempMain.getTempMax()));
                                weatherBundle.putString(Utils.CLOUDS_WEATHER, String.valueOf(tempWind.getSpeed()));
                                weatherBundle.putString(Utils.NAME_WEATHER, response.body().getName());
                                Log.e(LOG_TAG, weatherBundle.toString());
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                Log.e(LOG_TAG, "Set Visibility");
                                mFrameLayoutWeatherFragmentContainer.setVisibility(VISIBLE);
                                viewWeatherData.setArguments(weatherBundle);
                                getFragmentManager().beginTransaction()
                                        .add(R.id.frame_layout_view_weather_data, viewWeatherData)
                                        .commit();
                            }
                        }.execute();
                    } else {
                        //Show an Alert Dialog
                        new AlertDialog.Builder(mContext)
                                .setTitle("Error")
                                .setMessage("Unable to fetch Weather Data at the Moment. Please check your Internet Connection and try again.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                                .show();
                    }

                }
            }
        });

        mFrameLayoutWeatherFragmentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFrameLayoutWeatherFragmentContainer.getVisibility() == VISIBLE || viewWeatherData.isAdded()) {
                    mFrameLayoutWeatherFragmentContainer.setVisibility(GONE);
                    getFragmentManager().beginTransaction()
                            .remove(viewWeatherData)
                            .commit();
                }
            }
        });

        GeotagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recordLocation) {
                    recordLocation = false;
                    GeotagButton.setBackgroundResource(R.drawable.ic_location_disabled_white_24dp);
                } else {
                    recordLocation = true;
                    GeotagButton.setBackgroundResource(R.drawable.ic_my_location_white_24dp);
                }
            }
        });

        editTextAddEntry.setHint("ENTRY");
        editTextAddEntry.setGravity(Gravity.TOP);
        editTitleAddEntry.setHint("TITLE");

        CurrentDateTextView.setText(mDateFormatter.getDate());
        CurrentDayTextView.setText(mDateFormatter.getDay());

        //Going to add entry into database now
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabInsertEntry);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEntry();
                onBackPressed();
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("editTitleAddEntry", editTitleAddEntry.getText().toString());
        outState.putString("editTextAddEntry", editTextAddEntry.getText().toString());
        outState.putBoolean("recordLocation", recordLocation);
        outState.putStringArrayList("mImageArray", mImageArray);
        outState.putStringArrayList("mVideoArray", mVideoArray);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        editTitleAddEntry.setText(savedInstanceState.getString("editTitleAddEntry"));
        editTextAddEntry.setText(savedInstanceState.getString("editTextAddEntry"));
        recordLocation = savedInstanceState.getBoolean("recordLocation");
        mImageArray = savedInstanceState.getStringArrayList("mImageArray");
        mVideoArray = savedInstanceState.getStringArrayList("mVideoArray");
    }

    @Override
    public void onBackPressed() {
        if (mFrameLayoutWeatherFragmentContainer.getVisibility() == VISIBLE && viewWeatherData.isVisible()) {
            mFrameLayoutWeatherFragmentContainer.setVisibility(GONE);
            getFragmentManager().beginTransaction()
                    .remove(viewWeatherData)
                    .commit();
            return;
        }
        setResult(RESULT_OK, intent);
//        addEntry();
        supportFinishAfterTransition();
        super.onBackPressed();
    }

    private void addEntry() {

        intent.putExtra("ENTRY", true);
        //Content Values for Journal Table
        String TITLE = editTitleAddEntry.getText().toString();
        if (TITLE.isEmpty()) {
            TITLE = "Journal Entry";
        }
        String ENTRY = editTextAddEntry.getText().toString();
        String date = mDateFormatter.getDate();
        String time = mDateFormatter.getTime();
        String time_stamp = mDateFormatter.getTimeStamp();

        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                mJournalContentValues.put(Utils.TITLE_JOURNAL, params[0]);
                mJournalContentValues.put(Utils.ENTRY_JOURNAL, params[1]);
                mJournalContentValues.put(Utils.DATE_CREATED_JOURNAL, params[2]);
                mJournalContentValues.put(Utils.TIME_CREATED_JOURNAL, params[3]);
                mJournalContentValues.put(Utils.DATE_MODIFIED_JOURNAL, params[2]);
                mJournalContentValues.put(Utils.TIME_MODIFIED_JOURNAL, params[3]);
                mJournalContentValues.put(Utils.TIME_STAMP_JOURNAL, params[4]);

                //Geo-Location Details in Database Entry
                if (recordLocation) {
                    mJournalContentValues.put(Utils.HAS_LOCATION_JOURNAL, 1);
                    mLocationContentValues.put(Utils.LATITUDE_LOCATION, mLocationFetcher.fetchLatitude());
                    mLocationContentValues.put(Utils.LONGITUDE_LOCATION, mLocationFetcher.fetchLongitude());
                } else
                    mJournalContentValues.put(Utils.HAS_LOCATION_JOURNAL, -1);
                //Fetch Weather Data and Insert into Content Values
                if (response != null) {
                    mJournalContentValues.put(Utils.HAS_WEATHER_JOURNAL, "1");
                    List<Weather> tempWeather = response.body().getWeather();
                    Main tempMain = response.body().getMain();
                    Wind tempWind = response.body().getWind();
                    mWeatherContentValues.put(Utils.WEATHER_CONDITION_ID, tempWeather.get(0).getId());
                    mWeatherContentValues.put(Utils.WEATHER_MAIN_WEATHER, tempWeather.get(0).getMain());
                    mWeatherContentValues.put(Utils.WEATHER_DESCRIPTION_WEATHER, tempWeather.get(0).getDescription());
                    mWeatherContentValues.put(Utils.MAIN_TEMP_WEATHER, String.valueOf(tempMain.getTemp()));
                    mWeatherContentValues.put(Utils.MAIN_PRESSURE_WEATHER, String.valueOf(tempMain.getPressure()));
                    mWeatherContentValues.put(Utils.MAIN_HUMIDITY_WEATHER, String.valueOf(tempMain.getHumidity()));
                    mWeatherContentValues.put(Utils.MAIN_TEMP_MIN_WEATHER, String.valueOf(tempMain.getTempMin()));
                    mWeatherContentValues.put(Utils.MAIN_TEMP_MAX_WEATHER, String.valueOf(tempMain.getTempMax()));
                    mWeatherContentValues.put(Utils.CLOUDS_WEATHER, String.valueOf(tempWind.getSpeed()));
                    mWeatherContentValues.put(Utils.NAME_WEATHER, response.body().getName());
                    Log.d(LOG_TAG, mWeatherContentValues.toString());
                } else
                    mJournalContentValues.put(Utils.HAS_WEATHER_JOURNAL, "-1");
                return null;
            }
        }.execute(TITLE, ENTRY, date, time, time_stamp);

        //Call the Async Task to commit to database
        new CommitDatabase(mContext).execute(mJournalContentValues, mLocationContentValues, mWeatherContentValues);

        //ASYNC TASK to enter Images
        if (!mImageArray.isEmpty()) {
            String[] tempArray = mImageArray.toArray(new String[mImageArray.size()]);
            new AsyncTask<String, Void, Void>() {
                @Override
                protected Void doInBackground(String... params) {
                    ContentValues[] mImageContentValues = new ContentValues[params.length];
                    int i = 0;
                    for (String path : params) {
                        ContentValues temp = new ContentValues();
                        temp.put(Utils._ID_MAIN_IMAGE, _id_main);
                        temp.put(Utils.IMAGE_PATH, path);
                        mImageContentValues[i] = temp;
                        Log.e(LOG_TAG, "Images Values Entered Successfully");
                        i++;
                    }
                    mContext.getContentResolver().bulkInsert(JournalContentProvider.ImageContentProviderCreator.IMAGE,
                            mImageContentValues);
                    return null;
                }
            }.execute(tempArray);
        }

        //ASYNC TASK to enter Videos
        if (!mVideoArray.isEmpty()) {
            String[] tempArray = (String[]) mVideoArray.toArray(new String[mVideoArray.size()]);
            new AsyncTask<String, Void, Void>() {
                @Override
                protected Void doInBackground(String... params) {
                    ContentValues[] mVideoContentValues = new ContentValues[params.length];
                    int i = 0;
                    for (String path : params) {
                        ContentValues temp = new ContentValues();
                        temp.put(Utils._ID_MAIN_VIDEO, _id_main);
                        temp.put(Utils.VIDEO_PATH, path);
                        mVideoContentValues[i] = temp;
                        Log.e(LOG_TAG, "Video Values Entered Successfully");
                        i++;
                    }
                    mContext.getContentResolver().bulkInsert(JournalContentProvider.VideoContentProviderCreator.VIDEO,
                            mVideoContentValues);
                    return null;
                }
            }.execute(tempArray);
        }

        Toast.makeText(mContext, "Entry Added Successfully", Toast.LENGTH_SHORT).show();
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
        //IMAGE
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            mImageArray.add(mCurrentPhotoPath);
            mCameraPicsAdapter.notifyDataSetChanged();
            Log.e(LOG_TAG, "ADDED TO mImageArray" + mCurrentPhotoPath);
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_CANCELED) {
            File mVideoFile = new File(mCurrentPhotoPath);
            mVideoFile.delete();
        }

        //VIDEO
        if (requestCode == REQUEST_TAKE_VIDEO && resultCode == RESULT_OK) {
            mVideoArray.add(mCurrentVideoPath);
            mCameraVideoAdapter.notifyDataSetChanged();
            Log.e(LOG_TAG, "Added to mVideoArray" + mCurrentVideoPath);
        } else if (requestCode == REQUEST_TAKE_VIDEO && resultCode == RESULT_CANCELED) {
            File mVideoFile = new File(mCurrentVideoPath);
            mVideoFile.delete();
        }
    }

    @Override
    public void idOfEntry(long id) {
        _id_main = id;
    }
}