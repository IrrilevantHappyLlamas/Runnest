package ch.ihl.runnest.Model;

import android.os.SystemClock;

import java.io.Serializable;

/**
 * This class models a Run, which is a named and uniquely identified entity that contains a Track
 * and methods to get and set its state and values, such as its duration, whether the run is active or stopped. It also
 * provides methods to update the run with new CheckPoint data.
 */
public class Run implements Serializable {

    private String name = null;
    private Track track = null;
    private long duration;
    private long startTime;
    private long id;
    private boolean isRunning = false;

    /**
     * Constructor that instantiates a Run with a non-empty name passed as argument. It sets the id to -1
     * as a default.
     *
     * @param name  Desired name of the Run.
     */
    public Run(String name) {

        if (name == null) {
            throw new IllegalArgumentException("Run name can't be null");
        } else if (name.isEmpty()) {
            throw new IllegalArgumentException("Run name can't be empty");
        }

        this.name = name;
        isRunning = false;
        track = new Track();
        startTime = -1;
        id = -1;
    }

    /**
     * Default constructor, instantiates a Run with default name "tmp"
     */
    public Run() {
        this("tmp");
    }

    /**
     * Constructor that instantiates a Run with a non-empty name passed as argument. It allows to also
     * set the id.
     *
     * @param name      Desired name of the Run.
     * @param id        Desired id of the Run.
     */
    public Run(String name, long id) {
        this(name);
        this.id = id;
    }

    /**
     * Copy constructor. The resulting Run is not in running state, independently of the state of the
     * copied Run.
     *
     * @param toCopy    Run to copy.
     */
    public Run(Run toCopy) {

        if (toCopy == null) {
            throw new IllegalArgumentException("Run to copy can't be null");
        }

        name = toCopy.getName();
        track = new Track(toCopy.track);

        startTime = toCopy.startTime;
        duration = toCopy.getDuration()*1000;

        isRunning = false;
        id = toCopy.id;
    }

    /**
     * Start the Run.
     *
     * @return  True if correctly started, false otherwise
     */
    public boolean start() {
        if(track.getTotalCheckPoints() == 0) {
            isRunning = true;
            startTime = SystemClock.elapsedRealtime();
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Add a new non null CheckPoint to the Run if it's active.
     *
     * @param newPoint  New CheckPoint to add.
     * @return          True if correctly updated, false otherwise.
     */
    public boolean update(CheckPoint newPoint) {

        if (newPoint == null) {
            throw new IllegalArgumentException("CheckPoint used to update Run can't be null");
        }

        if (isRunning) {
            track.add(newPoint);
        }

        return isRunning;
    }

    /**
     * Stop a Run.
     *
     * @return  True if correctly stopped, false otherwise.
     */
    public boolean stop() {
        if (isRunning){
            duration = SystemClock.elapsedRealtime() - startTime;
            isRunning = false;

            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Get the duration of the Run in seconds.
     *
     * @return  Duration in seconds
     */
    public long getDuration() {
        if (isRunning && startTime != -1) {
            return (SystemClock.elapsedRealtime() - startTime) / 1000;
        } else {
            return duration / 1000;
        }
    }

    /**
     * Getter for Run name.
     *
     * @return  Run name.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the Track, a copy is returned to preserve encapsulation.
     *
     * @return  Current Track of the Run.
     */
    public Track getTrack() {
        return new Track(track);
    }

    /**
     * Getter for the Run active status.
     *
     * @return  True if the Run is ongoing, false otherwise.
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Getter for the Id
     *
     * @return  Id of the Run.
     */
    public long getId() { return id; }

    /**
     * Setter for the Track of the Run, to avoid updating with every CheckPoint manually.
     *
     * @param track     Track we wish to set to the Run, must be non null.
     */
    public void setTrack(Track track) {
        if(track == null) {
           throw new IllegalArgumentException("Cannot set a null Track to a Run");
        }

        this.track = new Track(track);
    }

    /**
     * Set the duration of the Run from a given value in seconds.
     *
     * @param duration  Desired duration, must be positive.
     */
    public void setDuration(long duration) {
        if(duration < 0) {
            throw new IllegalArgumentException("Duration of a Run must be positive");
        }

        this.duration = duration * 1000;
    }

    /**
     * Set the id of the Run.
     *
     * @param id    Id to set.
     */
    public void setId(long id) {
        this.id = id;
    }
}
