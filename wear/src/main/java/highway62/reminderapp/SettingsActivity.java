package highway62.reminderapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;

public class SettingsActivity extends Activity {

    WatchViewStub layoutContainer;
    int mins = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Intent intent = getIntent();
        if(intent != null){
            //mins = intent.getIntExtra(Consts.INTENT_SETTINGS_INT, 30);
        }


    }
}
