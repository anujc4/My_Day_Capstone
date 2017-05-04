package com.simplicity.anuj.myday.Adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.content.ContextCompat;
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
import com.simplicity.anuj.myday.Data.JournalContentProvider;
import com.simplicity.anuj.myday.Data.JournalContract;
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
        setupCursorAdapter(null,0, R.layout.card_view_item,false);
    }

    @Override
    public JournalAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new JournalAdapterViewHolder(mCursorAdapter.newView(mContext,mCursorAdapter.getCursor(),parent));
    }

    @Override
    public void onBindViewHolder(JournalAdapterViewHolder holder, int position) {

        mCursorAdapter.getCursor().moveToPosition(position);
        setViewHolder(holder);
        mCursorAdapter.bindView(null, mContext,mCursorAdapter.getCursor());
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    class JournalAdapterViewHolder extends RecyclerViewCursorViewHolder implements View.OnClickListener {
        final ImageView imageView;
        final TextView titleTextView;
        final TextView cardDescriptionTextView;
        final TextView cardTimeStampTextView;
        final Button cardDeleteButton;
        final Button cardShareButton;
        final CardView mCardView;
        final FrameLayout mFrameLayout;

        JournalAdapterViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.card_view_image_view);
            titleTextView  = (TextView) itemView.findViewById(R.id.card_title_text);
            cardDescriptionTextView = (TextView) itemView.findViewById(R.id.card_description_text);
            cardTimeStampTextView = (TextView) itemView.findViewById(R.id.card_view_time_stamp_text_view);
            cardShareButton = (Button) itemView.findViewById(R.id.card_view_share);
            cardDeleteButton = (Button) itemView.findViewById(R.id.card_view_delete);
            mCardView = (CardView) itemView.findViewById(R.id.card_view);
            mFrameLayout = (FrameLayout) itemView.findViewById(R.id.frame_layout_main_activity);
            itemView.setOnClickListener(this);
        }

        @Override
        public void bindCursor(final Cursor cursor) {
            itemView.setTag(cursor.getInt(Utils._ID_INDEX));

            cardDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(LOG_TAG, "Delete Button Click Registered");

                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //Performing the Delete Activity on a separate Thread to avoid blocking the UI thread
                            int _id = cursor.getInt(Utils._ID_INDEX);
                            Log.e(LOG_TAG, "Going to delete ID " + _id);
                            context.getContentResolver().delete(JournalContentProvider.ContentProviderCreator.JOURNAL,
                                    JournalContract._ID + "=" + _id, null);

                        }
                    });
                    t.run();
                    Toast.makeText(context, "Entry Deleted Successfully", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                }
            });

            cardShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO Share Data through Intent from here
                    Log.e(LOG_TAG, "SHARE BUTTON CLICK REGISTERED");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        cardShareButton.setElevation(12);
                    }
                    if (cardShareButton.isHovered()) {
                        cardShareButton.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                    } else {
                        cardShareButton.setTextColor(cardShareButton.getTextColors().getDefaultColor());
                    }
                }
            });

            cardTimeStampTextView.setText(cursor.getString(Utils.DATE_CREATED_INDEX) + "  at " + cursor.getString(Utils.TIME_CREATED_INDEX).substring(0, 6));
            titleTextView.setTextColor(mContext.getResources().getColor(R.color.Black));
            titleTextView.setText(cursor.getString(Utils.TITLE_INDEX));
            String description = cursor.getString(Utils.ENTRY_INDEX);
            if (description.length() > 100)
                description = description.substring(0, 100) + "...";
            cardDescriptionTextView.setText(description);


            DisplayMetrics metrics = new DisplayMetrics();
            ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int width = metrics.widthPixels;

            if (!cursor.getString(Utils.THUMB_PATH_INDEX).equals("null")) {
//                Log.e(LOG_TAG,cursor.getString(Utils.THUMB_PATH_INDEX));
                titleTextView.setTextColor(mContext.getResources().getColor(R.color.White));
                Glide.with(mContext)
                        .load(new File(cursor.getString(Utils.THUMB_PATH_INDEX)))
                        .centerCrop()
                        .override(width, 400)
                        .into(imageView);
            }
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
            else Log.e(LOG_TAG, "LISTENER EMPTY!!!!");
        }
    }
}

//public class JournalAdapter extends RecyclerViewCursorAdapter<JournalAdapter.MyViewHolder>{
//
//    /**
//     * Constructor.
//     *
//     * @param context The Context the Adapter is displayed in.
//     */
//    protected JournalAdapter(Context context) {
//        super(context);
//    }
//
//    @Override
//    public JournalAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        return null;
//    }
//
//    @Override
//    public void onBindViewHolder(JournalAdapter.MyViewHolder holder, int position) {
//
//    }
//
//    public class MyViewHolder extends RecyclerViewCursorViewHolder {
//        /**
//         * Constructor.
//         *
//         * @param view The root view of the ViewHolder.
//         */
//        public MyViewHolder(View view) {
//            super(view);
//        }
//
//        @Override
//        public void bindCursor(Cursor cursor) {
//
//        }
//    }
//}
