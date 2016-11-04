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

    public void showTrack(Track track) {
        PolylineOptions polylineOptions = new PolylineOptions();

        List<CheckPoint> trackPoints = track.getCheckpoints();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (CheckPoint checkPoint: trackPoints) {
            LatLng latLng = new LatLng(checkPoint.getLatitude(), checkPoint.getLongitude());
            polylineOptions.add(latLng);
            builder.include(latLng);
        }

        mGoogleMap.addPolyline(mPolylineOptions.color(Color.BLUE));

        LatLngBounds bounds = builder.build();
        int padding = 40; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mGoogleMap.animateCamera(cu);
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

            CameraPosition mCameraPosition = new CameraPosition.Builder().target(currentLatLng).build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(mCameraPosition);
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

        mGoogleMap.getUiSettings().setCompassEnabled(false);
        mGoogleMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        mGoogleMap.getUiSettings().setScrollGesturesEnabled(false);
    }
}
