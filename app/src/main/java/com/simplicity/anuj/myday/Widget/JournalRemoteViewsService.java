package com.simplicity.anuj.myday.Widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.simplicity.anuj.myday.Data.JournalContentProvider;
import com.simplicity.anuj.myday.Data.JournalContract;
import com.simplicity.anuj.myday.R;
import com.simplicity.anuj.myday.Utility.Utils;

/**
 * Created by anuj on 11/10/2016.
 */

public class JournalRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new JournalRemoteViewsFactory(this.getApplicationContext(), intent);

    }
}

class JournalRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    final private String LOG_TAG = JournalRemoteViewsFactory.class.getSimpleName();
    private Cursor mCursor;
    private Context mContext;
    private int mAppWidgetID;


    public JournalRemoteViewsFactory(Context applicationContext, Intent intent) {
        mContext = applicationContext;
        mAppWidgetID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (mCursor != null)
            mCursor.close();

        mCursor = mContext.getContentResolver().query(JournalContentProvider.ContentProviderCreator.JOURNAL,
                null,
                null,
                null,
                "_id DESC");
        Log.e(LOG_TAG, mCursor.toString());
    }

    @Override
    public void onDestroy() {
        if (mCursor != null)
            mCursor.close();
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int i) {

        String id = null;
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_collection_item);
        if (mCursor.moveToPosition(i)) {
            id = mCursor.getString(Utils._ID_INDEX);
            rv.setTextViewText(R.id.widget_list_journal_entry, mCursor.getString(mCursor.getColumnIndex(JournalContract.ENTRY)));
            rv.setTextViewText(R.id.widget_list_journal_title, mCursor.getString(mCursor.getColumnIndex(JournalContract.TITLE)));
            String date = mCursor.getString(mCursor.getColumnIndex(JournalContract.DATE_CREATED));
            rv.setTextViewText(R.id.widget_list_day_of_month, date.substring(0, 2));
            rv.setTextViewText(R.id.widget_list_rest_of_month, date.substring(2, date.length()));
        }

        final Intent in = new Intent();
        Log.d(LOG_TAG, "getViewAt: " + id);
        in.putExtra("id", id);
        rv.setOnClickFillInIntent(R.id.widget_relative_layout, in);
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

