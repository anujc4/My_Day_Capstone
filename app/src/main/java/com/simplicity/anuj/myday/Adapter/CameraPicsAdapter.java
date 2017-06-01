package com.simplicity.anuj.myday.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.simplicity.anuj.myday.Activity.FullscreenImageViewActivity;
import com.simplicity.anuj.myday.R;

import java.io.File;
import java.util.List;

/**
 * Created by anuj on 10/9/2016.
 */
public class CameraPicsAdapter extends RecyclerView.Adapter<CameraPicsAdapter.CameraViewHolder> {
    List<String> mImagesList;
    Context context;

    public CameraPicsAdapter(List<String> mImagesList, Context context) {
        this.mImagesList = mImagesList;
        this.context = context;
    }

    @Override
    public CameraViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.camera_images_add_new_entry, parent, false);
        return new CameraViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CameraViewHolder holder, int position) {
        final int pos = position;
        File mFile = new File(mImagesList.get(position));
        Glide.with(context)
                .load(mFile)
                .centerCrop()
                .into(holder.mImageView);
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FullscreenImageViewActivity.class);
                intent.putExtra("file_name", mImagesList.get(pos));
                context.startActivity(intent);
            }
        });
        holder.mImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //TODO Implement Delete Function Here
                new AlertDialog.Builder(context)
                        .setTitle("Delete Image")
                        .setMessage("Are you sure you want to Delete this Image?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context, "Currently Deleting Images is supported from View Entry Only.", Toast.LENGTH_LONG).show();
                            }
                        })
                        .show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImagesList.size();
    }

    class CameraViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;

        CameraViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.imageViewAdapter);
        }
    }
}
