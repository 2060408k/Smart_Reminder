package highway62.reminderapp.reminderhandlers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;

import highway62.reminderapp.WearAudioNotificationActivity;
import highway62.reminderapp.WearPromptNotification;
import highway62.reminderapp.WearReminderNotificationActivity;
import highway62.reminderapp.constants.Consts;
import highway62.reminderapp.constants.ReminderType;
import highway62.reminderapp.reminders.BaseReminder;

/**
 * Receives intents sent by the alarm manager after a reminder has been triggered
 */
public class ReminderReceiver extends BroadcastReceiver {

    private Context context;
    private byte[] audio;
    private long reminderID = -1;
    private Vibrator vibrator;
    private BaseReminder reminder;
    private boolean audioReminder = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        this.vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if(intent.hasExtra(Consts.REMINDER_ID_INTENT)){
            //this.audio = intent.getByteArrayExtra(Consts.AUDIO_INTENT);
            reminderID = intent.getLongExtra(Consts.REMINDER_ID_INTENT, -1);
            audioReminder = true;
        }else if(intent.hasExtra(Consts.REMINDER_INTENT)){
            Log.e("REMIND", "Reminder received by the ReminderReceiver");
            this.reminder = intent.getParcelableExtra(Consts.REMINDER_INTENT);
            audioReminder = false;
        }else {
            Log.e("REMIND", "No extra in reminderReceiver intent");
        }

        displayReminder();
    }

    private void displayReminder() {

        if(reminderID == -1) {
            notifyReminder();
        }else{
            if(ReminderHandler.getAudio(context, reminderID) != null && ReminderHandler.getAudio(context, reminderID).length > 1){
                notifyReminder();
            }
        }

        if(audioReminder) {
            if(ReminderHandler.getAudio(context, reminderID) != null && ReminderHandler.getAudio(context, reminderID).length > 1){
                Intent intent = new Intent(context, WearAudioNotificationActivity.class);
                intent.putExtra(Consts.REMINDER_ID_INTENT, reminderID);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }else {
            Log.e("REMIND", "audio reminder is false, starting checking reminder or prompt");
            if(reminder != null){
                if(reminder.getReminderType().equals(ReminderType.REMINDER)) {
                    Intent intent = new Intent(context, WearReminderNotificationActivity.class);
                    intent.putExtra(Consts.REMINDER_INTENT, reminder);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else{
                    Intent intent = new Intent(context, WearPromptNotification.class);
                    intent.putExtra(Consts.REMINDER_INTENT, reminder);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        }
    }

    private void notifyReminder(){
        // Get Audio Manager to override device volume settings
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringTone = RingtoneManager.getRingtone(context, notification);
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        int current_volume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION
                , audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION), 0);
        ringTone.play();
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION
                , current_volume, 0);

        vibrator.vibrate(1000);
    }
}
