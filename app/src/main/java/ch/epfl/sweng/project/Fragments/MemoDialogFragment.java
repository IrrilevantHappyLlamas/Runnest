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
import android.widget.Toast;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

import ch.epfl.sweng.project.Firebase.FirebaseHelper;
import ch.epfl.sweng.project.Model.Challenge;

/**
 * This class displays a memo Dialog which reminds the user of a scheduled challenge.
 */
public class MemoDialogFragment extends DialogFragment implements View.OnClickListener {
    private Challenge.Type type;
    private Date scheduledDate;
    private String sender;
    private String opponentEmail;

    private AlertDialog dialog;

    /**
     * interface for the listener of this class.
     */
    public interface MemoDialogListener {
        void onMemoDialogCloseClick(DialogFragment dialog);
        void onMemoDialogDeleteClick(DialogFragment dialog);
        void onMemoDialogChallengeClick(DialogFragment dialog);
    }

    private MemoDialogFragment.MemoDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_memo_dialog, null);

        builder.setCancelable(false);
        builder.setView(view);

        ((TextView)view.findViewById(R.id.txt_requester)).setText(getString(R.string.you_and) + sender + getString(R.string.scheduled_a_run_based_on));
        TextView typeTxt = (TextView) view.findViewById(R.id.txt_challenge_type);
        TextView dateDescriptionTxt = (TextView) view.findViewById(R.id.txt_date_description);
        TextView dateTxt = (TextView) view.findViewById(R.id.txt_date);
        TextView timeDescriptionTxt = (TextView) view.findViewById(R.id.txt_time_description);
        TextView timeTxt = (TextView) view.findViewById(R.id.txt_time);
        ImageView typeImg = (ImageView) view.findViewById(R.id.type_img);
        if(type == Challenge.Type.TIME){
            typeTxt.setText(R.string.Time);
            typeImg.setImageDrawable(getResources().getDrawable(R.drawable.time_white));
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(scheduledDate);
        dateDescriptionTxt.setText(getString(R.string.on_date));
        dateTxt.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + getString(R.string.dash) + String.valueOf(calendar.get(Calendar.MONTH)+1) + getString(R.string.dash) + String.valueOf(calendar.get(Calendar.YEAR)));
        timeDescriptionTxt.setText(getString(R.string.at));
        timeTxt.setText(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) + getString((R.string.colon_with_space)) + String.valueOf(calendar.get(Calendar.MINUTE)));

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
                listener.onMemoDialogCloseClick(MemoDialogFragment.this);
                dialog.dismiss();
                break;

            case R.id.decline_btn:
                listener.onMemoDialogDeleteClick(MemoDialogFragment.this);
                dialog.dismiss();
                break;
            case R.id.accept_btn:

                new FirebaseHelper().listenUserAvailability(opponentEmail, false, new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if ((boolean) dataSnapshot.getValue()) {
                                listener.onMemoDialogChallengeClick(MemoDialogFragment.this);
                                dialog.dismiss();
                            } else {
                                Toast.makeText(getContext(), "User is currently busy, try again later",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            throw new DatabaseException("Corrupted available node for user");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw new DatabaseException("Cannot read available status for user");
                    }
                });
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
            // Instantiate the ReceiveChallengeDialogListener so we can sendMessage events to the host
            listener = (MemoDialogFragment.MemoDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement MemoDialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
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
