package ch.epfl.sweng.project.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import ch.epfl.sweng.project.Model.Run;
import ch.epfl.sweng.project.Model.Track;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DisplayRunFragment.OnDisplayRunInteractionListener} interface
 * to handle interaction events.
 */
public class DisplayRunFragment extends Fragment {

    private static final String ARG_RUNTOBEDISPLAYED = "run to be displayed";
    private OnDisplayRunInteractionListener mListener;
    private Run mRunToBeDisplayed;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param runToBeDisplayed The run to be displayed.
     * @return A new instance of fragment RunningMapFragment.
     */
    public static DisplayRunFragment newInstance(Run runToBeDisplayed) {
        DisplayRunFragment fragment = new DisplayRunFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RUNTOBEDISPLAYED, runToBeDisplayed);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
             mRunToBeDisplayed = (Run) getArguments().getSerializable(ARG_RUNTOBEDISPLAYED);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_display_run, container, false);


        if (mRunToBeDisplayed != null) {

            Track track = mRunToBeDisplayed.getTrack();

            TableLayout table = (TableLayout) view.findViewById(R.id.table);

            // Name Row
            TableRow firstRow = new TableRow(this.getContext());
            createRowElement(firstRow, "Name :");
            createRowElement(firstRow, mRunToBeDisplayed.getName());
            table.addView(firstRow);

            // Distance Row
            TableRow secondRow = new TableRow(this.getContext());
            createRowElement(secondRow, "Distance :");
            createRowElement(secondRow,String.valueOf((int)track.getDistance()) + " m");
            table.addView(secondRow);

            // Duration Row
            TableRow thirdRow = new TableRow(this.getContext());
            createRowElement(thirdRow, "Duration :");
            long minutes = track.getDuration() / 60;
            long seconds = track.getDuration() % 60;
            createRowElement(thirdRow, minutes + "' " + seconds + "''");
            table.addView(thirdRow);

            // Uphill Row
            TableRow fourthRow = new TableRow(this.getContext());
            createRowElement(fourthRow, "Uphill :");
            createRowElement( fourthRow,String.valueOf((int)track.getUphill()) + " m");
            table.addView(fourthRow);

            // Downhill Row
            TableRow fifthRow = new TableRow(this.getContext());
            createRowElement(fifthRow, "Downhill :");
            createRowElement( fifthRow,String.valueOf((int)track.getDownhill()) + " m");
            table.addView(fifthRow);

            Button button = (Button) view.findViewById(R.id.button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mListener != null) {
                        mListener.onDisplayRunInteraction();
                    }
                }
            });
        }

        return view;
    }

    private void createRowElement(TableRow row, String text){

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams();
        layoutParams.setMargins(10, 50, 20, 10);
        TextView element = new TextView(this.getContext());
        element.setText(text);
        element.setTextSize(20);
        element.setLayoutParams(layoutParams);

        row.addView(element);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDisplayRunInteractionListener) {
            mListener = (OnDisplayRunInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnDisplayRunInteractionListener {

        void onDisplayRunInteraction();
    }
}
