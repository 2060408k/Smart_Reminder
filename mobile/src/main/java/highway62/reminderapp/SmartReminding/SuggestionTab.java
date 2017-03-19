package highway62.reminderapp.SmartReminding;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import java.util.List;

import highway62.reminderapp.R;

/**
 * Created by pbkou on 14/01/2017.
 */

public class SuggestionTab extends AppCompatActivity {
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suggestion_tab);

        this.linearLayout = (LinearLayout) findViewById(R.id.main_listview);
        TextView tv = new TextView(this);
        tv.setText("DOULEFKW");
        tv.setLayoutParams(new
                LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
        linearLayout.addView(tv);

    }
}
