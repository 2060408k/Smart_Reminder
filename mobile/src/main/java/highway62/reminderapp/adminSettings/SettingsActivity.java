package highway62.reminderapp.adminSettings;


import android.annotation.TargetApi;
import android.app.Activity;
import org.joda.time.DateTime;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;
import android.provider.Settings.Secure;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import highway62.reminderapp.R;
import highway62.reminderapp.SmartReminding.SmartReminding;
import highway62.reminderapp.communication.CommunicationHandler;
import highway62.reminderapp.constants.EventType;
import highway62.reminderapp.constants.NotificationScale;
import highway62.reminderapp.constants.ReminderPattern;
import highway62.reminderapp.constants.ReminderType;
import highway62.reminderapp.reminderhandlers.ReminderHandler;
import highway62.reminderapp.reminders.BaseReminder;

public class SettingsActivity extends AppCompatPreferenceActivity {

    private boolean inFragment = false;

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
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_settings_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || UIPreferenceFragment.class.getName().equals(fragmentName)
                || PromptLevelPreferenceFragment.class.getName().equals(fragmentName)
                || UPPreferenceFragment.class.getName().equals(fragmentName)
                || LoggingOptionsFragment.class.getName().equals(fragmentName)
                || SmartRemindingFragment.class.getName().equals(fragmentName);
    }

    /////////////////////////////////// PREFERENCE FRAGMENTS ///////////////////////////////////////

    /**
     * This fragment shows the user interface preferences
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class UIPreferenceFragment extends PreferenceFragment {

        SettingsActivity parent = null;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_ui);
            setHasOptionsMenu(true);
            bindPreferenceSummaryToValue(findPreference(getString(R.string.Pref_UI_NDBS_List)));
            parent = (SettingsActivity) getActivity();
        }

        @Override
        public void onResume() {
            super.onResume();
            if (parent != null) {
                parent.setInFragment(true);
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                parent.finish();
                //startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onDestroyView() {
            if (parent != null) {
                parent.setInFragment(false);
            }
            super.onDestroyView();
        }
    }

    /**
     * This fragment shows unsolicited prompts preferences.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class UPPreferenceFragment extends PreferenceFragment {

        SettingsActivity parent = null;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_up);
            setHasOptionsMenu(true);
            parent = (SettingsActivity) getActivity();
        }

        @Override
        public void onResume() {
            super.onResume();
            if (parent != null) {
                parent.setInFragment(true);
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                parent.finish();
                //startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onDestroyView() {
            if (parent != null) {
                parent.setInFragment(false);
            }
            super.onDestroyView();
        }
    }

    /**
     * This fragment shows prompt level preferences
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class PromptLevelPreferenceFragment extends PreferenceFragment {

        SettingsActivity parent = null;
        Preference checkBox;
        SharedPreferences prefs;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_prompt_levels);
            setHasOptionsMenu(true);
            bindPreferenceSummaryToValue(findPreference(getString(R.string.Pref_Prompt_Device_List)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.Pref_Prompt_Default_User_Subtlety_Level)));
            parent = (SettingsActivity) getActivity();
            prefs = PreferenceManager.getDefaultSharedPreferences(parent);
        }

        @Override
        public void onResume() {
            super.onResume();

            // Notify context activity that fragment is loaded
            if (parent != null) {
                parent.setInFragment(true);
            }

            // Set up the checkbox value and enable/disable UP/reminder level prefs
            boolean checkBoxValue = prefs.getBoolean(getString(R.string.Pref_Prompt_Default_Levels_Checkbox), false);
            if (!checkBoxValue) {
                enablePromptPreferences();
            } else {
                disablePromptPreferences();
            }

            // Add Preference Changed Listener to Checkbox
            checkBox = this.findPreference(getString(R.string.Pref_Prompt_Default_Levels_Checkbox));
            if (checkBox != null) {
                checkBox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("true")) {
                            disablePromptPreferences();
                        } else {
                            enablePromptPreferences();
                        }
                        return true;
                    }
                });
            }
        }

        private void disablePromptPreferences() {
            findPreference(getString(R.string.Pref_Prompt_UP_Levels)).setEnabled(false);
            findPreference(getString(R.string.Pref_Prompt_Reminder_Levels)).setEnabled(false);
        }

        private void enablePromptPreferences() {
            findPreference(getString(R.string.Pref_Prompt_UP_Levels)).setEnabled(true);
            findPreference(getString(R.string.Pref_Prompt_Reminder_Levels)).setEnabled(true);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                parent.finish();
                //startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onDestroyView() {
            if (parent != null) {
                parent.setInFragment(false);
            }

            if (checkBox != null) {
                checkBox.setOnPreferenceChangeListener(null);
            }
            super.onDestroyView();
        }

    }

    /**
     * This fragment shows logging options.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class LoggingOptionsFragment extends PreferenceFragment {

        SettingsActivity parent = null;
        Preference viewCountPref;
        Preference printReminderPref;
        Preference printPromptPref;
        Preference printWatchPref;
        GoogleApiClient googleClient;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_logging);
            setHasOptionsMenu(true);
            parent = (SettingsActivity) getActivity();
        }

        @Override
        public void onResume() {
            super.onResume();
            if (parent != null) {
                parent.setInFragment(true);
            }

            viewCountPref = this.findPreference(getString(R.string.view_reminder_count_preference_key));
            if (viewCountPref != null) {
                viewCountPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        LogViewReminderCountDialog viewDialog = new LogViewReminderCountDialog(getActivity());
                        viewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        viewDialog.show();
                        return true;
                    }
                });
            }

            printReminderPref = this.findPreference(getString(R.string.print_reminder_log_key));
            if (printReminderPref != null) {
                printReminderPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        LogPrintDialog lpd = new LogPrintDialog(getActivity(), ReminderType.REMINDER);
                        lpd.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        lpd.show();
                        return true;
                    }
                });
            }

            printPromptPref = this.findPreference(getString(R.string.print_prompt_log_key));
            if (printPromptPref != null) {
                printPromptPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        LogPrintDialog lpd = new LogPrintDialog(getActivity(), ReminderType.PROMPT);
                        lpd.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        lpd.show();
                        return true;
                    }
                });
            }

            printWatchPref = this.findPreference(getString(R.string.print_watch_log_key));
            if (printWatchPref != null) {
                printWatchPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        new AlertDialog.Builder(getActivity())
                                .setMessage("Print Logs from the Watch?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        final CommunicationHandler comsHandler = new CommunicationHandler(getActivity());


                                        googleClient = new GoogleApiClient.Builder(getActivity())
                                                .addApi(Wearable.API)
                                                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                                                    @Override
                                                    public void onConnected(@Nullable Bundle bundle) {
                                                        Log.e("MSGCOMS", "Settings Activity, connected to googleclient, adding listener");
                                                        Wearable.DataApi.addListener(googleClient, comsHandler);
                                                    }

                                                    @Override
                                                    public void onConnectionSuspended(int i) {

                                                    }

                                                })
                                                .build();
                                        googleClient.connect();


                                        Log.e("MSGCOMS", "Settings Activity, calling sendLogReqToWatch");
                                        comsHandler.sendLogRequestToWatch();


                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        return true;
                    }
                });
            }
        }

        @Override
        public void onPause() {
            if(googleClient != null && googleClient.isConnected()){
                googleClient.disconnect();
                googleClient = null;
            }
            super.onPause();
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                parent.finish();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onDestroyView() {
            if (parent != null) {
                parent.setInFragment(false);
            }
            super.onDestroyView();
        }
    }

    public static class SmartRemindingFragment extends PreferenceFragment {
        Preference checkBox;
        Preference timeList;
        Preference textBox;
        private String android_id ;
        String name="Bob";



        Integer time;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.smart_remind);
            // Add Preference Changed Listener to Checkbox
            checkBox = this.findPreference(getString(R.string.pref_Smart_reminding));
            if (checkBox != null) {
                checkBox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("true")) {
                            //Start the smart reminding
                            if (time!=null) {
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean("smart_reminding", true);
                                editor.commit();
                                new SmartReminding(preference.getContext(), time).collect_and_set_reminder_suggestions();
                            }
                            else {

                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean("smart_reminding", true);
                                editor.commit();
                                new SmartReminding(preference.getContext()).collect_and_set_reminder_suggestions();
                            }
                            //Startup the server

                            //get database reference
                            DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();

                            //get android device's unique id
                            //get android device's unique id or name
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                            Boolean smart_login = sharedPreferences.getBoolean("smart_login",false);
                            String smart_login_name = sharedPreferences.getString("smart_login_name",null);
                            final String  android_id;
                            if (smart_login){
                                android_id=smart_login_name;
                            }else{
                                android_id= Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

                            }
                            mDatabase.child(android_id).child("smart_reminding").setValue(true);
                            mDatabase.child(android_id).child("prompts_accepted").setValue(0);
                            mDatabase.child(android_id).child("total_prompts").setValue(0);
                            mDatabase.child(android_id).child("weekly_prompts_accepted").setValue(0);
                            mDatabase.child(android_id).child("two_week_prompts_accepted").setValue(0);
                            mDatabase.child(android_id).child("monthly_prompts_accepted").setValue(0);
                            mDatabase.child(android_id).child("weekly_prompts").setValue(0);
                            mDatabase.child(android_id).child("two_week_prompts").setValue(0);
                            mDatabase.child(android_id).child("monthly_prompts").setValue(0);

                        } else {
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("smart_reminding", false);
                            editor.commit();
                            new SmartReminding(preference.getContext()).disable_smart_reminders();
                        }
                        return true;
                    }
                });
            }

            timeList= this.findPreference("smart_remind_time_periods");
            if (timeList != null) {
                timeList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        switch (newValue.toString()){
                            case "Morning" : time=7;
                            case "Lunch" : time=12;
                            case "Afternoon" : time=17;
                            case "Night" : time=22;
                            default: time=7;
                        }
                        return true;
                    }
                });
            }

            this.android_id=Secure.getString(getContext().getContentResolver(),Secure.ANDROID_ID);
            //Add change listener to textbox
            textBox = this.findPreference("login_name_preference");
            if (textBox != null) {
                textBox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        name=newValue.toString();
                        if (newValue.toString().equals("") || newValue.toString().equals(null)){
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("smart_login", false);
                            editor.putString("smart_login_name", null);
                            editor.commit();
                        }else {
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("smart_login", true);
                            editor.putString("smart_login_name", name);
                            editor.commit();
                        }
                        return true;
                    }
                });
            }


        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (inFragment) {
                return false;
            } else {
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (inFragment) {
            super.onBackPressed();
        } else {
            // Notify reminder activity of closing
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            Toast.makeText(this, "Settings Saved", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void setInFragment(boolean inFragment) {
        this.inFragment = inFragment;
    }
}
