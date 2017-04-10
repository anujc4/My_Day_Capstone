package com.simplicity.anuj.myday.Utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by anujc on 4/2/2017.
 */

public class DateFormatter extends SimpleDateFormat {
    private static final String LOG_TAG = DateFormatter.class.getCanonicalName();
    private Calendar mCalendar;
    private SimpleDateFormat mSimpleDateFormat;

    public DateFormatter() {
        mCalendar = Calendar.getInstance(TimeZone.getDefault());
    }

    public String getDate() {
        mSimpleDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
        return mSimpleDateFormat.format(mCalendar.getTime());
    }

    public String getTime() {
        mSimpleDateFormat = new SimpleDateFormat("HH:mm:ss a");
        return mSimpleDateFormat.format(mCalendar.getTime());
    }

    public String getDay() {
        mSimpleDateFormat = new SimpleDateFormat("EEEE");
        return mSimpleDateFormat.format(mCalendar.getTime());
    }

    public int getYear() {
        return mCalendar.get(Calendar.YEAR);
    }

    public int getMonth() {
        return mCalendar.get(Calendar.MONTH);
    }

    public int getDayOfMonth() {
        return mCalendar.get(Calendar.DAY_OF_MONTH);
    }


}
