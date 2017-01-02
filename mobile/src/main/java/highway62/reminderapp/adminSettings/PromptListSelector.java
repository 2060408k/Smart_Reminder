package highway62.reminderapp.adminSettings;

import java.util.ArrayList;
import java.util.HashMap;

import highway62.reminderapp.reminders.BaseReminder;

/**
 *  Handles prompts selected in the ViewUPActivity class
 *  and notifies observers when selected prompts are deleted
 */
public class PromptListSelector {
    HashMap<Integer, BaseReminder> selectedItems;
    PromptSelectorObserver editModeObserver;

    public PromptListSelector(){
        selectedItems = new HashMap<>();
    }

    /**
     * Adds a prompt that has been multi-selected in the ViewUPActivity
     * @param p Prompt that has been selected
     * @param position Position of prompt in the ListView
     */
    public void addSelectedPrompt(BaseReminder p, int position){
        selectedItems.put(position, p);
    }

    /**
     * Removes a Prompt from the selected prompts list if it has been deselected in ViewUPActivity
     * @param p Prompt to remove from the selected prompt list
     */
    public void removeSelectedPrompt(BaseReminder p){
        if(selectedItems.containsValue(p)){
            selectedItems.remove(p);
        }
    }

    /**
     * Returns a list of all the selected prompts. Used in ViewUPActivity to
     * delete all selected items
     * @return ArrayList of currently selected Prompts
     */
    public ArrayList<BaseReminder> getSelectedItems(){
        ArrayList<BaseReminder> prompts = new ArrayList<>();
        for(BaseReminder p: selectedItems.values()){
            prompts.add(p);
        }
        return  prompts;
    }

    /**
     * Removes selected prompts from the selected prompts list and notifies the edit mode
     * handler. Used after multi-deleting prompts in the ViewUPActivity
     */
    public void clearSelectedItems(){
        if(editModeObserver != null){
            for(BaseReminder p : selectedItems.values()){
                editModeObserver.notifyPromptDeleted(p);
            }
        }
        selectedItems.clear();
    }

    /**
     * Sets an observer to notify when removing selected prompts
     * @param observer Observer object to notify when removing prompts
     */
    public void setObserver(PromptSelectorObserver observer){
        this.editModeObserver = observer;
    }
}
