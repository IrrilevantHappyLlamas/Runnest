package ch.epfl.sweng.project;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ch.epfl.sweng.project.Model.CheckPoint;
import ch.epfl.sweng.project.Model.Track;

/**
 * This class contains some UI methods used by multiple Fragment/Activity.
 */
public class UtilsUI {

    /**
     * Given the time in seconds convert it into a String with the format hh:mm:ss.
     * If showHours is true always display the hours digit (also if they are 00), otherwise,
     * if it is false, they are shown only if necessary (not if they are 00).
     *
     * @param time time to convert, in seconds
     * @param showHours indicates whether the hours digit must always be shown or not
     * @return time as a String formatted as hh:mm:ss
     */
    public static String timeToString(int time, boolean showHours) {
        if (time < 0) {
            throw new IllegalArgumentException("Run/Challenge could not have a negative duration");
        }

        String toDisplay = "";

        if (showHours || time >= 3600) {
            toDisplay += String.format(Locale.getDefault(), "%02d:", TimeUnit.SECONDS.toHours(time));
        }

        toDisplay += String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.SECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(time)),
                TimeUnit.SECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(time)));

        return toDisplay;
    }

    /**
     * Setup UI elements and gesture of the given GoogleMap. Used for recaps.
     *
     * @param googleMap map to setup
     */
    public static void recapDisplayTrackSetupUI(GoogleMap googleMap) {
        if (googleMap == null) {
            throw new NullPointerException();
        }

        googleMap.setBuildingsEnabled(false);
        googleMap.setIndoorEnabled(false);
        googleMap.setTrafficEnabled(false);

        UiSettings uiSettings = googleMap.getUiSettings();

        uiSettings.setCompassEnabled(false);
        uiSettings.setIndoorLevelPickerEnabled(false);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
    }

    /**
     * Display the given Track on the given GoogleMap. The argument color indicates the desired color for
     * the Track to be shown. Also takes care to center the camera on the displayed Track. Used for recaps
     *
     * @param track  Track to be shown
     * @param googleMap map where the Track must be shown
     * @param color color for the Tarck to be shown
     */
    public static void recapDisplayTrack(Track track, GoogleMap googleMap, int color) {
        if(track == null || googleMap == null) {
            throw new NullPointerException();
        }

        if (track.getTotalCheckPoints() != 0) {
            // Build polyline and latitude, longitude bounds
            PolylineOptions polylineOptions = new PolylineOptions();
            List<CheckPoint> trackPoints = track.getCheckpoints();
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (CheckPoint checkPoint : trackPoints) {
                LatLng latLng = new LatLng(checkPoint.getLatitude(), checkPoint.getLongitude());
                polylineOptions.add(latLng);
                builder.include(latLng);
            }

            googleMap.addPolyline(polylineOptions.color(color));

            // Center camera on past run
            LatLngBounds bounds = builder.build();
            int padding = 40;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            googleMap.animateCamera(cameraUpdate);
        }
    }
}
