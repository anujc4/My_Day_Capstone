package com.simplicity.anuj.myday.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
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
    private final String LOG = CalenderViewSearchEntryAdapter.class.getSimpleName();

    public CalenderViewSearchEntryAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        Log.e(LOG, "Inflating View");
        View view = LayoutInflater.from(context).inflate(R.layout.calender_list_view_results, viewGroup, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView mTitleTextView = (TextView) view.findViewById(R.id.calender_list_view_title);
        TextView mDescriptionTextView = (TextView) view.findViewById(R.id.calender_list_view_description);
        Typeface font_adlanta = Typeface.createFromAsset(context.getAssets(), "fonts/adlanta.ttf");
        Typeface font_adlanta_light = Typeface.createFromAsset(context.getAssets(), "fonts/adlanta_light.ttf");
        mTitleTextView.setTypeface(font_adlanta);
        mDescriptionTextView.setTypeface(font_adlanta_light);
        mTitleTextView.setText(cursor.getString(Utils.TITLE_INDEX));
        String temp = cursor.getString(Utils.ENTRY_INDEX);
        if (temp.length() > 150)
            temp = temp.substring(0, 150) + "...";
        mDescriptionTextView.setText(temp);
    }
}