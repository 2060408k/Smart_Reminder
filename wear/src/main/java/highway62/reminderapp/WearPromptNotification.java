package highway62.reminderapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import highway62.reminderapp.constants.Consts;
//import highway62.reminderapp.gestures.OnShakeListener;
//import highway62.reminderapp.gestures.ShakeDetector;
//import highway62.reminderapp.gestures.WearYShake;
import highway62.reminderapp.reminders.BaseReminder;

public class WearPromptNotification extends Activity {

    WatchViewStub layoutContainer;
    TextView mainTxt;
    Button dismissBtn;
    BaseReminder reminder;
    private SensorManager mSensorMgr;
    private Vibrator vibrator;

//    private ShakeDetector yShake;
//    private OnShakeListener cancelListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear_prompt_notification);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Consts.REMINDER_INTENT)){
            reminder = intent.getParcelableExtra(Consts.REMINDER_INTENT);
        }else{
            finish();
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//        setupGestureListeners();

        layoutContainer = (WatchViewStub) findViewById(R.id.wearPromptNotiCont);
        layoutContainer.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub watchViewStub) {
                mainTxt = (TextView) layoutContainer.findViewById(R.id.mainPromptText);
                dismissBtn = (Button) layoutContainer.findViewById(R.id.promptDismissBtn);
                mainTxt.setText(reminder.getNotes());
                dismissBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }
        });
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
        vibrator = null;
        super.onPause();
    }

//    private void setupGestureListeners(){
//        cancelListener = new OnShakeListener() {
//            @Override
//            public void onShake() {
//                vibrator.vibrate(50);
//                finish();
//            }
//        };
//    }
//
//    private void setupGestures() {
//        yShake = new WearYShake();
//        yShake.setOnShakeListener(cancelListener);
//        mSensorMgr.registerListener(yShake, mSensorMgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_GAME);
//    }
//
//    private void tearDownGestures() {
//        mSensorMgr.unregisterListener(yShake);
//        yShake.setOnShakeListener(null);
//        yShake = null;
//        mSensorMgr = null;
//    }
}
