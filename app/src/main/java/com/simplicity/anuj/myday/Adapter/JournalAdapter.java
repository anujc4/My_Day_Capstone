package com.simplicity.anuj.myday.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidessence.recyclerviewcursoradapter.RecyclerViewCursorAdapter;
import com.androidessence.recyclerviewcursoradapter.RecyclerViewCursorViewHolder;
import com.simplicity.anuj.myday.R;

/**
 * Created by anuj on 9/28/2016.
 */
public class JournalAdapter extends RecyclerViewCursorAdapter<JournalAdapter.JournalAdapterViewHolder> {
    //Cursor mCursor;
    /**
     * Constructor.
     *
     * @param context The Context the JournalAdapter is displayed in.
     */
    public JournalAdapter(Context context) {
        super(context);
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

    public class JournalAdapterViewHolder extends RecyclerViewCursorViewHolder {
        public final ImageView imageView;
        public final TextView titleTextView;
        public final TextView cardDescriptionTextView;

        /**
         * Constructor.
         *
         * @param view The root view of the ViewHolder.
         */
        public JournalAdapterViewHolder(View view) {
            super(view);
            imageView = (ImageView) itemView.findViewById(R.id.card_image);
            titleTextView  = (TextView) itemView.findViewById(R.id.card_title_text);
            cardDescriptionTextView = (TextView) itemView.findViewById(R.id.card_description_text);
        }

        @Override
        public void bindCursor(Cursor cursor) {
            titleTextView.setText(cursor.getString(7));
            cardDescriptionTextView.setText(cursor.getString(8));
        }
    }
}
