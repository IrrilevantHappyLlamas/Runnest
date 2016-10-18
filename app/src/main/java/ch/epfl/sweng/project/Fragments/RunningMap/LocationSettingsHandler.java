package ch.epfl.sweng.project.Fragments.RunningMap;

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

/**
 * Created by Lucio on 18.10.2016.
 */

public class LocationSettingsHandler implements
        ResultCallback<LocationSettingsResult>{


    // Constants
    public static final int REQUEST_CHECK_SETTINGS = 0x1;

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    // Attributes
    private GoogleApiClient mGoogleApiClient = null;
    private LocationRequest mLocationRequest = null;
    private LocationSettingsRequest mLocationSettingsRequest = null;
    private Activity mActvity = null;

    public LocationSettingsHandler(GoogleApiClient mGoogleApiClient, Activity mActvity){

        this.mGoogleApiClient = mGoogleApiClient;
        this.mActvity = mActvity;

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
    public void checkLocationSettings() {

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    // SAFE OR WE NEED "DEEP COPY" ?!?
    public LocationRequest getmLocationRequest() {
        return mLocationRequest;
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
                    status.startResolutionForResult(mActvity, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException ignored) {

                }
                break;

            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                break;
        }
    }
}
