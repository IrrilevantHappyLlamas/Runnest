package ch.epfl.sweng.project.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

import java.util.Calendar;
import java.util.Date;

import ch.epfl.sweng.project.Activities.ChallengeActivity;
import ch.epfl.sweng.project.Model.Challenge;

/**
 * This class displays a memo Dialog which reminds the user of a scheduled challenge.
 */
public class MemoDialogFragment extends DialogFragment {
    private Challenge.Type type;
    private Date scheduledDate;
    private String sender;
    private String opponentEmail;

    /**
     * interface for the listener of this class.
     */
    public interface MemoDialogListener {
        void onMemoDialogCloseClick(DialogFragment dialog);
        void onMemoDialogDeleteClick(DialogFragment dialog);
        void onMemoDialogChallengeClick(DialogFragment dialog);
    }

    MemoDialogFragment.MemoDialogListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_memo_dialog, null);

        builder.setCancelable(false);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //cancel
                        // Send the positive button event back to the host activity
                        mListener.onMemoDialogCloseClick(MemoDialogFragment.this);
                    }
                }).setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                        mListener.onMemoDialogDeleteClick(MemoDialogFragment.this);
            }
                }).setPositiveButton("Challenge", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Start the challenge
                        mListener.onMemoDialogChallengeClick(MemoDialogFragment.this);
            }
        });

        ((TextView)view.findViewById(R.id.txt_requester)).setText(getString(R.string.you_and) + sender + getString(R.string.scheduled_a_run_based_on));
        TextView typeTxt = (TextView) view.findViewById(R.id.txt_challenge_type);
        TextView dateDescriptionTxt = (TextView) view.findViewById(R.id.txt_date_description);
        TextView dateTxt = (TextView) view.findViewById(R.id.txt_date);
        TextView timeDescriptionTxt = (TextView) view.findViewById(R.id.txt_time_description);
        TextView timeTxt = (TextView) view.findViewById(R.id.txt_time);
        typeTxt.setText(type.toString());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(scheduledDate);
        dateDescriptionTxt.setText(getString(R.string.on_date));
        dateTxt.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + getString(R.string.righetta) + String.valueOf(calendar.get(Calendar.MONTH)+1) + getString(R.string.righetta) + String.valueOf(calendar.get(Calendar.YEAR)));
        timeDescriptionTxt.setText(getString(R.string.at));
        timeTxt.setText(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) + getString((R.string.due_punti)) + String.valueOf(calendar.get(Calendar.MINUTE)));

        return builder.create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        type = (Challenge.Type)args.get("type");
        scheduledDate = (Date) args.get("date");
        sender = args.getString("sender");
        opponentEmail = args.getString("opponentEmail");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the RequestDialogListener so we can send events to the host
            mListener = (MemoDialogFragment.MemoDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement MemoDialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * getter for the challenge type.
     * @return the challenge type.
     */
    public Challenge.Type getType() {
        return type;
    }

    /**
     * getter for the opponent mail.
     * @return the opponent mail.
     */
    public String getOpponentEmail() {
        return opponentEmail;
    }

    /**
     * getter for the sender address.
     * @return the sender address.
     */
    public String getSender() {
        return sender;
    }
}
