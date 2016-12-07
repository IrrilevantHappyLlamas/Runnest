package ch.epfl.sweng.project;

import android.os.SystemClock;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.project.Firebase.FirebaseHelper;
import ch.epfl.sweng.project.Model.CheckPoint;
import ch.epfl.sweng.project.Model.Message;

/**
 * Test suite for FirebaseHelper, has to be run completely to maintain remote database intact
 */
public class FirebaseHelperTest {

    private FirebaseHelper firebaseHelper;
    private ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Before
    public void instantiation() {
        firebaseHelper = new FirebaseHelper();
    }

    @Test
    public void DBcorrectInstantiationAndRootOrganization() {
        firebaseHelper.getDatabase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Assert.assertTrue(dataSnapshot.exists());
                Assert.assertTrue(dataSnapshot.hasChild("messages"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Test
    public void sendingMessageCorrectlyUpdatesDatabase() {
        String to = "you";
        Message msg = new Message("me", to, "me", "you", Message.MessageType.TEXT, "Hello, world!");
        firebaseHelper.send(msg);
        firebaseHelper.getDatabase().child("messages").child(to)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot = dataSnapshot.getChildren().iterator().next();
                        Assert.assertTrue(dataSnapshot.exists());
                        Assert.assertTrue(dataSnapshot.hasChild("from"));
                        Assert.assertTrue(dataSnapshot.hasChild("type"));
                        Assert.assertTrue(dataSnapshot.hasChild("message"));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Test
    public void deleteCorrectly() {
        String to = "you";
        Message msg = new Message("me", to, "me", "you", Message.MessageType.TEXT, "Hello, world!");

        firebaseHelper.send(msg);
        SystemClock.sleep(2000);

        firebaseHelper.delete(msg);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addOrUpdateUserThrowsIllegalArgumentIOnNull() {
        firebaseHelper.addOrUpdateUser("test", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addOrUpdateUserThrowsIllegalArgumentOnEmpty() {
        firebaseHelper.addOrUpdateUser("test", "");
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
    public void fetchingMessageCorrectlyReadsDatabase() {
        final List<Message> msgs = new ArrayList<>();
        firebaseHelper.fetchMessages("you", new FirebaseHelper.Handler() {
            @Override
            public void handleRetrievedMessages(List<Message> messages) {
                for (Message m : messages) {
                    msgs.add(m);
                }
                Assert.assertFalse(msgs.isEmpty());
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
    public void correctlyAddChallengeNode() {
        firebaseHelper.addChallengeNode("testUser1", "testUser2", "testChallenge");
        firebaseHelper.getDatabase().child("challenges").child("testChallenge")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Assert.assertTrue(dataSnapshot.exists());
                        Assert.assertTrue(dataSnapshot.hasChild("testUser1"));
                        Assert.assertTrue(dataSnapshot.hasChild("testUser2"));
                        DataSnapshot user1 = dataSnapshot.child("testUser1");
                        DataSnapshot user2 = dataSnapshot.child("testUser2");
                        Assert.assertTrue(user1.hasChild(FirebaseHelper.challengeNodeType.READY.toString()));
                        Assert.assertFalse((boolean)user1.child(FirebaseHelper.challengeNodeType.READY.toString()).getValue());
                        Assert.assertTrue(user2.hasChild(FirebaseHelper.challengeNodeType.READY.toString()));
                        Assert.assertFalse((boolean)user2.child(FirebaseHelper.challengeNodeType.READY.toString()).getValue());
                        Assert.assertTrue(user1.hasChild(FirebaseHelper.challengeNodeType.FINISH.toString()));
                        Assert.assertFalse((boolean)user1.child(FirebaseHelper.challengeNodeType.FINISH.toString()).getValue());
                        Assert.assertTrue(user2.hasChild(FirebaseHelper.challengeNodeType.FINISH.toString()));
                        Assert.assertFalse((boolean)user2.child(FirebaseHelper.challengeNodeType.FINISH.toString()).getValue());
                        Assert.assertTrue(user1.hasChild(FirebaseHelper.challengeNodeType.ABORT.toString()));
                        Assert.assertFalse((boolean)user1.child(FirebaseHelper.challengeNodeType.ABORT.toString()).getValue());
                        Assert.assertTrue(user2.hasChild(FirebaseHelper.challengeNodeType.ABORT.toString()));
                        Assert.assertFalse((boolean)user2.child(FirebaseHelper.challengeNodeType.ABORT.toString()).getValue());
                        Assert.assertTrue(user1.hasChild(FirebaseHelper.challengeNodeType.IN_ROOM.toString()));
                        Assert.assertFalse((boolean)user1.child(FirebaseHelper.challengeNodeType.IN_ROOM.toString()).getValue());
                        Assert.assertTrue(user2.hasChild(FirebaseHelper.challengeNodeType.IN_ROOM.toString()));
                        Assert.assertFalse((boolean)user2.child(FirebaseHelper.challengeNodeType.IN_ROOM.toString()).getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Test(expected = NullPointerException.class)
    public void addChallengeNodeThrowsNullPointer() {
        firebaseHelper.addChallengeNode("testUser1", "testUser2", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addChallengeNodeThrowsIllegalArgument() {
        firebaseHelper.addChallengeNode("testUser1", "testUser2", "");
    }

    @Test(expected = NullPointerException.class)
    public void deleteChallengeNodeThrowsNullPointer() {
        firebaseHelper.deleteChallengeNode(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteChallengeNodeThrowsIllegalArgument() {
        firebaseHelper.deleteChallengeNode("");
    }

    @Test
    public void correctlyAddChallengeCheckPoint() {
        CheckPoint checkPoint = new CheckPoint(100, 100);
        firebaseHelper.addChallengeCheckPoint(checkPoint, "testChallenge",  "testUser1", 0);
        firebaseHelper.getDatabase().child("challenges").child("testChallenge").child("testUser1")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Assert.assertTrue(dataSnapshot.exists());
                        Assert.assertTrue(dataSnapshot.hasChild("checkpoints"));
                        DataSnapshot checkpoints = dataSnapshot.child("checkpoints");
                        Assert.assertTrue(checkpoints.hasChild("0"));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Test(expected = NullPointerException.class)
    public void addChallengeCheckPointThrowsNullPointer() {
        firebaseHelper.addChallengeCheckPoint(null, "testChallenge",  "testUser1", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addChallengeCheckPointThrowsIllegalArgument() {
        CheckPoint checkPoint = new CheckPoint(100, 100);
        firebaseHelper.addChallengeCheckPoint(checkPoint, "",  "testUser1", 0);
    }

    @Test
    public void correctlySetUserReady() {
        firebaseHelper.setUserStatus("testChallenge", "testUser1", FirebaseHelper.challengeNodeType.READY, true);
        firebaseHelper.getDatabase().child("challenges").child("testChallenge")
                .child("testUser1").child(FirebaseHelper.challengeNodeType.READY.toString())
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

    @Test(expected = NullPointerException.class)
    public void setUserReadyThrowsNullPointer() {
        firebaseHelper.setUserStatus(null, "testUser1", FirebaseHelper.challengeNodeType.READY, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setUserReadyThrowsIllegalArgument() {
        firebaseHelper.setUserStatus("", "testUser1", FirebaseHelper.challengeNodeType.READY, true);
    }

    @Test
    public void correctlySetUserListeners() {
        firebaseHelper.setUserChallengeListener("testChallenge", "testUser1", listener, FirebaseHelper.challengeNodeType.READY);
        firebaseHelper.setUserChallengeListener("testChallenge", "testUser1", listener, FirebaseHelper.challengeNodeType.FINISH);
        firebaseHelper.setUserChallengeListener("testChallenge", "testUser1", listener, FirebaseHelper.challengeNodeType.DATA);
    }

    @Test(expected = NullPointerException.class)
    public void setUserStatusListenerThrowsNullPointer() {
        firebaseHelper.setUserChallengeListener("testChallenge", "testUser1", null, FirebaseHelper.challengeNodeType.READY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setUserStatusListenerThrowsIllegalArgument() {
        firebaseHelper.setUserChallengeListener("", "testUser1", listener, FirebaseHelper.challengeNodeType.READY);
    }

    @Test
    public void removeUserListenerWorks() {
        firebaseHelper.removeUserChallengeListener("testChallenge", "testUser1", listener, FirebaseHelper.challengeNodeType.READY);
        firebaseHelper.removeUserChallengeListener("testChallenge", "testUser1", listener, FirebaseHelper.challengeNodeType.FINISH);
        firebaseHelper.removeUserChallengeListener("testChallenge", "testUser1", listener, FirebaseHelper.challengeNodeType.DATA);
    }

    @Test(expected = NullPointerException.class)
    public void removeUserListenerThrowsNullPointer() {
        firebaseHelper.removeUserChallengeListener("testChallenge", "testUser1", null, FirebaseHelper.challengeNodeType.READY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeUserListenerThrowsIllegalArgument() {
        firebaseHelper.removeUserChallengeListener("", "testUser1", listener, FirebaseHelper.challengeNodeType.READY);
    }

    @Test
    public void deleteChallengeNodeWorks() {
        firebaseHelper.deleteChallengeNode("testChallenge");
    }

    @Test
    public void addNewUser() {
        firebaseHelper.getDatabase().child("users").child("uselessUser").removeValue();
        firebaseHelper.addOrUpdateUser("uselessUser", "uselessMail");
        firebaseHelper.getDatabase().child("users").child("uselessUser").removeValue();
    }

    @Test
    public void setUserAvailableCorrectlySetsStatus() {
        firebaseHelper.setUserAvailable("Test User", false, true);
        firebaseHelper.getDatabase().child("users").child("Test User")
                .child("available")
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

    @Test(expected = NullPointerException.class)
    public void setUserAvailableThrowsNullPointer() {
        firebaseHelper.setUserAvailable(null , false, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setUserAvailableThrowsIllegalArgument() {
        firebaseHelper.setUserAvailable("" ,false, true);
    }

    @Test(expected = NullPointerException.class)
    public void listenUserAvailabilityThrowsNullPointer() {
        firebaseHelper.listenUserAvailability("Test User", false, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void listenUserAvailabilityThrowsIllegalArgument() {
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
        firebaseHelper.setUserAvailable("Test User", false, true);
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

        firebaseHelper.listenUserAvailability("Test User", false,  listener);
    }



    /*
    @Test
    public void deletingMessageCorrectlyReadsDatabase() {
        final List<Message> msgs = new ArrayList<>();
        firebaseHelper.fetchMessages("you", new FirebaseHelper.Handler() {
            @Override
            public void handleRetrievedMessages(List<Message> messages1) {
                Assert.assertFalse(messages1.isEmpty());
                final String messageUid = messages1.get(0).getUid();
                firebaseHelper.delete(messages1.get(0));

                SystemClock.sleep(3000);

                firebaseHelper.fetchMessages("you", new FirebaseHelper.Handler() {
                    @Override
                    public void handleRetrievedMessages(List<Message> messages2) {
                        for (Message m : messages2) {
                            if (m.getUid().equals(messageUid)) {
                                Assert.assertTrue(false);
                            }
                        }
                    }
                });
            }
        });
    }
    */
}
