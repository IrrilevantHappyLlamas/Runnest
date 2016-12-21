package ch.epfl.sweng.project.Fragments.RunFragments;

import android.Manifest;
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
import java.util.Locale;

import ch.epfl.sweng.project.Activities.SideBarActivity;
import ch.epfl.sweng.project.AppRunnest;
import ch.epfl.sweng.project.Database.DBHelper;
import ch.epfl.sweng.project.Firebase.FirebaseHelper;
import ch.epfl.sweng.project.Model.Run;
import ch.epfl.sweng.project.Model.User;

/**
 * This Fragment represent our simplest kind of run. By implementing RunFragment
 * it takes care of showing all necessary information about the run it represents.
 *
 * In particular the Track is shown on a map, as well as the distance the user ran
 * right now and the elapsed time.
 *
 * Also it takes care that, once finished, the Run is stored on the local database.
 */
public class RunningMapFragment extends RunFragment {

    // Live stats
    private Chronometer chronometer = null;

    // Buttons
    private Button startUpdatesButton = null;
    private Button stopUpdatesButton = null;

    private RunningMapFragmentInteractionListener listener = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_running_map, container, false);

        // Buttons
        GUISetup(view);

        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this); //this is important

        // Location
        setupLocation();

        return view;
    }

    private void setupLocation() {

        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        requestingLocationUpdates = false;
        locationSettingsHandler = new LocationSettingsHandler(googleApiClient, getActivity());
        locationSettingsHandler.checkLocationSettings();
    }

    private void GUISetup(View view) {

        //Buttons
        startUpdatesButton = (Button) view.findViewById(R.id.start_run);
        startUpdatesButton.setVisibility(View.VISIBLE);
        startUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButtonPressed();
            }
        });

        stopUpdatesButton = (Button) view.findViewById(R.id.stop_run);
        stopUpdatesButton.setVisibility(View.INVISIBLE);
        stopUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopButtonPressed();
            }
        });

        setButtonsEnabledState();

        // Live stats
        chronometer = (Chronometer) view.findViewById(R.id.chronometer);

        distance = (TextView) view.findViewById(R.id.distance);
    }

    private void startButtonPressed() {

        if(checkPermission() && locationSettingsHandler.checkLocationSettings()) {

            // Set user as unavailable
            new FirebaseHelper().
                    setUserAvailable(((AppRunnest) getActivity().getApplication()).getUser().getEmail(), false, false);

            // initialize new Run
            SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy HH:mm:ss", Locale.ENGLISH);
            String runName = dateFormat.format(new Date());
            run = new Run(runName);
            super.startRun();
            // Prevent sleeping
            //noinspection ConstantConditions
            getView().setKeepScreenOn(true);

            startUpdatesButton.setVisibility(View.INVISIBLE);
            stopUpdatesButton.setVisibility(View.VISIBLE);

            chronometer.setVisibility(View.VISIBLE);
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();

            ((SideBarActivity)getActivity()).setRunning(true);

            setButtonsEnabledState();
        }
    }

    private void stopButtonPressed() {
        if (requestingLocationUpdates) {
            super.stopRun();
            // Allow sleeping
            //noinspection ConstantConditions
            getView().setKeepScreenOn(false);

            setButtonsEnabledState();
            chronometer.stop();
            ((SideBarActivity)getActivity()).setRunning(false);

            // Insert run in database
            DBHelper dbHelper = new DBHelper(getContext());
            dbHelper.insert(run);

            FirebaseHelper firebaseHelper = new FirebaseHelper();

            // Set user as available
            firebaseHelper.
                    setUserAvailable(((AppRunnest) getActivity().getApplication()).getUser().getEmail(), false, true);

            // update user statistics
            User currentUser = ((AppRunnest) getActivity().getApplication()).getUser();
            firebaseHelper.updateUserStatistics(currentUser.getEmail(), run.getDuration(),
                    run.getTrack().getDistance(), FirebaseHelper.RunType.SINGLE);

            // upload database
            ((AppRunnest)getActivity().getApplication()).launchDatabaseUpload();

            listener.onRunningMapFragmentInteraction(new Run(run));
        }
    }

    private void setButtonsEnabledState() {
        if (requestingLocationUpdates) {
            startUpdatesButton.setEnabled(false);
            stopUpdatesButton.setEnabled(true);
        } else {
            stopUpdatesButton.setEnabled(false);
            startUpdatesButton.setEnabled(true);
        }
    }

    /**
     * Check ACCESS_FINE_LOCATION permission, if necessary request it.
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
            listener = (RunningMapFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface RunningMapFragmentInteractionListener {
        void onRunningMapFragmentInteraction(Run run);
    }
}