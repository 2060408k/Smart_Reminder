package highway62.reminderapp.adminSettings;

import java.util.HashMap;

import highway62.reminderapp.reminders.BaseReminder;

/**
 * Handles the edit mode of prompts in the adapter
 * Acts as an observer to the PromptListSelector to remove prompts
 * from the edit mode list when they are deleted by the selector
 */
public class SettingsEditModeHandler implements PromptSelectorObserver {

    HashMap<BaseReminder, Boolean> editModeList;

    public SettingsEditModeHandler() {
        this.editModeList = new HashMap<>();
    }

    /**
     * Adds a prompt to the edit mode list on setup in the adapter
     * @param p Prompt to add with edit mode value set
     * @param editModeEnabled Edit mode value to set for p
     */
    public void addEditModePrompt(BaseReminder p, boolean editModeEnabled){
        editModeList.put(p, editModeEnabled);
    }

    /**
     * Removes prompts from the edit mode list if they have been singly deleted in the adapter
     * @param p Prompt to remove from edit mode list
     */
    public void removeEditModeItem(BaseReminder p){
        if(editModeList.containsKey(p)){
            editModeList.remove(p);
        }
    }

    /**
     * Checks whether supplied prompt is currently in edit mode
     * @param p Prompt to check
     * @return boolean whether p is in edit mode
     */
    public boolean promptInEditMode(BaseReminder p){
        if(editModeList.containsKey(p)){
            return editModeList.get(p);
        }else{
            return false;
        }
    }

    /**
     * Sets the supplied Prompt as being in edit mode
     * @param p Prompt to set
     * @param inEditMode boolean indicates whether p is currently in edit mode
     */
    public void setPromptInEditMode(BaseReminder p, boolean inEditMode){
        if(editModeList.containsKey(p)){
            editModeList.put(p, inEditMode);
        }
    }

    @Override
    public void notifyPromptDeleted(BaseReminder p) {
        if(editModeList.containsKey(p)){
            editModeList.remove(p);
        }
    }
}
