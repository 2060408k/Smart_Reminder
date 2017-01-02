package highway62.reminderapp.fragments;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import highway62.reminderapp.R;
import highway62.reminderapp.ReminderActivity;
import highway62.reminderapp.adminSettings.SettingsInterface;
import highway62.reminderapp.constants.EventType;
import highway62.reminderapp.fragmentHandlers.OnNDFragmentListener;
import highway62.reminderapp.pickers.DatePicker;


public class NDDateFragment extends Fragment {

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
    private DatePicker datePicker;

    private TextView dateInfoTextDay;
    private TextView dateInfoTextWhen;
    private TextView dateInfoTextDate;
    private final String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday"
            , "Thursday", "Friday", "Saturday", "Sunday"};


    public NDDateFragment() {
    }

    public static NDDateFragment newInstance(EventType typeOfEvent) {
        NDDateFragment fragment = new NDDateFragment();
        Bundle args = new Bundle();
        args.putSerializable(EVENT_TYPE, typeOfEvent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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
        View v = inflater.inflate(R.layout.fragment_nd_date, container, false);

        titleText = (TextView) v.findViewById(R.id.frag_nd_title_text);
        subtitleText = (TextView) v.findViewById(R.id.frag_nd_subtitle_text);
        navNext = (TextView) v.findViewById(R.id.frag_nd_nextBtn);
        navBack = (TextView) v.findViewById(R.id.frag_nd_backBtn);
        datePicker = (DatePicker) v.findViewById(R.id.frag_nd_datePicker);
        datePicker.setMinDate(System.currentTimeMillis() - 1000);
        dateInfoTextDay = (TextView) v.findViewById(R.id.date_info_text_day);
        dateInfoTextWhen = (TextView) v.findViewById(R.id.date_info_text_when);
        dateInfoTextDate = (TextView) v.findViewById(R.id.date_info_text_date);

        setDateListener();
        setDateText();
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

    private void prePopulateFields() {
        // Date
        if (handler.getReminder().getDateTime() > -1) {
            long dateTimeLong = handler.getReminder().getDateTime();
            DateTime dt = new DateTime(dateTimeLong);
            datePicker.updateDate(dt.getYear(), dt.getMonthOfYear() - 1, dt.getDayOfMonth());
        }
    }

    public void setHandler(OnNDFragmentListener handler) {
        this.handler = handler;
    }

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
            }
        };

        datePicker.init(dt.getYear(), (dt.getMonthOfYear() - 1), dt.getDayOfMonth(), dateListener);
    }

    //--------------------------------------------------------------------------------------------//

    // CATEGORY SPECIFIC CONTENT -----------------------------------------------------------------//
    private void setupTextViewContent() {
        // Title for date may not change, keep as generic
        titleText.setText(context.getString(R.string.frag_nd_date_gen));
        switch (typeOfEvent) {
            case GEN:
                subtitleText.setText(context.getString(R.string.frag_nd_date_gen_subtitle));
                break;
            case APPT:
                subtitleText.setText(context.getString(R.string.frag_nd_date_appt_subtitle));
                break;
            case SHOPPING:
                subtitleText.setText(context.getString(R.string.frag_nd_date_shopping_subtitle));
                break;
            case BIRTH:
                subtitleText.setText(context.getString(R.string.frag_nd_date_birth_subtitle));
                break;
            case MEDIC:
                subtitleText.setText(context.getString(R.string.frag_nd_date_medic_subtitle));
                break;
            case DAILY:
                subtitleText.setText(context.getString(R.string.frag_nd_date_daily_subtitle));
                break;
            case SOCIAL:
                subtitleText.setText(context.getString(R.string.frag_nd_date_social_subtitle));
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
                        // Navigate to title frag
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_title_tag));
                        break;
                    case APPT:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_title_tag));
                        break;
                    case SHOPPING:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_title_tag));
                        break;
                    case BIRTH:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_title_tag));
                        break;
                    case MEDIC:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_event_after_tag));
                        break;
                    case DAILY:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_title_tag));
                        break;
                    case SOCIAL:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_where_tag));
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
                        // Navigate to time frag
                        handler.navigateFragmentForward(typeOfEvent,context.getString(R.string.nd_time_tag));
                        break;
                    case APPT:
                        handler.navigateFragmentForward(typeOfEvent,context.getString(R.string.nd_time_tag));
                        break;
                    case SHOPPING:
                        handler.navigateFragmentForward(typeOfEvent,context.getString(R.string.nd_time_tag));
                        break;
                    case BIRTH:
                        handler.navigateFragmentForward(typeOfEvent,context.getString(R.string.nd_time_tag));
                        break;
                    case MEDIC:
                        handler.navigateFragmentForward(typeOfEvent,context.getString(R.string.nd_time_tag));
                        break;
                    case DAILY:
                        handler.navigateFragmentForward(typeOfEvent,context.getString(R.string.nd_time_tag));
                        break;
                    case SOCIAL:
                        handler.navigateFragmentForward(typeOfEvent,context.getString(R.string.nd_time_tag));
                        break;
                }
            }
        });
    }
    // -------------------------------------------------------------------------------------------//

    private void saveChoice(){
        // Date & Time
        DateTime mDateTime = new DateTime()
                .withDayOfMonth(datePicker.getDayOfMonth())
                .withMonthOfYear(datePicker.getMonth() + 1)
                .withYear(datePicker.getYear());
        handler.setTempDate(mDateTime);
    }
}
