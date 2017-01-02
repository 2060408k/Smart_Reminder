package highway62.reminderapp.adminSettings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import highway62.reminderapp.R;
import highway62.reminderapp.constants.Consts;
import highway62.reminderapp.constants.ReminderType;

/**
 * Created by Highway62 on 01/08/2016.
 */
public class SettingsInterface {

    private Context context;
    private SharedPreferences prefs;

    public SettingsInterface(Context context) {
        this.context = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public int getUIVariant() {
        return Integer.parseInt(prefs.getString(context.getString(R.string.Pref_UI_NDBS_List), "0"));
    }

    public boolean getUIDecisionTreeEnabled() {
        return prefs.getBoolean(context.getString(R.string.Pref_UI_DT_Switch), false);
    }

    public boolean getUserSubtletyEnabled() {
        return prefs.getBoolean(context.getString(R.string.Pref_Prompt_User_Subtlety_Checkbox), true);
    }


    // PROMPT LEVEL SETTINGS ---------------------------------------------------------------------//
    public int getDefaultUserSubtletyLevel() {
        return Integer.parseInt(prefs.getString(context.getString(R.string.Pref_Prompt_Default_User_Subtlety_Level), "0"));
    }

    public boolean useDefaultLevelsIsSet() {
        return prefs.getBoolean(context.getString(R.string.Pref_Prompt_Default_Levels_Checkbox), true);
    }

    // VIBRATE -----------------------------------------------------------------------------------//

    // Vibrate Set -------------------------------------------------------------------------------->
    public boolean enableVibrateIsSet(int level, ReminderType type) {
        if (type == null) {
            return enableVibrateIsSetForDefault(level);
        } else {
            switch (type) {
                case REMINDER:
                    return enableVibrateIsSetForReminder(level);
                case PROMPT:
                    return enableVibrateIsSetForPrompt(level);
                default:
                    return enableVibrateIsSetForDefault(level);
            }
        }
    }

    private boolean enableVibrateIsSetForDefault(int level) {
        switch (level) {
            case 0:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vib_chk_key_0), false);
            case 1:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vib_chk_key_1), false);
            case 2:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vib_chk_key_2), false);
            case 3:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vib_chk_key_3), false);
            default:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vib_chk_key_0), false);
        }
    }

    private boolean enableVibrateIsSetForReminder(int level) {
        switch (level) {
            case 0:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vib_chk_key_0r), false);
            case 1:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vib_chk_key_1r), false);
            case 2:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vib_chk_key_2r), false);
            case 3:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vib_chk_key_3r), false);
            default:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vib_chk_key_0r), false);
        }
    }

    private boolean enableVibrateIsSetForPrompt(int level) {
        switch (level) {
            case 0:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vib_chk_key_0u), false);
            case 1:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vib_chk_key_1u), false);
            case 2:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vib_chk_key_2u), false);
            case 3:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vib_chk_key_3u), false);
            default:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vib_chk_key_0u), false);
        }
    }
    //--------------------------------------------------------------------------------------------->

    // No of Bursts ------------------------------------------------------------------------------->
    public int getNoOfVibBursts(int level, ReminderType type) {
        if (type == null) {
            return getNoOfVibBurstsForDefault(level);
        } else {
            switch (type) {
                case REMINDER:
                    return getNoOfVibBurstsForReminder(level);
                case PROMPT:
                    return getNoOfVibBurstsForPrompt(level);
                default:
                    return getNoOfVibBurstsForDefault(level);
            }
        }
    }

    private int getNoOfVibBurstsForDefault(int level) {
        switch (level) {
            case 0:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_no_key_0), "1"));
            case 1:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_no_key_1), "2"));
            case 2:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_no_key_2), "3"));
            case 3:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_no_key_3), "4"));
            default:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_no_key_0), "1"));
        }
    }

    private int getNoOfVibBurstsForReminder(int level) {
        switch (level) {
            case 0:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_no_key_0r), "1"));
            case 1:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_no_key_1r), "2"));
            case 2:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_no_key_2r), "3"));
            case 3:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_no_key_3r), "4"));
            default:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_no_key_0r), "1"));
        }
    }

    private int getNoOfVibBurstsForPrompt(int level) {
        switch (level) {
            case 0:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_no_key_0u), "1"));
            case 1:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_no_key_1u), "2"));
            case 2:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_no_key_2u), "3"));
            case 3:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_no_key_3u), "4"));
            default:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_no_key_0u), "1"));
        }
    }
    //--------------------------------------------------------------------------------------------->

    // Time Between Vib Bursts  ------------------------------------------------------------------->
    public int getTimeBetweenVibrationBursts(int level, ReminderType type) {
        if (type == null) {
            return getTimeBetweenVibBurstsForDefault(level);
        } else {
            switch (type) {
                case REMINDER:
                    return getTimeBetweenVibBurstsReminder(level);
                case PROMPT:
                    return getTimeBetweenVibBurstsForPrompt(level);
                default:
                    return getTimeBetweenVibBurstsForDefault(level);
            }
        }
    }

    private int getTimeBetweenVibBurstsForDefault(int level) {
        switch (level) {
            case 0:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_time_key_0), "4"));
            case 1:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_time_key_1), "3"));
            case 2:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_time_key_2), "2"));
            case 3:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_time_key_3), "1"));
            default:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_time_key_0), "4"));
        }
    }

    private int getTimeBetweenVibBurstsReminder(int level) {
        switch (level) {
            case 0:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_time_key_0r), "4"));
            case 1:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_time_key_1r), "3"));
            case 2:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_time_key_2r), "2"));
            case 3:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_time_key_3r), "1"));
            default:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_time_key_0r), "4"));
        }
    }

    private int getTimeBetweenVibBurstsForPrompt(int level) {
        switch (level) {
            case 0:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_time_key_0u), "4"));
            case 1:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_time_key_1u), "3"));
            case 2:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_time_key_2u), "2"));
            case 3:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_time_key_3u), "1"));
            default:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_burst_time_key_0u), "4"));
        }
    }
    //--------------------------------------------------------------------------------------------->

    // Vibrate Length ----------------------------------------------------------------------------->
    public float getVibrationLength(int level, ReminderType type) {
        if (type == null) {
            return getVibLengthForDefault(level);
        } else {
            switch (type) {
                case REMINDER:
                    return getVibLengthForReminder(level);
                case PROMPT:
                    return getVibLengthForPrompt(level);
                default:
                    return getVibLengthForDefault(level);
            }
        }
    }

    private float getVibLengthForDefault(int level) {
        switch (level) {
            case 0:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_length_key_0), "0.5"));
            case 1:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_length_key_1), "1.0"));
            case 2:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_length_key_2), "2.0"));
            case 3:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_length_key_3), "3.0"));
            default:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_length_key_0), "0.5"));
        }
    }

    private float getVibLengthForReminder(int level) {
        switch (level) {
            case 0:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_length_key_0r), "0.5"));
            case 1:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_length_key_1r), "1.0"));
            case 2:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_length_key_2r), "2.0"));
            case 3:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_length_key_3r), "3.0"));
            default:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_length_key_0r), "0.5"));
        }
    }

    private float getVibLengthForPrompt(int level) {
        switch (level) {
            case 0:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_length_key_0u), "0.5"));
            case 1:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_length_key_1u), "1.0"));
            case 2:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_length_key_2u), "2.0"));
            case 3:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_length_key_3u), "3.0"));
            default:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_length_key_0u), "0.5"));
        }
    }
    //--------------------------------------------------------------------------------------------->

    // Vibrate Repeating Set ---------------------------------------------------------------------->
    public boolean enableVibRepeatIsSet(int level, ReminderType type) {
        if (type == null) {
            return enableVibRepeatIsSetForDefault(level);
        } else {
            switch (type) {
                case REMINDER:
                    return enableVibRepeatIsSetForReminder(level);
                case PROMPT:
                    return enableVibRepeatIsSetForPrompt(level);
                default:
                    return enableVibRepeatIsSetForDefault(level);
            }
        }
    }

    private boolean enableVibRepeatIsSetForDefault(int level) {
        switch (level) {
            case 0:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_vib_repeat_key_0), false);
            case 1:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_vib_repeat_key_1), false);
            case 2:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_vib_repeat_key_2), false);
            case 3:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_vib_repeat_key_3), false);
            default:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_vib_repeat_key_0), false);
        }
    }

    private boolean enableVibRepeatIsSetForReminder(int level) {
        switch (level) {
            case 0:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_vib_repeat_key_0r), false);
            case 1:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_vib_repeat_key_1r), false);
            case 2:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_vib_repeat_key_2r), false);
            case 3:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_vib_repeat_key_3r), false);
            default:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_vib_repeat_key_0r), false);
        }
    }

    private boolean enableVibRepeatIsSetForPrompt(int level) {
        switch (level) {
            case 0:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_vib_repeat_key_0u), false);
            case 1:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_vib_repeat_key_1u), false);
            case 2:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_vib_repeat_key_2u), false);
            case 3:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_vib_repeat_key_3u), false);
            default:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_vib_repeat_key_0u), false);
        }
    }
    //--------------------------------------------------------------------------------------------->

    // Repeat Vib After Mins ---------------------------------------------------------------------->
    public float getVibrationRepeatAfterInMins(int level, ReminderType type) {
        if (type == null) {
            return getVibRepeatAfterForDefault(level);
        } else {
            switch (type) {
                case REMINDER:
                    return getVibRepeatAfterForReminder(level);
                case PROMPT:
                    return getVibRepeatAfterForPrompt(level);
                default:
                    return getVibRepeatAfterForDefault(level);
            }
        }
    }

    private float getVibRepeatAfterForDefault(int level) {
        switch (level) {
            case 0:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_time_key_0), "10.0"));
            case 1:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_time_key_1), "5.0"));
            case 2:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_time_key_2), "1.0"));
            case 3:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_time_key_3), "0.25"));
            default:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_time_key_0), "10.0"));
        }
    }

    private float getVibRepeatAfterForReminder(int level) {
        switch (level) {
            case 0:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_time_key_0r), "10.0"));
            case 1:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_time_key_1r), "5.0"));
            case 2:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_time_key_2r), "1.0"));
            case 3:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_time_key_3r), "0.25"));
            default:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_time_key_0r), "10.0"));
        }
    }

    private float getVibRepeatAfterForPrompt(int level) {
        switch (level) {
            case 0:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_time_key_0u), "10.0"));
            case 1:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_time_key_1u), "5.0"));
            case 2:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_time_key_2u), "1.0"));
            case 3:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_time_key_3u), "0.25"));
            default:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_time_key_0u), "10.0"));
        }
    }
    //--------------------------------------------------------------------------------------------->

    // Continuous Vibrate Repeat ------------------------------------------------------------------>
    public int getNoOfTimestoRepeat(int level, ReminderType type) {
        if (type == null) {
            return getNoOfTimestoRepeatForDefault(level);
        } else {
            switch (type) {
                case REMINDER:
                    return getNoOfTimestoRepeatForReminder(level);
                case PROMPT:
                    return getNoOfTimestoRepeatForPrompt(level);
                default:
                    return getNoOfTimestoRepeatForDefault(level);
            }
        }
    }

    private int getNoOfTimestoRepeatForDefault(int level) {
        switch (level) {
            case 0:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_0), "1"));
            case 1:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_1), "2"));
            case 2:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_2), "3"));
            case 3:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_3), "5"));
            default:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_0), "1"));
        }
    }

    private int getNoOfTimestoRepeatForReminder(int level) {
        switch (level) {
            case 0:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_0r), "1"));
            case 1:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_1r), "2"));
            case 2:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_2r), "3"));
            case 3:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_3r), "5"));
            default:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_0r), "1"));
        }
    }

    private int getNoOfTimestoRepeatForPrompt(int level) {
        switch (level) {
            case 0:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_0u), "1"));
            case 1:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_1u), "2"));
            case 2:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_2u), "3"));
            case 3:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_3u), "5"));
            default:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_0u), "1"));
        }
    }
    //--------------------------------------------------------------------------------------------->

    // -------------------------------------------------------------------------------------------//

    // SOUND -------------------------------------------------------------------------------------//

    // Sound Set ---------------------------------------------------------------------------------->
    public boolean enableSoundIsSet(int level, ReminderType type) {
        if (type == null) {
            return enableSoundIsSetForDefault(level);
        } else {
            switch (type) {
                case REMINDER:
                    return enableSoundIsSetForReminder(level);
                case PROMPT:
                    return enableSoundIsSetForPrompt(level);
                default:
                    return enableSoundIsSetForDefault(level);
            }
        }
    }

    private boolean enableSoundIsSetForDefault(int level) {
        switch (level) {
            case 0:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_sound_chk_key_0), false);
            case 1:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_sound_chk_key_1), false);
            case 2:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_sound_chk_key_2), false);
            case 3:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_sound_chk_key_3), false);
            default:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_sound_chk_key_0), false);
        }
    }

    private boolean enableSoundIsSetForReminder(int level) {
        switch (level) {
            case 0:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_sound_chk_key_0r), false);
            case 1:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_sound_chk_key_1r), false);
            case 2:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_sound_chk_key_2r), false);
            case 3:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_sound_chk_key_3r), false);
            default:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_sound_chk_key_0r), false);
        }
    }

    private boolean enableSoundIsSetForPrompt(int level) {
        switch (level) {
            case 0:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_sound_chk_key_0u), false);
            case 1:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_sound_chk_key_1u), false);
            case 2:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_sound_chk_key_2u), false);
            case 3:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_sound_chk_key_3u), false);
            default:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_sound_chk_key_0u), false);
        }
    }
    //--------------------------------------------------------------------------------------------->

    // Sound Choice ------------------------------------------------------------------------------->
    public String getSoundChoice(int level, ReminderType type) {
        if (type == null) {
            return getSoundChoiceForDefault(level);
        } else {
            switch (type) {
                case REMINDER:
                    return getSoundChoiceForReminder(level);
                case PROMPT:
                    return getSoundChoiceForPrompt(level);
                default:
                    return getSoundChoiceForDefault(level);
            }
        }
    }

    private String getSoundChoiceForDefault(int level) {
        switch (level) {
            case 0:
                return prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_choice_key_0), "pref_prompt_level_ringtone_choice_key_0");
            case 1:
                return prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_choice_key_1), "pref_prompt_level_ringtone_choice_key_0");
            case 2:
                return prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_choice_key_2), "pref_prompt_level_ringtone_choice_key_0");
            case 3:
                return prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_choice_key_3), "pref_prompt_level_ringtone_choice_key_0");
            default:
                return prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_choice_key_0), "pref_prompt_level_ringtone_choice_key_0");
        }
    }

    private String getSoundChoiceForReminder(int level) {
        switch (level) {
            case 0:
                return prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_choice_key_0r), "pref_prompt_level_ringtone_choice_key_0");
            case 1:
                return prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_choice_key_1r), "pref_prompt_level_ringtone_choice_key_0");
            case 2:
                return prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_choice_key_2r), "pref_prompt_level_ringtone_choice_key_0");
            case 3:
                return prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_choice_key_3r), "pref_prompt_level_ringtone_choice_key_0");
            default:
                return prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_choice_key_0r), "pref_prompt_level_ringtone_choice_key_0");
        }
    }

    private String getSoundChoiceForPrompt(int level) {
        switch (level) {
            case 0:
                return prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_choice_key_0u), "pref_prompt_level_ringtone_choice_key_0");
            case 1:
                return prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_choice_key_1u), "pref_prompt_level_ringtone_choice_key_0");
            case 2:
                return prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_choice_key_2u), "pref_prompt_level_ringtone_choice_key_0");
            case 3:
                return prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_choice_key_3u), "pref_prompt_level_ringtone_choice_key_0");
            default:
                return prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_choice_key_0u), "pref_prompt_level_ringtone_choice_key_0");
        }
    }
    //--------------------------------------------------------------------------------------------->

    // Sound Repeating ---------------------------------------------------------------------------->
    public boolean soundRepeatingIsSet(int level, ReminderType type) {
        if (type == null) {
            return getIsSoundRepeatingForDefault(level);
        } else {
            switch (type) {
                case REMINDER:
                    return getIsSoundRepeatingForReminder(level);
                case PROMPT:
                    return getIsSoundRepeatingForPrompt(level);
                default:
                    return getIsSoundRepeatingForDefault(level);
            }
        }
    }

    private boolean getIsSoundRepeatingForDefault(int level) {
        switch (level) {
            case 0:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_ringtone_repeat_key_0), false);
            case 1:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_ringtone_repeat_key_1), false);
            case 2:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_ringtone_repeat_key_2), false);
            case 3:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_ringtone_repeat_key_3), false);
            default:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_ringtone_repeat_key_0), false);
        }
    }

    private boolean getIsSoundRepeatingForReminder(int level) {
        switch (level) {
            case 0:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_ringtone_repeat_key_0r), false);
            case 1:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_ringtone_repeat_key_1r), false);
            case 2:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_ringtone_repeat_key_2r), false);
            case 3:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_ringtone_repeat_key_3r), false);
            default:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_ringtone_repeat_key_0r), false);
        }
    }

    private boolean getIsSoundRepeatingForPrompt(int level) {
        switch (level) {
            case 0:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_ringtone_repeat_key_0u), false);
            case 1:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_ringtone_repeat_key_1u), false);
            case 2:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_ringtone_repeat_key_2u), false);
            case 3:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_ringtone_repeat_key_3u), false);
            default:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_ringtone_repeat_key_0u), false);
        }
    }
    //--------------------------------------------------------------------------------------------->

    // Sound Repeat After ------------------------------------------------------------------------->
    public float getSoundRepeatAfterInMins(int level, ReminderType type) {
        if (type == null) {
            return getSoundRepeatAfterForDefault(level);
        } else {
            switch (type) {
                case REMINDER:
                    return getSoundRepeatAfterForReminder(level);
                case PROMPT:
                    return getSoundRepeatAfterForPrompt(level);
                default:
                    return getSoundRepeatAfterForDefault(level);
            }
        }
    }

    private float getSoundRepeatAfterForDefault(int level) {
        switch (level) {
            case 0:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_after_key_0), "10.0"));
            case 1:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_after_key_1), "5.0"));
            case 2:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_after_key_2), "1.0"));
            case 3:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_after_key_3), "0.25"));
            default:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_after_key_0), "10.0"));
        }
    }

    private float getSoundRepeatAfterForReminder(int level) {
        switch (level) {
            case 0:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_after_key_0r), "10.0"));
            case 1:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_after_key_1r), "5.0"));
            case 2:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_after_key_2r), "1.0"));
            case 3:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_after_key_3r), "0.25"));
            default:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_after_key_0r), "10.0"));
        }
    }

    private float getSoundRepeatAfterForPrompt(int level) {
        switch (level) {
            case 0:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_after_key_0u), "10.0"));
            case 1:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_after_key_1u), "5.0"));
            case 2:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_after_key_2u), "1.0"));
            case 3:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_after_key_3u), "0.25"));
            default:
                return Float.parseFloat(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_after_key_0u), "10.0"));
        }
    }
    //--------------------------------------------------------------------------------------------->

    // Sound Repeat No of Time -------------------------------------------------------------------->
    public int getNoOfTimesToRepeatSound(int level, ReminderType type) {
        if (type == null) {
            return getNoOfTimesToRepeatSoundForDefault(level);
        } else {
            switch (type) {
                case REMINDER:
                    return getNoOfTimesToRepeatSoundForReminder(level);
                case PROMPT:
                    return getNoOfTimesToRepeatSoundForPrompt(level);
                default:
                    return getNoOfTimesToRepeatSoundForDefault(level);
            }
        }
    }

    private int getNoOfTimesToRepeatSoundForDefault(int level) {
        switch (level) {
            case 0:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_no_key_0), "1"));
            case 1:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_no_key_1), "2"));
            case 2:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_no_key_2), "3"));
            case 3:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_no_key_3), "5"));
            default:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_no_key_0), "1"));
        }
    }

    private int getNoOfTimesToRepeatSoundForReminder(int level) {
        switch (level) {
            case 0:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_no_key_0r), "1"));
            case 1:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_no_key_1r), "2"));
            case 2:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_no_key_2r), "3"));
            case 3:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_no_key_3r), "5"));
            default:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_no_key_0r), "1"));
        }
    }

    private int getNoOfTimesToRepeatSoundForPrompt(int level) {
        switch (level) {
            case 0:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_no_key_0u), "1"));
            case 1:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_no_key_1u), "2"));
            case 2:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_no_key_2u), "3"));
            case 3:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_no_key_3u), "5"));
            default:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_ringtone_repeat_no_key_0u), "1"));
        }
    }
    //--------------------------------------------------------------------------------------------->

    // Sound Volume Level ------------------------------------------------------------------------->
    public int getVolumeLevel(int level, ReminderType type) {
        if (type == null) {
            return getVolumeLevelForDefault(level);
        } else {
            switch (type) {
                case REMINDER:
                    return getVolumeLevelForReminder(level);
                case PROMPT:
                    return getVolumeLevelForPrompt(level);
                default:
                    return getVolumeLevelForDefault(level);
            }
        }
    }

    private int getVolumeLevelForDefault(int level) {
        switch (level) {
            case 0:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_sound_volume_level_key_0), "0"));
            case 1:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_sound_volume_level_key_1), "0"));
            case 2:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_sound_volume_level_key_2), "1"));
            case 3:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_sound_volume_level_key_3), "2"));
            default:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_sound_volume_level_key_0), "0"));
        }
    }

    private int getVolumeLevelForReminder(int level) {
        switch (level) {
            case 0:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_sound_volume_level_key_0r), "0"));
            case 1:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_sound_volume_level_key_1r), "0"));
            case 2:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_sound_volume_level_key_2r), "1"));
            case 3:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_sound_volume_level_key_3r), "2"));
            default:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_sound_volume_level_key_0r), "0"));
        }
    }

    private int getVolumeLevelForPrompt(int level) {
        switch (level) {
            case 0:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_sound_volume_level_key_0u), "0"));
            case 1:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_sound_volume_level_key_1u), "0"));
            case 2:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_sound_volume_level_key_2u), "1"));
            case 3:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_sound_volume_level_key_3u), "2"));
            default:
                return Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_sound_volume_level_key_0u), "0"));
        }
    }
    //--------------------------------------------------------------------------------------------->

    // -------------------------------------------------------------------------------------------//

    // VISUAL ------------------------------------------------------------------------------------//

    // Visual Enabled ----------------------------------------------------------------------------->
    public boolean visualEnabledIsSet(int level, ReminderType type) {
        if (type == null) {
            return visualEnabledIsSetForDefault(level);
        } else {
            switch (type) {
                case REMINDER:
                    return visualEnabledIsSetForReminder(level);
                case PROMPT:
                    return visualEnabledIsSetForPrompt(level);
                default:
                    return visualEnabledIsSetForDefault(level);
            }
        }
    }

    private boolean visualEnabledIsSetForDefault(int level) {
        switch (level) {
            case 0:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vis_chk_key_0), false);
            case 1:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vis_chk_key_1), false);
            case 2:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vis_chk_key_2), false);
            case 3:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vis_chk_key_3), false);
            default:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vis_chk_key_0), false);
        }
    }

    private boolean visualEnabledIsSetForReminder(int level) {
        switch (level) {
            case 0:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vis_chk_key_0r), false);
            case 1:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vis_chk_key_1r), false);
            case 2:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vis_chk_key_2r), false);
            case 3:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vis_chk_key_3r), false);
            default:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vis_chk_key_0r), false);
        }
    }

    private boolean visualEnabledIsSetForPrompt(int level) {
        switch (level) {
            case 0:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vis_chk_key_0u), false);
            case 1:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vis_chk_key_1u), false);
            case 2:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vis_chk_key_2u), false);
            case 3:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vis_chk_key_3u), false);
            default:
                return prefs.getBoolean(context.getString(R.string.pref_prompt_level_enable_vis_chk_key_0u), false);
        }
    }
    //--------------------------------------------------------------------------------------------->

    // Visual Choice ------------------------------------------------------------------------------>
    public Consts.VisualChoice getVisualChoice(int level, ReminderType type) {
        if (type == null) {
            return getVisualChoiceForDefault(level);
        } else {
            switch (type) {
                case REMINDER:
                    return getVisualChoiceForReminder(level);
                case PROMPT:
                    return getVisualChoiceForPrompt(level);
                default:
                    return getVisualChoiceForDefault(level);
            }
        }
    }

    private Consts.VisualChoice getVisualChoiceForDefault(int level) {
        switch (level) {
            case 0: {
                int i = Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_visual_key_0), "0"));
                if (i == 1) {
                    return Consts.VisualChoice.FULLSCREEN;
                } else {
                    return Consts.VisualChoice.NOTIFICATION;
                }
            }
            case 1: {
                int i = Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_visual_key_1), "0"));
                if (i == 1) {
                    return Consts.VisualChoice.FULLSCREEN;
                } else {
                    return Consts.VisualChoice.NOTIFICATION;
                }
            }
            case 2: {
                int i = Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_visual_key_2), "0"));
                if (i == 1) {
                    return Consts.VisualChoice.FULLSCREEN;
                } else {
                    return Consts.VisualChoice.NOTIFICATION;
                }
            }
            case 3: {
                int i = Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_visual_key_3), "0"));
                if (i == 1) {
                    return Consts.VisualChoice.FULLSCREEN;
                } else {
                    return Consts.VisualChoice.NOTIFICATION;
                }
            }
            default: {
                int i = Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_visual_key_0), "0"));
                if (i == 1) {
                    return Consts.VisualChoice.FULLSCREEN;
                } else {
                    return Consts.VisualChoice.NOTIFICATION;
                }
            }
        }
    }

    private Consts.VisualChoice getVisualChoiceForReminder(int level) {
        switch (level) {
            case 0: {
                int i = Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_visual_key_0r), "0"));
                if (i == 1) {
                    return Consts.VisualChoice.FULLSCREEN;
                } else {
                    return Consts.VisualChoice.NOTIFICATION;
                }
            }
            case 1: {
                int i = Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_visual_key_1r), "0"));
                if (i == 1) {
                    return Consts.VisualChoice.FULLSCREEN;
                } else {
                    return Consts.VisualChoice.NOTIFICATION;
                }
            }
            case 2: {
                int i = Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_visual_key_2r), "0"));
                if (i == 1) {
                    return Consts.VisualChoice.FULLSCREEN;
                } else {
                    return Consts.VisualChoice.NOTIFICATION;
                }
            }
            case 3: {
                int i = Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_visual_key_3r), "0"));
                if (i == 1) {
                    return Consts.VisualChoice.FULLSCREEN;
                } else {
                    return Consts.VisualChoice.NOTIFICATION;
                }
            }
            default: {
                int i = Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_visual_key_0r), "0"));
                if (i == 1) {
                    return Consts.VisualChoice.FULLSCREEN;
                } else {
                    return Consts.VisualChoice.NOTIFICATION;
                }
            }
        }
    }

    private Consts.VisualChoice getVisualChoiceForPrompt(int level) {
        switch (level) {
            case 0: {
                int i = Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_visual_key_0u), "0"));
                if (i == 1) {
                    return Consts.VisualChoice.FULLSCREEN;
                } else {
                    return Consts.VisualChoice.NOTIFICATION;
                }
            }
            case 1: {
                int i = Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_visual_key_1u), "0"));
                if (i == 1) {
                    return Consts.VisualChoice.FULLSCREEN;
                } else {
                    return Consts.VisualChoice.NOTIFICATION;
                }
            }
            case 2: {
                int i = Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_visual_key_2u), "0"));
                if (i == 1) {
                    return Consts.VisualChoice.FULLSCREEN;
                } else {
                    return Consts.VisualChoice.NOTIFICATION;
                }
            }
            case 3: {
                int i = Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_visual_key_3u), "0"));
                if (i == 1) {
                    return Consts.VisualChoice.FULLSCREEN;
                } else {
                    return Consts.VisualChoice.NOTIFICATION;
                }
            }
            default: {
                int i = Integer.parseInt(prefs.getString(context.getString(R.string.pref_prompt_level_visual_key_0u), "0"));
                if (i == 1) {
                    return Consts.VisualChoice.FULLSCREEN;
                } else {
                    return Consts.VisualChoice.NOTIFICATION;
                }
            }
        }
    }
    //--------------------------------------------------------------------------------------------->

    // -------------------------------------------------------------------------------------------//

    // Watch
    public boolean sendToWatchSet(){
        int deviceChoice = Integer.parseInt(prefs.getString(context.getString(R.string.Pref_Prompt_Device_List), "0"));
        return deviceChoice == 1;
    }
}
