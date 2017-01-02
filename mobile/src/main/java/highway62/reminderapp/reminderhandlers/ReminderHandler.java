package highway62.reminderapp.reminderhandlers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.ArrayList;

import highway62.reminderapp.constants.Consts;
import highway62.reminderapp.constants.Day;
import highway62.reminderapp.constants.EventType;
import highway62.reminderapp.constants.ReminderType;
import highway62.reminderapp.daos.ReminderDAO;
import highway62.reminderapp.reminders.BaseLogReminder;
import highway62.reminderapp.reminders.BaseReminder;
import highway62.reminderapp.reminders.PromptLogReminder;


public class ReminderHandler {

    /**
     * Creates database if not already created
     */
    public static ReminderDAO createDatabase(Context context) {
        return new ReminderDAO(context);
    }

    /**
     * Used after boot to reload any stored reminders
     */
    public static ArrayList<BaseReminder> loadReminders(Context context) {
        // Create DB if non existent
        ReminderDAO reminderDAO = createDatabase(context);
        // Check database for reminders and reset any alarms
        ArrayList<BaseReminder> reminderList = reminderDAO.getAllReminders();
        for (BaseReminder reminder : reminderList) {
            DateTime dateTime = new DateTime(reminder.getDateTime());
            if (!dateTime.isBeforeNow()) {
                if (reminder.getReminderType().equals(ReminderType.REMINDER)) {
                    setReminderAlarm(context, reminder);
                } else {
                    setPromptAlarm(context, reminder);
                }
            }
        }

        return reminderList;
    }

    public static ArrayList<BaseReminder> getAllReminders(Context context) {
        // Create DB if non existent
        ReminderDAO reminderDAO = createDatabase(context);
        return reminderDAO.getAllReminders();
    }

    public static void setReminder(Context context, BaseReminder reminder) {
        System.out.println("Adding reminder "+reminder.getDateTime()+ " " + reminder.getReminderType());
        // Check if DB exists, if not call createDB()
        ReminderDAO reminderDAO = createDatabase(context);

        // Add reminder to DB
        long rem_id = reminderDAO.addReminder(reminder);
        // Get reminder from DB with ID and set the alarm
        if (rem_id > 0) {
            BaseReminder reminderWithID = reminderDAO.getReminder(rem_id);
            if (reminderWithID.getReminderType().equals(ReminderType.REMINDER)) {
                System.out.println(" Adding REMINDER type");
                setReminderAlarm(context, reminderWithID);
            } else {
                System.out.println(" Adding PROMPT type");
                setPromptAlarm(context, reminderWithID);
                return;
            }
        } else {
            Log.e("REMIND_ERROR", "Error adding reminder to DB at ReminderHandler.setReminder()");
        }

        if (reminder.isRepeating()) {
            DateTime dt = new DateTime(reminder.getDateTime());
            Day[] rDays = reminder.getRepetitionDays();
            int weeks = reminder.getRepetitionWeeks();
            int reminderDay = dt.getDayOfWeek();

            // For every repeating week
            for (int i = 0; i <= weeks; i++) {
                // Check days
                for (Day d : rDays) {
                    DateTime rDt;
                    int dayVal = getDayValue(d, reminderDay);
                    if (dayVal > reminderDay) {
                        rDt = new DateTime(dt.getMillis())
                                .plusDays((dayVal - reminderDay) + (i * 7));
                    } else if (dayVal < reminderDay) {
                        rDt = new DateTime(dt.getMillis())
                                .plusDays(((dayVal + 7) - reminderDay) + (i * 7));
                    } else {
                        if (i > 0) {
                            rDt = new DateTime(dt.getMillis())
                                    .plusDays(i * 7);
                        } else {
                            // Do not repeat same reminder on same day
                            rDt = null;
                        }
                    }

                    if (rDt != null) {
                        BaseReminder bRem = new BaseReminder(reminder);
                        bRem.setDateTime(rDt.getMillis());
                        long bRemID = reminderDAO.addReminder(bRem);
                        if (bRemID > 0) {
                            BaseReminder bRemWithID = reminderDAO.getReminder(bRemID);
                            setReminderAlarm(context, bRemWithID);
                        } else {
                            Log.e("REMIND_ERROR", "Error adding repeat reminder to DB at ReminderHandler.setReminder()");
                        }
                    }
                }
            }
        }
    }

    private static int getDayValue(Day d, int defaultDay) {
        switch (d) {
            case MONDAY:
                return 1;
            case TUESDAY:
                return 2;
            case WEDNESDAY:
                return 3;
            case THURSDAY:
                return 4;
            case FRIDAY:
                return 5;
            case SATURDAY:
                return 6;
            case SUNDAY:
                return 7;
            default:
                return defaultDay;
        }
    }

    public static void updateReminder(Context context, BaseReminder reminder) {
        ReminderDAO reminderDAO = createDatabase(context);
        reminderDAO.updateReminder(reminder);
        if (reminder.getReminderType().equals(ReminderType.REMINDER)) {
            setReminderAlarm(context, reminder);
        } else {
            setPromptAlarm(context, reminder);
        }
    }

    public static ArrayList<BaseReminder> getPrompts(Context context) {
        ReminderDAO reminderDAO = createDatabase(context);
        return reminderDAO.getAllPrompts();
    }

    public static void manuallyDeleteReminder(Context context, BaseReminder reminder) {
        ReminderDAO reminderDAO = createDatabase(context);
        reminderDAO.manuallyDeleteReminder(reminder);
        cancelAlarm(context, reminder);
    }

    public static void manuallyDeletePrompt(Context context, BaseReminder reminder) {
        ReminderDAO reminderDAO = createDatabase(context);
        reminderDAO.manuallyDeletePrompt(reminder);
        DateTime dt = new DateTime(reminder.getDateTime());
        if(dt.isAfterNow()) {
            cancelAlarm(context, reminder);
        }
    }

    private static void setReminderAlarm(Context context, BaseReminder reminder) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        DateTime dt;

        // Check notification time
        int notTime = reminder.getNotificationTime();
        if (reminder.getNotificationScale() != null) {
            System.out.println("First");
            System.out.println(reminder.getNotificationScale());
            switch (reminder.getNotificationScale()) {
                case SAMETIME:
                    dt = new DateTime(reminder.getDateTime())
                            .withSecondOfMinute(0)
                            .withMillisOfSecond(0);
                    break;
                case MINS:
                    dt = new DateTime(reminder.getDateTime())
                            .withSecondOfMinute(0)
                            .withMillisOfSecond(0)
                            .minusMinutes(notTime);
                    break;
                case HOURS:
                    dt = new DateTime(reminder.getDateTime())
                            .withSecondOfMinute(0)
                            .withMillisOfSecond(0)
                            .minusHours(notTime);
                    break;
                case DAYS:
                    dt = new DateTime(reminder.getDateTime())
                            .withSecondOfMinute(0)
                            .withMillisOfSecond(0)
                            .minusDays(notTime);
                    break;
                case WEEKS:
                    dt = new DateTime(reminder.getDateTime())
                            .withSecondOfMinute(0)
                            .withMillisOfSecond(0)
                            .minusWeeks(notTime);
                    break;
                default:
                    dt = new DateTime(reminder.getDateTime())
                            .withSecondOfMinute(0)
                            .withMillisOfSecond(0);
            }
            // If user sets notification to go off before now, just display it straight away
            if (dt.isBeforeNow()) {
                dt = new DateTime()
                        .withSecondOfMinute(0)
                        .withMillisOfSecond(0);
            }

        } else if (reminder.getType().equals(EventType.BIRTH)) {
            System.out.println("Second");
            // Set notification for the day before
            dt = new DateTime(reminder.getDateTime())
                    .withSecondOfMinute(0)
                    .withMillisOfSecond(0)
                    .minusDays(1);

            // If day before birthday is before now (user has set reminder for tomorrow)
            // Work out difference between reminder and now, set appropriate notification time
            if (dt.isBeforeNow()) {
                DateTime remTime = new DateTime(reminder.getDateTime())
                        .withSecondOfMinute(0)
                        .withMillisOfSecond(0);

                DateTime now = new DateTime()
                        .withSecondOfMinute(0)
                        .withMillisOfSecond(0);

                // Period between now and the user's birthday reminder
                Period period = new Period(now, remTime);

                // Reminder set between 1-3 hours from now
                if (period.getHours() >= 1 && period.getHours() <= 3) {
                    // Set notification 30 mins before
                    dt = new DateTime(reminder.getDateTime())
                            .withSecondOfMinute(0)
                            .withMillisOfSecond(0)
                            .minusMinutes(30);
                    // Reminder set bewteen 3 - 6 hours from now
                } else if (period.getHours() > 3 && period.getHours() <= 6) {
                    // Set notification 2 hours before
                    dt = new DateTime(reminder.getDateTime())
                            .withSecondOfMinute(0)
                            .withMillisOfSecond(0)
                            .minusHours(2);
                    // Reminder set between 6 - 12 hours from now
                } else if (period.getHours() > 6 && period.getHours() <= 12) {
                    // Set notification 4 hours before
                    dt = new DateTime(reminder.getDateTime())
                            .withSecondOfMinute(0)
                            .withMillisOfSecond(0)
                            .minusHours(4);
                } else if (period.getHours() > 12) {
                    // Set notification 6 hours before
                    dt = new DateTime(reminder.getDateTime())
                            .withSecondOfMinute(0)
                            .withMillisOfSecond(0)
                            .minusHours(6);
                } else {
                    dt = new DateTime(reminder.getDateTime())
                            .withSecondOfMinute(0)
                            .withMillisOfSecond(0);
                }
            }

        } else {
            System.out.println("Third");
            // Reminder is social event or daily task, set notification for 1 hour before
            dt = new DateTime(reminder.getDateTime())
                    .withSecondOfMinute(0)
                    .withMillisOfSecond(0);

            if (!dt.minusHours(1).isBeforeNow()) {
                dt = new DateTime(reminder.getDateTime())
                        .withSecondOfMinute(0)
                        .withMillisOfSecond(0)
                        .minusHours(1);
            }
        }
        System.out.println("MEOW");
        System.out.println(reminder.getType()+ " " + dt.getMillis() + " ");
        // Set the alarm
        alarmManager.setExact(AlarmManager.RTC_WAKEUP
                , dt.getMillis()
                , getReminderIntent(context, reminder));
    }

    private static void setPromptAlarm(Context context, BaseReminder reminder) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        DateTime dt = new DateTime(reminder.getDateTime())
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP
                , dt.getMillis()
                , getReminderIntent(context, reminder));
    }

    private static void cancelAlarm(Context context, BaseReminder reminder) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pIntent = getCancelIntent(context, reminder);
        alarmManager.cancel(pIntent);
        pIntent.cancel();
    }

    private static PendingIntent getReminderIntent(Context context, BaseReminder reminder) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra(Consts.REMINDER_INTENT, reminder);
        return PendingIntent.getBroadcast(context, (int) reminder.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private static PendingIntent getCancelIntent(Context context, BaseReminder reminder) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra(Consts.REMINDER_INTENT, reminder);
        return PendingIntent.getBroadcast(context, (int) reminder.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    // LOGGING -----------------------------------------------------------------------------------//
    public static long getReminderLogCount(Context context) {
        ReminderDAO reminderDAO = createDatabase(context);
        return reminderDAO.getReminderLogCount();
    }

    public static long getPromptLogCount(Context context) {
        ReminderDAO reminderDAO = createDatabase(context);
        return reminderDAO.getPromptLogCount();
    }

    public static ArrayList<BaseLogReminder> getAllRemindersFromLog(Context context) {
        ReminderDAO reminderDAO = createDatabase(context);
        return reminderDAO.getAllRemindersFromLog();
    }

    public static ArrayList<PromptLogReminder> getAllPromptsFromLog(Context context) {
        ReminderDAO reminderDAO = createDatabase(context);
        return reminderDAO.getAllPromptsFromLog();
    }

    public static void updatePromptResponded(Context context, BaseReminder reminder){
        ReminderDAO reminderDAO = createDatabase(context);
        reminderDAO.updatePromptResponded(reminder);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////


}
