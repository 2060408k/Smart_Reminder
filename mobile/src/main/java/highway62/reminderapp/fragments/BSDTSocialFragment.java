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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import highway62.reminderapp.R;
import highway62.reminderapp.ReminderActivity;
import highway62.reminderapp.adminSettings.SettingsInterface;
import highway62.reminderapp.constants.EventType;
import highway62.reminderapp.constants.ReminderType;
import highway62.reminderapp.fragmentHandlers.OnBSFragmentListener;
import highway62.reminderapp.pickers.DatePicker;
import highway62.reminderapp.pickers.TimePicker;


public class BSDTSocialFragment extends Fragment {

    private InputMethodManager imm;
    private ReminderActivity context;
    private ActionBar actionBar;
    private SettingsInterface settings;
    private OnBSFragmentListener handler;
    private ScrollView rootScrollView;

    private EditText eventTitleEditTxt;

    private EditText eventLocationEditTxt;

    private DatePicker datePicker;
    private TextView dateInfoTextDay;
    private TextView dateInfoTextWhen;
    private TextView dateInfoTextDate;
    private final String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday"
            , "Thursday", "Friday", "Saturday", "Sunday"};
    private TimePicker timePicker;
    private TextView timeInfoText;

    private CardView promptLevelCard;
    private SeekBar eventLevelSeekbar;
    private final int noOfPromptLevels = 3;
    private RelativeLayout eventLevel0;
    private RelativeLayout eventLevel1;
    private RelativeLayout eventLevel2;
    private RelativeLayout eventLevel3;

    private Button saveButton;


    public BSDTSocialFragment() {}


    public static BSDTSocialFragment newInstance() {
        return new BSDTSocialFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = (ReminderActivity) getActivity();
        settings = context.getSettingsSingleton();
        actionBar = context.getSupportActionBar();
        imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bsdt_social, container, false);

        rootScrollView = (ScrollView) v.findViewById(R.id.bsFragRootScrollView);

        // EVENT TITLE
        eventTitleEditTxt = (EditText) v.findViewById(R.id.frag_bs_title_editTxt);
        setupTouchListenersForHidingKeyboard();

        // EVENT LOCATION
        eventLocationEditTxt = (EditText) v.findViewById(R.id.frag_bs_location_editTxt);

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

        // Location
        if (handler.getReminder().getLocation() != null) {
            eventLocationEditTxt.setText(handler.getReminder().getLocation());
        }

        // Date + Time
        if (handler.getReminder().getDateTime() > -1) {
            long dateTimeLong = handler.getReminder().getDateTime();
            DateTime dt = new DateTime(dateTimeLong);
            datePicker.updateDate(dt.getYear(), dt.getMonthOfYear() - 1, dt.getDayOfMonth());
            timePicker.setCurrentHour(dt.getHourOfDay());
            timePicker.setCurrentMinute(dt.getMinuteOfHour());
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
                setTime(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
            }
        };
        datePicker.init(dt.getYear(), (dt.getMonthOfYear() - 1), dt.getDayOfMonth(), dateListener);
    }
    //--------------------------------------------------------------------------------------------//

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
        DateTime dateChosen = new DateTime()
                .withDayOfMonth(datePicker.getDayOfMonth())
                .withMonthOfYear(datePicker.getMonth() + 1)
                .withYear(datePicker.getYear());

        DateTime curTime = new DateTime();

        if (dateChosen.isBeforeNow() || dateChosen.isEqualNow()) {
            if (hourOfDay <= curTime.getHourOfDay()) {
                timePicker.setCurrentHour(curTime.getHourOfDay());
                if (minute < curTime.getMinuteOfHour()) {
                    timePicker.setCurrentMinute(curTime.getMinuteOfHour());
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

    // PORMPT LEVEL SETTINGS ---------------------------------------------------------------------//
    private void setupPromptLevels() {
        eventLevelSeekbar.setMax(noOfPromptLevels);
        eventLevelSeekbar.setProgress(2);
        setSeekbarListener();
        setLevelTextButtonsListeners();
        if (!settings.getUserSubtletyEnabled()) {
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
            }
        });
    }

    private void saveReminder() {

        // Title
        String mTitle = eventTitleEditTxt.getText().toString();
        if (!TextUtils.isEmpty(mTitle)) {
            handler.getReminder().setTitle(mTitle);
        }

        //Location
        String mLoc = eventLocationEditTxt.getText().toString();
        if (!TextUtils.isEmpty(mLoc)) {
            handler.getReminder().setLocation(mLoc);
        }

        // Date & Time
        DateTime mDateTime = new DateTime()
                .withDayOfMonth(datePicker.getDayOfMonth())
                .withMonthOfYear(datePicker.getMonth() + 1)
                .withYear(datePicker.getYear())
                .withHourOfDay(timePicker.getCurrentHour())
                .withMinuteOfHour(timePicker.getCurrentMinute())
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        handler.getReminder().setDateTime(mDateTime.getMillis());

        // prompt level
        if (settings.getUserSubtletyEnabled()) {
            handler.getReminder().setPromptLevel(eventLevelSeekbar.getProgress());
        }

        // Event Type
        handler.getReminder().setType(EventType.SOCIAL);

        // Reminder Type
        handler.getReminder().setReminderType(ReminderType.REMINDER);

        handler.resetAfterSave();
    }
    // -------------------------------------------------------------------------------------------//

}
