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

    @DataType(DataType.Type.TEXT)@NotNull
    String DATE_CREATED = "date_created";

    @DataType(DataType.Type.TEXT)@NotNull
    String TIME_CREATED = "time_created";

    @DataType(DataType.Type.TEXT)@NotNull
    String DATE_MODIFIED = "date_modified";

    @DataType(DataType.Type.TEXT)@NotNull
    String TIME_MODIFIED = "time_modified";

    @DataType(DataType.Type.REAL)
    @DefaultValue("null")
    String LATITUDE = "latitude";

    @DataType(DataType.Type.REAL)
    @DefaultValue("null")
    String LONGITUDE = "longitude";

    @DataType(DataType.Type.TEXT)
    String TITLE = "title";

    @DataType(DataType.Type.TEXT)
            @NotNull
    String ENTRY = "entry";

    @DataType(DataType.Type.TEXT)
    @DefaultValue("null")
    String IMAGE_PATH = "imagepath";

}
