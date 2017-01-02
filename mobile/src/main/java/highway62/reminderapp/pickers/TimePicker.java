package highway62.reminderapp.pickers;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;

/**
 * Created by Highway62 on 17/07/2016.
 */
public class TimePicker extends android.widget.TimePicker {


    public TimePicker(Context context) {
        super(context);
    }

    public TimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // Stops the scroll view from interfering with the time picker
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Stop ScrollView from getting involved once you interact with the View
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            ViewParent p = getParent();
            if (p != null)
                p.requestDisallowInterceptTouchEvent(true);
        }
        return false;
    }
}
