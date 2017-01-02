package highway62.reminderapp.timehandlers;

import org.joda.time.DateTime;

import java.util.concurrent.TimeUnit;

/**
 * Created by Highway62 on 16/08/2016.
 */
public abstract class TimeHandler {

    /**
     * Returns the spinner value for how long before the reminder the notification is set
     * 0 = at time of event
     * 1 = 30 mins before
     * 2 = 1 hour before
     * 3 = 24 hours before
     * 4 = custom
     */
    public static int getNotifDiffSpinnerValue(DateTime reminder, DateTime notification){
        long differenceInMillis = reminder.getMillis() - notification.getMillis();
        long differenceInMinutes = TimeUnit.MILLISECONDS.toMinutes(differenceInMillis);
        if(differenceInMinutes == 0){
            // At time of event
            return 0;
        }else if(differenceInMinutes == 30){
            // 30 mins before
            return 1;
        }else if(differenceInMinutes == 60){
            // 1 hour before
            return 2;
        }else if(differenceInMinutes == 1440){
            // 24 hours before
            return 3;
        }else{
            return 4;
        }
    }

    /**
     * Returns the int chosen in the custom notification time
     * Returns the spinner value chosen in the custom time spinner
     * 0 = minutes
     * 1 = hours
     * 2 = days
     * 3 = weeks
     */
    public static CustomNotificationTime getCustomTime(DateTime reminder, DateTime notification){
        long differenceInMillis = reminder.getMillis() - notification.getMillis();
        long differenceInMinutes = TimeUnit.MILLISECONDS.toMinutes(differenceInMillis);
        CustomNotificationTime cTime = new CustomNotificationTime();

        if(differenceInMinutes > 8640){
            // > 6 days (weeks)
            cTime.setSpinnerChoice(3);
            cTime.setTime(noOfWeeks(differenceInMinutes));
            return cTime;
        }else if(differenceInMinutes > 1380 && differenceInMinutes <= 8640){
            // > 23 hours && <= 6 days (days)
            cTime.setSpinnerChoice(2);
            cTime.setTime(noOfDays(differenceInMinutes));
            return cTime;
        }else if(differenceInMinutes > 59 && differenceInMinutes <= 1380 ){
            // > 59 mins && <= 23 hours (hours)
            cTime.setSpinnerChoice(1);
            cTime.setTime(noOfHours(differenceInMinutes));
            return cTime;
        }else{
            cTime.setSpinnerChoice(0);
            cTime.setTime((int) differenceInMinutes);
            return cTime;
        }
    }

    private static int noOfWeeks(long mins){
        double diff = (double) mins;
        double hours = diff / 60;
        double days = hours / 24;
        double weeks = days /7;
        return (int) weeks;
    }

    private static int noOfDays(long mins){
        double diff = (double) mins;
        double hours = diff / 60;
        double days = hours / 24;
        return (int) days;
    }

    private static int noOfHours(long mins){
        double diff = (double) mins;
        double hours = diff / 60;
        return (int) hours;
    }

    public static int getHours(long mins){
        double dMins = (double) mins;
        return (int) (dMins / 60);
    }

    public static int getDays(long mins){
        double dMins = (double) mins;
        double dHours = dMins / 60;
        return (int) (dHours / 24);
    }

    public static int getWeeks(long mins){
        double dMins = (double) mins;
        double dHours = dMins / 60;
        double days = dHours / 24;
        return (int) days / 7;
    }
}
