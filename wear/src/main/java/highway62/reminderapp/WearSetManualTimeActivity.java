package highway62.reminderapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import highway62.reminderapp.constants.Consts;
import highway62.reminderapp.constants.DurationScale;
import highway62.reminderapp.constants.WearModes;
//import highway62.reminderapp.gestures.OnShakeListener;
//import highway62.reminderapp.gestures.ShakeDetector;
//import highway62.reminderapp.gestures.WearXFlick;
//import highway62.reminderapp.gestures.WearYFlick;
//import highway62.reminderapp.gestures.WearYShake;
import highway62.reminderapp.reminderhandlers.ReminderHandler;
import highway62.reminderapp.reminders.AudioReminder;
import highway62.reminderapp.viewpager.ManualTimeHandler;
import highway62.reminderapp.viewpager.ViewPagerAdapter;

public class WearSetManualTimeActivity extends FragmentActivity implements ManualTimeHandler {

    private WatchViewStub layoutContainer;
    private SensorManager mSensorMgr;
    private Vibrator vibrator;
    private byte[] audio;
    private ViewPager timeChooser;
    private ViewPagerAdapter adapter;
    private int currentPosition = 0;
    private int actualPostion = 0;
    private TextView cancelText;

    // Gestures
//    private ShakeDetector xFlick;
//    private ShakeDetector yShake;
//    private ShakeDetector yFlick;
//
//    private OnShakeListener cancelListener;     // yShake
//    private OnShakeListener selectListener;     // xFlick
//    private OnShakeListener scrollListener;     // yFlick

    private boolean layoutInflated = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear_set_manual_time);

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(Consts.AUDIO_INTENT)){
            audio = intent.getByteArrayExtra(Consts.AUDIO_INTENT);
        }else{
            exitToStartScreen(true);
        }

//        setupGestureListeners();

        layoutContainer = (WatchViewStub) findViewById(R.id.wearSetManualTimeCont);
        layoutContainer.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub watchViewStub) {

                cancelText = (TextView) layoutContainer.findViewById(R.id.setManualTimeCancelTxt);
                cancelText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelSetTime();
                    }
                });
                adapter = new ViewPagerAdapter(getSupportFragmentManager(), WearSetManualTimeActivity.this);
                timeChooser = (ViewPager) layoutContainer.findViewById(R.id.timeChooser);
                timeChooser.setAdapter(adapter);
                timeChooser.addOnPageChangeListener(adapter);
                currentPosition = Consts.FIRST_PAGE;
                timeChooser.setCurrentItem(currentPosition);
                // Set number of pages
                timeChooser.setOffscreenPageLimit(12);
                // Set no margin so other pages are hidden
                timeChooser.setPageMargin(0);

                layoutInflated = true;
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
    }

    @Override
    protected void onPause() {
//        tearDownGestures();
        mSensorMgr = null;
        vibrator = null;
        layoutInflated = false;
        super.onPause();
    }

//    private void setupGestureListeners(){
//
//        cancelListener = new OnShakeListener() {
//            @Override
//            public void onShake() {
//                cancelSetTime();
//            }
//        };
//
//        selectListener = new OnShakeListener() {
//            @Override
//            public void onShake() {
//                if(layoutInflated){
//                    selectSetTime();
//                }
//            }
//        };
//
//        scrollListener = new OnShakeListener() {
//            @Override
//            public void onShake() {
//                if(layoutInflated){
//                    vibrator.vibrate(50);
//                    currentPosition = timeChooser.getCurrentItem();
//                    currentPosition++;
//                    timeChooser.setCurrentItem(currentPosition, true);
//                }
//            }
//        };
//
//    }

    private void cancelSetTime(){
        exitToSetReminderScreen();
    }

    private void selectSetTime(){
        vibrator.vibrate(75);
        setManualAlarm(actualPostion);
        exitToStartScreen(false);
    }

    private void exitToSetReminderScreen(){
        audio = null;
        this.finish();
    }

    private void exitToStartScreen(boolean error){
        Intent intent = new Intent(this, WearMainActivity.class);
        intent.putExtra(Consts.INTENT_MODE, WearModes.MANUAL);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        if(error) {
            Toast.makeText(WearSetManualTimeActivity.this, "Error Reminder Not Saved", Toast.LENGTH_SHORT).show();
            Log.e("SETTIME", "Error: failed to get audio from intent, exiting to start screen");
        }else{
            Toast.makeText(WearSetManualTimeActivity.this, "Reminder Saved", Toast.LENGTH_SHORT).show();
        }
        audio = null;
        startActivity(intent);

        this.finish();
    }

//    private void setupGestures() {
//        xFlick = new WearXFlick();
//        yShake = new WearYShake();
//        yFlick = new WearYFlick();
//        xFlick.setOnShakeListener(selectListener);
//        yShake.setOnShakeListener(cancelListener);
//        yFlick.setOnShakeListener(scrollListener);
//        mSensorMgr.registerListener(xFlick, mSensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);
//        mSensorMgr.registerListener(yShake, mSensorMgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_GAME);
//        mSensorMgr.registerListener(yFlick, mSensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);
//    }
//
//    private void tearDownGestures() {
//        mSensorMgr.unregisterListener(xFlick);
//        mSensorMgr.unregisterListener(yShake);
//        mSensorMgr.unregisterListener(yFlick);
//        xFlick.setOnShakeListener(null);
//        yShake.setOnShakeListener(null);
//        yFlick.setOnShakeListener(null);
//        xFlick = null;
//        yShake = null;
//        yFlick = null;
//    }

    private void setManualAlarm(int pos) {
        Log.e("TIME", "Actual Position Set in Activity: " + pos);

        AudioReminder reminder = new AudioReminder();

        Calendar date = Calendar.getInstance();
        reminder.setDateTimeSet(date.getTimeInMillis());

        int remTime = Consts.REM_TIMES[pos];
        DurationScale remScale = Consts.REM_SCALES[pos];
        reminder.setAfterTime(remTime);
        reminder.setAfterScale(remScale);

        long remDateTimeInMillis;

        // Set reminder dateTime
        switch (remScale){
            case MINS:
                remDateTimeInMillis = (remTime * Consts.ONE_MIN_IN_MILLIS) + date.getTimeInMillis();
                break;
            case HOURS:
                remDateTimeInMillis = (remTime + Consts.ONE_HOUR_IN_MILLIS) + date.getTimeInMillis();
                break;
            default:
                remDateTimeInMillis = (remTime * Consts.ONE_MIN_IN_MILLIS) + date.getTimeInMillis();
                break;
        }

        reminder.setDateTime(remDateTimeInMillis);

        reminder.setAudio(audio);

        ReminderHandler.addManualReminder(this, reminder);

        /*
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        long TEN_SECS_IN_MILLIS = 10000;

        Calendar date = Calendar.getInstance();
        long t = date.getTimeInMillis();
        Date afterAddingMins = new Date(t + (Consts.TIME_HOURS[pos] * TEN_SECS_IN_MILLIS));

        alarmManager.setExact(AlarmManager.RTC_WAKEUP
                , afterAddingMins.getTime()
                , getReminderIntent(this, audio));
        */
    }

    @Override
    public void selectTime(int pos) {
        selectSetTime();
    }

    @Override
    public void cancel() {
        cancelSetTime();
    }

    @Override
    public void setActualPosition(int pos) {
        this.actualPostion = pos;
    }
}
