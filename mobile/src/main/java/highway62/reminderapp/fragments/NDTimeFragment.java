package highway62.reminderapp.fragments;


import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import highway62.reminderapp.R;
import highway62.reminderapp.ReminderActivity;
import highway62.reminderapp.adminSettings.SettingsInterface;
import highway62.reminderapp.constants.EventType;
import highway62.reminderapp.fragmentHandlers.OnNDFragmentListener;
import highway62.reminderapp.pickers.TimePicker;

public class NDTimeFragment extends Fragment {

    private static final String EVENT_TYPE = "event_type";
    private EventType typeOfEvent;
    private OnNDFragmentListener handler;
    private ReminderActivity context;
    private ActionBar actionBar;
    private SettingsInterface settings;

    private TextView titleText;
    private TextView subtitleText;
    private TextView navNext;
    private TextView navBack;
    private TimePicker timePicker;
    private TextView timeInfoText;


    public NDTimeFragment() {}

    public static NDTimeFragment newInstance(EventType typeOfEvent) {
        NDTimeFragment fragment = new NDTimeFragment();
        Bundle args = new Bundle();
        args.putSerializable(EVENT_TYPE, typeOfEvent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = (ReminderActivity) getActivity();
        settings = context.getSettingsSingleton();
        actionBar = context.getSupportActionBar();

        if (getArguments() != null) {
            typeOfEvent = (EventType) getArguments().getSerializable(EVENT_TYPE);
        } else {
            typeOfEvent = EventType.GEN;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_nd_time, container, false);

        titleText = (TextView) v.findViewById(R.id.frag_nd_title_text);
        subtitleText = (TextView) v.findViewById(R.id.frag_nd_subtitle_text);
        navNext = (TextView) v.findViewById(R.id.frag_nd_nextBtn);
        navBack = (TextView) v.findViewById(R.id.frag_nd_backBtn);
        timePicker = (TimePicker) v.findViewById(R.id.frag_nd_timePicker);
        timeInfoText = (TextView) v.findViewById(R.id.time_info_text);

        setupTimePicker();
        setupTextViewContent();
        setupNavigationButtons();
        prepopulateFields();

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        handler = null;
    }

    private void prepopulateFields(){
        //Time
        if (handler.getReminder().getDateTime() > -1) {
            long dateTimeLong = handler.getReminder().getDateTime();
            DateTime dt = new DateTime(dateTimeLong);
            timePicker.setCurrentHour(dt.getHourOfDay());
            timePicker.setCurrentMinute(dt.getMinuteOfHour());
        }
    }

    public void setHandler(OnNDFragmentListener handler) {
        this.handler = handler;
    }

    // TIME SETTINGS -----------------------------------------------------------------------------//
    private void setupTimePicker() {

        setTimeText(timePicker.getCurrentHour());

        timePicker.setOnTimeChangedListener(new android.widget.TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(android.widget.TimePicker view, int hourOfDay, int minute) {
                setTime(hourOfDay, minute);
            }
        });
    }

    private void setTime(int hourOfDay, int minute){
        DateTime dateChosen = handler.getTempDate();

        DateTime curTime = new DateTime();

        if (dateChosen.isBeforeNow() || dateChosen.isEqualNow()) {
            if (hourOfDay <= curTime.getHourOfDay()) {
                timePicker.setCurrentHour(curTime.getHourOfDay());
                if (minute < curTime.getMinuteOfHour()) {
                    timePicker.setCurrentMinute(curTime.getMinuteOfHour());
                }
            }
        }

        setTimeText(timePicker.getCurrentHour());
    }

    private void setTimeText(int hourOfDay) {
        if (hourOfDay >= 17 || hourOfDay == 0 || hourOfDay == 1) {
            timeInfoText.setText("Evening");
        } else if (hourOfDay >= 2 && hourOfDay <= 6) {
            timeInfoText.setText("Early Morning");
        } else if (hourOfDay >= 7 && hourOfDay <= 11) {
            timeInfoText.setText("Morning");
        } else if (hourOfDay >= 12 && hourOfDay < 17) {
            timeInfoText.setText("Afternoon");
        }
    }
    // -------------------------------------------------------------------------------------------//

    // CATEGORY SPECIFIC CONTENT -----------------------------------------------------------------//
    private void setupTextViewContent() {
        // Title for time (may not change)
        titleText.setText(context.getString(R.string.frag_nd_time_gen));
        switch (typeOfEvent) {
            case GEN:
                subtitleText.setText(context.getString(R.string.frag_nd_time_gen_subtitle));
                break;
            case APPT:
                subtitleText.setText(context.getString(R.string.frag_nd_time_appt_subtitle));
                break;
            case SHOPPING:
                subtitleText.setText(context.getString(R.string.frag_nd_time_shopping_subtitle));
                break;
            case BIRTH:
                subtitleText.setText(context.getString(R.string.frag_nd_time_birth_subtitle));
                break;
            case MEDIC:
                subtitleText.setText(context.getString(R.string.frag_nd_time_medic_subtitle));
                break;
            case DAILY:
                subtitleText.setText(context.getString(R.string.frag_nd_time_daily_subtitle));
                break;
            case SOCIAL:
                subtitleText.setText(context.getString(R.string.frag_nd_time_social_subtitle));
                if(!settings.getUserSubtletyEnabled()){
                    Drawable img = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_nav_save, null);
                    navNext.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
                    navNext.setText("Save");
                }
                break;
        }
    }
    // -------------------------------------------------------------------------------------------//

    // NAVIGATION LISTENERS ----------------------------------------------------------------------//
    private void setupNavigationButtons() {

        navBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveChoice();

                switch(typeOfEvent){
                    case GEN:
                        // Navigate to date frag
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_date_tag));
                        break;
                    case APPT:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_date_tag));
                        break;
                    case SHOPPING:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_date_tag));
                        break;
                    case BIRTH:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_date_tag));
                        break;
                    case MEDIC:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_date_tag));
                        break;
                    case DAILY:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_date_tag));
                        break;
                    case SOCIAL:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_date_tag));
                        break;
                }
            }
        });

        navNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveChoice();

                switch(typeOfEvent){
                    case GEN:
                        // Navigate to duration frag
                        handler.navigateFragmentForward(typeOfEvent,context.getString(R.string.nd_duration_tag));
                        break;
                    case APPT:
                        handler.navigateFragmentForward(typeOfEvent,context.getString(R.string.nd_duration_tag));
                        break;
                    case SHOPPING:
                        handler.navigateFragmentForward(typeOfEvent,context.getString(R.string.nd_notification_tag));
                        break;
                    case BIRTH:
                        handler.navigateFragmentForward(typeOfEvent,context.getString(R.string.nd_notes_tag));
                        break;
                    case MEDIC:
                        handler.navigateFragmentForward(typeOfEvent,context.getString(R.string.nd_notification_tag));
                        break;
                    case DAILY:
                        handler.navigateFragmentForward(typeOfEvent,context.getString(R.string.nd_repeat_tag));
                        break;
                    case SOCIAL:
                        if(settings.getUserSubtletyEnabled()){
                            handler.navigateFragmentForward(typeOfEvent,context.getString(R.string.nd_reminder_level_tag));
                        }else{
                            handler.resetAfterSave(typeOfEvent);
                        }
                        break;
                }
            }
        });

    }
    // -------------------------------------------------------------------------------------------//

    private void saveChoice(){
        DateTime dt = handler.getTempDate()
                .withHourOfDay(timePicker.getCurrentHour())
                .withMinuteOfHour(timePicker.getCurrentMinute())
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        handler.getReminder().setDateTime(dt.getMillis());
    }

}
