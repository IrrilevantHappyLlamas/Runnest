package ch.epfl.sweng.project.Model;

import android.location.Location;

/**
 * An intermediate location mesured during a run.
 *
 * @author Tobia Albergoni
 */
public class CheckPoint {

    private Location location;

    // Check parameters of Location? (time greater than zero, valid coordinates,....)?
    public CheckPoint(Location location) {
        this.location = new Location(location);
    }

    public float distanceTo(CheckPoint destination) {
        return location.distanceTo(destination.location);
    }

    public double getLatitude() {
        return location.getLatitude();
    }

    public double getLongitude() {
        return location.getLongitude();
    }

    public long getTime() {
        return location.getTime();
    }
}
