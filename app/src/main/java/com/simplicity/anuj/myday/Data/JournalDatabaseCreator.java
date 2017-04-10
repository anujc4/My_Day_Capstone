package com.simplicity.anuj.myday.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.IfNotExists;
import net.simonvt.schematic.annotation.OnCreate;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by anuj on 9/4/2016.
 */
@Database(version = JournalDatabaseCreator.VERSION)
public class JournalDatabaseCreator {
    static final int VERSION = 5;
    @Table(MultimediaContract.class)
    static final String MULTIMEDIA = "multimedia";
    @Table(LocationContract.class)
    static final String LOCATION = "location";
    @Table(WeatherContract.class)
    static final String WEATHER = "weather";

    @OnCreate
    public static void onCreate(Context context, SQLiteDatabase db) {
        Log.e("SQL", db.getPath() + "\n" + db.toString());
//        context.deleteDatabase("journalDatabaseCreator.db");
    }

    @OnUpgrade
    public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion,
                                 int newVersion) {
        context.deleteDatabase("journalDatabaseCreator.db");
        onCreate(context, db);
    }

    static class Tables {
        @Table(JournalContract.class)
        @IfNotExists
        public static final String JOURNAL = "journal";
    }

}
