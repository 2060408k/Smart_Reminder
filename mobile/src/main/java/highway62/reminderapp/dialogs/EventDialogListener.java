package highway62.reminderapp.dialogs;

/**
 * Created by Highway62 on 09/08/2016.
 */
public interface EventDialogListener {

    // Notification Dialog
    void passResults(int number, int timeIndex);
    // Repeat Dialog
    void passResults(int numberOfWeeks);
}
