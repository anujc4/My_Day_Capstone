package com.simplicity.anuj.myday.Activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
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
import com.simplicity.anuj.myday.Adapter.ViewEntryAdapter;
import com.simplicity.anuj.myday.Data.JournalContentProvider;
import com.simplicity.anuj.myday.R;
import com.simplicity.anuj.myday.Utility.DateFormatter;
import com.simplicity.anuj.myday.Utility.Utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.support.v4.content.FileProvider.getUriForFile;

public class ViewActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_PHOTO = 1001;
    private static final int LOADER_ID_FOR_ENTRY_DETAILS = 201;
    private static final int LOADER_ID_FOR_MEDIA_DETAILS = 202;
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
    RecyclerView mRecyclerView;
    FrameLayout mFrameLayout;
    ImageButton CloseButton;
    ViewEntryAdapter mViewEntryAdapter;
    boolean hasFirstImage;
    String mCurrentPhotoPath;
    String firstImage;

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
        CurrentDateTextView.setVisibility(View.GONE);
        editTextAddEntry = (EditText) findViewById(R.id.editText);
        editTitleAddEntry = (EditText) findViewById(R.id.TitleEditText);
        AddImagefromCameraButton = (ImageButton) findViewById(R.id.add_image_from_camera);
        GeotagButton = (ImageButton) findViewById(R.id.switch_geo_location_button);

        mRecyclerView = (RecyclerView) findViewById(R.id.add_entry_images_recycler_view);
        mRecyclerView.setVisibility(View.GONE);

        mFrameLayout = (FrameLayout) findViewById(R.id.frame_layout_view_location_map);
        mFrameLayout.setVisibility(View.GONE);

        CloseButton = (ImageButton) findViewById(R.id.close_map);

        mViewEntryAdapter = new ViewEntryAdapter(mContext);
        mRecyclerView.setAdapter(mViewEntryAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);

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

        GeotagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                int height = metrics.heightPixels;
                int width = metrics.widthPixels;

                FrameLayout.LayoutParams mLayoutParams = new FrameLayout.LayoutParams(width * 4 / 6, height * 5 / 8, 4);
                mLayoutParams.setMargins(width / 6, height / 8, 0, 0);
                mFrameLayout.setLayoutParams(mLayoutParams);
                mFrameLayout.setVisibility(View.VISIBLE);

                Cursor mCursor = getContentResolver().query(JournalContentProvider.LocationContentProviderCreator.LOCATION,
                        null,
                        "_id_main=?",
                        new String[]{String.valueOf(_ID)},
                        null);
                if (mCursor != null) {
                    mCursor.moveToFirst();
                    final double latitude = mCursor.getFloat(Utils.LATITUDE_INDEX);
                    final double longitude = mCursor.getFloat(Utils.LONGITUDE_INDEX);
                    Log.e(LOG_TAG, latitude + "   " + longitude);
                    mCursor.close();
                    MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment_map_location);
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            LatLng mLatLng = new LatLng(latitude, longitude);
                            CameraPosition position = CameraPosition.builder()
                                    .target(mLatLng)
                                    .zoom(14)
                                    .tilt(30)
                                    .build();
                            //                            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 500, null);
                        }
                    });
                } else
                    Toast.makeText(mContext, "No Location Recorded for this Entry", Toast.LENGTH_SHORT).show();

            }
        });

        CloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFrameLayout.setVisibility(View.GONE);
            }
        });

        AddImagefromCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRecyclerView.getVisibility() != View.VISIBLE)
                    mRecyclerView.setVisibility(View.VISIBLE);
                dispatchTakePictureIntent();
            }
        });

        PopulateEntry mPopulateEntry = new PopulateEntry();
        PopulateMedia mPopulateMedia = new PopulateMedia();
        getSupportLoaderManager().initLoader(LOADER_ID_FOR_MEDIA_DETAILS, null, mPopulateMedia);
        getSupportLoaderManager().initLoader(LOADER_ID_FOR_ENTRY_DETAILS, null, mPopulateEntry);

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
        if (textChanged)
            updateEntry();
        super.onBackPressed();

    }

    private void updateEntry() {
        ContentValues mContentValues = new ContentValues();

        DateFormatter mDateFormatter = new DateFormatter();
        String time = mDateFormatter.getTime();
        String date = mDateFormatter.getDate();
        mContentValues.put(Utils.DATE_MODIFIED_JOURNAL, date);
        mContentValues.put(Utils.TIME_MODIFIED_JOURNAL, time);

        //Find Title
        String TITLE = editTitleAddEntry.getText().toString();
        mContentValues.put(Utils.TITLE_JOURNAL, TITLE);

        //Find Entry
        String ENTRY = editTextAddEntry.getText().toString();
        mContentValues.put(Utils.ENTRY_JOURNAL, ENTRY);

        getContentResolver().update(JournalContentProvider.ContentProviderCreator.JOURNAL,
                mContentValues,
                "_id=?",
                new String[]{String.valueOf(_ID)});

        Intent i = new Intent(mContext, MainActivity.class);
        startActivity(i);
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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "MYDAY_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
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
            mViewEntryAdapter.notifyDataSetChanged();

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
                editTextAddEntry.setText(cursor.getString(Utils.ENTRY_INDEX));
                editTitleAddEntry.setText(cursor.getString(Utils.TITLE_INDEX));
                CurrentDateTextView.setText(cursor.getString(Utils.DATE_CREATED_INDEX));
                CurrentDayTextView.setText(cursor.getString(Utils.TIME_CREATED_INDEX));
                cursor.close();
            } else
                Log.e(LOG_TAG, "Data Cursor Null");
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            loader.reset();

        }
    }

    private class PopulateMedia implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.e(LOG_TAG, String.valueOf(_ID));
            return new CursorLoader(
                    mContext,
                    JournalContentProvider.MultimediaContentProviderCreator.MULTIMEDIA,
                    null,
                    "_id_main= ?",
                    new String[]{String.valueOf(_ID)},
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            Log.e("LOG", "onLoadFinished Invoked");
            if (cursor.getCount() != 0) {
                mRecyclerView.setVisibility(View.VISIBLE);
                cursor.moveToFirst();
                mViewEntryAdapter.swapCursor(cursor);
            } else {
                Log.e(LOG_TAG, "Cursor of images was returned empty");
                hasFirstImage = false;
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mViewEntryAdapter.swapCursor(null);
            loader.reset();
        }
    }
}
