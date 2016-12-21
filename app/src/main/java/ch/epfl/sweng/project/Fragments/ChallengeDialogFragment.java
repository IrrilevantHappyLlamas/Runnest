package ch.epfl.sweng.project.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

import ch.epfl.sweng.project.Activities.ChallengeActivity;
import ch.epfl.sweng.project.AppRunnest;
import ch.epfl.sweng.project.Model.Challenge;
import info.hoang8f.android.segmented.SegmentedGroup;


public class ChallengeDialogFragment extends DialogFragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private AlertDialog dialog;

    //Challenge type
    private Challenge.Type type;

    private NumberPicker firstPicker;
    private NumberPicker secondPicker;
    private TextView firstUnit;
    private TextView secondUnit;

    private int firstValue;
    private int secondValue;
    private SegmentedGroup typeSG;
    private RadioButton distanceRadio;
    private RadioButton timeRadio;

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
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_customize_challenge, null);

        builder.setCancelable(false);
        builder.setView(view);

        firstPicker = (NumberPicker) view.findViewById(R.id.first_picker);
        secondPicker = (NumberPicker) view.findViewById(R.id.second_picker);
        firstUnit = (TextView) view.findViewById(R.id.txt_first_unit);
        secondUnit = (TextView) view.findViewById(R.id.txt_second_unit);
        typeSG = (SegmentedGroup) view.findViewById(R.id.type_sg);
        distanceRadio = (RadioButton) view.findViewById(R.id.distance_radio);
        timeRadio = (RadioButton) view.findViewById(R.id.time_radio);

        typeSG.setOnCheckedChangeListener(this);

        setDividerColor(firstPicker, Color.GREEN);
        setDividerColor(secondPicker, Color.GREEN);

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
                if(type == Challenge.Type.DISTANCE){
                    secondValue = newVal*100;
                } else {
                    secondValue = newVal*5;
                }

                System.out.println("SECOND PICKER VALUE = " + newVal);
                System.out.println("SECOND VALUE = " + secondValue);
            }
        });

        distanceRadio.performClick();

        dialog = builder.create();

        view.findViewById(R.id.customize_positive_btn).setOnClickListener(this);
        view.findViewById(R.id.customize_negative_btn).setOnClickListener(this);

        return dialog;
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.customize_negative_btn:
                mListener.onDialogNegativeClick(ChallengeDialogFragment.this);
                dialog.dismiss();
                break;

            case R.id.customize_positive_btn:
                if(firstValue + secondValue != 0)  {
                    mListener.onDialogPositiveClick(ChallengeDialogFragment.this);
                    dialog.dismiss();
                } else {
                    if(((AppRunnest)getActivity().getApplicationContext()).isTestSession()){
                        firstValue = 1;
                        mListener.onDialogPositiveClick(ChallengeDialogFragment.this);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), R.string.challenge_not_null,
                                Toast.LENGTH_LONG).show();
                    }
                }

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
            // Instantiate the ChallengeDialogListener so we can sendMessage events to the host
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

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(distanceRadio.isChecked()){
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

            type = Challenge.Type.DISTANCE;
        } else {
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

            type = Challenge.Type.TIME;
        }
    }

    private void setDividerColor(NumberPicker picker, int color) {

        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public Challenge.Type getType() {
        return type;
    }

    public int getSecondValue() {
        return secondValue;
    }

    public int getFirstValue() {
        return firstValue;
    }
}
