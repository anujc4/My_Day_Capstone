package com.simplicity.anuj.myday.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidessence.recyclerviewcursoradapter.RecyclerViewCursorAdapter;
import com.androidessence.recyclerviewcursoradapter.RecyclerViewCursorViewHolder;
import com.bumptech.glide.Glide;
import com.simplicity.anuj.myday.R;
import com.simplicity.anuj.myday.Utility.ItemClickListener;
import com.simplicity.anuj.myday.Utility.Utils;

import java.io.File;

/**
 * Created by anuj on 10/9/2016.
 */
public class ViewEntryAdapter extends RecyclerViewCursorAdapter<ViewEntryAdapter.AdapterViewHolder> {
    private ItemClickListener clickListener;

    public ViewEntryAdapter(Context context) {
        super(context);
        setupCursorAdapter(null, 0, R.layout.card_view_staggered_pics, false);
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
            mImageView = (ImageView) itemView.findViewById(R.id.card_view_staggered_pics_image_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void bindCursor(Cursor cursor) {
            try {
                Glide.with(mContext)
                        .load(new File(cursor.getString(Utils.IMAGE_PATH_INDEX)))
                        .fitCenter()
                        .into(mImageView);
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
