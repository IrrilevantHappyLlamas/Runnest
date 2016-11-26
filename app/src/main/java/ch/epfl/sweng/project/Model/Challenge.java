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
    private boolean mIsWon;
    private Run mMyRun;
    private Run mOpponentRun;
    private long mId;

    public static enum Type {
        TIME,
        DISTANCE
    }

    public Challenge(String opponentName, Type type, double goal, boolean isWon, Run myRun, Run opponentRun) {
        if (opponentName == null || opponentName.equals("")
                || type == null || goal <= 0
                || myRun == null || opponentRun == null)
        {
            throw new IllegalArgumentException();
        }

        mOpponentName = opponentName;
        mType = type;
        mGoal = goal;
        mIsWon = isWon;
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

    public boolean isWon() {
        return mIsWon;
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
