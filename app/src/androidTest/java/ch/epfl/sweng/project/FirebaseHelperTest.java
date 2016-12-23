package ch.epfl.sweng.project;

import android.os.SystemClock;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import ch.epfl.sweng.project.Firebase.FirebaseHelper;
import ch.epfl.sweng.project.Firebase.FirebaseNodes;
import ch.epfl.sweng.project.Model.Challenge;
import ch.epfl.sweng.project.Model.CheckPoint;
import ch.epfl.sweng.project.Model.Message;

/**
 * Test suite for FirebaseHelper.
 */
public class FirebaseHelperTest {

    private FirebaseHelper firebaseHelper;
    private final ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    private final String TEST_USER = "Test User";

    private Message createTestMessage() {
        return new Message( "Test Sender",
                TEST_USER,
                "Tester",
                "Tested",
                Message.Type.CHALLENGE_REQUEST,
                "This is a test",
                new Date(),
                1,
                0,
                Challenge.Type.DISTANCE );
    }

    @Before
    public void instantiation() {
        firebaseHelper = new FirebaseHelper();
    }

    @Test
    public void databaseCorrectInstantiationAndRootOrganization() {
        firebaseHelper.getDatabase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Assert.assertTrue(dataSnapshot.exists());
                Assert.assertTrue(dataSnapshot.hasChild(FirebaseNodes.MESSAGES));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendMessageThrowsIllegalArgument() {
        firebaseHelper.sendMessage(null);
    }

    @Test
    public void sendingMessageCorrectlyUpdatesDatabase() {
        firebaseHelper.sendMessage(createTestMessage());
        firebaseHelper.getDatabase().child(FirebaseNodes.MESSAGES).child(TEST_USER)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Assert.assertTrue(dataSnapshot.exists());
                        DataSnapshot mex = dataSnapshot.getChildren().iterator().next();
                        Assert.assertTrue(mex.hasChild(FirebaseNodes.MEX_FROM));
                        Assert.assertTrue(mex.hasChild(FirebaseNodes.MEX_TYPE));
                        Assert.assertTrue(mex.hasChild(FirebaseNodes.MEX));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteUserMessagesThrowsIllegalArgumentOnNull() {
        firebaseHelper.deleteUserMessages(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteUserMessagesThrowsIllegalArgumentOnEmpty() {
        firebaseHelper.deleteUserMessages("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void fetchMessagesThrowsIllegalArgumentOnNull() {
        firebaseHelper.fetchMessages(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fetchMessagesThrowsIllegalArgumentOnEmpty() {
        firebaseHelper.fetchMessages("", new FirebaseHelper.Handler() {
            @Override
            public void handleRetrievedMessages(List<Message> messages) {

            }
        });
    }

    @Test
    public void fetchingMessageCorrectlyReadsDatabase() {
        firebaseHelper.sendMessage(createTestMessage());
        firebaseHelper.fetchMessages(TEST_USER, new FirebaseHelper.Handler() {
            @Override
            public void handleRetrievedMessages(List<Message> messages) {
                Assert.assertFalse(messages.isEmpty());
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteMessageThrowsIllegalArgumentNull() {
        firebaseHelper.deleteMessage(null);
    }

    @Test
    public void deleteCorrectly() {
        Message msg = createTestMessage();
        firebaseHelper.sendMessage(msg);
        // Needed to be sure that the sent message appears on firebase
        SystemClock.sleep(2000);
        firebaseHelper.deleteMessage(msg);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addOrUpdateUserThrowsIllegalArgumentIOnNull() {
        firebaseHelper.addOrUpdateUser("test", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addOrUpdateUserThrowsIllegalArgumentOnEmpty() {
        firebaseHelper.addOrUpdateUser("test", "");
    }

    @Test
    public void addOrUpdateUserCorrectlySetsNode() {
        firebaseHelper.addOrUpdateUser(TEST_USER, TEST_USER);
        firebaseHelper.getDatabase().child(FirebaseNodes.USERS).child(TEST_USER).child(FirebaseNodes.NAME)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Assert.assertTrue(dataSnapshot.exists());
                        Assert.assertTrue(dataSnapshot.getValue().toString().equals(TEST_USER));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
    }

    @Test
    public void addNewUser() {
        firebaseHelper.getDatabase().child(FirebaseNodes.USERS).child("TestNewMail").removeValue();
        // Needed to be sure that the sent message appears on firebase
        SystemClock.sleep(2000);
        firebaseHelper.addOrUpdateUser("TestNewUser", "TestNewMail");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setOrUpdateProfilePicUrlThrowsExceptionWithNullEmail() {
        firebaseHelper.setOrUpdateProfilePicUrl(null, "http://url.test.ch");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setOrUpdateProfilePicUrlThrowsExceptionWithEmptyEmail() {
        firebaseHelper.setOrUpdateProfilePicUrl("", "http://url.test.ch");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setOrUpdateProfilePicUrlThrowsExceptionWithNullUrl() {
        firebaseHelper.setOrUpdateProfilePicUrl(TEST_USER, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setOrUpdateProfilePicUrlThrowsExceptionWithEmptyUrl() {
        firebaseHelper.setOrUpdateProfilePicUrl(TEST_USER, "");
    }

    @Test
    public void canSetOrUpdateProfilePicUrl() {
        firebaseHelper.addOrUpdateUser(TEST_USER, TEST_USER);
        firebaseHelper.setOrUpdateProfilePicUrl(TEST_USER, "http://url.test.ch");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getProfilePicUrlThrowsExceptionWithNullEmail() {
        firebaseHelper.getProfilePicUrl(null, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void getProfilePicUrlThrowsExceptionWithEmptyEmail() {
        firebaseHelper.getProfilePicUrl("", new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void getProfilePicUrlThrowsExceptionWithNullListener() {
        firebaseHelper.getProfilePicUrl(TEST_USER, null);
    }

    @Test
    public void canGetProfilePicUrl() {
        final String testUserForPic = "Test User for Pic";
        final String url = "http://url.test.ch";
        firebaseHelper.addOrUpdateUser(TEST_USER, testUserForPic);
        firebaseHelper.setOrUpdateProfilePicUrl(testUserForPic, url);
        firebaseHelper.getProfilePicUrl(testUserForPic, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String fetchedUrl = (String) dataSnapshot.getValue();
                Assert.assertTrue(url.equals(fetchedUrl));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateUserStatisticThrowsIllegalArgumentIOnNull() {
        firebaseHelper.updateUserStatistics(null, (long)10, (float)10, FirebaseHelper.RunType.SINGLE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateUserStatisticThrowsIllegalArgumentOnEmpty() {
        firebaseHelper.updateUserStatistics("", (long)10, (float)10, FirebaseHelper.RunType.SINGLE);
    }

    @Test
    public void updateUserMissingStatistics() {
        firebaseHelper.getDatabase().child(FirebaseNodes.USERS).child("UserWithoutStatistics")
                .child(FirebaseNodes.STATISTICS).child("non existing statistic").setValue(0);
        firebaseHelper.updateUserStatistics("UserWithoutStatistics", 1, 1, FirebaseHelper.RunType.SINGLE);
        firebaseHelper.updateUserStatistics("UserWithoutStatistics", 1, 1, FirebaseHelper.RunType.CHALLENGE_LOST);
        firebaseHelper.updateUserStatistics("UserWithoutStatistics", 1, 1, FirebaseHelper.RunType.CHALLENGE_WON);
        // Needed to be sure that the sent message appears on firebase
        SystemClock.sleep(2000);
        firebaseHelper.getDatabase().child(FirebaseNodes.USERS).child("UserWithoutStatistics")
                .child(FirebaseNodes.STATISTICS).removeValue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getUserStatisticThrowsIllegalArgumentIOnNull() {
        firebaseHelper.getUserStatistics(null, new FirebaseHelper.statisticsHandler() {
            @Override
            public void handleRetrievedStatistics(String[] statistics) {
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void getUserStatisticThrowsIllegalArgumentOnEmpty() {
        firebaseHelper.getUserStatistics("", new FirebaseHelper.statisticsHandler() {
            @Override
            public void handleRetrievedStatistics(String[] statistics) {
            }
        });
    }

    @Test
    public void setUserAvailableCorrectlySetsStatus() {
        firebaseHelper.setUserAvailable(TEST_USER, false, true);
        firebaseHelper.getDatabase().child(FirebaseNodes.USERS).child(TEST_USER)
                .child(FirebaseNodes.AVAILABLE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Assert.assertTrue(dataSnapshot.exists());
                        Assert.assertTrue((boolean)dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Test(expected = IllegalArgumentException.class)
    public void setUserAvailableThrowsIllegalArgumentOnNull() {
        firebaseHelper.setUserAvailable(null , false, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setUserAvailableThrowsIllegalArgumentOnEmpty() {
        firebaseHelper.setUserAvailable("" ,false, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void listenUserAvailabilityThrowsIllegalArgumentOnNull() {
        firebaseHelper.listenUserAvailability(TEST_USER, false, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void listenUserAvailabilityThrowsIllegalArgumentOnEmpty() {
        firebaseHelper.listenUserAvailability("" , false, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Test
    public void listenUserAvailabilityCorrectlyAttachesListener() {
        firebaseHelper.setUserAvailable(TEST_USER, false, true);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Assert.assertTrue(dataSnapshot.exists());
                Assert.assertTrue((boolean)dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        firebaseHelper.listenUserAvailability(TEST_USER, false,  listener);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getFirebaseMailThrowsIllegalArgument() {
        FirebaseHelper.getFireBaseMail(null);
    }

    @Test
    public void correctlyAddChallengeNode() {
        firebaseHelper.addChallengeNode("testUser1", "testUser2", "testChallenge");
        firebaseHelper.getDatabase().child(FirebaseNodes.CHALLENGES).child("testChallenge")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Assert.assertTrue(dataSnapshot.exists());
                        Assert.assertTrue(dataSnapshot.hasChild("testUser1"));
                        Assert.assertTrue(dataSnapshot.hasChild("testUser2"));
                        DataSnapshot user1 = dataSnapshot.child("testUser1");
                        DataSnapshot user2 = dataSnapshot.child("testUser2");
                        Assert.assertTrue(user1.hasChild(FirebaseNodes.ChallengeStatus.READY.toString()));
                        Assert.assertFalse((boolean)user1.child(FirebaseNodes.ChallengeStatus.READY.toString()).getValue());
                        Assert.assertTrue(user2.hasChild(FirebaseNodes.ChallengeStatus.READY.toString()));
                        Assert.assertFalse((boolean)user2.child(FirebaseNodes.ChallengeStatus.READY.toString()).getValue());
                        Assert.assertTrue(user1.hasChild(FirebaseNodes.ChallengeStatus.FINISH.toString()));
                        Assert.assertFalse((boolean)user1.child(FirebaseNodes.ChallengeStatus.FINISH.toString()).getValue());
                        Assert.assertTrue(user2.hasChild(FirebaseNodes.ChallengeStatus.FINISH.toString()));
                        Assert.assertFalse((boolean)user2.child(FirebaseNodes.ChallengeStatus.FINISH.toString()).getValue());
                        Assert.assertTrue(user1.hasChild(FirebaseNodes.ChallengeStatus.ABORT.toString()));
                        Assert.assertFalse((boolean)user1.child(FirebaseNodes.ChallengeStatus.ABORT.toString()).getValue());
                        Assert.assertTrue(user2.hasChild(FirebaseNodes.ChallengeStatus.ABORT.toString()));
                        Assert.assertFalse((boolean)user2.child(FirebaseNodes.ChallengeStatus.ABORT.toString()).getValue());
                        Assert.assertTrue(user1.hasChild(FirebaseNodes.ChallengeStatus.IN_ROOM.toString()));
                        Assert.assertFalse((boolean)user1.child(FirebaseNodes.ChallengeStatus.IN_ROOM.toString()).getValue());
                        Assert.assertTrue(user2.hasChild(FirebaseNodes.ChallengeStatus.IN_ROOM.toString()));
                        Assert.assertFalse((boolean)user2.child(FirebaseNodes.ChallengeStatus.IN_ROOM.toString()).getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
    }

    @Test(expected = IllegalArgumentException.class)
    public void addChallengeNodeThrowsIllegalArgumentOnNull() {
        firebaseHelper.addChallengeNode("testUser1", "testUser2", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addChallengeNodeThrowsIllegalArgumentOnEmpty() {
        firebaseHelper.addChallengeNode("testUser1", "testUser2", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteChallengeNodeThrowsIllegalArgumentOnNull() {
        firebaseHelper.deleteChallengeNode(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteChallengeNodeThrowsIllegalArgumentOnEmpty() {
        firebaseHelper.deleteChallengeNode("");
    }

    @Test
    public void deleteChallengeNodeWorks() {
        firebaseHelper.addChallengeNode("testUser1", "testUser2", "testChallenge2");
        // Needed to be sure that the sent message appears on firebase
        SystemClock.sleep(2000);
        firebaseHelper.deleteChallengeNode("testChallenge2");
    }

    @Test
    public void correctlyAddChallengeCheckPoint() {
        CheckPoint checkPoint = new CheckPoint(90, 90);
        firebaseHelper.addChallengeCheckPoint(checkPoint, "testChallenge",  "testUser1", 0);
        firebaseHelper.getDatabase().child(FirebaseNodes.CHALLENGES).child("testChallenge").child("testUser1")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Assert.assertTrue(dataSnapshot.exists());
                        Assert.assertTrue(dataSnapshot.hasChild(FirebaseNodes.CHECKPOINTS));
                        DataSnapshot checkpoints = dataSnapshot.child(FirebaseNodes.CHECKPOINTS);
                        Assert.assertTrue(checkpoints.hasChild("0"));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
    }

    @Test(expected = IllegalArgumentException.class)
    public void addChallengeCheckPointThrowsIllegalArgumentOnNull() {
        firebaseHelper.addChallengeCheckPoint(null, "testChallenge",  "testUser1", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addChallengeCheckPointThrowsIllegalArgumentOnEmpty() {
        CheckPoint checkPoint = new CheckPoint(90, 90);
        firebaseHelper.addChallengeCheckPoint(checkPoint, "",  "testUser1", 0);
    }

    @Test
    public void correctlySetUserReady() {
        firebaseHelper.setUserStatus("testChallenge", "testUser1", FirebaseNodes.ChallengeStatus.READY, true);
        firebaseHelper.getDatabase().child(FirebaseNodes.CHALLENGES).child("testChallenge")
                .child("testUser1").child(FirebaseNodes.ChallengeStatus.READY.toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Assert.assertTrue(dataSnapshot.exists());
                        Assert.assertTrue((boolean)dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Test(expected = IllegalArgumentException.class)
    public void setUserReadyThrowsIllegalArgumentOnNull() {
        firebaseHelper.setUserStatus(null, "testUser1", FirebaseNodes.ChallengeStatus.READY, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setUserReadyThrowsIllegalArgumentOnEmpty() {
        firebaseHelper.setUserStatus("", "testUser1", FirebaseNodes.ChallengeStatus.READY, true);
    }

    @Test
    public void correctlySetUserListeners() {
        firebaseHelper.setUserChallengeListener("testChallenge", "testUser1", listener, FirebaseNodes.ChallengeStatus.READY);
        firebaseHelper.setUserChallengeListener("testChallenge", "testUser1", listener, FirebaseNodes.ChallengeStatus.FINISH);
        firebaseHelper.setUserChallengeListener("testChallenge", "testUser1", listener, FirebaseNodes.ChallengeStatus.DATA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setUserStatusListenerThrowsIllegalArgumentOnNull() {
        firebaseHelper.setUserChallengeListener("testChallenge", "testUser1", null, FirebaseNodes.ChallengeStatus.READY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setUserStatusListenerThrowsIllegalArgumentOnEmpty() {
        firebaseHelper.setUserChallengeListener("", "testUser1", listener, FirebaseNodes.ChallengeStatus.READY);
    }

    @Test
    public void removeUserListenerWorks() {
        firebaseHelper.removeUserChallengeListener("testChallenge", "testUser1", listener, FirebaseNodes.ChallengeStatus.READY);
        firebaseHelper.removeUserChallengeListener("testChallenge", "testUser1", listener, FirebaseNodes.ChallengeStatus.FINISH);
        firebaseHelper.removeUserChallengeListener("testChallenge", "testUser1", listener, FirebaseNodes.ChallengeStatus.DATA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeUserListenerThrowsIllegalArgumentOnNull() {
        firebaseHelper.removeUserChallengeListener("testChallenge", "testUser1", null, FirebaseNodes.ChallengeStatus.READY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeUserListenerThrowsIllegalArgumentOnEmpty() {
        firebaseHelper.removeUserChallengeListener("", "testUser1", listener, FirebaseNodes.ChallengeStatus.READY);
    }
}
