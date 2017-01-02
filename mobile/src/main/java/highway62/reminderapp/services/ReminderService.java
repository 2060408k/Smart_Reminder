package highway62.reminderapp.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import highway62.reminderapp.reminderhandlers.ReminderHandler;

public class ReminderService extends IntentService {

    private static final String ACTION_BOOT_RELOAD = "ACTION_BOOT_RELOAD";
    private static final String ACTION_DELETE = "ACTION_DELETE";


    public ReminderService() {
        super("ReminderService");
    }

    public static void startReloadAfterBoot(Context context) {
        Intent intent = new Intent(context, ReminderService.class);
        intent.setAction(ACTION_BOOT_RELOAD);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_BOOT_RELOAD.equals(action)) {
                reloadReminders();
            }
        }
    }

    private void reloadReminders(){
        ReminderHandler.loadReminders(this);
    }
}
