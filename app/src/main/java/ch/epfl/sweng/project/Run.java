package ch.epfl.sweng.project;

import android.icu.text.SimpleDateFormat;

import java.util.Calendar;

/**
 * Implementation of the run effort, which contains the track and
 * specifications of the run
 *
 * @author Tobia Albergoni
 */
public class Run implements Effort {

    private String name;
    private Track track;
    private boolean isRunning;

    public Run(String name) {
        this.name = name;
        isRunning = false;
    }

    public Run() {
        this.name = "temp";
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
        if (isRunning) {
            return track.add(newPoint);
        }
        else {
            return false;
        }
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
