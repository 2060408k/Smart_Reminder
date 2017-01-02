package highway62.reminderapp.constants;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Highway62 on 04/08/2016.
 */
public final class Consts {

    /**
     * Reminder intent keys
     */
    public static final String REMINDER_INTENT = "REMINDER_INTENT";
    public static final String REMINDER_SUGGEST = "REMINDER_SUGGEST";
    /**
     * Array of days to index into day of week strings
     */
    public static final Day[] daysOfWeek = {Day.MONDAY, Day.TUESDAY, Day.WEDNESDAY, Day.THURSDAY
            , Day.FRIDAY, Day.SATURDAY, Day.SUNDAY};

    /**
     * Visual Choices For Settings
     */
    public enum VisualChoice {
        NOTIFICATION, FULLSCREEN
    }

    /**
     * Unique int used for Notification IDs
     */
    public static AtomicInteger UNIQUE_INT = new AtomicInteger();

    /**
     * Communications handler
     */
    public static final String REMINDER_PATH = "/reminder";
    public static final String REMINDER_ASSET = "reminder";
    public static final String REMINDER_LOG_PATH = "/reminder_log";
    public static final String REMINDER_LOG_LIST_DATAMAP = "log_data_map_list";

    /**
     * Cancel Action for stopping vibration and sound
     */
    public static final String CANCEL_VIB_SOUND_ACTION = "cancel_vib_soun_action";


}
