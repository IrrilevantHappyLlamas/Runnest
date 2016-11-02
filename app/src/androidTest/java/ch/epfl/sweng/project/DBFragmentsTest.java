package ch.epfl.sweng.project;

import org.junit.Test;

import ch.epfl.sweng.project.Fragments.DBDownloadFragment;

/**
 * Created by Tobia Albergoni on 31.10.2016.
 */
public class DBFragmentsTest {

    @Test(expected = IllegalArgumentException.class)
    public void DWcallOnSuccessAndWriteNullFile() {
        DBDownloadFragment dwFrag = new DBDownloadFragment();
        dwFrag.onSuccess(null);
    }
}
