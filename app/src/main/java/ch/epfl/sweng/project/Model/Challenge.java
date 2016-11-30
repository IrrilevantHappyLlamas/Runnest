package ch.epfl.sweng.project.Model;

import java.io.Serializable;

/**
 * This class models a Challenge, it contains your Run and your opponent one.
 *
 * @author Pablo Pfister
 */
public class Challenge implements Serializable {
    private String mOpponentName;
    private Type mType;
    private double mGoal;
    private Result mResult;
    private Run mMyRun;
    private Run mOpponentRun;
    private long mId;

    public static enum Type {
        TIME,
        DISTANCE
    }

    public static enum Result {
        WON,
        LOST,
        ABORTED_BY_ME,
        ABORTED_BY_OTHER
    }

    public Challenge(String opponentName, Type type, double goal, Result result, Run myRun, Run opponentRun) {
        if (opponentName == null || opponentName.equals("")
                || type == null || goal <= 0
                || myRun == null || opponentRun == null)
        {
            throw new IllegalArgumentException();
        }

        mOpponentName = opponentName;
        mType = type;
        mGoal = goal;
        mResult = result;
        mMyRun = new Run(myRun);
        mOpponentRun = new Run(opponentRun);
        mId = -1;
    }

    public String getOpponentName() {
        return mOpponentName;
    }

    public Type getType() {
        return mType;
    }

    public double getGoal() {
        return mGoal;
    }

    public Result getResult() {
        return mResult;
    }

    public boolean isWon() {
        return mResult == Result.ABORTED_BY_OTHER || mResult == Result.WON;
    }

    public Run getMyRun() {
        return new Run(mMyRun);
    }

    public Run getOpponentRun() {
        return new Run(mOpponentRun);
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }
}
