package highway62.reminderapp.adminSettings;

import highway62.reminderapp.reminders.BaseReminder;

/**
 * Created by Highway62 on 28/07/2016.
 */
public interface PromptSelectorObserver {
    void notifyPromptDeleted(BaseReminder p);
}
