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

public class LocationSettingsHandler implements ResultCallback<LocationSettingsResult> {


    // Constants
    public static final int REQUEST_CHECK_SETTINGS = 0x1;

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 4000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 2000;

    // Attributes
    private GoogleApiClient mGoogleApiClient = null;
    private LocationRequest mLocationRequest = null;
    private LocationSettingsRequest mLocationSettingsRequest = null;
    private Activity mActivity = null;
    private boolean mGpsIsTurnedOn = false;

    public LocationSettingsHandler(GoogleApiClient googleApiClient, Activity activity) throws IllegalArgumentException {
        if(googleApiClient == null || activity == null) {
            throw new IllegalArgumentException("LocationSettingsHandler constructor: arguments cannot be null");
        }

        mGoogleApiClient = googleApiClient;
        mActivity = activity;

        mLocationRequest = createLocationRequest();
        mLocationSettingsRequest = buildLocationSettingsRequest();
    }

    /**
     * Initialize the <code>LocationRequest</code> field of the fragment and setup all
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
     * Build a <code>LocationSettingRequest</code> from <code>mLocationRequest</code> and
     * assign it to the appropriate field of the fragment.
     */
    private LocationSettingsRequest buildLocationSettingsRequest() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        return builder.build();
    }

    /**
     * Check whether gps is turned on or not.
     */
    public boolean checkLocationSettings() {

        // In case of a test session we don't want to check settings
        if(((AppRunnest)mActivity.getApplication()).isTestSession()) {
            return true;
        }

        if(!mGpsIsTurnedOn) {

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(
                            mGoogleApiClient,
                            mLocationSettingsRequest
                    );
            result.setResultCallback(this);
        }

        return mGpsIsTurnedOn;
    }

    /**
     * A getter for <code>mLocationRequest</code>.
     *
     * @return      <code>mLocationRequest</code>
     */
    //TODO: evaluate whether do a deep copy or not
    public LocationRequest getLocationRequest() {
        return mLocationRequest;
    }

    /**
     * A setter for <code>mGpsIsTurnedOn</code>.
     *
     * @param gpsIsTurnedOn     desired value for <code>mGpsIsTurnedOn</code>
     */
    public void setGpsIsTurnedOn(boolean gpsIsTurnedOn) {
        mGpsIsTurnedOn = gpsIsTurnedOn;
    }

    /**
     * Handle the result of <code>LocationSettingRequest</code>
     *
     * @param r    answer of the user to the request
     */
    @Override
    public void onResult(@NonNull LocationSettingsResult r) {

        final Status status = r.getStatus();

        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                mGpsIsTurnedOn = true;
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    status.startResolutionForResult(mActivity, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException ignored) {

                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                mGpsIsTurnedOn = false;
                break;
        }
    }
}
