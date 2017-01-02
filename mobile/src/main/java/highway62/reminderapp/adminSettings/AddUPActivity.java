package highway62.reminderapp.adminSettings;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Toast;

import org.joda.time.DateTime;

import highway62.reminderapp.R;
import highway62.reminderapp.constants.ReminderType;
import highway62.reminderapp.pickers.DatePicker;
import highway62.reminderapp.pickers.TimePicker;
import highway62.reminderapp.reminderhandlers.ReminderHandler;
import highway62.reminderapp.reminders.BaseReminder;

public class AddUPActivity extends AppCompatActivity {

    private InputMethodManager imm;
    private ScrollView rootScrollView;

    private DatePicker datePicker;
    private EditText editText;
    private TimePicker timePicker;

    private SeekBar eventLevelSeekbar;
    private final int noOfPromptLevels = 3;
    private RelativeLayout eventLevel0;
    private RelativeLayout eventLevel1;
    private RelativeLayout eventLevel2;
    private RelativeLayout eventLevel3;

    private Button addpUPBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_up);
        setupActionBar();
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        rootScrollView = (ScrollView) findViewById(R.id.pref_prompt_add_up_root);

        datePicker = (DatePicker) findViewById(R.id.pref_prompt_add_up_datePicker);
        datePicker.setMinDate(System.currentTimeMillis() - 1000);
        editText = (EditText) findViewById(R.id.pref_prompt_add_up_editText);
        timePicker = (TimePicker) findViewById(R.id.pref_prompt_add_up_timePicker);
        eventLevelSeekbar = (SeekBar) findViewById(R.id.add_up_prompt_level_seekbar);
        eventLevel0 = (RelativeLayout) findViewById(R.id.frag_bs_event_level_text_0);
        eventLevel1 = (RelativeLayout) findViewById(R.id.frag_bs_event_level_text_1);
        eventLevel2 = (RelativeLayout) findViewById(R.id.frag_bs_event_level_text_2);
        eventLevel3 = (RelativeLayout) findViewById(R.id.frag_bs_event_level_text_3);
        addpUPBtn = (Button) findViewById(R.id.pref_prompt_add_up_doneBtn);

        setupPromptLevels();
        setupTouchListenersForHidingKeyboard();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.actionbar_settings_add_UP_title));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupTouchListenersForHidingKeyboard(){

        if(editText != null){
            // Listen for change to focus of EditText
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        hideKeyboard(v);
                    }
                }
            });
        }

        if(rootScrollView != null){
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
        if(imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

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

    public void addPrompt(View view){
        BaseReminder upReminder = new BaseReminder();

        DateTime dt = new DateTime()
                .withDayOfMonth(datePicker.getDayOfMonth())
                .withMonthOfYear(datePicker.getMonth() + 1)
                .withYear(datePicker.getYear())
                .withHourOfDay(timePicker.getCurrentHour())
                .withMinuteOfHour(timePicker.getCurrentMinute())
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        upReminder.setDateTime(dt.getMillis());

        if (!TextUtils.isEmpty(editText.getText().toString())) {
            upReminder.setNotes(editText.getText().toString());
        }else{
            upReminder.setNotes(getString(R.string.pref_UP_add_prompt_default_content));
        }

        upReminder.setPromptLevel(eventLevelSeekbar.getProgress());

        upReminder.setReminderType(ReminderType.PROMPT);

        saveReminder(upReminder);
    }

    private void saveReminder(BaseReminder reminder){
        ReminderHandler.setReminder(this, reminder);
        Toast.makeText(this, "Prompt Added", Toast.LENGTH_SHORT).show();
    }
}
