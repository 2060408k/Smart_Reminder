package highway62.reminderapp.reminders;

/**
 * Created by Highway62 on 27/08/2016.
 */
public class PromptLogReminder {

    long dateTime;
    String content;
    int promptLevel;
    boolean postiveResponse;
    long dateTimeSet;

    public PromptLogReminder() {}

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPromptLevel() {
        return promptLevel;
    }

    public void setPromptLevel(int promptLevel) {
        this.promptLevel = promptLevel;
    }

    public boolean userRespondedPositively() {
        return postiveResponse;
    }

    public void setPostiveUserResponse(boolean postiveResponse) {
        this.postiveResponse = postiveResponse;
    }

    public long getDateTimeSet() {
        return dateTimeSet;
    }

    public void setDateTimeSet(long dateTimeSet) {
        this.dateTimeSet = dateTimeSet;
    }
}
