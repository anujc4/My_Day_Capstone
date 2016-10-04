package com.simplicity.anuj.myday.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.simplicity.anuj.myday.R;
import com.squareup.picasso.Picasso;

/**
 * Created by anuj on 10/1/2016.
 */
public class ImagesAdapter extends ArrayAdapter {
    ImageView mImageView;
    Context context;
    private final String LOG_TAG = ImagesAdapter.class.getCanonicalName();
    int LayoutID;



    public ImagesAdapter(Context context, int resource) {
        super(context, resource);
        this.context=context;
        this.LayoutID=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImagesThumbnail mImagesThumbnail;

        if (convertView==null){
            LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
            convertView = layoutInflater.inflate(LayoutID,parent,false);
            mImagesThumbnail = new ImagesThumbnail();
            mImagesThumbnail.mImageView = (ImageView) convertView.findViewById(R.id.imageViewArrayAdapter);
            convertView.setTag(mImagesThumbnail);
        }
        else
        {
            mImagesThumbnail= (ImagesThumbnail) convertView.getTag();
        }

        //TODO USE PICASSO TO LOAD IMAGE HERE

        Picasso.with(context)
                .load(R.drawable.cast_ic_mini_controller_forward30)
                .centerCrop()
                .into(mImageView);

        return convertView;
    }


    class ImagesThumbnail{
        ImageView mImageView;
    }
}
