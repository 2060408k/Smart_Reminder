package highway62.reminderapp.reminders;

import highway62.reminderapp.constants.DurationScale;

/**
 * Created by Highway62 on 25/09/2016.
 */
public class AudioReminder {

    private long id = -1;
    private long dateTime = -1;
    private long dateTimeSet = -1;
    private long afterTime = -1;
    private DurationScale afterScale = null;
    private byte[] audio = null;

    public AudioReminder(){}

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

    public long getDateTimeSet() {
        return dateTimeSet;
    }

    public void setDateTimeSet(long dateTimeSet) {
        this.dateTimeSet = dateTimeSet;
    }

    public long getAfterTime() {
        return afterTime;
    }

    public void setAfterTime(long afterTime) {
        this.afterTime = afterTime;
    }

    public DurationScale getAfterScale() {
        return afterScale;
    }

    public void setAfterScale(DurationScale afterScale) {
        this.afterScale = afterScale;
    }

    public byte[] getAudio() {
        return audio;
    }

    public void setAudio(byte[] audio) {
        this.audio = audio;
    }

}
