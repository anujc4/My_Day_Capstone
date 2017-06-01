package com.simplicity.anuj.myday.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidessence.recyclerviewcursoradapter.RecyclerViewCursorAdapter;
import com.androidessence.recyclerviewcursoradapter.RecyclerViewCursorViewHolder;
import com.bumptech.glide.Glide;
import com.simplicity.anuj.myday.Data.JournalContentProvider;
import com.simplicity.anuj.myday.R;
import com.simplicity.anuj.myday.Utility.ItemClickListener;

import java.io.File;
import java.util.Objects;

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

    class AdapterViewHolder extends RecyclerViewCursorViewHolder
            implements View.OnClickListener {
        ImageView mImageView;

        AdapterViewHolder(View view) {
            super(view);
            mImageView = (ImageView) itemView.findViewById(R.id.linear_videos_list_image_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void bindCursor(Cursor cursor) {
            DisplayMetrics metrics = new DisplayMetrics();
            ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int width = metrics.widthPixels;
            double height = width * 9 / 16;
            try {
                final File mFile = new File(cursor.getString(VIDEO_PATH_INDEX));
                Glide.with(mContext)
                        .load(mFile)
                        .centerCrop()
                        .override(width, (int) height)
                        .into(mImageView);
                mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri photoURI = FileProvider.getUriForFile(mContext, "com.anuj.simplicity.myday.fileprovider", mFile);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setDataAndType(photoURI, "video/*");
                        mContext.startActivity(intent);
                    }
                });
                mImageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        new AlertDialog.Builder(mContext)
                                .setTitle("Delete Video")
                                .setMessage("Do you want to Delete this Video?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int delete_db = mContext.getContentResolver().delete(JournalContentProvider.VideoContentProviderCreator.VIDEO,
                                                "video_path = ?",
                                                new String[]{mFile.getAbsolutePath()});
                                        boolean deleted = mFile.delete();
                                        if (deleted && delete_db == 1)
                                            Toast.makeText(mContext, "Successfully Deleted Video.", Toast.LENGTH_SHORT).show();
                                        notifyDataSetChanged();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Do nothing to dismiss
                                    }
                                })
                                .show();
                        return true;
                    }
                });
            } catch (NullPointerException e) {
                e.printStackTrace();
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
