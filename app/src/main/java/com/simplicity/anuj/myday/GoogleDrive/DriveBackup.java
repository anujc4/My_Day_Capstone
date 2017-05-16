package com.simplicity.anuj.myday.GoogleDrive;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

/**
 * Created by anujc on 14-05-2017.
 */

public class DriveBackup implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClientDrive;
    Context context;

    public DriveBackup(Context context) {
        this.context = context;
        setUpDrive();
    }

    private void setUpDrive() {
        mGoogleApiClientDrive = new GoogleApiClient.Builder(context)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    void onStartInvoked() {
        mGoogleApiClientDrive.connect();
    }

    ;

    void onStopInvoked() {
        if (mGoogleApiClientDrive != null)
            mGoogleApiClientDrive.disconnect();
    }
}
