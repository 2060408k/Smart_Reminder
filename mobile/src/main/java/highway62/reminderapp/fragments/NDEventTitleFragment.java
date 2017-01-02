package highway62.reminderapp.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import highway62.reminderapp.R;
import highway62.reminderapp.ReminderActivity;
import highway62.reminderapp.adminSettings.SettingsInterface;
import highway62.reminderapp.constants.EventType;
import highway62.reminderapp.fragmentHandlers.OnNDFragmentListener;


public class NDEventTitleFragment extends Fragment {

    private static final String EVENT_TYPE = "event_type";
    private EventType typeOfEvent;
    private OnNDFragmentListener handler;
    private InputMethodManager imm;
    private ReminderActivity context;
    private ActionBar actionBar;
    private SettingsInterface settings;
    private LinearLayout rootView;

    private TextView titleText;
    private TextView subtitleText;
    private EditText eventTitleEditTxt;
    private TextView navNext;
    private TextView navBack;

    public NDEventTitleFragment() {
    }

    public static NDEventTitleFragment newInstance(EventType typeOfEvent) {
        NDEventTitleFragment fragment = new NDEventTitleFragment();
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
        imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);

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
        v = inflater.inflate(R.layout.fragment_nd_event_title, container, false);
        rootView = (LinearLayout) v.findViewById(R.id.frag_nd_rootView);

        titleText = (TextView) v.findViewById(R.id.frag_nd_title_text);
        subtitleText = (TextView) v.findViewById(R.id.frag_nd_subtitle_text);
        eventTitleEditTxt = (EditText) v.findViewById(R.id.frag_nd_title_editTxt);
        setupTouchListenersForHidingKeyboard();

        navNext = (TextView) v.findViewById(R.id.frag_nd_nextBtn);
        navBack = (TextView) v.findViewById(R.id.frag_nd_backBtn);
        setupTextViewContent();
        setupNavigationButtons();

        prePopulateFields();

        return v;
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

    private void prePopulateFields() {
        // Title
        if (handler.getReminder().getTitle() != null) {
            eventTitleEditTxt.setText(handler.getReminder().getTitle());
        }
    }

    public void setHandler(OnNDFragmentListener handler) {
        this.handler = handler;
    }

    // CATEGORY SPECIFIC CONTENT -----------------------------------------------------------------//
    private void setupTextViewContent() {
        switch (typeOfEvent) {
            case GEN:
                titleText.setText(context.getString(R.string.frag_nd_event_title_gen));
                subtitleText.setText(context.getString(R.string.frag_nd_event_subtitle_gen));
                eventTitleEditTxt.setHint(context.getString(R.string.frag_nd_event_title_gen_hint));
                break;
            case APPT:
                titleText.setText(context.getString(R.string.frag_nd_event_title_appt));
                subtitleText.setText(context.getString(R.string.frag_nd_event_subtitle_appt));
                eventTitleEditTxt.setHint(context.getString(R.string.frag_nd_event_title_appt_hint));
                break;
            case SHOPPING:
                titleText.setText(context.getString(R.string.frag_nd_event_title_shopping));
                subtitleText.setText(context.getString(R.string.frag_nd_event_subtitle_shopping));
                eventTitleEditTxt.setHint(context.getString(R.string.frag_nd_event_title_shopping_hint));
                break;
            case BIRTH:
                titleText.setText(context.getString(R.string.frag_nd_event_title_birthday));
                subtitleText.setText(context.getString(R.string.frag_nd_event_subtitle_birthday));
                eventTitleEditTxt.setHint(context.getString(R.string.frag_nd_event_title_birthday_hint));
                break;
            case MEDIC:
                titleText.setText(context.getString(R.string.frag_nd_event_title_medic));
                subtitleText.setText(context.getString(R.string.frag_nd_event_subtitle_medic));
                eventTitleEditTxt.setHint(context.getString(R.string.frag_nd_event_title_medic_hint));
                break;
            case DAILY:
                titleText.setText(context.getString(R.string.frag_nd_event_title_daily));
                subtitleText.setText(context.getString(R.string.frag_nd_event_subtitle_daily));
                eventTitleEditTxt.setHint(context.getString(R.string.frag_nd_event_title_daily_hint));
                break;
            case SOCIAL:
                titleText.setText(context.getString(R.string.frag_nd_event_title_social));
                subtitleText.setText(context.getString(R.string.frag_nd_event_subtitle_social));
                eventTitleEditTxt.setHint(context.getString(R.string.frag_nd_event_title_social_hint));
                break;
        }
    }
    // -------------------------------------------------------------------------------------------//

    // NAVIGATION LISTENERS ----------------------------------------------------------------------//
    private void setupNavigationButtons() {

        navBack.setVisibility(View.INVISIBLE); // hide back in title (first screen)
        navNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveChoice();

                switch (typeOfEvent) {
                    case GEN:
                        // Navigate to date frag
                        handler.navigateFragmentForward(typeOfEvent, context.getString(R.string.nd_date_tag));
                        break;
                    case APPT:
                        // Navigate to date frag
                        handler.navigateFragmentForward(typeOfEvent, context.getString(R.string.nd_date_tag));
                        break;
                    case SHOPPING:
                        // Navigate to date frag
                        handler.navigateFragmentForward(typeOfEvent, context.getString(R.string.nd_date_tag));
                        break;
                    case BIRTH:
                        // Navigate to date frag
                        handler.navigateFragmentForward(typeOfEvent, context.getString(R.string.nd_date_tag));
                        break;
                    case MEDIC:
                        handler.navigateFragmentForward(typeOfEvent, context.getString(R.string.nd_event_after_tag));
                        break;
                    case DAILY:
                        // Navigate to date frag
                        handler.navigateFragmentForward(typeOfEvent, context.getString(R.string.nd_date_tag));
                        break;
                    case SOCIAL:
                        handler.navigateFragmentForward(typeOfEvent, context.getString(R.string.nd_where_tag));
                        break;
                }
            }
        });

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

        if (rootView != null) {
            // Listen for touch of the root view
            rootView.setOnTouchListener(new View.OnTouchListener() {
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

    private void saveChoice() {
        String text = eventTitleEditTxt.getText().toString();
        if (!TextUtils.isEmpty(text.trim())) {
            // Save title
            handler.getReminder().setTitle(text);
        } else {
            handler.getReminder().setTitle(null);
        }
    }

}
