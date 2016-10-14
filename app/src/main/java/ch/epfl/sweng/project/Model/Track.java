package ch.epfl.sweng.project.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * An ordinated sequence of checkpoints that modelizes a track
 *
 * @author Tobia Albergoni
 */
public class Track {

    private List<CheckPoint> checkpoints;
    private int totalCheckPoints;
    private float distance;
    private long duration;

    public Track(CheckPoint startingPoint) {
        checkpoints = new ArrayList<>();
        checkpoints.add(startingPoint);
        totalCheckPoints = 1;
        distance = 0;
        duration = 0;
    }

    // Copy constructor
    public Track(Track toCopy) {
        this.checkpoints = new ArrayList<>(toCopy.checkpoints);
        this.totalCheckPoints = toCopy.totalCheckPoints;
        this.distance = toCopy.distance;
        this.duration = toCopy.duration;
    }

    public boolean add(CheckPoint newPoint) {
        // Warning if we try to add a non coherent CheckPoint to the Track?
        if(newPoint.getTime() < checkpoints.get(totalCheckPoints-1).getTime()) {
            return false;
        }
        else {
            checkpoints.add(newPoint);
            distance += checkpoints.get(totalCheckPoints -1).distanceTo(newPoint);
            duration = newPoint.getTime() - checkpoints.get(0).getTime();
            totalCheckPoints++;
            return true;
        }
    }

    public float getDistance() {
        return distance;
    }

    public int getTotalCheckPoints() {
        return totalCheckPoints;
    }

    public long getDuration() {
        return duration;
    }

    public CheckPoint getLastPoint() {
        return checkpoints.get(totalCheckPoints -1);
    }
}
