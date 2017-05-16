package com.simplicity.anuj.myday.Utility;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.simplicity.anuj.myday.Data.JournalContentProvider;

/**
 * Created by anujc on 4/2/2017.
 */

public class CommitDatabase extends AsyncTask<ContentValues, Void, Void> {
    private static final String LOG_TAG = CommitDatabase.class.getCanonicalName();
    private Context mContext;
    private IDTransfer mTransfer;

    public CommitDatabase(Context mContext) {
        this.mContext = mContext;
        mTransfer = (IDTransfer) mContext;
    }

    @Override
    protected Void doInBackground(ContentValues... contentValues) {
        int len = contentValues.length;
        if (len < 0) {
            Toast.makeText(mContext, "Some Error Occurred while trying to save the Entry. Please Try Again", Toast.LENGTH_SHORT).show();
        } else {
            /*
            contentValues[0] : mJournalContentValues
            contentValues[1] : mLocationContentValues
            contentValues[2] : mWeatherContentValues
             */

            Uri result = mContext.getContentResolver().insert(JournalContentProvider.ContentProviderCreator.JOURNAL, contentValues[0]);
            long id = ContentUris.parseId(result);

            mTransfer.idOfEntry(id);
            contentValues[1].put(Utils._ID_MAIN_LOCATION, id);
            contentValues[2].put(Utils._ID_MAIN_WEATHER, id);

            Log.e(LOG_TAG, String.valueOf(contentValues[2]));

            mContext.getContentResolver().insert(JournalContentProvider.LocationContentProviderCreator.LOCATION, contentValues[1]);
            mContext.getContentResolver().insert(JournalContentProvider.WeatherContentProviderCreator.WEATHER, contentValues[2]);
        }
        return null;
    }
}
