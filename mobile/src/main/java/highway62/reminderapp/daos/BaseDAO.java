package highway62.reminderapp.daos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Creates or upgrades the database table
 */
public class BaseDAO extends SQLiteOpenHelper {

    public BaseDAO(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_REMINDER_TABLE =
                "CREATE TABLE " + DBContract.ReminderTable.TABLE_NAME
                        + "("
                        + DBContract.ReminderTable._ID + " INTEGER PRIMARY KEY NOT NULL,"
                        + DBContract.ReminderTable.COLUMN_TITLE + " TEXT,"
                        + DBContract.ReminderTable.COLUMN_EVENT_AFTER + " TEXT,"
                        + DBContract.ReminderTable.COLUMN_LOCATION + " TEXT,"
                        + DBContract.ReminderTable.COLUMN_DATE_TIME + " INTEGER DEFAULT -1,"
                        + DBContract.ReminderTable.COLUMN_DURATION_SET + " INTEGER DEFAULT 0,"
                        + DBContract.ReminderTable.COLUMN_DURATION_TIME + " INTEGER DEFAULT -1,"
                        + DBContract.ReminderTable.COLUMN_DURATION_SCALE + " TEXT,"
                        + DBContract.ReminderTable.COLUMN_NOTIFICATION_TIME + " INTEGER DEFAULT -1,"
                        + DBContract.ReminderTable.COLUMN_NOTIFICATION_SCALE + " TEXT,"
                        + DBContract.ReminderTable.COLUMN_NOTES + " TEXT,"
                        + DBContract.ReminderTable.COLUMN_REPEATING + " INTEGER DEFAULT 0,"
                        + DBContract.ReminderTable.COLUMN_REPETITION_WEEKS + " INTEGER DEFAULT -1,"
                        + DBContract.ReminderTable.COLUMN_PROMPT_LEVEL + " INTEGER DEFAULT 0,"
                        + DBContract.ReminderTable.COLUMN_EVENT_TYPE + " TEXT,"
                        + DBContract.ReminderTable.COLUMN_LOG_ID + " INTEGER DEFAULT 0,"
                        + DBContract.ReminderTable.COLUMN_REMINDER_TYPE + " TEXT"
                        + ")";

        String CREATE_REMINDER_DAYS_TABLE =
                "CREATE TABLE " + DBContract.ReminderDayTable.TABLE_NAME
                        + "("
                        + DBContract.ReminderDayTable.COLUMN_REMINDER_ID + " INTEGER REFERENCES "
                        + DBContract.ReminderTable.TABLE_NAME + "("
                        + DBContract.ReminderTable._ID + ") ON UPDATE CASCADE ON DELETE CASCADE,"
                        + DBContract.ReminderDayTable.COLUMN_DAY + " TEXT NOT NULL"
                        + ")";

        String CREATE_REMINDER_LOG_TABLE =
                "CREATE TABLE " + DBContract.ReminderLogTable.TABLE_NAME
                        + "("
                        + DBContract.ReminderLogTable._ID + " INTEGER PRIMARY KEY NOT NULL,"
                        + DBContract.ReminderLogTable.COLUMN_DATE_TIME + " INTEGER DEFAULT -1,"
                        + DBContract.ReminderLogTable.COLUMN_REPEATING + " INTEGER DEFAULT 0,"
                        + DBContract.ReminderLogTable.COLUMN_REPETITION_WEEKS + " INTEGER DEFAULT -1,"
                        + DBContract.ReminderLogTable.COLUMN_NOTES + " TEXT,"
                        + DBContract.ReminderLogTable.COLUMN_PROMPT_LEVEL + " INTEGER DEFAULT 0,"
                        + DBContract.ReminderLogTable.COLUMN_EVENT_TYPE + " TEXT,"
                        + DBContract.ReminderLogTable.COLUMN_REMINDER_TYPE + " TEXT,"
                        + DBContract.ReminderLogTable.COLUMN_PROMPT_RESPONDED + " INTEGER DEFAULT 0,"
                        + DBContract.ReminderLogTable.COLUMN_DATETIME_SET + " INTEGER DEFAULT -1"
                        + ")";

        String CREATE_REMINDER_DAYS_LOG_TABLE =
                "CREATE TABLE " + DBContract.ReminderDayLogTable.TABLE_NAME
                        + "("
                        + DBContract.ReminderDayLogTable.COLUMN_REMINDER_ID + " INTEGER REFERENCES "
                        + DBContract.ReminderLogTable.TABLE_NAME + "("
                        + DBContract.ReminderLogTable._ID + ") ON UPDATE CASCADE ON DELETE CASCADE,"
                        + DBContract.ReminderDayLogTable.COLUMN_DAY + " TEXT NOT NULL"
                        + ")";

        db.execSQL(CREATE_REMINDER_TABLE);
        db.execSQL(CREATE_REMINDER_DAYS_TABLE);
        db.execSQL(CREATE_REMINDER_LOG_TABLE);
        db.execSQL(CREATE_REMINDER_DAYS_LOG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion > oldVersion){
            db.execSQL("DROP TABLE IF EXISTS " + DBContract.ReminderTable.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + DBContract.ReminderDayTable.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + DBContract.ReminderLogTable.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + DBContract.ReminderDayLogTable.TABLE_NAME);
            onCreate(db);
        }
    }
}
