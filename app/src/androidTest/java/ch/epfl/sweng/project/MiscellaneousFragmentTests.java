package ch.epfl.sweng.project;

import org.junit.Assert;
import org.junit.Test;

import ch.epfl.sweng.project.Fragments.DBDownloadFragment;
import ch.epfl.sweng.project.Fragments.MemoDialogFragment;

/**
 * Test suite for DBDownloadFragment
 */
public class MiscellaneousFragmentTests {

    @Test(expected = IllegalArgumentException.class)
    public void callOnSuccessAndWriteNullFileDBDownloadFragment() {
        DBDownloadFragment dwFrag = new DBDownloadFragment();
        dwFrag.onSuccess(null);
    }

    @Test
    public void getTypeOnVanillaMemoDialogFragmentIsNull() {
        MemoDialogFragment frag = new MemoDialogFragment();
        Assert.assertTrue(frag.getType() == null);
    }


}
