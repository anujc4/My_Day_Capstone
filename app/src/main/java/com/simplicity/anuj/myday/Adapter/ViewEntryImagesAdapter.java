package com.simplicity.anuj.myday.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidessence.recyclerviewcursoradapter.RecyclerViewCursorAdapter;
import com.androidessence.recyclerviewcursoradapter.RecyclerViewCursorViewHolder;
import com.bumptech.glide.Glide;
import com.simplicity.anuj.myday.Activity.FullscreenImageViewActivity;
import com.simplicity.anuj.myday.Data.JournalContentProvider;
import com.simplicity.anuj.myday.R;
import com.simplicity.anuj.myday.Utility.ItemClickListener;

import java.io.File;
import java.util.Objects;

/**
 * Created by anuj on 10/9/2016.
 */
public class ViewEntryImagesAdapter extends RecyclerViewCursorAdapter<ViewEntryImagesAdapter.AdapterViewHolder> {
    private ItemClickListener clickListener;
    private final int IMAGE_PATH_INDEX = 2;
    private static final String LOG = ViewEntryImagesAdapter.class.getSimpleName();

    public ViewEntryImagesAdapter(Context context) {
        super(context);
        setupCursorAdapter(null, 0, R.layout.linear_images_list, false);
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
            mImageView = (ImageView) itemView.findViewById(R.id.linear_images_list_image_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void bindCursor(Cursor cursor) {
            if (!cursor.getString(IMAGE_PATH_INDEX).equals("null")) {
                Log.e(LOG, "Trying Image at " + cursor.getPosition());
                try {
                    final File mFile = new File(cursor.getString(IMAGE_PATH_INDEX));
                    Glide.with(mContext)
                            .load(mFile)
                            .centerCrop()
                            .into(mImageView);
                    mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, FullscreenImageViewActivity.class);
                            intent.putExtra("file_name", mFile.getAbsolutePath());
                            mContext.startActivity(intent);
                        }
                    });
                    mImageView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            new AlertDialog.Builder(mContext)
                                    .setTitle("Delete Image")
                                    .setMessage("Do you want to Delete this Image?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            int delete_db = mContext.getContentResolver().delete(
                                                    JournalContentProvider.ImageContentProviderCreator.IMAGE,
                                                    "image_path = ?",
                                                    new String[]{mFile.getAbsolutePath()}
                                            );
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
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onClick(view, getAdapterPosition());
            }
        }
    }
}
