package highway62.reminderapp.reminders;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import highway62.reminderapp.constants.Day;
import highway62.reminderapp.constants.DurationScale;
import highway62.reminderapp.constants.EventType;
import highway62.reminderapp.constants.NotificationScale;
import highway62.reminderapp.constants.ReminderPattern;
import highway62.reminderapp.constants.ReminderType;

/**
 * Created by Highway62 on 04/08/2016.
 */
public class BaseReminder implements Parcelable {

    private long id = -1;


    private String title;
    private String eventAfter;
    private String Location;
    private long dateTime = -1;
    private boolean durationSet = false;
    private int eventDurationTime = -1;
    private DurationScale eventDurationScale;
    private int notificationTime = -1;
    private NotificationScale notificationScale;
    private String notes;
    private boolean repeating = false;
    private Day[] repetitionDays = new Day[0];           // On what days to repeat the reminder
    private int repetitionWeeks = -1;                   // How many weeks to repeat the reminder
    private int promptLevel = -1;
    private EventType type;
    private ReminderType reminderType;
    private ReminderPattern pattern;
    private Boolean smartReminded=false;
    private long logID = -1;





    public BaseReminder(){}

    public BaseReminder(BaseReminder copy){
        this.title = copy.getTitle();
        this.eventAfter = copy.getEventAfter();
        this.Location = copy.getLocation();
        this.durationSet = copy.isDurationSet();
        this.eventDurationTime = copy.getEventDurationTime();
        this.eventDurationScale = copy.getEventDurationScale();
        this.notificationTime = copy.getNotificationTime();
        this.notificationScale = copy.getNotificationScale();
        this.notes = copy.getNotes();
        this.promptLevel = copy.getPromptLevel();
        this.type = copy.getType();
        this.reminderType = copy.getReminderType();
    }




    public BaseReminder(long id){
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getEventAfter() {
        return eventAfter;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public void setEventAfter(String eventAfter) {
        this.eventAfter = eventAfter;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getPromptLevel() {
        return promptLevel;
    }

    public void setPromptLevel(int promptLevel) {
        this.promptLevel = promptLevel;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isDurationSet() {
        return durationSet;
    }

    public void setDurationSet(boolean durationSet) {
        this.durationSet = durationSet;
    }

    public int getEventDurationTime() {
        return eventDurationTime;
    }

    public void setEventDurationTime(int eventDurationTime) {
        this.eventDurationTime = eventDurationTime;
    }

    public DurationScale getEventDurationScale() {
        return eventDurationScale;
    }

    public void setEventDurationScale(DurationScale eventDurationScale) {
        this.eventDurationScale = eventDurationScale;
    }

    public int getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(int notificationTime) {
        this.notificationTime = notificationTime;
    }

    public NotificationScale getNotificationScale() {
        return notificationScale;
    }

    public void setNotificationScale(NotificationScale notificationScale) {
        this.notificationScale = notificationScale;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public Day[] getRepetitionDays() {
        return repetitionDays;
    }

    public void setRepetitionDays(Day[] repetitionDays) {
        this.repetitionDays = repetitionDays;
    }

    public int getRepetitionWeeks() {
        return repetitionWeeks;
    }

    public void setRepetitionWeeks(int repetitionWeeks) {
        this.repetitionWeeks = repetitionWeeks;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public ReminderType getReminderType() {
        return reminderType;
    }

    public void setReminderType(ReminderType reminderType) {
        this.reminderType = reminderType;
    }

    public long getLogID() {
        return logID;
    }

    public void setLogID(long logID) {
        this.logID = logID;
    }

    public void setPattern(ReminderPattern pattern) {
        this.pattern = pattern;
    }

    public ReminderPattern getPattern() {
        return pattern;
    }

    public Boolean getSmartReminded() {
        return smartReminded;
    }

    public void setSmartReminded(Boolean smartReminded) {
        this.smartReminded = smartReminded;
    }

    protected BaseReminder(Parcel in) {
        id = in.readLong();
        title = (String) in.readValue(String.class.getClassLoader());
        eventAfter = (String) in.readValue(String.class.getClassLoader());
        Location = (String) in.readValue(String.class.getClassLoader());
        dateTime = in.readLong();
        durationSet = in.readByte() != 0x00;
        eventDurationTime = in.readInt();
        eventDurationScale = (DurationScale) in.readValue(DurationScale.class.getClassLoader());
        notificationTime = in.readInt();
        notificationScale = (NotificationScale) in.readValue(NotificationScale.class.getClassLoader());
        notes = (String) in.readValue(String.class.getClassLoader());
        repeating = in.readByte() != 0x00;

        ArrayList<String> repDaysAsStrings = new ArrayList<>();
        in.readList(repDaysAsStrings, String.class.getClassLoader());
        if(repDaysAsStrings.size() > 0){
            repetitionDays = new Day[repDaysAsStrings.size()];
            for(int i = 0 ; i < repDaysAsStrings.size(); i++){
                repetitionDays[i] = Day.valueOf(repDaysAsStrings.get(i));
            }
        }else{
            repetitionDays = null;
        }

        repetitionWeeks = in.readInt();
        promptLevel = in.readInt();
        type = (EventType) in.readValue(EventType.class.getClassLoader());
        reminderType = (ReminderType) in.readValue(ReminderType.class.getClassLoader());
        pattern = (ReminderPattern) in.readValue(ReminderPattern.class.getClassLoader());
        byte smartRemindedVal = in.readByte();
        smartReminded = smartRemindedVal == 0x02 ? null : smartRemindedVal != 0x00;
        logID = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeValue(title);
        dest.writeValue(eventAfter);
        dest.writeValue(Location);
        dest.writeLong(dateTime);
        dest.writeByte((byte) (durationSet ? 0x01 : 0x00));
        dest.writeInt(eventDurationTime);
        dest.writeValue(eventDurationScale);
        dest.writeInt(notificationTime);
        dest.writeValue(notificationScale);
        dest.writeValue(notes);
        dest.writeByte((byte) (repeating ? 0x01 : 0x00));

        ArrayList<String> dayStrings = new ArrayList<>();
        if(repetitionDays != null){
            for(Day d : repetitionDays){
                dayStrings.add(d.name());
            }
        }
        dest.writeList(dayStrings); // Repetition days as Strings

        dest.writeInt(repetitionWeeks);
        dest.writeInt(promptLevel);
        dest.writeValue(type);
        dest.writeValue(reminderType);
        dest.writeValue(pattern);
        if (smartReminded == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (smartReminded ? 0x01 : 0x00));
        }
        dest.writeLong(logID);
    }

    public static final Parcelable.Creator<BaseReminder> CREATOR = new Parcelable.Creator<BaseReminder>() {
        @Override
        public BaseReminder createFromParcel(Parcel in) { return new BaseReminder(in); }

        @Override
        public BaseReminder[] newArray(int size) {
            return new BaseReminder[size];
        }
    };
}
