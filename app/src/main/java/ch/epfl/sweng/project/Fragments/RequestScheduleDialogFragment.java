package ch.epfl.sweng.project.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import java.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

import ch.epfl.sweng.project.Activities.ChallengeActivity;
import ch.epfl.sweng.project.AppRunnest;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RequestScheduleDialogFragment.OnRequestScheduleDialogListener} interface
 * to handle interaction events.
 */
public class RequestScheduleDialogFragment extends DialogFragment implements View.OnClickListener {

    //Challenge type
    Button distanceBtn;
    Button timeBtn;

    DatePicker datePicker;
    TimePicker timePicker;

    //gathered info
    private int scheduledYear;
    private int scheduledMonth;
    private int scheduledDay;
    private int scheduledHour;
    private int scheduledMinute;
    ChallengeActivity.ChallengeType type;


    /* The activity that creates an instance of this dialog fragment must
 * implement this interface in order to receive event callbacks.
 * Each method passes the DialogFragment in case the host needs to query it. */
    public interface OnRequestScheduleDialogListener {
        void onRequestScheduleDialogPositiveClick(DialogFragment dialog);
        void onRequestScheduleDialogNegativeClick(DialogFragment dialog);
    }

    RequestScheduleDialogFragment.OnRequestScheduleDialogListener mListener;

    public RequestScheduleDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_request_schedule_dialog, null);

        setCurrentDateAndTime(view);

        builder.setCancelable(false);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Schedule!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        mListener.onRequestScheduleDialogPositiveClick(RequestScheduleDialogFragment.this);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        mListener.onRequestScheduleDialogNegativeClick(RequestScheduleDialogFragment.this);
                    }
                });

        distanceBtn = (Button) view.findViewById(R.id.button_distance);
        timeBtn = (Button) view.findViewById(R.id.button_time);

        distanceBtn.setOnClickListener(this);
        timeBtn.setOnClickListener(this);

        distanceBtn.performClick();

        return builder.create();
    }

    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_distance:
                distanceBtn.setBackgroundColor(Color.RED);
                timeBtn.setBackgroundColor(Color.GRAY);

                type = ChallengeActivity.ChallengeType.DISTANCE;
                break;

            case R.id.button_time:
                distanceBtn.setBackgroundColor(Color.GRAY);
                timeBtn.setBackgroundColor(Color.RED);

                type = ChallengeActivity.ChallengeType.TIME;
                break;
        }
    }

    public void setCurrentDateAndTime(View view) {

        datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        timePicker = (TimePicker) view.findViewById(R.id.timePicker);

        final Calendar c = Calendar.getInstance();

        scheduledYear = c.get(Calendar.YEAR);
        scheduledMonth = c.get(Calendar.MONTH);
        scheduledDay = c.get(Calendar.DAY_OF_MONTH);
        scheduledHour = c.get(Calendar.HOUR);
        scheduledMinute = c.get(Calendar.MINUTE);

        datePicker.init(scheduledYear, scheduledMonth, scheduledDay, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                scheduledYear = year;
                scheduledMonth = monthOfYear;
                scheduledDay = dayOfMonth;
            }
        });

        timePicker.setCurrentHour(scheduledHour);
        timePicker.setCurrentMinute(scheduledMinute);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                scheduledHour = hourOfDay;
                scheduledMinute = minute;
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ChallengeDialogListener so we can send events to the host
            mListener = (RequestScheduleDialogFragment.OnRequestScheduleDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement OnRequestScheduleDialogListener");
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

    public int getScheduledDay() {
        return scheduledDay;
    }

    public int getScheduledHour() {
        return scheduledHour;
    }

    public int getScheduledMinute() {
        return scheduledMinute;
    }

    public int getScheduledYear() {
        return scheduledYear;
    }

    public int getScheduledMonth() {
        return scheduledMonth;
    }
}
