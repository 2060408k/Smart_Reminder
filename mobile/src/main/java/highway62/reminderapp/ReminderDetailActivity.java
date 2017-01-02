package highway62.reminderapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import highway62.reminderapp.constants.Consts;
import highway62.reminderapp.constants.ReminderType;
import highway62.reminderapp.reminderhandlers.ReminderReceiver;
import highway62.reminderapp.reminders.BaseReminder;

public class ReminderDetailActivity extends AppCompatActivity {

    private BaseReminder reminder;

    TextView reminderTypeTxt;
    TextView reminderNameTxt;
    RelativeLayout reminderLocationCont;
    TextView reminderLocationTxt;
    RelativeLayout reminderEventAfterCont;
    TextView reminderEventAfterTxt;
    TextView reminderDateTxt;
    TextView reminderTimeTxt;
    RelativeLayout reminderDurationCont;
    TextView reminderDurationTxt;
    RelativeLayout reminderNotesCont;
    TextView reminderNotesTxt;
    Button okBtn;

    DateTimeFormatter timeForm;
    DateTimeFormatter dateForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_detail);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        if(ab != null){
            ab.hide();
        }

        // Cancel the vibration and sound (when opened from a notification)
        Intent cancelService = new Intent(this, ReminderReceiver.NotificationActionService.class);
        cancelService.setAction(Consts.CANCEL_VIB_SOUND_ACTION);
        startService(cancelService);

        reminderTypeTxt = (TextView) findViewById(R.id.reminderTypeTxt);
        reminderNameTxt = (TextView) findViewById(R.id.reminderNameTxt);
        reminderLocationCont = (RelativeLayout) findViewById(R.id.reminderLocationTxtCont);
        reminderLocationTxt = (TextView) findViewById(R.id.reminderLocationTxt);
        reminderEventAfterCont = (RelativeLayout) findViewById(R.id.reminderEventAfterCont);
        reminderEventAfterTxt = (TextView) findViewById(R.id.reminderEventAfter);
        reminderDateTxt = (TextView) findViewById(R.id.reminderDateTxt);
        reminderTimeTxt = (TextView) findViewById(R.id.reminderTimeTxt);
        reminderDurationCont = (RelativeLayout) findViewById(R.id.reminderDurationCont);
        reminderDurationTxt = (TextView) findViewById(R.id.reminderDurationTxt);
        reminderNotesCont = (RelativeLayout) findViewById(R.id.reminderNotesCont);
        reminderNotesTxt = (TextView) findViewById(R.id.reminderNotesTxt);
        okBtn = (Button) findViewById(R.id.reminderOKBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        timeForm = DateTimeFormat.forPattern("HH:mm");
        dateForm = DateTimeFormat.forPattern("dd/MM/yyyy");

        Intent intent = getIntent();
        if(intent != null && intent.getParcelableExtra(Consts.REMINDER_INTENT) != null){
            reminder = intent.getParcelableExtra(Consts.REMINDER_INTENT);
            if(reminder.getReminderType() != ReminderType.PROMPT){
                switch (reminder.getType()){
                    case GEN:
                        setupGeneric();
                        break;
                    case APPT:
                        setupAppointment();
                        break;
                    case SHOPPING:
                        setupShopping();
                        break;
                    case BIRTH:
                        setupBirthday();
                        break;
                    case MEDIC:
                        setupMedication();
                        break;
                    case DAILY:
                        setupDaily();
                        break;
                    case SOCIAL:
                        setupSocial();
                        break;
                    default:
                        setupGeneric();
                        break;
                }
            }
        }

    }

    private void setupGeneric(){
        reminderTypeTxt.setText("EVENT");
        if(reminder.getTitle() != null && !TextUtils.isEmpty(reminder.getTitle())){
            reminderNameTxt.setText("Event Name: " + reminder.getTitle());
        }else{
            reminderNameTxt.setVisibility(View.GONE);
        }

        reminderLocationCont.setVisibility(View.GONE);
        reminderEventAfterCont.setVisibility(View.GONE);

        DateTime dt = new DateTime(reminder.getDateTime());
        reminderDateTxt.setText("Event Date: " + dateForm.print(dt));
        reminderTimeTxt.setText("Event Time: " + timeForm.print(dt));

        if(reminder.isDurationSet()){
            reminderDurationTxt.setText("Event Duration: "
                    + reminder.getEventDurationTime()
                    + " "
                    + reminder.getEventDurationScale().name());
        }else{
            reminderDurationCont.setVisibility(View.GONE);
        }

        if(reminder.getNotes() != null && !TextUtils.isEmpty(reminder.getNotes())){
            reminderNotesTxt.setText("Event Notes: " + reminder.getNotes());
        }else{
            reminderNotesCont.setVisibility(View.GONE);
        }
    }

    private void setupAppointment(){

        reminderTypeTxt.setText("APPOINTMENT");
        if(reminder.getTitle() != null && !TextUtils.isEmpty(reminder.getTitle())){
            reminderNameTxt.setText("Appointment Name: " + reminder.getTitle());
        }else{
            reminderNameTxt.setVisibility(View.GONE);
        }

        reminderLocationCont.setVisibility(View.GONE);
        reminderEventAfterCont.setVisibility(View.GONE);

        DateTime dt = new DateTime(reminder.getDateTime());
        reminderDateTxt.setText("Date: " + dateForm.print(dt));
        reminderTimeTxt.setText("Time: " + timeForm.print(dt));

        if(reminder.isDurationSet()){
            reminderDurationTxt.setText("Appointment Duration: "
                    + reminder.getEventDurationTime()
                    + " "
                    + reminder.getEventDurationScale().name());
        }else{
            reminderDurationCont.setVisibility(View.GONE);
        }

        if(reminder.getNotes() != null && !TextUtils.isEmpty(reminder.getNotes())){
            reminderNotesTxt.setText("Appointment Notes: " + reminder.getNotes());
        }else{
            reminderNotesCont.setVisibility(View.GONE);
        }
    }

    private void setupShopping(){
        reminderTypeTxt.setText("SHOPPING TRIP");
        if(reminder.getTitle() != null && !TextUtils.isEmpty(reminder.getTitle())){
            reminderNameTxt.setText("Location: " + reminder.getTitle());
        }else{
            reminderNameTxt.setVisibility(View.GONE);
        }

        reminderLocationCont.setVisibility(View.GONE);
        reminderEventAfterCont.setVisibility(View.GONE);

        DateTime dt = new DateTime(reminder.getDateTime());
        reminderDateTxt.setText("Date: " + dateForm.print(dt));
        reminderTimeTxt.setText("Time: " + timeForm.print(dt));

        reminderDurationCont.setVisibility(View.GONE);

        if(reminder.getNotes() != null && !TextUtils.isEmpty(reminder.getNotes())){
            reminderNotesTxt.setText("Shopping List: " + reminder.getNotes());
        }else{
            reminderNotesCont.setVisibility(View.GONE);
        }
    }

    private void setupBirthday(){
        reminderTypeTxt.setText("BIRTHDAY");
        if(reminder.getTitle() != null && !TextUtils.isEmpty(reminder.getTitle())){
            reminderNameTxt.setText("Name: " + reminder.getTitle());
        }else{
            reminderNameTxt.setVisibility(View.GONE);
        }

        reminderLocationCont.setVisibility(View.GONE);
        reminderEventAfterCont.setVisibility(View.GONE);

        DateTime dt = new DateTime(reminder.getDateTime());
        reminderDateTxt.setText("Date: " + dateForm.print(dt));
        reminderTimeTxt.setText("Time: " + timeForm.print(dt));

        reminderDurationCont.setVisibility(View.GONE);

        if(reminder.getNotes() != null && !TextUtils.isEmpty(reminder.getNotes())){
            reminderNotesTxt.setText("Birthday Presents: " + reminder.getNotes());
        }else{
            reminderNotesCont.setVisibility(View.GONE);
        }
    }

    private void setupMedication(){
        reminderTypeTxt.setText("MEDICATION");
        if(reminder.getTitle() != null && !TextUtils.isEmpty(reminder.getTitle())){
            reminderNameTxt.setText("Type of Medication: " + reminder.getTitle());
        }else{
            reminderNameTxt.setVisibility(View.GONE);
        }

        if(reminder.getEventAfter() != null && !TextUtils.isEmpty(reminder.getEventAfter())){
            reminderEventAfterTxt.setText("Take Medication After: " + reminder.getEventAfter());
        }

        reminderLocationCont.setVisibility(View.GONE);

        DateTime dt = new DateTime(reminder.getDateTime());
        reminderDateTxt.setText("Take Medication on: " + dateForm.print(dt));
        reminderTimeTxt.setText("At Time: " + timeForm.print(dt));

        reminderDurationCont.setVisibility(View.GONE);

        reminderNotesCont.setVisibility(View.GONE);
    }

    private void setupDaily(){
        reminderTypeTxt.setText("DAILY TASK");
        if(reminder.getTitle() != null && !TextUtils.isEmpty(reminder.getTitle())){
            reminderNameTxt.setText("Task Name: " + reminder.getTitle());
        }else{
            reminderNameTxt.setVisibility(View.GONE);
        }

        reminderLocationCont.setVisibility(View.GONE);
        reminderEventAfterCont.setVisibility(View.GONE);

        DateTime dt = new DateTime(reminder.getDateTime());
        reminderDateTxt.setText("Date: " + dateForm.print(dt));
        reminderTimeTxt.setText("Time: " + timeForm.print(dt));

        reminderDurationCont.setVisibility(View.GONE);

        if(reminder.getNotes() != null && !TextUtils.isEmpty(reminder.getNotes())){
            reminderNotesTxt.setText("Task Notes: " + reminder.getNotes());
        }else{
            reminderNotesCont.setVisibility(View.GONE);
        }
    }

    private void setupSocial(){
        reminderTypeTxt.setText("SOCIAL EVENT");
        if(reminder.getTitle() != null && !TextUtils.isEmpty(reminder.getTitle())){
            reminderNameTxt.setText("Event Name: " + reminder.getTitle());
        }else{
            reminderNameTxt.setVisibility(View.GONE);
        }

        if(reminder.getLocation() != null && !TextUtils.isEmpty(reminder.getLocation())){
            reminderLocationTxt.setText("Event Location: " + reminder.getLocation());
        }

        reminderEventAfterCont.setVisibility(View.GONE);

        DateTime dt = new DateTime(reminder.getDateTime());
        reminderDateTxt.setText("Date: " + dateForm.print(dt));
        reminderTimeTxt.setText("Time: " + timeForm.print(dt));

        reminderDurationCont.setVisibility(View.GONE);

        reminderNotesCont.setVisibility(View.GONE);
    }
}
