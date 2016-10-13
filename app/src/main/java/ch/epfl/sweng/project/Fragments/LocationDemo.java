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
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LocationDemo.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LocationDemo} factory method to
 * create an instance of this fragment.
 */
public class LocationDemo extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /** My constants */
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    /** Layout */

    private Button mStartUpdatesButton;
    private Button mStopUpdatesButton;
    private TextView mLatitudeText;
    private TextView mLongitudeText;
    private TextView mNbCheckPointLabel;
    private TextView mNbCheckPointValue;

    /** My attributes */
    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;
    protected LocationSettingsRequest mLocationSettingsRequest;

    private boolean mRequestingLocationUpdates;

    private  int checkPointSaved;
    private CheckPoint mLastCheckPoint;
    private Run mRun;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location_demo, container, false);

        checkPermission();

        mLatitudeText = (TextView) view.findViewById(R.id.latitude_text);
        mLongitudeText = (TextView) view.findViewById(R.id.longitude_text);

        mNbCheckPointLabel = (TextView) view.findViewById(R.id.nb_checkPoint_label);
        mNbCheckPointValue = (TextView) view.findViewById(R.id.nb_checkPoint_value);
        mNbCheckPointLabel.setVisibility(View.INVISIBLE);
        mNbCheckPointValue.setVisibility(View.INVISIBLE);
        checkPointSaved = 0;

        // Buttons setup
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
                    ++checkPointSaved;
                    updateUI();
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


        mRequestingLocationUpdates = false;

        createGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        checkLocationSettings();


        return view;
    }

    private synchronized void createGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                try {
                    status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                break;
        }
    }

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



    public void checkPermission(){
        int fineLocation = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);

        if (fineLocation != PackageManager.PERMISSION_GRANTED){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, SideBarActivity.PERMISSION_REQUEST_CODE_FINE_LOCATION);
            }

        } else {
            ((SideBarActivity)getActivity()).setLocationPermissionGranted(true);
        }
    }

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
            public void onResult(Status status) {
                mRequestingLocationUpdates = true;
                setButtonsEnabledState();
            }
        });
    }


    private void setButtonsEnabledState() {
        if (mRequestingLocationUpdates) {
            mStartUpdatesButton.setEnabled(false);
            mStopUpdatesButton.setEnabled(true);
        } else {
            mStopUpdatesButton.setEnabled(false);

            if ( ((SideBarActivity)getActivity()).getLocationPermissionGranted() ) {
                mStartUpdatesButton.setEnabled(true);
            } else {
                mStartUpdatesButton.setEnabled(false);
            }
        }
    }

    private void updateUI() {
        setButtonsEnabledState();
        if (mLastCheckPoint != null) {
            mLatitudeText.setText(String.valueOf(mLastCheckPoint.getLatitude()));
            mLongitudeText.setText(String.valueOf(mLastCheckPoint.getLongitude()));
            mNbCheckPointValue.setText(String.valueOf(checkPointSaved));
        }
    }

    protected void stopLocationUpdates() {

        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = false;
                setButtonsEnabledState();
                mRun.stop();
            }
        });
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
            updateUI();
        }

        if (mRequestingLocationUpdates) {

            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastCheckPoint = new CheckPoint(location);
        if(mRun.isRunning()) {
            mRun.update(mLastCheckPoint);
            ++checkPointSaved;

        } else {
            mRun.start(mLastCheckPoint);
            ++checkPointSaved;

        }
        updateUI();
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
