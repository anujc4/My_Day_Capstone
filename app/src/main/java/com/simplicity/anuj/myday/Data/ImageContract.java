package com.simplicity.anuj.myday.Data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.DefaultValue;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

/**
 * Created by anujc on 26-05-2017.
 */

public interface ImageContract {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    @References(table = JournalDatabaseCreator.Tables.JOURNAL, column = JournalContract._ID)
    String _ID_MAIN = "_id_main";

    @DataType(DataType.Type.TEXT)
    @DefaultValue("null")
    String IMAGE_PATH = "image_path";
}
