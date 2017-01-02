package highway62.reminderapp.viewpager;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import highway62.reminderapp.R;
import highway62.reminderapp.constants.Consts;

public class TimeSelectorFragment extends Fragment {

    private static final String ARG_POS = "arg_pos";

    private ManualTimeHandler handler;
    private int position = 0;

    private TextView timeView;
    private TextView textView;

    public TimeSelectorFragment() {}

    public static TimeSelectorFragment newInstance(int pos) {
        TimeSelectorFragment fragment = new TimeSelectorFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POS, pos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POS) % Consts.PAGES;
            Log.e("TIME", "Position in Frag in onCreate(): " + position);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_time_selector, container, false);
        timeView = (TextView) v.findViewById(R.id.timeSelectorTime);
        textView = (TextView) v.findViewById(R.id.timeSelectorText);
        if(timeView != null && textView != null) {

            setupTimeAndText();

            timeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(handler != null){
                        handler.selectTime(position);
                    }
                }
            });

        }else{
            Log.e("TIMESELECT", "Error finding views in TimeSelectorFragment");
        }

        return v;
    }

    private void setupTimeAndText(){
        Log.e("TIME", "Position in frag when calling handler:" + position);
        handler.setActualPosition(position);

        switch (position){
            case 0:
                Log.e("TIME", "Text set to 5 mins with position 0. Actual:" + position);
                timeView.setText("5");
                textView.setText("Mins");
                break;
            case 1:
                Log.e("TIME", "Text set to 10 mins with position 1. Actual:" + position);
                timeView.setText("10");
                textView.setText("Mins");
                break;
            case 2:
                Log.e("TIME", "Text set to 15 mins with position 2. Actual:" + position);
                timeView.setText("15");
                textView.setText("Mins");
                break;
            case 3:
                Log.e("TIME", "Text set to 20 mins with position 3. Actual:" + position);
                timeView.setText("20");
                textView.setText("Mins");
                break;
            case 4:
                Log.e("TIME", "Text set to 30 mins with position 4. Actual:" + position);
                timeView.setText("30");
                textView.setText("mins");
                break;
            case 5:
                Log.e("TIME", "Text set to 45 mins with position 5. Actual:" + position);
                timeView.setText("45");
                textView.setText("mins");
                break;
            case 6:
                Log.e("TIME", "Text set to 50 mins with position 6. Actual:" + position);
                timeView.setText("50");
                textView.setText("mins");
                break;
            case 7:
                Log.e("TIME", "Text set to 1 hour with position 7. Actual:" + position);
                timeView.setText("1");
                textView.setText("Hour");
                break;
            case 8:
                Log.e("TIME", "Text set to 2 hour with position 8. Actual:" + position);
                timeView.setText("2");
                textView.setText("Hours");
                break;
            case 9:
                Log.e("TIME", "Text set to 4 hour with position 9. Actual:" + position);
                timeView.setText("4");
                textView.setText("Hours");
                break;
            case 10:
                Log.e("TIME", "Text set to 1 hour with position 10. Actual:" + position);
                timeView.setText("6");
                textView.setText("Hours");
                break;
            case 11:
                Log.e("TIME", "Text set to 1 hour with position 11. Actual:" + position);
                timeView.setText("8");
                textView.setText("Hours");
                break;
            default:
                timeView.setText("1");
                textView.setText("Hour");
                break;
        }

        /*
        switch (position){
            case 0:
                Log.e("TIME", "Text set to 1 hour with position 0. Actual:" + position);
                timeView.setText("1");
                textView.setText("Hour");
                break;
            case 1:
                Log.e("TIME", "Text set to 2 hours with position 1. Actual:" + position);
                timeView.setText("2");
                textView.setText("Hours");
                break;
            case 2:
                Log.e("TIME", "Text set to 4 hours with position 2. Actual:" + position);
                timeView.setText("4");
                textView.setText("Hours");
                break;
            case 3:
                Log.e("TIME", "Text set to 6 hours with position 3. Actual:" + position);
                timeView.setText("6");
                textView.setText("Hours");
                break;
            case 4:
                Log.e("TIME", "Text set to 8 hours with position 4. Actual:" + position);
                timeView.setText("8");
                textView.setText("Hours");
                break;
            default:
                timeView.setText("1");
                textView.setText("Hour");
                break;
        }*/
    }

    public void setHandler(ManualTimeHandler handler){
        this.handler = handler;

    }

}
