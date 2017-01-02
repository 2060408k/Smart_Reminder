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


public class NDEventAfterFragment extends Fragment {

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

    public NDEventAfterFragment() {}


    public static NDEventAfterFragment newInstance(EventType typeOfEvent) {
        NDEventAfterFragment fragment = new NDEventAfterFragment();
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
        View v = inflater.inflate(R.layout.fragment_nd_event_after, container, false);
        rootView = (LinearLayout) v.findViewById(R.id.frag_nd_rootView);

        titleText = (TextView) v.findViewById(R.id.frag_nd_title_text);
        subtitleText = (TextView) v.findViewById(R.id.frag_nd_subtitle_text);
        eventTitleEditTxt = (EditText) v.findViewById(R.id.frag_nd_title_editTxt);
        setupTouchListenersForHidingKeyboard();

        navNext = (TextView) v.findViewById(R.id.frag_nd_nextBtn);
        navBack = (TextView) v.findViewById(R.id.frag_nd_backBtn);

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

    private void prePopulateFields(){
        // Event After
        if (handler.getReminder().getEventAfter() != null) {
            eventTitleEditTxt.setText(handler.getReminder().getEventAfter());
        }
    }

    public void setHandler(OnNDFragmentListener handler) {
        this.handler = handler;
    }


    // NAVIGATION LISTENERS ----------------------------------------------------------------------//
    private void setupNavigationButtons() {

        navBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveChoice();

                handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_title_tag));
            }
        });

        navNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveChoice();

                handler.navigateFragmentForward(typeOfEvent, context.getString(R.string.nd_date_tag));
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
        if(imm != null){
            imm.showSoftInput(eventTitleEditTxt, InputMethodManager.SHOW_IMPLICIT);
        }
    }
    // -------------------------------------------------------------------------------------------//

    private void saveChoice(){
        String text = eventTitleEditTxt.getText().toString();
        if (!TextUtils.isEmpty(text.trim())) {
            // Save Event After
            handler.getReminder().setEventAfter(text);
        }else{
            handler.getReminder().setEventAfter(null);
        }
    }

}
