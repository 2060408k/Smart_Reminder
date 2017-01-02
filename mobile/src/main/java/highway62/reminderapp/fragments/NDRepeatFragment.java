package highway62.reminderapp.fragments;


import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.ArrayList;

import highway62.reminderapp.R;
import highway62.reminderapp.ReminderActivity;
import highway62.reminderapp.adminSettings.SettingsInterface;
import highway62.reminderapp.constants.Day;
import highway62.reminderapp.constants.EventType;
import highway62.reminderapp.dialogs.EventDialogListener;
import highway62.reminderapp.dialogs.EventRepeatDialog;
import highway62.reminderapp.fragmentHandlers.OnNDFragmentListener;


public class NDRepeatFragment extends Fragment {

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
    private RelativeLayout eventRepeatWeeksTxt;
    private Spinner eventRepeatWeeksSpinner;
    private TextView eventRepeatCustomWeeksText;
    private int eventRepeatCustomNoOfWeeks = -1;
    private CheckBox[] repeatCheckBArray = new CheckBox[7];

    public NDRepeatFragment() {}


    public static NDRepeatFragment newInstance(EventType typeOfEvent) {
        NDRepeatFragment fragment = new NDRepeatFragment();
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
        View v;
        if(typeOfEvent.equals(EventType.SHOPPING)){
            v = inflater.inflate(R.layout.fragment_nd_repeat_shopping, container, false);
        }else{
            v = inflater.inflate(R.layout.fragment_nd_repeat, container, false);
            eventRepeatSwitch = (Switch) v.findViewById(R.id.frag_bs_event_repeat_switch);
            eventRepeatDaysText = (RelativeLayout) v.findViewById(R.id.frag_bs_event_repeat_choose_days_txt);
            eventRepeatWeeksTxt = (RelativeLayout) v.findViewById(R.id.frag_bs_event_repeat_choose_week_txt);
            eventRepeatWeeksSpinner = (Spinner) v.findViewById(R.id.frag_bs_event_repeat_weeks_spinner);
            eventRepeatCustomWeeksText = (TextView) v.findViewById(R.id.frag_bs_event_repeat_custom_txt);
        }

        titleText = (TextView) v.findViewById(R.id.frag_nd_title_text);
        subtitleText = (TextView) v.findViewById(R.id.frag_nd_subtitle_text);
        navNext = (TextView) v.findViewById(R.id.frag_nd_nextBtn);
        navBack = (TextView) v.findViewById(R.id.frag_nd_backBtn);

        eventRepeatDaysGrid = (LinearLayout) v.findViewById(R.id.frag_bs_event_repeat_days_grid);
        eventRepeatCheckMon = (CheckBox) v.findViewById(R.id.frag_bs_event_repeat_check_mon);
        eventRepeatCheckTues = (CheckBox) v.findViewById(R.id.frag_bs_event_repeat_check_tues);
        eventRepeatCheckWed = (CheckBox) v.findViewById(R.id.frag_bs_event_repeat_check_wed);
        eventRepeatCheckThur = (CheckBox) v.findViewById(R.id.frag_bs_event_repeat_check_thur);
        eventRepeatCheckFri = (CheckBox) v.findViewById(R.id.frag_bs_event_repeat_check_fri);
        eventRepeatCheckSat = (CheckBox) v.findViewById(R.id.frag_bs_event_repeat_check_sat);
        eventRepeatCheckSun = (CheckBox) v.findViewById(R.id.frag_bs_event_repeat_check_sun);

        setupTextViewContent();
        setupNavigationButtons();
        if(!typeOfEvent.equals(EventType.SHOPPING)){
            // Setup hiding the views if switch active
            setupRepeat();
        }else{
            // No setup needed just add checkboxes to array
            addRepeatCheckBoxesToArray();
        }

        if(handler.getReminder().getDateTime() > -1){
            setRepeatDayCheckedAsDayOfWeekChosen(new DateTime(handler.getReminder().getDateTime()));
        }else{
            setRepeatDayCheckedInit();
        }

        prePopulateFields();

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        handler = null;
    }

    private void prePopulateFields(){
        if(typeOfEvent.equals(EventType.SHOPPING)){
            prePopulateShopping();
        }else{
            prePopulateNormal();
        }
    }

    private void prePopulateShopping(){
        // Repeating
        if (handler.getReminder().isRepeating()) {
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
        }
    }

    private void prePopulateNormal(){
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
    }

    public void setHandler(OnNDFragmentListener handler) {
        this.handler = handler;
    }

    // REPEAT SETTINGS ---------------------------------------------------------------------------//

    private void setupRepeat(){

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
    }

    private void addRepeatCheckBoxesToArray() {
        repeatCheckBArray[0] = eventRepeatCheckMon;
        repeatCheckBArray[1] = eventRepeatCheckTues;
        repeatCheckBArray[2] = eventRepeatCheckWed;
        repeatCheckBArray[3] = eventRepeatCheckThur;
        repeatCheckBArray[4] = eventRepeatCheckFri;
        repeatCheckBArray[5] = eventRepeatCheckSat;
        repeatCheckBArray[6] = eventRepeatCheckSun;
    }

    private void setRepeatSpinnerListener(){
        eventRepeatWeeksSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        eventRepeatCustomWeeksText.setVisibility(View.GONE);
                        break;
                    case 1:
                        eventRepeatCustomWeeksText.setVisibility(View.GONE);
                        break;
                    case 2:
                        eventRepeatCustomWeeksText.setVisibility(View.GONE);
                        break;
                    case 3:
                        eventRepeatCustomWeeksText.setVisibility(View.GONE);
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

    private void hideRepeatSettings(){
        eventRepeatDaysText.setVisibility(View.GONE);
        eventRepeatDaysGrid.setVisibility(View.GONE);
        eventRepeatWeeksTxt.setVisibility(View.GONE);
        eventRepeatWeeksSpinner.setVisibility(View.GONE);
        eventRepeatCustomWeeksText.setVisibility(View.GONE);
    }

    private void showRepeatSettings(){
        eventRepeatDaysText.setVisibility(View.VISIBLE);
        eventRepeatDaysGrid.setVisibility(View.VISIBLE);
        eventRepeatWeeksTxt.setVisibility(View.VISIBLE);
        eventRepeatWeeksSpinner.setVisibility(View.VISIBLE);
        if(eventRepeatWeeksSpinner.getSelectedItemPosition() == 4){
            eventRepeatCustomWeeksText.setVisibility(View.VISIBLE);
        }
    }

    private void setRepeatSwitchListener(){
        eventRepeatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    // Switch is on
                    showRepeatSettings();
                }else{
                    hideRepeatSettings();
                }
            }
        });
    }

    private void showCustomRepeatDialog(int existingValue){

        EventRepeatDialog eventRepeatDialog;

        if(existingValue == -1){
            eventRepeatDialog = new EventRepeatDialog(context);
        }else{
            eventRepeatDialog = new EventRepeatDialog(context, existingValue);
        }

        eventRepeatDialog.setDialogListener(new EventDialogListener() {
            @Override
            public void passResults(int number, int timeIndex) {}

            @Override
            public void passResults(int numberOfWeeks) {
                eventRepeatCustomWeeksText.setText("+ " + numberOfWeeks + " weeks");
                eventRepeatCustomWeeksText.setVisibility(View.VISIBLE);
                setEventRepeatCustomNoOfWeeks(numberOfWeeks);
            }
        });
        eventRepeatDialog.show();
    }

    protected void setEventRepeatCustomNoOfWeeks(int i){
        eventRepeatCustomNoOfWeeks = i;
    }

    private void setRepeatDayCheckedAsDayOfWeekChosen(DateTime dt) {
        int dayOfWeek = dt.getDayOfWeek();

        for (int i = 1; i <= 7; i++) {
            if (dayOfWeek == i) {
                repeatCheckBArray[i - 1].setChecked(true);
            } else {
                repeatCheckBArray[i - 1].setChecked(false);
            }
        }
    }

    private void setRepeatDayCheckedInit() {
        DateTime dt = new DateTime();
        int dayOfWeek = dt.getDayOfWeek();
        for (int i = 1; i <= 7; i++) {
            if (dayOfWeek == i) {
                repeatCheckBArray[i - 1].setChecked(true);
            }
        }
    }

    // -------------------------------------------------------------------------------------------//

    // CATEGORY SPECIFIC CONTENT -----------------------------------------------------------------//
    private void setupTextViewContent() {
        titleText.setText(context.getString(R.string.frag_nd_repeat_gen));

        switch (typeOfEvent) {
            case GEN:
                subtitleText.setText(context.getString(R.string.frag_nd_repeat_gen_subtitle));
                if(!settings.getUserSubtletyEnabled()){
                    Drawable img = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_nav_save, null);
                    navNext.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
                    navNext.setText("Save");                }
                break;
            case APPT:
                subtitleText.setText(context.getString(R.string.frag_nd_repeat_appt_subtitle));
                if(!settings.getUserSubtletyEnabled()){
                    Drawable img = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_nav_save, null);
                    navNext.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
                    navNext.setText("Save");                }
                break;
            case SHOPPING:
                if(!settings.getUserSubtletyEnabled()){
                    Drawable img = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_nav_save, null);
                    navNext.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
                    navNext.setText("Save");                }
                break;
            case BIRTH:
                break;
            case MEDIC:
                subtitleText.setText(context.getString(R.string.frag_nd_repeat_gen_subtitle));
                if(!settings.getUserSubtletyEnabled()){
                    Drawable img = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_nav_save, null);
                    navNext.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
                    navNext.setText("Save");                }
                break;
            case DAILY:
                subtitleText.setText(context.getString(R.string.frag_nd_notes_gen_subtitle));
                break;
            case SOCIAL:
                break;
        }
    }
    // -------------------------------------------------------------------------------------------//

    // NAVIGATION LISTENERS ----------------------------------------------------------------------//
    private void setupNavigationButtons() {

        navBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(typeOfEvent.equals(EventType.SHOPPING)){
                    saveChoicesShopping();
                }else{
                    saveChoices();
                }

                switch (typeOfEvent){
                    case GEN:
                        // Navigate to notes frag
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_notes_tag));
                        break;
                    case APPT:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_notes_tag));
                        break;
                    case SHOPPING:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_notes_tag));
                        break;
                    case BIRTH:
                        break;
                    case MEDIC:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_notification_tag));
                        break;
                    case DAILY:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_time_tag));
                        break;
                    case SOCIAL:
                        break;
                }
            }
        });


        navNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(typeOfEvent.equals(EventType.SHOPPING)){
                    saveChoicesShopping();
                }else{
                    saveChoices();
                }

                switch (typeOfEvent){
                    case GEN:
                        // Navigate to prompt level frag
                        if(settings.getUserSubtletyEnabled()) {
                            handler.navigateFragmentForward(typeOfEvent, context.getString(R.string.nd_reminder_level_tag));
                        }else{
                            // SAVE + CLEAR REMINDER, NAVIGATE TO HOME AND SWITCH TO SCHEDULE
                            handler.resetAfterSave(typeOfEvent);
                        }
                        break;
                    case APPT:
                        if(settings.getUserSubtletyEnabled()) {
                            handler.navigateFragmentForward(typeOfEvent, context.getString(R.string.nd_reminder_level_tag));
                        }else{
                            handler.resetAfterSave(typeOfEvent);
                        }
                        break;
                    case SHOPPING:
                        if(settings.getUserSubtletyEnabled()) {
                            handler.navigateFragmentForward(typeOfEvent, context.getString(R.string.nd_reminder_level_tag));
                        }else{
                            handler.resetAfterSave(typeOfEvent);
                        }
                        break;
                    case BIRTH:
                        break;
                    case MEDIC:
                        if(settings.getUserSubtletyEnabled()) {
                            handler.navigateFragmentForward(typeOfEvent, context.getString(R.string.nd_reminder_level_tag));
                        }else{
                            handler.resetAfterSave(typeOfEvent);
                        }
                        break;
                    case DAILY:
                        handler.navigateFragmentForward(typeOfEvent, context.getString(R.string.nd_notes_tag));
                        break;
                    case SOCIAL:
                        break;
                }
            }
        });

    }
    // -------------------------------------------------------------------------------------------//

    private void saveChoices(){
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
            }else{
                handler.getReminder().setRepeating(false);
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
    }

    private void saveChoicesShopping(){
        // Repeat
        ArrayList<Day> rDays = new ArrayList<>();
        if (eventRepeatCheckMon.isChecked()) { rDays.add(Day.MONDAY); }
        if (eventRepeatCheckTues.isChecked()) { rDays.add(Day.TUESDAY); }
        if (eventRepeatCheckWed.isChecked()) { rDays.add(Day.WEDNESDAY); }
        if (eventRepeatCheckThur.isChecked()) { rDays.add(Day.THURSDAY); }
        if (eventRepeatCheckFri.isChecked()) { rDays.add(Day.FRIDAY); }
        if (eventRepeatCheckSat.isChecked()) { rDays.add(Day.SATURDAY); }
        if (eventRepeatCheckSun.isChecked()) { rDays.add(Day.SUNDAY); }
        if (rDays.size() > 0) {
            handler.getReminder().setRepeating(true);
            Day[] rDaysArr = new Day[rDays.size()];
            handler.getReminder().setRepetitionDays(rDays.toArray(rDaysArr));
        }else{
            handler.getReminder().setRepeating(false);
        }
    }

}
