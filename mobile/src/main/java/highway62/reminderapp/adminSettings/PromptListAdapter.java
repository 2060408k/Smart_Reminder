package highway62.reminderapp.adminSettings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

import highway62.reminderapp.R;
import highway62.reminderapp.reminderhandlers.ReminderHandler;
import highway62.reminderapp.reminders.BaseReminder;

/**
 * Created by Highway62 on 23/07/2016.
 */
public class PromptListAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<BaseReminder> promptList;
    private SettingsEditModeHandler editModeHandler;
    private static LayoutInflater inflater = null;
    private int noOfPromptLevels = 3;
    private AlertDialog.Builder deleteDialogBuilder;
    private String edit;
    private String save;

    public PromptListAdapter(Context context, ArrayList<BaseReminder> promptList, PromptListSelector selector) {
        this.context = context;
        this.promptList = promptList;
        editModeHandler = new SettingsEditModeHandler();
        selector.setObserver(editModeHandler);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        edit = context.getResources().getString(R.string.pref_UP_view_prompt_edit_level_button_edit);
        save = context.getResources().getString(R.string.pref_UP_view_prompt_edit_level_button_save);
        noOfPromptLevels = context.getResources().getInteger(R.integer.prompt_subtlety_no_of_levels);
        deleteDialogBuilder = new AlertDialog.Builder(context);
        // Store prompts in editModeList and initialise edit modes to false
        for (int i= 0; i < promptList.size(); i++){
            editModeHandler.addEditModePrompt(promptList.get(i), false);
        }
    }

    @Override
    public int getCount() {
        return promptList.size();
    }

    @Override
    public Object getItem(int position) {
        return promptList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final ViewUPActivity.ViewHolder viewHolder;

        View v = convertView;
        if(v == null){
            v = inflater.inflate(R.layout.activity_view_up_row, null);
            viewHolder = new ViewUPActivity.ViewHolder();
            viewHolder.dateTimeView = (TextView) v.findViewById(R.id.pref_prompt_view_up_date_time);
            viewHolder.contentView = (TextView) v.findViewById(R.id.pref_prompt_view_up_content);
            viewHolder.sliderView = (SeekBar) v.findViewById(R.id.pref_prompt_view_up_slider);
            viewHolder.deleteView = (RelativeLayout) v.findViewById(R.id.pref_prompt_view_up_delete_cross);
            viewHolder.sliderTextBtn1 = (RelativeLayout) v.findViewById(R.id.pref_prompt_view_up_sliderTxt1);
            viewHolder.sliderTextBtn2 = (RelativeLayout) v.findViewById(R.id.pref_prompt_view_up_sliderTxt2);
            viewHolder.sliderTextBtn3 = (RelativeLayout) v.findViewById(R.id.pref_prompt_view_up_sliderTxt3);
            viewHolder.sliderTextBtn4 = (RelativeLayout) v.findViewById(R.id.pref_prompt_view_up_sliderTxt4);
            viewHolder.editLevelBtn = (Button) v.findViewById(R.id.pref_prompt_view_up_edit_level_btn);
            v.setTag(viewHolder);
        }else{
            viewHolder = (ViewUPActivity.ViewHolder) v.getTag();
        }

        // Get the object instance variables
        final BaseReminder p = promptList.get(position);
        long dTime = p.getDateTime();
        DateTime dt = new DateTime(dTime);
        final String content = p.getNotes();
        int sliderLevel = p.getPromptLevel();

        // Format the date and time
        DateTimeFormatter dtfDate = DateTimeFormat.forPattern("dd/MM/yyyy");
        DateTimeFormatter dtfTime = DateTimeFormat.forPattern("HH:mm");
        String dateTime = dtfDate.print(dt) + " @ " + dtfTime.print(dt);

        // Set the views
        viewHolder.dateTimeView.setText(dateTime);
        viewHolder.contentView.setText(content);

        // Set slider properties
        viewHolder.sliderView.setMax(noOfPromptLevels); // Highest level
        viewHolder.sliderView.setProgress(sliderLevel);
        viewHolder.sliderView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progressStep = seekBar.getMax() / noOfPromptLevels;
                int lastDotProgress = Math.round(seekBar.getProgress() / progressStep) * progressStep;
                int nextDotProgress = lastDotProgress + progressStep;
                int midBetweenDots = lastDotProgress + (progressStep / 2);

                if (seekBar.getProgress() > midBetweenDots) {
                    seekBar.setProgress(nextDotProgress);
                }else {
                    seekBar.setProgress(lastDotProgress);
                }
                // Set new subtlety level of the Prompt object
                setSubtletyLevel(position, seekBar.getProgress());
            }
        });

        // Check if prompt is in edit mode
        if(!editModeHandler.promptInEditMode(p)){
            disableLevelInput(viewHolder);
            viewHolder.editLevelBtn.setText(edit);
        }else{
            enableLevelInput(viewHolder);
            viewHolder.editLevelBtn.setText(save);
        }

        // Set click listeners for the slider text
        setSliderTextOnClickListeners(viewHolder, position);

        // Set up the individual delete button
        viewHolder.deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(position);
            }
        });


        // Set up edit button listener
        viewHolder.editLevelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editModeHandler.promptInEditMode(p)){
                    // Save the edited prompt level
                    viewHolder.editLevelBtn.setText(edit);
                    disableLevelInput(viewHolder);
                    editModeHandler.setPromptInEditMode(p, false);
                    ReminderHandler.updateReminder(context, p);
                }else{
                    // Edit the prompt Level
                    viewHolder.editLevelBtn.setText(save);
                    enableLevelInput(viewHolder);
                    editModeHandler.setPromptInEditMode(p, true);
                }
            }
        });

        return v;
    }

    private void setSliderTextOnClickListeners(ViewUPActivity.ViewHolder vHolder, final int pos){
        // Set the slider text buttons touchListeners

        final SeekBar sliderView = vHolder.sliderView;
        vHolder.sliderTextBtn1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sliderView.setProgress(0);
                setSubtletyLevel(pos, 0);
                return true;
            }
        });

        vHolder.sliderTextBtn2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sliderView.setProgress(1);
                setSubtletyLevel(pos, 1);
                return true;
            }
        });

        vHolder.sliderTextBtn3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sliderView.setProgress(2);
                setSubtletyLevel(pos, 2);
                return true;
            }
        });

        vHolder.sliderTextBtn4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sliderView.setProgress(3);
                setSubtletyLevel(pos, 3);
                return true;
            }
        });
    }

    private void setSubtletyLevel(int pos, int level){
        BaseReminder q = promptList.get(pos);
        q.setPromptLevel(level);
        promptList.set(pos,q);
    }

    private void disableLevelInput(ViewUPActivity.ViewHolder holder){
        holder.sliderView.setEnabled(false);
        holder.sliderTextBtn1.setEnabled(false);
        holder.sliderTextBtn2.setEnabled(false);
        holder.sliderTextBtn3.setEnabled(false);
        holder.sliderTextBtn4.setEnabled(false);
    }

    private void enableLevelInput(ViewUPActivity.ViewHolder holder){
        holder.sliderView.setEnabled(true);
        holder.sliderTextBtn1.setEnabled(true);
        holder.sliderTextBtn2.setEnabled(true);
        holder.sliderTextBtn3.setEnabled(true);
        holder.sliderTextBtn4.setEnabled(true);
    }

    private void showDeleteDialog(final int position){

        AlertDialog deleteDialog;

        deleteDialogBuilder.setTitle(context.getResources().getString(R.string.pref_UP_view_prompt_delete_dialog_title));
        deleteDialogBuilder.setCancelable(true);

        deleteDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem(position);

                dialog.cancel();
            }
        });

        deleteDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        deleteDialog = deleteDialogBuilder.create();
        deleteDialog.show();
    }

    private void deleteItem(int position){
        editModeHandler.removeEditModeItem(promptList.get(position));
        ReminderHandler.manuallyDeletePrompt(context, promptList.get(position));
        promptList.remove(position);
        notifyDataSetChanged();
    }
}
