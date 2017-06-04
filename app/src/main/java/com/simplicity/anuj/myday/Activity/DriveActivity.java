//package com.simplicity.anuj.myday.Activity;
//
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.ResultCallback;
//import com.google.android.gms.drive.Drive;
//import com.google.android.gms.drive.DriveFolder;
//import com.google.android.gms.drive.DriveId;
//import com.google.android.gms.drive.MetadataChangeSet;
//import com.simplicity.anuj.myday.R;
//
//public class DriveActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<DriveFolder.DriveFolderResult> {
//
//    DriveId mDriveId;
//    GoogleApiClient mGoogleApiClient;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_drive);
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle("New folder").build();
//        Drive.DriveApi.getRootFolder(getGoogleApiClient())
//                .createFolder(getGoogleApiClient(), changeSet)
//                .setResultCallback(this);
//    }
//
//    private GoogleApiClient getGoogleApiClient() {
//        return mGoogleApiClient;
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }
//
//    @Override
//    public void onResult(@NonNull DriveFolder.DriveFolderResult result) {
//        if (!result.getStatus().isSuccess()) {
//            showMessage("Error while trying to create the folder");
//            return;
//        }
//        folderId = result.getDriveFolder().getDriveId();
//        showMessage("Created a folder: " + result.getDriveFolder().getDriveId());
//    }
//}
