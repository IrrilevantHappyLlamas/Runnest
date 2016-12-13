package ch.epfl.sweng.project.Fragments.RunFragments;

import android.app.Activity;
import android.content.IntentSender;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import ch.epfl.sweng.project.AppRunnest;

/**
 * This class handles everything that has to do with location settings
 */
public class LocationSettingsHandler implements ResultCallback<LocationSettingsResult> {

    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 4000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 2000;

    private GoogleApiClient googleApiClient = null;
    private LocationRequest locationRequest = null;
    private LocationSettingsRequest locationSettingsRequest = null;
    private Activity activity = null;
    private boolean gpsIsTurnedOn = false;

    /**
     * Constructor of the class.
     *
     * @param googleApiClient the google api client
     * @param activity the current activity
     */
    public LocationSettingsHandler(GoogleApiClient googleApiClient, Activity activity) {
        if (googleApiClient == null || activity == null) {
            throw new NullPointerException();
        }

        this.googleApiClient = googleApiClient;
        this.activity = activity;

        locationRequest = createLocationRequest();
        locationSettingsRequest = buildLocationSettingsRequest();
    }

    /**
     * Initialize the LocationRequest field of the fragment and setup all
     * necessary parameters using the apposite constants.
     */
    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();

        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return locationRequest;
    }

    /**
     * Build a LocationSettingRequest from mLocationRequest and
     * assign it to the appropriate field of the fragment.
     */
    private LocationSettingsRequest buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        return builder.build();
    }

    /**
     * Check whether gps is turned on or not.
     */
    public boolean checkLocationSettings() {
        // In case of a test session don't check settings
        if (((AppRunnest) activity.getApplication()).isTestSession()) {
            return true;
        }

        if (!gpsIsTurnedOn) {
            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(
                    googleApiClient,
                    locationSettingsRequest);
            result.setResultCallback(this);
        }

        return gpsIsTurnedOn;
    }

    /**
     * A getter for mLocationRequest.
     *
     * @return mLocationRequest
     */
    protected LocationRequest getLocationRequest() {
        return locationRequest;
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult r) {

        final Status status = r.getStatus();

        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                gpsIsTurnedOn = true;
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    status.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException ignored) {
                    ignored.printStackTrace();
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                gpsIsTurnedOn = false;
                break;
        }
    }
}