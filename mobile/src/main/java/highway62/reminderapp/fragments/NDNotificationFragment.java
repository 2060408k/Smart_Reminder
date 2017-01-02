package highway62.reminderapp.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.joda.time.DateTime;

import highway62.reminderapp.R;
import highway62.reminderapp.ReminderActivity;
import highway62.reminderapp.adminSettings.SettingsInterface;
import highway62.reminderapp.constants.EventType;
import highway62.reminderapp.constants.NotificationScale;
import highway62.reminderapp.dialogs.EventDialogListener;
import highway62.reminderapp.dialogs.EventNotificationDialog;
import highway62.reminderapp.fragmentHandlers.OnNDFragmentListener;

public class NDNotificationFragment extends Fragment {

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

    private Spinner eventNotificationSpinner;
    private boolean spinnerSetAuto = false;
    private TextView eventNotificationCustomTxt;
    private RelativeLayout eventNotificationTxtHolder;
    private int eventNotificationCustomTimeNo = -1;
    private int eventNotificationCustomScaleNo = -1;
    private String[] customEventNotificationArray;
    private RelativeLayout eventNotificationWarning;


    public NDNotificationFragment() {}


    public static NDNotificationFragment newInstance(EventType typeOfEvent) {
        NDNotificationFragment fragment = new NDNotificationFragment();
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
        View v = inflater.inflate(R.layout.fragment_nd_notification, container, false);
        titleText = (TextView) v.findViewById(R.id.frag_nd_title_text);
        subtitleText = (TextView) v.findViewById(R.id.frag_nd_subtitle_text);
        navNext = (TextView) v.findViewById(R.id.frag_nd_nextBtn);
        navBack = (TextView) v.findViewById(R.id.frag_nd_backBtn);

        // NOTIFICATION
        eventNotificationSpinner = (Spinner) v.findViewById(R.id.frag_bs_event_notification_spinner);
        eventNotificationCustomTxt = (TextView) v.findViewById(R.id.frag_bs_notification_custom_txt);
        eventNotificationTxtHolder = (RelativeLayout) v.findViewById(R.id.frag_bs_notification_custom_txt_holder);
        eventNotificationWarning = (RelativeLayout) v.findViewById(R.id.notification_warning);
        eventNotificationWarning.setVisibility(View.GONE);
        setupNotification();

        setupTextViewContent();
        setupNavigationButtons();
        prePopulateFields();

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        handler = null;
    }

    private  void prePopulateFields(){
        // Notification
        if (handler.getReminder().getNotificationScale() != null) {
            int nTime = handler.getReminder().getNotificationTime();
            NotificationScale nScale = handler.getReminder().getNotificationScale();
            int timeScaleIndex = 0;
            switch (nScale) {
                case SAMETIME:
                    eventNotificationSpinner.setSelection(0);
                    break;
                case MINS:
                    if (nTime == 30) {
                        eventNotificationSpinner.setSelection(1);
                    } else {
                        eventNotificationSpinner.setSelection(4); // Custom
                        timeScaleIndex = 0;
                    }
                    break;
                case HOURS:
                    if (nTime == 1) {
                        eventNotificationSpinner.setSelection(2);
                    } else {
                        eventNotificationSpinner.setSelection(4); // Custom
                        timeScaleIndex = 1;
                    }
                    break;
                case DAYS:
                    if (nTime == 1) {
                        eventNotificationSpinner.setSelection(3);
                    } else {
                        eventNotificationSpinner.setSelection(4);
                        timeScaleIndex = 2;
                    }
                    break;
                case WEEKS:
                    eventNotificationSpinner.setSelection(4);
                    timeScaleIndex = 3;
                    break;
            }

            if (eventNotificationSpinner.getSelectedItemPosition() == 4) {
                spinnerSetAuto = true;
                eventNotificationCustomTxt.setVisibility(View.VISIBLE);
                String timeScale = customEventNotificationArray[timeScaleIndex];
                eventNotificationCustomTxt.setText(nTime + " " + timeScale);
                setNotificationCustomTimeNo(nTime); // Gets passed to dialog if opened
                setEventNotificationCustomScaleNo(timeScaleIndex);
            }
        }

        // Set the notification spinner listener after checking the reminder object
        setNotificationSpinnerListener();
    }

    public void setHandler(OnNDFragmentListener handler) {
        this.handler = handler;
    }

    // NOTIFICATION SETTINGS ---------------------------------------------------------------------//

    private void setupNotification() {

        customEventNotificationArray = getResources()
                .getStringArray(R.array.frag_bs_gen_event_notification_custom_array);
        ArrayAdapter<CharSequence> notificationAdapter = ArrayAdapter.createFromResource(context,
                R.array.frag_bs_gen_event_notification_array, android.R.layout.simple_spinner_item);
        notificationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventNotificationSpinner.setAdapter(notificationAdapter);

        // Set listener for the custom text to re-open dialog
        eventNotificationTxtHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomNotificationDialog(eventNotificationCustomTimeNo
                        , eventNotificationCustomScaleNo);
            }
        });
        // Hide the custom text initially
        eventNotificationCustomTxt.setVisibility(View.GONE);
    }

    private void setNotificationSpinnerListener() {
        eventNotificationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        disableCustomNotification();
                        setNotificationWarning(1, NotificationScale.SAMETIME);
                        break;
                    case 1:
                        disableCustomNotification();
                        setNotificationWarning(30, NotificationScale.MINS);
                        break;
                    case 2:
                        disableCustomNotification();
                        setNotificationWarning(1, NotificationScale.HOURS);
                        break;
                    case 3:
                        disableCustomNotification();
                        setNotificationWarning(1, NotificationScale.DAYS);
                        break;
                    case 4: // Custom
                        if (!spinnerSetAuto) {
                            showCustomNotificationDialog(eventNotificationCustomTimeNo, eventNotificationCustomScaleNo);
                        } else {
                            spinnerSetAuto = false;
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                eventNotificationSpinner.setSelection(0);
            }
        });
    }

    private void showCustomNotificationDialog(int existingValue, int existingIndex) {
        EventNotificationDialog eventNotificationDialog;
        if (existingValue == -1) {
            eventNotificationDialog = new EventNotificationDialog(context);
        } else {
            eventNotificationDialog = new EventNotificationDialog(context, existingValue, existingIndex);
        }

        eventNotificationDialog.setEventDialogListener(new EventDialogListener() {
            @Override
            public void passResults(int number, int timeIndex) {
                eventNotificationCustomTxt.setVisibility(View.VISIBLE);
                String timeValue = customEventNotificationArray[timeIndex];
                eventNotificationCustomTxt.setText(number + " " + timeValue);
                setNotificationCustomTimeNo(number);
                setEventNotificationCustomScaleNo(timeIndex);
                // Set warning
                NotificationScale nScale;
                switch (timeIndex) {
                    case 0:
                        nScale = NotificationScale.MINS;
                        break;
                    case 1:
                        nScale = NotificationScale.HOURS;
                        break;
                    case 2:
                        nScale = NotificationScale.DAYS;
                        break;
                    case 3:
                        nScale = NotificationScale.WEEKS;
                        break;
                    default:
                        nScale = NotificationScale.MINS;
                }
                setNotificationWarning(eventNotificationCustomTimeNo, nScale);
            }

            @Override
            public void passResults(int numberOfWeeks) {
            }
        });
        eventNotificationDialog.show();
    }

    protected void setNotificationCustomTimeNo(int i) {
        eventNotificationCustomTimeNo = i;
    }

    protected void setEventNotificationCustomScaleNo(int i) {
        eventNotificationCustomScaleNo = i;
    }

    private void disableCustomNotification() {
        eventNotificationCustomTimeNo = -1;
        eventNotificationCustomScaleNo = -1;
        eventNotificationCustomTxt.setText("");
        eventNotificationCustomTxt.setVisibility(View.GONE);
    }

    private boolean notificationIsSetBeforeNow(int time, NotificationScale scale) {

        DateTime dateTimeChosen = new DateTime(handler.getReminder().getDateTime());

        DateTime curTime = new DateTime();

        DateTime notificationTime;
        switch (scale) {
            case SAMETIME:
                // Add a milli to stop waning on startup
                notificationTime = dateTimeChosen.plusMillis(time);
                break;
            case MINS:
                notificationTime = dateTimeChosen.minusMinutes(time);
                break;
            case HOURS:
                notificationTime = dateTimeChosen.minusHours(time);
                break;
            case DAYS:
                notificationTime = dateTimeChosen.minusDays(time);
                break;
            case WEEKS:
                notificationTime = dateTimeChosen.minusWeeks(time);
                break;
            default:
                notificationTime = dateTimeChosen;
                break;
        }

        if (notificationTime.isBefore(curTime)) {
            return true;
        } else {
            return false;
        }
    }

    private void setNotificationWarning(int time, NotificationScale scale) {
        if (notificationIsSetBeforeNow(time, scale)) {
            eventNotificationWarning.setVisibility(View.VISIBLE);
        } else {
            eventNotificationWarning.setVisibility(View.GONE);
        }
    }
    // -------------------------------------------------------------------------------------------//

    // -------------------------------------------------------------------------------------------//

    // CATEGORY SPECIFIC CONTENT -----------------------------------------------------------------//
    private void setupTextViewContent() {
        // Title and subtitle for notification
        titleText.setText(context.getString(R.string.frag_nd_notification_gen));
        subtitleText.setText(context.getString(R.string.frag_nd_notification_gen_subtitle));
    }
    // -------------------------------------------------------------------------------------------//

    // NAVIGATION LISTENERS ----------------------------------------------------------------------//
    private void setupNavigationButtons() {

        navBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveChoices();

                switch (typeOfEvent){
                    case GEN:
                        // Navigate to duration frag
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_duration_tag));
                        break;
                    case APPT:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_duration_tag));
                        break;
                    case SHOPPING:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_time_tag));
                        break;
                    case BIRTH:
                        break;
                    case MEDIC:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_time_tag));
                        break;
                    case DAILY:
                        break;
                    case SOCIAL:
                        break;
                }
            }
        });

        navNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveChoices();

                switch (typeOfEvent){
                    case GEN:
                        // Navigate to notes frag
                        handler.navigateFragmentForward(typeOfEvent,context.getString(R.string.nd_notes_tag));
                        break;
                    case APPT:
                        handler.navigateFragmentForward(typeOfEvent,context.getString(R.string.nd_notes_tag));
                        break;
                    case SHOPPING:
                        handler.navigateFragmentForward(typeOfEvent,context.getString(R.string.nd_notes_tag));
                        break;
                    case BIRTH:
                        break;
                    case MEDIC:
                        handler.navigateFragmentForward(typeOfEvent,context.getString(R.string.nd_repeat_tag));
                        break;
                    case DAILY:
                        break;
                    case SOCIAL:
                        break;
                }
            }
        });

    }
    // -------------------------------------------------------------------------------------------//

    private void saveChoices(){
        // Notification
        switch (eventNotificationSpinner.getSelectedItemPosition()) {
            case 0:
                handler.getReminder().setNotificationScale(NotificationScale.SAMETIME);
                break;
            case 1:
                if (notificationIsSetBeforeNow(30, NotificationScale.MINS)) {
                    handler.getReminder().setNotificationScale(NotificationScale.SAMETIME);
                } else {
                    handler.getReminder().setNotificationTime(30);
                    handler.getReminder().setNotificationScale(NotificationScale.MINS);
                }
                break;
            case 2:
                if (notificationIsSetBeforeNow(1, NotificationScale.HOURS)) {
                    handler.getReminder().setNotificationScale(NotificationScale.SAMETIME);
                } else {
                    handler.getReminder().setNotificationTime(1);
                    handler.getReminder().setNotificationScale(NotificationScale.HOURS);
                }
                break;
            case 3:
                if (notificationIsSetBeforeNow(1, NotificationScale.DAYS)) {
                    handler.getReminder().setNotificationScale(NotificationScale.SAMETIME);
                } else {
                    handler.getReminder().setNotificationTime(1);
                    handler.getReminder().setNotificationScale(NotificationScale.DAYS);
                }
                break;
            case 4:
                if (eventNotificationCustomTimeNo != -1) {
                    if (eventNotificationCustomScaleNo == 0) {
                        if (notificationIsSetBeforeNow(eventNotificationCustomTimeNo, NotificationScale.MINS)) {
                            handler.getReminder().setNotificationScale(NotificationScale.SAMETIME);
                        } else {
                            handler.getReminder().setNotificationTime(eventNotificationCustomTimeNo);
                            handler.getReminder().setNotificationScale(NotificationScale.MINS);
                        }
                    } else if (eventNotificationCustomScaleNo == 1) {
                        if (notificationIsSetBeforeNow(eventNotificationCustomTimeNo, NotificationScale.HOURS)) {
                            handler.getReminder().setNotificationScale(NotificationScale.SAMETIME);
                        } else {
                            handler.getReminder().setNotificationTime(eventNotificationCustomTimeNo);
                            handler.getReminder().setNotificationScale(NotificationScale.HOURS);
                        }
                    } else if (eventNotificationCustomScaleNo == 2) {
                        if (notificationIsSetBeforeNow(eventNotificationCustomTimeNo, NotificationScale.DAYS)) {
                            handler.getReminder().setNotificationScale(NotificationScale.SAMETIME);
                        } else {
                            handler.getReminder().setNotificationTime(eventNotificationCustomTimeNo);
                            handler.getReminder().setNotificationScale(NotificationScale.DAYS);
                        }
                    } else if (eventNotificationCustomScaleNo == 3) {
                        if (notificationIsSetBeforeNow(eventNotificationCustomTimeNo, NotificationScale.WEEKS)) {
                            handler.getReminder().setNotificationScale(NotificationScale.SAMETIME);
                        } else {
                            handler.getReminder().setNotificationTime(eventNotificationCustomTimeNo);
                            handler.getReminder().setNotificationScale(NotificationScale.WEEKS);
                        }
                    }
                } else {
                    handler.getReminder().setNotificationScale(NotificationScale.SAMETIME);
                }
                break;
        }
    }

}
