package ch.epfl.sweng.project.Model;

/**
 * This class models a Challenge, it contains your Run and your opponent one.
 *
 * @author Pablo Pfister
 */
public class Challenge {
    private String mOpponentName;
    private Run mMyRun;
    private Run mOpponentRun;

    public Challenge(String opponentName, Run myRun, Run opponentRun) {
        if (opponentName == null || opponentName.equals("") || myRun == null || opponentRun == null) {
            throw new IllegalArgumentException();
        }

        mOpponentName = opponentName;
        mMyRun = new Run(myRun);
        mOpponentRun = new Run(opponentRun);
    }

    public String getOpponentName() {
        return mOpponentName;
    }

    public Run getMyRun() {
        return new Run(mMyRun);
    }

    public Run getOpponentRun() {
        return new Run(mOpponentRun);
    }
}
