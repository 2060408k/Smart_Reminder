package highway62.reminderapp.daos;

import android.provider.BaseColumns;

/**
 * Created by Highway62 on 25/09/2016.
 */
public class WearDBContract {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "ApplTreeWearDB.db";

    public static abstract class ReminderTable implements BaseColumns {
        public static final String TABLE_NAME = "reminders";
        public static final String COLUMN_DATE_TIME = "date_time";
        public static final String COLUMN_DATETIME_SET = "datetime_set";
        public static final String COLUMN_AFTER_TIME = "after_time";
        public static final String COLUMN_AFTER_SCALE = "after_scale";
        public static final String COLUMN_AUDIO = "audio";
    }
}
