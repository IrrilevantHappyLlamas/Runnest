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
import ch.epfl.sweng.project.AppRunnest;


public class ChallengeDialogFragment extends DialogFragment implements View.OnClickListener {

    //Challenge type
    Button distanceBtn;
    Button timeBtn;
    ChallengeActivity.ChallengeType type;

    TextView setParameter;

    NumberPicker firstPicker;
    NumberPicker secondPicker;
    TextView firstUnit;
    TextView secondUnit;

    int firstValue;
    int secondValue;


    /* The activity that creates an instance of this dialog fragment must
 * implement this interface in order to receive event callbacks.
 * Each method passes the DialogFragment in case the host needs to query it. */
    public interface ChallengeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    ChallengeDialogListener mListener;

    public ChallengeDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_customize_challenge, null);

        builder.setCancelable(false);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Challenge!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // launch challenge
                        if(firstValue + secondValue != 0)  {
                            mListener.onDialogPositiveClick(ChallengeDialogFragment.this);
                        } else {
                            if(((AppRunnest)getActivity().getApplicationContext()).isTestSession()){
                                firstValue = 1;
                                mListener.onDialogPositiveClick(ChallengeDialogFragment.this);
                            } else {
                                Toast.makeText(getContext(), "The goal of the challenge cannot be 0!",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //cancel
                        // Send the positive button event back to the host activity
                        mListener.onDialogNegativeClick(ChallengeDialogFragment.this);
                    }
                });

        firstPicker = (NumberPicker) view.findViewById(R.id.first_picker);
        secondPicker = (NumberPicker) view.findViewById(R.id.second_picker);
        setParameter = (TextView) view.findViewById(R.id.txt_set_parameter);
        firstUnit = (TextView) view.findViewById(R.id.txt_first_unit);
        secondUnit = (TextView) view.findViewById(R.id.txt_second_unit);
        distanceBtn = (Button) view.findViewById(R.id.btn_distance);
        timeBtn = (Button) view.findViewById(R.id.btn_time);


        firstPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                firstValue = newVal;
                System.out.println("FIRST PICKER VALUE = " + newVal);
            }
        });

        secondPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(type == ChallengeActivity.ChallengeType.DISTANCE){
                    secondValue = newVal*100;
                } else {
                    secondValue = newVal*5;
                }

                System.out.println("SECOND PICKER VALUE = " + newVal);
                System.out.println("SECOND VALUE = " + secondValue);
            }
        });

        distanceBtn.setOnClickListener(this);
        timeBtn.setOnClickListener(this);

        distanceBtn.performClick();

        return builder.create();
    }

    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_distance:
                distanceBtn.setBackgroundColor(Color.RED);
                timeBtn.setBackgroundColor(Color.GRAY);
                setParameter.setText("Set the distance");
                firstUnit.setText("km");
                secondUnit.setText("m");

                firstPicker.setValue(0);
                firstValue = 0;
                firstPicker.setMinValue(0);
                firstPicker.setMaxValue(100);
                firstPicker.setWrapSelectorWheel(true);


                String[] meters = new String[10];
                for(int i=0; i<meters.length; i++) {
                    meters[i] = Integer.toString((i)*100);
                }
                secondPicker.setValue(0);
                secondValue = 0;
                secondPicker.setMaxValue(9);
                secondPicker.setDisplayedValues(meters);
                secondPicker.setMinValue(0);
                secondPicker.setMaxValue(9);
                secondPicker.setWrapSelectorWheel(true);


                /*
                secondPicker.setMinValue(0);
                secondPicker.setMaxValue(999);
                secondPicker.setWrapSelectorWheel(true);*/

                type = ChallengeActivity.ChallengeType.DISTANCE;
                break;

            case R.id.btn_time:
                distanceBtn.setBackgroundColor(Color.GRAY);
                timeBtn.setBackgroundColor(Color.RED);
                setParameter.setText("Set the time");
                firstUnit.setText("h");
                secondUnit.setText("min");

                firstPicker.setValue(0);
                firstValue = 0;
                firstPicker.setMinValue(0);
                firstPicker.setMaxValue(10);
                firstPicker.setWrapSelectorWheel(true);

                String[] minutes = new String[12];
                for(int i=0; i<minutes.length; i++) {
                    minutes[i] = Integer.toString((i)*5);
                }
                secondPicker.setValue(0);
                secondValue = 0;
                secondPicker.setDisplayedValues(minutes);
                secondPicker.setMinValue(0);
                secondPicker.setMaxValue(11);
                secondPicker.setWrapSelectorWheel(true);

                /*
                secondPicker.setMinValue(0);
                secondPicker.setMaxValue(59);
                secondPicker.setWrapSelectorWheel(true);*/

                type = ChallengeActivity.ChallengeType.TIME;
                break;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ChallengeDialogListener so we can send events to the host
            mListener = (ChallengeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ChallengeDialogListener");
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

    public int getSecondValue() {
        return secondValue;
    }

    public int getFirstValue() {
        return firstValue;
    }
}
