package highway62.reminderapp.reminderhandlers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import highway62.reminderapp.constants.Consts;
import highway62.reminderapp.daos.WearReminderDAO;
import highway62.reminderapp.reminders.AudioLogReminder;
import highway62.reminderapp.reminders.AudioReminder;

/**
 * Created by Highway62 on 26/09/2016.
 */
public class ReminderHandler {

    // Add Reminder ------------------------------------------------------------------------------//
    public static void addReminder(Context context, AudioReminder reminder){

        WearReminderDAO reminderDAO = new WearReminderDAO(context);
        long reminderID = reminderDAO.addReminder(reminder);
        if(reminderID > -1){
            setQuickAlarm(context, reminderID, reminder);
        }else{
            Toast t = new Toast(context);
            t.makeText(context, "Error, please try again",
                    Toast.LENGTH_SHORT)
                    .setGravity(Gravity.BOTTOM|Gravity.CENTER, 0,0);
            t.show();
        }
    }

    public static void addManualReminder(Context context, AudioReminder reminder){
        WearReminderDAO reminderDAO = new WearReminderDAO(context);
        long reminderID = reminderDAO.addReminder(reminder);
        if(reminderID > -1){
            reminder.setId(reminderID);
            setManualAlarm(context, reminder);
        }else{
            Toast t = new Toast(context);
            t.makeText(context, "Error, please try again",
                    Toast.LENGTH_SHORT)
                    .setGravity(Gravity.BOTTOM|Gravity.CENTER, 0,0);
            t.show();
        }
    }

    private static void setManualAlarm(Context context, AudioReminder reminder){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP
                , reminder.getDateTime()
                , getReminderIntent(context, reminder.getId()));
    }

    private static void setQuickAlarm(Context context, long reminderID, AudioReminder reminder) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        /*
        Settings settings = new Settings(context);
        long quickReminderTime = settings.getQuickReminderTime();
        DurationScale quickReminderScale = settings.getQuickReminderScale();
        long reminderTimeInMillis;

        switch (quickReminderScale){
            case MINS:
                reminderTimeInMillis = quickReminderTime * Consts.ONE_MIN_IN_MILLIS;
                break;
            case HOURS:
                reminderTimeInMillis = quickReminderTime * Consts.ONE_MIN_IN_MILLIS;
                break;
            default:
                reminderTimeInMillis = quickReminderTime * Consts.ONE_MIN_IN_MILLIS;
                break;
        }

        Calendar date = Calendar.getInstance();
        long t = date.getTimeInMillis();
        Date afterAddingMins = new Date(t + (reminderTimeInMillis));*/

        /*
        alarmManager.setExact(AlarmManager.RTC_WAKEUP
                , afterAddingMins.getTime()
                , getReminderIntent(context, reminderID));
                */

        alarmManager.setExact(AlarmManager.RTC_WAKEUP
                , reminder.getDateTime()
                , getReminderIntent(context, reminderID));
    }

    private static PendingIntent getReminderIntent(Context context, long reminderID) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra(Consts.REMINDER_ID_INTENT, reminderID);
        Random rand = new Random();
        return PendingIntent.getBroadcast(context, rand.nextInt(), intent, PendingIntent.FLAG_ONE_SHOT);
    }
    //--------------------------------------------------------------------------------------------//

    public static byte[] getAudio(Context context, long reminderID){
        WearReminderDAO reminderDAO = new WearReminderDAO(context);
        return reminderDAO.getAudio(reminderID);
    }

    public static void deleteAudio(Context context, long reminderID){
        WearReminderDAO reminderDAO = new WearReminderDAO(context);
        reminderDAO.deleteAudio(reminderID);
    }

    public static ArrayList<AudioReminder> getAllReminders(Context context){
        WearReminderDAO reminderDAO = new WearReminderDAO(context);
        ArrayList<AudioReminder> reminders = reminderDAO.getAllReminders();
        if(reminders != null && reminders.size() > 0){
            return reminders;
        }else{
            return null;
        }
    }

    public static ArrayList<AudioLogReminder> getAllRemindersForLog(Context context){
        WearReminderDAO reminderDAO = new WearReminderDAO(context);
        ArrayList<AudioLogReminder> reminders = reminderDAO.getAllRemindersForLog();
        Log.e("DBREM", "Wear ReminderHandelr/getAllRemindersForLog, no. of Reminders: " + reminders.size());
        return reminders;
    }

    public static void deleteReminder(Context context, long reminderID){
        WearReminderDAO reminderDAO = new WearReminderDAO(context);
        reminderDAO.deleteReminder(reminderID);
    }

}
