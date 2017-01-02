package highway62.reminderapp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;

import highway62.reminderapp.R;
import highway62.reminderapp.ReminderActivity;

/**
 * Created by Highway62 on 08/08/2016.
 */
public class EventNotificationDialog extends Dialog implements View.OnClickListener, DialogInterface.OnDismissListener{

    private EventDialogListener mListener;
    private ReminderActivity context;
    private NumberPicker numPicker;
    private Spinner timeScaleSpinner;
    private int existingValue;
    private int existingTimeIndex;

    public EventNotificationDialog(Context context) {
        super(context);
        this.context = (ReminderActivity) context;
        this.existingValue = -1;
        this.existingTimeIndex = -1;
    }

    public EventNotificationDialog(Context context, int existingValue, int existingTimeIndex) {
        super(context);
        this.context = (ReminderActivity) context;
        this.existingValue = existingValue;
        this.existingTimeIndex = existingTimeIndex;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(context.getString(R.string.frag_bs_gen_event_dialog_title));
        setContentView(R.layout.event_notification_dialog);

        numPicker = (NumberPicker) findViewById(R.id.frag_bs_custom_event_notification_num_picker);
        numPicker.setMinValue(1);
        numPicker.setMaxValue(99);
        numPicker.setWrapSelectorWheel(false);

        timeScaleSpinner = (Spinner) findViewById(R.id.frag_bs_custom_event_notification_spinner);
        ArrayAdapter<CharSequence> notificationDialogAdapter = ArrayAdapter.createFromResource(context,
                R.array.frag_bs_gen_event_notification_custom_array, android.R.layout.simple_spinner_item);
        notificationDialogAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeScaleSpinner.setAdapter(notificationDialogAdapter);

        if(existingTimeIndex != -1){
            timeScaleSpinner.setSelection(existingTimeIndex);
        }

        if(existingValue != -1){
            numPicker.setValue(existingValue);
        }


        Button okButton = (Button) findViewById(R.id.frag_bs_custom_event_notification_button);
        okButton.setOnClickListener(this);
        setOnDismissListener(this);
    }

    // OK button onclick listenr
    @Override
    public void onClick(View v) {
        dismiss();
    }



    public void setEventDialogListener(EventDialogListener listener){
        this.mListener = listener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        int i = numPicker.getValue();
        int j = timeScaleSpinner.getSelectedItemPosition();
        mListener.passResults(i, j);
    }
}
