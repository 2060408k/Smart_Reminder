package highway62.reminderapp.fragmentHandlers;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import org.joda.time.DateTime;

import highway62.reminderapp.R;
import highway62.reminderapp.ReminderActivity;
import highway62.reminderapp.actionbarlisteners.OnHomeListener;
import highway62.reminderapp.adminSettings.SettingsInterface;
import highway62.reminderapp.constants.EventType;
import highway62.reminderapp.constants.ReminderType;
import highway62.reminderapp.fragments.NDDateFragment;
import highway62.reminderapp.fragments.NDDurationFragment;
import highway62.reminderapp.fragments.NDEventAfterFragment;
import highway62.reminderapp.fragments.NDEventTitleFragment;
import highway62.reminderapp.fragments.NDNotesFragment;
import highway62.reminderapp.fragments.NDNotificationFragment;
import highway62.reminderapp.fragments.NDPromptFragment;
import highway62.reminderapp.fragments.NDRepeatFragment;
import highway62.reminderapp.fragments.NDTimeFragment;
import highway62.reminderapp.fragments.NDWhereFragment;
import highway62.reminderapp.reminderhandlers.ReminderHandler;
import highway62.reminderapp.reminders.BaseReminder;

/**
 * Created by Highway62 on 03/08/2016.
 */
public class NDFragmentHandler implements OnNDFragmentListener {

    private ReminderActivity context;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private SettingsInterface settings;
    private ActionBar actionBar;
    private BaseReminder reminder;
    private DateTime tempDate;
    private OnHomeListener listener;
    private boolean editMode = false;

    public NDFragmentHandler(Activity c, EventType eventType, OnHomeListener listener) {
        this.context = (ReminderActivity) c;
        fm = context.getFragmentManager();
        this.listener = listener;
        settings = context.getSettingsSingleton();
        actionBar = context.getSupportActionBar();
        displayUI(eventType);
        setupActionBar();
        reminder = new BaseReminder();
        reminder.setType(eventType);
        reminder.setReminderType(ReminderType.REMINDER);
    }

    public NDFragmentHandler(Activity c, EventType eventType, OnHomeListener listener ,BaseReminder reminder) {
        this.context = (ReminderActivity) c;
        fm = context.getFragmentManager();
        this.listener = listener;
        settings = context.getSettingsSingleton();
        actionBar = context.getSupportActionBar();
        displayUI(eventType);
        setupActionBar();
        this.reminder = reminder;
        editMode = true;
    }

    private void displayUI(EventType eventType){
        NDEventTitleFragment frag = NDEventTitleFragment
                .newInstance(eventType);
        frag.setHandler(this);
        fm.beginTransaction()
                .replace(R.id.tabReminderContainer, frag)
                .commit();
    }

    private void setupActionBar(){
        if(settings.getUIDecisionTreeEnabled()){
            // Hide App Name
            actionBar.setDisplayShowTitleEnabled(false);
            // Show custom title text
            LayoutInflater mInflater = LayoutInflater.from(context);
            View mCustomView = mInflater.inflate(R.layout.actionbar_title, null);

            mCustomView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onHomePressed();
                }
            });

            actionBar.setCustomView(mCustomView);
            actionBar.setDisplayShowCustomEnabled(true);
        }else{
            // Hide custom title text
            actionBar.setDisplayShowCustomEnabled(false);
            // Show app name
            actionBar.setDisplayShowTitleEnabled(true);
        }
    }

    // NAVIGATION METHODS ----------------------------------------------------------------------//
    @Override
    public void navigateFragmentForward(EventType eventType, String tag) {
        ft = fm.beginTransaction();
        ft.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left);

        if(tag.equals(context.getString(R.string.nd_date_tag))){
            NDDateFragment frag = NDDateFragment.newInstance(eventType);
            frag.setHandler(this);
            ft.replace(R.id.tabReminderContainer, frag, tag);
        }else if(tag.equals(context.getString(R.string.nd_time_tag))){
            NDTimeFragment frag = NDTimeFragment.newInstance(eventType);
            frag.setHandler(this);
            ft.replace(R.id.tabReminderContainer, frag, tag);
        }else if(tag.equals(context.getString(R.string.nd_duration_tag))){
            NDDurationFragment frag = NDDurationFragment.newInstance(eventType);
            frag.setHandler(this);
            ft.replace(R.id.tabReminderContainer, frag, tag);
        }else if(tag.equals(context.getString(R.string.nd_notification_tag))){
            NDNotificationFragment frag = NDNotificationFragment.newInstance(eventType);
            frag.setHandler(this);
            ft.replace(R.id.tabReminderContainer, frag, tag);
        }else if(tag.equals(context.getString(R.string.nd_notes_tag))){
            NDNotesFragment frag = NDNotesFragment.newInstance(eventType);
            frag.setHandler(this);
            ft.replace(R.id.tabReminderContainer, frag, tag);
        }else if(tag.equals(context.getString(R.string.nd_repeat_tag))){
            NDRepeatFragment frag = NDRepeatFragment.newInstance(eventType);
            frag.setHandler(this);
            ft.replace(R.id.tabReminderContainer, frag, tag);
        }else if(tag.equals(context.getString(R.string.nd_reminder_level_tag))){
            NDPromptFragment frag = NDPromptFragment.newInstance(eventType);
            frag.setHandler(this);
            ft.replace(R.id.tabReminderContainer, frag, tag);
        }else if(tag.equals(context.getString(R.string.nd_where_tag))){
            NDWhereFragment frag = NDWhereFragment.newInstance(eventType);
            frag.setHandler(this);
            ft.replace(R.id.tabReminderContainer, frag, tag);
        }else if(tag.equals(context.getString(R.string.nd_event_after_tag))){
            NDEventAfterFragment frag = NDEventAfterFragment.newInstance(eventType);
            frag.setHandler(this);
            ft.replace(R.id.tabReminderContainer, frag, tag);
        }

        ft.commit();
    }

    @Override
    public void navigateFragmentBack(EventType eventType, String tag) {
        ft = fm.beginTransaction();
        ft.setCustomAnimations(R.animator.enter_from_left, R.animator.exit_to_right);

        if(tag.equals(context.getString(R.string.nd_title_tag))){
            NDEventTitleFragment frag = NDEventTitleFragment.newInstance(eventType);
            frag.setHandler(this);
            ft.replace(R.id.tabReminderContainer, frag, tag);
        }else if(tag.equals(context.getString(R.string.nd_date_tag))){
            NDDateFragment frag = NDDateFragment.newInstance(eventType);
            frag.setHandler(this);
            ft.replace(R.id.tabReminderContainer, frag, tag);
        }else if(tag.equals(context.getString(R.string.nd_time_tag))){
            NDTimeFragment frag = NDTimeFragment.newInstance(eventType);
            frag.setHandler(this);
            ft.replace(R.id.tabReminderContainer, frag, tag);
        }else if(tag.equals(context.getString(R.string.nd_duration_tag))){
            NDDurationFragment frag = NDDurationFragment.newInstance(eventType);
            frag.setHandler(this);
            ft.replace(R.id.tabReminderContainer, frag, tag);
        }else if(tag.equals(context.getString(R.string.nd_notification_tag))){
            NDNotificationFragment frag = NDNotificationFragment.newInstance(eventType);
            frag.setHandler(this);
            ft.replace(R.id.tabReminderContainer, frag, tag);
        }else if(tag.equals(context.getString(R.string.nd_notes_tag))){
            NDNotesFragment frag = NDNotesFragment.newInstance(eventType);
            frag.setHandler(this);
            ft.replace(R.id.tabReminderContainer, frag, tag);
        }else if(tag.equals(context.getString(R.string.nd_repeat_tag))){
            NDRepeatFragment frag = NDRepeatFragment.newInstance(eventType);
            frag.setHandler(this);
            ft.replace(R.id.tabReminderContainer, frag, tag);
        }else if(tag.equals(context.getString(R.string.nd_where_tag))){
            NDWhereFragment frag = NDWhereFragment.newInstance(eventType);
            frag.setHandler(this);
            ft.replace(R.id.tabReminderContainer, frag, tag);
        }else if(tag.equals(context.getString(R.string.nd_event_after_tag))){
            NDEventAfterFragment frag = NDEventAfterFragment.newInstance(eventType);
            frag.setHandler(this);
            ft.replace(R.id.tabReminderContainer, frag, tag);
        }

        ft.commit();
    }

    @Override
    public void resetAfterSave(EventType eventType) {

        Toast.makeText(context, "Reminder Saved",Toast.LENGTH_SHORT).show();

        if(editMode){
            ReminderHandler.updateReminder(context, getReminder());
        }else{
            ReminderHandler.setReminder(context, getReminder());
        }

        resetReminder();
        resetTempDate();
        context.switchToScheduleTab();
        listener.onHomePressed();
    }

    @Override
    public void resetAfterSaveBirthday() {
        Toast.makeText(context, "Reminder Saved",Toast.LENGTH_SHORT).show();

        if(editMode){
            ReminderHandler.updateReminder(context, getReminder());
        }else{
            ReminderHandler.setReminder(context, getReminder());
        }

        resetReminder();
        resetTempDate();
        displayUI(EventType.SHOPPING);
    }

    @Override
    public BaseReminder getReminder() {
        return reminder;
    }

    private void resetReminder(){
        this.reminder = new BaseReminder();
    }

    @Override
    public void setTempDate(DateTime dt){
        this.tempDate = dt;
    }

    @Override
    public DateTime getTempDate(){
        return this.tempDate;
    }

    @Override
    public void onHomePressed() {
        listener.onHomePressed();
    }

    private void resetTempDate(){
        this.tempDate = null;
    }


}
