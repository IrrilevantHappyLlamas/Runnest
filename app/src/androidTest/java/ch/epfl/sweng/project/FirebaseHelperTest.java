package ch.epfl.sweng.project;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.junit.Assert;
import org.junit.Test;

import ch.epfl.sweng.project.Model.FirebaseHelper;
import ch.epfl.sweng.project.Model.Run;

/**
 * Test suite for FirebaseHelper, has to be run completely to maintain remote database intact
 */
public class FirebaseHelperTest {

    private final FirebaseHelper firebaseHelper = new FirebaseHelper();

    @Test
    public void DBcorrectInstantiationAndRootOrganization() {

        firebaseHelper.getDatabase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Assert.assertTrue(dataSnapshot.exists());
                Assert.assertTrue(dataSnapshot.hasChild("users"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Test
    public void addingNewUserCorrectlyUpdatesDatabase() {
        firebaseHelper.addOrUpdateUser("testUser", "testName");

        firebaseHelper.getDatabase().child("users").child("testUser")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Assert.assertTrue(dataSnapshot.exists());
                Assert.assertTrue(dataSnapshot.hasChild("name"));
                Assert.assertTrue(dataSnapshot.child("name").getValue().toString().equals("testName"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Test
    public void addingExistingUserUpdatesName() {

        firebaseHelper.addOrUpdateUser("testUser", "testNameChange");

        firebaseHelper.getDatabase().child("users").child("testUser")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Assert.assertTrue(dataSnapshot.exists());
                Assert.assertTrue(dataSnapshot.hasChild("name"));
                Assert.assertTrue(dataSnapshot.child("name").getValue().toString().equals("testNameChange"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Test
    public void addingNewEffortCorrectlyUpdatesDatabase() {

        Run testRun = new Run("testRun");
        testRun.start(TrackTest.buildCheckPoint(1,1,1));
        testRun.update(TrackTest.buildCheckPoint(2,2,2));
        testRun.update(TrackTest.buildCheckPoint(3,3,3));
        testRun.stop();
        firebaseHelper.addOrUpdateEffort("testUser", testRun);

        firebaseHelper.getDatabase().child("users").child("testUser").child("efforts")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Assert.assertTrue(dataSnapshot.exists());
                Assert.assertTrue(dataSnapshot.hasChild("testRun"));
                Assert.assertTrue(dataSnapshot.child("testRun").hasChild("distance"));
                Assert.assertTrue(dataSnapshot.child("testRun").hasChild("duration"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void callingAddOrUpdateUserOnNullArgumentsLaunchesException() {
        firebaseHelper.addOrUpdateUser(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void callingAddOrUpdateUserOnEmptyArgumentsLaunchesException() {
        firebaseHelper.addOrUpdateUser("", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void callingAddOrUpdateEffortOnNullArgumentsLaunchesException() {
        firebaseHelper.addOrUpdateEffort(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void callingAddOrUpdateEffortOnEmptyArgumentsLaunchesException() {
        firebaseHelper.addOrUpdateEffort("", new Run("test"));
    }
}
