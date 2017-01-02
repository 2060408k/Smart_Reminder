package highway62.reminderapp.communications;

import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.Random;

import highway62.reminderapp.constants.Consts;
import highway62.reminderapp.marshalling.ParcelableUtil;
import highway62.reminderapp.reminderhandlers.ReminderHandler;
import highway62.reminderapp.reminderhandlers.ReminderReceiver;
import highway62.reminderapp.reminders.BaseReminder;

/**
 * Listens for a message event from the phone and broadcasts an intent for the broadcast receiver
 */
public class ListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if (messageEvent.getPath().equals(Consts.REMINDER_PATH)) {

            BaseReminder reminder = ParcelableUtil.unmarshall(messageEvent.getData(), BaseReminder.CREATOR);

            Log.e("LISTENER", "Reminder received in ListenerService: " + reminder.getReminderType().name());

            // Broadcast message to wearable activity for display
            Intent intent = new Intent(this, ReminderReceiver.class);
            intent.putExtra(Consts.REMINDER_INTENT, reminder);
            Random rand = new Random();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, rand.nextInt(), intent, PendingIntent.FLAG_ONE_SHOT);

            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }else if(messageEvent.getPath().equals(Consts.REMINDER_LOG_PATH)){

            // Handles requests for logs of audio reminders set on the watch
            ComsHandler cHandler = new ComsHandler();
            cHandler.sendReminderLogsToPhone(this,ReminderHandler.getAllRemindersForLog(this));

        } else {
            Log.e("LISTENER", "Message received, wrong path");
            super.onMessageReceived(messageEvent);
        }
    }
}
