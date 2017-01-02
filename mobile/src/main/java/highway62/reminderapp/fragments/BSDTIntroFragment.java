package highway62.reminderapp.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import highway62.reminderapp.R;
import highway62.reminderapp.constants.EventType;
import highway62.reminderapp.constants.HandlerType;
import highway62.reminderapp.fragmentHandlers.DecisionTreeListener;


public class BSDTIntroFragment extends Fragment {

    private TextView apptBtn;
    private TextView shopBtn;
    private TextView birthBtn;
    private TextView medicBtn;
    private TextView dailyBtn;
    private TextView socialBtn;

    private DecisionTreeListener mListener;

    public BSDTIntroFragment() {}

    public static BSDTIntroFragment newInstance() {
        return new BSDTIntroFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_nddt_intro, container, false);

        apptBtn = (TextView) v.findViewById(R.id.intro_cat_appt);
        shopBtn = (TextView) v.findViewById(R.id.intro_cat_shop);
        birthBtn = (TextView) v.findViewById(R.id.intro_cat_birth);
        medicBtn = (TextView) v.findViewById(R.id.intro_cat_medic);
        dailyBtn = (TextView) v.findViewById(R.id.intro_cat_daily);
        socialBtn = (TextView) v.findViewById(R.id.intro_cat_social);

        apptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.loadFragmentHandler(HandlerType.BS, EventType.APPT);
            }
        });

        shopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.loadFragmentHandler(HandlerType.BS,EventType.SHOPPING);
            }
        });

        birthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.loadFragmentHandler(HandlerType.BS,EventType.BIRTH);
            }
        });

        medicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.loadFragmentHandler(HandlerType.BS,EventType.MEDIC);
            }
        });

        dailyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.loadFragmentHandler(HandlerType.BS,EventType.DAILY);
            }
        });

        socialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.loadFragmentHandler(HandlerType.BS,EventType.SOCIAL);
            }
        });

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setDecisionTreeListener(DecisionTreeListener listener){
        this.mListener = listener;
    }

}
