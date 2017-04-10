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

    public static final String AUTHORITY = "com.simplicity.anuj.myday1";

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


    @TableEndpoint(table = JournalDatabaseCreator.MULTIMEDIA)
    public static class MultimediaContentProviderCreator {
        @ContentUri(
                path = JournalDatabaseCreator.MULTIMEDIA,
                type = "vnd.android.cursor.dir/multimedia",
                defaultSort = MultimediaContract._ID + " ASC")
        public static final Uri MULTIMEDIA = Uri.parse("content://" + AUTHORITY + "/multimedia");
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

