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
    private long duration;

    // Altitude
    private double positiveDifference;
    private double negativeDifference;

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
        duration = 0;

        positiveDifference = 0;
        negativeDifference = 0;
    }

    public Track() {
        checkpoints = new ArrayList<>();
        totalCheckPoints = 0;
        distance = 0;
        duration = 0;

        positiveDifference = 0;
        negativeDifference = 0;
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
        duration = toCopy.duration;

        positiveDifference = toCopy.positiveDifference;
        negativeDifference = toCopy.negativeDifference;
    }


    /**
     * Add a new <code>CheckPoint</code> to the <code>Track</code>. The new point must be chronologically after current
     * last point
     *
     * @param newPoint  <code>CheckPoint</code> to add
     * @return          <code>true</code> if the operation succeeds, <code>false</code> otherwise
     */
    public boolean add(CheckPoint newPoint) {
        // Warning if we try to add a non coherent CheckPoint to the Track?
        if (totalCheckPoints > 0) {
            if (newPoint.getTime() < checkpoints.get(totalCheckPoints - 1).getTime()) {
                return false;
            } else {
                checkpoints.add(newPoint);
                distance += checkpoints.get(totalCheckPoints - 1).distanceTo(newPoint);
                duration = newPoint.getTime() - checkpoints.get(0).getTime();
                totalCheckPoints++;

                updateAltitudeDiff(newPoint);

                return true;
            }
        } else {
            checkpoints.add(newPoint);
            totalCheckPoints++;
            return true;
        }
    }

    private void updateAltitudeDiff(CheckPoint newPoint) {

        double altitudeDiff = newPoint.getAltitude() - checkpoints.get(totalCheckPoints - 1).getAltitude();
        if(altitudeDiff >= 0) {
            positiveDifference += altitudeDiff;
        } else {
            negativeDifference += altitudeDiff;
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

    /**
     * Getter for the current duration of the <code>Track</code>, in seconds
     *
     * @return  total time between timestamps of first and last <code>CheckPoint</code>
     */
    public long getDuration() {
        return duration;
    }

    public double getPositiveDifference() {
        return positiveDifference;
    }

    public double getNegativeDifference() {
        return negativeDifference;
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
