package ch.epfl.sweng.project.Fragments.NewRun;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.text.DateFormat;
import java.util.Date;

import ch.epfl.sweng.project.Activities.SideBarActivity;
import ch.epfl.sweng.project.Database.DBHelper;
import ch.epfl.sweng.project.Model.CheckPoint;
import ch.epfl.sweng.project.Model.Run;

public class RunningMapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    // Location update
    private GoogleApiClient mGoogleApiClient = null;
    private LocationSettingsHandler mLocationSettingsHandler = null;
    private boolean mRequestingLocationUpdates = false;

    // Live stats
    private Chronometer mChronometer = null;
    private TextView mDistance = null;

    // Buttons
    private Button mStartUpdatesButton = null;
    private Button mStopUpdatesButton = null;

    // Data storage
    private CheckPoint mLastCheckPoint = null;
    private Run mRun = null;

    // Map
    private MapView mMapView = null;
    private MapHandler mMapHandler = null;

    private RunningMapFragmentInteractionListener mListener = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_running_map, container, false);

        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this); //this is important

        // Location
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mRequestingLocationUpdates = false;
        mLocationSettingsHandler = new LocationSettingsHandler(mGoogleApiClient, getActivity());
        mLocationSettingsHandler.checkLocationSettings();

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
        mRun = new Run();
        mChronometer = (Chronometer) view.findViewById(R.id.chronometer);
        mChronometer.setVisibility(View.INVISIBLE);

        mDistance = (TextView) view.findViewById(R.id.distance);
        mDistance.setVisibility(View.INVISIBLE);

    }

    /**
     * Include all actions to perfermor when the Start button is pressed
     */
    private void startButtonPressed() {

        if(checkPermission() && mLocationSettingsHandler.checkLocationSettings()) {
            mStartUpdatesButton.setVisibility(View.INVISIBLE);
            mStopUpdatesButton.setVisibility(View.VISIBLE);


            // initialize new Run
            String runName = DateFormat.getDateTimeInstance().format(new Date());
            mRun = new Run(runName);

            mChronometer.setVisibility(View.VISIBLE);
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();

            mRun.start();

            ((SideBarActivity)getActivity()).setRunning(true);

            mRequestingLocationUpdates = true;
            setButtonsEnabledState();

            mDistance.setVisibility(View.VISIBLE);
            double distanceInKm = (int)(mRun.getTrack().getDistance()/100.0)/10.0;
            mDistance.setText(distanceInKm + " Km");

            startLocationUpdates();
        }
    }

    /**
     * Include all actions to perfermor when the Stop button is pressed
     */
    private void stopButtonPressed() {
        if (mRequestingLocationUpdates) {
            mRequestingLocationUpdates = false;
            setButtonsEnabledState();
            stopLocationUpdates();


            mChronometer.stop();
            mRun.stop();
            ((SideBarActivity)getActivity()).setRunning(false);


            DBHelper dbHelper = new DBHelper(getContext());
            //TODO: verify that insertion has been performed correctly
            dbHelper.insert(new Run(mRun));

            mListener.onRunningMapFragmentInteraction(new Run(mRun));
        }
    }

    /**
     * Set enabled state of the buttons to be coherent with other variables values.
     */
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

    /**
     * Perform all necessary action in order to start getting location updates.
     */
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Start listening for updates
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,
                    mLocationSettingsHandler.getLocationRequest(),
                    this
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status r) {
                    mRequestingLocationUpdates = true;
                }
            });

            mMapHandler.setRunningGesture();

            if(mRun.isRunning()) {
                mMapHandler.stopShowingLocation();
            } else {
                mMapHandler.startShowingLocation();
            }
        }
    }

    /**
     * Stop location updates, update buttons state and end the current run.
     */
    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status r) {
                mRequestingLocationUpdates = false;
                mMapHandler.stopShowingLocation();
            }
        });
    }

    /**
     * Handle results sent by the running activity.
     *
     * @param requestCode   code of the request, an <code>int</code>
     * @param resultCode    code of the result, an <code>int</code>
     * @param data          not used here
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LocationSettingsHandler.REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        mLocationSettingsHandler.setGpsIsTurnedOn(true);
                        break;
                    case Activity.RESULT_CANCELED:
                        mLocationSettingsHandler.setGpsIsTurnedOn(false);
                        break;
                }
                break;
        }
    }

    /**
     * Handle a location update.
     *
     * @param location      the new <code>Location</code>
     */
    @Override
    public void onLocationChanged(Location location) {
        mLastCheckPoint = new CheckPoint(location);

        if(mRun.isRunning()) {
            mRun.update(mLastCheckPoint);
        }

        mMapHandler.updateMap(mLastCheckPoint);

        double distanceInKm = (int)(mRun.getTrack().getDistance()/100.0)/10.0;
        mDistance.setText(distanceInKm + " Km");
    }

    /**
     * Called when the <code>GoogleMap</code> is ready. Initialize a MapHandler.
     *
     * @param googleMap     the <code>GoogleMap</code>
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMapHandler = new MapHandler(googleMap);
    }

    /**
     * Called when <code>GoogleApiClient</code> is connected. Try to get the last known location and
     * start location updates if necessary.
     *
     * @param bundle    not used here
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location location = null;

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            mMapHandler.startShowingLocation();
        }
        if(location != null) {
            mLastCheckPoint = new CheckPoint(location);
        }
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    //TODO: Handle connection failure
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
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