package highway62.reminderapp.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import org.joda.time.DateTime;

import java.util.ArrayList;

import highway62.reminderapp.constants.Day;
import highway62.reminderapp.constants.DurationScale;
import highway62.reminderapp.constants.EventType;
import highway62.reminderapp.constants.NotificationScale;
import highway62.reminderapp.constants.ReminderPattern;
import highway62.reminderapp.constants.ReminderType;
import highway62.reminderapp.reminders.BaseLogReminder;
import highway62.reminderapp.reminders.BaseReminder;
import highway62.reminderapp.reminders.PromptLogReminder;

/**
 * Handles all database CRUD operations for reminders
 */
public class ReminderDAO extends BaseDAO {

    public ReminderDAO(Context context) {
        super(context, DBContract.DB_NAME, null, DBContract.DB_VERSION);
    }

    public long addReminder(BaseReminder reminder) {

        ContentValues values = new ContentValues();
        ContentValues values_log = new ContentValues();

        if (reminder.getTitle() != null) {
            values.put(DBContract.ReminderTable.COLUMN_TITLE, reminder.getTitle());
        }

        if (reminder.getEventAfter() != null) {
            values.put(DBContract.ReminderTable.COLUMN_EVENT_AFTER, reminder.getEventAfter());
        }

        if (reminder.getLocation() != null) {
            values.put(DBContract.ReminderTable.COLUMN_LOCATION, reminder.getLocation());
        }

        if (reminder.getDateTime() != -1) {
            values.put(DBContract.ReminderTable.COLUMN_DATE_TIME, reminder.getDateTime());
            values_log.put(DBContract.ReminderLogTable.COLUMN_DATE_TIME, reminder.getDateTime());
        }

        if (reminder.isDurationSet()) {
            values.put(DBContract.ReminderTable.COLUMN_DURATION_SET, 1);
        }

        if (reminder.getSmartReminded()) {
            values.put(DBContract.ReminderTable.COLUMN_SMART_REMINDED, 1);
        }

        if (reminder.getEventDurationTime() != -1) {
            values.put(DBContract.ReminderTable.COLUMN_DURATION_TIME, reminder.getEventDurationTime());
        }

        if (reminder.getEventDurationScale() != null) {
            values.put(DBContract.ReminderTable.COLUMN_DURATION_SCALE, reminder.getEventDurationScale().name());
        }

        if (reminder.getNotificationTime() != -1) {
            values.put(DBContract.ReminderTable.COLUMN_NOTIFICATION_TIME, reminder.getNotificationTime());
        }

        if (reminder.getNotificationScale() != null) {
            values.put(DBContract.ReminderTable.COLUMN_NOTIFICATION_SCALE, reminder.getNotificationScale().name());
        }

        if (reminder.getNotes() != null) {
            values.put(DBContract.ReminderTable.COLUMN_NOTES, reminder.getNotes());
            if (reminder.getReminderType().equals(ReminderType.PROMPT)) {
                values_log.put(DBContract.ReminderLogTable.COLUMN_NOTES, reminder.getNotes());
            }
        }

        if (reminder.isRepeating()) {
            values.put(DBContract.ReminderTable.COLUMN_REPEATING, 1);
            values_log.put(DBContract.ReminderLogTable.COLUMN_REPEATING, 1);
        }

        if (reminder.getRepetitionWeeks() != -1) {
            values.put(DBContract.ReminderTable.COLUMN_REPETITION_WEEKS, reminder.getRepetitionWeeks());
            values_log.put(DBContract.ReminderLogTable.COLUMN_REPETITION_WEEKS, reminder.getRepetitionWeeks());
        }

        if (reminder.getPromptLevel() != -1) {
            values.put(DBContract.ReminderTable.COLUMN_PROMPT_LEVEL, reminder.getPromptLevel());
            values_log.put(DBContract.ReminderLogTable.COLUMN_PROMPT_LEVEL, reminder.getPromptLevel());
        }

        if (reminder.getType() != null) {
            values.put(DBContract.ReminderTable.COLUMN_EVENT_TYPE, reminder.getType().name());
            values_log.put(DBContract.ReminderLogTable.COLUMN_EVENT_TYPE, reminder.getType().name());
        }

        if (reminder.getReminderType() != null) {
            values.put(DBContract.ReminderTable.COLUMN_REMINDER_TYPE, reminder.getReminderType().name());
            values_log.put(DBContract.ReminderLogTable.COLUMN_REMINDER_TYPE, reminder.getReminderType().name());
        }

        if (reminder.getPattern() != null) {
            values.put(DBContract.ReminderTable.COLUMN_PATTERN_TYPE, reminder.getPattern().name());
            values_log.put(DBContract.ReminderLogTable.COLUMN_PATTERN_TYPE, reminder.getPattern().name());
        }

        // Add time reminder was set to the log
        DateTime dtSet = new DateTime()
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        values_log.put(DBContract.ReminderLogTable.COLUMN_DATETIME_SET, dtSet.getMillis());

        SQLiteDatabase db = this.getWritableDatabase();

        // LOG REMINDERS
        long reminder_log_ID = db.insert(DBContract.ReminderLogTable.TABLE_NAME, null, values_log);
        reminder.setLogID(reminder_log_ID);
        values.put(DBContract.ReminderTable.COLUMN_LOG_ID, reminder.getLogID());

        long reminder_ID = db.insert(DBContract.ReminderTable.TABLE_NAME, null, values);


        if (reminder_ID < 0) {
            db.close();
            return reminder_ID;
        } else {
            // Add Reminder Days to Reminder Days Table
            if (reminder.getRepetitionDays() != null) {
                for (Day d : reminder.getRepetitionDays()) {

                    ContentValues dayValues = new ContentValues();
                    dayValues.put(DBContract.ReminderDayTable.COLUMN_REMINDER_ID, reminder_ID);
                    dayValues.put(DBContract.ReminderDayTable.COLUMN_DAY, d.name());

                    // Logging
                    ContentValues dayValuesLog = new ContentValues();
                    dayValuesLog.put(DBContract.ReminderDayLogTable.COLUMN_REMINDER_ID, reminder_log_ID);
                    dayValuesLog.put(DBContract.ReminderDayLogTable.COLUMN_DAY, d.name());

                    db.insert(DBContract.ReminderDayTable.TABLE_NAME, null, dayValues);
                    db.insert(DBContract.ReminderDayLogTable.TABLE_NAME, null, dayValuesLog);

                }
            }
            db.close();
            return reminder_ID;
        }
    }

    public BaseReminder getReminder(long reminder_ID) {

        // Columns to return
        String[] projection = {
                DBContract.ReminderTable._ID,
                DBContract.ReminderTable.COLUMN_TITLE,
                DBContract.ReminderTable.COLUMN_EVENT_AFTER,
                DBContract.ReminderTable.COLUMN_LOCATION,
                DBContract.ReminderTable.COLUMN_DATE_TIME,
                DBContract.ReminderTable.COLUMN_DURATION_SET,
                DBContract.ReminderTable.COLUMN_DURATION_TIME,
                DBContract.ReminderTable.COLUMN_DURATION_SCALE,
                DBContract.ReminderTable.COLUMN_NOTIFICATION_TIME,
                DBContract.ReminderTable.COLUMN_NOTIFICATION_SCALE,
                DBContract.ReminderTable.COLUMN_NOTES,
                DBContract.ReminderTable.COLUMN_REPEATING,
                DBContract.ReminderTable.COLUMN_REPETITION_WEEKS,
                DBContract.ReminderTable.COLUMN_PROMPT_LEVEL,
                DBContract.ReminderTable.COLUMN_EVENT_TYPE,
                DBContract.ReminderTable.COLUMN_REMINDER_TYPE,
                DBContract.ReminderTable.COLUMN_PATTERN_TYPE,
                DBContract.ReminderTable.COLUMN_SMART_REMINDED,
                DBContract.ReminderTable.COLUMN_LOG_ID
        };

        // Columns for the where clause
        String selection = DBContract.ReminderTable._ID + "=?";

        // Values for the where clause
        String[] selectionArgs = {
                String.valueOf(reminder_ID)
        };


        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                DBContract.ReminderTable.TABLE_NAME,
                projection,     // columns to return
                selection,      // columns for where clause
                selectionArgs,  // values for where clause
                null,           // group by
                null,           // having
                null);          // order by

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            BaseReminder reminder;
            long rID = cursor.getInt(cursor.getColumnIndex(DBContract.ReminderTable._ID));
            if (rID < 0) {
                return null;
            } else {
                reminder = new BaseReminder(rID);
            }

            if (!cursor.isNull(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_TITLE))) {
                reminder.setTitle(cursor.getString(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_TITLE)));
            }

            if (!cursor.isNull(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_EVENT_AFTER))) {
                reminder.setEventAfter(cursor.getString(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_EVENT_AFTER)));
            }

            if (!cursor.isNull(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_LOCATION))) {
                reminder.setLocation(cursor.getString(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_LOCATION)));
            }

            if (cursor.getLong(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_DATE_TIME)) >= 0) {
                reminder.setDateTime(cursor.getLong(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_DATE_TIME)));
            }

            if (cursor.getInt(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_DURATION_SET)) > 0) {
                reminder.setDurationSet(true);
            }

            if (cursor.getInt(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_SMART_REMINDED)) > 0 ){
                reminder.setSmartReminded(true);
            }

            if (cursor.getInt(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_SMART_REMINDED)) > 0) {
                reminder.setSmartReminded(true);
            }

            if (cursor.getInt(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_DURATION_TIME)) >= 0) {
                reminder.setEventDurationTime(cursor.getInt(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_DURATION_TIME)));
            }

            if (!cursor.isNull(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_DURATION_SCALE))) {
                reminder.setEventDurationScale(DurationScale.valueOf(cursor.getString(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_DURATION_SCALE))));
            }

            if (cursor.getLong(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_NOTIFICATION_TIME)) >= 0) {
                reminder.setNotificationTime(cursor.getInt(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_NOTIFICATION_TIME)));
            }

            if (!cursor.isNull(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_NOTIFICATION_SCALE))) {
                reminder.setNotificationScale(NotificationScale.valueOf(cursor.getString(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_NOTIFICATION_SCALE))));
            }

            if (!cursor.isNull(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_NOTES))) {
                reminder.setNotes(cursor.getString(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_NOTES)));
            }

            if (cursor.getInt(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_REPEATING)) > 0) {
                reminder.setRepeating(true);
            }

            if (cursor.getInt(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_REPETITION_WEEKS)) >= 0) {
                reminder.setRepetitionWeeks(cursor.getInt(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_REPETITION_WEEKS)));
            }

            if (cursor.getInt(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_PROMPT_LEVEL)) >= 0) {
                reminder.setPromptLevel(cursor.getInt(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_PROMPT_LEVEL)));
            }

            if (!cursor.isNull(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_EVENT_TYPE))) {
                reminder.setType(EventType.valueOf(cursor.getString(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_EVENT_TYPE))));
            }

            if (!cursor.isNull(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_REMINDER_TYPE))) {
                reminder.setReminderType(ReminderType.valueOf(cursor.getString(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_REMINDER_TYPE))));
            }

            if (!cursor.isNull(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_PATTERN_TYPE))) {
                reminder.setPattern(ReminderPattern.valueOf(cursor.getString(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_PATTERN_TYPE))));
            }

            if (cursor.getLong(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_LOG_ID)) >= 0) {
                reminder.setLogID(cursor.getLong(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_LOG_ID)));
            }

            cursor.close();


            // Get Reminder Days ---------------------->
            // Columns to return
            String[] day_projection = {
                    DBContract.ReminderDayTable.COLUMN_DAY
            };

            // Columns for the where clause
            String day_selection = DBContract.ReminderDayTable.COLUMN_REMINDER_ID + "=?";

            // Values for the where clause
            String[] day_selectionArgs = {
                    String.valueOf(reminder_ID)
            };

            Cursor day_cursor = db.query(
                    DBContract.ReminderDayTable.TABLE_NAME,
                    day_projection,     // columns to return
                    day_selection,      // columns for where clause
                    day_selectionArgs,  // values for where clause
                    null,           // group by
                    null,           // having
                    null);          // order by

            if (day_cursor != null && day_cursor.getCount() > 0) {
                ArrayList<String> dayList = new ArrayList<>();

                day_cursor.moveToFirst();
                do {
                    dayList.add(day_cursor.getString(day_cursor.getColumnIndex(DBContract.ReminderDayTable.COLUMN_DAY)));
                } while (day_cursor.moveToNext());
                day_cursor.close();
                db.close();

                if (dayList.size() > 0) {
                    Day[] repeat_days = new Day[dayList.size()];
                    for (int i = 0; i < dayList.size(); i++) {
                        repeat_days[i] = Day.valueOf(dayList.get(i));
                    }
                    // Set the reminder's repeat days
                    reminder.setRepetitionDays(repeat_days);
                }
            } else {
                db.close();
            }

            return reminder;

        } else {
            db.close();
            return null;
        }
    }

    public ArrayList<BaseReminder> getAllReminders() {
        ArrayList<BaseReminder> reminderList = new ArrayList<>();

        // Columns to return
        String[] projection = {
                DBContract.ReminderTable._ID,
                DBContract.ReminderTable.COLUMN_TITLE,
                DBContract.ReminderTable.COLUMN_EVENT_AFTER,
                DBContract.ReminderTable.COLUMN_LOCATION,
                DBContract.ReminderTable.COLUMN_DATE_TIME,
                DBContract.ReminderTable.COLUMN_DURATION_SET,
                DBContract.ReminderTable.COLUMN_DURATION_TIME,
                DBContract.ReminderTable.COLUMN_DURATION_SCALE,
                DBContract.ReminderTable.COLUMN_NOTIFICATION_TIME,
                DBContract.ReminderTable.COLUMN_NOTIFICATION_SCALE,
                DBContract.ReminderTable.COLUMN_NOTES,
                DBContract.ReminderTable.COLUMN_REPEATING,
                DBContract.ReminderTable.COLUMN_REPETITION_WEEKS,
                DBContract.ReminderTable.COLUMN_PROMPT_LEVEL,
                DBContract.ReminderTable.COLUMN_EVENT_TYPE,
                DBContract.ReminderTable.COLUMN_REMINDER_TYPE,
                DBContract.ReminderTable.COLUMN_PATTERN_TYPE,
                DBContract.ReminderTable.COLUMN_SMART_REMINDED,
                DBContract.ReminderTable.COLUMN_LOG_ID
        };

        SQLiteDatabase db = this.getReadableDatabase();

        // Columns for the where clause
        String selection = DBContract.ReminderTable.COLUMN_REMINDER_TYPE + "=?";

        // Values for the where clause
        String[] selectionArgs = {
                ReminderType.REMINDER.name()
        };

        Cursor cursor = db.query(
                DBContract.ReminderTable.TABLE_NAME,
                projection,     // columns to return
                selection,      // columns for where clause
                selectionArgs,  // values for where clause
                null,           // group by
                null,           // having
                null);          // order by

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                BaseReminder reminder = new BaseReminder(cursor.getInt(cursor.getColumnIndex(DBContract.ReminderTable._ID)));

                if (!cursor.isNull(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_TITLE))) {
                    reminder.setTitle(cursor.getString(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_TITLE)));
                }

                if (!cursor.isNull(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_EVENT_AFTER))) {
                    reminder.setEventAfter(cursor.getString(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_EVENT_AFTER)));
                }

                if (!cursor.isNull(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_LOCATION))) {
                    reminder.setLocation(cursor.getString(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_LOCATION)));
                }

                if (cursor.getLong(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_DATE_TIME)) >= 0) {
                    reminder.setDateTime(cursor.getLong(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_DATE_TIME)));
                }

                if (cursor.getInt(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_DURATION_SET)) > 0) {
                    reminder.setDurationSet(true);
                }

                if (cursor.getInt(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_SMART_REMINDED)) >0 ){
                    reminder.setSmartReminded(true);
                }

                if (cursor.getInt(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_DURATION_TIME)) >= 0) {
                    reminder.setEventDurationTime(cursor.getInt(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_DURATION_TIME)));
                }

                if (!cursor.isNull(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_DURATION_SCALE))) {
                    reminder.setEventDurationScale(DurationScale.valueOf(cursor.getString(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_DURATION_SCALE))));
                }

                if (cursor.getLong(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_NOTIFICATION_TIME)) >= 0) {
                    reminder.setNotificationTime(cursor.getInt(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_NOTIFICATION_TIME)));
                }

                if (!cursor.isNull(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_NOTIFICATION_SCALE))) {
                    reminder.setNotificationScale(NotificationScale.valueOf(cursor.getString(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_NOTIFICATION_SCALE))));
                }

                if (!cursor.isNull(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_NOTES))) {
                    reminder.setNotes(cursor.getString(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_NOTES)));
                }

                if (cursor.getInt(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_REPEATING)) > 0) {
                    reminder.setRepeating(true);
                }

                if (cursor.getInt(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_REPETITION_WEEKS)) >= 0) {
                    reminder.setRepetitionWeeks(cursor.getInt(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_REPETITION_WEEKS)));
                }

                if (cursor.getInt(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_PROMPT_LEVEL)) >= 0) {
                    reminder.setPromptLevel(cursor.getInt(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_PROMPT_LEVEL)));
                }

                if (!cursor.isNull(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_EVENT_TYPE))) {
                    reminder.setType(EventType.valueOf(cursor.getString(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_EVENT_TYPE))));
                }

                if (!cursor.isNull(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_REMINDER_TYPE))) {
                    reminder.setReminderType(ReminderType.valueOf(cursor.getString(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_REMINDER_TYPE))));
                }

                if (!cursor.isNull(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_PATTERN_TYPE))) {
                    reminder.setPattern(ReminderPattern.valueOf(cursor.getString(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_PATTERN_TYPE))));
                }

                if (cursor.getLong(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_LOG_ID)) >= 0) {
                    reminder.setLogID(cursor.getLong(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_LOG_ID)));
                }

                // Get Reminder Days ---------------------->
                // Columns to return
                String[] day_projection = {
                        DBContract.ReminderDayTable.COLUMN_DAY
                };

                // Columns for the where clause
                String day_selection = DBContract.ReminderDayTable.COLUMN_REMINDER_ID + "=?";

                // Values for the where clause
                String[] day_selectionArgs = {
                        String.valueOf(reminder.getId())
                };

                Cursor day_cursor = db.query(
                        DBContract.ReminderDayTable.TABLE_NAME,
                        day_projection,     // columns to return
                        day_selection,      // columns for where clause
                        day_selectionArgs,  // values for where clause
                        null,           // group by
                        null,           // having
                        null);          // order by

                if (day_cursor != null && day_cursor.getCount() > 0) {
                    ArrayList<String> dayList = new ArrayList<>();

                    day_cursor.moveToFirst();
                    do {
                        dayList.add(day_cursor.getString(day_cursor.getColumnIndex(DBContract.ReminderDayTable.COLUMN_DAY)));
                    } while (day_cursor.moveToNext());
                    day_cursor.close();

                    if (dayList.size() > 0) {
                        Day[] repeat_days = new Day[dayList.size()];
                        for (int i = 0; i < dayList.size(); i++) {
                            repeat_days[i] = Day.valueOf(dayList.get(i));
                        }
                        // Set the reminder's repeat days
                        reminder.setRepetitionDays(repeat_days);
                    }
                }

                // Add reminder to the list
                reminderList.add(reminder);

            } while (cursor.moveToNext());
            cursor.close();
            db.close();
        } else {
            db.close();
        }

        return reminderList;
    }

    public ArrayList<BaseReminder> getAllPrompts() {
        ArrayList<BaseReminder> promptList = new ArrayList<>();

        // Columns to return
        String[] projection = {
                DBContract.ReminderTable._ID,
                DBContract.ReminderTable.COLUMN_DATE_TIME,
                DBContract.ReminderTable.COLUMN_NOTES,
                DBContract.ReminderTable.COLUMN_PROMPT_LEVEL,
                DBContract.ReminderTable.COLUMN_LOG_ID,
                DBContract.ReminderTable.COLUMN_REMINDER_TYPE,
                DBContract.ReminderTable.COLUMN_PATTERN_TYPE
        };

        // Columns for the where clause
        String selection = DBContract.ReminderLogTable.COLUMN_REMINDER_TYPE + "=?";

        // Values for the where clause
        String[] selectionArgs = {
                ReminderType.PROMPT.name()
        };

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                DBContract.ReminderTable.TABLE_NAME,
                projection,     // columns to return
                selection,      // columns for where clause
                selectionArgs,  // values for where clause
                null,           // group by
                null,           // having
                null);          // order by

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                BaseReminder reminder = new BaseReminder(cursor.getInt(cursor.getColumnIndex(DBContract.ReminderTable._ID)));

                if (cursor.getLong(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_DATE_TIME)) >= 0) {
                    reminder.setDateTime(cursor.getLong(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_DATE_TIME)));
                }

                if (!cursor.isNull(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_NOTES))) {
                    reminder.setNotes(cursor.getString(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_NOTES)));
                }

                if (cursor.getInt(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_PROMPT_LEVEL)) >= 0) {
                    reminder.setPromptLevel(cursor.getInt(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_PROMPT_LEVEL)));
                }

                if (cursor.getLong(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_LOG_ID)) >= 0) {
                    reminder.setLogID(cursor.getLong(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_LOG_ID)));
                }

                if (!cursor.isNull(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_REMINDER_TYPE))) {
                    reminder.setReminderType(ReminderType.valueOf(cursor.getString(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_REMINDER_TYPE))));
                }

                if (!cursor.isNull(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_PATTERN_TYPE))) {
                    reminder.setPattern(ReminderPattern.valueOf(cursor.getString(cursor.getColumnIndex(DBContract.ReminderTable.COLUMN_PATTERN_TYPE))));
                }

                // Add reminder to the list
                promptList.add(0,reminder);

            } while (cursor.moveToNext());
            cursor.close();
            db.close();
        } else {
            db.close();
        }

        return promptList;
    }

    public void updateReminder(BaseReminder reminder) {

        SQLiteDatabase db = this.getWritableDatabase();
        String selection = DBContract.ReminderTable._ID + "=?";
        String[] selectionArgs = {String.valueOf(reminder.getId())};

        // REMINDER VALUES TO UPDATE
        ContentValues values = new ContentValues();
        if (reminder.getTitle() != null) {
            values.put(DBContract.ReminderTable.COLUMN_TITLE, reminder.getTitle());
        } else {
            values.putNull(DBContract.ReminderTable.COLUMN_TITLE);
        }

        if (reminder.getEventAfter() != null) {
            values.put(DBContract.ReminderTable.COLUMN_EVENT_AFTER, reminder.getEventAfter());
        } else {
            values.putNull(DBContract.ReminderTable.COLUMN_EVENT_AFTER);
        }

        if (reminder.getLocation() != null) {
            values.put(DBContract.ReminderTable.COLUMN_LOCATION, reminder.getLocation());
        } else {
            values.putNull(DBContract.ReminderTable.COLUMN_LOCATION);
        }

        if (reminder.getDateTime() != -1) {
            values.put(DBContract.ReminderTable.COLUMN_DATE_TIME, reminder.getDateTime());
        } else {
            values.put(DBContract.ReminderTable.COLUMN_DATE_TIME, -1);
        }

        if (reminder.isDurationSet()) {
            values.put(DBContract.ReminderTable.COLUMN_DURATION_SET, 1);
        } else {
            values.put(DBContract.ReminderTable.COLUMN_DURATION_SET, 0);
        }

        if (reminder.getSmartReminded()) {
           values.put(DBContract.ReminderTable.COLUMN_SMART_REMINDED,1);
        } else {
            values.put(DBContract.ReminderTable.COLUMN_SMART_REMINDED,0);
        }

        if (reminder.getEventDurationTime() != -1) {
            values.put(DBContract.ReminderTable.COLUMN_DURATION_TIME, reminder.getEventDurationTime());
        } else {
            values.put(DBContract.ReminderTable.COLUMN_DURATION_TIME, -1);
        }

        if (reminder.getEventDurationScale() != null) {
            values.put(DBContract.ReminderTable.COLUMN_DURATION_SCALE, reminder.getEventDurationScale().name());
        } else {
            values.putNull(DBContract.ReminderTable.COLUMN_DURATION_SCALE);
        }

        if (reminder.getNotificationTime() != -1) {
            values.put(DBContract.ReminderTable.COLUMN_NOTIFICATION_TIME, reminder.getNotificationTime());
        } else {
            values.put(DBContract.ReminderTable.COLUMN_NOTIFICATION_TIME, -1);
        }

        if (reminder.getNotificationScale() != null) {
            values.put(DBContract.ReminderTable.COLUMN_NOTIFICATION_SCALE, reminder.getNotificationScale().name());
        } else {
            values.putNull(DBContract.ReminderTable.COLUMN_NOTIFICATION_SCALE);
        }

        if (reminder.getNotes() != null) {
            values.put(DBContract.ReminderTable.COLUMN_NOTES, reminder.getNotes());
        } else {
            values.putNull(DBContract.ReminderTable.COLUMN_NOTES);
        }

        if (reminder.isRepeating()) {
            values.put(DBContract.ReminderTable.COLUMN_REPEATING, 1);
        } else {
            values.put(DBContract.ReminderTable.COLUMN_REPEATING, 0);
        }

        if (reminder.getRepetitionWeeks() != -1) {
            values.put(DBContract.ReminderTable.COLUMN_REPETITION_WEEKS, reminder.getRepetitionWeeks());
        } else {
            values.put(DBContract.ReminderTable.COLUMN_REPETITION_WEEKS, -1);
        }

        if (reminder.getPromptLevel() != -1) {
            values.put(DBContract.ReminderTable.COLUMN_PROMPT_LEVEL, reminder.getPromptLevel());
        } else {
            values.put(DBContract.ReminderTable.COLUMN_PROMPT_LEVEL, -1);
        }

        if (reminder.getType() != null) {
            values.put(DBContract.ReminderTable.COLUMN_EVENT_TYPE, reminder.getType().name());
        } else {
            values.putNull(DBContract.ReminderTable.COLUMN_EVENT_TYPE);
        }

        // UPDATE REMINDER
        db.update(DBContract.ReminderTable.TABLE_NAME, values, selection, selectionArgs);

        if(!reminder.getReminderType().equals(ReminderType.PROMPT)) {
            // DELETE REMINDER REPEAT DAYS AND RE-ENTER
            deleteRepeatDays(reminder.getId());
            // Re-open db (deletRepeatDays closes it)
            db = this.getWritableDatabase();
            //Re-enter days
            for (Day d : reminder.getRepetitionDays()) {
                ContentValues dayValues = new ContentValues();
                dayValues.put(DBContract.ReminderDayTable.COLUMN_REMINDER_ID, reminder.getId());
                dayValues.put(DBContract.ReminderDayTable.COLUMN_DAY, d.name());
                db.insert(DBContract.ReminderDayTable.TABLE_NAME, null, dayValues);
            }
        }

        db.close();

        //update log
        updateReminderLog(reminder);
    }

    private void deleteRepeatDays(long reminder_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = DBContract.ReminderDayTable.COLUMN_REMINDER_ID + "=?";
        String[] selectionArgs = {String.valueOf(reminder_id)};
        // delete rows
        db.delete(DBContract.ReminderDayTable.TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    public void manuallyDeleteReminder(BaseReminder reminder){
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = DBContract.ReminderTable._ID + "=?";
        String[] selectionArgs = {String.valueOf(reminder.getId())};
        // delete row
        db.delete(DBContract.ReminderTable.TABLE_NAME, selection, selectionArgs);
        db.close();
        deleteReminderFromLog(reminder.getLogID());
    }

    public void manuallyDeletePrompt(BaseReminder reminder){
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = DBContract.ReminderTable._ID + "=?";
        String[] selectionArgs = {String.valueOf(reminder.getId())};
        // delete row
        db.delete(DBContract.ReminderTable.TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    // LOGGING /////////////////////////////////////////////////////////////////////////////////////
    public long getReminderLogCount() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT COUNT(*) FROM "
                + DBContract.ReminderLogTable.TABLE_NAME
                + " WHERE "
                + DBContract.ReminderLogTable.COLUMN_REMINDER_TYPE
                + " = "
                + "\"" + ReminderType.REMINDER.name() + "\"";

        long numRows = DatabaseUtils.longForQuery(db, query, null);
        db.close();
        return numRows;
    }

    public long getPromptLogCount() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT COUNT(*) FROM "
                + DBContract.ReminderLogTable.TABLE_NAME
                + " WHERE "
                + DBContract.ReminderLogTable.COLUMN_REMINDER_TYPE
                + " = "
                + "\"" + ReminderType.PROMPT.name() + "\"";

        long numRows = DatabaseUtils.longForQuery(db, query, null);
        db.close();
        return numRows;
    }

    private void deleteReminderFromLog(long promptLogID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = DBContract.ReminderLogTable._ID + "=?";
        String[] selectionArgs = {String.valueOf(promptLogID)};
        // delete row
        db.delete(DBContract.ReminderLogTable.TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    private void updateReminderLog(BaseReminder reminder) {

        SQLiteDatabase db = this.getWritableDatabase();
        String selection = DBContract.ReminderLogTable._ID + "=?";
        String[] selectionArgs = {String.valueOf(reminder.getLogID())};

        // REMINDER VALUES TO UPDATE
        ContentValues values = new ContentValues();

        if (reminder.getDateTime() != -1) {
            values.put(DBContract.ReminderLogTable.COLUMN_DATE_TIME, reminder.getDateTime());
        } else {
            values.put(DBContract.ReminderLogTable.COLUMN_DATE_TIME, -1);
        }

        // Update notes if prompt (content)
        if (reminder.getReminderType().equals(ReminderType.PROMPT)) {
            if (reminder.getNotes() != null) {
                values.put(DBContract.ReminderLogTable.COLUMN_NOTES, reminder.getNotes());
            } else {
                values.putNull(DBContract.ReminderLogTable.COLUMN_NOTES);
            }
        }

        if (reminder.isRepeating()) {
            values.put(DBContract.ReminderLogTable.COLUMN_REPEATING, 1);
        } else {
            values.put(DBContract.ReminderLogTable.COLUMN_REPEATING, 0);
        }

        if (reminder.getRepetitionWeeks() != -1) {
            values.put(DBContract.ReminderLogTable.COLUMN_REPETITION_WEEKS, reminder.getRepetitionWeeks());
        } else {
            values.put(DBContract.ReminderLogTable.COLUMN_REPETITION_WEEKS, -1);
        }

        if (reminder.getPromptLevel() != -1) {
            values.put(DBContract.ReminderLogTable.COLUMN_PROMPT_LEVEL, reminder.getPromptLevel());
        } else {
            values.put(DBContract.ReminderLogTable.COLUMN_PROMPT_LEVEL, -1);
        }

        if (reminder.getType() != null) {
            values.put(DBContract.ReminderLogTable.COLUMN_EVENT_TYPE, reminder.getType().name());
        } else {
            values.putNull(DBContract.ReminderLogTable.COLUMN_EVENT_TYPE);
        }

        // UPDATE REMINDER
        db.update(DBContract.ReminderLogTable.TABLE_NAME, values, selection, selectionArgs);

        if (!reminder.getReminderType().equals(ReminderType.PROMPT)) {
            // DELETE REMINDER REPEAT LOG DAYS AND RE-ENTER
            deleteRepeatLogDays(reminder.getLogID());
            //Re-enter days
            for (Day d : reminder.getRepetitionDays()) {
                ContentValues dayValues = new ContentValues();
                dayValues.put(DBContract.ReminderDayLogTable.COLUMN_REMINDER_ID, reminder.getId());
                dayValues.put(DBContract.ReminderDayLogTable.COLUMN_DAY, d.name());
                db.insert(DBContract.ReminderDayLogTable.TABLE_NAME, null, dayValues);
            }
        }

        db.close();

    }

    private void deleteRepeatLogDays(long reminderLogID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = DBContract.ReminderDayLogTable.COLUMN_REMINDER_ID + "=?";
        String[] selectionArgs = {String.valueOf(reminderLogID)};
        // delete rows
        db.delete(DBContract.ReminderDayLogTable.TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    public ArrayList<BaseLogReminder> getAllRemindersFromLog() {

        ArrayList<BaseLogReminder> reminderLogList = new ArrayList<>();

        // Columns to return
        String[] projection = {
                DBContract.ReminderLogTable._ID,
                DBContract.ReminderLogTable.COLUMN_DATE_TIME,
                DBContract.ReminderLogTable.COLUMN_REPEATING,
                DBContract.ReminderLogTable.COLUMN_REPETITION_WEEKS,
                DBContract.ReminderLogTable.COLUMN_PROMPT_LEVEL,
                DBContract.ReminderLogTable.COLUMN_EVENT_TYPE,
                DBContract.ReminderLogTable.COLUMN_DATETIME_SET
        };

        // Columns for the where clause
        String selection = DBContract.ReminderLogTable.COLUMN_REMINDER_TYPE + "=?";

        // Values for the where clause
        String[] selectionArgs = {
                ReminderType.REMINDER.name()
        };

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                DBContract.ReminderLogTable.TABLE_NAME,
                projection,     // columns to return
                selection,      // columns for where clause
                selectionArgs,  // values for where clause
                null,           // group by
                null,           // having
                null);          // order by

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                BaseLogReminder reminder = new BaseLogReminder();

                reminder.setId(cursor.getLong(cursor.getColumnIndex(DBContract.ReminderLogTable._ID)));
                reminder.setDateTime(cursor.getLong(cursor.getColumnIndex(DBContract.ReminderLogTable.COLUMN_DATE_TIME)));
                reminder.setRepeating(cursor.getInt(cursor.getColumnIndex(DBContract.ReminderLogTable.COLUMN_REPEATING)) != 0);

                // Get Reminder Days ---------------------->
                // Columns to return
                String[] day_projection = {
                        DBContract.ReminderDayLogTable.COLUMN_DAY
                };

                // Columns for the where clause
                String day_selection = DBContract.ReminderDayLogTable.COLUMN_REMINDER_ID + "=?";

                // Values for the where clause
                String[] day_selectionArgs = {
                        String.valueOf(reminder.getId())
                };

                Cursor day_cursor = db.query(
                        DBContract.ReminderDayLogTable.TABLE_NAME,
                        day_projection,     // columns to return
                        day_selection,      // columns for where clause
                        day_selectionArgs,  // values for where clause
                        null,           // group by
                        null,           // having
                        null);          // order by

                if (day_cursor != null && day_cursor.getCount() > 0) {
                    ArrayList<String> dayList = new ArrayList<>();

                    day_cursor.moveToFirst();
                    do {
                        dayList.add(day_cursor.getString(day_cursor.getColumnIndex(DBContract.ReminderDayLogTable.COLUMN_DAY)));
                    } while (day_cursor.moveToNext());
                    day_cursor.close();

                    if (dayList.size() > 0) {
                        Day[] repeat_days = new Day[dayList.size()];
                        for (int i = 0; i < dayList.size(); i++) {
                            repeat_days[i] = Day.valueOf(dayList.get(i));
                        }
                        // Set the reminder's repeat days
                        reminder.setRepeatingDays(repeat_days);
                    }
                }

                if (cursor.getInt(cursor.getColumnIndex(DBContract.ReminderLogTable.COLUMN_REPETITION_WEEKS)) >= 0) {
                    reminder.setRepetitionWeeks(cursor.getInt(cursor.getColumnIndex(DBContract.ReminderLogTable.COLUMN_REPETITION_WEEKS)));
                }

                reminder.setPromptLevel(cursor.getInt(cursor.getColumnIndex(DBContract.ReminderLogTable.COLUMN_PROMPT_LEVEL)));

                if (!cursor.isNull(cursor.getColumnIndex(DBContract.ReminderLogTable.COLUMN_EVENT_TYPE))) {
                    reminder.setEventType(EventType.valueOf(cursor.getString(cursor.getColumnIndex(DBContract.ReminderLogTable.COLUMN_EVENT_TYPE))));
                }

                reminder.setDateTimeSet(cursor.getLong(cursor.getColumnIndex(DBContract.ReminderLogTable.COLUMN_DATETIME_SET)));

                reminderLogList.add(reminder);

            } while (cursor.moveToNext());
            cursor.close();
            db.close();
        } else {
            db.close();
        }

        return reminderLogList;
    }

    public ArrayList<PromptLogReminder> getAllPromptsFromLog() {

        ArrayList<PromptLogReminder> promptList = new ArrayList<>();

        // Columns to return
        String[] projection = {
                DBContract.ReminderLogTable.COLUMN_DATE_TIME,
                DBContract.ReminderLogTable.COLUMN_NOTES,
                DBContract.ReminderLogTable.COLUMN_PROMPT_LEVEL,
                DBContract.ReminderLogTable.COLUMN_PROMPT_RESPONDED,
                DBContract.ReminderLogTable.COLUMN_DATETIME_SET
        };

        // Columns for the where clause
        String selection = DBContract.ReminderLogTable.COLUMN_REMINDER_TYPE + "=?";

        // Values for the where clause
        String[] selectionArgs = {
                ReminderType.PROMPT.name()
        };

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                DBContract.ReminderLogTable.TABLE_NAME,
                projection,     // columns to return
                selection,      // columns for where clause
                selectionArgs,  // values for where clause
                null,           // group by
                null,           // having
                null);          // order by

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {

                PromptLogReminder reminder = new PromptLogReminder();

                reminder.setDateTime(cursor.getLong(cursor.getColumnIndex(DBContract.ReminderLogTable.COLUMN_DATE_TIME)));

                if (!cursor.isNull(cursor.getColumnIndex(DBContract.ReminderLogTable.COLUMN_NOTES))) {
                    reminder.setContent(cursor.getString(cursor.getColumnIndex(DBContract.ReminderLogTable.COLUMN_NOTES)));
                }

                reminder.setPromptLevel(cursor.getInt(cursor.getColumnIndex(DBContract.ReminderLogTable.COLUMN_PROMPT_LEVEL)));

                if(cursor.getInt(cursor.getColumnIndex(DBContract.ReminderLogTable.COLUMN_PROMPT_RESPONDED)) == 1){
                    reminder.setPostiveUserResponse(true);
                }else{
                    reminder.setPostiveUserResponse(false);
                }

                reminder.setDateTimeSet(cursor.getLong(cursor.getColumnIndex(DBContract.ReminderLogTable.COLUMN_DATETIME_SET)));

                promptList.add(reminder);

            } while (cursor.moveToNext());
            cursor.close();
            db.close();
        } else {
            db.close();
        }

        return promptList;
    }

    public void updatePromptResponded(BaseReminder reminder){
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = DBContract.ReminderLogTable._ID + "=?";
        String[] selectionArgs = {String.valueOf(reminder.getLogID())};

        // REMINDER VALUES TO UPDATE
        ContentValues values = new ContentValues();
        values.put(DBContract.ReminderLogTable.COLUMN_PROMPT_RESPONDED, 1);

        // UPDATE REMINDER
        db.update(DBContract.ReminderLogTable.TABLE_NAME, values, selection, selectionArgs);
        db.close();
    }
}