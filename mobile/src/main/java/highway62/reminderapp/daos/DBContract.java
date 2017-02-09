package highway62.reminderapp.daos;

import android.provider.BaseColumns;

/**
 * Defines the details of the database tables
 */
public class DBContract {

    public static final int DB_VERSION = 8;
    public static final String DB_NAME = "ApplTreeDB.db";

    public static abstract class ReminderTable implements BaseColumns{
        public static final String TABLE_NAME = "reminders";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_EVENT_AFTER = "event_after";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_DATE_TIME = "date_time";
        public static final String COLUMN_DURATION_SET = "duration_set";
        public static final String COLUMN_DURATION_TIME = "duration_time";
        public static final String COLUMN_DURATION_SCALE = "duration_scale";
        public static final String COLUMN_NOTIFICATION_TIME = "notification_time";
        public static final String COLUMN_NOTIFICATION_SCALE = "notification_scale";
        public static final String COLUMN_NOTES = "notes";
        public static final String COLUMN_REPEATING = "repeating";
        public static final String COLUMN_REPETITION_WEEKS = "repetition_weeks";
        public static final String COLUMN_PROMPT_LEVEL = "prompt_level";
        public static final String COLUMN_EVENT_TYPE = "event_type";
        public static final String COLUMN_REMINDER_TYPE = "reminder_type";
        public static final String COLUMN_PATTERN_TYPE = "reminder_pattern";
        public static final String COLUMN_SMART_REMINDED = "smart_reminded";
        public static final String COLUMN_LOG_ID = "log_id";

    }

    public static abstract class ReminderDayTable{
        public static final String TABLE_NAME = "reminder_days";
        public static final String COLUMN_REMINDER_ID = "reminder_id";
        public static final String COLUMN_DAY = "reminder_day";
    }

    // LOGGING /////////////////////////////////////////////////////////////////////////////////////

    public static abstract class ReminderLogTable implements BaseColumns{
        public static final String TABLE_NAME = "reminder_logs";
        public static final String COLUMN_DATE_TIME = "date_time";
        public static final String COLUMN_REPEATING = "repeating";
        public static final String COLUMN_REPETITION_WEEKS = "repetition_weeks";
        public static final String COLUMN_NOTES = "notes";
        public static final String COLUMN_PROMPT_LEVEL= "prompt_level";
        public static final String COLUMN_EVENT_TYPE= "event_type";
        public static final String COLUMN_REMINDER_TYPE= "reminder_type";
        public static final String COLUMN_PATTERN_TYPE = "reminder_pattern";
        public static final String COLUMN_PROMPT_RESPONDED = "prompt_responded";
        public static final String COLUMN_DATETIME_SET = "datetime_set";
    }

    public static abstract class ReminderDayLogTable{
        public static final String TABLE_NAME = "reminder_days_log";
        public static final String COLUMN_REMINDER_ID = "reminder_id";
        public static final String COLUMN_DAY = "reminder_day";
    }
}
