package ch.epfl.sweng.project.Model;

/**
 * Interface to implement different kinds of Efforts
 *
 * @author Tobia Albergoni
 */
public interface Effort {

    String getName();
    Track getTrack();

    boolean start(CheckPoint startingPoint);
    boolean isRunning();
    boolean update(CheckPoint newPoint);
    boolean stop();
}
