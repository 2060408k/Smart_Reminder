package highway62.reminderapp.daos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Highway62 on 25/09/2016.
 */
public class WearBaseDAO extends SQLiteOpenHelper {

    public WearBaseDAO(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_REMINDER_TABLE =
                "CREATE TABLE " + WearDBContract.ReminderTable.TABLE_NAME
                        + "("
                        + WearDBContract.ReminderTable._ID + " INTEGER PRIMARY KEY NOT NULL,"
                        + WearDBContract.ReminderTable.COLUMN_DATE_TIME + " INTEGER,"
                        + WearDBContract.ReminderTable.COLUMN_DATETIME_SET + " INTEGER,"
                        + WearDBContract.ReminderTable.COLUMN_AFTER_TIME + " INTEGER DEFAULT -1,"
                        + WearDBContract.ReminderTable.COLUMN_AFTER_SCALE + " TEXT,"
                        + WearDBContract.ReminderTable.COLUMN_AUDIO + " BLOB"
                        + ")";

        db.execSQL(CREATE_REMINDER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion > oldVersion){
            db.execSQL("DROP TABLE IF EXISTS " + WearDBContract.ReminderTable.TABLE_NAME);
            onCreate(db);
        }
    }
}
