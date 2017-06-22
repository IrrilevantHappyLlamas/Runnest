package ch.ihl.runnest.Fragments.RunFragments;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import ch.ihl.runnest.Model.CheckPoint;

/**
 * This class handles a map, it displays it and updates it
 */
public class MapHandler {

    private static final float CAMERA_ZOOM = 16f;

    private GoogleMap googleMap = null;
    private final UiSettings uiSettings;

    private PolylineOptions polylineOptions = null;
    private int trackColor;

    /**
     * Constructor of the class
     *
     * @param googleMap a google map
     * @param trackColor the color of the track
     */
    public MapHandler(GoogleMap googleMap, int trackColor) {
        if (googleMap == null) {
            throw new IllegalArgumentException();
        }

        this.trackColor = trackColor;

        this.googleMap = googleMap;
        this.googleMap.setLocationSource(null);

        uiSettings = this.googleMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(false);
        uiSettings.setCompassEnabled(false);

        polylineOptions = new PolylineOptions();
    }

    /**
     * Given a new CheckPoint, if non null, update the map and the polyline showed on it.
     *
     * @param checkPoint a new CheckPoint
     */
    public void updateMap(CheckPoint checkPoint) {
        if (checkPoint != null) {
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
     * @throws SecurityException if app does not have the necessary permissions
     */
    public void startShowingLocation() throws SecurityException {
        googleMap.setMyLocationEnabled(true);
    }

    /**
     * Set "My Location Enabled" to false.
     *
     * @throws SecurityException if app does not have the necessary permissions
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

        uiSettings.setIndoorLevelPickerEnabled(false);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setScrollGesturesEnabled(false);
        uiSettings.setZoomGesturesEnabled(false);
        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setZoomControlsEnabled(true);
    }
}