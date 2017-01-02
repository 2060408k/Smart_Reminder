package highway62.reminderapp.fragmentHandlers;

import highway62.reminderapp.reminders.BaseReminder;

/**
 * Created by Highway62 on 10/08/2016.
 */
public interface OnBSFragmentListener {
    void resetAfterSave();
    void resetAfterSaveBirthday();
    BaseReminder getReminder();
    void onHomePressed();
}