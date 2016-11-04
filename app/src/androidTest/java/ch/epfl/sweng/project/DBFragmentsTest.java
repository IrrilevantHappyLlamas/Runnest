package ch.epfl.sweng.project;

import org.junit.Test;

import ch.epfl.sweng.project.Fragments.DBDownloadFragment;

/**
 * Test suite for DBDownloadFragment and DBUploadFragment
 */
public class DBFragmentsTest {

    @Test(expected = IllegalArgumentException.class)
    public void callOnSuccessAndWriteNullFile() {
        DBDownloadFragment dwFrag = new DBDownloadFragment();
        dwFrag.onSuccess(null);
    }
}
