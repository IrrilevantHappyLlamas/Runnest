package ch.epfl.sweng.project.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

import ch.epfl.sweng.project.Model.Challenge;

/**
 * this class display a Dialog which asks the User to accept or decline a challenge.
 */
public class ReceiveChallengeDialogFragment extends DialogFragment implements View.OnClickListener {


    private Challenge.Type type;
    private int firstValue;
    private int secondValue;
    private String sender;

    private AlertDialog dialog;


    /**
     * interface for the listener of this class.
     */
    public interface ReceiveChallengeDialogListener {
        void onReceiveChallengeDialogAcceptClick(DialogFragment dialog);
        void onReceiveChallengeDialogDeclineClick(DialogFragment dialog);
        void onReceiveChallengeDialogCancelClick(DialogFragment dialog);
    }

    ReceiveChallengeDialogListener mListener;

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_receive_challenge_dialog, null);

        builder.setCancelable(false);
        builder.setView(view);

        ((TextView)view.findViewById(R.id.txt_requester)).setText(sender + " challenged you on a run based on:");
        TextView typeTxt = (TextView) view.findViewById(R.id.txt_challenge_type);
        TextView requestDescriptionTxt = (TextView) view.findViewById(R.id.txt_request_description);
        TextView goalTxt = (TextView) view.findViewById(R.id.txt_goal);
        ImageView typeImg = (ImageView) view.findViewById(R.id.type_img);
        if(type == Challenge.Type.TIME){
            typeTxt.setText("Time");
            typeImg.setImageDrawable(getResources().getDrawable(R.drawable.time_white));
        }


        if(type == Challenge.Type.DISTANCE) {
            requestDescriptionTxt.setText(R.string.wins_the_first_one_to_reach);
            goalTxt.setText((firstValue + (secondValue / 1000.0)) + "km");
        } else {
            requestDescriptionTxt.setText(R.string.wins_the_one_who_runs_the_most_distance_in);
            goalTxt.setText(firstValue + getString(R.string.spaced_h) + secondValue + R.string.min);
        }

        view.findViewById(R.id.cancel_btn).setOnClickListener(this);
        view.findViewById(R.id.decline_btn).setOnClickListener(this);
        view.findViewById(R.id.accept_btn).setOnClickListener(this);

        dialog = builder.create();

        return dialog;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.cancel_btn:
                mListener.onReceiveChallengeDialogCancelClick(ReceiveChallengeDialogFragment.this);
                dialog.dismiss();
                break;

            case R.id.decline_btn:
                mListener.onReceiveChallengeDialogDeclineClick(ReceiveChallengeDialogFragment.this);
                dialog.dismiss();
                break;
            case R.id.accept_btn:
                mListener.onReceiveChallengeDialogAcceptClick(ReceiveChallengeDialogFragment.this);
                dialog.dismiss();
                break;
        }
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
            // Instantiate the ReceiveChallengeDialogListener so we can sendMessage events to the host
            mListener = (ReceiveChallengeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ReceiveChallengeDialogListener");
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
