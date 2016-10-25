package ch.epfl.sweng.project.Model;

import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;

import java.util.Date;
import java.io.Serializable;

/**
 * Implementation of the run effort, which contains the track and
 * specifications of the run
 */
@SuppressWarnings("ClassNamingConvention")
public class Run implements Serializable {

    private String name;
    private Track track = null;
    private long duration;
    private boolean isRunning;

    private long startTime;

    /**
     * Constructor that instantiates a <code>Run</code> with the name passed as argument
     *
     * @param name  the name of the <code>Run</code>
     */
    public Run(String name) {
        this.name = name;
        isRunning = false;
        track = new Track();
        startTime = -1;
    }

    /**
     * Default constructor, instantiates a <code>Run</code> with default name "temp"
     */
    public Run() {
        name = "temp";
        isRunning = false;
        track = new Track();
        startTime = -1;
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

        //TODO: evaluate where to multiply and divide
        startTime = toCopy.startTime;
        duration = toCopy.getDuration()*1000;

        isRunning = false;
    }

    //Passo la base del cronometro o getto qui?
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

    public boolean update(CheckPoint newPoint) {
        return isRunning && track.add(newPoint);
    }

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

    public long getDuration() {
        if(isRunning) {
            return (SystemClock.elapsedRealtime() - startTime)/1000;
        } else {
            return duration/1000;
        }
    }

    public String getName() {
        return name;
    }

    public Track getTrack() {
        return new Track(track);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setTrack(Track track) {
        this.track = new Track(track);
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
