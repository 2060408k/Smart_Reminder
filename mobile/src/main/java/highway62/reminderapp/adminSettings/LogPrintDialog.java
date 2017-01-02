package highway62.reminderapp.adminSettings;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import highway62.reminderapp.R;
import highway62.reminderapp.constants.ReminderType;

/**
 * Created by Highway62 on 29/08/2016.
 */
public class LogPrintDialog extends Dialog implements PrinterListener{

    ReminderType type;
    Context context;
    TextView titleText;
    TextView printingText;
    TextView progressText;
    Button cancelBtn;
    Button printBtn;

    LogPrintDialog(Context context, ReminderType type){
        super(context);
        this.context = context;
        this.type = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_print_log_dialog);
        titleText = (TextView) findViewById(R.id.printLogTitletext);

        if(type.equals(ReminderType.REMINDER)){
            titleText.setText(context.getString(R.string.print_log_title_text) + " Reminders");
        }else{
            titleText.setText(context.getString(R.string.print_log_title_text) + " UPs");
        }

        printingText = (TextView) findViewById(R.id.printLogPrintingTxt);
        progressText = (TextView) findViewById(R.id.printLogProgressTxt);
        printingText.setVisibility(View.INVISIBLE);
        progressText.setVisibility(View.INVISIBLE);

        cancelBtn = (Button) findViewById(R.id.printLogCancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        printBtn = (Button) findViewById(R.id.printLogBtn);
        printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printingText.setVisibility(View.VISIBLE);
                progressText.setVisibility(View.VISIBLE);
                if(type.equals(ReminderType.REMINDER)){
                    printReminders();
                }else{
                    printPrompts();
                }
            }
        });
    }

    protected void printReminders(){
        new LogPrinter(context, this, progressText).printReminders();
    }

    protected void printPrompts(){
        new LogPrinter(context, this, progressText).printPrompts();
    }

    @Override
    public void onFinishedPrinting(boolean successful, String reason) {
        if(successful){
            String loc = context.getString(R.string.print_log_location);
            String msg = type.name() + " FILE: Stored in " + loc;
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(context, reason, Toast.LENGTH_LONG).show();
        }
        dismiss();
    }

    @Override
    public void upDateProgress(int progress) {
        progressText.setText(context.getString(R.string.print_log_progress_text) + " " + progress + "%");
    }
}
