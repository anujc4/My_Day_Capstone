package com.simplicity.anuj.myday.Activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;

import com.simplicity.anuj.myday.Adapter.CalenderViewSearchEntryAdapter;
import com.simplicity.anuj.myday.Data.JournalContentProvider;
import com.simplicity.anuj.myday.Data.JournalContract;
import com.simplicity.anuj.myday.R;
import com.simplicity.anuj.myday.Utility.DateFormatter;
import com.simplicity.anuj.myday.Utility.Utils;

import java.util.Date;

public class CalenderActivity extends AppCompatActivity {

    private final String LOG_TAG = CalenderActivity.class.getCanonicalName();
    CalendarView mCalendarView;
    Cursor mCursor;
    TextView nothingFoundTextView;
    ListView mListView;
    CalenderViewSearchEntryAdapter mEntryAdapter;
    Context mContext;
    DateFormatter mDateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mContext = this;
        mDateFormatter = new DateFormatter();
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        mCalendarView.setMaxDate(new Date().getTime());
        nothingFoundTextView = (TextView) findViewById(R.id.calender_view_nothing_found);
        mListView = (ListView) findViewById(R.id.calender_view_list_view);

        Typeface font_south_gardens = Typeface.createFromAsset(getAssets(), "fonts/south_gardens.ttf");
        nothingFoundTextView.setTypeface(font_south_gardens);

        int i = mDateFormatter.getYear();
        int i1 = mDateFormatter.getMonth();
        int i2 = mDateFormatter.getDayOfMonth();
        getInfo(i, i1, i2);

        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                /* i = year
                i1= month ranging 0-11
                i2 = dayOfMonth
                */
                getInfo(i, i1, i2);
            }
        });
    }

    void getInfo(int i, int i1, int i2) {
        DateFormatter formatter = new DateFormatter();
        String date = String.valueOf(i2) + "-" + formatter.getMonth(i1) + "-" + String.valueOf(i);
        Log.e(LOG_TAG, date);
        mCursor = getContentResolver().query(JournalContentProvider.ContentProviderCreator.JOURNAL,
                null,
                JournalContract.DATE_CREATED + "=?",
                new String[]{date},
                "_id DESC");

        if (mCursor.getCount() != 0) {
            Log.e(LOG_TAG, "Entries Found " + mCursor.getCount() + "\n" + mCursor.toString());
            if (nothingFoundTextView.getVisibility() == View.VISIBLE)
                nothingFoundTextView.setVisibility(View.GONE);
            if (mListView.getVisibility() == View.GONE)
                mListView.setVisibility(View.VISIBLE);

            mEntryAdapter = new CalenderViewSearchEntryAdapter(mContext, mCursor, 0);
            mListView.setAdapter(mEntryAdapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    int m = mCursor.getInt(Utils._ID_INDEX);
                    Intent intent = new Intent(mContext, ViewActivity.class);
                    intent.putExtra("ID", m);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    mContext.startActivity(intent);
                }
            });
        } else {
            Log.e(LOG_TAG, "Cursor was Returned empty");
            if (mListView.getVisibility() == View.VISIBLE)
                mListView.setVisibility(View.GONE);
            if (nothingFoundTextView.getVisibility() == View.GONE)
                nothingFoundTextView.setVisibility(View.VISIBLE);
        }
    }
}
