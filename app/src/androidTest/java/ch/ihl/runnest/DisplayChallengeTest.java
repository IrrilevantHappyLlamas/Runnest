package ch.ihl.runnest;


import org.junit.Test;

import ch.ihl.runnest.Fragments.DisplayChallengeFragment;
import ch.ihl.runnest.Model.Challenge;
import ch.ihl.runnest.Model.CheckPoint;
import ch.ihl.runnest.Model.Run;

public class DisplayChallengeTest {

    @Test
    public void DisplayChallengeFragment(){
        Run myRun = new Run("my Run");
        Run myOpponentRun = new Run("My opponent Run");
        myRun.update(new CheckPoint(20, 20));
        myOpponentRun.update(new CheckPoint(10, 10));
        Challenge challenge = new Challenge("Runnest", Challenge.Type.TIME, 100, Challenge.Result.WON, myRun, myOpponentRun);

        DisplayChallengeFragment.newInstance(challenge);
    }
}
