package highway62.reminderapp;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

import highway62.reminderapp.adminSettings.SettingsInterface;
import highway62.reminderapp.constants.Consts;
import highway62.reminderapp.constants.ReminderType;
import highway62.reminderapp.reminders.BaseReminder;

public class ReminderNotificationActivity extends AppCompatActivity {

    BaseReminder reminder;
    TextView reminderType;
    TextView reminderDate;
    TextView reminderTime;
    Button dismissBtn;
    Button viewBtn;
    ImageView reminderHead;
    android.support.v7.app.ActionBar actionBar;
    SettingsInterface settings;
    private final String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday"
            , "Thursday", "Friday", "Saturday", "Sunday"};
    private Vibrator vibrator;
    Ringtone ringTone;
    Thread vibThread;
    Thread soundThread;
    boolean ringToneCancelled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_notification);
        settings = new SettingsInterface(this);
        actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }

        reminderHead = (ImageView) findViewById(R.id.reminder_head);
        // Set animation
        Animation flashAnimation = AnimationUtils.loadAnimation(this, R.anim.flashing_tween);
        reminderHead.startAnimation(flashAnimation);
        reminderType = (TextView) findViewById(R.id.reminderTypeText);
        reminderDate = (TextView) findViewById(R.id.reminderDateTxt);
        reminderTime = (TextView) findViewById(R.id.reminderTimeTxt);
        dismissBtn = (Button) findViewById(R.id.reminderCancelBtn);
        viewBtn = (Button) findViewById(R.id.reminderOKBtn);

        Intent intent = getIntent();
        if(intent != null){
            reminder = intent.getParcelableExtra(Consts.REMINDER_INTENT);
            setupTextViews();
        }else{
            Log.e("REMIND", "Failed to get reminder intent");
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setBtnListeners();

        // Handle vibration and sound
        ReminderType reminderType = reminder.getReminderType();

        int promptLevel = reminder.getPromptLevel();
        if (promptLevel == -1) {
            promptLevel = settings.getDefaultUserSubtletyLevel();
        }

        displayReminderForLevel(promptLevel, reminderType);

    }

    private void displayReminderForLevel(final int level, ReminderType rType) {

        if (settings.useDefaultLevelsIsSet()) {
            rType = null;
        }

        final ReminderType type = rType;

        // Vibrate -------------------------------------------------------------------------------//
        if (settings.enableVibrateIsSet(level, type)) {
            vibThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (vibrator.hasVibrator()) {
                        // Vibrate with the given pattern (-1 indicates not to repeat indefinitely)
                        vibrator.vibrate(getVibrationPattern(level, type), -1);
                    }

                }
            });
            vibThread.start();
        }
        //----------------------------------------------------------------------------------------//

        // Sound ---------------------------------------------------------------------------------//
        if (settings.enableSoundIsSet(level, type)) {
            soundThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    // Get sound settings
                    String sound = settings.getSoundChoice(level, type);
                    ringTone = RingtoneManager.getRingtone(ReminderNotificationActivity.this, Uri.parse(sound));
                    int soundVolume = settings.getVolumeLevel(level, type); // 0 = quiet -> 2 = loud

                    // Get Audio Manager to override device volume settings
                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    int current_volume = audioManager.getStreamVolume(AudioManager.STREAM_RING);

                    // Get volumes from audio manager for settings
                    int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
                    Log.e("SOUND", "max volume: " + maxVolume);
                    int mediumVolume = Math.round((float) maxVolume / 2);
                    Log.e("SOUND", "medium volume: " + mediumVolume);
                    int lowVolume = Math.round((float) maxVolume / 3);
                    Log.e("SOUND", "low volume: " + lowVolume);
                    int[] volumes = {lowVolume, mediumVolume, maxVolume};

                    // TODO check the volume

                    // Set volume based on settings
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, volumes[soundVolume], 0);

                    // Play sound once
                    ringTone.play();

                    if (settings.soundRepeatingIsSet(level, type)) {
                        float repeatSoundAfterInMins = settings.getSoundRepeatAfterInMins(level, type);
                        int repeatSoundAfterInMillis = (int) ((repeatSoundAfterInMins * 60) * 1000);
                        // Repeat
                        for (int i = 0; i < settings.getNoOfTimesToRepeatSound(level, type); i++) {
                            try {
                                Thread.sleep(repeatSoundAfterInMillis);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            if(!ringToneCancelled){
                                ringTone.play();
                            }
                        }
                    }

                    // Set device volume back to what is was at before playing the notification sound
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, current_volume, 0);

                }
            });

            soundThread.start();
        }
        //----------------------------------------------------------------------------------------//
    }

    private void setBtnListeners(){
        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vibrator != null && vibThread != null){
                    vibrator.cancel();
                    vibThread.interrupt();
                }

                if(ringTone != null && soundThread != null){
                    ringTone.stop();
                    ringToneCancelled = true;
                    soundThread.interrupt();
                }

                finish();
            }
        });

        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(vibrator != null && vibThread != null){
                    vibrator.cancel();
                    vibThread.interrupt();
                }

                if(ringTone != null && soundThread != null){
                    ringTone.stop();
                    ringToneCancelled = true;
                    soundThread.interrupt();
                }

                Intent intent = new Intent(ReminderNotificationActivity.this, ReminderDetailActivity.class);
                intent.putExtra(Consts.REMINDER_INTENT, reminder);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupTextViews(){
        if(reminder.getReminderType().equals(ReminderType.REMINDER)) {
            String whenText = getWhenText();

            switch (reminder.getType()) {
                case GEN:
                    reminderType.setText("You have an EVENT " + whenText);
                    break;
                case APPT:
                    reminderType.setText("You have an APPOINTMENT " + whenText);
                    break;
                case SHOPPING:
                    reminderType.setText("You have a SHOPPING TRIP " + whenText);
                    break;
                case BIRTH:
                    reminderType.setText("You have a BIRTHDAY " + whenText);
                    break;
                case MEDIC:
                    reminderType.setText("You have MEDICATION to take, " + whenText);
                    break;
                case DAILY:
                    reminderType.setText("You have a DAILY EVENT " + whenText);
                    break;
                case SOCIAL:
                    reminderType.setText("You have a SOCIAL EVENT " + whenText);
                    break;
            }
        }

        DateTime dayTime = new DateTime(reminder.getDateTime());
        String dayOfWeek = daysOfWeek[dayTime.getDayOfWeek() - 1];

        DateTime dt = new DateTime(reminder.getDateTime());
        DateTimeFormatter timeForm = DateTimeFormat.forPattern("HH:mm");
        DateTimeFormatter dateForm = DateTimeFormat.forPattern("dd/MM/yyyy");

        reminderDate.setText("On " + dayOfWeek + " " + dateForm.print(dt));
        reminderTime.setText("At " + timeForm.print(dt));
    }

    private String getWhenText(){

        DateTime dt = new DateTime(reminder.getDateTime());
        DateTime ct = new DateTime();
        Duration diffDay = new Duration(ct.withTimeAtStartOfDay(), dt.withTimeAtStartOfDay());

        if (diffDay.getStandardHours() < 24) {
            return "today";
        } else if (diffDay.getStandardHours() >= 24 && diffDay.getStandardDays() < 7) {
            return "this week";
        } else if (diffDay.getStandardDays() >= 7 && diffDay.getStandardDays() < 14) {
            return "next week";
        }else if (diffDay.getStandardDays() >= 14 && diffDay.getStandardDays() < 21) {
            return "in 3 weeks";
        }else if (diffDay.getStandardDays() >= 21 && diffDay.getStandardDays() < 28) {
            return "in 4 weeks";
        } else if (diffDay.getStandardDays() >= 28 && diffDay.getStandardDays() < 365) {
            return "over a month away";
        } else if (diffDay.getStandardDays() >= 365) {
            return "over a year away";
        }else{
            return "";
        }
    }

    private long[] getVibrationPattern(int level, ReminderType type) {

        int noOfBursts = settings.getNoOfVibBursts(level, type);
        int timeBetweenBursts = (settings.getTimeBetweenVibrationBursts(level, type) * 1000); //ms
        int vibrationLength = (int) (settings.getVibrationLength(level, type) * 1000); //ms
        float repeatAfterTimeInMins = settings.getVibrationRepeatAfterInMins(level, type);
        long repeatAfterTimeInMillis = (long) ((repeatAfterTimeInMins * 60) * 1000);
        int noOfTimesToRepeat = settings.getNoOfTimestoRepeat(level, type);


        ArrayList<Long> patternBuilder = new ArrayList<>();
        // Initial rest = 0. ie vibrate immediately
        patternBuilder.add(0L);

        for (int i = 0; i < noOfBursts; i++) {
            // Vibrate for length
            patternBuilder.add((long) vibrationLength);
            if (i != (noOfBursts - 1)) {
                // Rest for length (unless last index pos)
                patternBuilder.add((long) timeBetweenBursts);
            }
        }

        // Handle repeat
        if (settings.enableVibRepeatIsSet(level, type)) {
            for (int i = 0; i < noOfTimesToRepeat; i++) {
                patternBuilder.add(repeatAfterTimeInMillis);
                for (int j = 0; j < noOfBursts; j++) {
                    patternBuilder.add((long) vibrationLength);
                    if (j != (noOfBursts - 1)) {
                        patternBuilder.add((long) timeBetweenBursts);
                    }
                }
            }
        }

        // Convert patternBuilder arraylist to primitive long array
        long[] pattern = new long[patternBuilder.size()];
        for (int j = 0; j < patternBuilder.size(); j++) {
            pattern[j] = patternBuilder.get(j);
        }

        return pattern;
    }
}
