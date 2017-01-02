package highway62.reminderapp.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import highway62.reminderapp.R;
import highway62.reminderapp.ReminderActivity;
import highway62.reminderapp.adminSettings.SettingsInterface;
import highway62.reminderapp.constants.DurationScale;
import highway62.reminderapp.constants.EventType;
import highway62.reminderapp.fragmentHandlers.OnNDFragmentListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NDDurationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NDDurationFragment extends Fragment {

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
    private Switch eventDurationSwitch;
    private NumberPicker eventDurationNumPicker;
    private Spinner eventDurationSpinner;


    public NDDurationFragment() {}

    public static NDDurationFragment newInstance(EventType typeOfEvent) {
        NDDurationFragment fragment = new NDDurationFragment();
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
        View v = inflater.inflate(R.layout.fragment_nd_duration, container, false);

        titleText = (TextView) v.findViewById(R.id.frag_nd_title_text);
        subtitleText = (TextView) v.findViewById(R.id.frag_nd_subtitle_text);
        navNext = (TextView) v.findViewById(R.id.frag_nd_nextBtn);
        navBack = (TextView) v.findViewById(R.id.frag_nd_backBtn);

        eventDurationSwitch = (Switch) v.findViewById(R.id.frag_event_length_switch);
        eventDurationNumPicker = (NumberPicker) v.findViewById(R.id.frag_nd_duration_num_picker);
        eventDurationSpinner = (Spinner) v.findViewById(R.id.frag_nd_duration_spinner);
        setupDuration();

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
    }

    public void setHandler(OnNDFragmentListener handler) {
        this.handler = handler;
    }

    private void setupDuration(){
        eventDurationSwitch.setTextOff("No");
        eventDurationSwitch.setTextOn("Yes");
        setDurationSwitchListeners();

        eventDurationNumPicker.setMinValue(1);
        eventDurationNumPicker.setMaxValue(99);
        eventDurationNumPicker.setWrapSelectorWheel(false);

        ArrayAdapter<CharSequence> durationAdapter = ArrayAdapter.createFromResource(context,
                R.array.frag_bs_gen_event_duration_array, android.R.layout.simple_spinner_item);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventDurationSpinner.setAdapter(durationAdapter);

        // Hide duration settings initially
        hideDurationSettings();
    }

    private void setDurationSwitchListeners(){
        eventDurationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    // Switch is on
                    showDurationSettings();
                }else{
                    // Switch is off
                    hideDurationSettings();
                }
            }
        });
    }

    private void showDurationSettings(){
        eventDurationNumPicker.setVisibility(View.VISIBLE);
        eventDurationSpinner.setVisibility(View.VISIBLE);
    }

    private void hideDurationSettings(){
        eventDurationNumPicker.setVisibility(View.GONE);
        eventDurationSpinner.setVisibility(View.GONE);
    }

    // CATEGORY SPECIFIC CONTENT -----------------------------------------------------------------//
    private void setupTextViewContent() {
        // Title for time (may not change)
        titleText.setText(context.getString(R.string.frag_nd_duration_gen));
        switch (typeOfEvent) {
            case GEN:
                subtitleText.setText(context.getString(R.string.frag_nd_duration_gen_subtitle));
                break;
            case APPT:
                subtitleText.setText(context.getString(R.string.frag_nd_duration_appt_subtitle));
                break;
            case SHOPPING:
                break;
            case BIRTH:
                break;
            case MEDIC:
                break;
            case DAILY:
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

                saveChoice();

                switch (typeOfEvent){
                    case GEN:
                        // Navigate to time frag
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_time_tag));
                        break;
                    case APPT:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_time_tag));
                        break;
                    case SHOPPING:
                        break;
                    case BIRTH:
                        break;
                    case MEDIC:
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

                saveChoice();

                switch (typeOfEvent){
                    case GEN:
                        // Navigate to notification frag
                        handler.navigateFragmentForward(typeOfEvent,context.getString(R.string.nd_notification_tag));
                        break;
                    case APPT:
                        handler.navigateFragmentForward(typeOfEvent,context.getString(R.string.nd_notification_tag));
                        break;
                    case SHOPPING:
                        break;
                    case BIRTH:
                        break;
                    case MEDIC:
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

    private void saveChoice(){
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
    }

}
