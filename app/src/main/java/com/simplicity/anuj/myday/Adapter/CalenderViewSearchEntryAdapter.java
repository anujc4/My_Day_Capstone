package com.simplicity.anuj.myday.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidessence.recyclerviewcursoradapter.RecyclerViewCursorAdapter;
import com.androidessence.recyclerviewcursoradapter.RecyclerViewCursorViewHolder;
import com.bumptech.glide.Glide;
import com.simplicity.anuj.myday.Data.JournalContentProvider;
import com.simplicity.anuj.myday.R;
import com.simplicity.anuj.myday.Utility.ItemClickListener;
import com.simplicity.anuj.myday.Utility.Utils;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by anuj on 10/2/2016.
 */

public class CalenderViewSearchEntryAdapter extends RecyclerViewCursorAdapter<CalenderViewSearchEntryAdapter.CalenderViewHolder> {

    private static final String LOG = CalenderViewSearchEntryAdapter.class.getSimpleName();
    private ItemClickListener clickListener;

    public CalenderViewSearchEntryAdapter(Context context) {
        super(context);
        setupCursorAdapter(null, 0, R.layout.calender_list_view_results, false);
    }

    @Override
    public CalenderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CalenderViewSearchEntryAdapter.CalenderViewHolder(mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent));
    }

    @Override
    public void onBindViewHolder(CalenderViewHolder holder, int position) {
        mCursorAdapter.getCursor().moveToPosition(position);
        setViewHolder(holder);
        mCursorAdapter.bindView(null, mContext, mCursorAdapter.getCursor());
    }

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }


    class CalenderViewHolder extends RecyclerViewCursorViewHolder implements View.OnClickListener {
        private CircleImageView mImageView;
        private TextView mTitleTextView;
        private TextView mDescriptionTextView;

        CalenderViewHolder(View view) {
            super(view);
            mImageView = (CircleImageView) view.findViewById(R.id.calender_list_view_image);
            mTitleTextView = (TextView) view.findViewById(R.id.calender_list_view_title);
            mDescriptionTextView = (TextView) view.findViewById(R.id.calender_list_view_description);
            Typeface font_adlanta = Typeface.createFromAsset(mContext.getAssets(), "fonts/adlanta.ttf");
            Typeface font_adlanta_light = Typeface.createFromAsset(mContext.getAssets(), "fonts/adlanta_light.ttf");
            mTitleTextView.setTypeface(font_adlanta);
            mDescriptionTextView.setTypeface(font_adlanta_light);
            view.setOnClickListener(this);
        }

        @Override
        public void bindCursor(Cursor cursor) {
            itemView.setTag(cursor.getInt(Utils._ID_INDEX));
            new loadImage().execute(cursor.getInt(Utils._ID_INDEX));
            mTitleTextView.setText(cursor.getString(Utils.TITLE_INDEX));
            String temp = cursor.getString(Utils.ENTRY_INDEX);
            if (temp.length() > 150)
                temp = temp.substring(0, 150) + "...";
            mDescriptionTextView.setText(temp);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) clickListener.onClick(v, getAdapterPosition());
            else Log.e(LOG, "LISTENER EMPTY!!!!");
        }

        private class loadImage extends AsyncTask<Integer, Void, String> {
            @Override
            protected String doInBackground(Integer... params) {
                Cursor c;
                String file;
                c = mContext.getContentResolver().query(JournalContentProvider.ImageContentProviderCreator.IMAGE
                        , null
                        , "_id_main=?"
                        , new String[]{String.valueOf(params[0])}
                        , null);
                if (c != null && c.moveToFirst()) {
                    file = c.getString(Utils.IMAGE_PATH_INDEX);
                    c.close();
                    return file;
                } else {
                    c = mContext.getContentResolver().query(JournalContentProvider.VideoContentProviderCreator.VIDEO
                            , null
                            , "_id_main=?"
                            , new String[]{String.valueOf(params[0])}
                            , null);
                    if (c != null && c.moveToFirst()) {
                        file = c.getString(Utils.IMAGE_PATH_INDEX);
                        c.close();
                        return file;
                    }
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null) {
                    Glide.with(mContext)
                            .load(new File(s))
                            .error(R.drawable.error_placeholder)
                            .placeholder(R.drawable.loading_placeholder)
                            .centerCrop()
                            .into(mImageView);
                } else
                    Glide.with(mContext)
                            .load(R.drawable.side_nav_iamge)
                            .centerCrop()
                            .into(mImageView);
            }
        }
    }
}