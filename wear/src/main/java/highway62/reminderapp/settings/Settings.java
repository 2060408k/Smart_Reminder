package highway62.reminderapp.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import highway62.reminderapp.R;
import highway62.reminderapp.constants.DurationScale;

/**
 * Created by Highway62 on 26/09/2016.
 */
public class Settings {

    private Context context;
    private SharedPreferences prefs;

    public Settings(Context context){
        this.context = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public int getQuickReminderTime(){
        return prefs.getInt(context.getString(R.string.quick_reminder_time), 5);
    }

    public DurationScale getQuickReminderScale(){
        return DurationScale.valueOf(prefs.getString(context.getString(R.string.quick_reminder_scale), "MINS"));
    }

    public void setQuickReminderTime(int time){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(context.getString(R.string.quick_reminder_time), time);
        editor.commit();
    }

    public void setQuickReminderScale(DurationScale scale){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.quick_reminder_scale), scale.name());
        editor.commit();
    }

    public int getQuickReminderIndex(){
        return prefs.getInt(context.getString(R.string.quick_reminder_index), 0);
    }

    public void setQuickReminderIndex(int index){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(context.getString(R.string.quick_reminder_index), index);
        editor.commit();
    }
}
