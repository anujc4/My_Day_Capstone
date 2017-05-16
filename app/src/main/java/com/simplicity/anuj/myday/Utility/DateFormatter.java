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
        mSimpleDateFormat = new SimpleDateFormat("d-MMMM-yyyy");
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

    public String getMonth(int mon) {
        switch (mon) {
            case 0:
                return "January";
            case 1:
                return "February";
            case 2:
                return "March";
            case 3:
                return "April";
            case 4:
                return "May";
            case 5:
                return "June";
            case 6:
                return "July";
            case 7:
                return "August";
            case 8:
                return "September";
            case 9:
                return "October";
            case 10:
                return "November";
            case 11:
                return "December";
            default:
                return null;
        }
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
