package highway62.reminderapp;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import highway62.reminderapp.adminSettings.SettingsInterface;
import highway62.reminderapp.constants.Consts;
import highway62.reminderapp.constants.ReminderType;
import highway62.reminderapp.reminderhandlers.ReminderHandler;
import highway62.reminderapp.reminders.BaseReminder;

public class PromptNotificationActivity extends AppCompatActivity {

    ImageView questionImage;
    TextView mainText;
    Button dismissBtn;
    Button okBtn;
    BaseReminder reminder;
    SettingsInterface settings;
    private Vibrator vibrator;
    Ringtone ringTone;
    Thread vibThread;
    Thread soundThread;
    boolean ringToneCancelled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt_notification);
        settings = new SettingsInterface(this);
        ActionBar ab = getSupportActionBar();
        if(ab != null){
            ab.hide();
        }

        Intent intent = getIntent();
        if(intent != null && intent.getParcelableExtra(Consts.REMINDER_INTENT) != null){
            reminder = intent.getParcelableExtra(Consts.REMINDER_INTENT);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        questionImage = (ImageView) findViewById(R.id.promptNotificationQuestionImg);
        Animation flashAnimation = AnimationUtils.loadAnimation(this, R.anim.flashing_tween);
        questionImage.startAnimation(flashAnimation);
        mainText = (TextView) findViewById(R.id.promptNotificationMainTxt);
        dismissBtn = (Button) findViewById(R.id.promptNotificationDismissBtn);
        okBtn = (Button) findViewById(R.id.promptNotificationOKBtn);

        mainText.setText(reminder.getNotes());
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

        okBtn.setOnClickListener(new View.OnClickListener() {
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

                ReminderHandler.updatePromptResponded(PromptNotificationActivity.this, reminder);
                Intent intent = new Intent(PromptNotificationActivity.this, ReminderActivity.class);
                startActivity(intent);
            }
        });

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
                    ringTone = RingtoneManager.getRingtone(PromptNotificationActivity.this, Uri.parse(sound));
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
                            if(!ringToneCancelled) {
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
