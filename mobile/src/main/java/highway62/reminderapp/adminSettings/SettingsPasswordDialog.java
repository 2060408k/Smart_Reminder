package highway62.reminderapp.adminSettings;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import highway62.reminderapp.R;

/**
 * Created by Highway62 on 20/07/2016.
 */
public class SettingsPasswordDialog extends Dialog{

    private SettingsPasswordListener listener;
    private EditText editText;
    private Button cancelBtn;
    private Button enterBtn;

    public SettingsPasswordDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_password_dialog);
        editText = (EditText) findViewById(R.id.settings_pw_editTxt);
        cancelBtn = (Button) findViewById(R.id.settings_pw_cancel_btn);
        enterBtn = (Button) findViewById(R.id.settings_pw_enter_btn);
        setButtonListeners();
        setTitle("Enter Password");
    }

    private void setButtonListeners(){
        if(cancelBtn == null || enterBtn == null){dismiss(); return;}

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText != null && listener != null){
                    String pw = editText.getText().toString().toLowerCase();
                    listener.enterPassword(pw);
                    dismiss();
                }else{
                    dismiss();
                }
            }
        });

    }

    public void setListener(SettingsPasswordListener listener) {
        this.listener = listener;
    }

}
