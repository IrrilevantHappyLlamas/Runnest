package ch.epfl.sweng.project.Fragments.RunningMap;

import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import ch.epfl.sweng.project.Model.CheckPoint;


public class MapHandler {

    // Constants
    private static final float CAMERA_ZOOM = 16f;

    private GoogleMap mGoogleMap = null;
    private PolylineOptions mPolylineOptions = null;
    private CameraPosition mCameraPosition;

    public MapHandler(GoogleMap mGoogleMap) {

        this.mGoogleMap = mGoogleMap;
        this.mGoogleMap.setLocationSource(null);

        mPolylineOptions = new PolylineOptions();
    }

    public void updateMap(CheckPoint checkPoint) {

        LatLng currentLatLng = new LatLng(checkPoint.getLatitude(), checkPoint.getLongitude());
        mPolylineOptions.add(currentLatLng);
        mGoogleMap.clear();
        mGoogleMap.addPolyline(mPolylineOptions.color(Color.BLUE));

        mCameraPosition = new CameraPosition.Builder().target(currentLatLng).zoom(CAMERA_ZOOM).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(mCameraPosition);
        mGoogleMap.animateCamera(cameraUpdate);
    }

    public void startShowingLocation() throws SecurityException {
            mGoogleMap.setMyLocationEnabled(true);
    }

    public void stopShowingLocation() throws SecurityException {
        mGoogleMap.setMyLocationEnabled(false);
    }
}
