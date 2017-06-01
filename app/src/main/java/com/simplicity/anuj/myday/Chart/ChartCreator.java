package com.simplicity.anuj.myday.Chart;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.simplicity.anuj.myday.Data.JournalContentProvider;
import com.simplicity.anuj.myday.Utility.DateFormatter;
import com.simplicity.anuj.myday.Utility.Utils;

import java.util.ArrayList;
import java.util.Arrays;

import static com.simplicity.anuj.myday.Utility.DateFormatter.MONTH;
import static com.simplicity.anuj.myday.Utility.DateFormatter.YEAR;
import static com.simplicity.anuj.myday.Utility.DateFormatter.CASE1;
import static com.simplicity.anuj.myday.Utility.DateFormatter.CASE2;
import static com.simplicity.anuj.myday.Utility.DateFormatter.CASE3;

/**
 * Created by anujc on 27-05-2017.
 */

public class ChartCreator {
    private static final String LOG = ChartCreator.class.getSimpleName();
    private Context context;
    private ArrayList<Float> mValues;
    private ArrayList<String> mLabels;

    public float[] getValues() {
        return values;
    }

    public void setValues(float[] values) {
        this.values = values;
    }

    public String[] getLabels() {
        return labels;
    }

    public void setLabels(String[] labels) {
        this.labels = labels;
    }

    private float[] values;
    private String[] labels;
    private DateFormatter formatter;
    private OnChartDataLoadFinishedListener listener;

    public ChartCreator(Context context, OnChartDataLoadFinishedListener listener) {
        this.context = context;
        mValues = new ArrayList<>();
        mLabels = new ArrayList<>();
        this.listener = listener;
        getCursor();
    }

    private void getCursor() {
        formatter = new DateFormatter();
        new AsyncTask<Void, Void, DATA>() {

            @Override
            protected DATA doInBackground(Void... params) {
                //Get Cursor
                SharedPreferences preferences = context.getSharedPreferences("com.simplicity.anuj.myday.FileDirectory", Context.MODE_PRIVATE);
                String dbPath = preferences.getString("database_path", "NA");
                if (dbPath.equals("NA")) {
                    Log.e(LOG, "CANNOT FIND DATABASE");
                    return null;
                }
                try (SQLiteDatabase database = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY)) {

                    Cursor c = context.getContentResolver().query(
                            JournalContentProvider.ContentProviderCreator.JOURNAL,
                            new String[]{Utils._ID_JOURNAL, Utils.DATE_CREATED_JOURNAL},
                            null,
                            null,
                            "_id ASC"
                    );
                    if (c != null && c.getCount() < 10) {
                        c.close();
                        Log.e(LOG, "Database has less than 10 entries");
                        return null;
                    }


                    String query = "SELECT _id , date_created AS date , COUNT(date_created) AS count FROM journal GROUP BY date_created ORDER BY _id ASC";
                    Cursor cursor;
                    cursor = database.rawQuery(query, null);
//                Log.e(LOG, DatabaseUtils.dumpCursorToString(cursor));

                    //Find the number of days between the first and last date the entries were made
                    if (cursor != null && cursor.moveToFirst()) {
                        String initialDate = cursor.getString(1);
                        cursor.moveToLast();
                        String lastDate = cursor.getString(1);
                        int difference = formatter.difference(initialDate, lastDate);
                        cursor.moveToFirst();
                        Log.e(LOG, String.valueOf(difference));


//                  Check the 5 Time Frames
//                  1. 1 Month : All Days
//                  2. 1 Year : All Months
//                  3. More than 1 Year : Years

                        if (difference > 6 && difference <= 30) {
                            //Case 1: 1 Month
                            Log.e(LOG, "CASE 1 : Difference = " + difference);
                            do {
                                String day_month = formatter.getParamFromDate(cursor.getString(1), CASE1);
                                Log.e(LOG + " DAY", day_month);
                                Log.e(LOG + " VALUE", String.valueOf(cursor.getFloat(2)));
                                mLabels.add(day_month);
                                mValues.add(cursor.getFloat(2));
                            } while (cursor.moveToNext());

                        } else if (difference > 30 && difference < 365) {
                            //Case 2: 1 Year
                            Log.e(LOG, "CASE 2 : " + difference);
                            do {
                                int index;
                                String date1 = cursor.getString(1);
                                float count1 = cursor.getFloat(2);
                                //Check if the cursor can move to next position and compare the values
                                while (cursor.moveToNext()) {
                                    index = cursor.getPosition();
                                    String date2 = cursor.getString(1);
                                    float count2 = cursor.getFloat(2);
                                    if (formatter.getMonthYearFromDate(date1, MONTH) == formatter.getMonthYearFromDate(date2, MONTH)) {
                                        //Month was a Match. Increase the count
                                        count1 = count1 + count2;
                                    } else {
                                        //Month was different. Set the values to ArrayList
                                        mLabels.add(formatter.getParamFromDate(date1, CASE2).substring(0, 4));
                                        mValues.add(count1);
                                        cursor.moveToPosition(index);
                                        break;
                                    }
                                }
                            } while (cursor.moveToNext());
                        } else if (difference >= 365) {
                            //Case 3: More than a year
                            Log.e(LOG, "CASE 2 : " + difference);
                            do {
                                int index;
                                String date1 = cursor.getString(1);
                                float count1 = cursor.getFloat(2);
                                //Check if the cursor can move to next position and compare the values
                                while (cursor.moveToNext()) {
                                    index = cursor.getPosition();
                                    String date2 = cursor.getString(1);
                                    float count2 = cursor.getFloat(2);
                                    if (formatter.getMonthYearFromDate(date1, YEAR) == formatter.getMonthYearFromDate(date2, YEAR)) {
                                        //Month was a Match. Increase the count
                                        count1 = count1 + count2;
                                    } else {
                                        //Month was different. Set the values to ArrayList
                                        mLabels.add(formatter.getParamFromDate(date1, CASE3));
                                        mValues.add(count1);
                                        cursor.moveToPosition(index);
                                        break;
                                    }
                                }
                            } while (cursor.moveToNext());
                        } else {
                            //Default case
                            Log.e(LOG + " ERROR", "Default Case");
                            cursor.close();
                            mLabels = null;
                            mValues = null;
                        }


                    }
                    if (mLabels != null && mValues != null) {
                        DATA d = new DATA();
                        String[] labels = mLabels.toArray(new String[mLabels.size()]);
                        float[] values = new float[mValues.size()];
                        for (int i = 0; i < mValues.size(); i++)
                            values[i] = mValues.get(i);
                        d.setmLabels(labels);
                        d.setmValues(values);
                        return d;
                    } else {
                        Log.e(LOG, "Less than 2 days");
                        return null;
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(DATA data) {
                super.onPostExecute(data);
                if (data != null) {
                    String[] labels = data.getmLabels();
                    float[] values = data.getmValues();
                    setLabels(labels);
                    setValues(values);
                    listener.onCompleted();
                } else
                    listener.noData();
            }
        }.execute();
    }
}


class DATA {
    private float[] mValues;
    private String[] mLabels;

    DATA() {
    }

    float[] getmValues() {
        return mValues;
    }

    void setmValues(float[] mValues) {
        this.mValues = mValues;
    }

    String[] getmLabels() {
        return mLabels;
    }

    void setmLabels(String[] mLabels) {
        this.mLabels = mLabels;
    }
}