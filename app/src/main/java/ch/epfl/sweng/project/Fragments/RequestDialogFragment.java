package ch.epfl.sweng.project.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

import ch.epfl.sweng.project.Activities.ChallengeActivity;
import ch.epfl.sweng.project.Model.Challenge;


public class RequestDialogFragment extends DialogFragment implements View.OnClickListener {


    private Challenge.Type type;
    private int firstValue;
    private int secondValue;
    private String opponent;
    private String sender;
    private TextView typeTxt;
    private TextView requestDescriptionTxt;
    private TextView goalTxt;

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface RequestDialogListener {
        void onDialogAcceptClick(DialogFragment dialog);
        void onDialogDeclineClick(DialogFragment dialog);
        void onDialogCancelClick(DialogFragment dialog);
    }

    RequestDialogListener mListener;

    public RequestDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_request_dialog, null);

        builder.setCancelable(false);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        mListener.onDialogAcceptClick(RequestDialogFragment.this);
                    }
                })
                .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //cancel
                        // Send the positive button event back to the host activity
                        mListener.onDialogDeclineClick(RequestDialogFragment.this);
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //cancel
                        // Send the positive button event back to the host activity
                        mListener.onDialogCancelClick(RequestDialogFragment.this);
                    }
                });



        ((TextView)view.findViewById(R.id.txt_requester)).setText(sender + " challenged you on a run based on:");
        typeTxt = (TextView) view.findViewById(R.id.txt_challenge_type);
        requestDescriptionTxt = (TextView) view.findViewById(R.id.txt_request_description);
        goalTxt = (TextView) view.findViewById(R.id.txt_goal);

        typeTxt.setText(type.toString());

        if(type == Challenge.Type.DISTANCE) {
            requestDescriptionTxt.setText("Wins the first one to reach ");
            goalTxt.setText((firstValue + secondValue/1000.0) + "km");
        } else {
            requestDescriptionTxt.setText("Wins the one who runs the most distance in ");
            goalTxt.setText(firstValue + "h " + secondValue + "min");
        }

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
        firstValue = args.getInt("firstValue");
        secondValue = args.getInt("secondValue");
        opponent = args.getString("opponent");
        sender = args.getString("sender");

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the RequestDialogListener so we can send events to the host
            mListener = (RequestDialogListener) activity;
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

    public int getFirstValue() {
        return firstValue;
    }

    public int getSecondValue() {
        return secondValue;
    }

    public String getOpponent() {
        return opponent;
    }

}
