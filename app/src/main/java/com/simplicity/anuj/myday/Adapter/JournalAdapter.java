package com.simplicity.anuj.myday.Adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidessence.recyclerviewcursoradapter.RecyclerViewCursorAdapter;
import com.androidessence.recyclerviewcursoradapter.RecyclerViewCursorViewHolder;
import com.bumptech.glide.Glide;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.simplicity.anuj.myday.Data.JournalContentProvider;
import com.simplicity.anuj.myday.R;
import com.simplicity.anuj.myday.Utility.ItemClickListener;
import com.simplicity.anuj.myday.Utility.Utils;

import java.io.File;

/**
 * Created by anuj on 9/28/2016.
 */
public class JournalAdapter extends RecyclerViewCursorAdapter<JournalAdapter.JournalAdapterViewHolder> {
    private static final String LOG_TAG = JournalAdapter.class.getCanonicalName();

    static int count = 0;
    private Context context;
    private ItemClickListener clickListener;

    public JournalAdapter(Context context) {
        super(context);
        this.context = context;
        setupCursorAdapter(null, 0, R.layout.card_view_item, false);
    }

//    @Override
//    public int getItemCount() {
//        return count;
//    }

    @Override
    public JournalAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new JournalAdapterViewHolder(mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent));
    }

    @Override
    public void onBindViewHolder(JournalAdapterViewHolder holder, int position) {

        mCursorAdapter.getCursor().moveToPosition(position);
        setViewHolder(holder);
        mCursorAdapter.bindView(null, mContext, mCursorAdapter.getCursor());
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    class JournalAdapterViewHolder extends RecyclerViewCursorViewHolder
            implements View.OnClickListener {
        final ImageView imageView;
        final TextView titleTextView;
        final TextView cardDescriptionTextView;
        final TextView cardTimeStampTextView;
        final Button cardDeleteButton;
        final Button cardShareButton;
        final CardView mCardView;
        final FrameLayout mFrameLayout;
        final LikeButton likeButton;

        JournalAdapterViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.card_view_image_view);
            titleTextView = (TextView) itemView.findViewById(R.id.card_title_text);
            cardDescriptionTextView = (TextView) itemView.findViewById(R.id.card_description_text);
            cardTimeStampTextView = (TextView) itemView.findViewById(R.id.card_view_time_stamp_text_view);
            cardShareButton = (Button) itemView.findViewById(R.id.card_view_share);
            cardDeleteButton = (Button) itemView.findViewById(R.id.card_view_delete);
            mCardView = (CardView) itemView.findViewById(R.id.card_view);
            mFrameLayout = (FrameLayout) itemView.findViewById(R.id.frame_layout_main_activity);
            likeButton = (LikeButton) itemView.findViewById(R.id.marked_favourite_entry);

            Typeface adlanta = Typeface.createFromAsset(mContext.getAssets(), "fonts/adlanta.ttf");
            Typeface adlanta_light = Typeface.createFromAsset(mContext.getAssets(), "fonts/adlanta_light.ttf");
            titleTextView.setTypeface(adlanta);
            cardDescriptionTextView.setTypeface(adlanta_light);
            cardTimeStampTextView.setTypeface(adlanta_light);
            cardShareButton.setTypeface(adlanta);
            cardDeleteButton.setTypeface(adlanta);
            itemView.setOnClickListener(this);
        }

        @Override
        public void bindCursor(final Cursor cursor) {
            itemView.setTag(R.id._ID, cursor.getInt(Utils._ID_INDEX));
            itemView.setTag(R.id.pos, getAdapterPosition());

            final int index = cursor.getInt(Utils._ID_INDEX);

            imageView.setImageResource(android.R.color.transparent);
            cardDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(mContext)
                            .setTitle("Confirm Delete")
                            .setMessage("Are you sure you want to delete this entry? This action cannot be reverted.")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Thread t = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //Performing the Delete Activity on a separate Thread to avoid blocking the UI thread
                                            Log.e(LOG_TAG, "Going to delete ID " + index);
                                            context.getContentResolver().delete(
                                                    JournalContentProvider.ContentProviderCreator.JOURNAL,
                                                    Utils._ID_JOURNAL + "=?",
                                                    new String[]{String.valueOf(index)});
                                        }
                                    });
                                    t.run();
                                    Toast.makeText(context, "Entry Deleted Successfully", Toast.LENGTH_SHORT).show();
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                }
            });

            likeButton.setOnLikeListener(new OnLikeListener() {
                ContentValues tempValues = new ContentValues();

                @Override
                public void liked(LikeButton likeButton) {
                    tempValues.put(Utils.IS_MARKED, "1");
                    new AsyncTask<Integer, Void, Void>() {
                        @Override
                        protected Void doInBackground(Integer... params) {
                            int updated = mContext.getContentResolver().update(
                                    JournalContentProvider.ContentProviderCreator.JOURNAL,
                                    tempValues,
                                    "_id = ?",
                                    new String[]{String.valueOf(index)}
                            );
//                            Log.e(LOG_TAG ,updated + " entries Updated");
                            return null;
                        }
                    }.execute(index);
                    notifyItemChanged(getAdapterPosition());
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    tempValues.put(Utils.IS_MARKED, "-1");
                    new AsyncTask<Integer, Void, Void>() {
                        @Override
                        protected Void doInBackground(Integer... params) {
                            int updated = mContext.getContentResolver().update(
                                    JournalContentProvider.ContentProviderCreator.JOURNAL,
                                    tempValues,
                                    "_id = ?",
                                    new String[]{String.valueOf(index)}
                            );

//                            Log.e(LOG_TAG ,updated + " entries Updated");
                            return null;
                        }
                    }.execute(index);
                    notifyItemChanged(getAdapterPosition());
                }
            });

            final String title = cursor.getString(Utils.TITLE_INDEX);
            final String entry = cursor.getString(Utils.ENTRY_INDEX);
            final String date_created = cursor.getString(Utils.DATE_CREATED_INDEX);
            final String time_created = cursor.getString(Utils.TIME_CREATED_INDEX);
            final int isMarked = cursor.getInt(Utils.IS_MARKED_INDEX);
            if (isMarked == 1)
                likeButton.setLiked(true);
            else
                likeButton.setLiked(false);

            cardShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    String message = title +
                            "\n" + entry +
                            "\n" + "Created on:" +
                            date_created + " at " +
                            time_created +
                            "\n" + "--Sent From My Day.";
                    sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                    sendIntent.setType("text/plain");
                    context.startActivity(sendIntent);
                }
            });

            cardTimeStampTextView.setText(cursor.getString(Utils.DATE_CREATED_INDEX) + "  at " + cursor.getString(Utils.TIME_CREATED_INDEX));
            titleTextView.setTextColor(mContext.getResources().getColor(R.color.primary_text));
            titleTextView.setText(cursor.getString(Utils.TITLE_INDEX));
            String description = cursor.getString(Utils.ENTRY_INDEX);
            if (description.length() > 200)
                description = description.substring(0, 200) + "...";
            cardDescriptionTextView.setText(description);


            DisplayMetrics metrics = new DisplayMetrics();
            ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
            final int width = metrics.widthPixels;

            //LOAD Image or Video File
            new AsyncTask<Integer, Void, File>() {
                @Override
                protected File doInBackground(Integer... params) {
                    int _ID = params[0];
                    Cursor cursor_thumbnail = mContext.getContentResolver().query(
                            JournalContentProvider.ImageContentProviderCreator.IMAGE,
                            null,
                            "_id_main = ?",
                            new String[]{String.valueOf(_ID)},
                            null
                    );

                    if (cursor_thumbnail != null && cursor_thumbnail.moveToFirst()) {
                        File file = new File(cursor_thumbnail.getString(Utils.IMAGE_PATH_INDEX));
                        cursor_thumbnail.close();
                        return file;
                    } else {
                        cursor_thumbnail = mContext.getContentResolver().query(
                                JournalContentProvider.VideoContentProviderCreator.VIDEO,
                                null,
                                "_id_main = ?",
                                new String[]{String.valueOf(_ID)},
                                null
                        );
                        if (cursor_thumbnail != null && cursor_thumbnail.moveToFirst()) {
                            File file = new File(cursor_thumbnail.getString(Utils.VIDEO_PATH_INDEX));
                            Log.e(LOG_TAG, file.getAbsolutePath());
                            cursor_thumbnail.close();
                            return file;
                        } else {
                            cursor_thumbnail.close();
                        }
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(File file) {
                    super.onPostExecute(file);
                    if (file != null) {
                        titleTextView.setTextColor(mContext.getResources().getColor(R.color.white));
                        Glide.with(mContext)
                                .load(file)
                                .centerCrop()
                                .override(width, 400)
                                .error(R.drawable.error_placeholder)
                                .placeholder(R.drawable.loading_placeholder)
                                .into(imageView);

                    }
                }
            }.execute(cursor.getInt(Utils._ID_INDEX));
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
            else Log.e(LOG_TAG, "LISTENER EMPTY!!!!");
        }
    }
}


