package ch.epfl.sweng.project.Fragments.RunFragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapView;

import java.text.SimpleDateFormat;
import java.util.Date;

import ch.epfl.sweng.project.Activities.SideBarActivity;
import ch.epfl.sweng.project.AppRunnest;
import ch.epfl.sweng.project.Database.DBHelper;
import ch.epfl.sweng.project.Firebase.FirebaseHelper;
import ch.epfl.sweng.project.Model.Run;
import ch.epfl.sweng.project.Model.User;


/**
 * This Fragment represent our simplest kind of run. By implementing <code>RunFragment</code>
 * it takes care of showing all necessary information about the run it represents.
 *
 * In particular the <code>Track</code> is shown on a map, as well as the distance the user ran
 * right now and the elapsed time.
 *
 * Also it takes care that, once finished, the <code>Run</code> is stored on the local database.
 */
public class RunningMapFragment extends RunFragment {

    // Live stats
    private Chronometer mChronometer = null;

    // Buttons
    private Button mStartUpdatesButton = null;
    private Button mStopUpdatesButton = null;

    private RunningMapFragmentInteractionListener mListener = null;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_running_map, container, false);

        // Buttons
        GUISetup(view);

        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this); //this is important

        // Location
        setupLocation();

        return view;
    }

    private void setupLocation() {

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mRequestingLocationUpdates = false;
        mLocationSettingsHandler = new LocationSettingsHandler(mGoogleApiClient, getActivity());
        mLocationSettingsHandler.checkLocationSettings();
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
                startButtonPressed();
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

        mDistance = (TextView) view.findViewById(R.id.distance);
    }


    /**
     * Include all actions to perform when Start button is pressed
     */
    private void startButtonPressed() {

        if(checkPermission() && mLocationSettingsHandler.checkLocationSettings()) {

            // Set user as unavailable
            new FirebaseHelper().
                    setUserAvailable(((AppRunnest) getActivity().getApplication()).getUser().getEmail(), false, false);

            // initialize new Run
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy HH:mm:ss");
            String runName = dateFormat.format(new Date());
            mRun = new Run(runName);
            super.startRun();
            // Prevent sleeping
            getView().setKeepScreenOn(true);

            mStartUpdatesButton.setVisibility(View.INVISIBLE);
            mStopUpdatesButton.setVisibility(View.VISIBLE);

            mChronometer.setVisibility(View.VISIBLE);
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();

            ((SideBarActivity)getActivity()).setRunning(true);

            setButtonsEnabledState();
        }
    }

    private void stopButtonPressed() {
        if (mRequestingLocationUpdates) {
            super.stopRun();
            // Allow sleeping
            getView().setKeepScreenOn(false);

            setButtonsEnabledState();
            mChronometer.stop();
            ((SideBarActivity)getActivity()).setRunning(false);

            DBHelper dbHelper = new DBHelper(getContext());
            //TODO: verify that insertion has been performed correctly
            dbHelper.insert(mRun);

            FirebaseHelper firebaseHelper = new FirebaseHelper();

            // Set user as available
            firebaseHelper.
                    setUserAvailable(((AppRunnest) getActivity().getApplication()).getUser().getEmail(), false, true);

            //update user statistics
            User currentUser = ((AppRunnest) getActivity().getApplication()).getUser();
            firebaseHelper.updateUserStatistics(currentUser.getEmail(), mRun.getDuration(),
                    mRun.getTrack().getDistance(), FirebaseHelper.RunType.SINGLE);

            // upload database
            ((AppRunnest)getActivity().getApplication()).launchDatabaseUpload();

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

    /**
     * Check <code>ACCESS_FINE_LOCATION</code> permission, if necessary request it.
     * This check is necessary only with Android 6.0+ and/or SDK 22+
     */
    private boolean checkPermission() {
        int fineLocation = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);

        if (fineLocation != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        SideBarActivity.PERMISSION_REQUEST_CODE_FINE_LOCATION);
            }
            return false;
        } else {
            return true;
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