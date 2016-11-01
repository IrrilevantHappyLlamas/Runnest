package ch.epfl.sweng.project.Fragments.NewRun;

import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
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

        for (CheckPoint checkPoint: trackPoints) {
            LatLng latLng = new LatLng(checkPoint.getLatitude(), checkPoint.getLongitude());
            polylineOptions.add(latLng);
        }

        mGoogleMap.addPolyline(mPolylineOptions.color(Color.BLUE));
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

            CameraPosition mCameraPosition = new CameraPosition.Builder().target(currentLatLng).zoom(CAMERA_ZOOM).build();
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
}
