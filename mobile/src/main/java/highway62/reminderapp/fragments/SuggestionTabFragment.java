package highway62.reminderapp.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.vision.text.Line;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;

import highway62.reminderapp.R;
import highway62.reminderapp.ReminderActivity;
import highway62.reminderapp.SmartReminding.MainOcr;
import highway62.reminderapp.SmartReminding.SmartReminding;
import highway62.reminderapp.SmartReminding.SuggestionTab;
import highway62.reminderapp.constants.Consts;
import highway62.reminderapp.constants.EventType;
import highway62.reminderapp.constants.ReminderPattern;
import highway62.reminderapp.constants.ReminderType;
import highway62.reminderapp.reminderhandlers.ReminderHandler;
import highway62.reminderapp.reminders.BaseReminder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SuggestionTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SuggestionTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SuggestionTabFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SuggestionTabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SuggestionTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SuggestionTabFragment newInstance(String param1, String param2) {

        SuggestionTabFragment fragment = new SuggestionTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view=inflater.inflate(R.layout.fragment_suggestion_tab, container, false);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.main_linear_layout);

        SmartReminding sm= new SmartReminding(getContext());
        ArrayList<ArrayList<BaseReminder>> suggestion_lists = sm.getAllSuggestions();
        for (ArrayList<BaseReminder> list : suggestion_lists){
            int i=0;
            for (BaseReminder reminder : list){
                i=i++;
                final BaseReminder old_rem = reminder;
                final BaseReminder rem =reminder;
                final String title = reminder.getTitle();
                final long dt_millis = reminder.getDateTime();
                final DateTime dt = new DateTime(dt_millis);

                String minute=minute=""+dt.getMinuteOfHour();
                if(dt.getMinuteOfHour()<=9) minute="0"+dt.getMinuteOfHour();
                String hour=""+dt.getHourOfDay();
                if(dt.getHourOfDay()<=9) hour = "0"+dt.getHourOfDay();
                String month = ""+dt.getMonthOfYear();
                if(dt.getMonthOfYear()<=9) month = "0" + dt.getMonthOfYear();
                String new_month = ""+new DateTime().getMonthOfYear();
                if(dt.getMonthOfYear()<=9) new_month = "0" + new DateTime().getMonthOfYear();

                final String date_string = " "+dt.getDayOfMonth()+"/"+month+"/"+dt.getYear()+ " at "+hour+":"+minute;
                final String date_string_today = new DateTime().getDayOfMonth()+"/"+new_month+"/"+new DateTime().getYear()+ " at "+hour+":"+minute;
                String tv_string="";
                if (title == null || title==""){
                    tv_string= " No Title \n " +date_string;
                }else{
                    tv_string= " "+title+" : \n" +date_string;
                }
                TextView tv = new TextView(getContext());
                final int idt =tv.generateViewId();
                tv.setId(idt);
                tv.setText(tv_string);
                tv.setTextSize(28);
                tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
                tv.setBackgroundResource(R.drawable.pressed_text_view);
                tv.setLayoutParams(new
                        ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                LinearLayout ll = new LinearLayout(getContext());
                ll.addView(tv);
                ll.setLayoutParams(new
                        ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                CardView cd = new CardView(getContext());
                cd.addView(ll);



                cd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), ReminderActivity.class);
                        intent.putExtra("", dt);


                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                        final TextView et = new TextView(getContext());
                        et.setText(" Add a reminder for "+date_string_today + " ?");
                        et.setTextSize(28);
                        // set prompts.xml to alertdialog builder
                        alertDialogBuilder.setView(et);

                        // set dialog message
                        alertDialogBuilder.setCancelable(true).setPositiveButton("Set", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Create a BaseReminder and populate it with the suggested time
                                rem.setTitle(title);

                                DateTime dt1 = new DateTime()
                                        .withHourOfDay(dt.getHourOfDay())
                                        .withMinuteOfHour(dt.getMinuteOfHour());

                                rem.setDateTime(dt1.getMillis());

                                rem.setReminderType(ReminderType.REMINDER);
                                rem.setType(EventType.GEN);
                                rem.setPromptLevel(2);

                                //Set the reminder
                                ReminderHandler.setReminder(getContext(),rem);
                                old_rem.setSmartReminded(true);// set the old reminder as smart reminded so it doesn't fire off again
                                ReminderHandler.updateReminder(getContext(),old_rem);
                                View tv = view.findViewById(idt);
                                ((ViewGroup) tv.getParent()).removeView(tv);

                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // show it
                        alertDialog.show();
                    }
                });

                layout.addView(cd);

                View line = new View(getContext());
                line.setLayoutParams(new
                        ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                line.setBackgroundColor(1);
                layout.addView(line);

            }

        }
        return view;
    }

    public void setSuggestionProperties(){
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void show(){

    }

    public void updateDatabase(BaseReminder rem){
        final BaseReminder reminder = rem;
        //get database reference
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        //get android device's unique id or name
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Boolean smart_login = sharedPreferences.getBoolean("smart_login",false);
        String smart_login_name = sharedPreferences.getString("smart_login_name",null);
        final String  android_id;
        if (smart_login){
            android_id=smart_login_name;
        }else{
            android_id= Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        }
        //add one time event listener to update data
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get the mapped database values
                HashMap map = (HashMap)dataSnapshot.getValue();

                //New value for prompts_accepted fields
                long value = (long)((HashMap)map.get(android_id)).get("total_prompts") + 1;
                long weekly_value = (long)((HashMap)map.get(android_id)).get("weekly_prompts") + 1;
                long two_week_value = (long)((HashMap)map.get(android_id)).get("two_week_prompts") + 1;
                long monthly_value = (long)((HashMap)map.get(android_id)).get("monthly_prompts") + 1;
                long weekly_value_accepted = (long)((HashMap)map.get(android_id)).get("weekly_prompts_accepted") + 1;
                long two_week_value_accepted = (long)((HashMap)map.get(android_id)).get("two_week_prompts_accepted") + 1;
                long monthly_value_accepted = (long)((HashMap)map.get(android_id)).get("monthly_prompts_accepted") + 1;

                //Check if smart reminding is enabled
                boolean check= (boolean)((HashMap)map.get(android_id)).get("smart_reminding");
                if (check)
                    dataSnapshot.getRef().child(android_id).child("total_prompts").setValue(value);
                ReminderPattern pattern=reminder.getPattern();
                if (pattern!=null){
                    if (pattern.equals(ReminderPattern.WEEKLY)) {
                        dataSnapshot.getRef().child(android_id).child("weekly_prompts").setValue(weekly_value);
                        dataSnapshot.getRef().child(android_id).child("weekly_prompts_accepted").setValue(weekly_value_accepted);
                    }
                    if (pattern.equals(ReminderPattern.TWO_WEEKS)) {
                        dataSnapshot.getRef().child(android_id).child("two_week_prompts").setValue(two_week_value);
                        dataSnapshot.getRef().child(android_id).child("two_week_prompts_accepted").setValue(two_week_value_accepted);
                    }
                    if (pattern.equals(ReminderPattern.MONTHLY)) {
                        dataSnapshot.getRef().child(android_id).child("monthly_prompts").setValue(monthly_value);
                        dataSnapshot.getRef().child(android_id).child("monthly_prompts_accepted").setValue(monthly_value_accepted);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });
    }
}
