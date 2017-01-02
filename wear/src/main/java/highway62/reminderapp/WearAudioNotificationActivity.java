package highway62.reminderapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import highway62.reminderapp.constants.Consts;
//import highway62.reminderapp.gestures.OnShakeListener;
//import highway62.reminderapp.gestures.ShakeDetector;
//import highway62.reminderapp.gestures.WearXFlick;
//import highway62.reminderapp.gestures.WearYShake;
import highway62.reminderapp.recording.SoundHandler;
import highway62.reminderapp.reminderhandlers.ReminderHandler;

public class WearAudioNotificationActivity extends Activity {

    private SensorManager mSensorMgr;
    private Vibrator vibrator;
    private SoundHandler soundHandler;

    private WatchViewStub layoutContainer;
    private byte[] audio;
    private long reminderID = -1;
    private TextView cancelBtn;
    private TextView playBtn;

//    private ShakeDetector xFlick;
//    private ShakeDetector yShake;
//    private OnShakeListener cancelListener;     // yShake
//    private OnShakeListener playListener;       // xFlick

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear_audio_notification);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Consts.REMINDER_ID_INTENT)) {
            reminderID = intent.getLongExtra(Consts.REMINDER_ID_INTENT, -1);
            getAudio();
            //audio = intent.getByteArrayExtra(Consts.AUDIO_INTENT);
        } else {
            finish();
        }

        soundHandler = new SoundHandler();
//
//        setupGestureListeners();

        layoutContainer = (WatchViewStub) findViewById(R.id.audioNotiContainer);
        layoutContainer.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub watchViewStub) {
                cancelBtn = (TextView) layoutContainer.findViewById(R.id.audioCancelBtn);
                playBtn = (TextView) layoutContainer.findViewById(R.id.audioPlayBtn);

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

                playBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playAudio();
                    }
                });
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
        if(soundHandler.isPlaying()){
            soundHandler.stopPlaying();
        }
//        tearDownGestures();
        vibrator = null;
        super.onPause();
    }

    @Override
    protected void onStop() {
        // Delete the audio from the database once the reminder notification is closed
        if(reminderID != -1) {
            ReminderHandler.deleteAudio(this, reminderID);
        }

        super.onStop();
    }

    private void getAudio(){
        if(reminderID != -1){
            audio = ReminderHandler.getAudio(this, reminderID);
        }else{
            Log.e("AUDIO", "Error retrieving audio from reminder handler, from wearaudionotification getAudio()");
            finish();
        }

        if(audio == null){
            Log.e("AUDIO", "Error retrieving audio from reminder handler, from wearaudionotification getAudio()");
            finish();
        }
    }

    private void playAudio() {
        if(!soundHandler.isPlaying()){
            vibrator.vibrate(50);
            soundHandler.startPlaying(audio);
        }
    }
//
//    private void setupGestureListeners() {
//
//        cancelListener = new OnShakeListener() {
//            @Override
//            public void onShake() {
//                vibrator.vibrate(50);
//                finish();
//            }
//        };
//
//        playListener = new OnShakeListener() {
//            @Override
//            public void onShake() {
//                playAudio();
//            }
//        };
//    }

//    private void setupGestures() {
//        xFlick = new WearXFlick();
//        yShake = new WearYShake();
//        xFlick.setOnShakeListener(playListener);
//        yShake.setOnShakeListener(cancelListener);
//        mSensorMgr.registerListener(xFlick, mSensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);
//        mSensorMgr.registerListener(yShake, mSensorMgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_GAME);
//    }
//
//    private void tearDownGestures() {
//        mSensorMgr.unregisterListener(xFlick);
//        mSensorMgr.unregisterListener(yShake);
//        xFlick.setOnShakeListener(null);
//        yShake.setOnShakeListener(null);
//        xFlick = null;
//        yShake = null;
//        mSensorMgr = null;
//    }
}
