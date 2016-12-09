package ch.epfl.sweng.project.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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


public class RequestChallengeDialogFragment extends DialogFragment {


    private Challenge.Type type;
    private int firstValue;
    private int secondValue;
    private String sender;

    /**
     * interface for the listener of this class.
     */
    public interface RequestDialogListener {
        void onDialogAcceptClick(DialogFragment dialog);
        void onDialogDeclineClick(DialogFragment dialog);
        void onDialogCancelClick(DialogFragment dialog);
    }

    RequestDialogListener mListener;

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_request_dialog, null);

        builder.setCancelable(false);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        mListener.onDialogAcceptClick(RequestChallengeDialogFragment.this);
                    }
                })
                .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //cancel
                        // Send the positive button event back to the host activity
                        mListener.onDialogDeclineClick(RequestChallengeDialogFragment.this);
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //cancel
                        // Send the positive button event back to the host activity
                        mListener.onDialogCancelClick(RequestChallengeDialogFragment.this);
                    }
                });



        ((TextView)view.findViewById(R.id.txt_requester)).setText(sender + " challenged you on a run based on:");
        TextView typeTxt = (TextView) view.findViewById(R.id.txt_challenge_type);
        TextView requestDescriptionTxt = (TextView) view.findViewById(R.id.txt_request_description);
        TextView goalTxt = (TextView) view.findViewById(R.id.txt_goal);

        typeTxt.setText(type.toString());

        if(type == Challenge.Type.DISTANCE) {
            requestDescriptionTxt.setText(R.string.wins_the_first_one_to_reach);
            goalTxt.setText((firstValue + (secondValue / 1000.0)) + "km");
        } else {
            requestDescriptionTxt.setText(R.string.wins_the_one_who_runs_the_most_distance_in);
            goalTxt.setText(firstValue + getString(R.string.spaced_h) + secondValue + R.string.min);
        }

        return builder.create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        type = (Challenge.Type)args.get("type");
        firstValue = args.getInt("firstValue");
        secondValue = args.getInt("secondValue");
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

    /**
     * getter for the challenge type.
     *
     * @return the challenge type.
     */
    public Challenge.Type getType() {
        return type;
    }

    /**
     * getter for the second value.
     *
     * @return the second value.
     */
    public int getSecondValue() {
        return secondValue;
    }

    /**
     * getter for the first value.
     *
     * @return the first value.
     */
    public int getFirstValue() {
        return firstValue;
    }
}
