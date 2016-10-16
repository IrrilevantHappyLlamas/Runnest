package ch.epfl.sweng.project.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import ch.epfl.sweng.project.Activities.SideBarActivity;
import ch.epfl.sweng.project.Model.CheckPoint;
import ch.epfl.sweng.project.Model.Run;



@SuppressWarnings({"CastToConcreteClass", "MethodParameterNamingConvention"})
public class MapFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback,
        LocationListener,
        ResultCallback<LocationSettingsResult> {

    // Default attributes
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private OnFragmentInteractionListener mListener = null;

    // Constants
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    // Layout
    private Button mStartUpdatesButton = null;
    private Button mStopUpdatesButton = null;

    // Location update
    private GoogleApiClient mGoogleApiClient = null;
    private LocationRequest mLocationRequest = null;
    private LocationSettingsRequest mLocationSettingsRequest = null;
    private boolean mRequestingLocationUpdates = false;

    // Map
    private MapView mMapView = null;
    private GoogleMap mMap = null;
    private PolylineOptions mPolylineOptions = null;

    // Data storage
    private CheckPoint mLastCheckPoint = null;
    private Run mRun = null;

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
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Setup Graphics
        buttonsSetup(view);

        // Setup location tracking
        mRequestingLocationUpdates = false;
        createGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        checkLocationSettings();

        // Map
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        return view;
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

                if(checkPermission()) {

                    mStartUpdatesButton.setVisibility(View.INVISIBLE);
                    mStopUpdatesButton.setVisibility(View.VISIBLE);
                    mRun = new Run();

                    if (mLastCheckPoint != null) {
                        mRun.start(mLastCheckPoint);
                    }

                    if (!mRequestingLocationUpdates) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();
                    }

                    setButtonsEnabledState();
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
            mStartUpdatesButton.setEnabled(true);
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
     * this method draws an hardcoded path on the given map that connects EPFL to
     * Lugano passing by several swiss cities.
     *
     * @param googleMap the map where the path will be represented.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Add a marker at the EPFL and move the camera.

        showCurrentPosition();

        //this line disables all user interaction with the map.
        //mMap.getUiSettings().setAllGesturesEnabled(false);


        //this line represents the path on the map.
        //Polyline mMutablePolyline = mMap.addPolyline(mPolylineOptions.color(Color.BLUE));

        //this line moves the camera so that the path can be displayed nicely.
        //mMap.moveCamera(CameraUpdateFactory.newCameraPosition(SWITZERLAND));
    }

    private void showCurrentPosition() {
        if(mLastCheckPoint != null) {

            LatLng currentPosition = new LatLng(mLastCheckPoint.getLatitude(), mLastCheckPoint.getLongitude());
            mMap.addMarker(new MarkerOptions().position(currentPosition).title("You are here!"));
        }
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
    private void checkLocationSettings() {

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
     * Start location updates if <code>ACCESS_FINE_LOCATION</code> permission is given.
     * Eventually update the buttons state to be coherent by calling
     * <code>setButtonsEnabledState()</code>.
     */
    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (mLastCheckPoint == null) {
                Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (location != null) {
                    mLastCheckPoint = new CheckPoint(location);
                }
            }


            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,
                    mLocationRequest,
                    this
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    mRequestingLocationUpdates = true;
                }
            });
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

            Location location = null;

            if (ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            }

            if(location != null) {
                mLastCheckPoint = new CheckPoint(location);
            }
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
        } else {

            mRun.start(mLastCheckPoint);
        }
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
        void onProfileFragmentInteraction(Uri uri);
    }
}
