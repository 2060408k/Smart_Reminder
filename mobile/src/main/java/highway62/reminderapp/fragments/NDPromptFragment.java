package highway62.reminderapp.fragments;


import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import highway62.reminderapp.R;
import highway62.reminderapp.ReminderActivity;
import highway62.reminderapp.adminSettings.SettingsInterface;
import highway62.reminderapp.constants.EventType;
import highway62.reminderapp.fragmentHandlers.OnNDFragmentListener;


public class NDPromptFragment extends Fragment {

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

    private SeekBar eventLevelSeekbar;
    private final int noOfPromptLevels = 3;
    private RelativeLayout eventLevel0;
    private RelativeLayout eventLevel1;
    private RelativeLayout eventLevel2;
    private RelativeLayout eventLevel3;

    public NDPromptFragment() {
    }

    public static NDPromptFragment newInstance(EventType typeOfEvent) {
        NDPromptFragment fragment = new NDPromptFragment();
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
        View v = inflater.inflate(R.layout.fragment_nd_prompt, container, false);

        titleText = (TextView) v.findViewById(R.id.frag_nd_title_text);
        subtitleText = (TextView) v.findViewById(R.id.frag_nd_subtitle_text);
        navNext = (TextView) v.findViewById(R.id.frag_nd_nextBtn);
        Drawable img = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_nav_save, null);
        navNext.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
        navNext.setText("Save");

        navBack = (TextView) v.findViewById(R.id.frag_nd_backBtn);

        eventLevelSeekbar = (SeekBar) v.findViewById(R.id.frag_bs_event_level_seekbar);
        eventLevel0 = (RelativeLayout) v.findViewById(R.id.frag_bs_event_level_text_0);
        eventLevel1 = (RelativeLayout) v.findViewById(R.id.frag_bs_event_level_text_1);
        eventLevel2 = (RelativeLayout) v.findViewById(R.id.frag_bs_event_level_text_2);
        eventLevel3 = (RelativeLayout) v.findViewById(R.id.frag_bs_event_level_text_3);
        setupPromptLevels();

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

    private void prePopulateFields(){
        // Prompt Levels
        if (handler.getReminder().getPromptLevel() != -1) {
            eventLevelSeekbar.setProgress(handler.getReminder().getPromptLevel());
        }
    }

    public void setHandler(OnNDFragmentListener handler) {
        this.handler = handler;
    }

    // LEVEL SETTINGS ----------------------------------------------------------------------------//

    private void setupPromptLevels() {
        eventLevelSeekbar.setMax(noOfPromptLevels);
        eventLevelSeekbar.setProgress(2);
        setSeekbarListener();
        setLevelTextButtonsListeners();
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
    // -------------------------------------------------------------------------------------------//

    // CATEGORY SPECIFIC CONTENT -----------------------------------------------------------------//
    private void setupTextViewContent() {
        // Title for time (may not change)
        titleText.setText(context.getString(R.string.frag_nd_prompt_gen));
        switch (typeOfEvent) {
            case GEN:
                subtitleText.setText(context.getString(R.string.frag_nd_prompt_gen_subtitle));
                break;
            case APPT:
                subtitleText.setText(context.getString(R.string.frag_nd_prompt_appt_subtitle));
                break;
            case SHOPPING:
                subtitleText.setText(context.getString(R.string.frag_nd_prompt_shopping_subtitle));
                break;
            case BIRTH:
                subtitleText.setText(context.getString(R.string.frag_nd_prompt_birthday_subtitle));
                break;
            case MEDIC:
                subtitleText.setText(context.getString(R.string.frag_nd_prompt_medic_subtitle));
                break;
            case DAILY:
                subtitleText.setText(context.getString(R.string.frag_nd_prompt_daily_subtitle));
                break;
            case SOCIAL:
                subtitleText.setText(context.getString(R.string.frag_nd_prompt_social_subtitle));
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

                switch (typeOfEvent) {
                    case GEN:
                        // Navigate to repeat frag
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_repeat_tag));
                        break;
                    case APPT:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_repeat_tag));
                        break;
                    case SHOPPING:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_repeat_tag));
                        break;
                    case BIRTH:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_notes_tag));
                        break;
                    case MEDIC:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_repeat_tag));
                        break;
                    case DAILY:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_notes_tag));
                        break;
                    case SOCIAL:
                        handler.navigateFragmentBack(typeOfEvent, context.getString(R.string.nd_time_tag));
                        break;
                }
            }
        });

        navNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveChoices();

                if (typeOfEvent.equals(EventType.BIRTH)) {
                    // Check if any presents have been specified, if so, show shopping dialog
                    if(handler.getReminder().getNotes() != null
                            && !TextUtils.isEmpty(handler.getReminder().getNotes())){
                        showShoppingDialog();
                    }else{
                        handler.resetAfterSave(typeOfEvent);
                    }
                } else {
                    handler.resetAfterSave(typeOfEvent);
                }
            }
        });
    }

    private void showShoppingDialog() {
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
        // prompt level
        if (settings.getUserSubtletyEnabled()) {
            handler.getReminder().setPromptLevel(eventLevelSeekbar.getProgress());
        }
    }
}
