package com.simplicity.anuj.myday.LocationService;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;

/**
 * Created by anujc on 4/2/2017.
 */

public interface LocationInterface extends GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    @Override
    void onConnected(@Nullable Bundle bundle);

    @Override
    void onConnectionSuspended(int i);

    @Override
    void onConnectionFailed(@NonNull ConnectionResult connectionResult);

    @Override
    void onLocationChanged(Location location);
}
