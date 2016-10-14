package ch.epfl.sweng.project.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import ch.epfl.sweng.project.Activities.SideBarActivity;
import ch.epfl.sweng.project.Model.CheckPoint;
import ch.epfl.sweng.project.Model.Run;



public class LocationDemo extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult> {

    // Default attibutes
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private OnFragmentInteractionListener mListener;

    // Constants
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    // Layout
    private Button mStartUpdatesButton;
    private Button mStopUpdatesButton;
    private TextView mLatitudeText;
    private TextView mLongitudeText;
    private TextView mNbCheckPointLabel;
    private TextView mNbCheckPointValue;

    // Location update
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    protected LocationSettingsRequest mLocationSettingsRequest;
    private boolean mRequestingLocationUpdates;

    // Data storage
    private  int mCheckPointSaved;
    private CheckPoint mLastCheckPoint;
    private Run mRun;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location_demo, container, false);

        // Check location permission
        checkPermission();

        // Setup Graphics
        textViewSetup(view);
        buttonsSetup(view);

        // Setup location tracking
        mRequestingLocationUpdates = false;
        createGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        checkLocationSettings();

        return view;
    }

    /**
     * Setup all <code>textView</code> of the fragment, linking them to their respective
     * layout component. Also initialize their value and visibility if necessary.
     *
     * @param view <code>View</code> where text views must be added
     */
    private void textViewSetup(View view) {

        mLatitudeText = (TextView) view.findViewById(R.id.latitude_text);
        mLongitudeText = (TextView) view.findViewById(R.id.longitude_text);

        mNbCheckPointLabel = (TextView) view.findViewById(R.id.nb_checkPoint_label);
        mNbCheckPointValue = (TextView) view.findViewById(R.id.nb_checkPoint_value);
        mNbCheckPointLabel.setVisibility(View.INVISIBLE);
        mNbCheckPointValue.setVisibility(View.INVISIBLE);
        mCheckPointSaved = 0;
    }


    /**
     * Setup buttons, linking them to their respective layout components and
     * assigning them an appropriate listener. Also initialize their visibility
     * if necessary.
     *
     * @param view <code>View</code> where buttons must be added
     */
    private void buttonsSetup(View view) {
        mStartUpdatesButton = (Button) view.findViewById(R.id.start_updates_button);
        mStartUpdatesButton.setVisibility(View.VISIBLE);
        mStartUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mNbCheckPointLabel.setVisibility(View.VISIBLE);
                mNbCheckPointValue.setVisibility(View.VISIBLE);

                mStartUpdatesButton.setVisibility(View.INVISIBLE);
                mStopUpdatesButton.setVisibility(View.VISIBLE);
                mRun = new Run();

                if(mLastCheckPoint != null) {
                    mRun.start(mLastCheckPoint);
                    ++mCheckPointSaved;
                    updateGUI();
                }

                if (!mRequestingLocationUpdates) {
                    mRequestingLocationUpdates = true;
                    setButtonsEnabledState();
                    startLocationUpdates();
                }
            }
        });

        mStopUpdatesButton = (Button) view.findViewById(R.id.stop_updates_button);
        mStopUpdatesButton.setVisibility(View.INVISIBLE);
        mStopUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRequestingLocationUpdates) {
                    mRequestingLocationUpdates = false;
                    setButtonsEnabledState();
                    stopLocationUpdates();
                }
            }
        });

        setButtonsEnabledState();
    }

    /**
     * Set enabled state of the buttons to be coherent with the actual state
     * of other variables.
     */
    private void setButtonsEnabledState() {

        if (mRequestingLocationUpdates) {

            mStartUpdatesButton.setEnabled(false);
            mStopUpdatesButton.setEnabled(true);
        } else {

            mStopUpdatesButton.setEnabled(false);

            if (((SideBarActivity) getActivity()).getLocationPermissionGranted()) {
                mStartUpdatesButton.setEnabled(true);
            } else {

                mStartUpdatesButton.setEnabled(false);
            }
        }
    }

    /**
     * Update the GUI in order to be coherent with the current state of the variables.
     */
    private void updateGUI() {

        setButtonsEnabledState();

        if (mLastCheckPoint != null) {
            mLatitudeText.setText(String.valueOf(mLastCheckPoint.getLatitude()));
            mLongitudeText.setText(String.valueOf(mLastCheckPoint.getLongitude()));
            mNbCheckPointValue.setText(String.valueOf(mCheckPointSaved));
        }
    }

    /**
     * Initialize the <code>GoogleApiClient</code> field of the fragment, add to it all
     * necessary parameters and finally build it.
     */
    private synchronized void createGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Initialize the <code>LocationRequest</code> field of the fragment and setup all
     * necessary parameters using the apposite constants.
     */
    private void createLocationRequest() {

        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Build a <code>LocationSettingRequest</code> from <code>mLocationRequest</code> and
     * assign it to the appropriate field of the fragment.
     */
    private void buildLocationSettingsRequest() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Check whether gps is turned on or not.
     */
    protected void checkLocationSettings() {

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    /**
     * Handle the result of <code>LocationSettingRequest</code>
     *
     * @param locationSettingsResult    answer of the user to the request
     */
    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {

        final Status status = locationSettingsResult.getStatus();

        switch (status.getStatusCode()) {

            case LocationSettingsStatusCodes.SUCCESS:
                break;

            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException ignored) {

                }
                break;

            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                break;
        }
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

            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {

                    case Activity.RESULT_OK:

                        startLocationUpdates();
                        break;

                    case Activity.RESULT_CANCELED:

                        break;
                }
                break;
        }
    }

    /**
     * Check <code>ACCESS_FINE_LOCATION</code> permission, if necessary request it.
     * This check is necessary only with Android 6.0+ and/or SDK 22+
     */
    public void checkPermission() {

        int fineLocation = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);

        if (fineLocation != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, SideBarActivity.PERMISSION_REQUEST_CODE_FINE_LOCATION);
            }

        } else {
            ((SideBarActivity)getActivity()).setLocationPermissionGranted(true);
        }
    }

    /**
     * Start location updates if <code>ACCESS_FINE_LOCATION</code> permission is given.
     * Eventually update the buttons state to be coherent by calling
     * <code>setButtonsEnabledState()</code>.
     */
    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            checkPermission();
            setButtonsEnabledState();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                mRequestingLocationUpdates = true;
                setButtonsEnabledState();
            }
        });
    }

    /**
     * Stop location updates, update buttons state and end the current run.
     */
    protected void stopLocationUpdates() {

        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                mRequestingLocationUpdates = false;
                setButtonsEnabledState();
                mRun.stop();
            }
        });
    }

    /**
     * Called when a connect request has been successfully completed.
     *
     * @param connectionHint    not used here
     */
    @Override
    public void onConnected(Bundle connectionHint) {

        // We don't store data yet, we wait that user start the run
        if (mLastCheckPoint == null) {

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                checkPermission();
                setButtonsEnabledState();
            }

            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if(location != null) {
                mLastCheckPoint = new CheckPoint(location);
            }
            updateGUI();
        }

        if (mRequestingLocationUpdates) {

            startLocationUpdates();
        }
    }

    /**
     * Called when the client is temporarily in a disconnected state.
     * Try to reconnect.
     *
     * @param i     not used here
     */
    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    /**
     * Called when client fails. Does nothing for now.
     *
     * @param connectionResult      not used here
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Handle location updates by updating all necessary fields. Then update the GUI
     * by calling <code>updateGUI()</code>
     *
     * @param location      the new <code>Location</code>
     */
    @Override
    public void onLocationChanged(Location location) {

        mLastCheckPoint = new CheckPoint(location);

        if(mRun.isRunning()) {
            mRun.update(mLastCheckPoint);
            ++mCheckPointSaved;
        } else {

            mRun.start(mLastCheckPoint);
            ++mCheckPointSaved;
        }

        updateGUI();
    }


    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    public interface OnFragmentInteractionListener {

    }
}
