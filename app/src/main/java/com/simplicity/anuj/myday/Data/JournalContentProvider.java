package com.simplicity.anuj.myday.Data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by anuj on 9/4/2016.
 */
@ContentProvider(authority = JournalContentProvider.AUTHORITY, database = JournalDatabaseCreator.class)
public class JournalContentProvider {

    public static final String AUTHORITY = "com.simplicity.anuj.myday";

    @TableEndpoint(table = JournalDatabaseCreator.JOURNAL)
    public static class ContentProviderCreator {
        @ContentUri(
                path = JournalDatabaseCreator.JOURNAL,
                type = "vnd.android.cursor.dir/journal",
                defaultSort = JournalContract._ID + " ASC")
        public static final Uri JOURNAL = Uri.parse("content://" + AUTHORITY + "/journal");
    }

//    @InexactContentUri(
//            path = MovieDatabase.MOVIE + "/#",
//            name = "MOVIE_ID",
//            type = "vnd.android.cursor.item/movie",
//            whereColumn = MovieColumns._ID,
//            pathSegment = 1)
//    public static Uri withId(long id) {
//        return Uri.parse("content://" + CONTENT_AUTHORITY + "/lists/" + id);
//    }

}
