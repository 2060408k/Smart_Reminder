package highway62.reminderapp.adminSettings;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import highway62.reminderapp.R;
import highway62.reminderapp.constants.Day;
import highway62.reminderapp.constants.ReminderType;
import highway62.reminderapp.reminderhandlers.ReminderHandler;
import highway62.reminderapp.reminders.AudioLogReminder;
import highway62.reminderapp.reminders.BaseLogReminder;
import highway62.reminderapp.reminders.PromptLogReminder;

/**
 * Created by Highway62 on 29/08/2016.
 */
public class LogPrinter {

    TextView progressText;
    Context context;
    PrinterListener listener;
    boolean successful;
    String reason = "";
    ArrayList<AudioLogReminder> logReminders;

    public LogPrinter(Context context){
        this.context = context;
        successful = false;
    }

    public LogPrinter(Context context, PrinterListener listener, TextView progressText) {
        this.context = context;
        this.listener = listener;
        this.progressText = progressText;
        successful = false;
    }

    public void printReminders() {
        new PrintTask().execute(ReminderType.REMINDER);
    }

    public void printPrompts() {
        new PrintTask().execute(ReminderType.PROMPT);
    }

    public void printWatchLogs(ArrayList<AudioLogReminder> logReminders) {
        this.logReminders = logReminders;
        new PrintTask().execute(ReminderType.WATCH);
    }

    private class PrintTask extends AsyncTask<ReminderType, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(ReminderType... params) {

            if (isExternalStorageWritable()) {
                if (params[0].equals(ReminderType.REMINDER)) {
                    try {
                        writeRemindersToFile(ReminderHandler.getAllRemindersFromLog(context));
                    } catch (IOException e) {
                        Log.e("LOGPRINT", e.getMessage());
                        successful = false;
                        reason = "Error: Failed to Print. " + e.getMessage();
                    }
                } else if (params[0].equals(ReminderType.PROMPT)) {
                    try {
                        writePromptsToFile(ReminderHandler.getAllPromptsFromLog(context));
                    } catch (IOException e) {
                        Log.e("LOGPRINT", e.getMessage());
                        successful = false;
                        reason = "Error: Failed to Print. " + e.getMessage();
                    }
                } else if (params[0].equals(ReminderType.WATCH)) {
                    try {
                        writeWatchLogsToFile();
                    } catch (IOException e) {
                        Log.e("LOGPRINT", e.getMessage());
                        successful = false;
                        reason = "Error: Failed to Print. " + e.getMessage();
                    }
                }
            } else {
                successful = false;
                reason = "Error: No external memory to write to. Check SD Card and volume size";
                Log.e("PRINT", "External storage not writable");
            }
            return successful;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(listener != null) {
                listener.upDateProgress(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if(listener != null) {
                listener.onFinishedPrinting(success, reason);
            }
        }

        private void writeRemindersToFile(ArrayList<BaseLogReminder> reminders) throws IOException {
            if (reminders.size() > 0) {
                String fileName = getFilename(ReminderType.REMINDER);
                File reminderLogFile =
                        new File(getAlbumStorageDir(context.getString(R.string.print_log_reminder_dir)), fileName);

                FileOutputStream f = new FileOutputStream(reminderLogFile);
                PrintWriter pw = new PrintWriter(f);

                for (int i = 0; i < reminders.size(); i++) {
                    BaseLogReminder reminder = reminders.get(i);
                    pw.println(getReminderPrintString(reminder));
                    publishProgress(Math.round((i + 1) * (100 / reminders.size())));
                }

                pw.flush();
                pw.close();
                f.close();
                successful = true;
                reason = "";
            } else {
                successful = false;
                reason = "No reminders to print.";
            }
        }

        private void writePromptsToFile(ArrayList<PromptLogReminder> prompts) throws IOException {

            if (prompts.size() > 0) {

                String fileName = getFilename(ReminderType.PROMPT);
                File promptLogFile =
                        new File(getAlbumStorageDir(context.getString(R.string.print_log_prompt_dir)), fileName);

                FileOutputStream f = new FileOutputStream(promptLogFile);
                PrintWriter pw = new PrintWriter(f);

                for (int i = 0; i < prompts.size(); i++) {
                    PromptLogReminder reminder = prompts.get(i);
                    pw.println(getPromptPrintString(reminder));
                    publishProgress(Math.round((i + 1) * (100 / prompts.size())));
                }

                pw.flush();
                pw.close();
                f.close();
                successful = true;
                reason = "";
            } else {
                successful = false;
                reason = "No prompts to print.";
            }
        }

        private void writeWatchLogsToFile() throws IOException {
            if (logReminders != null) {
                String fileName = getFilename(ReminderType.WATCH);
                File watchLogFile =
                        new File(getAlbumStorageDir(context.getString(R.string.print_log_watch_dir)), fileName);

                FileOutputStream f = new FileOutputStream(watchLogFile);
                PrintWriter pw = new PrintWriter(f);

                StringBuilder sb = new StringBuilder();
                sb.append("Total No. of Watch Audio Reminders Set: ");
                sb.append(logReminders.size());
                sb.append("\r\n\r\n");

                pw.println(sb.toString());

                for (int i = 0; i < logReminders.size(); i++) {
                    AudioLogReminder alr = logReminders.get(i);
                    pw.println(getWatchPrintString(alr));
                }

                pw.flush();
                pw.close();
                f.close();
                successful = true;
                reason = "";
            } else {
                successful = false;
                reason = "No watch logs to print.";
            }
        }

        /* Checks if external storage is available for read and write */
        public boolean isExternalStorageWritable() {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                return true;
            }
            return false;
        }

        public File getAlbumStorageDir(String albumName) {
            // Get the directory for the user's public documents directory.
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS), albumName);
            if (!file.mkdirs()) {
                Log.e("LOGPRINT", "LogPrinter: getAlbumStorageDir() - Directory not created");
            }
            return file;
        }

        private String getFilename(ReminderType type) {
            DateTime dt = new DateTime();
            /*DateTimeFormatter dateFormat = DateTimeFormat.forPattern("dd-MM-yyyy");
            DateTimeFormatter timeFormat = DateTimeFormat.forPattern("HH-mm");
            String dateString = dateFormat.print(dt);
            String timeString = timeFormat.print(dt);*/
            String minuteString = "";
            if(dt.getMinuteOfHour() < 10){
                minuteString = "0";
            }

            if (type.equals(ReminderType.REMINDER)) {
                return "R_" + dt.getDayOfMonth()
                        + "-" + dt.getMonthOfYear()
                        + "-" + dt.getYear()
                        + "_" + dt.getHourOfDay()
                        + minuteString
                        + dt.getMinuteOfHour()
                        + ".txt";
            } else if (type.equals(ReminderType.WATCH)) {
                return "W_" + dt.getDayOfMonth()
                        + "-" + dt.getMonthOfYear()
                        + "-" + dt.getYear()
                        + "_" + dt.getHourOfDay()
                        + minuteString
                        + dt.getMinuteOfHour()
                        + ".txt";
            } else {
                return "P_" + dt.getDayOfMonth()
                        + "-" + dt.getMonthOfYear()
                        + "-" + dt.getYear()
                        + "_" + dt.getHourOfDay()
                        + minuteString
                        + dt.getMinuteOfHour()
                        + ".txt";
            }
        }

        private String getReminderPrintString(BaseLogReminder reminder) {
            DateTime dt = new DateTime(reminder.getDateTime());
            DateTime timeSet = new DateTime(reminder.getDateTimeSet());
            DateTimeFormatter dateFormat = DateTimeFormat.forPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormat = DateTimeFormat.forPattern("HH:mm");
            StringBuilder sb = new StringBuilder();
            sb.append("Date: " + dateFormat.print(dt));
            sb.append("\r\n");
            sb.append("Time: " + timeFormat.print(dt));
            sb.append("\r\n");
            sb.append("Repeating: " + reminder.isRepeating());
            if (reminder.isRepeating()) {
                sb.append("\r\n");
                sb.append("Repetition Days: ");
                Day[] days = reminder.getRepeatingDays();
                for (int i = 0; i < days.length; i++) {
                    sb.append(days[i].name());
                    if (i != days.length - 1) {
                        sb.append(", ");
                    }
                }
                sb.append("\r\n");
                sb.append("Repeating No. of Weeks: " + reminder.getRepetitionWeeks());
            }
            sb.append("\r\n");
            sb.append("Prompt Level: " + reminder.getPromptLevel());
            sb.append("\r\n");
            sb.append("Event Type: " + reminder.getEventType().name());
            sb.append("\r\n");
            sb.append("Reminder was Saved on " + dateFormat.print(timeSet) + ", at " + timeFormat.print(timeSet));
            sb.append("\r\n");
            sb.append("--------------------------------------------------------------");
            sb.append("\r\n");
            return sb.toString();
        }

        private String getPromptPrintString(PromptLogReminder reminder) {
            DateTime dt = new DateTime(reminder.getDateTime());
            DateTime timeSet = new DateTime(reminder.getDateTimeSet());
            DateTimeFormatter dateFormat = DateTimeFormat.forPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormat = DateTimeFormat.forPattern("HH:mm");
            StringBuilder sb = new StringBuilder();
            sb.append("Date: " + dateFormat.print(dt));
            sb.append("\r\n");
            sb.append("Time: " + timeFormat.print(dt));
            sb.append("\r\n");
            sb.append("Content: " + reminder.getContent());
            sb.append("\r\n");
            sb.append("Prompt Level: " + reminder.getPromptLevel());
            sb.append("\r\n");
            sb.append("Positive Response to Prompt?" + reminder.userRespondedPositively());
            sb.append("\r\n");
            sb.append("Prompt was Sent on " + dateFormat.print(timeSet) + ", at " + timeFormat.print(timeSet));
            sb.append("\r\n");
            sb.append("--------------------------------------------------------------------------");
            sb.append("\r\n");
            return sb.toString();
        }

        private String getWatchPrintString(AudioLogReminder alr){
            DateTime dt = new DateTime(alr.getDateTime());
            DateTime timeSet = new DateTime(alr.getDateTimeSet());
            DateTimeFormatter dateFormat = DateTimeFormat.forPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormat = DateTimeFormat.forPattern("HH:mm");
            StringBuilder sb = new StringBuilder();
            sb.append("Date: " + dateFormat.print(dt));
            sb.append("\r\n");
            sb.append("Time: " + timeFormat.print(dt));
            sb.append("\r\n");
            sb.append("Reminder was saved on "
                    + dateFormat.print(timeSet) + " at " + timeFormat.print(timeSet));
            sb.append("\r\n");
            sb.append("User chose to be reminded after " + alr.getAfterTime()
                    + " " + alr.getAfterScale().name());
            sb.append("\r\n");
            sb.append("--------------------------------------------------------------------------");
            sb.append("\r\n");
            return sb.toString();
        }
    }
}
