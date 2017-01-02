package highway62.reminderapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import highway62.reminderapp.R;
import highway62.reminderapp.ViewRemindersActivity;
import highway62.reminderapp.reminderhandlers.ReminderHandler;
import highway62.reminderapp.reminders.AudioReminder;

/**
 * Created by Highway62 on 06/10/2016.
 */
public class ViewReminderAdapter extends BaseAdapter {

    private ArrayList<AudioReminder> reminders;
    private ViewRemindersActivity context;
    private LayoutInflater inflater;
    private boolean audioPlaying = false;
    ViewRemindersActivity.ViewRemindViewHolder viewHolder;


    public ViewReminderAdapter(ViewRemindersActivity context, ArrayList<AudioReminder> reminders){
        this.context = context;
        this.reminders = reminders;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    public View getView(final int i, View convertView, ViewGroup viewGroup) {

        View v = convertView;
        if(v == null){
            v = inflater.inflate(R.layout.activity_view_reminder_adapter_row, null);
            viewHolder = new ViewRemindersActivity.ViewRemindViewHolder();
            viewHolder.deleteBtn = (ImageView) v.findViewById(R.id.viewReminderDelete);
            viewHolder.playBtn = (ImageView) v.findViewById(R.id.viewReminderPlay);
            viewHolder.reminderTimeText = (TextView) v.findViewById(R.id.viewReminderText);
            v.setTag(viewHolder);
        } else{
            viewHolder = (ViewRemindersActivity.ViewRemindViewHolder) v.getTag();
        }

        final AudioReminder reminder = reminders.get(i);

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yy");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(reminder.getDateTime());
        StringBuilder sb = new StringBuilder();
        sb.append(dateFormatter.format(calendar.getTime()));
        sb.append(" ");
        sb.append(timeFormatter.format(calendar.getTime()));
        viewHolder.reminderTimeText.setText(sb.toString());

        viewHolder.playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] audio = reminder.getAudio();
                if(audio != null && audio.length > 1){
                    playAudio(audio, viewHolder);
                }
            }
        });

        viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteReminder(i, viewHolder);
            }
        });

        return v;
    }

    private void playAudio(byte[] audio, ViewRemindersActivity.ViewRemindViewHolder viewHolder){
        if(!context.audioIsPlaying()){
            context.playAudio(audio);
            Toast.makeText(context, "Playing",Toast.LENGTH_SHORT).show();
            audioPlaying = true;
        }else{
            stopAudio(viewHolder);
        }
    }

    private void stopAudio(ViewRemindersActivity.ViewRemindViewHolder viewHolder){
        context.stopAudio();
        audioPlaying = false;
    }

    private void deleteReminder(final int position, ViewRemindersActivity.ViewRemindViewHolder viewHolder){
        stopAudio(viewHolder);
        new AlertDialog.Builder(context)
                .setMessage("Delete Reminder?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        ReminderHandler.deleteReminder(context, reminders.get(position).getId());
                        reminders.remove(position);
                        notifyDataSetChanged();

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
