package ch.epfl.sweng.project.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * An ordinated sequence of checkpoints that models a track
 */
@SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
public class Track {

    private List<CheckPoint> checkpoints = null;
    private int totalCheckPoints;
    private float distance;

    // Altitude
    private double uphill;
    private double downhill;

    /**
     * Constructor that takes a starting <code>CheckPoint</code> for the <code>Track</code>
     *
     * @param startingPoint     starting point of the <code>Track</code>
     */
    public Track(CheckPoint startingPoint) {
        checkpoints = new ArrayList<>();
        checkpoints.add(startingPoint);
        totalCheckPoints = 1;
        distance = 0;

        uphill = 0;
        downhill = 0;
    }

    public Track() {
        checkpoints = new ArrayList<>();
        totalCheckPoints = 0;
        distance = 0;

        uphill = 0;
        downhill = 0;
    }

    /**
     * Copy constructor
     *
     * @param toCopy    <code>Track</code> to copy
     */
    public Track(Track toCopy) {
        checkpoints = new ArrayList<>(toCopy.checkpoints);
        totalCheckPoints = toCopy.totalCheckPoints;
        distance = toCopy.distance;

        uphill = toCopy.uphill;
        downhill = toCopy.downhill;
    }


    /**
     * Add a new <code>CheckPoint</code> to the <code>Track</code>. The new point must be non null
     *
     * @param newPoint  <code>CheckPoint</code> to add
     * @return          <code>true</code> if the operation succeeds, <code>false</code> otherwise
     */
    public boolean add(CheckPoint newPoint) {
        if (newPoint == null) {
            return false;
        }

        if (totalCheckPoints > 0) {

            updateAltitudeDiff(newPoint);

            checkpoints.add(newPoint);
            distance += checkpoints.get(totalCheckPoints - 1).distanceTo(newPoint);
            totalCheckPoints++;
        } else {
            checkpoints.add(newPoint);
            totalCheckPoints++;
        }

    return true;
    }

    private void updateAltitudeDiff(CheckPoint newPoint) {

        double altitudeDiff = newPoint.getAltitude() - checkpoints.get(totalCheckPoints - 1).getAltitude();
        if(altitudeDiff >= 0) {
            uphill += altitudeDiff;
        } else {
            downhill += altitudeDiff;
        }
    }

    /**
     * Getter for the total distance in meters
     *
     * @return  total distance
     */
    public float getDistance() {
        return distance;
    }

    public double getUphill() {
        return uphill;
    }

    public double getDownhill() {
        return downhill;
    }

    /**
     * Getter for the total number of <code>CheckPoint</code>
     *
     * @return  total number of points
     */
    public int getTotalCheckPoints() {
        return totalCheckPoints;
    }

    /**
     * Getter for the last registered <code>CheckPoint</code>
     *
     * @return
     */
    // TODO: handle empty track (else branch)
    public CheckPoint getLastPoint() {
        if (totalCheckPoints != 0) {
            return checkpoints.get(totalCheckPoints - 1);
        } else {
            return null;
        }
    }

    /**
     * Getter for the <code>CheckPoint</code> list
     *
     * @return  a list of <code>CheckPoint</code>
     */
    public List<CheckPoint> getCheckpoints() { return new ArrayList<>(checkpoints); }
}
