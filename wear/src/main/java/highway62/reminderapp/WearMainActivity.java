package highway62.reminderapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import highway62.reminderapp.constants.Consts;
import highway62.reminderapp.constants.WearModes;
//import highway62.reminderapp.gestures.OnShakeListener;
//import highway62.reminderapp.gestures.ShakeDetector;
//import highway62.reminderapp.gestures.WearXFlick;
//import highway62.reminderapp.gestures.WearYShake;
import highway62.reminderapp.reminderhandlers.ReminderHandler;
import highway62.reminderapp.reminders.AudioReminder;
import highway62.reminderapp.settings.Settings;

public class WearMainActivity extends WearableActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private WearModes activityMode;
    private SensorManager mSensorMgr;
    private Vibrator vibrator;
    private WatchViewStub layoutContainer;
    private TextView versionText;
    private LinearLayout mainContainer;
    private TextView minsText;
    private ImageView settingsIcon;
    private RelativeLayout leftNav;
    private RelativeLayout rightNav;
    private ImageView viewRemindersBtn;
    private RelativeLayout quickSettingsBtn;
    private int quickTimeIndex = 0;
    private Settings settings;

//    private ShakeDetector yShake;
//    private ShakeDetector xFlick;
    private boolean vibPermissionGranted = false;
    private boolean bindNotifPermissionGranted = false;
    private boolean recordAudioPermissionGranted = false;
    private boolean layoutInflated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear_main);

        // Set activity mode
        activityMode = WearModes.QUICK;

        settings = new Settings(this);
        
        layoutContainer = (WatchViewStub) findViewById(R.id.mainWearContainer);
        layoutContainer.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub watchViewStub) {
                mainContainer = (LinearLayout) layoutContainer.findViewById(R.id.mainWearActivityView);
                versionText = (TextView) layoutContainer.findViewById(R.id.mainScreenVersion);
                versionText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openReminderActivity();
                    }
                });

                leftNav = (RelativeLayout) layoutContainer.findViewById(R.id.mainLeftNav);
                leftNav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeActivityMode();
                    }
                });

                rightNav = (RelativeLayout) layoutContainer.findViewById(R.id.mainRightNav);
                rightNav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeActivityMode();
                    }
                });

                quickSettingsBtn = (RelativeLayout) layoutContainer.findViewById(R.id.mainQuickSettingsBtn);
                quickSettingsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeQuickTime();
                    }
                });

                viewRemindersBtn = (ImageView) layoutContainer.findViewById(R.id.viewRemindersBtn);
                viewRemindersBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<AudioReminder> reminders = ReminderHandler.getAllReminders(WearMainActivity.this);
                        if(reminders != null && reminders.size() > 0) {
                            Intent intent = new Intent(WearMainActivity.this, ViewRemindersActivity.class);
                            intent.putExtra(Consts.INTENT_MODE, activityMode);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            WearMainActivity.this.finish();
                        }else{
                            Toast.makeText(WearMainActivity.this, "No Reminders Set", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                minsText = (TextView) layoutContainer.findViewById(R.id.quickMinsText);
                settingsIcon = (ImageView) layoutContainer.findViewById(R.id.mainSettings);

                loadQuickTime();
                resetActivityMode();

            }
        });

        // Request permissions
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.VIBRATE,
                    Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WAKE_LOCK}
                    ,Consts.PERMISSION_CODE);
        }

        // Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorMgr = (SensorManager) getSystemService(Activity.SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//        setupGestureListeners();
        loadQuickTime();
    }

    @Override
    protected void onPause() {
//        tearDownGestureListeners();
        vibrator = null;
        super.onPause();
    }

//    private void setupGestureListeners(){
//
//        // "Up and Down" shake to change activity modes
//        yShake = new WearYShake();
//        yShake.setOnShakeListener(new OnShakeListener() {
//            @Override
//            public void onShake() {
//                Log.e("SHAKE", "Y-Shake detected");
//                changeActivityMode();
//            }
//        });
//        mSensorMgr.registerListener(yShake, mSensorMgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_GAME);
//
//        // Wrist rotation to open reminder activity
//        xFlick = new WearXFlick();
//        xFlick.setOnShakeListener(new OnShakeListener() {
//            @Override
//            public void onShake() {
//                Log.e("SHAKE", "xFlick detected");
//                openReminderActivity();
//            }
//        });
//        mSensorMgr.registerListener(xFlick, mSensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);
//
//    }

//    private void tearDownGestureListeners() {
//        mSensorMgr.unregisterListener(yShake);
//        mSensorMgr.unregisterListener(xFlick);
//        yShake.setOnShakeListener(null);
//        yShake = null;
//        xFlick.setOnShakeListener(null);
//        xFlick = null;
//        mSensorMgr = null;
//    }

    private void openReminderActivity(){
        Log.e("INFLATE", "Opening reminder activity with mode: " + activityMode.name());
        Intent intent = new Intent(this, WearSetReminderActivity.class);
        intent.putExtra(Consts.INTENT_MODE, activityMode);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        this.finish();
    }

    private void changeActivityMode(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(vibPermissionGranted){
                vibrator.vibrate(50);
            }
        }else{
            vibrator.vibrate(50);
        }

        if(activityMode.equals(WearModes.QUICK)){
            changeModeTo(WearModes.MANUAL);
        }else{
            changeModeTo(WearModes.QUICK);
        }
    }

    private void changeModeTo(WearModes mode){
        Log.e("INFLATE", "changeModeTo called with mode: " + mode.name());
        if(mode.equals(WearModes.MANUAL)){
            activityMode = WearModes.MANUAL;

            // Change Background colour to blue
            mainContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.cs3Blue));

            //Change text to "manual"
            TextView versionText = (TextView) findViewById(R.id.mainScreenVersion);
            versionText.setText("Manual\nReminder");

            // Hide mins text
            minsText.setVisibility(View.GONE);
            settingsIcon.setVisibility(View.GONE);
        }else{
            activityMode = WearModes.QUICK;
            Log.e("INFLATE", "in modeChangedTo, activity mode changed to: " + activityMode.name());

            // Change background colour to green
            mainContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.cs3GreenDark3));

            //Change text to "manual"
            TextView versionText = (TextView) findViewById(R.id.mainScreenVersion);
            versionText.setText("Quick\nReminder");

            // Show mins text
            minsText.setVisibility(View.VISIBLE);
            settingsIcon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Consts.PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    vibPermissionGranted = true;
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

                if(grantResults.length > 0
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    bindNotifPermissionGranted = true;
                }else{

                }

                if(grantResults.length > 0
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED){
                    recordAudioPermissionGranted = true;
                }else{

                }

                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void resetActivityMode(){
        Intent intent = getIntent();
        if(intent != null){
            if(intent.getSerializableExtra(Consts.INTENT_MODE) != null){
                activityMode = (WearModes) intent.getSerializableExtra(Consts.INTENT_MODE);
            }
        }

        if(activityMode.equals(WearModes.QUICK)){
            changeModeTo(WearModes.QUICK);
        }else{
            changeModeTo(WearModes.MANUAL);
        }
    }

    /**
     * Loads the quick reminder time from the prefs, if pre-set
     */
    private void loadQuickTime(){
        quickTimeIndex = settings.getQuickReminderIndex();
        if(minsText != null) {
            minsText.setText(Consts.QUICK_TIMES[quickTimeIndex] + " " + Consts.QUICK_SCALES[quickTimeIndex].name());
        }
    }

    private void changeQuickTime(){
        quickTimeIndex = (quickTimeIndex + 1) % Consts.indexLimit;
        settings.setQuickReminderTime(Consts.QUICK_TIMES[quickTimeIndex]);
        settings.setQuickReminderScale(Consts.QUICK_SCALES[quickTimeIndex]);
        settings.setQuickReminderIndex(quickTimeIndex);

        // Change text
        minsText.setText(Consts.QUICK_TIMES[quickTimeIndex] + " " + Consts.QUICK_SCALES[quickTimeIndex].name());
    }
}
