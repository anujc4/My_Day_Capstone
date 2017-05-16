package com.simplicity.anuj.myday.Adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidessence.recyclerviewcursoradapter.RecyclerViewCursorAdapter;
import com.androidessence.recyclerviewcursoradapter.RecyclerViewCursorViewHolder;
import com.bumptech.glide.Glide;
import com.simplicity.anuj.myday.R;
import com.simplicity.anuj.myday.Utility.ItemClickListener;

import java.io.File;

/**
 * Created by anuj on 10/9/2016.
 */
public class ViewEntryVideosAdapter extends RecyclerViewCursorAdapter<ViewEntryVideosAdapter.AdapterViewHolder> {
    private ItemClickListener clickListener;
    private final int VIDEO_PATH_INDEX = 2;

    public ViewEntryVideosAdapter(Context context) {
        super(context);
        setupCursorAdapter(null, 0, R.layout.linear_video_list, false);
    }

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public AdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AdapterViewHolder(mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent));
    }

    @Override
    public void onBindViewHolder(AdapterViewHolder holder, int position) {
        mCursorAdapter.getCursor().moveToPosition(position);
        setViewHolder(holder);
        mCursorAdapter.bindView(null, mContext, mCursorAdapter.getCursor());
    }

    class AdapterViewHolder extends RecyclerViewCursorViewHolder implements View.OnClickListener {
        ImageView mImageView;

        AdapterViewHolder(View view) {
            super(view);
            mImageView = (ImageView) itemView.findViewById(R.id.linear_videos_list_image_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void bindCursor(Cursor cursor) {
            if (cursor.getString(VIDEO_PATH_INDEX) != null) {
                DisplayMetrics metrics = new DisplayMetrics();
                ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
                int width = metrics.widthPixels;
                double height = width * 9 / 16;
                try {
                    Glide.with(mContext)
                            .load(new File(cursor.getString(VIDEO_PATH_INDEX)))
                            .fitCenter()
                            .override(width, (int) height)
                            .into(mImageView);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onClick(view, getAdapterPosition());
            }
        }
    }
}
