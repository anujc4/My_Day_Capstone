package com.simplicity.anuj.myday.Data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.DefaultValue;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by anuj on 9/4/2016.
 */
public interface JournalContract {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(DataType.Type.TEXT)
    String TITLE = "title";

    @DataType(DataType.Type.TEXT)
    String ENTRY = "entry";

    @DataType(DataType.Type.TEXT)@NotNull
    String DATE_CREATED = "date_created";

    @DataType(DataType.Type.TEXT)@NotNull
    String TIME_CREATED = "time_created";

    @DataType(DataType.Type.TEXT)@NotNull
    String DATE_MODIFIED = "date_modified";

    @DataType(DataType.Type.TEXT)@NotNull
    String TIME_MODIFIED = "time_modified";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String TIME_STAMP = "time_stamp";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    @DefaultValue("-1")
    String HAS_LOCATION = "has_location";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    @DefaultValue("-1")
    String HAS_WEATHER = "has_weather";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    @DefaultValue("-1")
    String IS_MARKED = "is_marked";
}
