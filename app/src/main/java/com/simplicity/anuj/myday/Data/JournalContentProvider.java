package com.simplicity.anuj.myday.Data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by anuj on 9/4/2016.
 */
@ContentProvider(authority = JournalContentProvider.AUTHORITY, database = JournalDatabaseCreator.class)
public final class JournalContentProvider {


    static final String AUTHORITY = "com.simplicity.anuj.myday1";

    JournalContentProvider() {
    }

    @TableEndpoint(table = JournalDatabaseCreator.Tables.JOURNAL)
    public static class ContentProviderCreator {
        @ContentUri(
                path = JournalDatabaseCreator.Tables.JOURNAL,
                type = "vnd.android.cursor.dir/journal",
                defaultSort = JournalContract._ID + " ASC")
        public static final Uri JOURNAL = Uri.parse("content://" + AUTHORITY + "/journal");
    }


    @TableEndpoint(table = JournalDatabaseCreator.IMAGE)
    public static class ImageContentProviderCreator {
        @ContentUri(
                path = JournalDatabaseCreator.IMAGE,
                type = "vnd.android.cursor.dir/multimedia",
                defaultSort = ImageContract._ID + " ASC")
        public static final Uri IMAGE = Uri.parse("content://" + AUTHORITY + "/image");
    }

    @TableEndpoint(table = JournalDatabaseCreator.VIDEO)
    public static class VideoContentProviderCreator {
        @ContentUri(
                path = JournalDatabaseCreator.VIDEO,
                type = "vnd.android.cursor.dir/multimedia",
                defaultSort = VideoContract._ID + " ASC")
        public static final Uri VIDEO = Uri.parse("content://" + AUTHORITY + "/video");
    }

    @TableEndpoint(table = JournalDatabaseCreator.LOCATION)
    public static class LocationContentProviderCreator {
        @ContentUri(
                path = JournalDatabaseCreator.LOCATION,
                type = "vnd.android.cursor.dir/location",
                defaultSort = LocationContract._ID + " ASC")
        public static final Uri LOCATION = Uri.parse("content://" + AUTHORITY + "/location");
    }

    @TableEndpoint(table = JournalDatabaseCreator.WEATHER)
    public static class WeatherContentProviderCreator {
        @ContentUri(
                path = JournalDatabaseCreator.WEATHER,
                type = "vnd.android.cursor.dir/weather",
                defaultSort = WeatherContract._ID + " ASC")
        public static final Uri WEATHER = Uri.parse("content://" + AUTHORITY + "/weather");
    }

}

