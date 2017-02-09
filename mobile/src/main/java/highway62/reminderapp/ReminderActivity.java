package highway62.reminderapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.HashMap;

import highway62.reminderapp.SmartReminding.SuggestionTab;
import highway62.reminderapp.actionbarlisteners.OnHomeListener;
import highway62.reminderapp.adminSettings.SettingsActivity;
import highway62.reminderapp.adminSettings.SettingsInterface;
import highway62.reminderapp.adminSettings.SettingsPasswordDialog;
import highway62.reminderapp.adminSettings.SettingsPasswordListener;
import highway62.reminderapp.constants.EventType;
import highway62.reminderapp.constants.HandlerType;
import highway62.reminderapp.constants.ReminderPattern;
import highway62.reminderapp.constants.ReminderType;
import highway62.reminderapp.daos.ReminderDAO;
import highway62.reminderapp.fontSpan.TypefaceSpan;
import highway62.reminderapp.fragmentHandlers.BSFragmentHandler;
import highway62.reminderapp.fragmentHandlers.DecisionTreeListener;
import highway62.reminderapp.fragmentHandlers.NDFragmentHandler;
import highway62.reminderapp.fragments.BSDTIntroFragment;
import highway62.reminderapp.fragments.NDDTIntroFragment;
import highway62.reminderapp.fragments.SuggestionTabFragment;
import highway62.reminderapp.reminderhandlers.ReminderHandler;
import highway62.reminderapp.reminders.BaseReminder;
import highway62.reminderapp.tablisteners.TabListener;


public class ReminderActivity extends AppCompatActivity implements DecisionTreeListener, SuggestionTabFragment.OnFragmentInteractionListener {

    SettingsInterface settingsSingleton;
    FragmentManager fm;
    FragmentTransaction ft;
    android.support.v7.app.ActionBar actionBar;
    TabHost mTabHost;
    ReminderDAO reminderDAO;
    OnHomeListener fraghandlerHomeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("ReminderActivity started");
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            Intent intent =getIntent();
            if(extras == null)
            {
                System.out.println("//Cry about not being clicked on");
            }
            if (extras!=null && extras.getBoolean("NotiClick") && (ReminderPattern) intent.getSerializableExtra("pattern")!=null)
            {
                sendToServerNotificationFeedback((ReminderPattern) intent.getSerializableExtra("pattern"));
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        fm = getFragmentManager();
        actionBar = getSupportActionBar();
        settingsSingleton = new SettingsInterface(this);
        setupFragHandlerHomeListener();
        setupTabs();
        setupActionBar();
        chooseUI();
        createDB();

        // Initialise Joda-Time
        JodaTimeAndroid.init(this);

        // Request permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.VIBRATE,
                    android.Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE,
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WAKE_LOCK}, 1);
        }

    }

    private void setupActionBar() {
        if (actionBar != null) {
            SpannableString s = new SpannableString(getString(R.string.app_name));
            s.setSpan(new TypefaceSpan(this, "adam.otf"), 0, s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            actionBar.setTitle(s);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setElevation(0);
        }
    }

    private void setupTabs() {
        mTabHost = (TabHost) findViewById(R.id.mainActivityTabHost);
        mTabHost.setup();
        mTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider); // Adds Divider
        addTab(getString(R.string.reminder_tab_text), R.drawable.ic_tab_reminder, R.id.tabReminderContainer);
        addTab(getString(R.string.schedule_tab_text), R.drawable.ic_tab_schedule, R.id.tabScheduleContainer);
        addTab("Suggestions",R.drawable.quantum_ic_art_track_grey600_48,R.id.tabSuggestionContainer);
//        TabHost.TabSpec spec; // Reusable TabSpec for each tab
//        Intent intent; // Reusable Intent for each tab
//        spec = mTabHost.newTabSpec("Suggestions"); // Create a new TabSpec using tab host
//        spec.setIndicator("Suggestions"); // set the “HOME” as an indicator
//        View tabview = createTabView(mTabHost.getContext(),"Suggestions",R.drawable.quantum_ic_art_track_grey600_48);
//        spec.setIndicator(tabview);
//        // Create an Intent to launch an Activity for the tab (to be reused)
//        intent = new Intent(this, SuggestionTab.class);
//        spec.setContent(intent);
//        TabHost.TabSpec content = mTabHost.newTabSpec("Suggestions").setIndicator(tabview).setContent(intent);
//        mTabHost.addTab(content);


        setupTabListener();
    }

    private void addTab(final String tag, final int imgRes, final int fragContainer) {
        View tabview = createTabView(mTabHost.getContext(), tag, imgRes);
        TabHost.TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(fragContainer);
        mTabHost.addTab(setContent);
    }

    private View createTabView(final Context context, final String text, final int imgRes) {
        View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
        TextView tv = (TextView) view.findViewById(R.id.tabsText);
        ImageView iv = (ImageView) view.findViewById(R.id.tabsImage);
        tv.setText(text);
        iv.setImageResource(imgRes);
        return view;
    }

    private void setupTabListener() {
        mTabHost.setOnTabChangedListener(new TabListener(this, mTabHost));
    }

    private void setupFragHandlerHomeListener() {
        fraghandlerHomeListener = new OnHomeListener() {
            @Override
            public void onHomePressed() {
                chooseUI();
                setupActionBar();
            }
        };
    }

    private void chooseUI() {
        if (settingsSingleton.getUIVariant() == 0) {
            // Broad Shallow
            if (settingsSingleton.getUIDecisionTreeEnabled()) {
                // BS DT
                displayBSDecisionTreeIntro();
            } else {
                new BSFragmentHandler(this, EventType.GEN, fraghandlerHomeListener);
            }
        } else {
            // Narrow Deep
            if (settingsSingleton.getUIDecisionTreeEnabled()) {
                // ND DT
                displayNDDecisionTreeIntro();
            } else {
                new NDFragmentHandler(this, EventType.GEN, fraghandlerHomeListener);
            }
        }
    }

    private void chooseUIEdit(BaseReminder reminder) {
        if (settingsSingleton.getUIVariant() == 0) {
            // Broad Shallow
            new BSFragmentHandler(this, reminder.getType(), fraghandlerHomeListener, reminder);
        } else {
            // Narrow Deep
            new NDFragmentHandler(this, reminder.getType(), fraghandlerHomeListener, reminder);
        }
    }


    private void displayNDDecisionTreeIntro() {
        NDDTIntroFragment frag = NDDTIntroFragment.newInstance();
        frag.setDecisionTreeListener(this);
        ft = fm.beginTransaction()
                .replace(R.id.tabReminderContainer, frag, getString(R.string.nddt_intro_tag));
        ft.commit();
    }

    private void displayBSDecisionTreeIntro() {
        BSDTIntroFragment frag = BSDTIntroFragment.newInstance();
        frag.setDecisionTreeListener(this);
        ft = fm.beginTransaction()
                .replace(R.id.tabReminderContainer, frag, getString(R.string.bsdt_intro_tag));
        ft.commit();
    }

    // DECISION TREE LISTENER METHOD
    @Override
    public void loadFragmentHandler(HandlerType handlerType, EventType type) {
        if (handlerType.equals(HandlerType.ND)) {
            new NDFragmentHandler(this, type, fraghandlerHomeListener);
        } else {
            new BSFragmentHandler(this, type, fraghandlerHomeListener);
        }
    }

    public void launchSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, 1);
    }

    /**
     * Listens for the settings closing and reloads the UI
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_CANCELED) {
                chooseUI();
                setupActionBar();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the action bar menu
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Set up the password dialog
        final SettingsPasswordDialog mDialog = new SettingsPasswordDialog(this);
        mDialog.setListener(new SettingsPasswordListener() {
            @Override
            public void enterPassword(String pWord) {
                if (pWord.equals(getString(R.string.actionbar_admin_settings_password))) {
                    launchSettings();
                } else {
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (v != null) {
                        v.vibrate(200);
                        Toast.makeText(ReminderActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        switch (item.getItemId()) {
            case R.id.action_bar_settings:
                mDialog.show();
                break;
            case android.R.id.home:
                chooseUI();
                setupActionBar();
                break;
            default:
                break;
        }

        return true;
    }

    public SettingsInterface getSettingsSingleton() {
        return settingsSingleton;
    }

    @Override
    public void onBackPressed() {
    }

    public void switchToScheduleTab() {
        mTabHost.setCurrentTab(1);
    }

    public void checkCurrentFragmentAndDisplayTitle() {
        Fragment f = fm.findFragmentById(R.id.tabReminderContainer);
        //If current fragment isn't an intro fragment
        if (!(f instanceof NDDTIntroFragment) && !(f instanceof BSDTIntroFragment)) {
            setupDTActionBar();
        }
    }


    private void setupDTActionBar() {
        if (settingsSingleton.getUIDecisionTreeEnabled()) {
            // Hide App Name
            actionBar.setDisplayShowTitleEnabled(false);
            // Show custom title text
            LayoutInflater mInflater = LayoutInflater.from(this);
            View mCustomView = mInflater.inflate(R.layout.actionbar_title, null);

            mCustomView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fraghandlerHomeListener.onHomePressed();
                }
            });

            actionBar.setCustomView(mCustomView);
            actionBar.setDisplayShowCustomEnabled(true);
        } else {
            // Hide custom title text
            actionBar.setDisplayShowCustomEnabled(false);
            // Show app name
            actionBar.setDisplayShowTitleEnabled(true);
        }
    }

    public void editReminder(BaseReminder reminder) {
        mTabHost.setCurrentTab(0);
        chooseUIEdit(reminder);
    }

    public int getCurrentTab() {
        if (mTabHost != null) {
            return mTabHost.getCurrentTab();
        } else {
            return 0;
        }
    }

    // -------------------------- REMINDER AND DATABASE METHODS ----------------------------------//
    private void createDB() {
        reminderDAO = ReminderHandler.createDatabase(this);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

/*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    */

    private void sendToServerNotificationFeedback(final ReminderPattern pattern){
        //get database reference
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        //get android device's unique id or name
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        Boolean smart_login = sharedPreferences.getBoolean("smart_login",false);
        String smart_login_name = sharedPreferences.getString("smart_login_name",null);
        final String  android_id;
        if (smart_login){
            android_id=smart_login_name;
        }else{
            android_id= Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        }
        //add one time event listener to update data
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get the mapped database values
                HashMap map = (HashMap) dataSnapshot.getValue();
                boolean check = (boolean) ((HashMap) map.get(android_id)).get("smart_reminding");

                if (check){
                    long value = 0;
                    long accepted_prompts_value = (long) ((HashMap) map.get(android_id)).get("prompts_accepted") + 1;

                    //increase total accepted prompts
                    dataSnapshot.getRef().child(android_id).child("prompts_accepted").setValue(accepted_prompts_value);

                    //Find the pattern and increase it
                    if (pattern != null) {
                        if (pattern.equals(ReminderPattern.WEEKLY)) {
                            value = (long) ((HashMap) map.get(android_id)).get("weekly_prompts_accepted") + 1;
                            dataSnapshot.getRef().child(android_id).child("weekly_prompts_accepted").setValue(value);
                        }
                        if (pattern.equals(ReminderPattern.TWO_WEEKS)) {
                            value = (long) ((HashMap) map.get(android_id)).get("two_week_prompts_accepted") + 1;
                            dataSnapshot.getRef().child(android_id).child("two_week_prompts_accepted").setValue(value);
                        }
                        if (pattern.equals(ReminderPattern.MONTHLY)) {
                            value = (long) ((HashMap) map.get(android_id)).get("monthly_prompts_accepted") + 1;
                            dataSnapshot.getRef().child(android_id).child("monthly_prompts_accepted").setValue(value);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
