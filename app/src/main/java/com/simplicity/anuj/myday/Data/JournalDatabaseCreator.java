package com.simplicity.anuj.myday.Data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by anuj on 9/4/2016.
 */
@Database(version = JournalDatabaseCreator.VERSION)
public class JournalDatabaseCreator {
    public static final int VERSION = 3;

    @Table(JournalContract.class)
    public static final String JOURNAL = "journal";
}
