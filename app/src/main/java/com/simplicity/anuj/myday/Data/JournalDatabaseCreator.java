package com.simplicity.anuj.myday.Data;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.IfNotExists;
import net.simonvt.schematic.annotation.OnCreate;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by anuj on 9/4/2016.
 */
@Database(version = JournalDatabaseCreator.VERSION)
public class JournalDatabaseCreator {
    static final int VERSION = 9;
    @Table(ImageContract.class)
    static final String IMAGE = "image";
    @Table(VideoContract.class)
    static final String VIDEO = "video";
    @Table(LocationContract.class)
    static final String LOCATION = "location";
    @Table(WeatherContract.class)
    static final String WEATHER = "weather";

    @OnCreate
    public static void onCreate(Context context, SQLiteDatabase db) {
//        Log.e("SQL", db.getPath() + "\n" + db.toString());

        //Store path to database
        SharedPreferences fileDirPreferences = context.getSharedPreferences("com.simplicity.anuj.myday.FileDirectory", MODE_PRIVATE);
        SharedPreferences.Editor editor = fileDirPreferences.edit();
        editor.putString("database_path", db.getPath());
        editor.apply();

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
