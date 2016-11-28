package ch.epfl.sweng.project.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

import java.util.Date;

import ch.epfl.sweng.project.Activities.ChallengeActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MemoDialogFragment.MemoDialogListener} interface
 * to handle interaction events.
 */
public class MemoDialogFragment extends DialogFragment {
    private ChallengeActivity.ChallengeType type;
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
    public interface MemoDialogListener {
        void onMemoDialogCloseClick(DialogFragment dialog);
        void onMemoDialogDeleteClick(DialogFragment dialog);
    }

    MemoDialogFragment.MemoDialogListener mListener;

    public MemoDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_memo_dialog, null);

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
        });

        ((TextView)view.findViewById(R.id.txt_requester)).setText("you and " + sender + " scheduled a run based on");
        typeTxt = (TextView) view.findViewById(R.id.txt_challenge_type);
        dateDescriptionTxt = (TextView) view.findViewById(R.id.txt_date_description);
        dateTxt = (TextView) view.findViewById(R.id.txt_date);
        timeDescriptionTxt = (TextView) view.findViewById(R.id.txt_time_description);
        timeTxt = (TextView) view.findViewById(R.id.txt_time);
        typeTxt.setText(type.toString());

        dateDescriptionTxt.setText("On date");
        dateTxt.setText(String.valueOf(scheduledDate.getDay()) + "-" + String.valueOf(scheduledDate.getMonth()) + "-" + String.valueOf(scheduledDate.getYear()));
        timeDescriptionTxt.setText("At");
        timeTxt.setText(String.valueOf(scheduledDate.getHours()) + " : " + String.valueOf(scheduledDate.getMinutes()));

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
        type = (ChallengeActivity.ChallengeType)args.get("type");
        scheduledDate = (Date) args.get("date");
        sender = args.getString("sender");
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
                    + " must implement RequestDialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public ChallengeActivity.ChallengeType getType() {
        return type;
    }

    public Date getScheduledDate() {
        return scheduledDate;
    }
}
