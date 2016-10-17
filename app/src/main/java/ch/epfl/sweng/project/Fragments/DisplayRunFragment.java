package ch.epfl.sweng.project.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public DisplayRunFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param runToBeDisplayed The run to be displayed.
     * @return A new instance of fragment RunningMapFragment.
     */
    // TODO: Rename and change types and number of parameters
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

        View view = inflater.inflate(R.layout.simple_listview, container, false);

        if (mRunToBeDisplayed != null) {

            Track track = mRunToBeDisplayed.getTrack();
            TableLayout table = (TableLayout) view.findViewById(R.id.table);

            TableRow row = new TableRow(this.getContext());

            TextView name = new TextView(this.getContext());
            name.setText(mRunToBeDisplayed.getName());

            TextView duration = new TextView(this.getContext());
            duration.setText(String.valueOf(track.getDuration()));

            TextView distance = new TextView(this.getContext());
            distance.setText(String.valueOf(track.getDistance()));

            row.addView(name);
            row.addView(duration);
            row.addView(distance);

            table.addView(row);
        }

        return view;
    }

    public void onButtonPressed() {

        if (mListener != null) {
            mListener.onDisplayRunInteraction();
        }
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
