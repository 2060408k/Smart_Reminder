package highway62.reminderapp.scheduleadapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

import highway62.reminderapp.R;
import highway62.reminderapp.ReminderActivity;
import highway62.reminderapp.adminSettings.SettingsInterface;
import highway62.reminderapp.constants.Day;
import highway62.reminderapp.constants.DurationScale;
import highway62.reminderapp.constants.NotificationScale;
import highway62.reminderapp.fragmentHandlers.OnScheduleFragmentListener;
import highway62.reminderapp.fragments.ScheduleViewDateFragment;
import highway62.reminderapp.reminderhandlers.ReminderHandler;
import highway62.reminderapp.reminders.BaseReminder;

/**
 * Created by Highway62 on 01/09/2016.
 */
public class ScheduleAdapter extends BaseAdapter {

    private ReminderActivity context;
    private ArrayList<BaseReminder> reminders;
    private AlertDialog.Builder dialogBuilder;
    private LayoutInflater inflater = null;
    private SettingsInterface settings;
    private OnScheduleFragmentListener handler;

    public ScheduleAdapter(ReminderActivity context, OnScheduleFragmentListener handler
            , ArrayList<BaseReminder> reminders){
        this.context = context;
        this.reminders = reminders;
        this.settings = context.getSettingsSingleton();
        this.handler = handler;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dialogBuilder = new AlertDialog.Builder(context);
    }

    @Override
    public int getCount() {
        return reminders.size();
    }

    @Override
    public Object getItem(int i) {
        return reminders.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final ScheduleViewDateFragment.ViewHolder viewHolder;
        View v = convertView;
        if(v == null){
            v = inflater.inflate(R.layout.schedule_reminder_row, null);
            viewHolder = new ScheduleViewDateFragment.ViewHolder();
            viewHolder.scheduleRowTitle = (TextView) v.findViewById(R.id.scheduleRowTitle);
            viewHolder.scheduleRowEdit = (TextView) v.findViewById(R.id.scheduleRowEdit);
            viewHolder.scheduleRowDelete = (TextView) v.findViewById(R.id.scheduleRowDelete);
            viewHolder.scheduleRowName = (TextView) v.findViewById(R.id.scheduleRowName);
            viewHolder.scheduleRowLocationCont = (RelativeLayout) v.findViewById(R.id.scheduleRowLocationCont);
            viewHolder.scheduleRowLocation = (TextView) v.findViewById(R.id.scheduleRowLocation);
            viewHolder.scheduleRowEventAfterCont = (RelativeLayout) v.findViewById(R.id.scheduleRowEventAfterCont);
            viewHolder.scheduleRowEventAfter = (TextView) v.findViewById(R.id.scheduleRowEventAfter);
            viewHolder.scheduleRowDate = (TextView) v.findViewById(R.id.scheduleRowDate);
            viewHolder.scheduleRowTime = (TextView) v.findViewById(R.id.scheduleRowTime);
            viewHolder.scheduleRowDurationCont = (RelativeLayout) v.findViewById(R.id.scheduleRowDurationCont);
            viewHolder.scheduleRowDuration = (TextView) v.findViewById(R.id.scheduleRowDuration);
            viewHolder.scheduleRowNotificationCont = (RelativeLayout) v.findViewById(R.id.scheduleRowNotificationCont);
            viewHolder.scheduleRowNotification = (TextView) v.findViewById(R.id.scheduleRowNotification);
            viewHolder.scheduleRowNotesCont = (RelativeLayout) v.findViewById(R.id.scheduleRowNotesCont);
            viewHolder.scheduleRowNotes = (TextView) v.findViewById(R.id.scheduleRowNotes);
            viewHolder.scheduleRowRepeatCont = (RelativeLayout) v.findViewById(R.id.scheduleRowRepeatCont);
            viewHolder.scheduleRowRepeat = (TextView) v.findViewById(R.id.scheduleRowRepeat);
            viewHolder.scheduleRowRepeatWeeksCont = (RelativeLayout) v.findViewById(R.id.scheduleRowRepeatWeeksCont);
            viewHolder.scheduleRowRepeatWeeks = (TextView) v.findViewById(R.id.scheduleRowRepeatWeeks);
            viewHolder.scheduleRowPromptLevelCont = (RelativeLayout) v.findViewById(R.id.scheduleRowPromptLevelCont);
            viewHolder.scheduleRowPromptLevel = (TextView) v.findViewById(R.id.scheduleRowPromptLevel);
            v.setTag(viewHolder);
        }else{
            viewHolder = (ScheduleViewDateFragment.ViewHolder) v.getTag();
        }

        BaseReminder reminder = reminders.get(position);

        // Date Time
        // Format the date and time
        final DateTime dt = new DateTime(reminder.getDateTime());
        DateTime dtNow = new DateTime();
        DateTimeFormatter dtfDate = DateTimeFormat.forPattern("dd/MM/yyyy");
        DateTimeFormatter dtfTime = DateTimeFormat.forPattern("HH:mm");
        String date = dtfDate.print(dt);
        String time = dtfTime.print(dt);

        viewHolder.scheduleRowDate.setText("Date: " +  date);
        viewHolder.scheduleRowTime.setText("Time: " + time);

        switch (reminder.getType()){
            case GEN:
                setupGeneric(viewHolder, reminder);
                break;
            case APPT:
                setupAppt(viewHolder, reminder);
                break;
            case SHOPPING:
                setupShopping(viewHolder, reminder);
                break;
            case BIRTH:
                setupBirthday(viewHolder, reminder);
                break;
            case MEDIC:
                setupMedication(viewHolder, reminder);
                break;
            case DAILY:
                setupDaily(viewHolder, reminder);
                break;
            case SOCIAL:
                setupSocial(viewHolder, reminder);
                break;
        }

        if(!settings.getUserSubtletyEnabled()){
            viewHolder.scheduleRowPromptLevelCont.setVisibility(View.GONE);
        }else{
            viewHolder.scheduleRowPromptLevel.setText("Notification Subtlety Level: " + reminder.getPromptLevel());
        }

        // Hide edit and delete buttons if they have already passed
        DateTimeFormatter dtfTimeLog = DateTimeFormat.forPattern("HH:mm:ss");
        Log.e("TIME", "reminder date: " + dtfDate.print(dt) + " time: " + dtfTimeLog.print(dt));
        Log.e("TIME", "now date: " + dtfDate.print(dtNow) + " time: " + dtfTimeLog.print(dtNow));
        if(dt.isBeforeNow()){
            Log.e("TIME", "dt is before now, hiding the edit and delete buttons");
            viewHolder.scheduleRowEdit.setVisibility(View.INVISIBLE);
            viewHolder.scheduleRowDelete.setVisibility(View.INVISIBLE);
        }else{
            viewHolder.scheduleRowEdit.setVisibility(View.VISIBLE);
            viewHolder.scheduleRowDelete.setVisibility(View.VISIBLE);

            viewHolder.scheduleRowEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showEditDialog(position, dt);
                }
            });

            viewHolder.scheduleRowDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDeleteDialog(position, dt);
                }
            });
        }

        return v;
    }

    // Setup Views -------------------------------------------------------------------------------->
    private void setupGeneric(ScheduleViewDateFragment.ViewHolder viewHolder, BaseReminder reminder){

        //Title
        viewHolder.scheduleRowTitle.setText("EVENT");


        // Name
        if(reminder.getTitle() != null && !TextUtils.isEmpty(reminder.getTitle())){
            viewHolder.scheduleRowName.setText("Event Name: " + reminder.getTitle());
        }else{
            viewHolder.scheduleRowName.setVisibility(View.GONE);
        }

        //Event After
        viewHolder.scheduleRowEventAfterCont.setVisibility(View.GONE);

        //Location
        viewHolder.scheduleRowLocationCont.setVisibility(View.GONE);

        //Notification
        if(reminder.getNotificationScale() != null){
            viewHolder.scheduleRowNotification.setText("Notification: "
                    + getNotificationString(reminder.getNotificationTime()
                    ,reminder.getNotificationScale()));
        }else{
            viewHolder.scheduleRowNotificationCont.setVisibility(View.GONE);
        }

        //Duration
        if(reminder.isDurationSet()){
            viewHolder.scheduleRowDuration.setText("Event Duration: "
                    + getDurationString(reminder.getEventDurationTime()
                    , reminder.getEventDurationScale()));
        }else{
            viewHolder.scheduleRowDurationCont.setVisibility(View.GONE);
        }

        // Notes
        if(reminder.getNotes() != null && !TextUtils.isEmpty(reminder.getNotes())){
            viewHolder.scheduleRowNotes.setText("Event Notes: " + reminder.getNotes());
        }else{
            viewHolder.scheduleRowNotesCont.setVisibility(View.GONE);
        }

        // Repeating
        if(reminder.isRepeating()){
            Day[] rDays = reminder.getRepetitionDays();
            if(rDays != null){
                StringBuilder sb = new StringBuilder();
                sb.append("Repeating Days: ");
                for(int i = 0; i < rDays.length; i++){
                    sb.append(rDays[i].name());
                    if(i != (rDays.length - 1)){
                        sb.append(", ");
                    }
                }
                viewHolder.scheduleRowRepeat.setText(sb.toString());

                // Weeks
                if(reminder.getRepetitionWeeks() > 0){
                    viewHolder.scheduleRowRepeatWeeks.setText("Repeating For: +"
                            + reminder.getRepetitionWeeks() + "Weeks");
                }else{
                    viewHolder.scheduleRowRepeatWeeksCont.setVisibility(View.GONE);
                }
            }else{
                viewHolder.scheduleRowRepeatCont.setVisibility(View.GONE);
                viewHolder.scheduleRowRepeatWeeksCont.setVisibility(View.GONE);
            }
        }else{
            viewHolder.scheduleRowRepeatCont.setVisibility(View.GONE);
            viewHolder.scheduleRowRepeatWeeksCont.setVisibility(View.GONE);
        }
    }

    private void setupAppt(ScheduleViewDateFragment.ViewHolder viewHolder, BaseReminder reminder){

        //Title
        viewHolder.scheduleRowTitle.setText("APPOINTMENT");

        // Name
        if(reminder.getTitle() != null && !TextUtils.isEmpty(reminder.getTitle())){
            viewHolder.scheduleRowName.setText("Appointment Name: " + reminder.getTitle());
        }else{
            viewHolder.scheduleRowName.setVisibility(View.GONE);
        }

        //Event After
        viewHolder.scheduleRowEventAfterCont.setVisibility(View.GONE);

        //Location
        viewHolder.scheduleRowLocationCont.setVisibility(View.GONE);

        //Notification
        if(reminder.getNotificationScale() != null){
            viewHolder.scheduleRowNotification.setText("Notification: "
                    + getNotificationString(reminder.getNotificationTime()
                    ,reminder.getNotificationScale()));
        }else{
            viewHolder.scheduleRowNotificationCont.setVisibility(View.GONE);
        }

        //Duration
        if(reminder.isDurationSet()){
            viewHolder.scheduleRowDuration.setText("Appointment Duration: "
                    + getDurationString(reminder.getEventDurationTime()
                    , reminder.getEventDurationScale()));
        }else{
            viewHolder.scheduleRowDurationCont.setVisibility(View.GONE);
        }

        // Notes
        if(reminder.getNotes() != null && !TextUtils.isEmpty(reminder.getNotes())){
            viewHolder.scheduleRowNotes.setText("Appointment Notes: " + reminder.getNotes());
        }else{
            viewHolder.scheduleRowNotesCont.setVisibility(View.GONE);
        }

        // Repeating
        if(reminder.isRepeating()){
            Day[] rDays = reminder.getRepetitionDays();
            if(rDays != null){
                StringBuilder sb = new StringBuilder();
                sb.append("Repeating Days: ");
                for(int i = 0; i < rDays.length; i++){
                    sb.append(rDays[i].name());
                    if(i != (rDays.length - 1)){
                        sb.append(", ");
                    }
                }
                viewHolder.scheduleRowRepeat.setText(sb.toString());

                // Weeks
                if(reminder.getRepetitionWeeks() > 0){
                    viewHolder.scheduleRowRepeatWeeks.setText("Repeating For: +"
                            + reminder.getRepetitionWeeks() + "Weeks");
                }else{
                    viewHolder.scheduleRowRepeatWeeksCont.setVisibility(View.GONE);
                }
            }else{
                viewHolder.scheduleRowRepeatCont.setVisibility(View.GONE);
                viewHolder.scheduleRowRepeatWeeksCont.setVisibility(View.GONE);
            }
        }else{
            viewHolder.scheduleRowRepeatCont.setVisibility(View.GONE);
            viewHolder.scheduleRowRepeatWeeksCont.setVisibility(View.GONE);
        }
    }

    private void setupShopping(ScheduleViewDateFragment.ViewHolder viewHolder, BaseReminder reminder){

        //Title
        viewHolder.scheduleRowTitle.setText("SHOPPING");

        // Name
        if(reminder.getTitle() != null && !TextUtils.isEmpty(reminder.getTitle())){
            viewHolder.scheduleRowName.setText("Shopping Location: " + reminder.getTitle());
        }else{
            viewHolder.scheduleRowName.setVisibility(View.GONE);
        }

        //Event After
        viewHolder.scheduleRowEventAfterCont.setVisibility(View.GONE);

        //Location
        viewHolder.scheduleRowLocationCont.setVisibility(View.GONE);

        //Notification
        if(reminder.getNotificationScale() != null){
            viewHolder.scheduleRowNotification.setText("Notification: "
                    + getNotificationString(reminder.getNotificationTime()
                    ,reminder.getNotificationScale()));
        }else{
            viewHolder.scheduleRowNotificationCont.setVisibility(View.GONE);
        }

        //Duration
        viewHolder.scheduleRowDurationCont.setVisibility(View.GONE);

        // Notes
        if(reminder.getNotes() != null && !TextUtils.isEmpty(reminder.getNotes())){
            viewHolder.scheduleRowNotes.setText("Shopping List: " + reminder.getNotes());
        }else{
            viewHolder.scheduleRowNotesCont.setVisibility(View.GONE);
        }

        // Repeating
        if(reminder.isRepeating()){
            Day[] rDays = reminder.getRepetitionDays();
            if(rDays != null){
                StringBuilder sb = new StringBuilder();
                sb.append("Repeating Days: ");
                for(int i = 0; i < rDays.length; i++){
                    sb.append(rDays[i].name());
                    if(i != (rDays.length - 1)){
                        sb.append(", ");
                    }
                }

                viewHolder.scheduleRowRepeat.setText(sb.toString());

            }else{
                viewHolder.scheduleRowRepeatCont.setVisibility(View.GONE);
                viewHolder.scheduleRowRepeatWeeksCont.setVisibility(View.GONE);
            }
        }else{
            viewHolder.scheduleRowRepeatCont.setVisibility(View.GONE);
            viewHolder.scheduleRowRepeatWeeksCont.setVisibility(View.GONE);
        }

    }

    private void setupBirthday(ScheduleViewDateFragment.ViewHolder viewHolder, BaseReminder reminder){

        //Title
        viewHolder.scheduleRowTitle.setText("BIRTHDAY");

        // Name
        if(reminder.getTitle() != null && !TextUtils.isEmpty(reminder.getTitle())){
            viewHolder.scheduleRowName.setText("Birthday Name: " + reminder.getTitle());
        }else{
            viewHolder.scheduleRowName.setVisibility(View.GONE);
        }

        //Event After
        viewHolder.scheduleRowEventAfterCont.setVisibility(View.GONE);

        //Location
        viewHolder.scheduleRowLocationCont.setVisibility(View.GONE);

        //Notification
        viewHolder.scheduleRowNotificationCont.setVisibility(View.GONE);

        //Duration
        viewHolder.scheduleRowDurationCont.setVisibility(View.GONE);

        // Repeating
        viewHolder.scheduleRowRepeatCont.setVisibility(View.GONE);
        viewHolder.scheduleRowRepeatWeeksCont.setVisibility(View.GONE);

        // Notes
        if(reminder.getNotes() != null && !TextUtils.isEmpty(reminder.getNotes())){
            viewHolder.scheduleRowNotes.setText("Presents: " + reminder.getNotes());
        }else{
            viewHolder.scheduleRowNotesCont.setVisibility(View.GONE);
        }
    }

    private void setupMedication(ScheduleViewDateFragment.ViewHolder viewHolder, BaseReminder reminder){

        //Title
        viewHolder.scheduleRowTitle.setText("MEDICATION");

        // Name
        if(reminder.getTitle() != null && !TextUtils.isEmpty(reminder.getTitle())){
            viewHolder.scheduleRowName.setText("Type of Medication: " + reminder.getTitle());
        }else{
            viewHolder.scheduleRowName.setVisibility(View.GONE);
        }

        //Location
        viewHolder.scheduleRowLocationCont.setVisibility(View.GONE);

        //Event after
        if(reminder.getEventAfter() != null && !TextUtils.isEmpty(reminder.getEventAfter())){
            viewHolder.scheduleRowEventAfter.setText("Take After: " + reminder.getEventAfter());
        }else{
            viewHolder.scheduleRowEventAfterCont.setVisibility(View.GONE);
        }

        //Notification
        if(reminder.getNotificationScale() != null){
            viewHolder.scheduleRowNotification.setText("Notification: "
                    + getNotificationString(reminder.getNotificationTime()
                    ,reminder.getNotificationScale()));
        }else{
            viewHolder.scheduleRowNotificationCont.setVisibility(View.GONE);
        }

        //Duration
        viewHolder.scheduleRowDurationCont.setVisibility(View.GONE);

        // Notes
        viewHolder.scheduleRowNotesCont.setVisibility(View.GONE);

        // Repeating
        if(reminder.isRepeating()){
            Day[] rDays = reminder.getRepetitionDays();
            if(rDays != null){
                StringBuilder sb = new StringBuilder();
                sb.append("Repeating Days: ");
                for(int i = 0; i < rDays.length; i++){
                    sb.append(rDays[i].name());
                    if(i != (rDays.length - 1)){
                        sb.append(", ");
                    }
                }
                viewHolder.scheduleRowRepeat.setText(sb.toString());

                // Weeks
                if(reminder.getRepetitionWeeks() > 0){
                    viewHolder.scheduleRowRepeatWeeks.setText("Repeating For: +"
                            + reminder.getRepetitionWeeks() + "Weeks");
                }else{
                    viewHolder.scheduleRowRepeatWeeksCont.setVisibility(View.GONE);
                }
            }else{
                viewHolder.scheduleRowRepeatCont.setVisibility(View.GONE);
                viewHolder.scheduleRowRepeatWeeksCont.setVisibility(View.GONE);
            }
        }else{
            viewHolder.scheduleRowRepeatCont.setVisibility(View.GONE);
            viewHolder.scheduleRowRepeatWeeksCont.setVisibility(View.GONE);
        }
    }

    private void setupDaily(ScheduleViewDateFragment.ViewHolder viewHolder, BaseReminder reminder){

        //Title
        viewHolder.scheduleRowTitle.setText("DAILY TASK");

        // Name
        if(reminder.getTitle() != null && !TextUtils.isEmpty(reminder.getTitle())){
            viewHolder.scheduleRowName.setText("Task Name: " + reminder.getTitle());
        }else{
            viewHolder.scheduleRowName.setVisibility(View.GONE);
        }

        //Event After
        viewHolder.scheduleRowEventAfterCont.setVisibility(View.GONE);

        //Location
        viewHolder.scheduleRowLocationCont.setVisibility(View.GONE);

        // Notes
        if(reminder.getNotes() != null && !TextUtils.isEmpty(reminder.getNotes())){
            viewHolder.scheduleRowNotes.setText("Task Notes: " + reminder.getNotes());
        }else{
            viewHolder.scheduleRowNotesCont.setVisibility(View.GONE);
        }

        //Notification
        viewHolder.scheduleRowNotificationCont.setVisibility(View.GONE);

        //Duration
        viewHolder.scheduleRowDurationCont.setVisibility(View.GONE);

        // Repeating
        if(reminder.isRepeating()){
            Day[] rDays = reminder.getRepetitionDays();
            if(rDays != null){
                StringBuilder sb = new StringBuilder();
                sb.append("Repeating Days: ");
                for(int i = 0; i < rDays.length; i++){
                    sb.append(rDays[i].name());
                    if(i != (rDays.length - 1)){
                        sb.append(", ");
                    }
                }
                viewHolder.scheduleRowRepeat.setText(sb.toString());

                // Weeks
                if(reminder.getRepetitionWeeks() > 0){
                    viewHolder.scheduleRowRepeatWeeks.setText("Repeating For: +"
                            + reminder.getRepetitionWeeks() + "Weeks");
                }else{
                    viewHolder.scheduleRowRepeatWeeksCont.setVisibility(View.GONE);
                }
            }else{
                viewHolder.scheduleRowRepeatCont.setVisibility(View.GONE);
                viewHolder.scheduleRowRepeatWeeksCont.setVisibility(View.GONE);
            }
        }else{
            viewHolder.scheduleRowRepeatCont.setVisibility(View.GONE);
            viewHolder.scheduleRowRepeatWeeksCont.setVisibility(View.GONE);
        }
    }

    private void setupSocial(ScheduleViewDateFragment.ViewHolder viewHolder, BaseReminder reminder){

        //Title
        viewHolder.scheduleRowTitle.setText("SOCIAL EVENT");

        // Name
        if(reminder.getTitle() != null && !TextUtils.isEmpty(reminder.getTitle())){
            viewHolder.scheduleRowName.setText("Event Name: " + reminder.getTitle());
        }else{
            viewHolder.scheduleRowName.setVisibility(View.GONE);
        }

        //Event After
        viewHolder.scheduleRowEventAfterCont.setVisibility(View.GONE);

        //Location
        if(reminder.getLocation() != null && !TextUtils.isEmpty(reminder.getLocation())){
            viewHolder.scheduleRowLocation.setText("Event Location: " + reminder.getLocation());
        }else{
            viewHolder.scheduleRowLocationCont.setVisibility(View.GONE);
        }

        //Notification
        viewHolder.scheduleRowNotificationCont.setVisibility(View.GONE);

        //Duration
        viewHolder.scheduleRowDurationCont.setVisibility(View.GONE);

        // Notes
        viewHolder.scheduleRowNotesCont.setVisibility(View.GONE);

        // Repeating
        viewHolder.scheduleRowRepeatCont.setVisibility(View.GONE);
        viewHolder.scheduleRowRepeatWeeksCont.setVisibility(View.GONE);

    }
    //--------------------------------------------------------------------------------------------//

    private String getNotificationString(int time, NotificationScale scale){
        if(scale.equals(NotificationScale.SAMETIME)){
            return "At Time of Event";
        }

        if(time != -1) {
            return time + " " + scale.name().toLowerCase() + " before";
        }else{
            return "At Time of Event";
        }
    }

    private String getDurationString(int time, DurationScale scale){
        return time + " " + scale.name().toLowerCase();
    }

    private void showEditDialog(final int position, DateTime dt){

        AlertDialog editDialog;

        dialogBuilder.setTitle(context.getResources().getString(R.string.schedule_edit_dialog_title));
        dialogBuilder.setCancelable(true);

        dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.editReminder(reminders.get(position));
                dialog.cancel();
            }
        });

        dialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        editDialog = dialogBuilder.create();
        if(dt.isAfterNow()) {
            editDialog.show();
        }
    }

    private void showDeleteDialog(final int position, DateTime dt){

        AlertDialog deleteDialog;

        dialogBuilder.setTitle(context.getResources().getString(R.string.pref_UP_view_prompt_delete_dialog_title));
        dialogBuilder.setCancelable(true);

        dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem(position);
                dialog.cancel();
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        deleteDialog = dialogBuilder.create();
        if(dt.isAfterNow()) {
            deleteDialog.show();
        }
    }

    private void deleteItem(int position){
        ReminderHandler.manuallyDeleteReminder(context, reminders.get(position));
        reminders.remove(position);
        notifyDataSetChanged();
    }
}
