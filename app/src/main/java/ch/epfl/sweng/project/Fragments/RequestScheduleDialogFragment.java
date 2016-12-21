package ch.epfl.sweng.project.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import java.util.Calendar;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

import ch.epfl.sweng.project.Activities.ChallengeActivity;
import ch.epfl.sweng.project.AppRunnest;
import ch.epfl.sweng.project.Model.Challenge;
import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * This class displays a schedule request dialog.
 */
public class RequestScheduleDialogFragment extends DialogFragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    // Challenge type
    private SegmentedGroup typeSG;
    private RadioButton distanceRadio;

    DatePicker datePicker;
    TimePicker timePicker;

    private Calendar scheduledCalendar;
    Challenge.Type type;
    private AlertDialog dialog;

    /**
     * interface for the listener of this class.
     */
    public interface OnRequestScheduleDialogListener {
        void onRequestScheduleDialogPositiveClick(DialogFragment dialog);
        void onRequestScheduleDialogNegativeClick(DialogFragment dialog);
    }

    RequestScheduleDialogFragment.OnRequestScheduleDialogListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_request_schedule_dialog, null);

        setCurrentDateAndTime(view);

        builder.setCancelable(false);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view);

        typeSG = (SegmentedGroup) view.findViewById(R.id.type_sg);
        distanceRadio = (RadioButton) view.findViewById(R.id.distance_radio);

        typeSG.setOnCheckedChangeListener(this);
        view.findViewById(R.id.schedule_positive_btn).setOnClickListener(this);
        view.findViewById(R.id.schedule_negative_btn).setOnClickListener(this);

        distanceRadio.performClick();
        dialog = builder.create();
        return dialog;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.schedule_negative_btn:
                mListener.onRequestScheduleDialogNegativeClick(RequestScheduleDialogFragment.this);
                dialog.dismiss();
                break;

            case R.id.schedule_positive_btn:
                Toast.makeText(getContext(), R.string.challenge_scheduled,
                        Toast.LENGTH_LONG).show();
                mListener.onRequestScheduleDialogPositiveClick(RequestScheduleDialogFragment.this);
                dialog.dismiss();
                break;
        }
    }

    private void setCurrentDateAndTime(View view) {

        datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        timePicker = (TimePicker) view.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        scheduledCalendar = Calendar.getInstance();

        datePicker.init(scheduledCalendar.get(Calendar.YEAR), scheduledCalendar.get(Calendar.MONTH), scheduledCalendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                scheduledCalendar.set(year, monthOfYear, dayOfMonth);
            }
        });

        timePicker.setCurrentHour(scheduledCalendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(scheduledCalendar.get(Calendar.MINUTE));

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                scheduledCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                scheduledCalendar.set(Calendar.MINUTE, minute);
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ChallengeDialogListener so we can sendMessage events to the host
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

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(distanceRadio.isChecked()){
            type = Challenge.Type.DISTANCE;
        } else {
            type = Challenge.Type.TIME;
        }
    }

    /**
     * getter for the challenge type.
     * @return the challenge type.
     */
    public Challenge.Type getType() {
        return type;
    }

    /**
     * getter for scheduled calendar.
     * @return the scheduled calendar.
     */
    public Calendar getScheduledCalendar() {
        return scheduledCalendar;
    }
}
