package highway62.reminderapp.tablisteners;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;

import highway62.reminderapp.R;
import highway62.reminderapp.ReminderActivity;
import highway62.reminderapp.fragmentHandlers.ScheduleFragmentHandler;

/**
 * Created by Highway62 on 30/08/2016.
 */
public class TabListener implements TabHost.OnTabChangeListener {

    ReminderActivity context;
    TabHost mTabHost;
    TabWidget tw;
    ImageView tabReminderImgView;
    ImageView tabScheduleImgView;
    Drawable reminderIcon;
    Drawable scheduleIcon;
    int tabIconColor;


    public TabListener(ReminderActivity context, TabHost tabHost) {
        this.context = context;
        this.mTabHost = tabHost;
        tw = mTabHost.getTabWidget();
        tabReminderImgView = (ImageView) tw.getChildTabViewAt(0).findViewById(R.id.tabsImage);
        tabScheduleImgView = (ImageView) tw.getChildTabViewAt(1).findViewById(R.id.tabsImage);
        reminderIcon = tabReminderImgView.getDrawable();
        scheduleIcon = tabScheduleImgView.getDrawable();
        tabIconColor = context.getResources().getColor(R.color.appOrangeDark1);
        if (reminderIcon != null) {
            reminderIcon.mutate().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public void onTabChanged(String tabId) {

        if (context.getString(R.string.reminder_tab_text).equals(tabId)) {
            // Change Color
            setReminderActiveFilter();
            context.checkCurrentFragmentAndDisplayTitle();
        } else {
            // Change Color
            setScheduleActiveFilter();
            // Create new schedule handler
            new ScheduleFragmentHandler(context);
        }
    }

    private void setReminderActiveFilter() {
        if (reminderIcon != null && scheduleIcon != null) {
            reminderIcon.mutate().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            scheduleIcon.clearColorFilter();
        }
    }

    private void setScheduleActiveFilter() {
        if (reminderIcon != null && scheduleIcon != null) {
            reminderIcon.clearColorFilter();
            scheduleIcon.mutate().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        }
    }
}
