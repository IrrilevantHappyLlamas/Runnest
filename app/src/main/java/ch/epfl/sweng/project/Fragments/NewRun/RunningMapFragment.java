package ch.epfl.sweng.project.Fragments.NewRun;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.android.gms.maps.MapView;

import ch.epfl.sweng.project.Activities.SideBarActivity;
import ch.epfl.sweng.project.Database.DBHelper;
import ch.epfl.sweng.project.Model.Run;

public class RunningMapFragment extends RunFragment {
    // Live stats
    private Chronometer mChronometer = null;

    // Buttons
    private Button mStopUpdatesButton = null;


    private RunningMapFragmentInteractionListener mListener = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_running_map, container, false);

        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this); //this is important

        // Location
        super.setupLocation();

        // Buttons
        GUISetup(view);

        return view;
    }

    /**
     * Setup the two buttons of the fragment: Start and Stop.
     *
     * @param view <code>View</code> where buttons must be added
     */
    private void GUISetup(View view) {

        //Buttons
        mStartUpdatesButton = (Button) view.findViewById(R.id.start_run);
        mStartUpdatesButton.setVisibility(View.VISIBLE);
        mStartUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUpdatesButtonPressed();
            }
        });

        mStopUpdatesButton = (Button) view.findViewById(R.id.stop_run);
        mStopUpdatesButton.setVisibility(View.INVISIBLE);
        mStopUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopButtonPressed();
            }
        });

        setButtonsEnabledState();



        // Live stats
        mChronometer = (Chronometer) view.findViewById(R.id.chronometer);
        mChronometer.setVisibility(View.INVISIBLE);

        mDistance = (TextView) view.findViewById(R.id.distance);
        mDistance.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void startRun() {
        super.startRun();

        mStopUpdatesButton.setVisibility(View.VISIBLE);

        mChronometer.setVisibility(View.VISIBLE);
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();

        ((SideBarActivity)getActivity()).setRunning(true);

        setButtonsEnabledState();
    }

    private void stopButtonPressed() {
        if (mRequestingLocationUpdates) {
            mRequestingLocationUpdates = false;
            setButtonsEnabledState();
            super.stopLocationUpdates();

            mChronometer.stop();
            mRun.stop();
            ((SideBarActivity)getActivity()).setRunning(false);


            DBHelper dbHelper = new DBHelper(getContext());
            //TODO: verify that insertion has been performed correctly
            dbHelper.insert(mRun);

            mListener.onRunningMapFragmentInteraction(new Run(mRun));
        }
    }

    private void setButtonsEnabledState() {
        if (mRequestingLocationUpdates) {
            mStartUpdatesButton.setEnabled(false);
            mStopUpdatesButton.setEnabled(true);
        } else {
            mStopUpdatesButton.setEnabled(false);
            mStartUpdatesButton.setEnabled(true);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RunningMapFragmentInteractionListener) {
            mListener = (RunningMapFragmentInteractionListener) context;
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

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public interface RunningMapFragmentInteractionListener {
        void onRunningMapFragmentInteraction(Run run);
    }
}