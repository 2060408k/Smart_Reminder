package highway62.reminderapp.reminderhandlers;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import highway62.reminderapp.ParcelableUtil;
import highway62.reminderapp.PromptNotificationActivity;
import highway62.reminderapp.R;
import highway62.reminderapp.ReminderActivity;
import highway62.reminderapp.ReminderDetailActivity;
import highway62.reminderapp.ReminderNotificationActivity;
import highway62.reminderapp.SmartReminding.SmartReminding;
import highway62.reminderapp.adminSettings.SettingsInterface;
import highway62.reminderapp.communication.CommunicationHandler;
import highway62.reminderapp.constants.Consts;
import highway62.reminderapp.constants.EventType;
import highway62.reminderapp.constants.ReminderPattern;
import highway62.reminderapp.constants.ReminderType;
import highway62.reminderapp.reminders.BaseReminder;

/**
 * Receives intents sent by the alarm manager after a reminder has been triggered
 */
public class ReminderReceiver extends BroadcastReceiver {

    private SettingsInterface settings;
    private Context context;
    private BaseReminder reminder;
    private BaseReminder past_reminder;
    public static Vibrator vibrator;
    public static Ringtone ringTone;
    public static Thread vibThread;
    public static Thread soundThread;
    public static boolean ringToneCancelled = false;

    public ReminderReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        boolean suggest=intent.getBooleanExtra(Consts.REMINDER_SUGGEST,false);
        if (suggest){

            new SmartReminding(this.context).collect_and_set_reminder_suggestions();
            return;}
        this.settings = new SettingsInterface(context);
        if (Build.VERSION.SDK_INT >= 24){
            byte[] bytes = intent.getByteArrayExtra(Consts.REMINDER_INTENT);
            this.reminder = (BaseReminder) ParcelableUtil.toParcelable(bytes,BaseReminder.CREATOR);
        }else {
            this.reminder = (BaseReminder) intent.getParcelableExtra(Consts.REMINDER_INTENT);
        }

        //Get past reminder in case it's a SMART reminder
        byte[] bytes = intent.getByteArrayExtra(Consts.REMINDER_PAST);
        if(bytes!= null){
            this.past_reminder= (BaseReminder) ParcelableUtil.toParcelable(bytes,BaseReminder.CREATOR);
        }
        if (this.reminder==null || this.reminder.getSmartReminded()) return;
        ringToneCancelled = false;
        displayReminder();
    }

    private void displayReminder() {
        if (settings.sendToWatchSet() && reminder!=null) {
            sendReminderToWatch(reminder);
        }
        ReminderType type = reminder.getReminderType();
        if (settings.useDefaultLevelsIsSet()) {
            type = null;
        }
        int level = reminder.getPromptLevel();
        if (level == -1) {
            level = settings.getDefaultUserSubtletyLevel();
        }
        // Reminder Visual -----------------------------------------------------------------------//
        if (settings.visualEnabledIsSet(level, type) || type==ReminderType.SMART) {
            Consts.VisualChoice visChoice = settings.getVisualChoice(level, type);
            if(type==ReminderType.SMART) visChoice= Consts.VisualChoice.NOTIFICATION;
            switch (visChoice) {
                case NOTIFICATION:
                    displayNotification(level, type);
                    break;
                case FULLSCREEN:
                    displayFullScreenReminder();
                    break;
            }
        }else{
            playVibrationAndSound(level, type);
        }
        //----------------------------------------------------------------------------------------//
    }

    private void displayNotification(final int level, ReminderType type) {

        playVibrationAndSound(level, type);

        String title = buildTitle();
        String content = buildContent();

        /*
        // Intents to handle cancelling vibration and sound on notification click
        Intent cancelIntent = new Intent(context, NotificationActionService.class)
                .setAction(Consts.CANCEL_VIB_SOUND_ACTION);

        PendingIntent cancelPendingIntent = PendingIntent.getService(context, 0,
                cancelIntent, PendingIntent.FLAG_ONE_SHOT);
        */

        // Build silent notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_tab_reminder)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setLights(0xff00ff00, 300, 100)
                        .setAutoCancel(true)
                        .setCategory(Notification.CATEGORY_MESSAGE)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setVibrate(null)
                        .setSound(null);

        if (reminder.getReminderType().equals(ReminderType.SMART)){
            NotificationCompat.InboxStyle inboxStyle =
                    new NotificationCompat.InboxStyle();

            // Sets a title for the Inbox in expanded layout
            inboxStyle.setBigContentTitle("Reminder Suggestion!");

            // Set content
            String[] items = reminder.getNotes().split(",");
            List<String> itemList = new ArrayList<String>(Arrays.asList(items));

            for (String text : itemList){
                inboxStyle.addLine(text);
            }

            mBuilder.setStyle(inboxStyle);
        }
        Intent resultIntent;
        if (reminder.getReminderType().equals(ReminderType.REMINDER)) {

            resultIntent = new Intent(context, ReminderDetailActivity.class);
            resultIntent.putExtra(Consts.REMINDER_INTENT, reminder);
        } else {
            resultIntent = new Intent(context, ReminderActivity.class);
            if (this.past_reminder!=null){
                byte[] bytes=ParcelableUtil.toByteArray(this.past_reminder);
                resultIntent.putExtra(Consts.REMINDER_PAST,bytes);
            }


            if (reminder.getReminderType().equals(ReminderType.SMART)){
                resultIntent.putExtra("NotiClick",true);
                resultIntent.putExtra("pattern",reminder.getPattern());

                //get database reference
                DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
                //get android device's unique id or name
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
                Boolean smart_login = sharedPreferences.getBoolean("smart_login",false);
                String smart_login_name = sharedPreferences.getString("smart_login_name",null);
                final String  android_id;
                if (smart_login){
                    android_id=smart_login_name;
                }else{
                    android_id= Settings.Secure.getString(this.context.getContentResolver(), Settings.Secure.ANDROID_ID);

                }
                //add one time event listener to update data
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get the mapped database values
                        HashMap map = (HashMap)dataSnapshot.getValue();

                        //New value for prompts_accepted fields
                        long value = (long)((HashMap)map.get(android_id)).get("total_prompts") + 1;
                        long weekly_value = (long)((HashMap)map.get(android_id)).get("weekly_prompts") + 1;
                        long two_week_value = (long)((HashMap)map.get(android_id)).get("two_week_prompts") + 1;
                        long monthly_value = (long)((HashMap)map.get(android_id)).get("monthly_prompts") + 1;

                        //Check if smart reminding is enabled
                        boolean check= (boolean)((HashMap)map.get(android_id)).get("smart_reminding");
                        if (check)
                            dataSnapshot.getRef().child(android_id).child("total_prompts").setValue(value);
                            ReminderPattern pattern=reminder.getPattern();

                            if (pattern!=null){
                                if (pattern.equals(ReminderPattern.WEEKLY))
                                    dataSnapshot.getRef().child(android_id).child("weekly_prompts").setValue(weekly_value);
                                if (pattern.equals(ReminderPattern.TWO_WEEKS))
                                    dataSnapshot.getRef().child(android_id).child("two_week_prompts").setValue(two_week_value);
                                if (pattern.equals(ReminderPattern.MONTHLY))
                                    dataSnapshot.getRef().child(android_id).child("monthly_prompts").setValue(monthly_value);
                            }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                    }
                });
            }

        }

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        // Send the notification
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(Consts.UNIQUE_INT.incrementAndGet(), mBuilder.build());
    }

    private void displayFullScreenReminder() {
        Intent intent;
        if (reminder.getReminderType().equals(ReminderType.REMINDER)) {
            intent = new Intent(context, ReminderNotificationActivity.class);
        } else {
            intent = new Intent(context, PromptNotificationActivity.class);
        }
        intent.putExtra(Consts.REMINDER_INTENT, reminder);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void playVibrationAndSound(final int level, final ReminderType type) {

        // Vibrate -------------------------------------------------------------------------------//
        if (settings.enableVibrateIsSet(level, type)) {
            vibThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    if (vibrator.hasVibrator()) {
                        // Vibrate with the given pattern (-1 indicates not to repeat indefinitely)
                        vibrator.vibrate(getVibrationPattern(level, type), -1);
                    }

                }
            });

            vibThread.start();
        }
        //----------------------------------------------------------------------------------------//

        // Sound ---------------------------------------------------------------------------------//
        if (settings.enableSoundIsSet(level, type)) {
            soundThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    // Get sound settings
                    String sound = settings.getSoundChoice(level, type);
                    ringTone = RingtoneManager.getRingtone(context, Uri.parse(sound));
                    int soundVolume = settings.getVolumeLevel(level, type); // 0 = quiet -> 2 = loud

                    // Get Audio Manager to override device volume settings
                    AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    int current_volume = audioManager.getStreamVolume(AudioManager.STREAM_RING);

                    // Get volumes from audio manager for settings
                    int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
                    Log.e("SOUND", "max volume: " + maxVolume);
                    int mediumVolume = Math.round((float) maxVolume / 2);
                    Log.e("SOUND", "medium volume: " + mediumVolume);
                    int lowVolume = Math.round((float) maxVolume / 3);
                    Log.e("SOUND", "low volume: " + lowVolume);
                    int[] volumes = {lowVolume, mediumVolume, maxVolume};

                    // TODO check the volume

                    // Set volume based on settings
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, volumes[soundVolume], 0);

                    // Play sound once
                    ringTone.play();

                    if (settings.soundRepeatingIsSet(level, type)) {
                        float repeatSoundAfterInMins = settings.getSoundRepeatAfterInMins(level, type);
                        int repeatSoundAfterInMillis = (int) ((repeatSoundAfterInMins * 60) * 1000);
                        // Repeat
                        for (int i = 0; i < settings.getNoOfTimesToRepeatSound(level, type); i++) {
                            try {
                                Thread.sleep(repeatSoundAfterInMillis);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            if(!ringToneCancelled) {
                                ringTone.play();
                            }
                        }

                    }

                    // Set device volume back to what is was at before playing the notification sound
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, current_volume, 0);

                }
            });

            soundThread.start();
        }
        //----------------------------------------------------------------------------------------//
    }

    private long[] getVibrationPattern(int level, ReminderType type) {

        int noOfBursts = settings.getNoOfVibBursts(level, type);
        int timeBetweenBursts = (settings.getTimeBetweenVibrationBursts(level, type) * 1000); //ms
        int vibrationLength = (int) (settings.getVibrationLength(level, type) * 1000); //ms
        float repeatAfterTimeInMins = settings.getVibrationRepeatAfterInMins(level, type);
        long repeatAfterTimeInMillis = (long) ((repeatAfterTimeInMins * 60) * 1000);
        int noOfTimesToRepeat = settings.getNoOfTimestoRepeat(level, type);


        ArrayList<Long> patternBuilder = new ArrayList<>();
        // Initial rest = 0. ie vibrate immediately
        patternBuilder.add(0L);

        for (int i = 0; i < noOfBursts; i++) {
            // Vibrate for length
            patternBuilder.add((long) vibrationLength);
            if (i != (noOfBursts - 1)) {
                // Rest for length (unless last index pos)
                patternBuilder.add((long) timeBetweenBursts);
            }
        }

        // Handle repeat
        if (settings.enableVibRepeatIsSet(level, type)) {
            for (int i = 0; i < noOfTimesToRepeat; i++) {
                patternBuilder.add(repeatAfterTimeInMillis);
                for (int j = 0; j < noOfBursts; j++) {
                    patternBuilder.add((long) vibrationLength);
                    if (j != (noOfBursts - 1)) {
                        patternBuilder.add((long) timeBetweenBursts);
                    }
                }
            }
        }
        // Convert patternBuilder arraylist to primitive long array
        long[] pattern = new long[patternBuilder.size()];
        for (int j = 0; j < patternBuilder.size(); j++) {
            pattern[j] = patternBuilder.get(j);
        }

        return pattern;

    }

    private String buildTitle() {

        String title = reminder.getTitle();
        StringBuilder sb = new StringBuilder();

        if (reminder.getReminderType().equals(ReminderType.REMINDER)) {
            EventType eventType = reminder.getType();
            switch (eventType) {
                case GEN:
                    if (title != null && !TextUtils.isEmpty(title)) {
                        sb.append("Event: ");
                        sb.append(title);
                    } else {
                        sb.append("Event Reminder");
                    }
                    break;
                case APPT:
                    if (title != null && !TextUtils.isEmpty(title)) {
                        sb.append("Appointment: ");
                        sb.append(title);
                    } else {
                        sb.append("Appointment Reminder");
                    }
                    break;
                case SHOPPING:
                    if (title != null && !TextUtils.isEmpty(title)) {
                        sb.append("Shopping at: ");
                        sb.append(title);
                    } else {
                        sb.append("Shopping Reminder");
                    }
                    break;
                case BIRTH:
                    if (title != null && !TextUtils.isEmpty(title)) {
                        sb.append("Birthday: ");
                        sb.append(title);
                    } else {
                        sb.append("Birthday Reminder");
                    }
                    break;
                case MEDIC:
                    if (title != null && !TextUtils.isEmpty(title)) {
                        sb.append("Take Medication: ");
                        sb.append(title);
                    } else {
                        sb.append("Medication Reminder");
                    }
                    break;
                case DAILY:
                    if (title != null && !TextUtils.isEmpty(title)) {
                        sb.append("Daily Task: ");
                        sb.append(title);
                    } else {
                        sb.append("Daily Task Reminder");
                    }
                    break;
                case SOCIAL:
                    if (title != null && !TextUtils.isEmpty(title)) {
                        sb.append("Social Event: ");
                        sb.append(title);
                    } else {
                        sb.append("Social Event Reminder");
                    }
                    break;
                default:
                    if (title != null && !TextUtils.isEmpty(title)) {
                        sb.append("Event: ");
                        sb.append(title);
                    } else {
                        sb.append("Event Reminder");
                    }
                    break;
            }
        } else {
            String notes = reminder.getNotes();

            if (reminder.getReminderType().equals(ReminderType.SMART)){

                notes ="Reminder Suggestion !";
            }
            sb.append(notes);
        }

        return sb.toString();
    }

    private String buildContent() {
        String content;
        if (reminder.getReminderType().equals(ReminderType.REMINDER)) {
            DateTime dateTime = new DateTime(reminder.getDateTime());
            DateTimeFormatter dtfDate = DateTimeFormat.forPattern("dd/MM/yyyy");
            DateTimeFormatter dtfTime = DateTimeFormat.forPattern("HH:mm");
            content = "Set for: " + dtfDate.print(dateTime) + " at " + dtfTime.print(dateTime);

        } else {
            content = "Click here to Set a Reminder";
        }
        if (reminder.getReminderType().equals(ReminderType.SMART)) {
            DateTime dateTime = new DateTime(reminder.getDateTime());
            DateTimeFormatter dtfDate = DateTimeFormat.forPattern("dd/MM/yyyy");
            DateTimeFormatter dtfTime = DateTimeFormat.forPattern("HH:mm");
            StringBuilder sb2 = new StringBuilder();
            if(reminder.getTitle()!=null) sb2.append(reminder.getTitle()+": ");
            sb2.append(dtfTime.print(dateTime));
            content= sb2.toString();
        }

        return content;
    }

    private void sendReminderToWatch(BaseReminder reminder) {
        Log.e("MSGCOM", "sending reminder to watch");
        CommunicationHandler comsHandler = new CommunicationHandler(context);
        comsHandler.sendReminderToWatch(reminder);
    }

    // Handle a click event on the notification (if sent)
    public static class NotificationActionService extends IntentService {

        public NotificationActionService() {
            super(NotificationActionService.class.getSimpleName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            String action = intent.getAction();
            if (Consts.CANCEL_VIB_SOUND_ACTION.equals(action)) {
                // If you want to cancel the notification: NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
                if (vibrator != null && vibThread != null) {
                    vibrator.cancel();
                    vibThread.interrupt();
                }

                if (ringTone != null && soundThread != null) {
                    ringTone.stop();
                    ringToneCancelled = true;
                    soundThread.interrupt();
                }
            }
        }
    }
}
