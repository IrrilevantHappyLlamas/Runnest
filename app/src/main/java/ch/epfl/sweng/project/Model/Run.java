package ch.epfl.sweng.project.Model;

import java.io.Serializable;

/**
 * Implementation of the run effort, which contains the track and
 * specifications of the run
 *
 * @author Tobia Albergoni
 */
@SuppressWarnings("ClassNamingConvention")
public class Run
        implements Effort,
        Serializable{

    private String name;
    private Track track = null;
    private boolean isRunning;

    public Run(String name) {
        this.name = name;
        isRunning = false;
    }

    public Run() {
        name = "temp";
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

    public void setTrack(Track track) {
        this.track = new Track(track);
    }

    @Override
    public boolean start(CheckPoint startingPoint) {
        if (track == null) {
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
}
