package highway62.reminderapp.reminders;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.wearable.DataMap;

import highway62.reminderapp.constants.DurationScale;

/**
 * Created by Highway62 on 29/09/2016.
 */
public class AudioLogReminder implements Parcelable{

    private long id = -1;
    private long dateTime = -1;
    private long dateTimeSet = -1;
    private long afterTime = -1;
    private DurationScale afterScale = null;

    public AudioLogReminder(){}

    public AudioLogReminder(long id, long dt, long dtSet, long afterTime, String afterScale){
        this.id = id;
        this.dateTime = dt;
        this.dateTimeSet = dtSet;
        this.afterTime = afterTime;
        this.afterScale = DurationScale.valueOf(afterScale);
    }

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

    // Parcelable Methods ------------------------------------------------------------------------//
    protected AudioLogReminder(Parcel in) {
        id = in.readLong();
        dateTime = in.readLong();
        dateTimeSet = in.readLong();
        afterTime = in.readLong();
        afterScale = (DurationScale) in.readValue(DurationScale.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(dateTime);
        dest.writeLong(dateTimeSet);
        dest.writeLong(afterTime);
        dest.writeValue(afterScale);
    }

    @SuppressWarnings("unused")
    public static final Creator<AudioLogReminder> CREATOR = new Creator<AudioLogReminder>() {
        @Override
        public AudioLogReminder createFromParcel(Parcel in) {
            return new AudioLogReminder(in);
        }

        @Override
        public AudioLogReminder[] newArray(int size) {
            return new AudioLogReminder[size];
        }
    };

    // ------------------------------------------------------------------------------------------ //

    // Data Map Methods ------------------------------------------------------------------------- //
    public AudioLogReminder(DataMap map) {
        this(map.getLong("map_id"),
                map.getLong("map_datetime"),
                map.getLong("map_datetime_set"),
                map.getLong("map_after_time"),
                map.getString("map_after_scale")
        );
    }

    public DataMap putToDataMap(DataMap map) {
        map.putLong("map_id", getId());
        map.putLong("map_datetime", getDateTime());
        map.putLong("map_datetime_set", getDateTimeSet());
        map.putLong("map_after_time", getAfterTime());
        map.putString("map_after_scale",getAfterScale().name());
        return map;
    }
    // ------------------------------------------------------------------------------------------ //
}
