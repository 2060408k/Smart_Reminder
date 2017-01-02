package highway62.reminderapp.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import highway62.reminderapp.constants.DurationScale;
import highway62.reminderapp.reminders.AudioLogReminder;
import highway62.reminderapp.reminders.AudioReminder;

/**
 * Created by Highway62 on 25/09/2016.
 */
public class WearReminderDAO extends WearBaseDAO {

    public WearReminderDAO(Context context) {
        super(context, WearDBContract.DB_NAME, null, WearDBContract.DB_VERSION);
    }

    /**
     * Adds a reminder to the database
     */
    public long addReminder(AudioReminder reminder) {

        ContentValues values = new ContentValues();

        if (reminder.getDateTime() != -1) {
            values.put(WearDBContract.ReminderTable.COLUMN_DATE_TIME, reminder.getDateTime());
        }

        if (reminder.getDateTimeSet() != -1) {
            values.put(WearDBContract.ReminderTable.COLUMN_DATETIME_SET, reminder.getDateTimeSet());
        }

        if (reminder.getAfterTime() != -1) {
            values.put(WearDBContract.ReminderTable.COLUMN_AFTER_TIME, reminder.getAfterTime());
        }

        if (reminder.getAfterScale() != null) {
            values.put(WearDBContract.ReminderTable.COLUMN_AFTER_SCALE, reminder.getAfterScale().name());
        }

        if (reminder.getAudio() != null) {
            values.put(WearDBContract.ReminderTable.COLUMN_AUDIO, reminder.getAudio());
        }

        // Insert
        SQLiteDatabase db = this.getWritableDatabase();
        long reminder_ID = db.insert(WearDBContract.ReminderTable.TABLE_NAME, null, values);
        db.close();
        return reminder_ID;
    }

    /**
     * Returns all reminders for the view reminders screen
     */
    public ArrayList<AudioReminder> getAllReminders() {

        ArrayList<AudioReminder> reminders = new ArrayList<>();

        // Columns to return
        String[] projection = {
                WearDBContract.ReminderTable._ID,
                WearDBContract.ReminderTable.COLUMN_DATE_TIME,
                WearDBContract.ReminderTable.COLUMN_AUDIO
        };


        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                WearDBContract.ReminderTable.TABLE_NAME,
                projection,     // columns to return
                null,           // columns for where clause
                null,           // values for where clause
                null,           // group by
                null,           // having
                null);          // order by

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                // Only retrieve reminders set to go off after the current time
                AudioReminder reminder = new AudioReminder();
                Calendar cal = Calendar.getInstance();
                Date now = cal.getTime();
                // Get reminder date time
                long remTimeinMillis = cursor.getLong(cursor.getColumnIndex(WearDBContract.ReminderTable.COLUMN_DATE_TIME));
                cal.setTimeInMillis(remTimeinMillis);
                Date timeOfReminder = cal.getTime();

                if (!timeOfReminder.before(now)) {
                    reminder.setId(cursor.getLong(cursor.getColumnIndex(WearDBContract.ReminderTable._ID)));
                    reminder.setDateTime(cursor.getLong(cursor.getColumnIndex(WearDBContract.ReminderTable.COLUMN_DATE_TIME)));
                    if (cursor.getBlob(cursor.getColumnIndex(WearDBContract.ReminderTable.COLUMN_AUDIO)) != null) {
                        byte[] audio = cursor.getBlob(cursor.getColumnIndex(WearDBContract.ReminderTable.COLUMN_AUDIO));
                        reminder.setAudio(audio);
                        reminders.add(0,reminder);
                    }
                }
            } while (cursor.moveToNext());
        }

        db.close();

        return reminders;
    }

    /**
     * Returns all reminders for the log file
     */
    public ArrayList<AudioLogReminder> getAllRemindersForLog() {

        ArrayList<AudioLogReminder> reminders = new ArrayList<>();

        // Columns to return
        String[] projection = {
                WearDBContract.ReminderTable._ID,
                WearDBContract.ReminderTable.COLUMN_DATE_TIME,
                WearDBContract.ReminderTable.COLUMN_DATETIME_SET,
                WearDBContract.ReminderTable.COLUMN_AFTER_TIME,
                WearDBContract.ReminderTable.COLUMN_AFTER_SCALE
        };

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                WearDBContract.ReminderTable.TABLE_NAME,
                projection,     // columns to return
                null,           // columns for where clause
                null,           // values for where clause
                null,           // group by
                null,           // having
                null);          // order by

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {

                AudioLogReminder reminder = new AudioLogReminder();
                reminder.setId(cursor.getLong(cursor.getColumnIndex(WearDBContract.ReminderTable._ID)));
                reminder.setDateTime(cursor.getLong(cursor.getColumnIndex(WearDBContract.ReminderTable.COLUMN_DATE_TIME)));
                reminder.setDateTimeSet(cursor.getLong(cursor.getColumnIndex(WearDBContract.ReminderTable.COLUMN_DATETIME_SET)));
                reminder.setAfterTime(cursor.getLong(cursor.getColumnIndex(WearDBContract.ReminderTable.COLUMN_AFTER_TIME)));
                if (!cursor.isNull(cursor.getColumnIndex(WearDBContract.ReminderTable.COLUMN_AFTER_SCALE))) {
                    reminder.setAfterScale(DurationScale.valueOf(cursor.getString(cursor.getColumnIndex(WearDBContract.ReminderTable.COLUMN_AFTER_SCALE))));
                }

                reminders.add(reminder);
            } while (cursor.moveToNext());
        }

        db.close();

        return reminders;
    }

    /**
     * Deletes audio after the reminder has been opened and played/dismissed
     */
    public void deleteAudio(long reminderID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = WearDBContract.ReminderTable._ID + "=?";
        String[] selectionArgs = {String.valueOf(reminderID)};
        ContentValues values = new ContentValues();
        values.putNull(WearDBContract.ReminderTable.COLUMN_AUDIO);
        // Update audio column as null
        db.update(WearDBContract.ReminderTable.TABLE_NAME, values, selection, selectionArgs);
        db.close();
    }

    public void deleteReminder(long reminderID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = WearDBContract.ReminderTable._ID + "=?";
        String[] selectionArgs = {String.valueOf(reminderID)};
        // delete row
        db.delete(WearDBContract.ReminderTable.TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    /**
     * Returns the audio for when the reminder triggers, or the user clicks to view a reminder
     */
    public byte[] getAudio(long reminderID) {
        byte[] audio;

        // Columns to return
        String[] projection = {
                WearDBContract.ReminderTable.COLUMN_AUDIO
        };

        SQLiteDatabase db = this.getReadableDatabase();

        // Columns for the where clause
        String selection = WearDBContract.ReminderTable._ID + "=?";

        // Values for the where clause
        String[] selectionArgs = {
                String.valueOf(reminderID)
        };

        Cursor cursor = db.query(
                WearDBContract.ReminderTable.TABLE_NAME,
                projection,     // columns to return
                selection,      // columns for where clause
                selectionArgs,  // values for where clause
                null,           // group by
                null,           // having
                null);          // order by

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                audio = cursor.getBlob(cursor.getColumnIndex(WearDBContract.ReminderTable.COLUMN_AUDIO));
            } while (cursor.moveToNext());
        } else {
            audio = null;
        }

        db.close();

        return audio;
    }
}
