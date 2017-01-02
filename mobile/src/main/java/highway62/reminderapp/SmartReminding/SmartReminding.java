package highway62.reminderapp.SmartReminding;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import highway62.reminderapp.constants.EventType;
import highway62.reminderapp.constants.NotificationScale;
import highway62.reminderapp.constants.ReminderType;
import highway62.reminderapp.ReminderActivity;
import highway62.reminderapp.constants.Consts;
import highway62.reminderapp.reminderhandlers.ReminderReceiver;
import highway62.reminderapp.reminders.BaseReminder;
import highway62.reminderapp.reminderhandlers.ReminderHandler;
import org.joda.time.DateTime;
import org.joda.time.Weeks;

/**
 * Created by pbkou on 19/12/2016.
 */

public class SmartReminding {

    private ReminderHandler handler;
    private ArrayList<BaseReminder> pastReminders;
    private Context context;
    private DateTime currentDate;
    private Integer time=null;


    public SmartReminding(Context context){
        System.out.println("MEOWWWW");
        this.context=context;
        this.handler = new ReminderHandler();
        this.pastReminders = ReminderHandler.getAllReminders(this.context);
        currentDate=new DateTime();
    }

    public SmartReminding(Context context,Integer t){
        System.out.println("TSIOUUUUU");
        this.time=t;
        this.context=context;
        this.handler = new ReminderHandler();
        this.pastReminders = ReminderHandler.getAllReminders(this.context);
        currentDate=new DateTime();
    }


    public void collect_and_set_reminder_suggestions(){

        ArrayList<ArrayList<BaseReminder>> lists = new ArrayList<ArrayList<BaseReminder>>();
        lists.add(one_month_pattern());
        lists.add(two_week_pattern());
        lists.add(one_month_pattern());
        set_pattern_suggestion_alarms(lists);
    }

    public ArrayList<BaseReminder> one_week_pattern(){

        ArrayList<BaseReminder> pattern_list = new ArrayList<BaseReminder>();
        DateTime before_seven_days=this.currentDate.minusDays(7);

        //Find reminders with difference of 7 days from today
        for (BaseReminder reminder : this.pastReminders){
            DateTime dt1 = new DateTime(reminder.getDateTime());
            if ( ( dt1.getDayOfYear() == before_seven_days.getDayOfYear() ) &&
                    ( dt1.getYear() == before_seven_days.getYear() ) ) {
                pattern_list.add(reminder);}
        }

        return pattern_list;
    }
    public ArrayList<BaseReminder> two_week_pattern(){
        ArrayList<BaseReminder> pattern_list = new ArrayList<BaseReminder>();
        DateTime before_two_weeks=this.currentDate.minusDays(14);

        //Find reminders with difference of 14 days from today
        for (BaseReminder reminder : this.pastReminders){
            DateTime dt1 = new DateTime(reminder.getDateTime());
            if ( ( dt1.getDayOfYear() == before_two_weeks.getDayOfYear() ) &&
                    ( dt1.getYear() == before_two_weeks.getYear() ) ) {
                pattern_list.add(reminder);}
        }

        return pattern_list;
    }

    public ArrayList<BaseReminder> one_month_pattern(){
        ArrayList<BaseReminder> pattern_list = new ArrayList<BaseReminder>();
        DateTime before_two_weeks=this.currentDate.minusMonths(1);

        //Find reminders with difference of 14 days from today
        for (BaseReminder reminder : this.pastReminders){
            DateTime dt1 = new DateTime(reminder.getDateTime());
            if ( ( dt1.getDayOfYear() == before_two_weeks.getDayOfYear() ) &&
                    ( dt1.getYear() == before_two_weeks.getYear() ) ) {
                pattern_list.add(reminder);}
        }

        return pattern_list;
    }



    public void disable_smart_reminders(){
        for (BaseReminder reminder : pastReminders){
            if (reminder.getReminderType()==ReminderType.SMART){
                ReminderHandler.manuallyDeletePrompt(this.context,reminder);
            }
        }
    }

    /**
     * 1.Suggest reminders
     * 2.Set alarm for next day to suggest reminders
     * */
    public void set_pattern_suggestion_alarms(ArrayList<ArrayList<BaseReminder>> reminders){

        //Suggest reminders
        add_notifications(reminders);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        DateTime dt = DateTime.now().plusDays(1).withHourOfDay(8);
        if (this.time!=null) {
            dt = DateTime.now().plusDays(1).withHourOfDay(this.time);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP
                , dt.getMillis()
                , getReminderIntent(context, reminders));
    }

    public void add_notifications(ArrayList<ArrayList<BaseReminder>> reminders){
        DateTime time=null;
        if (this.time==null){
            time=DateTime.now().plusDays(1).withHourOfDay(8).withMinuteOfHour(0).withSecondOfMinute(0); //TIME
        }else{
            time=DateTime.now().plusDays(1).withHourOfDay(this.time).withMinuteOfHour(0).withSecondOfMinute(0); //TIME
        }

        ArrayList<BaseReminder> prompts=new ArrayList<BaseReminder>();
        for (ArrayList<BaseReminder> reminder_list : reminders){
            for (BaseReminder reminder : reminder_list){
                BaseReminder rem = new BaseReminder();
                rem.setReminderType(ReminderType.SMART);//Set ReminderType
                rem.setPromptLevel(2);

                rem.setNotes("You had an alarm on "+new DateTime(rem.getDateTime()).toDate()+". Do you want to set a new one?");

                rem.setDateTime(time.getMillis());//Set the time
                rem.setNotificationScale(NotificationScale.SAMETIME);//Set scale
                ReminderHandler.setReminder(this.context,rem);
            }
        }
        BaseReminder r= new BaseReminder(DateTime.now().getMillis());
        r.setReminderType(ReminderType.SMART);
        r.setDateTime(DateTime.now().getMillis());//Set the time
        r.setPromptLevel(2);
        r.setNotes("You have initiated SmartReminding");
        ReminderHandler.setReminder(this.context,r);
    }

    private static PendingIntent getReminderIntent(Context context, ArrayList<ArrayList<BaseReminder>> reminders) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra(Consts.REMINDER_SUGGEST, reminders);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
