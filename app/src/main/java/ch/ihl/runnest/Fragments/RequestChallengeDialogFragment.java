package ch.ihl.runnest.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.multidex.ch.ihl.runnest.AppRunnest.R;

import ch.ihl.runnest.AppRunnest;
import ch.ihl.runnest.Model.Challenge;
import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * this class display a dialog that allows to send a challenge request.
 */
public class RequestChallengeDialogFragment extends DialogFragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private AlertDialog dialog;

    //Challenge type
    private Challenge.Type type;

    private NumberPicker firstPicker;
    private NumberPicker secondPicker;
    private TextView firstUnit;
    private TextView secondUnit;
    private int firstValue;
    private int secondValue;
    private RadioButton distanceRadio;

    /**
     * interface for the listener of this class.
     */
    public interface SendChallengeDialogListener {
        void onSendChallengeDialogPositiveClick(DialogFragment dialog);
        void onSendChallengeDialogNegativeClick(DialogFragment dialog);
    }

    private SendChallengeDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_send_challenge_dialog, null);

        builder.setCancelable(false);
        builder.setView(view);


        firstPicker = (NumberPicker) view.findViewById(R.id.first_picker);
        secondPicker = (NumberPicker) view.findViewById(R.id.second_picker);
        firstUnit = (TextView) view.findViewById(R.id.txt_first_unit);
        secondUnit = (TextView) view.findViewById(R.id.txt_second_unit);
        SegmentedGroup typeSG = (SegmentedGroup) view.findViewById(R.id.type_sg);
        distanceRadio = (RadioButton) view.findViewById(R.id.distance_radio);

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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.customize_negative_btn:
                listener.onSendChallengeDialogNegativeClick(RequestChallengeDialogFragment.this);
                dialog.dismiss();
                break;

            case R.id.customize_positive_btn:
                if(firstValue + secondValue != 0)  {
                    listener.onSendChallengeDialogPositiveClick(RequestChallengeDialogFragment.this);
                    dialog.dismiss();
                } else {
                    if(((AppRunnest)getActivity().getApplicationContext()).isTestSession()){
                        firstValue = 1;
                        listener.onSendChallengeDialogPositiveClick(RequestChallengeDialogFragment.this);
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the SendChallengeDialogListener so we can sendMessage events to the host
            listener = (SendChallengeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement SendChallengeDialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(distanceRadio.isChecked()){
            firstUnit.setText(getString(R.string.km));
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
            secondUnit.setText(getString(R.string.min));

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
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    /**
     * Getter for the challenge type.
     *
     * @return the challenge type
     */
    public Challenge.Type getType() {
        return type;
    }

    /**
     * getter for the second value
     *
     * @return the second value
     */
    public int getSecondValue() {
        return secondValue;
    }

    /**
     * getter for the first value
     *
     * @return the first value
     */
    public int getFirstValue() {
        return firstValue;
    }
}