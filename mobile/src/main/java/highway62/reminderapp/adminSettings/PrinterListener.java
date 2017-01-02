package highway62.reminderapp.adminSettings;

/**
 * Created by Highway62 on 29/08/2016.
 */
public interface PrinterListener {

    void onFinishedPrinting(boolean successful, String reason);

    void upDateProgress(int progress);
}
