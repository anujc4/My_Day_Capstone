package com.simplicity.anuj.myday.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by anujc on 4/2/2017.
 */

public class DateFormatter extends SimpleDateFormat {
    private static final String LOG_TAG = DateFormatter.class.getCanonicalName();
    private Calendar mCalendar;
    private SimpleDateFormat mSimpleDateFormat;
    public static final int MONTH = 1;
    public static final int YEAR = 2;
    public static final int CASE1 = 1;
    public static final int CASE2 = 2;
    public static final int CASE3 = 3;


    public DateFormatter() {
        mCalendar = Calendar.getInstance(TimeZone.getDefault());
    }

    public String getDate() {
        mSimpleDateFormat = new SimpleDateFormat("d-MMMM-yyyy", Locale.getDefault());
        return mSimpleDateFormat.format(mCalendar.getTime());
    }

    public String getTime() {
        mSimpleDateFormat = new SimpleDateFormat("HH:mm:ss a", Locale.getDefault());
        return mSimpleDateFormat.format(mCalendar.getTime());
    }

    public String getDay() {
        mSimpleDateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        return mSimpleDateFormat.format(mCalendar.getTime());
    }

    public String getTimeStamp() {
        mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
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


    public String getParamFromDate(String date, int field) {
        SimpleDateFormat format = new SimpleDateFormat("d-MMMM-yyyy", Locale.getDefault());
        Date mDate;
        try {
            mDate = format.parse(date);
            switch (field) {
                case 1:
                    return new SimpleDateFormat("d-MMMM", Locale.getDefault()).format(mDate);
                case 2:
                    return new SimpleDateFormat("MMMM", Locale.getDefault()).format(mDate);
                case 3:
                    return new SimpleDateFormat("YYYY", Locale.getDefault()).format(mDate);
                default:
                    return null;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getMonthYearFromDate(String date, int field) {
        SimpleDateFormat format = new SimpleDateFormat("d-MMMM-yyyy", Locale.getDefault());
        Date mDate;
        try {
            Calendar calendar = Calendar.getInstance();
            mDate = format.parse(date);
            calendar.setTime(mDate);
            switch (field) {
                case 1:
                    return calendar.get(Calendar.MONTH);
                case 2:
                    return calendar.get(Calendar.YEAR);
                default:
                    return -1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }


    public int difference(String initialDate, String lastDate) {
        Calendar cal1 = new GregorianCalendar();
        Calendar cal2 = new GregorianCalendar();

        SimpleDateFormat sdf = new SimpleDateFormat("d-MMMM-yyyy", Locale.getDefault());
        Date date;

        try {
            date = sdf.parse(initialDate);
            cal1.setTime(date);
            date = sdf.parse(lastDate);
            cal2.setTime(date);
            return daysBetween(cal1.getTime(), cal2.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private int daysBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }
}
