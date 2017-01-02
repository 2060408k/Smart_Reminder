package highway62.reminderapp.adminSettings;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import highway62.reminderapp.R;
import highway62.reminderapp.reminderhandlers.ReminderHandler;

/**
 * Created by Highway62 on 29/08/2016.
 */
public class LogViewReminderCountDialog extends Dialog {

    TextView reminderCountTxt;
    TextView promptCountText;
    Button okBtn;
    Context context;

    public LogViewReminderCountDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_view_log_count_dialog);
        reminderCountTxt = (TextView) findViewById(R.id.viewLogCountReminderText);
        promptCountText = (TextView) findViewById(R.id.viewLogCountPromptText);
        okBtn = (Button) findViewById(R.id.viewLogCountBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        setLogCounts();

    }

    private void setLogCounts(){
        long reminderCount = ReminderHandler.getReminderLogCount(context);
        long promptCount = ReminderHandler.getPromptLogCount(context);

        reminderCountTxt.setText(context.getString(R.string.view_log_count_reminder_text) + " " + reminderCount);
        promptCountText.setText(context.getString(R.string.view_log_count_prompt_text) + " " + promptCount);
    }

}
