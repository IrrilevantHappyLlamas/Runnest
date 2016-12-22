package ch.epfl.sweng.project.Model;

import java.io.Serializable;

/**
 * This class models a finished Challenge, it contains your Run and your opponent's one.
 *
 * @author Pablo Pfister
 */
public class Challenge implements Serializable {
    private String opponentName;
    private Type type;
    private double goal;
    private Result result;
    private Run myRun;
    private Run opponentRun;
    private long id;

    /**
     * This enumeration represents all types of challenge that can be created
     */
    public enum Type {
        TIME,
        DISTANCE
    }

    /**
     * This enumeration represents the result of a finished challenge
     */
    public enum Result {
        WON,
        LOST,
        ABORTED_BY_ME,
        ABORTED_BY_OTHER
    }

    /**
     * The constructor of the finished challenge
     *
     * @param opponentName the name of the opponent
     * @param type the type of the challenge (based on time or distance)
     * @param goal the goal of the challenge (an amount of time or a distance)
     * @param result the result of the challenge
     * @param myRun the run of the current user
     * @param opponentRun the run of the opponent
     */
    public Challenge(String opponentName, Type type, double goal, Result result, Run myRun, Run opponentRun) {
        if (opponentName == null || type == null || myRun == null || opponentRun == null
                || opponentName.equals("") || goal <= 0) {
            throw new IllegalArgumentException();
        }

        this.opponentName = opponentName;
        this.type = type;
        this.goal = goal;
        this.result = result;
        this.myRun = new Run(myRun);
        this.opponentRun = new Run(opponentRun);
        id = -1;
    }

    /**
     * Getter for the opponent's name
     *
     * @return the opponent's name
     */
    public String getOpponentName() {
        return opponentName;
    }

    /**
     * Getter for the type of the challenge
     *
     * @return the type of the challenge
     */
    public Type getType() {
        return type;
    }

    /**
     * Getter for the goal of the challenge
     *
     * @return the goal of the challenge
     */
    public double getGoal() {
        return goal;
    }

    /**
     * Getter for the result of the challenge
     *
     * @return the result of the challenge
     */
    public Result getResult() {
        return result;
    }

    /**
     * Getter for the current user's run
     *
     * @return the current user's run
     */
    public Run getMyRun() {
        return new Run(myRun);
    }

    /**
     * Getter for the opponent's run
     *
     * @return the opponent's run
     */
    public Run getOpponentRun() {
        return new Run(opponentRun);
    }

    /**
     * Getter for the id of the challenge
     *
     * @return the id of the challenge
     */
    public long getId() {
        return id;
    }

    /**
     * Setter for the id of the challenge (used when the challenge is retrieved from the local database)
     *
     * @param id the id of the challenge that mus be positive
     */
    public void setId(long id) {
        if (id < 0) {
            throw new IllegalArgumentException();
        }
        this.id = id;
    }

    /**
     * Indicates if the challenge was won by the current user
     *
     * @return true if the challenge was won by the current user, false otherwise
     */
    public boolean isWon() {
        return result == Result.ABORTED_BY_OTHER || result == Result.WON;
    }
}
