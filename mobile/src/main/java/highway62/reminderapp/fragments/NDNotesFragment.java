package highway62.reminderapp.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import highway62.reminderapp.R;
import highway62.reminderapp.ReminderActivity;
import highway62.reminderapp.adminSettings.SettingsInterface;
import highway62.reminderapp.constants.EventType;
import highway62.reminderapp.fragmentHandlers.OnNDFragmentListener;


public class NDNotesFragment extends Fragment {

    private static final String EVENT_TYPE = "event_type";
    private EventType typeOfEvent;
    private OnNDFragmentListener handler;
    private ReminderActivity context;
    private InputMethodManager imm;
    private ActionBar actionBar;
    private SettingsInterface settings;

    private LinearLayout rootView;
    private TextView titleText;
    private TextView subtitleText;
    private TextView navNext;
    private TextView navBack;

    private EditText eventTitleEditTxt;

    public NDNotesFragment() {}

    public static NDNotesFragment newInstance(EventType typeOfEvent) {
        NDNotesFragment fragment = new NDNotesFragment();
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
        View v = inflater.inflate(R.layout.fragment_nd_notes, container, false);
        rootView = (LinearLayout) v.findViewById(R.id.frag_nd_rootView);
        titleText = (TextView) v.findViewById(R.id.frag_nd_title_text);
        subtitleText = (TextView) v.findViewById(R.id.frag_nd_subtitle_text);
        navNext = (TextView) v.findViewById(R.id.frag_nd_nextBtn);
        navBack = (TextView) v.findViewById(R.id.frag_nd_backBtn);

        eventTitleEditTxt = (EditText) v.findViewById(R.id.frag_nd_title_editTxt);
        setupTouchListenersForHidingKeyboard();

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

    private void prePopulateFields(){
        if (handler.getReminder().getNotes() != null) {
            eventTitleEditTxt.setText(handler.getReminder().getNotes());
        }
    }

    public void setHandler(OnNDFragmentListener handler) {
        this.handler = handler;
    }

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

    // CATEGORY SPECIFIC CONTENT -----------------------------------------------------------------//
    private void setupTextViewContent() {
        switch (typeOfEvent) {
            case GEN:
                titleText.setText(context.getString(R.string.frag_nd_notes_gen));
                subtitleText.setText(context.getString(R.string.frag_nd_notes_gen_subtitle));
                eventTitleEditTxt.setHint(context.getString(R.string.frag_nd_notes_gen_hint));
                break;
            case APPT:
                titleText.setText(context.getString(R.string.frag_nd_notes_gen));
                subtitleText.setText(context.getString(R.string.frag_nd_notes_appt_subtitle));
                eventTitleEditTxt.setHint(context.getString(R.string.frag_nd_notes_appt_hint));
                break;
            case SHOPPING:
                titleText.setText(context.getString(R.string.frag_nd_notes_shopping));
                subtitleText.setText(context.getString(R.string.frag_nd_notes_shopping_subtitle));
                eventTitleEditTxt.setHint(context.getString(R.string.frag_nd_notes_shopping_hint));
                break;
            case BIRTH:
                titleText.setText(context.getString(R.string.frag_nd_notes_birthday));
                subtitleText.setText(context.getString(R.string.frag_nd_notes_birthday_subtitle));
                eventTitleEditTxt.setHint(context.getString(R.string.frag_nd_notes_birthday_hint));
                if(!settings.getUserSubtletyEnabled()){
                    Drawable img = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_nav_save, null);
                    navNext.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
                    navNext.setText("Save");                }
                break;
            case MEDIC:
                titleText.setText(context.getString(R.string.frag_nd_notes_gen));
                subtitleText.setText(context.getString(R.string.frag_nd_notes_gen_subtitle));
                eventTitleEditTxt.setHint(context.getString(R.string.frag_nd_notes_gen_hint));
                break;
            case DAILY:
                titleText.setText(context.getString(R.string.frag_nd_notes_gen));
                subtitleText.setText(context.getString(R.string.frag_nd_notes_daily_subtitle));
                eventTitleEditTxt.setHint(context.getString(R.string.frag_nd_notes_daily_hint));
                if(!settings.getUserSubtletyEnabled()){
                    Drawable img = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_nav_save, null);
                    navNext.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
                    navNext.setText("Save");                }
                break;
            case SOCIAL:
                titleText.setText(context.getString(R.string.frag_nd_notes_gen));
                subtitleText.setText(context.getString(R.string.frag_nd_notes_gen_subtitle));
                eventTitleEditTxt.setHint(context.getString(R.string.frag_nd_notes_gen_hint));
                break;
        }
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
                        // Navigate to notification frag
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_notification_tag));
                        break;
                    case APPT:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_notification_tag));
                        break;
                    case SHOPPING:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_notification_tag));
                        break;
                    case BIRTH:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_time_tag));
                        break;
                    case MEDIC:
                        break;
                    case DAILY:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_repeat_tag));
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
                        // Navigate to repeat frag
                        handler.navigateFragmentForward(typeOfEvent,context.getString(R.string.nd_repeat_tag));
                        break;
                    case APPT:
                        handler.navigateFragmentForward(typeOfEvent,context.getString(R.string.nd_repeat_tag));
                        break;
                    case SHOPPING:
                        handler.navigateFragmentForward(typeOfEvent,context.getString(R.string.nd_repeat_tag));
                        break;
                    case BIRTH:
                        if(settings.getUserSubtletyEnabled()){
                            handler.navigateFragmentForward(typeOfEvent,context.getString(R.string.nd_reminder_level_tag));
                        }else{
                            if (!TextUtils.isEmpty(eventTitleEditTxt.getText().toString())) {
                                // If presents have been entered, show the shopping dialog
                                showShoppingDialog();
                            }else{
                                handler.resetAfterSave(typeOfEvent);
                            }
                        }
                        break;
                    case MEDIC:
                        break;
                    case DAILY:
                        if(settings.getUserSubtletyEnabled()){
                            handler.navigateFragmentForward(typeOfEvent,context.getString(R.string.nd_reminder_level_tag));
                        }else{
                            handler.resetAfterSave(typeOfEvent);
                        }
                        break;
                    case SOCIAL:
                        break;
                }
            }
        });

    }

    private void showShoppingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Would you like to set a shopping reminder for the presents?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.resetAfterSaveBirthday();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.resetAfterSave(typeOfEvent);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }
    // -------------------------------------------------------------------------------------------//

    private void saveChoices(){
        if (!TextUtils.isEmpty(eventTitleEditTxt.getText().toString())) {
            handler.getReminder().setNotes(eventTitleEditTxt.getText().toString());
        }
    }

}
