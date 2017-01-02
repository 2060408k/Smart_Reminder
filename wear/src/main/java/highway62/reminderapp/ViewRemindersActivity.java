package highway62.reminderapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import highway62.reminderapp.adapters.ViewReminderAdapter;
import highway62.reminderapp.constants.Consts;
import highway62.reminderapp.constants.WearModes;
import highway62.reminderapp.recording.SoundHandler;
import highway62.reminderapp.reminderhandlers.ReminderHandler;
import highway62.reminderapp.reminders.AudioReminder;

public class ViewRemindersActivity extends Activity {

    private WearModes activityMode = WearModes.QUICK;;
    private WatchViewStub layoutContainer;
    private ListView viewReminderListView;
    private SoundHandler soundHandler;
    ViewReminderAdapter adapter;
    TextView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reminders);
        Intent i = getIntent();
        if(i != null && i.hasExtra(Consts.INTENT_MODE)){
            activityMode = (WearModes) i.getSerializableExtra(Consts.INTENT_MODE);
        }
        soundHandler = new SoundHandler(this);
        adapter = new ViewReminderAdapter(this, getReminders());
        layoutContainer = (WatchViewStub) findViewById(R.id.wearViewRemindersContainer);
        layoutContainer.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub watchViewStub) {
                viewReminderListView = (ListView) layoutContainer.findViewById(R.id.viewReminderList);
                viewReminderListView.setAdapter(adapter);
                backBtn = (TextView) layoutContainer.findViewById(R.id.viewBackBtn);
                backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goBackToHomeScreen();
                    }
                });
            }
        });
    }

    private void goBackToHomeScreen(){
        Intent intent = new Intent(this, WearMainActivity.class);
        intent.putExtra(Consts.INTENT_MODE, activityMode);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private ArrayList<AudioReminder> getReminders(){
        return ReminderHandler.getAllReminders(this);
    }

    public static class ViewRemindViewHolder{
        public TextView reminderTimeText;
        public ImageView deleteBtn;
        public ImageView playBtn;
    }

    public void playAudio(byte[] audio){
        if(soundHandler.isPlaying()){
            soundHandler.stopPlaying();
        }
        soundHandler.startPlaying(audio);
    }

    public void stopAudio(){
        if(soundHandler.isPlaying()){
            soundHandler.stopPlaying();
        }
    }

    public boolean audioIsPlaying(){
        return soundHandler.isPlaying();
    }

    @Override
    protected void onPause() {
        if(soundHandler != null && soundHandler.isPlaying()){
            soundHandler.stopPlaying();
        }
        super.onPause();
    }
}
