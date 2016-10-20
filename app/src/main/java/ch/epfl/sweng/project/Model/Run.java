package ch.epfl.sweng.project.Model;

import java.util.Date;
import java.io.Serializable;

/**
 * Implementation of the run effort, which contains the track and
 * specifications of the run
 */
@SuppressWarnings("ClassNamingConvention")
public class Run
        implements Effort,
        Serializable{

    private String name;
    private Track track = null;
    private boolean isRunning;

    /**
     * Constructor that instantiates a <code>Run</code> with the name passed as argument
     *
     * @param name  the name of the <code>Run</code>
     */
    public Run(String name) {
        this.name = name;
        isRunning = false;
        track = new Track();
    }

    /**
     * Default constructor, instantiates a <code>Run</code> with default name "temp"
     */
    public Run() {
        name = "temp";
        isRunning = false;
        track = new Track();
    }

    /**
     * Copy constructor. The resulting <code>Run</code> is not in running state, independently of the state of the
     * copied <code>Run</code>
     *
     * @param toCopy    <code>Run</code> to copy
     */
    public Run(Run toCopy) {
        name = toCopy.getName();
        track = new Track(toCopy.track);
        isRunning = false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Track getTrack() {
        return new Track(track);
    }

    @Override
    public boolean start(CheckPoint startingPoint) {
        if (track.getTotalCheckPoints() == 0) {
            track = new Track(startingPoint);
            isRunning = true;
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public boolean update(CheckPoint newPoint) {
        return isRunning && track.add(newPoint);
    }

    @Override
    public boolean stop() {
        if (isRunning){
            isRunning = false;
            return true;
        }
        else {
            return false;
        }
    }

    public void setTrack(Track track) {
        this.track = new Track(track);
    }
}
