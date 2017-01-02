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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import highway62.reminderapp.constants.Consts;
//import highway62.reminderapp.gestures.OnShakeListener;
//import highway62.reminderapp.gestures.ShakeDetector;
//import highway62.reminderapp.gestures.WearYShake;
import highway62.reminderapp.reminders.BaseReminder;

public class WearReminderNotificationActivity extends Activity {

    WatchViewStub layoutContainer;
    TextView title;
    TextView dateTxt;
    TextView timeTxt;
    Button dismissBtn;
    BaseReminder reminder;
    private SensorManager mSensorMgr;
    private Vibrator vibrator;

//    private ShakeDetector yShake;
//    private OnShakeListener cancelListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear_reminder_notification);
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

        layoutContainer = (WatchViewStub) findViewById(R.id.wearReminderNotificationCont);
        layoutContainer.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub watchViewStub) {

                title = (TextView) layoutContainer.findViewById(R.id.notificationTitleText);
                dateTxt = (TextView) layoutContainer.findViewById(R.id.notificationDateText);
                timeTxt = (TextView) layoutContainer.findViewById(R.id.notificationTimeText);
                dismissBtn = (Button) layoutContainer.findViewById(R.id.notificationDismissBtn);

                switch (reminder.getType()) {
                    case GEN:
                        title.setText("You have an EVENT");
                        break;
                    case APPT:
                        title.setText("You have an APPOINTMENT ");
                        break;
                    case SHOPPING:
                        title.setText("You have a SHOPPING TRIP ");
                        break;
                    case BIRTH:
                        title.setText("You have a BIRTHDAY ");
                        break;
                    case MEDIC:
                        title.setText("You have MEDICATION to take, ");
                        break;
                    case DAILY:
                        title.setText("You have a DAILY EVENT ");
                        break;
                    case SOCIAL:
                        title.setText("You have a SOCIAL EVENT ");
                        break;
                }

                DateTime dt = new DateTime(reminder.getDateTime());
                DateTimeFormatter timeForm = DateTimeFormat.forPattern("HH:mm");
                DateTimeFormatter dateForm = DateTimeFormat.forPattern("dd/MM/yyyy");

                dateTxt.setText("On " + dateForm.print(dt));
                timeTxt.setText("At " + timeForm.print(dt));

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
