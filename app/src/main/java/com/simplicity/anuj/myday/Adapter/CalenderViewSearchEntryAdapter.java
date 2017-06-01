package com.simplicity.anuj.myday.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.ImageHeaderParser;
import com.simplicity.anuj.myday.Data.JournalContentProvider;
import com.simplicity.anuj.myday.R;
import com.simplicity.anuj.myday.Utility.Utils;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by anuj on 10/2/2016.
 */
public class CalenderViewSearchEntryAdapter extends CursorAdapter {
    private final String LOG = CalenderViewSearchEntryAdapter.class.getSimpleName();
    private TextView mTitleTextView;
    private TextView mDescriptionTextView;
    private CircleImageView mImageView;
    private Context context;

    public CalenderViewSearchEntryAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        Log.e(LOG, "Inflating View");
        View view = LayoutInflater.from(context).inflate(R.layout.calender_list_view_results, viewGroup, false);
        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        mTitleTextView = (TextView) view.findViewById(R.id.calender_list_view_title);
        mDescriptionTextView = (TextView) view.findViewById(R.id.calender_list_view_description);
        mImageView = (CircleImageView) view.findViewById(R.id.calender_list_view_image);
        mImageView.setImageDrawable(null);
        Typeface font_adlanta = Typeface.createFromAsset(context.getAssets(), "fonts/adlanta.ttf");
        Typeface font_adlanta_light = Typeface.createFromAsset(context.getAssets(), "fonts/adlanta_light.ttf");
        mTitleTextView.setTypeface(font_adlanta);
        mDescriptionTextView.setTypeface(font_adlanta_light);
        new loadImage().execute(cursor.getInt(Utils._ID_INDEX));
        mTitleTextView.setText(cursor.getString(Utils.TITLE_INDEX));
        String temp = cursor.getString(Utils.ENTRY_INDEX);
        if (temp.length() > 150)
            temp = temp.substring(0, 150) + "...";
        mDescriptionTextView.setText(temp);
    }

    private class loadImage extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... params) {
            Cursor c;
            String file;
            c = context.getContentResolver().query(JournalContentProvider.ImageContentProviderCreator.IMAGE
                    , null
                    , "_id_main=?"
                    , new String[]{String.valueOf(params[0])}
                    , null);
            if (c != null && c.moveToFirst()) {
                file = c.getString(Utils.IMAGE_PATH_INDEX);
                return file;
            } else {
                c = context.getContentResolver().query(JournalContentProvider.VideoContentProviderCreator.VIDEO
                        , null
                        , "_id_main=?"
                        , new String[]{String.valueOf(params[0])}
                        , null);
                if (c != null && c.moveToFirst()) {
                    file = c.getString(Utils.IMAGE_PATH_INDEX);
                    return file;
                }
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                Glide.with(context)
                        .load(new File(s))
                        .error(R.drawable.error_placeholder)
                        .placeholder(R.drawable.loading_placeholder)
                        .centerCrop()
                        .into(mImageView);
            } else
                Glide.with(context)
                        .load(R.drawable.side_nav_bar)
                        .centerCrop()
                        .into(mImageView);
        }
    }
}