package com.simplicity.anuj.myday.Notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.simplicity.anuj.myday.Activity.AddEntryActivity;
import com.simplicity.anuj.myday.Activity.MainActivity;
import com.simplicity.anuj.myday.R;

/**
 * Created by anujc on 15-05-2017.
 */

public class AlarmReceiver extends BroadcastReceiver {

    private static final int PENDING_INTENT_REQUEST_CODE = 901;
    private static int NOTIFICATION = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, AddEntryActivity.class);
        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_calender_black_24dp)
                .setContentTitle("My Day")
                .setContentText("It's been some time since you have made any new Entries. ")
                .setVibrate(new long[]{1000});

        //Do this to make the app go back to Main Activity when user navigates back from AddEntry Activity
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(PENDING_INTENT_REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT);
        mNotifyBuilder.setContentIntent(pendingIntent);
        notificationManager.notify(NOTIFICATION, mNotifyBuilder.build());
        NOTIFICATION++;
    }
}
