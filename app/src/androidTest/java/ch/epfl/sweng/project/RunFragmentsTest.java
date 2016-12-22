package ch.epfl.sweng.project;

import org.junit.Test;

import ch.epfl.sweng.project.Fragments.RunFragments.ChallengeReceiverFragment;
import ch.epfl.sweng.project.Fragments.RunFragments.ChallengeSenderFragment;

/**
 * Test suite for unit testing the following fragments: RunFragment, RunningMapFragment,
 * ChallengeReceiverFragment, ChallengeSenderFragment.
 */
public class RunFragmentsTest {

    @Test(expected = IllegalArgumentException.class)
    public void challengerReceiverOnNewDataThrowsIllegalArgument() {
        new ChallengeReceiverFragment().onNewData(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void challengerReceiverOnMapReadyThrowsIllegalArgument() {
        new ChallengeReceiverFragment().onMapReady(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void challengerSenderOnLocationChangedThrowsIllegalArgument() {
        new ChallengeSenderFragment().onLocationChanged(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void challengerSenderOnMapReadyThrowsIllegalArgument() {
        new ChallengeSenderFragment().onMapReady(null);
    }

}
