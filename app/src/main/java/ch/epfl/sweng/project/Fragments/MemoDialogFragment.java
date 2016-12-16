package ch.epfl.sweng.project.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

import java.util.Calendar;
import java.util.Date;

import ch.epfl.sweng.project.Activities.ChallengeActivity;
import ch.epfl.sweng.project.Model.Challenge;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MemoDialogFragment.MemoDialogListener} interface
 * to handle interaction events.
 */
public class MemoDialogFragment extends DialogFragment implements View.OnClickListener {
    private Challenge.Type type;
    private Date scheduledDate;
    private String sender;
    private String opponentEmail;
    private TextView typeTxt;
    private TextView dateDescriptionTxt;
    private TextView dateTxt;
    private TextView timeDescriptionTxt;
    private TextView timeTxt;
    private ImageView typeImg;
    private AlertDialog dialog;

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface MemoDialogListener {
        void onMemoDialogCloseClick(DialogFragment dialog);
        void onMemoDialogDeleteClick(DialogFragment dialog);
        void onMemoDialogChallengeClick(DialogFragment dialog);
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
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_memo_dialog, null);

        builder.setCancelable(false);
        builder.setView(view);

        ((TextView)view.findViewById(R.id.txt_requester)).setText(getString(R.string.you_and) + sender + getString(R.string.scheduled_a_run_based_on));
        typeTxt = (TextView) view.findViewById(R.id.txt_challenge_type);
        dateDescriptionTxt = (TextView) view.findViewById(R.id.txt_date_description);
        dateTxt = (TextView) view.findViewById(R.id.txt_date);
        timeDescriptionTxt = (TextView) view.findViewById(R.id.txt_time_description);
        timeTxt = (TextView) view.findViewById(R.id.txt_time);
        typeImg = (ImageView) view.findViewById(R.id.type_img);
        if(type == Challenge.Type.TIME){
            typeTxt.setText("Time");
            typeImg.setImageDrawable(getResources().getDrawable(R.drawable.time_white));
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(scheduledDate);
        dateDescriptionTxt.setText(getString(R.string.on_date));
        dateTxt.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + getString(R.string.righetta) + String.valueOf(calendar.get(Calendar.MONTH)+1) + getString(R.string.righetta) + String.valueOf(calendar.get(Calendar.YEAR)));
        timeDescriptionTxt.setText(getString(R.string.at));
        timeTxt.setText(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) + getString((R.string.due_punti)) + String.valueOf(calendar.get(Calendar.MINUTE)));

        dialog = builder.create();

        view.findViewById(R.id.cancel_btn).setOnClickListener(this);
        view.findViewById(R.id.decline_btn).setOnClickListener(this);
        view.findViewById(R.id.accept_btn).setOnClickListener(this);

        return dialog;
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.cancel_btn:
                mListener.onMemoDialogCloseClick(MemoDialogFragment.this);
                dialog.dismiss();
                break;

            case R.id.decline_btn:
                mListener.onMemoDialogDeleteClick(MemoDialogFragment.this);
                dialog.dismiss();
                break;
            case R.id.accept_btn:
                mListener.onMemoDialogChallengeClick(MemoDialogFragment.this);
                dialog.dismiss();
                break;
        }
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

    public String getOpponentEmail() {
        return opponentEmail;
    }

    public String getSender() {
        return sender;
    }
}
