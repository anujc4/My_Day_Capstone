package com.simplicity.anuj.myday.Data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

/**
 * Created by anujc on 28-03-2017.
 */

public interface LocationContract {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(DataType.Type.INTEGER)
    @References(table = JournalDatabaseCreator.Tables.JOURNAL, column = JournalContract._ID)
    String _ID_MAIN = "_id_main";

    @DataType(DataType.Type.REAL)
    String LATITUDE = "latitude";

    @DataType(DataType.Type.REAL)
    String LONGITUDE = "longitude";
}
