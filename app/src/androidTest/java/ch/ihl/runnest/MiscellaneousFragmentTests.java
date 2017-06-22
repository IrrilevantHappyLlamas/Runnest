package ch.ihl.runnest;

import org.junit.Assert;
import org.junit.Test;

import ch.ihl.runnest.Fragments.DBDownloadFragment;
import ch.ihl.runnest.Fragments.MemoDialogFragment;

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
