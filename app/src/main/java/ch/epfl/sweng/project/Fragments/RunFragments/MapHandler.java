package ch.epfl.sweng.project.Fragments.RunFragments;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import ch.epfl.sweng.project.Model.CheckPoint;


public class MapHandler {

    // Constants
    private static final float CAMERA_ZOOM = 16f;

    private GoogleMap googleMap = null;
    private PolylineOptions polylineOptions = null;
    private int trackColor;


    //TODO: check sul color
    public MapHandler(GoogleMap googleMap, int color)  throws IllegalArgumentException {
        if(googleMap == null) {
            throw new IllegalArgumentException("MapHandler constructor: argument cannot be null");
        }

        trackColor = color;

        this.googleMap = googleMap;
        this.googleMap.setLocationSource(null);

        polylineOptions = new PolylineOptions();
    }

    /**
     * Given a new <code>CheckPoint</code>, if non null, update the map and the polyline showed on it.
     *
     * @param checkPoint    a new <code>CheckPoint</code>
     */
    public void updateMap(CheckPoint checkPoint) {
        if(checkPoint != null) {

            LatLng currentLatLng = new LatLng(checkPoint.getLatitude(), checkPoint.getLongitude());
            polylineOptions.add(currentLatLng);
            googleMap.clear();
            googleMap.addPolyline(polylineOptions.color(trackColor));

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
            googleMap.animateCamera(cameraUpdate);
        }
    }

    /**
     * Set "My Location Enabled" to true.
     *
     * @throws SecurityException    if app does not have the necessary permissions
     */
    public void startShowingLocation() throws SecurityException {
            googleMap.setMyLocationEnabled(true);
    }

    /**
     * Set "My Location Enabled" to false.
     *
     * @throws SecurityException    if app does not have the necessary permissions
     */
    public void stopShowingLocation() throws SecurityException {
        googleMap.setMyLocationEnabled(false);
    }

    /**
     * Define what the user can do during a run and what he can't do.
     */
    public void setupRunningMapUI() {
        googleMap.setBuildingsEnabled(false);
        googleMap.setIndoorEnabled(false);
        googleMap.setTrafficEnabled(false);
        googleMap.setMinZoomPreference(CAMERA_ZOOM);

        UiSettings uiSettings = googleMap.getUiSettings();

        uiSettings.setCompassEnabled(false);
        uiSettings.setIndoorLevelPickerEnabled(false);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setScrollGesturesEnabled(false);
        uiSettings.setZoomGesturesEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
        uiSettings.setRotateGesturesEnabled(false);

        uiSettings.setZoomControlsEnabled(true);
    }
}