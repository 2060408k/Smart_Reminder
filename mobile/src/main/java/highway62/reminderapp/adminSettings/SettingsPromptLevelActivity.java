package highway62.reminderapp.adminSettings;


import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import highway62.reminderapp.R;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsPromptLevelActivity extends AppCompatPreferenceActivity implements NestedSettingsCloser {

    private String ACTIVITY_TYPE = "DEFAULT";
    private String ACTIVITY_DEFAULT;
    private String ACTIVITY_UP;
    private String ACTIVITY_REMINDER;
    private FragmentTransaction ft;
    private FragmentManager fm;

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        ACTIVITY_TYPE = i.getStringExtra(getString(R.string.pref_prompt_modality_level_intent_key));
        ACTIVITY_DEFAULT = getString(R.string.pref_prompt_modality_level_intent_value_default);
        ACTIVITY_UP = getString(R.string.pref_prompt_modality_level_intent_value_UP);
        ACTIVITY_REMINDER = getString(R.string.pref_prompt_modality_level_intent_value_reminder);

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        fm = getFragmentManager();

        if(ACTIVITY_TYPE.equals(ACTIVITY_UP)){
            if(ab != null){
                ab.setTitle("Set UP Levels");
            }
            ft = fm.beginTransaction();
            ft.replace(android.R.id.content, new UPLevelsFragment()).commit();
        }else if(ACTIVITY_TYPE.equals(ACTIVITY_REMINDER)){
            if(ab != null){
                ab.setTitle("Set Reminder Levels");
            }
            ft = fm.beginTransaction();
            ft.replace(android.R.id.content, new ReminderLevelsFragment()).commit();
        }else{
            if(ab != null){
                ab.setTitle("Set Default Levels");
            }
            ft = fm.beginTransaction();
            ft.replace(android.R.id.content, new DefaultLevelsFragment()).commit();
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || DefaultLevelsFragment.class.getName().equals(fragmentName)
                || UPLevelsFragment.class.getName().equals(fragmentName)
                || ReminderLevelsFragment.class.getName().equals(fragmentName);
    }

    @Override
    public void closeNestedSettings() {
        fm.popBackStackImmediate();
        finish();
    }

    /**
     * This fragment shows settings for the Default Prompt Levels.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DefaultLevelsFragment extends PreferenceFragment {

        SettingsPromptLevelActivity parent = null;
        SharedPreferences prefs;

        Preference repeatingVibCheck0;
        Preference repeatingVibCheck1;
        Preference repeatingVibCheck2;
        Preference repeatingVibCheck3;

        Preference repeatingSoundCheck0;
        Preference repeatingSoundCheck1;
        Preference repeatingSoundCheck2;
        Preference repeatingSoundCheck3;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_prompt_set_levels_default);
            setHasOptionsMenu(true);
            parent = (SettingsPromptLevelActivity) getActivity();
            prefs = PreferenceManager.getDefaultSharedPreferences(parent);

            // Burst No
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_burst_no_key_0)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_burst_no_key_1)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_burst_no_key_2)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_burst_no_key_3)));

            // Burst Time
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_burst_time_key_0)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_burst_time_key_1)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_burst_time_key_2)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_burst_time_key_3)));

            // Vibration Length
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_length_key_0)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_length_key_1)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_length_key_2)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_length_key_3)));

            // Repeat Time
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_0)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_1)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_2)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_3)));

            // Repeat Vib No of Times
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_0)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_1)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_2)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_3)));

            // Sound Repeat After
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_0)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_1)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_2)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_3)));

            // Sound Repeat No of Times
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_0)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_1)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_2)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_3)));

            // Sound Volume
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_sound_volume_level_key_0)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_sound_volume_level_key_1)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_sound_volume_level_key_2)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_sound_volume_level_key_3)));

            // Notification
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_visual_key_0)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_visual_key_1)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_visual_key_2)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_visual_key_3)));
        }

        @Override
        public void onResume() {
            super.onResume();

            boolean vibRepeatCheckBox0 = prefs.getBoolean(getString(R.string.pref_prompt_level_vib_repeat_key_0), false);
            boolean vibRepeatCheckBox1 = prefs.getBoolean(getString(R.string.pref_prompt_level_vib_repeat_key_1), false);
            boolean vibRepeatCheckBox2 = prefs.getBoolean(getString(R.string.pref_prompt_level_vib_repeat_key_2), false);
            boolean vibRepeatCheckBox3 = prefs.getBoolean(getString(R.string.pref_prompt_level_vib_repeat_key_3), false);

            // Set up the checkbox value and enable/disable sound prefs
            boolean soundRepeatCheckBox0 = prefs.getBoolean(getString(R.string.pref_prompt_level_ringtone_repeat_key_0), false);
            boolean soundRepeatCheckBox1 = prefs.getBoolean(getString(R.string.pref_prompt_level_ringtone_repeat_key_1), false);
            boolean soundRepeatCheckBox2 = prefs.getBoolean(getString(R.string.pref_prompt_level_ringtone_repeat_key_2), false);
            boolean soundRepeatCheckBox3 = prefs.getBoolean(getString(R.string.pref_prompt_level_ringtone_repeat_key_3), false);


            if(!vibRepeatCheckBox0){
                disableVibPreferences0();
            }else{
                enableVibPreferences0();
            }

            if(!vibRepeatCheckBox1){
                disableVibPreferences1();
            }else{
                enableVibPreferences1();
            }

            if(!vibRepeatCheckBox2){
                disableVibPreferences2();
            }else{
                enableVibPreferences2();
            }

            if(!vibRepeatCheckBox3){
                disableVibPreferences3();
            }else{
                enableVibPreferences3();
            }

            if(!soundRepeatCheckBox0){
                disableSoundPreferences0();
            }else{
                enableSoundPreferences0();
            }

            if(!soundRepeatCheckBox1){
                disableSoundPreferences1();
            }else{
                enableSoundPreferences1();
            }

            if(!soundRepeatCheckBox2){
                disableSoundPreferences2();
            }else{
                enableSoundPreferences2();
            }

            if(!soundRepeatCheckBox3){
                disableSoundPreferences3();
            }else{
                enableSoundPreferences3();
            }

            repeatingVibCheck0 = findPreference(getString(R.string.pref_prompt_level_vib_repeat_key_0));
            if(repeatingVibCheck0 != null){
                repeatingVibCheck0.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("false")) {
                            disableVibPreferences0();
                        } else {
                            enableVibPreferences0();
                        }
                        return true;
                    }
                });
            }

            repeatingVibCheck1 = findPreference(getString(R.string.pref_prompt_level_vib_repeat_key_1));
            if(repeatingVibCheck1 != null){
                repeatingVibCheck1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("false")) {
                            disableVibPreferences1();
                        } else {
                            enableVibPreferences1();
                        }
                        return true;
                    }
                });
            }
            repeatingVibCheck2 = findPreference(getString(R.string.pref_prompt_level_vib_repeat_key_2));
            if(repeatingVibCheck2 != null){
                repeatingVibCheck2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("false")) {
                            disableVibPreferences2();
                        } else {
                            enableVibPreferences2();
                        }
                        return true;
                    }
                });
            }

            repeatingVibCheck3 = findPreference(getString(R.string.pref_prompt_level_vib_repeat_key_3));
            if(repeatingVibCheck3 != null){
                repeatingVibCheck3.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("false")) {
                            disableVibPreferences3();
                        } else {
                            enableVibPreferences3();
                        }
                        return true;
                    }
                });
            }

            repeatingSoundCheck0 = findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_key_0));
            if(repeatingSoundCheck0 != null){
                repeatingSoundCheck0.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("false")) {
                            disableSoundPreferences0();
                        } else {
                            enableSoundPreferences0();
                        }
                        return true;
                    }
                });
            }

            repeatingSoundCheck1 = findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_key_1));
            if(repeatingSoundCheck1 != null){
                repeatingSoundCheck1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("false")) {
                            disableSoundPreferences1();
                        } else {
                            enableSoundPreferences1();
                        }
                        return true;
                    }
                });
            }

            repeatingSoundCheck2 = findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_key_2));
            if(repeatingSoundCheck2 != null){
                repeatingSoundCheck2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("false")) {
                            disableSoundPreferences2();
                        } else {
                            enableSoundPreferences2();
                        }
                        return true;
                    }
                });
            }

            repeatingSoundCheck3 = findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_key_3));
            if(repeatingSoundCheck3 != null){
                repeatingSoundCheck3.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("false")) {
                            disableSoundPreferences3();
                        } else {
                            enableSoundPreferences3();
                        }
                        return true;
                    }
                });
            }
        }

        private void disableSoundPreferences0(){
            findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_0)).setEnabled(false);
            findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_0)).setEnabled(false);
        }

        private void disableSoundPreferences1(){
            findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_1)).setEnabled(false);
            findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_1)).setEnabled(false);
        }

        private void disableSoundPreferences2(){
            findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_2)).setEnabled(false);
            findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_2)).setEnabled(false);
        }

        private void disableSoundPreferences3(){
            findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_3)).setEnabled(false);
            findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_3)).setEnabled(false);
        }

        private void enableSoundPreferences0(){
            if(prefs.getBoolean(getString(R.string.pref_prompt_level_enable_sound_chk_key_0), false)){
                findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_0)).setEnabled(true);
                findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_0)).setEnabled(true);
            }
        }

        private void enableSoundPreferences1(){
            if(prefs.getBoolean(getString(R.string.pref_prompt_level_enable_sound_chk_key_1), false)){
                findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_1)).setEnabled(true);
                findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_1)).setEnabled(true);
            }
        }

        private void enableSoundPreferences2(){
            if(prefs.getBoolean(getString(R.string.pref_prompt_level_enable_sound_chk_key_2), false)){
                findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_2)).setEnabled(true);
                findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_2)).setEnabled(true);
            }
        }

        private void enableSoundPreferences3(){
            if(prefs.getBoolean(getString(R.string.pref_prompt_level_enable_sound_chk_key_3), false)){
                findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_3)).setEnabled(true);
                findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_3)).setEnabled(true);
            }
        }

        private void disableVibPreferences0(){
            findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_0)).setEnabled(false);
            findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_0)).setEnabled(false);
        }

        private void disableVibPreferences1(){
            findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_1)).setEnabled(false);
            findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_1)).setEnabled(false);
        }

        private void disableVibPreferences2(){
            findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_2)).setEnabled(false);
            findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_2)).setEnabled(false);
        }

        private void disableVibPreferences3(){
            findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_3)).setEnabled(false);
            findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_3)).setEnabled(false);
        }

        private void enableVibPreferences0(){
            if(prefs.getBoolean(getString(R.string.pref_prompt_level_enable_vib_chk_key_0), false)){
                findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_0)).setEnabled(true);
                findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_0)).setEnabled(true);
            }
        }

        private void enableVibPreferences1(){
            if(prefs.getBoolean(getString(R.string.pref_prompt_level_enable_vib_chk_key_1), false)){
                findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_1)).setEnabled(true);
                findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_1)).setEnabled(true);
            }
        }

        private void enableVibPreferences2(){
            if(prefs.getBoolean(getString(R.string.pref_prompt_level_enable_vib_chk_key_2), false)){
                findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_2)).setEnabled(true);
                findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_2)).setEnabled(true);
            }
        }

        private void enableVibPreferences3(){
            if(prefs.getBoolean(getString(R.string.pref_prompt_level_enable_vib_chk_key_3), false)){
                findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_3)).setEnabled(true);
                findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_3)).setEnabled(true);
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                NestedSettingsCloser context = (NestedSettingsCloser) getActivity();
                context.closeNestedSettings();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows settings for the Unsolicited Prompt Levels.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class UPLevelsFragment extends PreferenceFragment {

        SettingsPromptLevelActivity parent = null;
        SharedPreferences prefs;

        Preference repeatingVibCheck0;
        Preference repeatingVibCheck1;
        Preference repeatingVibCheck2;
        Preference repeatingVibCheck3;

        Preference repeatingSoundCheck0;
        Preference repeatingSoundCheck1;
        Preference repeatingSoundCheck2;
        Preference repeatingSoundCheck3;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_prompt_set_levels_up);
            setHasOptionsMenu(true);
            parent = (SettingsPromptLevelActivity) getActivity();
            prefs = PreferenceManager.getDefaultSharedPreferences(parent);

            // Burst No
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_burst_no_key_0u)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_burst_no_key_1u)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_burst_no_key_2u)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_burst_no_key_3u)));

            // Burst Time
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_burst_time_key_0u)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_burst_time_key_1u)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_burst_time_key_2u)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_burst_time_key_3u)));

            // Vibration Length
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_length_key_0u)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_length_key_1u)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_length_key_2u)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_length_key_3u)));

            // Repeat Time
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_0u)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_1u)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_2u)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_3u)));

            // Repeat No of Times
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_0u)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_1u)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_2u)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_3u)));

            // Sound Repeat After
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_0u)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_1u)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_2u)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_3u)));

            // Sound Repeat No of Times
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_0u)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_1u)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_2u)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_3u)));

            // Sound Volume
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_sound_volume_level_key_0u)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_sound_volume_level_key_1u)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_sound_volume_level_key_2u)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_sound_volume_level_key_3u)));

            // Notification
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_visual_key_0u)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_visual_key_1u)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_visual_key_2u)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_visual_key_3u)));
        }

        @Override
        public void onResume() {
            super.onResume();

            boolean vibRepeatCheckBox0 = prefs.getBoolean(getString(R.string.pref_prompt_level_vib_repeat_key_0u), false);
            boolean vibRepeatCheckBox1 = prefs.getBoolean(getString(R.string.pref_prompt_level_vib_repeat_key_1u), false);
            boolean vibRepeatCheckBox2 = prefs.getBoolean(getString(R.string.pref_prompt_level_vib_repeat_key_2u), false);
            boolean vibRepeatCheckBox3 = prefs.getBoolean(getString(R.string.pref_prompt_level_vib_repeat_key_3u), false);

            // Set up the checkbox value and enable/disable sound prefs
            boolean soundRepeatCheckBox0 = prefs.getBoolean(getString(R.string.pref_prompt_level_ringtone_repeat_key_0u), false);
            boolean soundRepeatCheckBox1 = prefs.getBoolean(getString(R.string.pref_prompt_level_ringtone_repeat_key_1u), false);
            boolean soundRepeatCheckBox2 = prefs.getBoolean(getString(R.string.pref_prompt_level_ringtone_repeat_key_2u), false);
            boolean soundRepeatCheckBox3 = prefs.getBoolean(getString(R.string.pref_prompt_level_ringtone_repeat_key_3u), false);

            if(!vibRepeatCheckBox0){
                disableVibPreferences0();
            }else{
                enableVibPreferences0();
            }

            if(!vibRepeatCheckBox1){
                disableVibPreferences1();
            }else{
                enableVibPreferences1();
            }

            if(!vibRepeatCheckBox2){
                disableVibPreferences2();
            }else{
                enableVibPreferences2();
            }

            if(!vibRepeatCheckBox3){
                disableVibPreferences3();
            }else{
                enableVibPreferences3();
            }

            if(!soundRepeatCheckBox0){
                disableSoundPreferences0();
            }else{
                enableSoundPreferences0();
            }

            if(!soundRepeatCheckBox1){
                disableSoundPreferences1();
            }else{
                enableSoundPreferences1();
            }

            if(!soundRepeatCheckBox2){
                disableSoundPreferences2();
            }else{
                enableSoundPreferences2();
            }

            if(!soundRepeatCheckBox3){
                disableSoundPreferences3();
            }else{
                enableSoundPreferences3();
            }

            repeatingVibCheck0 = findPreference(getString(R.string.pref_prompt_level_vib_repeat_key_0u));
            if(repeatingVibCheck0 != null){
                repeatingVibCheck0.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("false")) {
                            disableVibPreferences0();
                        } else {
                            enableVibPreferences0();
                        }
                        return true;
                    }
                });
            }

            repeatingVibCheck1 = findPreference(getString(R.string.pref_prompt_level_vib_repeat_key_1u));
            if(repeatingVibCheck1 != null){
                repeatingVibCheck1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("false")) {
                            disableVibPreferences1();
                        } else {
                            enableVibPreferences1();
                        }
                        return true;
                    }
                });
            }
            repeatingVibCheck2 = findPreference(getString(R.string.pref_prompt_level_vib_repeat_key_2u));
            if(repeatingVibCheck2 != null){
                repeatingVibCheck2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("false")) {
                            disableVibPreferences2();
                        } else {
                            enableVibPreferences2();
                        }
                        return true;
                    }
                });
            }

            repeatingVibCheck3 = findPreference(getString(R.string.pref_prompt_level_vib_repeat_key_3u));
            if(repeatingVibCheck3 != null){
                repeatingVibCheck3.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("false")) {
                            disableVibPreferences3();
                        } else {
                            enableVibPreferences3();
                        }
                        return true;
                    }
                });
            }

            repeatingSoundCheck0 = findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_key_0u));
            if(repeatingSoundCheck0 != null){
                repeatingSoundCheck0.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("false")) {
                            disableSoundPreferences0();
                        } else {
                            enableSoundPreferences0();
                        }
                        return true;
                    }
                });
            }

            repeatingSoundCheck1 = findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_key_1u));
            if(repeatingSoundCheck1 != null){
                repeatingSoundCheck1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("false")) {
                            disableSoundPreferences1();
                        } else {
                            enableSoundPreferences1();
                        }
                        return true;
                    }
                });
            }

            repeatingSoundCheck2 = findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_key_2u));
            if(repeatingSoundCheck2 != null){
                repeatingSoundCheck2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("false")) {
                            disableSoundPreferences2();
                        } else {
                            enableSoundPreferences2();
                        }
                        return true;
                    }
                });
            }

            repeatingSoundCheck3 = findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_key_3u));
            if(repeatingSoundCheck3 != null){
                repeatingSoundCheck3.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("false")) {
                            disableSoundPreferences3();
                        } else {
                            enableSoundPreferences3();
                        }
                        return true;
                    }
                });
            }
        }

        private void disableSoundPreferences0(){
            findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_0u)).setEnabled(false);
            findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_0u)).setEnabled(false);
        }

        private void disableSoundPreferences1(){
            findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_1u)).setEnabled(false);
            findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_1u)).setEnabled(false);
        }

        private void disableSoundPreferences2(){
            findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_2u)).setEnabled(false);
            findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_2u)).setEnabled(false);
        }

        private void disableSoundPreferences3(){
            findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_3u)).setEnabled(false);
            findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_3u)).setEnabled(false);
        }

        private void enableSoundPreferences0(){
            if(prefs.getBoolean(getString(R.string.pref_prompt_level_enable_sound_chk_key_0u), false)){
                findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_0u)).setEnabled(true);
                findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_0u)).setEnabled(true);
            }
        }

        private void enableSoundPreferences1(){
            if(prefs.getBoolean(getString(R.string.pref_prompt_level_enable_sound_chk_key_1u), false)){
                findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_1u)).setEnabled(true);
                findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_1u)).setEnabled(true);
            }
        }

        private void enableSoundPreferences2(){
            if(prefs.getBoolean(getString(R.string.pref_prompt_level_enable_sound_chk_key_2u), false)){
                findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_2u)).setEnabled(true);
                findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_2u)).setEnabled(true);
            }
        }

        private void enableSoundPreferences3(){
            if(prefs.getBoolean(getString(R.string.pref_prompt_level_enable_sound_chk_key_3u), false)){
                findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_3u)).setEnabled(true);
                findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_3u)).setEnabled(true);
            }
        }

        private void disableVibPreferences0(){
            findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_0u)).setEnabled(false);
            findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_0u)).setEnabled(false);
        }

        private void disableVibPreferences1(){
            findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_1u)).setEnabled(false);
            findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_1u)).setEnabled(false);
        }

        private void disableVibPreferences2(){
            findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_2u)).setEnabled(false);
            findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_2u)).setEnabled(false);
        }

        private void disableVibPreferences3(){
            findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_3u)).setEnabled(false);
            findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_3u)).setEnabled(false);
        }

        private void enableVibPreferences0(){
            if(prefs.getBoolean(getString(R.string.pref_prompt_level_enable_vib_chk_key_0u), false)){
                findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_0u)).setEnabled(true);
                findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_0u)).setEnabled(true);
            }
        }

        private void enableVibPreferences1(){
            if(prefs.getBoolean(getString(R.string.pref_prompt_level_enable_vib_chk_key_1u), false)){
                findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_1u)).setEnabled(true);
                findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_1u)).setEnabled(true);
            }
        }

        private void enableVibPreferences2(){
            if(prefs.getBoolean(getString(R.string.pref_prompt_level_enable_vib_chk_key_2u), false)){
                findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_2u)).setEnabled(true);
                findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_2u)).setEnabled(true);
            }
        }

        private void enableVibPreferences3(){
            if(prefs.getBoolean(getString(R.string.pref_prompt_level_enable_vib_chk_key_3u), false)){
                findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_3u)).setEnabled(true);
                findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_3u)).setEnabled(true);
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                NestedSettingsCloser context = (NestedSettingsCloser) getActivity();
                context.closeNestedSettings();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows settings for the Reminder Prompt Levels.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ReminderLevelsFragment extends PreferenceFragment {

        SettingsPromptLevelActivity parent = null;
        SharedPreferences prefs;

        Preference repeatingVibCheck0;
        Preference repeatingVibCheck1;
        Preference repeatingVibCheck2;
        Preference repeatingVibCheck3;

        Preference repeatingSoundCheck0;
        Preference repeatingSoundCheck1;
        Preference repeatingSoundCheck2;
        Preference repeatingSoundCheck3;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_prompt_set_levels_reminder);
            setHasOptionsMenu(true);
            parent = (SettingsPromptLevelActivity) getActivity();
            prefs = PreferenceManager.getDefaultSharedPreferences(parent);

            // Burst No
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_burst_no_key_0r)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_burst_no_key_1r)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_burst_no_key_2r)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_burst_no_key_3r)));

            // Burst Time
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_burst_time_key_0r)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_burst_time_key_1r)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_burst_time_key_2r)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_burst_time_key_3r)));

            // Vibration Length
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_length_key_0r)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_length_key_1r)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_length_key_2r)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_length_key_3r)));

            // Repeat Time
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_0r)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_1r)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_2r)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_3r)));

            // Repeat No of Times
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_0r)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_1r)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_2r)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_3r)));

            // Sound Repeat After
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_0r)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_1r)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_2r)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_3r)));

            // Sound Repeat No of Times
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_0r)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_1r)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_2r)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_3r)));

            // Sound Volume
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_sound_volume_level_key_0r)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_sound_volume_level_key_1r)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_sound_volume_level_key_2r)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_sound_volume_level_key_3r)));

            // Notification
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_visual_key_0r)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_visual_key_1r)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_visual_key_2r)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_prompt_level_visual_key_3r)));
        }

        @Override
        public void onResume() {
            super.onResume();

            boolean vibRepeatCheckBox0 = prefs.getBoolean(getString(R.string.pref_prompt_level_vib_repeat_key_0r), false);
            boolean vibRepeatCheckBox1 = prefs.getBoolean(getString(R.string.pref_prompt_level_vib_repeat_key_1r), false);
            boolean vibRepeatCheckBox2 = prefs.getBoolean(getString(R.string.pref_prompt_level_vib_repeat_key_2r), false);
            boolean vibRepeatCheckBox3 = prefs.getBoolean(getString(R.string.pref_prompt_level_vib_repeat_key_3r), false);

            // Set up the checkbox value and enable/disable sound prefs
            boolean soundRepeatCheckBox0 = prefs.getBoolean(getString(R.string.pref_prompt_level_ringtone_repeat_key_0r), false);
            boolean soundRepeatCheckBox1 = prefs.getBoolean(getString(R.string.pref_prompt_level_ringtone_repeat_key_1r), false);
            boolean soundRepeatCheckBox2 = prefs.getBoolean(getString(R.string.pref_prompt_level_ringtone_repeat_key_2r), false);
            boolean soundRepeatCheckBox3 = prefs.getBoolean(getString(R.string.pref_prompt_level_ringtone_repeat_key_3r), false);

            if(!vibRepeatCheckBox0){
                disableVibPreferences0();
            }else{
                enableVibPreferences0();
            }

            if(!vibRepeatCheckBox1){
                disableVibPreferences1();
            }else{
                enableVibPreferences1();
            }

            if(!vibRepeatCheckBox2){
                disableVibPreferences2();
            }else{
                enableVibPreferences2();
            }

            if(!vibRepeatCheckBox3){
                disableVibPreferences3();
            }else{
                enableVibPreferences3();
            }

            if(!soundRepeatCheckBox0){
                disableSoundPreferences0();
            }else{
                enableSoundPreferences0();
            }

            if(!soundRepeatCheckBox1){
                disableSoundPreferences1();
            }else{
                enableSoundPreferences1();
            }

            if(!soundRepeatCheckBox2){
                disableSoundPreferences2();
            }else{
                enableSoundPreferences2();
            }

            if(!soundRepeatCheckBox3){
                disableSoundPreferences3();
            }else{
                enableSoundPreferences3();
            }

            repeatingVibCheck0 = findPreference(getString(R.string.pref_prompt_level_vib_repeat_key_0r));
            if(repeatingVibCheck0 != null){
                repeatingVibCheck0.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("false")) {
                            disableVibPreferences0();
                        } else {
                            enableVibPreferences0();
                        }
                        return true;
                    }
                });
            }

            repeatingVibCheck1 = findPreference(getString(R.string.pref_prompt_level_vib_repeat_key_1r));
            if(repeatingVibCheck1 != null){
                repeatingVibCheck1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("false")) {
                            disableVibPreferences1();
                        } else {
                            enableVibPreferences1();
                        }
                        return true;
                    }
                });
            }
            repeatingVibCheck2 = findPreference(getString(R.string.pref_prompt_level_vib_repeat_key_2r));
            if(repeatingVibCheck2 != null){
                repeatingVibCheck2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("false")) {
                            disableVibPreferences2();
                        } else {
                            enableVibPreferences2();
                        }
                        return true;
                    }
                });
            }

            repeatingVibCheck3 = findPreference(getString(R.string.pref_prompt_level_vib_repeat_key_3r));
            if(repeatingVibCheck3 != null){
                repeatingVibCheck3.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("false")) {
                            disableVibPreferences3();
                        } else {
                            enableVibPreferences3();
                        }
                        return true;
                    }
                });
            }

            repeatingSoundCheck0 = findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_key_0r));
            if(repeatingSoundCheck0 != null){
                repeatingSoundCheck0.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("false")) {
                            disableSoundPreferences0();
                        } else {
                            enableSoundPreferences0();
                        }
                        return true;
                    }
                });
            }

            repeatingSoundCheck1 = findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_key_1r));
            if(repeatingSoundCheck1 != null){
                repeatingSoundCheck1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("false")) {
                            disableSoundPreferences1();
                        } else {
                            enableSoundPreferences1();
                        }
                        return true;
                    }
                });
            }

            repeatingSoundCheck2 = findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_key_2r));
            if(repeatingSoundCheck2 != null){
                repeatingSoundCheck2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("false")) {
                            disableSoundPreferences2();
                        } else {
                            enableSoundPreferences2();
                        }
                        return true;
                    }
                });
            }

            repeatingSoundCheck3 = findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_key_3r));
            if(repeatingSoundCheck3 != null){
                repeatingSoundCheck3.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("false")) {
                            disableSoundPreferences3();
                        } else {
                            enableSoundPreferences3();
                        }
                        return true;
                    }
                });
            }
        }

        private void disableSoundPreferences0(){
            findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_0r)).setEnabled(false);
            findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_0r)).setEnabled(false);
        }

        private void disableSoundPreferences1(){
            findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_1r)).setEnabled(false);
            findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_1r)).setEnabled(false);
        }

        private void disableSoundPreferences2(){
            findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_2r)).setEnabled(false);
            findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_2r)).setEnabled(false);
        }

        private void disableSoundPreferences3(){
            findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_3r)).setEnabled(false);
            findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_3r)).setEnabled(false);
        }

        private void enableSoundPreferences0(){
            if(prefs.getBoolean(getString(R.string.pref_prompt_level_enable_sound_chk_key_0r), false)){
                findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_0r)).setEnabled(true);
                findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_0r)).setEnabled(true);
            }
        }

        private void enableSoundPreferences1(){
            if(prefs.getBoolean(getString(R.string.pref_prompt_level_enable_sound_chk_key_1r), false)){
                findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_1r)).setEnabled(true);
                findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_1r)).setEnabled(true);
            }
        }

        private void enableSoundPreferences2(){
            if(prefs.getBoolean(getString(R.string.pref_prompt_level_enable_sound_chk_key_2r), false)){
                findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_2r)).setEnabled(true);
                findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_2r)).setEnabled(true);
            }
        }

        private void enableSoundPreferences3(){
            if(prefs.getBoolean(getString(R.string.pref_prompt_level_enable_sound_chk_key_3r), false)){
                findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_after_key_3r)).setEnabled(true);
                findPreference(getString(R.string.pref_prompt_level_ringtone_repeat_no_key_3r)).setEnabled(true);
            }
        }

        private void disableVibPreferences0(){
            findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_0r)).setEnabled(false);
            findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_0r)).setEnabled(false);
        }

        private void disableVibPreferences1(){
            findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_1r)).setEnabled(false);
            findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_1r)).setEnabled(false);
        }

        private void disableVibPreferences2(){
            findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_2r)).setEnabled(false);
            findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_2r)).setEnabled(false);
        }

        private void disableVibPreferences3(){
            findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_3r)).setEnabled(false);
            findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_3r)).setEnabled(false);
        }

        private void enableVibPreferences0(){
            if(prefs.getBoolean(getString(R.string.pref_prompt_level_enable_vib_chk_key_0r), false)){
                findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_0r)).setEnabled(true);
                findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_0r)).setEnabled(true);
            }
        }

        private void enableVibPreferences1(){
            if(prefs.getBoolean(getString(R.string.pref_prompt_level_enable_vib_chk_key_1r), false)){
                findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_1r)).setEnabled(true);
                findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_1r)).setEnabled(true);
            }
        }

        private void enableVibPreferences2(){
            if(prefs.getBoolean(getString(R.string.pref_prompt_level_enable_vib_chk_key_2r), false)){
                findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_2r)).setEnabled(true);
                findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_2r)).setEnabled(true);
            }
        }

        private void enableVibPreferences3(){
            if(prefs.getBoolean(getString(R.string.pref_prompt_level_enable_vib_chk_key_3r), false)){
                findPreference(getString(R.string.pref_prompt_level_vib_repeat_time_key_3r)).setEnabled(true);
                findPreference(getString(R.string.pref_prompt_level_vib_repeat_cont_chk_key_3r)).setEnabled(true);
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                NestedSettingsCloser context = (NestedSettingsCloser) getActivity();
                context.closeNestedSettings();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
