package ch.epfl.sweng.project.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import java.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

import java.util.Date;

import ch.epfl.sweng.project.Activities.ChallengeActivity;
import ch.epfl.sweng.project.Model.Challenge;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AcceptScheduleDialogFragment.AcceptScheduleDialogListener} interface
 * to handle interaction events.
 */
public class AcceptScheduleDialogFragment extends DialogFragment {
    private Challenge.Type type;
    private Date scheduledDate;
    private String sender;
    private TextView typeTxt;
    private TextView dateDescriptionTxt;
    private TextView dateTxt;
    private TextView timeDescriptionTxt;
    private TextView timeTxt;

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface AcceptScheduleDialogListener {
        void onAcceptScheduleDialogAcceptClick(DialogFragment dialog);
        void onAcceptScheduleDialogDeclineClick(DialogFragment dialog);
        void onAcceptScheduleDialogCancelClick(DialogFragment dialog);
    }

    AcceptScheduleDialogFragment.AcceptScheduleDialogListener mListener;

    public AcceptScheduleDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_accept_schedule_dialog, null);

        builder.setCancelable(false);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        mListener.onAcceptScheduleDialogAcceptClick(AcceptScheduleDialogFragment.this);
                    }
                })
                .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //cancel
                        // Send the positive button event back to the host activity
                        mListener.onAcceptScheduleDialogDeclineClick(AcceptScheduleDialogFragment.this);
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //cancel
                        // Send the positive button event back to the host activity
                        mListener.onAcceptScheduleDialogCancelClick(AcceptScheduleDialogFragment.this);
                    }
                });



        ((TextView)view.findViewById(R.id.txt_requester)).setText(sender + getString(R.string.wants_to_schedule_a_run_based_on));
        typeTxt = (TextView) view.findViewById(R.id.txt_challenge_type);
        dateDescriptionTxt = (TextView) view.findViewById(R.id.txt_date_description);
        dateTxt = (TextView) view.findViewById(R.id.txt_date);
        timeDescriptionTxt = (TextView) view.findViewById(R.id.txt_time_description);
        timeTxt = (TextView) view.findViewById(R.id.txt_time);
        typeTxt.setText(type.toString());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(scheduledDate);
        dateDescriptionTxt.setText(getString(R.string.on_date));
        dateTxt.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + getString(R.string.righetta) + String.valueOf(calendar.get(Calendar.MONTH)+1) + getString(R.string.righetta) + String.valueOf(calendar.get(Calendar.YEAR)));
        timeDescriptionTxt.setText(R.string.at);
        timeTxt.setText(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) + getString(R.string.due_punti) + String.valueOf(calendar.get(Calendar.MINUTE)));

        return builder.create();
    }

    public void onClick(View v) {

        switch (v.getId()) {

        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        type = (Challenge.Type)args.get("type");
        scheduledDate = (Date) args.get("date");
        sender = args.getString("sender");
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the RequestDialogListener so we can send events to the host
            mListener = (AcceptScheduleDialogFragment.AcceptScheduleDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement RequestDialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public Challenge.Type getType() {
        return type;
    }

    public Date getScheduledDate() {
        return scheduledDate;
    }


}
