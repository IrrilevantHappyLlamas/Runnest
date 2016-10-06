package ch.epfl.sweng.project;

/**
 * Interface to implement different kinds of Efforts
 *
 * @author Tobia Albergoni
 */
public interface Effort {

    public String getName();
    public Track getTrack();

    public boolean start(CheckPoint startingPoint);
    public boolean isRunning();
    public boolean update(CheckPoint newPoint);
    public boolean stop();
}
