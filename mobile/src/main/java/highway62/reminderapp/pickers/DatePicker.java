package highway62.reminderapp.pickers;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;

/**
 * Created by Highway62 on 17/07/2016.
 */
public class DatePicker extends android.widget.DatePicker {


    public DatePicker(Context context) {
        super(context);
    }

    public DatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DatePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // Stops the scroll view from interfering with the date picker
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN)
        {
            ViewParent p = getParent();
            if (p != null)
                p.requestDisallowInterceptTouchEvent(true);
        }

        return false;
    }
}
