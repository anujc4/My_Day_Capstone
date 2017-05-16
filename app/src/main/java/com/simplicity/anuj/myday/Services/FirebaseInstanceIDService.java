//package com.simplicity.anuj.myday.Services;
//
//import android.util.Log;
//
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.FirebaseInstanceIdService;
//
///**
// * Created by anujc on 14-05-2017.
// */
//
//public class FirebaseInstanceIDService extends FirebaseInstanceIdService {
//    private static final String TAG = FirebaseInstanceIDService.class.getSimpleName();
//
//    @Override
//    public void onTokenRefresh() {
//        super.onTokenRefresh();
//        // Get updated InstanceID token.
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        Log.d(TAG, "Refreshed token: " + refreshedToken);
//
//        // If you want to send messages to this application instance or
//        // manage this apps subscriptions on the server side, send the
//        // Instance ID token to your app server.
//        sendRegistrationToServer(refreshedToken);
//    }
//
//    private void sendRegistrationToServer(String token) {
//        //TODO Send Token to Server
//    }
//}
