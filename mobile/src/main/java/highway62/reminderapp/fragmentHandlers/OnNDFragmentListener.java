package highway62.reminderapp.fragmentHandlers;

import org.joda.time.DateTime;

import highway62.reminderapp.constants.EventType;
import highway62.reminderapp.reminders.BaseReminder;

/**
 * Created by Highway62 on 10/08/2016.
 */
public interface OnNDFragmentListener {

    // Navigate method - Generic
    void navigateFragmentForward(EventType eventType, String tag);
    void navigateFragmentBack(EventType eventType, String tag);
    void resetAfterSave(EventType eventType);
    void resetAfterSaveBirthday();
    BaseReminder getReminder();
    void setTempDate(DateTime dt);
    DateTime getTempDate();
    void onHomePressed();
}
