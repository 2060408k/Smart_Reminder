package highway62.reminderapp.reminders;

import highway62.reminderapp.constants.Day;
import highway62.reminderapp.constants.EventType;

/**
 * Created by Highway62 on 27/08/2016.
 */
public class BaseLogReminder {
    private long id;
    private long dateTime = -1;
    private boolean repeating = false;
    private Day[] repeatingDays;
    private int repetitionWeeks = -1;
    private int promptLevel = -1;
    private EventType eventType;
    private long dateTimeSet = -1;

    public BaseLogReminder() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public Day[] getRepeatingDays() {
        return repeatingDays;
    }

    public void setRepeatingDays(Day[] repeatingDays) {
        this.repeatingDays = repeatingDays;
    }

    public int getRepetitionWeeks() {
        return repetitionWeeks;
    }

    public void setRepetitionWeeks(int repetitionWeeks) {
        this.repetitionWeeks = repetitionWeeks;
    }

    public int getPromptLevel() {
        return promptLevel;
    }

    public void setPromptLevel(int promptLevel) {
        this.promptLevel = promptLevel;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public long getDateTimeSet() {
        return dateTimeSet;
    }

    public void setDateTimeSet(long dateTimeSet) {
        this.dateTimeSet = dateTimeSet;
    }
}
