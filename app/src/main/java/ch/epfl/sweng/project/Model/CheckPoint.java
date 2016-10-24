package ch.epfl.sweng.project.Model;

import android.location.Location;

/**
 * An intermediate location measured during a run.
 */
@SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
public class CheckPoint {

    private final Location location;

    /**
     * Constructor that takes an instance of <code>Location</code>
     *
     * @param location  <code>Location</code> for which to instantiate a <code>CheckPoint</code>
     */
    public CheckPoint(Location location) {
        this.location = new Location(location);
    }

    /**
     * Constructor that takes values for latitude, longitude and time
     *
     * @param latitude
     * @param longitude
     * @param altitude
     */
    public CheckPoint(double latitude, double longitude, double altitude) {
        location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setAltitude(altitude);
    }

    /**
     * Computes the distance in meters to a <code>CheckPoint</code> passed as argument
     *
     * @param destination   <code>CheckPoint</code> to which calculate the distance
     * @return              distance between the points
     */
    public float distanceTo(CheckPoint destination) {
        return location.distanceTo(destination.location);
    }

    /**
     * Getter for latitude
     *
     * @return  the latitude of the point, in degrees
     */
    public double getLatitude() {
        return location.getLatitude();
    }

    /**
     * Getter for longitude
     *
     * @return  the longitude of the point, in degrees
     */
    public double getLongitude() {
        return location.getLongitude();
    }

    public double getAltitude() { return location.getAltitude(); }
}
