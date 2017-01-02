package highway62.reminderapp.adminSettings;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import highway62.reminderapp.R;
import highway62.reminderapp.reminderhandlers.ReminderHandler;
import highway62.reminderapp.reminders.BaseReminder;

public class ViewUPActivity extends AppCompatActivity {

    private ListView promptListView;
    private PromptListSelector selector;
    private AbsListView.MultiChoiceModeListener mMultiChoiceModeListener;
    private ArrayList<BaseReminder> promptList;
    private ActionMode mActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_up);
        setupActionBar();
        selector = new PromptListSelector();
        promptListView = (ListView) findViewById(R.id.pref_prompt_view_up_list);
        // Load the prompts
        new PromptLoader().execute("");

    }

    private void setupPromptListViewMultiSelect(final PromptListAdapter pListAdapter){

        mMultiChoiceModeListener = new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if(checked){
                    selector.addSelectedPrompt(promptList.get(position), position);
                }else{
                    selector.removeSelectedPrompt(promptList.get(position));
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.delete_menu, menu);
                mode.setTitle("Delete Prompts");
                mActionMode = mode;
                //disableListViewRows(pListAdapter);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_delete:

                        // delete all items in items selected
                        for(BaseReminder p : selector.getSelectedItems()){
                            if(promptList.contains(p)){
                                promptList.remove(p);
                                ReminderHandler.manuallyDeletePrompt(ViewUPActivity.this, p);
                            }
                        }

                        // Update the adapter
                        pListAdapter.notifyDataSetChanged();
                        selector.clearSelectedItems();

                        // Close action mode
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mActionMode = null;
                //enableListViewRows(pListAdapter);
            }
        };

        promptListView.setMultiChoiceModeListener(mMultiChoiceModeListener);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.actionbar_settings_view_UP_title));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mActionMode != null){
            mActionMode.finish();
        }else{
            super.onBackPressed();
        }
    }

    /**
     * View Holder to optimise the listview and stop values from changing after scrolling
     */
    static class ViewHolder{
        public TextView dateTimeView;
        public TextView contentView;
        public SeekBar sliderView;
        public RelativeLayout deleteView;

        public RelativeLayout sliderTextBtn1;
        public RelativeLayout sliderTextBtn2;
        public RelativeLayout sliderTextBtn3;
        public RelativeLayout sliderTextBtn4;

        public Button editLevelBtn;
    }

    private void loadPrompts(ArrayList<BaseReminder> pList){
        if(pList != null) {
            // Get list of prompts from DB
            promptList = pList;
            PromptListAdapter pAdapter = new PromptListAdapter(this, pList, selector);
            promptListView.setAdapter(pAdapter);
            promptListView.setLongClickable(true);
            promptListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            promptListView.setItemsCanFocus(false);
            setupPromptListViewMultiSelect(pAdapter);
        }else{
            Log.e("DBERR", "Error retrieving prompts from DB in ViewUPActivity: loadPrompts()");
        }
    }

    private class PromptLoader extends AsyncTask<String, Void, ArrayList<BaseReminder>>{

        @Override
        protected ArrayList<BaseReminder> doInBackground(String... params) {
            return ReminderHandler.getPrompts(ViewUPActivity.this);
        }

        @Override
        protected void onPostExecute(ArrayList<BaseReminder> baseReminders) {
            loadPrompts(baseReminders);
        }
    }
}
