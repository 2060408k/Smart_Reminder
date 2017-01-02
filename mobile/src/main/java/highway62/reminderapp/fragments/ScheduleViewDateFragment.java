package highway62.reminderapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

import highway62.reminderapp.R;
import highway62.reminderapp.ReminderActivity;
import highway62.reminderapp.adminSettings.SettingsInterface;
import highway62.reminderapp.constants.Consts;
import highway62.reminderapp.fragmentHandlers.OnScheduleFragmentListener;
import highway62.reminderapp.reminders.BaseReminder;
import highway62.reminderapp.scheduleadapters.ScheduleAdapter;


public class ScheduleViewDateFragment extends Fragment {

    private static final String ARG_REMINDERS = "argReminders";

    private ArrayList<BaseReminder> reminders;
    private ListView reminderList;
    private ReminderActivity context;
    private SettingsInterface settings;
    private TextView backBtn;
    private OnScheduleFragmentListener handler;
    private TextView dateTitle;
    private DateTime dateSelected;

    public ScheduleViewDateFragment() {}

    public static ScheduleViewDateFragment newInstance(ArrayList<BaseReminder> remArg) {
        ScheduleViewDateFragment fragment = new ScheduleViewDateFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_REMINDERS, remArg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println(getArguments()+" neiw");
        if (getArguments() != null) {
            this.reminders = getArguments().getParcelableArrayList(ARG_REMINDERS);
        }
        context = (ReminderActivity) getActivity();
        settings = context.getSettingsSingleton();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_schedule_view_date, container, false);
        reminderList = (ListView) v.findViewById(R.id.scheduleList);

//        System.out.println("yoleleis");
//        System.out.println(this.reminders);
//        BaseReminder it=null;
//        int i=0;
//        while (i<this.reminders.size()){
//            it = this.reminders.get(i);
//            BaseReminder rem = (BaseReminder) it;
//            DateTime dt = new DateTime(rem.getDateTime())
//                    .withSecondOfMinute(0)
//                    .withMillisOfSecond(0);
//            System.out.println(dt);
//            i++;
//        }
        ScheduleAdapter adapter = new ScheduleAdapter(context, handler, reminders);
        dateTitle = (TextView) v.findViewById(R.id.schedule_date_title);
        if(dateSelected != null){
            DateTimeFormatter dtfDate = DateTimeFormat.forPattern("dd/MM/yyyy");
            String dayOfweek = Consts.daysOfWeek[dateSelected.getDayOfWeek() - 1].name();
            dateTitle.setText(dayOfweek + " " + dtfDate.print(dateSelected));
        }
        reminderList.setAdapter(adapter);
        backBtn = (TextView) v.findViewById(R.id.schedule_backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.navigateBack();
            }
        });
        return v;
    }

    public void setHandler(OnScheduleFragmentListener handler){
        this.handler = handler;
    }

    public static class ViewHolder{
        public TextView scheduleRowTitle;
        public TextView scheduleRowEdit;
        public TextView scheduleRowDelete;
        public TextView scheduleRowName;
        public RelativeLayout scheduleRowLocationCont;
        public TextView scheduleRowLocation;
        public RelativeLayout scheduleRowEventAfterCont;
        public TextView scheduleRowEventAfter;
        public TextView scheduleRowDate;
        public TextView scheduleRowTime;
        public RelativeLayout scheduleRowDurationCont;
        public TextView scheduleRowDuration;
        public RelativeLayout scheduleRowNotificationCont;
        public TextView scheduleRowNotification;
        public RelativeLayout scheduleRowNotesCont;
        public TextView scheduleRowNotes;
        public RelativeLayout scheduleRowRepeatCont;
        public TextView scheduleRowRepeat;
        public RelativeLayout scheduleRowRepeatWeeksCont;
        public TextView scheduleRowRepeatWeeks;
        public RelativeLayout scheduleRowPromptLevelCont;
        public TextView scheduleRowPromptLevel;
    }

    public void setDate(DateTime dt){
        dateSelected = dt;
    }

}
