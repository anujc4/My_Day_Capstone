package com.simplicity.anuj.myday.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.RemoteViews;

import com.simplicity.anuj.myday.Activity.ViewActivity;
import com.simplicity.anuj.myday.Data.JournalContentProvider;
import com.simplicity.anuj.myday.R;

/**
 * Implementation of App Widget functionality.
 */
public class MyDayAppWidget extends AppWidgetProvider {

    public static String CLICK_ACTION = "com.simplicity.anuj.myday.widget.CLICK";
    private static JournalDataProviderObserver sDataObserver;
    private static HandlerThread sWorkerThread;
    private static Handler sWorkerQueue;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_day_app_widget);
//        views.setTextViewText(R.id.appwidget_text, widgetText);
//        views.setTextViewText(R.id.widget_list_day_of_month,"301");
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, JournalRemoteViewsService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            //TODO Make this layout similar to the main activity layout.....
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_day_app_widget);

            //TODO Set the id to list view
            views.setRemoteAdapter(R.id.widget_list_view, intent);
            Intent onClickIntent = new Intent(context, ViewActivity.class);
            onClickIntent.setAction(MyDayAppWidget.CLICK_ACTION);
            onClickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            onClickIntent.setData(Uri.parse(onClickIntent.toUri(Intent.URI_INTENT_SCHEME)));
            final PendingIntent onClickPendingIntent = PendingIntent.getBroadcast(context, 0,
                    onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //TODO Set the id to list view
            views.setPendingIntentTemplate(R.id.widget_list_view, onClickPendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();
        if (action.equals(CLICK_ACTION)) {
            final String symbol = intent.getStringExtra("symbol");
            final String id = intent.getStringExtra("id");

            Intent i = new Intent(context, ViewActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("symbol", symbol);
            i.putExtra("id", id);
            context.startActivity(i);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        final ContentResolver r = context.getContentResolver();
        if (sDataObserver == null) {
            final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            final ComponentName cn = new ComponentName(context, MyDayAppWidget.class);

            sDataObserver = new JournalDataProviderObserver(mgr, cn, sWorkerQueue);
            r.registerContentObserver(JournalContentProvider.ContentProviderCreator.JOURNAL, true, sDataObserver);
        }
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private static class JournalDataProviderObserver extends ContentObserver {
        private AppWidgetManager mAppWidgetManager;
        private ComponentName mComponentName;

        public JournalDataProviderObserver(AppWidgetManager mgr, ComponentName cn, Handler h) {
            super(h);
            mAppWidgetManager = mgr;
            mComponentName = cn;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            // The data has changed, so notify the widget that the collection view needs to be updated.
            // In response, the factory's onDataSetChanged() will be called which will requery the
            // cursor for the new data.
            mAppWidgetManager.notifyAppWidgetViewDataChanged(
                    //TODO Change to list view ID
                    mAppWidgetManager.getAppWidgetIds(mComponentName), R.id.widget_list_view);
        }
    }
}

