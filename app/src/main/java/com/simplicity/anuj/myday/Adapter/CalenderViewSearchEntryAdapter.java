package com.simplicity.anuj.myday.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.simplicity.anuj.myday.R;
import com.simplicity.anuj.myday.Utility.Utils;

/**
 * Created by anuj on 10/2/2016.
 */
public class CalenderViewSearchEntryAdapter extends CursorAdapter {
    TextView mTitleTextView;
    TextView mDescriptionTextView;

    public CalenderViewSearchEntryAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(R.layout.calender_list_view_results, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        mTitleTextView = (TextView) view.findViewById(R.id.calender_list_view_title);
        mDescriptionTextView = (TextView) view.findViewById(R.id.calender_list_view_description);
        Log.e("ADAPTER", cursor.getString(Utils.ENTRY_INDEX));
        mTitleTextView.setText(cursor.getString(Utils.TITLE_INDEX));
        String temp = cursor.getString(Utils.ENTRY_INDEX);
        if (temp.length() > 100) {
            temp = temp.substring(0, 100) + "...";
        }
        mDescriptionTextView.setText(temp);
    }
}
