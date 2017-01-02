package highway62.reminderapp.fragmentHandlers;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import highway62.reminderapp.R;
import highway62.reminderapp.ReminderActivity;
import highway62.reminderapp.actionbarlisteners.OnHomeListener;
import highway62.reminderapp.adminSettings.SettingsInterface;
import highway62.reminderapp.constants.EventType;
import highway62.reminderapp.constants.ReminderType;
import highway62.reminderapp.fragments.BSDTAppointmentFragment;
import highway62.reminderapp.fragments.BSDTBirthdayFragment;
import highway62.reminderapp.fragments.BSDTDailyFragment;
import highway62.reminderapp.fragments.BSDTMedicFragment;
import highway62.reminderapp.fragments.BSDTShoppingFragment;
import highway62.reminderapp.fragments.BSDTSocialFragment;
import highway62.reminderapp.fragments.BSFragment;
import highway62.reminderapp.reminderhandlers.ReminderHandler;
import highway62.reminderapp.reminders.BaseReminder;

/**
 * Created by Highway62 on 02/08/2016.
 */
public class BSFragmentHandler implements OnBSFragmentListener {

    private ReminderActivity context;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private SettingsInterface settings;
    private BaseReminder reminder;
    private ActionBar actionBar;
    private OnHomeListener listener;
    private boolean editMode = false;

    public BSFragmentHandler(Activity c, EventType eventType, OnHomeListener listener) {
        context = (ReminderActivity) c;
        fm = context.getFragmentManager();
        this.listener = listener;
        reminder = new BaseReminder();
        reminder.setType(eventType);
        reminder.setReminderType(ReminderType.REMINDER);
        actionBar = context.getSupportActionBar();
        settings = context.getSettingsSingleton();
        setupActionBar();
        // Display the UI based on the settings
        displayUI(eventType);
    }

    public BSFragmentHandler(Activity c, EventType eventType, OnHomeListener listener, BaseReminder reminder) {
        context = (ReminderActivity) c;
        fm = context.getFragmentManager();
        this.listener = listener;
        this.reminder = reminder;
        actionBar = context.getSupportActionBar();
        settings = context.getSettingsSingleton();
        setupActionBar();
        // Display the UI based on the settings
        displayUI(eventType);
        editMode = true;
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

    private void displayUI(EventType eventType) {
        ft = fm.beginTransaction();
        switch (eventType) {
            case GEN: {
                BSFragment frag = BSFragment.newInstance();
                frag.setHandler(this);
                ft.replace(R.id.tabReminderContainer, frag, context.getString(R.string.bs_gen_tag));
                break;
            }
            case APPT: {
                BSDTAppointmentFragment frag = BSDTAppointmentFragment.newInstance();
                frag.setHandler(this);
                ft.replace(R.id.tabReminderContainer, frag, context.getString(R.string.bsdt_appt_tag));
                break;
            }
            case SHOPPING: {
                BSDTShoppingFragment frag = BSDTShoppingFragment.newInstance();
                frag.setHandler(this);
                ft.replace(R.id.tabReminderContainer, frag, context.getString(R.string.bsdt_shopping_tag));
                break;
            }
            case BIRTH: {
                BSDTBirthdayFragment frag = BSDTBirthdayFragment.newInstance();
                frag.setHandler(this);
                ft.replace(R.id.tabReminderContainer, frag, context.getString(R.string.bsdt_birthday_tag));
                break;
            }
            case MEDIC: {
                BSDTMedicFragment frag = BSDTMedicFragment.newInstance();
                frag.setHandler(this);
                ft.replace(R.id.tabReminderContainer, frag, context.getString(R.string.bsdt_medic_tag));
                break;
            }
            case DAILY: {
                BSDTDailyFragment frag = BSDTDailyFragment.newInstance();
                frag.setHandler(this);
                ft.replace(R.id.tabReminderContainer, frag, context.getString(R.string.bsdt_daily_tag));
                break;
            }
            case SOCIAL: {
                BSDTSocialFragment frag = BSDTSocialFragment.newInstance();
                frag.setHandler(this);
                ft.replace(R.id.tabReminderContainer, frag, context.getString(R.string.bsdt_social_tag));
                break;
            }
        }

        ft.commit();
    }

    @Override
    public void resetAfterSave() {
        Toast.makeText(context, "Reminder Saved",Toast.LENGTH_SHORT).show();

        if(editMode){
            ReminderHandler.updateReminder(context, getReminder());
        }else{
            ReminderHandler.setReminder(context, getReminder());
        }

        resetReminder();
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
        displayUI(EventType.SHOPPING);
    }

    @Override
    public BaseReminder getReminder() {
        return reminder;
    }

    @Override
    public void onHomePressed() {
        listener.onHomePressed();
    }

    private void resetReminder(){
        this.reminder = new BaseReminder();
    }

}