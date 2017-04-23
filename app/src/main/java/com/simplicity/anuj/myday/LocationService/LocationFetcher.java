package com.simplicity.anuj.myday.LocationService;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.simplicity.anuj.myday.Activity.AddEntryActivity;

/**
 * Created by anujc on 4/2/2017.
 */

public class LocationFetcher implements LocationInterface, OnConnectionFailedListener, ConnectionCallbacks {
    private static final String LOG_TAG = LocationFetcher.class.getCanonicalName();
    public boolean GPSGenerated = false;
    private GoogleApiClient mGoogleApiClient;
    private Context mContext;
    private static double lt = -1;
    private static double ln = -1;

    public LocationFetcher(Context c) {
        this.mContext = c;
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addApi(LocationServices.API)
                    .addOnConnectionFailedListener(this)
                    .addConnectionCallbacks(this)
                    .build();

            if (!mGoogleApiClient.isConnected())
                mGoogleApiClient.connect();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(mContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(AddEntryActivity.class.cast(mContext),
                    new String[]{"android.permission.ACCESS_FINE_LOCATION",
                            "android.permission.ACCESS_COARSE_LOCATION"}, 1);
            return;
        }
        //This is for latest location coordinates
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        //For the time being that LocationRequest tries to get the latest location coordinates
        //set the lt and ln values to the last known location
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            lt = mLastLocation.getLatitude();
            ln = mLastLocation.getLongitude();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lt = location.getLatitude();
        ln = location.getLongitude();
        GPSGenerated = true;
        Log.d(LOG_TAG, lt + "   " + ln);
    }


    public void onStartInvoked() {
        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
    }

    public void onStopInvoked() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

    }

    public void onPauseInvoked() {
        mGoogleApiClient.disconnect();
    }

    public void onResumeInvoked() {
        mGoogleApiClient.connect();

    }

    public double fetchLatitude() {
        return lt;
    }

    public double fetchLongitude() {
        return ln;
    }
}
