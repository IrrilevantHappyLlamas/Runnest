package ch.ihl.runnest.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * An ordinated sequence of checkpoints that models a track. This class provides methods to manipulate the Track by
 * adding CheckPoint data to it, retrieve all checkpoints and compute the total distance.
 */
public class Track {

    private List<CheckPoint> checkpoints = null;
    private int totalCheckPoints;
    private float distance;


    /**
     * Constructor which initialize an empty Track.
     */
    public Track() {
        checkpoints = new ArrayList<>();
        totalCheckPoints = 0;
        distance = 0;
    }

    /**
     * Constructor that takes a starting CheckPoint for the Track.
     *
     * @param startingPoint     Starting point of the Track, must be non null.
     */
    public Track(CheckPoint startingPoint) {

        if (startingPoint == null) {
            throw new IllegalArgumentException("Starting point of a Track can't be null");
        }

        checkpoints = new ArrayList<>();
        checkpoints.add(startingPoint);
        totalCheckPoints = 1;
        distance = 0;
    }

    /**
     * Copy constructor.
     *
     * @param toCopy    Track to copy, must be non null.
     */
    public Track(Track toCopy) {

        if (toCopy == null) {
            throw new IllegalArgumentException("Track to copy can't be null");
        }
        checkpoints = new ArrayList<>(toCopy.checkpoints);
        totalCheckPoints = toCopy.totalCheckPoints;
        distance = toCopy.distance;
    }

    /**
     * Add a new CheckPoint to the Track. The new point must be non null.
     *
     * @param newPoint  CheckPoint to add, must be non null.
     */
    public void add(CheckPoint newPoint) {
        if (newPoint == null) {
            throw new IllegalArgumentException("Cannot add a null CheckPoint to a Track");
        }

        if (totalCheckPoints > 0) {
            checkpoints.add(newPoint);
            distance += checkpoints.get(totalCheckPoints - 1).distanceTo(newPoint);
            totalCheckPoints++;
        } else {
            checkpoints.add(newPoint);
            totalCheckPoints++;
        }
    }

    /**
     * Getter for the total distance in meters.
     *
     * @return  Total distance in meters.
     */
    public float getDistance() {
        return distance;
    }

    /**
     * Getter for the total number of CheckPoints.
     *
     * @return  Total number of points.
     */
    public int getTotalCheckPoints() {
        return totalCheckPoints;
    }

    /**
     * Getter for the CheckPoint list.
     *
     * @return  A copy of the list of CheckPoints.
     */
    public List<CheckPoint> getCheckpoints() { return new ArrayList<>(checkpoints); }

    /**
     * Getter for the last registered CheckPoint.
     *
     * @return  Last CheckPoint added or null if the Track is empty.
     */
    public CheckPoint getLastPoint() {
        if (totalCheckPoints != 0) {
            return checkpoints.get(totalCheckPoints - 1);
        } else {
            return null;
        }
    }
}
