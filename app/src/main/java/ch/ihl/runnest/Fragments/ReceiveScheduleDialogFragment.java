package ch.ihl.runnest.Fragments;

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

import com.example.android.multidex.ch.ihl.runnest.AppRunnest.R;

import java.util.Calendar;
import java.util.Date;

import ch.ihl.runnest.Model.Challenge;

/**
 * this class display a Dialog which asks the User to accept or decline the tentative scheduling of a challenge.
 */
public class ReceiveScheduleDialogFragment extends DialogFragment implements View.OnClickListener {
    private Challenge.Type type;
    private Date scheduledDate;
    private String sender;
    private AlertDialog dialog;

    /**
     * Interface for the listener of this class.
     */
    public interface ReceiveScheduleDialogListener {
        void onReceiveScheduleDialogAcceptClick(DialogFragment dialog);
        void onReceiveScheduleDialogDeclineClick(DialogFragment dialog);
        void onReceiveScheduleDialogCancelClick(DialogFragment dialog);
    }

    private ReceiveScheduleDialogListener listener;

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_receive_schedule_dialog, null);

        builder.setCancelable(false);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view);

        ((TextView)view.findViewById(R.id.txt_requester)).setText(sender + getString(R.string.wants_to_schedule_a_run_based_on));
        TextView typeTxt = (TextView) view.findViewById(R.id.txt_challenge_type);
        TextView dateDescriptionTxt = (TextView) view.findViewById(R.id.txt_date_description);
        TextView dateTxt = (TextView) view.findViewById(R.id.txt_date);
        TextView timeDescriptionTxt = (TextView) view.findViewById(R.id.txt_time_description);
        TextView timeTxt = (TextView) view.findViewById(R.id.txt_time);
        ImageView typeImg = (ImageView) view.findViewById(R.id.type_img);
        if(type == Challenge.Type.TIME){
            typeTxt.setText("Time");
            typeImg.setImageDrawable(getResources().getDrawable(R.drawable.time_white));
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(scheduledDate);
        dateDescriptionTxt.setText(getString(R.string.on_date));
        dateTxt.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + getString(R.string.dash) + String.valueOf(calendar.get(Calendar.MONTH)+1) + getString(R.string.dash) + String.valueOf(calendar.get(Calendar.YEAR)));
        timeDescriptionTxt.setText(R.string.at);
        timeTxt.setText(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) + getString(R.string.colon_with_space) + String.valueOf(calendar.get(Calendar.MINUTE)));

        dialog = builder.create();

        view.findViewById(R.id.cancel_btn).setOnClickListener(this);
        view.findViewById(R.id.decline_btn).setOnClickListener(this);
        view.findViewById(R.id.accept_btn).setOnClickListener(this);

        return dialog;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.cancel_btn:
                listener.onReceiveScheduleDialogCancelClick(ReceiveScheduleDialogFragment.this);
                dialog.dismiss();
                break;

            case R.id.decline_btn:
                listener.onReceiveScheduleDialogDeclineClick(ReceiveScheduleDialogFragment.this);
                dialog.dismiss();
                break;
            case R.id.accept_btn:
                listener.onReceiveScheduleDialogAcceptClick(ReceiveScheduleDialogFragment.this);
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
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ReceiveChallengeDialogListener so we can sendMessage events to the host
            listener = (ReceiveScheduleDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ReceiveScheduleDialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /**
     * getter for the challenge type.
     * @return the challenge type
     */
    public Challenge.Type getType() {
        return type;
    }

    /**
     * getter for scheduled date.
     * @return the scheduled date
     */
    public Date getScheduledDate() {
        return scheduledDate;
    }
}
