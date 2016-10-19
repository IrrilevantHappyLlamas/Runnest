package ch.epfl.sweng.project.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * An ordinated sequence of checkpoints that models a track
 */
@SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
public class Track {

    private List<CheckPoint> checkpoints;
    private int totalCheckPoints;
    private float distance;
    private long duration;

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
    }

    public Track() {
        checkpoints = new ArrayList<>();
        totalCheckPoints = 0;
        distance = 0;
        duration = 0;
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
                return true;
            }
        } else {
            checkpoints.add(newPoint);
            totalCheckPoints++;
            return true;
        }
    }

    /**
     * Getter for the total distance
     *
     * @return  total distance
     */
    public float getDistance() {
        return distance;
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
     * Getter for the current duration of the <code>Track</code>
     *
     * @return  total time between timestamps of first and last <code>CheckPoint</code>
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Getter for the last registered <code>CheckPoint</code>
     *
     * @return
     */
    public CheckPoint getLastPoint() {
        return checkpoints.get(totalCheckPoints -1);
    }

    public List<CheckPoint> getCheckpoints() { return new ArrayList<>(checkpoints); }
}
