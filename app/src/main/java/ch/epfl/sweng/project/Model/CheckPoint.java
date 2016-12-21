package ch.epfl.sweng.project.Model;

import android.location.Location;

/**
 * An intermediate location measured during a run that contains methods to store and retrieve coordinates and to compute
 * distances between points.
 */
public class CheckPoint {

    private final Location location;

    /**
     * Constructor that takes an instance of Location and stores it through containment.
     *
     * @param location  Location for which to instantiate a CheckPoint.
     */
    public CheckPoint(Location location) {

        if (location == null) {
            throw new IllegalArgumentException("The location used to instantiate a CheckPoint can't be null");
        }

        this.location = new Location(location);
    }

    /**
     * Constructor that takes values for latitude and longitude in degrees and constructs an encapsulated
     * Location object to store them. Latitude and longitude must belong to the intervals [-90°, 90°] and
     * [-180°, 180°] respectively.
     *
     * @param latitude      Latitude in degrees.
     * @param longitude     Longitude in degrees.
     */
    public CheckPoint(double latitude, double longitude) {

        if (latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("Latitude must belong to interval [-90°, 90°]");
        } else if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must belong to interval [-180°, 180°]");
        }

        location = new Location("AppRunnest");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
    }

    /**
     * Computes the distance in meters to a CheckPoint passed as argument, which can't be null.
     *
     * @param destination   CheckPoint to which calculate the distance
     * @return              Distance between the points
     */
    public float distanceTo(CheckPoint destination) {

        if (destination == null) {
            throw new IllegalArgumentException("CheckPoint to which to calculate distance can't be null");
        }

        return location.distanceTo(destination.location);
    }

    /**
     * Getter for latitude.
     *
     * @return  Latitude of the point, in degrees
     */
    public double getLatitude() {
        return location.getLatitude();
    }

    /**
     * Getter for longitude
     *
     * @return  Longitude of the point, in degrees
     */
    public double getLongitude() {
        return location.getLongitude();
    }

}
