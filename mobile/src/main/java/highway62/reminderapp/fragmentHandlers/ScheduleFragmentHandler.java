package highway62.reminderapp.fragmentHandlers;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import highway62.reminderapp.R;
import highway62.reminderapp.ReminderActivity;
import highway62.reminderapp.fontSpan.TypefaceSpan;
import highway62.reminderapp.fragments.ScheduleViewDateFragment;
import highway62.reminderapp.reminderhandlers.ReminderHandler;
import highway62.reminderapp.reminders.BaseReminder;

/**
 * Created by Highway62 on 30/08/2016.
 */
public class ScheduleFragmentHandler implements OnScheduleFragmentListener{
    ReminderActivity context;
    private android.support.v4.app.FragmentManager fm;
    private android.support.v4.app.FragmentTransaction ft;
    ArrayList<BaseReminder> reminders;
    ActionBar actionBar;
    private CaldroidFragment calendarFrag;
    private InputMethodManager imm;

    /**
     * Map of ArrayLists which contain reminders for a given date.
     * EG. There may be 5 reminders set on 5/9/16.
     * The 5 reminders are put into Arraylist and stored at key: DateTime(5/9/16).
     * DateTime keys are set as the day and month, with hours,mins,seconds,milliseconds, all set to 0
     * When a user clicks on a calendar date, the Arraylist at that date in the map is passed to the
     * fragment displaying the list of reminders for that date.
     */
    private HashMap<DateTime, ArrayList<BaseReminder>> remindersForDates;

    public ScheduleFragmentHandler(ReminderActivity context){
        this.context = context;
        fm = context.getSupportFragmentManager();
        imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        actionBar = context.getSupportActionBar();
        hideKeyboard();
        setupActionBar();
        showCalendar();
    }

    private void setupActionBar(){
        if(actionBar != null){
            SpannableString s = new SpannableString(context.getString(R.string.app_name));
            s.setSpan(new TypefaceSpan(context,"adam.otf"), 0, s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            actionBar.setTitle(s);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setElevation(0);
        }
    }

    private void showCalendar(){

        calendarFrag = new CaldroidFragment();
        DateTime dt = new DateTime();
        Bundle args = new Bundle();
        args.putInt(CaldroidFragment.MONTH, dt.getMonthOfYear());
        args.putInt(CaldroidFragment.YEAR, dt.getYear());
        calendarFrag.setArguments(args);

        ft = fm.beginTransaction();
        ft.replace(R.id.tabScheduleContainer, calendarFrag);
        ft.commit();

        loadReminders();
    }

    private void loadReminders(){
        remindersForDates = new HashMap<>();
        new AsyncLoadReminders().execute();
    }

    private class AsyncLoadReminders extends AsyncTask<Void, Void, ArrayList<BaseReminder>>{

        @Override
        protected ArrayList<BaseReminder> doInBackground(Void... params) {
            return ReminderHandler.getAllReminders(context);
        }

        @Override
        protected void onPostExecute(ArrayList<BaseReminder> baseReminders) {
            super.onPostExecute(baseReminders);
            reminders = baseReminders;
            displayReminders(baseReminders);
        }
    }

    private void displayReminders(ArrayList<BaseReminder> reminders){

        for(BaseReminder reminder : reminders){
            DateTime dt = new DateTime(reminder.getDateTime())
                    .withHourOfDay(0)
                    .withMinuteOfHour(0)
                    .withSecondOfMinute(0)
                    .withMillisOfSecond(0);

            if(remindersForDates.containsKey(dt)){
                ArrayList<BaseReminder> dayRems = remindersForDates.get(dt);
                dayRems.add(0,reminder);
                remindersForDates.remove(dt);
                remindersForDates.put(dt, dayRems);
            }else{
                ArrayList<BaseReminder> dayRems = new ArrayList<>();
                dayRems.add(0,reminder);
                remindersForDates.put(dt, dayRems);
            }

            calendarFrag.setSelectedDate(dt.toDate());
        }

        final CaldroidListener onDateTouchListener = new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
                DateTime dt = new DateTime(date)
                        .withHourOfDay(0)
                        .withMinuteOfHour(0)
                        .withSecondOfMinute(0)
                        .withMillisOfSecond(0);
                if(remindersForDates.containsKey(dt)){
                    showRemindersForDateFrag(dt, remindersForDates.get(dt));
                }
            }
        };

        calendarFrag.setCaldroidListener(onDateTouchListener);
        calendarFrag.refreshView();
    }

    private void showRemindersForDateFrag(DateTime dt, ArrayList<BaseReminder> reminders){
        // Load scheduleViewDateFragment
        ScheduleViewDateFragment frag = ScheduleViewDateFragment.newInstance(reminders);
        frag.setHandler(this);
        frag.setDate(dt);
        ft = fm.beginTransaction();
        ft.replace(R.id.tabScheduleContainer, frag);
        ft.commit();
    }

    @Override
    public void navigateBack(){
        showCalendar();
    }

    private void hideKeyboard() {
        View view = context.getCurrentFocus();
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
