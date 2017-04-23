package com.simplicity.anuj.myday.Data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

/**
 * Created by anujc on 28-03-2017.
 */

public interface WeatherContract {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(DataType.Type.INTEGER)
    @References(table = JournalDatabaseCreator.Tables.JOURNAL, column = JournalContract._ID)
    String _ID_MAIN = "_id_main";

    @DataType(DataType.Type.INTEGER)
    String WEATHER_CONDITION_ID = "weather_condition_id";

    @DataType(DataType.Type.TEXT)
    String WEATHER_MAIN = "weather_main";

    @DataType(DataType.Type.TEXT)
    String WEATHER_DESCRIPTION = "weather_description";

    @DataType(DataType.Type.INTEGER)
    String MAIN_TEMP = "main_temp";

    @DataType(DataType.Type.INTEGER)
    String MAIN_TEMP_MIN = "main_temp_min";

    @DataType(DataType.Type.INTEGER)
    String MAIN_TEMP_MAX = "main_temp_max";

    @DataType(DataType.Type.INTEGER)
    String MAIN_HUMIDITY = "main_humidity";

    @DataType(DataType.Type.INTEGER)
    String MAIN_PRESSURE = "main_pressure";

    @DataType(DataType.Type.INTEGER)
    String WIND_SPEED = "wind_speed";

    @DataType(DataType.Type.TEXT)
    String CLOUDS = "clouds";

    @DataType(DataType.Type.TEXT)
    String NAME = "name";
}
