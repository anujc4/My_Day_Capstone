package com.simplicity.anuj.myday.Data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

/**
 * Created by anuj on 10/8/2016.
 */
public interface MultimediaContract {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    @References(table = JournalDatabaseCreator.Tables.JOURNAL, column = JournalContract._ID)
    String _ID_MAIN = "_id_main";

    @DataType(DataType.Type.TEXT)
    String IMAGE_PATH = "image_path";

    @DataType(DataType.Type.TEXT)
    String VIDEO_PATH = "video_path";

    @DataType(DataType.Type.TEXT)
    String AUDIO_PATH = "audio_path";
}
