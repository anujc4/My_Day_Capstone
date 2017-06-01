package com.simplicity.anuj.myday.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.simplicity.anuj.myday.R;

import java.io.File;
import java.util.List;

/**
 * Created by anuj on 10/9/2016.
 */
public class CameraVideoAdapter extends RecyclerView.Adapter<CameraVideoAdapter.CameraViewHolder> {
    private List<String> mVideosList;
    private Context context;

    public CameraVideoAdapter(List<String> mVideosList, Context context) {
        this.mVideosList = mVideosList;
        this.context = context;
    }

    @Override
    public CameraViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.camera_videos_add_new_entry, parent, false);
        return new CameraViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CameraViewHolder holder, int position) {
        final File mFile = new File(mVideosList.get(position));
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        double height = width * 9 / 16;
        Glide.with(context)
                .load(mFile)
                .centerCrop()
                .override(width, (int) height)
                .into(holder.mImageView);
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri photoURI = FileProvider.getUriForFile(context, "com.anuj.simplicity.myday.fileprovider", mFile);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(photoURI, "video/*");
                context.startActivity(intent);
            }
        });
        holder.mImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //TODO Implement Delete Function Here
                new AlertDialog.Builder(context)
                        .setTitle("Delete Video")
                        .setMessage("Are you sure you want to Delete this Video?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context, "Currently Deleting Videos is supported from View Entry Only.", Toast.LENGTH_LONG).show();
                            }
                        })
                        .show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVideosList.size();
    }

    class CameraViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;

        CameraViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.videoViewAdapter);
        }
    }
}
