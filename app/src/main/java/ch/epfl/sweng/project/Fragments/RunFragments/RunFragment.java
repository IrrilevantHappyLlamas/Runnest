package ch.epfl.sweng.project.Fragments.RunFragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.View;
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

import ch.epfl.sweng.project.Model.CheckPoint;
import ch.epfl.sweng.project.Model.Run;

/**
 * Abstract class that represent the base skeleton for a <code>Fragment</code> handling
 * a <code>Run</code> and showing the path done by the user thanks to gps services and
 * <code>GoogleMap</code>.
 *
 * As said the path done is shown, as well as the distance that the user ran, from
 * when challenge started until now.
 */
abstract class RunFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    // Location update
    protected GoogleApiClient mGoogleApiClient = null;
    protected boolean mRequestingLocationUpdates = false;
    protected LocationSettingsHandler mLocationSettingsHandler = null;

    // Live stats
    protected TextView mDistance = null;

    // Data storage
    private CheckPoint mLastCheckPoint = null;
    protected Run mRun = null;

    // Map
    protected MapView mMapView = null;
    private MapHandler mMapHandler = null;

    protected void startRun() {

        // initialize new Run
        @SuppressLint("SimpleDateFormat")
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