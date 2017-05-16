package com.simplicity.anuj.myday.Notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by anujc on 14-05-2017.
 */

public class NotificationsSetup {
    private Context context;

    public NotificationsSetup(Context context) {
        this.context = context;
        BuildNotifications();
    }

    private void BuildNotifications() {
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        int hour = getPrefs.getInt("setHour", -1);
        int minutes = getPrefs.getInt("setMin", -1);
        if (hour != -1 && minutes != -1) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minutes);
            calendar.set(Calendar.SECOND, -1);
            Log.e("ALARM SET", String.valueOf(calendar.getTime()));
            Intent intent1 = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }
}
