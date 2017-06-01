package com.simplicity.anuj.myday.Testing;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.simplicity.anuj.myday.Data.JournalContentProvider;
import com.simplicity.anuj.myday.Utility.Utils;

import java.util.Random;

/**
 * Created by anujc on 28-05-2017.
 */

public class FakeDataForJournal {
    private Context context;
    private Random random = new Random();
    String data = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer dignissim iaculis risus, quis dignissim ex " +
            "dignissim vel. Vestibulum et faucibus orci, non ultricies ligula. Maecenas nulla dolor, feugiat eget sollicitudin" +
            " vel, interdum eget ante. Etiam tempus, turpis ac tempus pellentesque, mauris purus posuere purus, non cursus augue " +
            "sapien egestas dolor. Ut molestie porta erat vitae efficitur. Ut enim tellus, vestibulum sit amet iaculis et, imperdiet" +
            " in dui. Suspendisse rutrum metus urna, et vehicula lorem iaculis nec. Maecenas fermentum elit nisl, ac commodo lectus " +
            "sodales sed. Etiam dapibus nibh mattis semper suscipit. Suspendisse potenti. Donec mollis purus nec varius porta." +
            " Mauris ac enim placerat, venenatis eros id, fermentum tellus." +
            "Praesent accumsan, sapien nec fermentum rhoncus, augue risus volutpat velit, in laoreet ligula nibh vitae erat. " +
            "Nunc dui metus, consectetur sed lorem nec, viverra accumsan mi. Phasellus consectetur enim in ligula elementum " +
            "placerat. Nunc non risus ex. Nullam tellus augue, pretium ut ante vitae, posuere vulputate tortor. Donec interdum massa " +
            "nec vestibulum auctor. Vivamus varius, ante quis maximus accumsan, risus erat porttitor nunc, sit amet laoreet nibh " +
            "metus vel purus. Ut fermentum finibus urna, eu gravida diam viverra pharetra. Aenean vestibulum elit quis molestie" +
            " tristique. Aliquam tincidunt elit eget felis convallis vehicula. Nullam vulputate, ex ac faucibus tincidunt, lacus " +
            "erat consectetur magna, ut mollis augue nunc at mauris. Sed condimentum sapien et nulla interdum, quis semper arcu" +
            " accumsan. Ut tincidunt sem quis dui bibendum euismod ut sit amet ligula. Morbi placerat finibus nulla, sit amet " +
            "luctus libero egestas sed. Duis nisi lacus, imperdiet vestibulum efficitur id, aliquam nec justo." +
            "Sed convallis sem ut enim rhoncus volutpat. Etiam id diam non arcu ullamcorper vestibulum. Aenean nec leo lectus. " +
            "Nunc cursus purus in nibh auctor suscipit. Suspendisse nibh diam, sodales sed dignissim et, dignissim ut mi.";

    public FakeDataForJournal(Context context) {
        this.context = context;
        insertFakeDatac();
    }

    private void insertFakeDatac() {


        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                ContentValues[] values = new ContentValues[70];
                for (int i = 0; i < 70; i++) {
                    ContentValues v1 = new ContentValues();
                    v1.put(Utils.TITLE_JOURNAL, "Journal Entry");
                    v1.put(Utils.ENTRY_JOURNAL, getEntry());
                    v1.put(Utils.DATE_CREATED_JOURNAL, getDate(i));
                    v1.put(Utils.TIME_CREATED_JOURNAL, "21:36:12 PM");
                    v1.put(Utils.DATE_MODIFIED_JOURNAL, getDate(i));
                    v1.put(Utils.TIME_MODIFIED_JOURNAL, "21:36:12 PM");
                    v1.put(Utils.HAS_LOCATION_JOURNAL, -1);
                    v1.put(Utils.HAS_WEATHER_JOURNAL, -1);
                    values[i] = v1;
                }
                context.getContentResolver().delete(JournalContentProvider.ContentProviderCreator.JOURNAL, null, null);
                context.getContentResolver().bulkInsert(JournalContentProvider.ContentProviderCreator.JOURNAL, values);
                return null;
            }
        }.execute();

    }

    private String getDate(int index) {
        String[] date = {
                "1-July-2016",
                "2-July-2016",
                "3-July-2016",
                "4-July-2016",
                "4-July-2016",
                "4-July-2016",
                "5-July-2016",
                "6-July-2016",
                "12-July-2016",
                "12-July-2016",
                "12-July-2016",
                "21-August-2016",
                "22-August-2016",
                "23-August-2016",
                "23-August-2016",
                "22-September-2016",
                "1-October-2016",
                "2-October-2016",
                "3-October-2016",
                "3-October-2016",
                "3-October-2016",
                "21-October-2016",
                "22-October-2016",
                "23-October-2016",
                "23-October-2016",
                "1-November-2016",
                "4-November-2016",
                "1-December-2016",
                "2-December-2016",
                "3-December-2016",
                "5-December-2016",
                "6-December-2016",
                "6-December-2016",
                "7-December-2016",
                "8-December-2016",
                "8-December-2016",
                "8-December-2016",
                "8-December-2016",
                "13-December-2016",
                "15-December-2016",
                "27-December-2016",
                "27-December-2016",
                "31-December-2016",
                "31-December-2016",
                "1-January-2017",
                "4-January-2017",
                "10-January-2017",
                "15-January-2017",
                "21-January-2017",
                "26-January-2017",
                "01-February-2017",
                "1-March-2017",
                "4-March-2017",
                "10-March-2017",
                "15-March-2017",
                "21-March-2017",
                "26-March-2017",
                "1-April-2017",
                "2-April-2017",
                "2-April-2017",
                "2-April-2017",
                "3-April-2017",
                "3-April-2017",
                "4-April-2017",
                "10-April-2017",
                "15-April-2017",
                "21-April-2017",
                "26-April-2017",
                "26-April-2017",
                "26-April-2017",
                "26-April-2017",
                "26-April-2017",
                "1-May-2017",
                "4-May-2017",
                "10-May-2017",
                "15-May-2017",
                "21-May-2017",
                "26-May-2017"
        };
        return date[index];
    }

    private String getEntry() {
        int i = random.nextInt(1000);
        return data.substring(i, i + 500);
    }

    private String getTitle() {
        int i = random.nextInt(500);
        return data.substring(i, i + 50);
    }
}
