package highway62.reminderapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Listens for broadcast of intent sent when the device reboots or the activity starts.
 * Reloads all the alarms (reminders)
 */
public class ReminderBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ReminderService.startReloadAfterBoot(context);
    }

}
