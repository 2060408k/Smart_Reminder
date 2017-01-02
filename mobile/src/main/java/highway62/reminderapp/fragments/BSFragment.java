package highway62.reminderapp.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import highway62.reminderapp.SmartReminding.SmartReminding;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.ArrayList;

import highway62.reminderapp.R;
import highway62.reminderapp.ReminderActivity;
import highway62.reminderapp.adminSettings.SettingsInterface;
import highway62.reminderapp.constants.Day;
import highway62.reminderapp.constants.DurationScale;
import highway62.reminderapp.constants.EventType;
import highway62.reminderapp.constants.NotificationScale;
import highway62.reminderapp.dialogs.EventDialogListener;
import highway62.reminderapp.dialogs.EventNotificationDialog;
import highway62.reminderapp.dialogs.EventRepeatDialog;
import highway62.reminderapp.fragmentHandlers.OnBSFragmentListener;
import highway62.reminderapp.pickers.DatePicker;
import highway62.reminderapp.pickers.TimePicker;
import highway62.reminderapp.reminders.BaseReminder;

public class BSFragment extends Fragment {

    private InputMethodManager imm;
    private ReminderActivity context;
    private ActionBar actionBar;
    private SettingsInterface settings;
    private OnBSFragmentListener handler;
    private ScrollView rootScrollView;

    private EditText eventTitleEditTxt;

    private DatePicker datePicker;
    private TextView dateInfoTextDay;
    private TextView dateInfoTextWhen;
    private TextView dateInfoTextDate;
    private static final String ARG_REMINDERS = "argReminders";
    private final String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday"
            , "Thursday", "Friday", "Saturday", "Sunday"};
    private TimePicker timePicker;
    private TextView timeInfoText;

    private EditText eventNotes;

    private Spinner eventDurationSpinner;
    private NumberPicker eventDurationNumPicker;
    private Switch eventDurationSwitch;

    private ArrayList<BaseReminder> reminders;
    private Spinner eventNotificationSpinner;
    private boolean spinnerSetAuto = false;
    private TextView eventNotificationCustomTxt;
    private RelativeLayout eventNotificationTxtHolder;
    private int eventNotificationCustomTimeNo = -1;
    private int eventNotificationCustomScaleNo = -1;
    private String[] customEventNotificationArray;
    private RelativeLayout eventNotificationWarning;

    private Switch eventRepeatSwitch;
    private RelativeLayout eventRepeatDaysText;
    private LinearLayout eventRepeatDaysGrid;
    private CheckBox eventRepeatCheckMon;
    private CheckBox eventRepeatCheckTues;
    private CheckBox eventRepeatCheckWed;
    private CheckBox eventRepeatCheckThur;
    private CheckBox eventRepeatCheckFri;
    private CheckBox eventRepeatCheckSat;
    private CheckBox eventRepeatCheckSun;
    private CheckBox[] repeatCheckBArray = new CheckBox[7];
    private RelativeLayout eventRepeatWeeksTxt;
    private Spinner eventRepeatWeeksSpinner;
    private TextView eventRepeatCustomWeeksText;
    private int eventRepeatCustomNoOfWeeks = -1;

    private CardView promptLevelCard;
    private SeekBar eventLevelSeekbar;
    private final int noOfPromptLevels = 3;
    private RelativeLayout eventLevel0;
    private RelativeLayout eventLevel1;
    private RelativeLayout eventLevel2;
    private RelativeLayout eventLevel3;
    private SmartReminding smartReminding;
    private Button saveButton;

    public BSFragment() {}

    public static BSFragment newInstance() {
        return new BSFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = (ReminderActivity) getActivity();
        //
        settings = context.getSettingsSingleton();
        actionBar = context.getSupportActionBar();
        System.out.println("meowleleis");
        if (getArguments() != null) {
            this.reminders = getArguments().getParcelableArrayList(ARG_REMINDERS);
            BaseReminder it=null;
            int i=0;
            while (i<this.reminders.size()){
                it = this.reminders.get(i);
                BaseReminder rem = (BaseReminder) it;
                DateTime dt = new DateTime(rem.getDateTime())
                        .withSecondOfMinute(0)
                        .withMillisOfSecond(0);
                System.out.println(dt);
                i++;
            }
        }
        imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bs, container, false);

        rootScrollView = (ScrollView) v.findViewById(R.id.bsFragRootScrollView);

        // EVENT TITLE
        eventTitleEditTxt = (EditText) v.findViewById(R.id.frag_bs_title_editTxt);
        setupTouchListenersForHidingKeyboard();

        // EVENT DATE
        datePicker = (DatePicker) v.findViewById(R.id.frag_bs_datePicker);
        datePicker.setMinDate(System.currentTimeMillis() - 1000);
        dateInfoTextDay = (TextView) v.findViewById(R.id.date_info_text_day);
        dateInfoTextWhen = (TextView) v.findViewById(R.id.date_info_text_when);
        dateInfoTextDate = (TextView) v.findViewById(R.id.date_info_text_date);
        setDateListener();
        setDateText();

        // EVENT TIME
        timePicker = (TimePicker) v.findViewById(R.id.frag_bs_timePicker);
        timeInfoText = (TextView) v.findViewById(R.id.time_info_text);
        setupTimePicker();

        // EVENT DURATION
        eventDurationSwitch = (Switch) v.findViewById(R.id.frag_event_length_switch);
        eventDurationNumPicker = (NumberPicker) v.findViewById(R.id.frag_bs_event_length_num_picker);
        eventDurationSpinner = (Spinner) v.findViewById(R.id.frag_bs_event_length_spinner);
        setupDuration();

        // NOTIFICATION
        eventNotificationSpinner = (Spinner) v.findViewById(R.id.frag_bs_event_notification_spinner);
        eventNotificationCustomTxt = (TextView) v.findViewById(R.id.frag_bs_notification_custom_txt);
        eventNotificationTxtHolder = (RelativeLayout) v.findViewById(R.id.frag_bs_notification_custom_txt_holder);
        eventNotificationWarning = (RelativeLayout) v.findViewById(R.id.notification_warning);
        eventNotificationWarning.setVisibility(View.GONE);
        setupNotification();

        // NOTES
        eventNotes = (EditText) v.findViewById(R.id.frag_bs_notes_editTxt);

        // EVENT REPEAT
        eventRepeatSwitch = (Switch) v.findViewById(R.id.frag_bs_event_repeat_switch);
        eventRepeatDaysText = (RelativeLayout) v.findViewById(R.id.frag_bs_event_repeat_choose_days_txt);
        eventRepeatDaysGrid = (LinearLayout) v.findViewById(R.id.frag_bs_event_repeat_days_grid);
        eventRepeatCheckMon = (CheckBox) v.findViewById(R.id.frag_bs_event_repeat_check_mon);
        eventRepeatCheckTues = (CheckBox) v.findViewById(R.id.frag_bs_event_repeat_check_tues);
        eventRepeatCheckWed = (CheckBox) v.findViewById(R.id.frag_bs_event_repeat_check_wed);
        eventRepeatCheckThur = (CheckBox) v.findViewById(R.id.frag_bs_event_repeat_check_thur);
        eventRepeatCheckFri = (CheckBox) v.findViewById(R.id.frag_bs_event_repeat_check_fri);
        eventRepeatCheckSat = (CheckBox) v.findViewById(R.id.frag_bs_event_repeat_check_sat);
        eventRepeatCheckSun = (CheckBox) v.findViewById(R.id.frag_bs_event_repeat_check_sun);
        eventRepeatWeeksTxt = (RelativeLayout) v.findViewById(R.id.frag_bs_event_repeat_choose_week_txt);
        eventRepeatWeeksSpinner = (Spinner) v.findViewById(R.id.frag_bs_event_repeat_weeks_spinner);
        eventRepeatCustomWeeksText = (TextView) v.findViewById(R.id.frag_bs_event_repeat_custom_txt);
        setupRepeat();

        // EVENT LEVELS
        promptLevelCard = (CardView) v.findViewById(R.id.frag_bs_prompt_level_card);
        eventLevelSeekbar = (SeekBar) v.findViewById(R.id.frag_bs_event_level_seekbar);
        eventLevel0 = (RelativeLayout) v.findViewById(R.id.frag_bs_event_level_text_0);
        eventLevel1 = (RelativeLayout) v.findViewById(R.id.frag_bs_event_level_text_1);
        eventLevel2 = (RelativeLayout) v.findViewById(R.id.frag_bs_event_level_text_2);
        eventLevel3 = (RelativeLayout) v.findViewById(R.id.frag_bs_event_level_text_3);
        setupPromptLevels();

        // SAVE BUTTON
        saveButton = (Button) v.findViewById(R.id.frag_bs_save_btn);
        setSaveButtonListener();

        // PRE-POPULATE FIELDS IF SAVED IN REMINDER
        prePopulateFields();

        return v;
    }

    @Override
    public void onPause() {
        hideKeyboard(eventTitleEditTxt);
        super.onPause();
    }

    @Override
    public void onDetach() {
        hideKeyboard(eventTitleEditTxt);
        handler = null;
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(eventTitleEditTxt.getText().toString())) {
            if(context.getCurrentTab() != 1) {
                eventTitleEditTxt.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        eventTitleEditTxt.requestFocus();
                        showKeyboard();
                    }
                }, 100);
            }
        }
    }

    // PREPOPULATE FIELDS FOR EDITING ------------------------------------------------------------//
    private void prePopulateFields() {

        // Title
        if (handler.getReminder().getTitle() != null) {
            eventTitleEditTxt.setText(handler.getReminder().getTitle());
        }

        // Date + Time
        if (handler.getReminder().getDateTime() > -1) {
            long dateTimeLong = handler.getReminder().getDateTime();
            DateTime dt = new DateTime(dateTimeLong);
            datePicker.updateDate(dt.getYear(), dt.getMonthOfYear() - 1, dt.getDayOfMonth());
            timePicker.setHour(dt.getHourOfDay());
            timePicker.setMinute(dt.getMinuteOfHour());
        }

        // Duration
        if (handler.getReminder().isDurationSet()) {
            eventDurationSwitch.setChecked(true);
            showDurationSettings();
            eventDurationNumPicker.setValue(handler.getReminder().getEventDurationTime());
            DurationScale dScale = handler.getReminder().getEventDurationScale();

            switch (dScale) {
                case MINS:
                    eventDurationSpinner.setSelection(0);
                    break;
                case HOURS:
                    eventDurationSpinner.setSelection(1);
                    break;
                case DAYS:
                    eventDurationSpinner.setSelection(2);
                    break;
                case WEEKS:
                    eventDurationSpinner.setSelection(3);
                    break;
            }
        }

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

        // Notes
        if (handler.getReminder().getNotes() != null) {
            eventNotes.setText(handler.getReminder().getNotes());
        }

        // Repeating
        if (handler.getReminder().isRepeating()) {
            eventRepeatSwitch.setChecked(true);
            showRepeatSettings();
            if (handler.getReminder().getRepetitionDays() != null) {
                Day[] repDays = handler.getReminder().getRepetitionDays();
                for (Day d : repDays) {
                    switch (d) {
                        case SUNDAY:
                            eventRepeatCheckSun.setChecked(true);
                            break;
                        case MONDAY:
                            eventRepeatCheckMon.setChecked(true);
                            break;
                        case TUESDAY:
                            eventRepeatCheckTues.setChecked(true);
                            break;
                        case WEDNESDAY:
                            eventRepeatCheckWed.setChecked(true);
                            break;
                        case THURSDAY:
                            eventRepeatCheckThur.setChecked(true);
                            break;
                        case FRIDAY:
                            eventRepeatCheckFri.setChecked(true);
                            break;
                        case SATURDAY:
                            eventRepeatCheckSat.setChecked(true);
                            break;
                    }
                }
            }

            if (handler.getReminder().getRepetitionWeeks() != -1) {
                int weeksToRepeat = handler.getReminder().getRepetitionWeeks();
                if (weeksToRepeat == 0) {
                    eventRepeatWeeksSpinner.setSelection(0);
                } else if (weeksToRepeat == 1) {
                    eventRepeatWeeksSpinner.setSelection(1);
                } else if (weeksToRepeat == 2) {
                    eventRepeatWeeksSpinner.setSelection(2);
                } else if (weeksToRepeat == 3) {
                    eventRepeatWeeksSpinner.setSelection(3);
                } else {
                    eventRepeatWeeksSpinner.setSelection(4);
                    eventRepeatCustomWeeksText.setVisibility(View.VISIBLE);
                    eventRepeatCustomWeeksText.setText("+ " + weeksToRepeat + " weeks");
                    setEventRepeatCustomNoOfWeeks(weeksToRepeat);
                }
            }
        }

        // Prompt Levels
        if (handler.getReminder().getPromptLevel() != -1) {
            eventLevelSeekbar.setProgress(handler.getReminder().getPromptLevel());
        }

    }
    //--------------------------------------------------------------------------------------------//

    // DATE SETTINGS -----------------------------------------------------------------------------//
    private void setDateText() {
        DateTime dt = new DateTime()
                .withDayOfMonth(datePicker.getDayOfMonth())
                .withMonthOfYear(datePicker.getMonth() + 1)
                .withYear(datePicker.getYear());

        DateTime ct = new DateTime();
        Duration diffDay = new Duration(ct.withTimeAtStartOfDay(), dt.withTimeAtStartOfDay());

        dateInfoTextDay.setText(daysOfWeek[dt.getDayOfWeek() - 1]);

        if (diffDay.getStandardHours() < 24) {
            dateInfoTextWhen.setText("Today");
            dateInfoTextWhen.setTypeface(null, Typeface.NORMAL);
        } else if (diffDay.getStandardHours() >= 24 && diffDay.getStandardDays() < 7) {
            dateInfoTextWhen.setText("This Week");
            dateInfoTextWhen.setTypeface(null, Typeface.NORMAL);
        } else if (diffDay.getStandardDays() >= 7 && diffDay.getStandardDays() < 31) {
            dateInfoTextWhen.setText("Over a Week");
            dateInfoTextWhen.setTypeface(null, Typeface.NORMAL);
        } else if (diffDay.getStandardDays() >= 31 && diffDay.getStandardDays() < 365) {
            dateInfoTextWhen.setText("Over a Month");
            dateInfoTextWhen.setTypeface(null, Typeface.NORMAL);
        } else if (diffDay.getStandardDays() >= 365) {
            dateInfoTextWhen.setText("Over a Year");
            dateInfoTextWhen.setTypeface(null, Typeface.BOLD);
        }

        dateInfoTextDate.setText(dt.toString("dd-MM-yyyy"));
    }

    private void setDateListener() {
        DateTime dt = new DateTime();
        android.widget.DatePicker.OnDateChangedListener dateListener = new android.widget.DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                setDateText();
                setTime(timePicker.getHour(), timePicker.getMinute());
                setRepeatDayCheckedAsDayOfWeekChosen(dayOfMonth, monthOfYear + 1, year);
            }
        };
        datePicker.init(dt.getYear(), (dt.getMonthOfYear() - 1), dt.getDayOfMonth(), dateListener);
    }

    private void setRepeatDayCheckedAsDayOfWeekChosen(int day, int month, int year){
        DateTime dt = new DateTime()
                .withDayOfMonth(day)
                .withMonthOfYear(month)
                .withYear(year);

        int dayOfWeek = dt.getDayOfWeek();

        for(int i = 1; i <= 7; i++){
            if(dayOfWeek == i){
                repeatCheckBArray[i - 1].setChecked(true);
            }else{
                repeatCheckBArray[i - 1].setChecked(false);
            }
        }
    }

    private void setRepeatDayCheckedInit(){
        DateTime dt = new DateTime();
        int dayOfWeek = dt.getDayOfWeek();
        for(int i = 1; i <= 7; i++){
            if(dayOfWeek == i){
                repeatCheckBArray[i - 1].setChecked(true);
            }
        }
    }
    //--------------------------------------------------------------------------------------------//

    // TIME SETTINGS -----------------------------------------------------------------------------//
    private void setupTimePicker() {

        setTimeText(timePicker.getHour());

        timePicker.setOnTimeChangedListener(new android.widget.TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(android.widget.TimePicker view, int hourOfDay, int minute) {
                setTime(hourOfDay, minute);
            }
        });
    }

    private void setTime(int hourOfDay, int minute){
        DateTime dateChosen = new DateTime()
                .withDayOfMonth(datePicker.getDayOfMonth())
                .withMonthOfYear(datePicker.getMonth() + 1)
                .withYear(datePicker.getYear());

        DateTime curTime = new DateTime();

        if (dateChosen.isBeforeNow() || dateChosen.isEqualNow()) {
            if (hourOfDay <= curTime.getHourOfDay()) {
                timePicker.setHour(curTime.getHourOfDay());
                if (minute < curTime.getMinuteOfHour()) {
                    timePicker.setMinute(curTime.getMinuteOfHour());
                }
            }
        }

        setTimeText(hourOfDay);
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

    // DURATION SETTINGS -------------------------------------------------------------------------//
    private void setupDuration() {
        eventDurationSwitch.setTextOff("No");
        eventDurationSwitch.setTextOn("Yes");
        setDurationSwitchListeners();

        ArrayAdapter<CharSequence> durationAdapter = ArrayAdapter.createFromResource(context,
                R.array.frag_bs_gen_event_duration_array, android.R.layout.simple_spinner_item);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventDurationSpinner.setAdapter(durationAdapter);

        eventDurationNumPicker.setMinValue(1);
        eventDurationNumPicker.setMaxValue(99);
        eventDurationNumPicker.setWrapSelectorWheel(false);

        // Hide duration settings initially
        hideDurationSettings();
    }

    private void showDurationSettings() {
        eventDurationNumPicker.setVisibility(View.VISIBLE);
        eventDurationSpinner.setVisibility(View.VISIBLE);
    }

    private void hideDurationSettings() {
        eventDurationNumPicker.setVisibility(View.GONE);
        eventDurationSpinner.setVisibility(View.GONE);
    }

    private void setDurationSwitchListeners() {
        eventDurationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Switch is on
                    showDurationSettings();
                } else {
                    // Switch is off
                    hideDurationSettings();
                }
            }
        });
    }
    // -------------------------------------------------------------------------------------------//

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

        DateTime dateTimeChosen = new DateTime()
                .withDayOfMonth(datePicker.getDayOfMonth())
                .withMonthOfYear(datePicker.getMonth() + 1)
                .withYear(datePicker.getYear())
                .withHourOfDay(timePicker.getHour())
                .withMinuteOfHour(timePicker.getMinute());

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

        return notificationTime.isBefore(curTime);
    }

    private void setNotificationWarning(int time, NotificationScale scale) {
        if (notificationIsSetBeforeNow(time, scale)) {
            eventNotificationWarning.setVisibility(View.VISIBLE);
        } else {
            eventNotificationWarning.setVisibility(View.GONE);
        }
    }
    // -------------------------------------------------------------------------------------------//

    // REPEAT SETTINGS ---------------------------------------------------------------------------//
    private void setupRepeat() {

        eventRepeatSwitch.setTextOff("No");
        eventRepeatSwitch.setTextOn("Yes");

        ArrayAdapter<CharSequence> repeatAdapter = ArrayAdapter.createFromResource(context,
                R.array.frag_bs_gen_event_repeat_weeks_array, android.R.layout.simple_spinner_item);
        repeatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventRepeatWeeksSpinner.setAdapter(repeatAdapter);
        setRepeatSpinnerListener();
        hideRepeatSettings();           // Hide the repeat settings initially
        setRepeatSwitchListener();
        eventRepeatCustomWeeksText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomRepeatDialog(eventRepeatCustomNoOfWeeks);
            }
        });

        addRepeatCheckBoxesToArray();
        setRepeatDayCheckedInit();
    }

    private void addRepeatCheckBoxesToArray(){
        repeatCheckBArray[0] = eventRepeatCheckMon;
        repeatCheckBArray[1] = eventRepeatCheckTues;
        repeatCheckBArray[2] = eventRepeatCheckWed;
        repeatCheckBArray[3] = eventRepeatCheckThur;
        repeatCheckBArray[4] = eventRepeatCheckFri;
        repeatCheckBArray[5] = eventRepeatCheckSat;
        repeatCheckBArray[6] = eventRepeatCheckSun;
    }

    private void setRepeatSpinnerListener() {
        eventRepeatWeeksSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        eventRepeatCustomWeeksText.setVisibility(View.GONE);
                        setEventRepeatCustomNoOfWeeks(-1);
                        break;
                    case 1:
                        eventRepeatCustomWeeksText.setVisibility(View.GONE);
                        setEventRepeatCustomNoOfWeeks(-1);
                        break;
                    case 2:
                        eventRepeatCustomWeeksText.setVisibility(View.GONE);
                        setEventRepeatCustomNoOfWeeks(-1);
                        break;
                    case 3:
                        eventRepeatCustomWeeksText.setVisibility(View.GONE);
                        setEventRepeatCustomNoOfWeeks(-1);
                        break;
                    case 4:
                        showCustomRepeatDialog(-1);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                eventRepeatWeeksSpinner.setSelection(0);
            }
        });
    }

    private void hideRepeatSettings() {
        eventRepeatDaysText.setVisibility(View.GONE);
        eventRepeatDaysGrid.setVisibility(View.GONE);
        eventRepeatWeeksTxt.setVisibility(View.GONE);
        eventRepeatWeeksSpinner.setVisibility(View.GONE);
        eventRepeatCustomWeeksText.setVisibility(View.GONE);
    }

    private void showRepeatSettings() {
        eventRepeatDaysText.setVisibility(View.VISIBLE);
        eventRepeatDaysGrid.setVisibility(View.VISIBLE);
        eventRepeatWeeksTxt.setVisibility(View.VISIBLE);
        eventRepeatWeeksSpinner.setVisibility(View.VISIBLE);
        if (eventRepeatWeeksSpinner.getSelectedItemPosition() == 4) {
            eventRepeatCustomWeeksText.setVisibility(View.VISIBLE);
        }
    }

    private void setRepeatSwitchListener() {
        eventRepeatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Switch is on
                    showRepeatSettings();
                } else {
                    hideRepeatSettings();
                }
            }
        });
    }

    private void showCustomRepeatDialog(int existingValue) {

        EventRepeatDialog eventRepeatDialog;

        if (existingValue == -1) {
            eventRepeatDialog = new EventRepeatDialog(context);
        } else {
            eventRepeatDialog = new EventRepeatDialog(context, existingValue);
        }

        eventRepeatDialog.setDialogListener(new EventDialogListener() {
            @Override
            public void passResults(int number, int timeIndex) {
            }

            @Override
            public void passResults(int numberOfWeeks) {
                eventRepeatCustomWeeksText.setText("+ " + numberOfWeeks + " weeks");
                eventRepeatCustomWeeksText.setVisibility(View.VISIBLE);
                setEventRepeatCustomNoOfWeeks(numberOfWeeks);
            }
        });
        eventRepeatDialog.show();
    }

    protected void setEventRepeatCustomNoOfWeeks(int i) {
        eventRepeatCustomNoOfWeeks = i;
    }
    // -------------------------------------------------------------------------------------------//

    // PORMPT LEVEL SETTINGS ---------------------------------------------------------------------//
    private void setupPromptLevels() {
        eventLevelSeekbar.setMax(noOfPromptLevels);
        eventLevelSeekbar.setProgress(2);
        setSeekbarListener();
        setLevelTextButtonsListeners();
        if (settings != null && (!settings.getUserSubtletyEnabled())) {
            hideSubtletyLevelsCard();
        } else {
            showSubtletyLevelsCard();
        }
    }

    private void setSeekbarListener() {
        eventLevelSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progressStep = seekBar.getMax() / noOfPromptLevels;
                int lastDotProgress = Math.round(seekBar.getProgress() / progressStep) * progressStep;
                int nextDotProgress = lastDotProgress + progressStep;
                int midBetweenDots = lastDotProgress + (progressStep / 2);

                if (seekBar.getProgress() > midBetweenDots) {
                    seekBar.setProgress(nextDotProgress);
                } else {
                    seekBar.setProgress(lastDotProgress);
                }
            }
        });
    }

    private void setLevelTextButtonsListeners() {

        eventLevel0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventLevelSeekbar.setProgress(0);
            }
        });

        eventLevel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventLevelSeekbar.setProgress(1);
            }
        });

        eventLevel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventLevelSeekbar.setProgress(2);
            }
        });

        eventLevel3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventLevelSeekbar.setProgress(3);
            }
        });
    }

    private void hideSubtletyLevelsCard() {
        promptLevelCard.setVisibility(View.GONE);
    }

    private void showSubtletyLevelsCard() {
        promptLevelCard.setVisibility(View.VISIBLE);
    }
    // -------------------------------------------------------------------------------------------//

    // 'HIDE KEYBOARD' LISTENERS -----------------------------------------------------------------//
    private void setupTouchListenersForHidingKeyboard() {

        if (eventTitleEditTxt != null) {
            // Listen for change to focus of EditText
            eventTitleEditTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        hideKeyboard(v);
                    }
                }
            });
        }

        if (rootScrollView != null) {
            // Listen for touch of the scroll view
            rootScrollView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard(v);
                    return false;
                }
            });
        }
    }

    private void hideKeyboard(View view) {
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showKeyboard() {
        eventTitleEditTxt.requestFocus();
        if (imm != null) {
            imm.showSoftInput(eventTitleEditTxt, InputMethodManager.SHOW_IMPLICIT);
        }
    }
    // -------------------------------------------------------------------------------------------//

    // LISTENER/HANDLER SETTING ------------------------------------------------------------------//
    public void setHandler(OnBSFragmentListener handler) {
        this.handler = handler;
    }
    //--------------------------------------------------------------------------------------------//

    // SAVE BUTTON SETTINGS ----------------------------------------------------------------------//
    private void setSaveButtonListener() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReminder();
                handler.resetAfterSave();
            }
        });
    }

    private void saveReminder() {

        // Title
        String mTitle = eventTitleEditTxt.getText().toString();
        if (!TextUtils.isEmpty(mTitle)) {
            handler.getReminder().setTitle(mTitle);
        }

        // Date & Time
        DateTime mDateTime = new DateTime()
                .withDayOfMonth(datePicker.getDayOfMonth())
                .withMonthOfYear(datePicker.getMonth() + 1)
                .withYear(datePicker.getYear())
                .withHourOfDay(timePicker.getHour())
                .withMinuteOfHour(timePicker.getMinute())
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        handler.getReminder().setDateTime(mDateTime.getMillis());

        // Duration
        boolean mDurChecked = eventDurationSwitch.isChecked();
        handler.getReminder().setDurationSet(mDurChecked);
        if (mDurChecked) {
            handler.getReminder().setEventDurationTime(eventDurationNumPicker.getValue());
            switch (eventDurationSpinner.getSelectedItemPosition()) {
                case 0:
                    handler.getReminder().setEventDurationScale(DurationScale.MINS);
                    break;
                case 1:
                    handler.getReminder().setEventDurationScale(DurationScale.HOURS);
                    break;
                case 2:
                    handler.getReminder().setEventDurationScale(DurationScale.DAYS);
                    break;
                case 3:
                    handler.getReminder().setEventDurationScale(DurationScale.WEEKS);
                    break;
            }
        }

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

        // Notes
        if (!TextUtils.isEmpty(eventNotes.getText().toString())) {
            handler.getReminder().setNotes(eventNotes.getText().toString());
        }

        // Repeat
        boolean mRepCheck = eventRepeatSwitch.isChecked();
        handler.getReminder().setRepeating(mRepCheck);
        if (mRepCheck) {
            // Get days
            ArrayList<Day> rDays = new ArrayList<>();

            if (eventRepeatCheckMon.isChecked()) {
                rDays.add(Day.MONDAY);
            }

            if (eventRepeatCheckTues.isChecked()) {
                rDays.add(Day.TUESDAY);
            }

            if (eventRepeatCheckWed.isChecked()) {
                rDays.add(Day.WEDNESDAY);
            }

            if (eventRepeatCheckThur.isChecked()) {
                rDays.add(Day.THURSDAY);
            }

            if (eventRepeatCheckFri.isChecked()) {
                rDays.add(Day.FRIDAY);
            }

            if (eventRepeatCheckSat.isChecked()) {
                rDays.add(Day.SATURDAY);
            }

            if (eventRepeatCheckSun.isChecked()) {
                rDays.add(Day.SUNDAY);
            }

            if (rDays.size() > 0) {
                Day[] rDaysArr = new Day[rDays.size()];
                handler.getReminder().setRepetitionDays(rDays.toArray(rDaysArr));
            }

            switch (eventRepeatWeeksSpinner.getSelectedItemPosition()) {
                case 0:
                    handler.getReminder().setRepetitionWeeks(0);
                    break;
                case 1:
                    handler.getReminder().setRepetitionWeeks(1);
                    break;
                case 2:
                    handler.getReminder().setRepetitionWeeks(2);
                    break;
                case 3:
                    handler.getReminder().setRepetitionWeeks(3);
                    break;
                case 4:
                    if (eventRepeatCustomNoOfWeeks != -1) {
                        handler.getReminder().setRepetitionWeeks(eventRepeatCustomNoOfWeeks);
                    } else {
                        handler.getReminder().setRepetitionWeeks(0);
                    }
                    break;
            }
        }

        // prompt level
        if (settings.getUserSubtletyEnabled()) {
            handler.getReminder().setPromptLevel(eventLevelSeekbar.getProgress());
        }

        // Reminder Type
        handler.getReminder().setType(EventType.GEN);

    }
    // -------------------------------------------------------------------------------------------//


}
