package com.simplicity.anuj.myday.ContentProviderGen;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import net.simonvt.schematic.utils.SelectionBuilder;

import java.util.ArrayList;

public class JournalContentProvider extends ContentProvider {
    public static final String AUTHORITY = "com.simplicity.anuj.myday1";

    private static final int CONTENTPROVIDERCREATOR_JOURNAL = 0;

    private static final int MULTIMEDIACONTENTPROVIDERCREATOR_MULTIMEDIA = 1;

    private static final int LOCATIONCONTENTPROVIDERCREATOR_LOCATION = 2;

    private static final int WEATHERCONTENTPROVIDERCREATOR_WEATHER = 3;

    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        MATCHER.addURI(AUTHORITY, "journal", CONTENTPROVIDERCREATOR_JOURNAL);
        MATCHER.addURI(AUTHORITY, "multimedia", MULTIMEDIACONTENTPROVIDERCREATOR_MULTIMEDIA);
        MATCHER.addURI(AUTHORITY, "location", LOCATIONCONTENTPROVIDERCREATOR_LOCATION);
        MATCHER.addURI(AUTHORITY, "weather", WEATHERCONTENTPROVIDERCREATOR_WEATHER);
    }

    private SQLiteOpenHelper database;

    @Override
    public boolean onCreate() {
//        database = JournalDatabaseCreator.getInstance(getContext());
        return true;
    }

    private SelectionBuilder getBuilder(String table) {
        SelectionBuilder builder = new SelectionBuilder();
        return builder;
    }

    private long[] insertValues(SQLiteDatabase db, String table, ContentValues[] values) {
        long[] ids = new long[values.length];
        for (int i = 0; i < values.length; i++) {
            ContentValues cv = values[i];
            db.insertOrThrow(table, null, cv);
        }
        return ids;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = database.getWritableDatabase();
        db.beginTransaction();
        try {
            switch (MATCHER.match(uri)) {
                case CONTENTPROVIDERCREATOR_JOURNAL: {
                    long[] ids = insertValues(db, "journal", values);
                    getContext().getContentResolver().notifyChange(uri, null);
                    break;
                }
                case MULTIMEDIACONTENTPROVIDERCREATOR_MULTIMEDIA: {
                    long[] ids = insertValues(db, "multimedia", values);
                    getContext().getContentResolver().notifyChange(uri, null);
                    break;
                }
                case LOCATIONCONTENTPROVIDERCREATOR_LOCATION: {
                    long[] ids = insertValues(db, "location", values);
                    getContext().getContentResolver().notifyChange(uri, null);
                    break;
                }
                case WEATHERCONTENTPROVIDERCREATOR_WEATHER: {
                    long[] ids = insertValues(db, "weather", values);
                    getContext().getContentResolver().notifyChange(uri, null);
                    break;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return values.length;
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> ops) throws OperationApplicationException {
        ContentProviderResult[] results;
        final SQLiteDatabase db = database.getWritableDatabase();
        db.beginTransaction();
        try {
            results = super.applyBatch(ops);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return results;
    }

    @Override
    public String getType(Uri uri) {
        switch (MATCHER.match(uri)) {
            case CONTENTPROVIDERCREATOR_JOURNAL: {
                return "vnd.android.cursor.dir/journal";
            }
            case MULTIMEDIACONTENTPROVIDERCREATOR_MULTIMEDIA: {
                return "vnd.android.cursor.dir/multimedia";
            }
            case LOCATIONCONTENTPROVIDERCREATOR_LOCATION: {
                return "vnd.android.cursor.dir/location";
            }
            case WEATHERCONTENTPROVIDERCREATOR_WEATHER: {
                return "vnd.android.cursor.dir/weather";
            }
            default: {
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = database.getReadableDatabase();
        switch (MATCHER.match(uri)) {
            case CONTENTPROVIDERCREATOR_JOURNAL: {
                SelectionBuilder builder = getBuilder("ContentProviderCreator");
                if (sortOrder == null) {
                    sortOrder = "_id ASC";
                }
                String table = "journal";
                final String groupBy = null;
                final String having = null;
                final String limit = null;
                Cursor cursor = builder.table(table)
                        .where(selection, selectionArgs)
                        .query(db, projection, groupBy, having, sortOrder, limit);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            }
            case MULTIMEDIACONTENTPROVIDERCREATOR_MULTIMEDIA: {
                SelectionBuilder builder = getBuilder("MultimediaContentProviderCreator");
                if (sortOrder == null) {
                    sortOrder = "_id ASC";
                }
                String table = "multimedia";
                final String groupBy = null;
                final String having = null;
                final String limit = null;
                Cursor cursor = builder.table(table)
                        .where(selection, selectionArgs)
                        .query(db, projection, groupBy, having, sortOrder, limit);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            }
            case LOCATIONCONTENTPROVIDERCREATOR_LOCATION: {
                SelectionBuilder builder = getBuilder("LocationContentProviderCreator");
                if (sortOrder == null) {
                    sortOrder = "_id ASC";
                }
                String table = "location";
                final String groupBy = null;
                final String having = null;
                final String limit = null;
                Cursor cursor = builder.table(table)
                        .where(selection, selectionArgs)
                        .query(db, projection, groupBy, having, sortOrder, limit);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            }
            case WEATHERCONTENTPROVIDERCREATOR_WEATHER: {
                SelectionBuilder builder = getBuilder("WeatherContentProviderCreator");
                if (sortOrder == null) {
                    sortOrder = "_id ASC";
                }
                String table = "weather";
                final String groupBy = null;
                final String having = null;
                final String limit = null;
                Cursor cursor = builder.table(table)
                        .where(selection, selectionArgs)
                        .query(db, projection, groupBy, having, sortOrder, limit);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            }
            default: {
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = database.getWritableDatabase();
        switch (MATCHER.match(uri)) {
            case CONTENTPROVIDERCREATOR_JOURNAL: {
                final long id = db.insertOrThrow("journal", null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            }
            case MULTIMEDIACONTENTPROVIDERCREATOR_MULTIMEDIA: {
                final long id = db.insertOrThrow("multimedia", null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            }
            case LOCATIONCONTENTPROVIDERCREATOR_LOCATION: {
                final long id = db.insertOrThrow("location", null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            }
            case WEATHERCONTENTPROVIDERCREATOR_WEATHER: {
                final long id = db.insertOrThrow("weather", null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            }
            default: {
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        final SQLiteDatabase db = database.getWritableDatabase();
        switch (MATCHER.match(uri)) {
            case CONTENTPROVIDERCREATOR_JOURNAL: {
                SelectionBuilder builder = getBuilder("ContentProviderCreator");
                builder.where(where, whereArgs);
                final int count = builder.table("journal")
                        .update(db, values);
                if (count > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return count;
            }
            case MULTIMEDIACONTENTPROVIDERCREATOR_MULTIMEDIA: {
                SelectionBuilder builder = getBuilder("MultimediaContentProviderCreator");
                builder.where(where, whereArgs);
                final int count = builder.table("multimedia")
                        .update(db, values);
                if (count > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return count;
            }
            case LOCATIONCONTENTPROVIDERCREATOR_LOCATION: {
                SelectionBuilder builder = getBuilder("LocationContentProviderCreator");
                builder.where(where, whereArgs);
                final int count = builder.table("location")
                        .update(db, values);
                if (count > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return count;
            }
            case WEATHERCONTENTPROVIDERCREATOR_WEATHER: {
                SelectionBuilder builder = getBuilder("WeatherContentProviderCreator");
                builder.where(where, whereArgs);
                final int count = builder.table("weather")
                        .update(db, values);
                if (count > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return count;
            }
            default: {
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
        }
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        final SQLiteDatabase db = database.getWritableDatabase();
        switch (MATCHER.match(uri)) {
            case CONTENTPROVIDERCREATOR_JOURNAL: {
                SelectionBuilder builder = getBuilder("ContentProviderCreator");
                builder.where(where, whereArgs);
                final int count = builder
                        .table("journal")
                        .delete(db);
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            }
            case MULTIMEDIACONTENTPROVIDERCREATOR_MULTIMEDIA: {
                SelectionBuilder builder = getBuilder("MultimediaContentProviderCreator");
                builder.where(where, whereArgs);
                final int count = builder
                        .table("multimedia")
                        .delete(db);
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            }
            case LOCATIONCONTENTPROVIDERCREATOR_LOCATION: {
                SelectionBuilder builder = getBuilder("LocationContentProviderCreator");
                builder.where(where, whereArgs);
                final int count = builder
                        .table("location")
                        .delete(db);
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            }
            case WEATHERCONTENTPROVIDERCREATOR_WEATHER: {
                SelectionBuilder builder = getBuilder("WeatherContentProviderCreator");
                builder.where(where, whereArgs);
                final int count = builder
                        .table("weather")
                        .delete(db);
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            }
            default: {
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
        }
    }
}
