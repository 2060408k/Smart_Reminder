package highway62.reminderapp.SmartReminding;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import highway62.reminderapp.constants.EventType;
import highway62.reminderapp.constants.NotificationScale;
import highway62.reminderapp.constants.ReminderPattern;
import highway62.reminderapp.constants.ReminderType;
import highway62.reminderapp.ReminderActivity;
import highway62.reminderapp.constants.Consts;
import highway62.reminderapp.reminderhandlers.ReminderReceiver;
import highway62.reminderapp.reminderhandlers.SmartReceiver;
import highway62.reminderapp.reminders.BaseReminder;
import highway62.reminderapp.reminderhandlers.ReminderHandler;
import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by pbkou on 19/12/2016.
 */

public class SmartReminding {

    private ReminderHandler handler;
    private ArrayList<BaseReminder> pastReminders;
    private Context context;
    private DateTime currentDate;
    private Integer remindingTime=null;
    private SharedPreferences sharedPreferences;
    private boolean enabled;

    public SmartReminding(Context context){
        this.context=context;
        this.handler = new ReminderHandler();
        this.pastReminders = ReminderHandler.getAllReminders(this.context);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        this.enabled = sharedPreferences.getBoolean("smart_reminding",false);
        currentDate=new DateTime();

    }

    public SmartReminding(Context context,Integer t){
        this.remindingTime=t;
        this.context=context;
        this.handler = new ReminderHandler();
        this.pastReminders = ReminderHandler.getAllReminders(this.context);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        this.enabled= sharedPreferences.getBoolean("smart_reminding",false);
        currentDate=new DateTime();
    }

    public ArrayList<ArrayList<BaseReminder>> getAllSuggestions(){
        ArrayList<ArrayList<BaseReminder>> out = new ArrayList<ArrayList<BaseReminder>>();
        out.add(one_week_pattern());

        out.add(two_week_pattern());

        out.add(one_month_pattern());

        System.out.println(one_month_pattern().size());
        System.out.println(two_week_pattern().size());
        System.out.println(one_month_pattern().size());
        return out;
    }

    public void collect_and_set_reminder_suggestions(){
        if (!this.enabled) {System.out.println("There is no meow in the TOILET");return;}
        ArrayList<ArrayList<BaseReminder>> lists = new ArrayList<ArrayList<BaseReminder>>();
        lists.add(one_week_pattern());
        lists.add(two_week_pattern());
        lists.add(one_month_pattern());
        set_pattern_suggestion_alarms(lists);
    }

    public ArrayList<BaseReminder> one_week_pattern(){

        DateTime before_seven_days=this.currentDate.minusDays(7);
        DateTime before_seven_days_next_day;

        //Get the dates correct hour
        Integer time=get_Time();

        //assign next day's correct date
        before_seven_days_next_day = new DateTime().minusDays(6).withHourOfDay(time);

        // pass data and return the correct dates
        return return_correct_reminders(before_seven_days,before_seven_days_next_day,time,ReminderPattern.WEEKLY);
    }


    public ArrayList<BaseReminder> two_week_pattern(){

        //Create two DateTimes, one for a two week old and one with the next day
        DateTime before_two_weeks=this.currentDate.minusDays(14);
        DateTime before_two_weeks_next_day;

        //Get the dates correct hour
        Integer time=get_Time();

        //assign next day's correct date
        before_two_weeks_next_day = new DateTime().minusDays(13).withHourOfDay(time);

        // pass data and return the correct dates
        return return_correct_reminders(before_two_weeks,before_two_weeks_next_day,time,ReminderPattern.TWO_WEEKS);
    }

    /**
     *
     * @return
     */
    public ArrayList<BaseReminder> one_month_pattern(){

        //Create two DateTimes, one for a month old and one with the next day
        DateTime before_one_month=this.currentDate.minusMonths(1);
        DateTime before_one_month_next_day;

        //Get the dates correct hour
        Integer time=get_Time();

        //assign next day's correct date
        before_one_month_next_day = new DateTime().minusMonths(1).plusDays(1).withHourOfDay(time);

        // pass data and return the correct dates
        return return_correct_reminders(before_one_month,before_one_month_next_day,time,ReminderPattern.MONTHLY);
    }

    /**
     * We are passing the first day and its next day along with the time for the prompts to be sent and what pattern they are.
     * Iterate through the past reminders and check which dates pass the cirteria for date and time and they are assigned into
     * a pattern
     * @param date1 the first day
     * @param date2 next day of date1
     * @param time The time to be set
     * @param pattern pattern to be set
     * @return an arraylist with the correct reminders
     */
    public ArrayList<BaseReminder> return_correct_reminders(DateTime date1, DateTime date2, Integer time, ReminderPattern pattern){

        ArrayList<BaseReminder> pattern_list = new ArrayList<BaseReminder>();

        for (BaseReminder reminder : this.pastReminders){
            DateTime dt1 = new DateTime(reminder.getDateTime());
            if ( ( (dt1.getDayOfYear() == date1.getDayOfYear()) ||((dt1.getDayOfYear() == date2.getDayOfYear()) && (dt1.getHourOfDay()<=time)))
                    && (dt1.getYear() == date1.getYear())
                    && (!reminder.getSmartReminded())) {
                reminder.setPattern(pattern);
                pattern_list.add(reminder);
            }


        }
        return pattern_list;
    }

    public Integer get_Time(){
        Integer time = 8;
        if (remindingTime!=null) time=remindingTime;
        return time;
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
        if (this.remindingTime!=null) {
            dt = DateTime.now().plusDays(1).withHourOfDay(this.remindingTime);
        }


        alarmManager.setExact(AlarmManager.RTC_WAKEUP
                , dt.getMillis()
                , getReminderIntent(context, reminders));
    }

    public void add_notifications(ArrayList<ArrayList<BaseReminder>> reminders){

        ArrayList<BaseReminder> prompts=new ArrayList<BaseReminder>();

        //Limit the reminder patters looking at the user preference
        reminders=get_Modified_Pattern_Reminders(reminders);
        if (reminders==null) {System.out.println("Reminders in add notifications are null");}
        for (ArrayList<BaseReminder> reminder_list : reminders){
            if (reminder_list==null){System.out.println("Reminder list in add notidications is null");}
            for (BaseReminder reminder : reminder_list){
                BaseReminder rem = new BaseReminder();
                rem.setReminderType(ReminderType.SMART);//Set ReminderType
                rem.setPromptLevel(2);

                DateTime dateTime = new DateTime(reminder.getDateTime());
                DateTimeFormatter dtfDate = DateTimeFormat.forPattern("dd/MM/yyyy");
                DateTimeFormatter dtfTime = DateTimeFormat.forPattern("HH:mm");
                StringBuilder sb = new StringBuilder();
                if(reminder.getTitle()!=null) {
                    sb.append("You had a reminder for : " + reminder.getTitle()+",");
                } else {
                    sb.append("You had a reminder ,");
                }
                sb.append("Date: " + dtfDate.print(dateTime) + " Time: " + dtfTime.print(dateTime) + ",");
                sb.append("Click here to set a new reminder ! ,");
                rem.setNotes(sb.toString());

                rem.setDateTime(DateTime.now().getMillis());//Set the time
                rem.setNotificationScale(NotificationScale.SAMETIME);//Set scale
                rem.setPattern(reminder.getPattern());
                System.out.println("Adding a notification for now with "+reminder.getPattern());
                ReminderHandler.setReminder(this.context,rem);
                reminder.setSmartReminded(true);// set the old reminder as smart reminded so it doesn't fire off again
                ReminderHandler.updateReminder(context,reminder);
            }
        }
        BaseReminder r= new BaseReminder(DateTime.now().getMillis());
        r.setReminderType(ReminderType.PROMPT);
        r.setDateTime(DateTime.now().getMillis());//Set the time
        r.setPromptLevel(2);
        r.setNotes("You have initiated SmartReminding");
        ReminderHandler.setReminder(this.context,r);
    }

    private static PendingIntent getReminderIntent(Context context, ArrayList<ArrayList<BaseReminder>> reminders) {
        Intent intent = new Intent(context, SmartReceiver.class);
        intent.putExtra(Consts.REMINDER_SUGGEST, true);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public void add_test_reminder(BaseReminder reminder){
        ReminderHandler.setReminder(this.context,reminder);
    };

    public ArrayList<ArrayList<BaseReminder>> get_Modified_Pattern_Reminders(ArrayList<ArrayList<BaseReminder>> reminders){
        System.out.println(reminders);
        if (reminders == null ) System.out.println("reminders in get_modified is null");
        //get database reference
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        //get android device's unique id
        //get android device's unique id or name
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        Boolean smart_login = sharedPreferences.getBoolean("smart_login",false);
        String smart_login_name = sharedPreferences.getString("smart_login_name",null);
        final String  android_id;
        if (smart_login){
            android_id=smart_login_name;
        }else{
            android_id= Settings.Secure.getString(this.context.getContentResolver(), Settings.Secure.ANDROID_ID);

        }

        final ArrayList<ArrayList<BaseReminder>> reminder_list=reminders;
        final ArrayList<ArrayList<BaseReminder>> final_reminder_list=new ArrayList<ArrayList<BaseReminder>>();
        //add one time event listener to update data
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get the mapped database values
                HashMap map = (HashMap) dataSnapshot.getValue();
                boolean check = (boolean) ((HashMap) map.get(android_id)).get("smart_reminding");

                if (check){
                    //Get the data from the database
                    long weekly_prompts_accepted = (long) ((HashMap) map.get(android_id)).get("weekly_prompts_accepted");
                    long two_week_prompts_accepted = (long) ((HashMap) map.get(android_id)).get("two_week_prompts_accepted");
                    long monthly_prompts_accepted = (long) ((HashMap) map.get(android_id)).get("monthly_prompts_accepted");
                    long weekly_prompts = (long) ((HashMap) map.get(android_id)).get("weekly_prompts");
                    long two_week_prompts= (long) ((HashMap) map.get(android_id)).get("two_week_prompts");
                    long monthly_prompts = (long) ((HashMap) map.get(android_id)).get("monthly_prompts");
                    long total_prompts = (long) ((HashMap) map.get(android_id)).get("total_prompts");
                    long prompts_accepted = (long) ((HashMap) map.get(android_id)).get("prompts_accepted");

                    //Have enough reminders to actually calculate some data.
                    //The maximum amount of reminders are 6
                    if (prompts_accepted > 10 && reminder_list.size()>6 ) {
                        //calculate the ratios of accepted reminder patterns
                        double weekly_prompts_used_ratio = weekly_prompts_accepted/weekly_prompts;
                        double two_week_prompts_used_ratio = two_week_prompts_accepted/two_week_prompts;
                        double monthly_prompts_used_ratio = monthly_prompts_accepted/monthly_prompts;

                        //get all the reminders and seperate them to pattern types
                        ArrayList<BaseReminder> one_week_reminders=reminder_list.get(0);
                        ArrayList<BaseReminder> two_week_reminders=reminder_list.get(1);
                        ArrayList<BaseReminder> monthly_reminders=reminder_list.get(2);

                        //Set counts based on the ratios of the patterns
                        //The multiplication by 5 sets the reminders to never pass the maximum of 6 reminders
                        int outbound_weekly_reminders_count = (int)Math.floor(weekly_prompts_used_ratio*5);
                        int outbound_two_week_reminders_count = (int)Math.floor(two_week_prompts_used_ratio*5);
                        int outbound_monthly_reminders_count = (int)Math.floor(monthly_prompts_used_ratio*5);

                        //Add to the final reminder list the limited amount of reminders
                        ArrayList<BaseReminder> list=new ArrayList<BaseReminder>();
                        for (int i =0;i<outbound_weekly_reminders_count;i++){
                            list.add(one_week_reminders.get(i));
                        }
                        final_reminder_list.add(list);
                        list = new ArrayList<BaseReminder>();
                        for (int i =0;i<outbound_two_week_reminders_count;i++){
                            list.add(two_week_reminders.get(i));
                        }
                        final_reminder_list.add(list);
                        list = new ArrayList<BaseReminder>();
                        for (int i =0;i<outbound_monthly_reminders_count;i++){
                            list.add(monthly_reminders.get(i));
                        }
                        final_reminder_list.add(list);
                    }

                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });
        if (reminders == null ) System.out.println("return reminders in get_modified is null");
        if (final_reminder_list == null ) System.out.println("final_reminder_list in get_modified is null");
        if (final_reminder_list.size()>0 ) {
            System.out.println("Returning final_reminder_list");
            return final_reminder_list;
        } else {
            System.out.println("Returning reminders");
            return reminders;
        }

    }
}
