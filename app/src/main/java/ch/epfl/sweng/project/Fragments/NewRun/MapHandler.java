package ch.epfl.sweng.project.Fragments.NewRun;

import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import ch.epfl.sweng.project.Model.CheckPoint;
import ch.epfl.sweng.project.Model.Track;


public class MapHandler {

    // Constants
    private static final float CAMERA_ZOOM = 16f;

    private GoogleMap mGoogleMap = null;
    private PolylineOptions mPolylineOptions = null;

    public MapHandler(GoogleMap googleMap)  throws IllegalArgumentException {
        if(googleMap == null) {
            throw new IllegalArgumentException("MapHandler constructor: argument cannot be null");
        }

        mGoogleMap = googleMap;
        mGoogleMap.setLocationSource(null);

        mPolylineOptions = new PolylineOptions();
    }

    //TODO: Comment this method
    public void showTrack(Track track) {
        if(track.getTotalCheckPoints() != 0) {

            // Build polyline and LatLngBounds
            PolylineOptions polylineOptions = new PolylineOptions();
            List<CheckPoint> trackPoints = track.getCheckpoints();
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (CheckPoint checkPoint : trackPoints) {
                LatLng latLng = new LatLng(checkPoint.getLatitude(), checkPoint.getLongitude());
                polylineOptions.add(latLng);
                builder.include(latLng);
            }

            mGoogleMap.addPolyline(polylineOptions.color(Color.BLUE));

            // Setup UI
            mGoogleMap.setBuildingsEnabled(false);
            mGoogleMap.setIndoorEnabled(false);
            mGoogleMap.setTrafficEnabled(false);
            UiSettings uiSettings = mGoogleMap.getUiSettings();
            uiSettings.setCompassEnabled(false);
            uiSettings.setIndoorLevelPickerEnabled(false);
            uiSettings.setMapToolbarEnabled(false);
            //uiSettings.setScrollGesturesEnabled(false);
            //uiSettings.setZoomGesturesEnabled(false);
            uiSettings.setZoomControlsEnabled(true);

            // Center camera on past run
            LatLngBounds bounds = builder.build();
            int padding = 40;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mGoogleMap.animateCamera(cameraUpdate);
        }
    }

    /**
     * Given a new <code>CheckPoint</code>, if non null, update the map and the polyline showed on it.
     *
     * @param checkPoint    a new <code>CheckPoint</code>
     */
    public void updateMap(CheckPoint checkPoint) {
        if(checkPoint != null) {

            LatLng currentLatLng = new LatLng(checkPoint.getLatitude(), checkPoint.getLongitude());
            mPolylineOptions.add(currentLatLng);
            mGoogleMap.clear();
            mGoogleMap.addPolyline(mPolylineOptions.color(Color.BLUE));

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
            mGoogleMap.animateCamera(cameraUpdate);
        }
    }

    /**
     * Set "My Location Enabled" to true.
     *
     * @throws SecurityException    if app does not have the necessary permissions
     */
    public void startShowingLocation() throws SecurityException {
            mGoogleMap.setMyLocationEnabled(true);
    }

    /**
     * Set "My Location Enabled" to false.
     *
     * @throws SecurityException    if app does not have the necessary permissions
     */
    public void stopShowingLocation() throws SecurityException {
        mGoogleMap.setMyLocationEnabled(false);
    }

    /**
     * Define what the user can do during a run and what he can't to.
     */
    public void setRunningGesture() {
        mGoogleMap.setBuildingsEnabled(false);
        mGoogleMap.setIndoorEnabled(false);
        mGoogleMap.setTrafficEnabled(false);
        mGoogleMap.setMinZoomPreference(CAMERA_ZOOM);

        UiSettings uiSettings = mGoogleMap.getUiSettings();

        uiSettings.setCompassEnabled(false);
        uiSettings.setIndoorLevelPickerEnabled(false);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setScrollGesturesEnabled(false);
        uiSettings.setZoomGesturesEnabled(false);

        uiSettings.setZoomControlsEnabled(true);
    }
}