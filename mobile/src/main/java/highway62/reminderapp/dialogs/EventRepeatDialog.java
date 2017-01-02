package highway62.reminderapp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;

import highway62.reminderapp.R;
import highway62.reminderapp.ReminderActivity;

/**
 * Created by Highway62 on 09/08/2016.
 */
public class EventRepeatDialog extends Dialog implements View.OnClickListener, DialogInterface.OnDismissListener{

    private EventDialogListener mListener;
    private ReminderActivity context;
    private NumberPicker numPicker;
    private Spinner timeScaleSpinner;
    private int existingValue;

    public EventRepeatDialog(Context context) {
        super(context);
        this.context = (ReminderActivity) context;
    }

    public EventRepeatDialog(Context context, int existingValue) {
        super(context);
        this.context = (ReminderActivity) context;
        this.existingValue = existingValue;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Set Number of Weeks");
        setContentView(R.layout.event_repeat_dialog);

        numPicker = (NumberPicker) findViewById(R.id.frag_bs_event_repeat_custom_weeks_num_picker);
        numPicker.setMinValue(4);
        numPicker.setMaxValue(52);
        numPicker.setWrapSelectorWheel(false);
        if(existingValue != -1){
            numPicker.setValue(existingValue);
        }

        Button okButton = (Button) findViewById(R.id.frag_bs_event_repeat_custom_weeks_button);
        okButton.setOnClickListener(this);
        setOnDismissListener(this);

    }

    public void setDialogListener(EventDialogListener listener){
        this.mListener = listener;
    }


    @Override
    public void onClick(View v) {
        dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        int i = numPicker.getValue();
        mListener.passResults(i);
    }
}
