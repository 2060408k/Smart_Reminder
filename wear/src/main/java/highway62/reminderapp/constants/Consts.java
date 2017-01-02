package highway62.reminderapp.constants;

import android.media.AudioFormat;

/**
 * Created by Highway62 on 06/09/2016.
 */
public abstract class Consts {

    public static final String INTENT_MODE = "INTENT_MODE";
    public static final String REMINDER_INTENT = "REMINDER_INTENT";
    public static final String AUDIO_INTENT = "AUDIO_INTENT";
    public static final String REMINDER_ID_INTENT = "REMINDER_ID_INTENT";

    public static final int PERMISSION_CODE = 1;

    /**
     * Communications Handler
     */
    public static final String REMINDER_PATH = "/reminder";
    public static final String REMINDER_ASSET = "reminder";
    public static final String REMINDER_LOG_PATH = "/reminder_log";
    public static final String REMINDER_LOG_LIST_DATAMAP = "log_data_map_list";

    /**
     * Audio
     */
    public static final int RECORDER_SAMPLERATE = 8000;
    public static final int RECORDER_CHANNELS_IN = AudioFormat.CHANNEL_IN_MONO;
    public static final int RECORDER_CHANNELS_OUT = AudioFormat.CHANNEL_OUT_MONO;
    public static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    /**
     * Manual Time Chooser
     */
    public static final  int PAGES = 12;
    public static final int LOOPS = 1000;
    public static final int FIRST_PAGE = PAGES * LOOPS / 2;

    /**
     * Used by the manual view pager. (Offset by 1)
     * Very hacky solution, sorry! (pressed for time)
     */
    public static final int[] REM_TIMES = new int[]{10, 15, 20, 30, 45, 50, 1, 2, 4, 6, 8, 5};
    public static final DurationScale[] REM_SCALES = new DurationScale[]{
            DurationScale.MINS,
            DurationScale.MINS,
            DurationScale.MINS,
            DurationScale.MINS,
            DurationScale.MINS,
            DurationScale.MINS,
            DurationScale.HOURS,
            DurationScale.HOURS,
            DurationScale.HOURS,
            DurationScale.HOURS,
            DurationScale.HOURS,
            DurationScale.MINS
    };

    /**
     * Used by the quick reminder time button on main screen to cycle through times
     */
    public static final int[] QUICK_TIMES = new int[] {5, 10, 15, 20, 30
            , 45, 50, 1, 2, 4, 6, 8, 5};
    public static final DurationScale[] QUICK_SCALES = new DurationScale[] {
            DurationScale.MINS,
            DurationScale.MINS,
            DurationScale.MINS,
            DurationScale.MINS,
            DurationScale.MINS,
            DurationScale.MINS,
            DurationScale.MINS,
            DurationScale.HOURS,
            DurationScale.HOURS,
            DurationScale.HOURS,
            DurationScale.HOURS,
            DurationScale.HOURS,
            DurationScale.HOURS
    };
    public static final int indexLimit = 12;


    /* TESTING
    public static final long ONE_MIN_IN_MILLIS = 6000;
    public static final long ONE_HOUR_IN_MILLIS = ONE_MIN_IN_MILLIS * 60;
    */

    // RELEASE
    public static final long ONE_MIN_IN_MILLIS = 60000;
    public static final long ONE_HOUR_IN_MILLIS = ONE_MIN_IN_MILLIS * 60;

}
