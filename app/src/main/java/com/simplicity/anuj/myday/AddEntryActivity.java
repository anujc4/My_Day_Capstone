package com.simplicity.anuj.myday;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.simplicity.anuj.myday.Adapter.ImagesAdapter;
import com.simplicity.anuj.myday.Data.JournalContentProvider;
import com.simplicity.anuj.myday.Data.JournalContract;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class AddEntryActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final String LOG_TAG = AddEntryActivity.class.getCanonicalName();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;


    TextView CurrentDayTextView;
    TextView CurrentDateTextView;
    TextView HintTextView;
    EditText editTextAddEntry;
    EditText editTitleAddEntry;
    ImageButton AddImagefromCameraButton;
    ImageButton AddTextFromVoiceButton;
    LinearLayout linearLayout;
    ListView CameraListView;
    ImagesAdapter mImagesAdapter;

    Calendar mCalendar;
    Date mDate;
    SimpleDateFormat mSimpleDateFormat;

    Context mContext;

    String day;
    String date;

    double lt = -1;
    double ln = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = this;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CurrentDayTextView = (TextView) findViewById(R.id.CurrentDayTextView);
        CurrentDateTextView = (TextView) findViewById(R.id.CurrentTimeTextView);
        HintTextView = (TextView) findViewById(R.id.HintTextView);
        editTextAddEntry = (EditText) findViewById(R.id.editText);
        editTitleAddEntry = (EditText) findViewById(R.id.TitleEditText);
        AddImagefromCameraButton = (ImageButton) findViewById(R.id.imageButton);
        AddTextFromVoiceButton = (ImageButton) findViewById(R.id.imageButton2);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        CameraListView = (ListView) findViewById(R.id.ImagesListView);

        //Implementing the ListView for Camera Shots
        mImagesAdapter = new ImagesAdapter(mContext, R.layout.images_for_list_view);
        CameraListView.setAdapter(mImagesAdapter);

        //Initially remove the linear layout as there are no images
        linearLayout.setVisibility(View.GONE);

        //Initiating the Location API
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addOnConnectionFailedListener(this)
                    .addConnectionCallbacks(this)
                    .build();
        }


        //Call the function to capture Images
        AddImagefromCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO CLick Photo Here

                dispatchTakePictureIntent();

            }


        });

        editTextAddEntry.setHint("Enter Here...");
        editTextAddEntry.setGravity(Gravity.TOP);
        editTitleAddEntry.setHint("TITLE");

        //GET TIME AND DATE DETAILS
        mCalendar = Calendar.getInstance(TimeZone.getDefault());
        mDate = mCalendar.getTime();
        mSimpleDateFormat = new SimpleDateFormat("EEEE");
        day = mSimpleDateFormat.format(mDate);

        date = String.valueOf(mCalendar.get(Calendar.DATE))
                + "/" +
                String.valueOf(mCalendar.get(Calendar.MONTH) + 1)
                + "/" +
                String.valueOf(mCalendar.get(Calendar.YEAR));

        CurrentDateTextView.setText(date);
        CurrentDayTextView.setText(day);

        //Going to add entry into database now
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabInsertEntry);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int HOUR = mCalendar.get(Calendar.HOUR);
                int MIN = mCalendar.get(Calendar.MINUTE);
                int SEC = mCalendar.get(Calendar.SECOND);
                boolean fetchedCo_ordinates = false;
                String TITLE = editTitleAddEntry.getText().toString();
                Log.e(LOG_TAG, TITLE);
                if (TITLE.matches("")) {
                    TITLE = "Journal Entry";
                }

                if (lt != -1 && ln != -1) {
                    fetchedCo_ordinates = true;
                }

                String time = String.valueOf(HOUR) + ":" + String.valueOf(MIN) + ":" + String.valueOf(SEC);
                ContentValues mContentValues = new ContentValues();
                mContentValues.put(JournalContract.DATE_CREATED, date);
                mContentValues.put(JournalContract.TIME_CREATED, time);
                mContentValues.put(JournalContract.DATE_MODIFIED, date);
                mContentValues.put(JournalContract.TIME_MODIFIED, time);

                //Geo-Location Details in Database Entry
                if (fetchedCo_ordinates) {
                    mContentValues.put(JournalContract.LATITUDE, lt);
                    mContentValues.put(JournalContract.LONGITUDE, ln);
                }

                mContentValues.put(JournalContract.TITLE, TITLE);
                String ENTRY = editTextAddEntry.getText().toString();
                mContentValues.put(JournalContract.ENTRY, ENTRY);

                //TODO USE IMAGES URI HERE INSTEAD OF NULL
                mContentValues.put(JournalContract.IMAGE_PATH, "null");

                //Only insert if entry is not empty
                if (!ENTRY.matches("")) {
                    getContentResolver().insert(JournalContentProvider.ContentProviderCreator.JOURNAL, mContentValues);
                    Toast.makeText(mContext, "Entry Added Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "Some Error Occurred. Please Try Again.", Toast.LENGTH_LONG).show();
                }

                Intent i = new Intent(mContext, MainActivity.class);
                startActivity(i);
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.simplicity.anuj.myday.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
        @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Requesting Permissions Again
            ActivityCompat.requestPermissions(this,
                    new String[]{"android.permission.ACCESS_FINE_LOCATION",
                            "android.permission.ACCESS_COARSE_LOCATION"}, 1);
        }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        try {
            LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        catch(SecurityException e)
        {
            Log.e(LOG_TAG,"Caught Security Exception while getting last location");
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               Log.i(LOG_TAG,"PERMISSIONS GRANTED");
            }
            else
            {
                Log.e(LOG_TAG,"PERMISSIONS DENIED");
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onLocationChanged(Location location) {
        lt = location.getLatitude();
        ln=location.getLongitude();
        String s="Latitude   " +Double.toString(lt) + "\n" + "Longitude  " +Double.toString(ln);
        Log.e(LOG_TAG,"Detected Location"+s);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(LOG_TAG,"Connection to Location API Failed");
    }

}
