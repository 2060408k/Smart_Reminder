package highway62.reminderapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import highway62.reminderapp.constants.Consts;
import highway62.reminderapp.constants.DurationScale;
import highway62.reminderapp.constants.RecordingModes;
import highway62.reminderapp.constants.WearModes;
//import highway62.reminderapp.gestures.OnShakeListener;
//import highway62.reminderapp.gestures.ShakeDetector;
//import highway62.reminderapp.gestures.WearXFlick;
//import highway62.reminderapp.gestures.WearYFlick;
//import highway62.reminderapp.gestures.WearYShake;
import highway62.reminderapp.recording.RecorderHandler;
import highway62.reminderapp.reminderhandlers.ReminderHandler;
import highway62.reminderapp.reminders.AudioReminder;
import highway62.reminderapp.settings.Settings;

public class WearSetReminderActivity extends Activity {

    private WearModes activityMode;
    private RecordingModes recordingMode;

    private SensorManager mSensorMgr;
    private Vibrator vibrator;
    private RecorderHandler recorderHandler;
    private Settings settings;

    private WatchViewStub layoutContainer;
    private TextView modeTitle;
    private LinearLayout reminderContainer;
    private TextView recordButton;
    private TextView cancelText;
    private boolean activityPaused = false;

    // Gestures
//    private ShakeDetector yShake;
//    private ShakeDetector yFlick;
//    private ShakeDetector xFlick;
//    private OnShakeListener cancelListener;     // yFlick
//    private OnShakeListener recordListener;     // xFlick
//    private OnShakeListener saveListener;       // yShake

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear_set_reminder);

        recorderHandler = new RecorderHandler();
        settings = new Settings(this);

//        setupGestureListeners();

        // Wait for inlfater to inflate layout
        layoutContainer = (WatchViewStub) findViewById(R.id.wearSetReminderContainer);
        layoutContainer.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub watchViewStub) {

                // Get views
                modeTitle = (TextView) layoutContainer.findViewById(R.id.setReminderActivityModetitle);
                recordButton = (TextView) layoutContainer.findViewById(R.id.setReminderActivityRecordBtn);
                reminderContainer = (LinearLayout) layoutContainer.findViewById(R.id.setReminderActivityContainer);
                cancelText = (TextView) layoutContainer.findViewById(R.id.setReminderActivityCancelText);
                cancelText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (recordingMode) {
                            case STARTMODE:
                                cancelBeforeRecording();
                                break;
                            case RECORDMODE:
                                cancelDuringRecording();
                                break;
                            case STOPPEDMODE:
                                cancelAfterRecording();
                                break;
                        }
                    }
                });

                // Setup click listeners for starting/stopping recording
                setupRecordingClickListeners();

                // Get Activity Mode
                Intent intent = getIntent();
                if (intent != null) {
                    activityMode = (WearModes) intent.getSerializableExtra(Consts.INTENT_MODE);
                }

                // Setup activity based on activity mode
                if (activityMode != null) {
                    setupStartScreen();
                } else {
                    Log.e("REMIND", "Error retrieving the Activity Mode intent extra");
                }

            }
        });

        // Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorMgr = (SensorManager) getSystemService(Activity.SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//        setupGestures();
        if (activityPaused) {
            setupStartScreen();
            activityPaused = false;
        }
    }

    @Override
    protected void onPause() {
        stopRecording();
        clearAudio();
//        tearDownGestures();
        activityPaused = true;
        vibrator = null;
        super.onPause();
    }

    private void setupStartScreen() {

        stopRecording();
        clearAudio();

        recordingMode = RecordingModes.STARTMODE;

        if (activityMode.equals(WearModes.QUICK)) {
            modeTitle.setText("Quick Reminder");
            reminderContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.cs3GreenDark2));
        } else {
            modeTitle.setText("Manual Reminder");
            reminderContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.cs3Blue));
        }

        recordButton.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_circle_red));
        recordButton.setText("START");
        cancelText.setText("Exit");
    }

    private void setupRecordingScreen() {

        recordingMode = RecordingModes.RECORDMODE;

        reminderContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.FireBrick));

        recordButton.setText("STOP");
        cancelText.setText("Cancel");

    }

    private void setupAfterRecordingScreen() {

        if (activityMode.equals(WearModes.QUICK)) {
            recordingMode = RecordingModes.STOPPEDMODE;
            reminderContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.cs3GreenDark2));

            recordButton.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_circle_grey));
            recordButton.setText("SAVE");
            cancelText.setText("Cancel");
        } else {
            Intent intent = new Intent(this, WearSetManualTimeActivity.class);
            intent.putExtra(Consts.AUDIO_INTENT, getAudio());
            setupStartScreen();
            startActivity(intent);
            this.finish();
        }
    }

//    private void setupGestureListeners() {
//
//        cancelListener = new OnShakeListener() {
//            @Override
//            public void onShake() {
//                vibrator.vibrate(50);
//                switch (recordingMode) {
//                    case STARTMODE: {
//                        cancelBeforeRecording();
//                    }
//                    break;
//                    case RECORDMODE: {
//                        cancelDuringRecording();
//                    }
//                    break;
//                    case STOPPEDMODE:
//                        cancelAfterRecording();
//                        break;
//                }
//            }
//        };
//
//        recordListener = new OnShakeListener() {
//            @Override
//            public void onShake() {
//                switch (recordingMode) {
//                    case STARTMODE:
//                        vibrator.vibrate(50);
//                        setupRecordingScreen();
//                        startRecording();
//                        break;
//                    case RECORDMODE:
//                        stopRecording();
//                        vibrator.vibrate(50);
//                        setupAfterRecordingScreen();
//                        break;
//                    case STOPPEDMODE:
//                        // Do Nothing
//                        break;
//                }
//            }
//        };
//
//        saveListener = new OnShakeListener() {
//            @Override
//            public void onShake() {
//                switch (recordingMode) {
//                    case STARTMODE:
//                        // Do Nothing
//                        break;
//                    case RECORDMODE:
//                        // Do Nothing
//                        break;
//                    case STOPPEDMODE:
//                        vibrator.vibrate(50);
//                        saveReminder();
//                        break;
//                }
//            }
//        };
//    }

//    private void setupGestures() {
//        yShake = new WearYShake();
//        yFlick = new WearYFlick();
//        xFlick = new WearXFlick();
//        xFlick.setOnShakeListener(recordListener);
//        yFlick.setOnShakeListener(cancelListener);
//        yShake.setOnShakeListener(saveListener);
//        mSensorMgr.registerListener(xFlick, mSensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);
//        mSensorMgr.registerListener(yFlick, mSensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);
//        mSensorMgr.registerListener(yShake, mSensorMgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_GAME);
//    }

//    private void tearDownGestures() {
//        mSensorMgr.unregisterListener(xFlick);
//        mSensorMgr.unregisterListener(yFlick);
//        mSensorMgr.unregisterListener(yShake);
//        xFlick.setOnShakeListener(null);
//        yFlick.setOnShakeListener(null);
//        yShake.setOnShakeListener(null);
//        xFlick = null;
//        yFlick = null;
//        yShake = null;
//        mSensorMgr = null;
//    }

    private void setupRecordingClickListeners() {
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (recordingMode) {
                    case STARTMODE:
                        setupRecordingScreen();
                        startRecording();
                        break;
                    case RECORDMODE:
                        stopRecording();
                        setupAfterRecordingScreen();
                        break;
                    case STOPPEDMODE:
                        saveReminder();
                        break;
                }
            }
        });
    }

    private void startRecording() {
        recorderHandler.startRecording();
    }

    private void stopRecording() {
        recorderHandler.stopRecording();
    }

    private void clearAudio() {
        recorderHandler.clearReminderAudio();
    }

    private byte[] getAudio() {
        return recorderHandler.getReminderAudio();
    }

    private void cancelBeforeRecording() {
        Intent intent = new Intent(WearSetReminderActivity.this, WearMainActivity.class);
        intent.putExtra(Consts.INTENT_MODE, activityMode);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        WearSetReminderActivity.this.finish();
    }

    private void cancelDuringRecording() {
        stopRecording();
        clearAudio();
        setupStartScreen();
    }

    private void cancelAfterRecording() {
        clearAudio();
        setupStartScreen();
    }

    private void saveReminder() {
        AudioReminder reminder = new AudioReminder();

        Calendar date = Calendar.getInstance();
        reminder.setDateTimeSet(date.getTimeInMillis());

        int remTime = settings.getQuickReminderTime();
        DurationScale remScale = settings.getQuickReminderScale();
        reminder.setAfterTime(remTime);
        reminder.setAfterScale(remScale);

        long remDateTimeInMillis;

        // Set reminder dateTime
        switch (remScale){
            case MINS:
                remDateTimeInMillis = (remTime * Consts.ONE_MIN_IN_MILLIS) + date.getTimeInMillis();
                break;
            case HOURS:
                remDateTimeInMillis = (remTime * Consts.ONE_HOUR_IN_MILLIS) + date.getTimeInMillis();
                break;
            default:
                remDateTimeInMillis = (remTime * Consts.ONE_MIN_IN_MILLIS) + date.getTimeInMillis();
                break;
        }

        reminder.setDateTime(remDateTimeInMillis);
        reminder.setAudio(getAudio());

        ReminderHandler.addReminder(this, reminder);

        Toast.makeText(this, "Reminder Saved", Toast.LENGTH_SHORT).show();
        clearAudio();
        cancelBeforeRecording(); // Exit back to main screen
    }

    public RecordingModes getRecordingMode() {
        return this.recordingMode;
    }
}
