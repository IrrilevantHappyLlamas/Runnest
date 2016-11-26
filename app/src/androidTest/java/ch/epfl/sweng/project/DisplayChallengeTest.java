package ch.epfl.sweng.project;


import org.junit.Test;

import ch.epfl.sweng.project.Fragments.DisplayChallengeFragment;
import ch.epfl.sweng.project.Model.Challenge;
import ch.epfl.sweng.project.Model.Run;
import ch.epfl.sweng.project.Model.CheckPoint;

public class DisplayChallengeTest {

    @Test
    public void DisplayChallengeFragment(){
        Run myRun = new Run("my Run");
        Run myOpponentRun = new Run("My opponent Run");
        myRun.update(new CheckPoint(20, 20));
        myOpponentRun.update(new CheckPoint(10, 10));
        Challenge challenge = new Challenge("Runnest", myRun, myOpponentRun);

        DisplayChallengeFragment fragment = DisplayChallengeFragment.newInstance(challenge);
    }
}
