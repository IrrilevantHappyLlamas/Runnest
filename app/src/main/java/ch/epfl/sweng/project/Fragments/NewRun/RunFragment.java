package ch.epfl.sweng.project.Fragments.NewRun;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
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

import java.text.SimpleDateFormat;
import java.util.Date;

import ch.epfl.sweng.project.Activities.SideBarActivity;
import ch.epfl.sweng.project.Model.CheckPoint;
import ch.epfl.sweng.project.Model.Run;

abstract class RunFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    // Location update
    protected GoogleApiClient mGoogleApiClient = null;
    protected LocationSettingsHandler mLocationSettingsHandler = null;
    protected boolean mRequestingLocationUpdates = false;

    // Live stats
    protected TextView mDistance = null;

    // Buttons
    protected Button mStartUpdatesButton = null;          // Diventa Ready

    // Data storage
    protected CheckPoint mLastCheckPoint = null;
    protected Run mRun = null;

    // Map
    protected MapView mMapView = null;
    protected MapHandler mMapHandler = null;

    protected void setupLocation() {

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
     * Include all actions to perform when Start button is pressed
     */
    protected void startUpdatesButtonPressed() {

        if(checkPermission() && mLocationSettingsHandler.checkLocationSettings()) {
            startRun();
        }
    }

    protected void startRun() {

        mStartUpdatesButton.setVisibility(View.INVISIBLE);

        // initialize new Run
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy HH:mm:ss");
        String runName = dateFormat.format(new Date());
        mRun = new Run(runName);
        mRun.start();

        mDistance.setVisibility(View.VISIBLE);
        updateDisplayedDistance();

        mRequestingLocationUpdates = true;
        startLocationUpdates();
    }

    private void updateDisplayedDistance() {
        String distanceInKm = (int)(mRun.getTrack().getDistance()/100.0)/10.0
                + " "
                + getString(R.string.km);

        mDistance.setText(distanceInKm);
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

            mMapHandler.setupRunningMapUI();

            mMapHandler.startShowingLocation();
        }
    }

    /**
     * Stop location updates, update buttons state and end current run.
     */
    protected void stopLocationUpdates() {
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

        updateDisplayedDistance();
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}