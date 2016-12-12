package ch.epfl.sweng.project.Fragments.RunFragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.maps.model.MapStyleOptions;

import java.util.Locale;

import ch.epfl.sweng.project.AppRunnest;
import ch.epfl.sweng.project.Model.CheckPoint;
import ch.epfl.sweng.project.Model.Run;

/**
 * Abstract class that represent the base skeleton for a Fragment handling
 * a Run and showing the path done by the user thanks to gps services and
 * GoogleMap.
 *
 * As said the path done is shown, as well as the distance that the user ran, from
 * when the challenge started until now.
 */
abstract class RunFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{
    // Location update
    protected GoogleApiClient googleApiClient = null;
    protected boolean requestingLocationUpdates = false;
    protected LocationSettingsHandler locationSettingsHandler = null;

    // Live stats
    protected TextView distance = null;

    // Data storage
    private CheckPoint lastCheckPoint = null;
    protected Run run = null;

    // Map
    protected MapView mapView = null;
    private MapHandler mapHandler = null;
    private CountDownTimer locationChangeSimulation = null;

    protected void startRun() {
        run.start();

        distance.setVisibility(View.VISIBLE);
        updateDisplayedDistance();

        requestingLocationUpdates = true;
        startLocationUpdates();

        if(((AppRunnest)getActivity().getApplication()).isTestSession()) {
            setupLocationChangeSimulation();
        }
    }

    protected void stopRun() {
        requestingLocationUpdates = false;
        stopLocationUpdates();
        run.stop();

        if(((AppRunnest)getActivity().getApplication()).isTestSession()) {
            locationChangeSimulation.onFinish();
        }
    }

    protected void updateDisplayedDistance() {
        double distanceToShow = run.getTrack().getDistance()/1000.0;
        String distanceInKm = String.format(Locale.getDefault(), "%.2f", distanceToShow) +
                " " +
                getString(R.string.km);

        distance.setText(distanceInKm);
    }

    private void setupLocationChangeSimulation() {
        locationChangeSimulation = new CountDownTimer(10000, 500) {
            private Location location;
            private double lat = 0.001;
            private double lon = 0.001;

            private boolean isRunning = true;

            public void onTick(long millisUntilFinished) {
                if(isRunning) {
                    lat += 0.001;
                    lon += 0.001;

                    location = new Location("AppRunnest Test");
                    location.setLatitude(lat);
                    location.setLongitude(lon);

                    onLocationChanged(location);
                }
            }

            public void onFinish() {
                isRunning = false;
            }
        }.start();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Start listening for updates
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient,
                    locationSettingsHandler.getLocationRequest(),
                    this
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status r) {
                    requestingLocationUpdates = true;
                }
            });

            mapHandler.setupRunningMapUI();

            mapHandler.startShowingLocation();
        }
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status r) {
                requestingLocationUpdates = false;
                mapHandler.stopShowingLocation();
            }
        });
    }

    /**
     * Handle a location update.
     *
     * @param location      The new Location, must be non null.
     */
    @Override
    public void onLocationChanged(Location location) {

        if (location == null) {
            throw new IllegalArgumentException("New Location must be non null");
        }

        lastCheckPoint = new CheckPoint(location);

        if(run.isRunning()) {
            run.update(lastCheckPoint);
        }

        mapHandler.updateMap(lastCheckPoint);

        updateDisplayedDistance();
    }

    /**
     * Called when the GoogleMap is ready. Initialize a MapHandler.
     *
     * @param googleMap     The GoogleMap must be non null.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (googleMap == null) {
            throw new IllegalArgumentException("The GoogleMap must be non null");
        }

        MapStyleOptions mapStyle = MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style_no_label);
        googleMap.setMapStyle(mapStyle);
        mapHandler = new MapHandler(googleMap, ContextCompat.getColor(getContext(), R.color.colorAccent));
    }

    /**
     * Called when GoogleApiClient is connected. Try to get the last known location and
     * start location updates if necessary.
     *
     * @param bundle    Not used here.
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location location = null;

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            mapHandler.startShowingLocation();
        }
        if(location != null) {
            lastCheckPoint = new CheckPoint(location);
        }
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Retry connecting
        googleApiClient.connect();
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        //Resume location updates
        if (googleApiClient.isConnected() && requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}