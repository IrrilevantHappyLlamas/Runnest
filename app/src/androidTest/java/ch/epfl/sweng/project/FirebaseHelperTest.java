package ch.epfl.sweng.project;

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
                        Assert.assertTrue(user1.hasChild("status"));
                        Assert.assertFalse((boolean)user1.child("status").getValue());
                        Assert.assertTrue(user2.hasChild("status"));
                        Assert.assertFalse((boolean)user2.child("status").getValue());
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
        CheckPoint checkPoint = new CheckPoint(100, 100);
        firebaseHelper.addChallengeCheckPoint(null, "testChallenge",  "testUser1", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addChallengeCheckPointThrowsIllegalArgument() {
        CheckPoint checkPoint = new CheckPoint(100, 100);
        firebaseHelper.addChallengeCheckPoint(checkPoint, "",  "testUser1", 0);
    }

    @Test
    public void correctlySetUserReady() {
        firebaseHelper.setUserReady("testChallenge", "testUser1");
        firebaseHelper.getDatabase().child("challenges").child("testChallenge")
                .child("testUser1").child("status")
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
        firebaseHelper.setUserReady(null,  "testUser1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setUserReadyThrowsIllegalArgument() {
        firebaseHelper.setUserReady("",  "testUser1");
    }

    @Test
    public void correctlySetUserListeners() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        firebaseHelper.setUserChallengeListener("testChallenge", "testUser1", listener);
        firebaseHelper.setUserDataListener("testChallenge", "testUser1", listener);
    }

    @Test(expected = NullPointerException.class)
    public void setUserStatusListenerThrowsNullPointer() {
        firebaseHelper.setUserChallengeListener("testChallenge", "testUser1", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setUserStatusListenerThrowsIllegalArgument() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        firebaseHelper.setUserChallengeListener("", "testUser1", listener);
    }

    @Test(expected = NullPointerException.class)
    public void setUserDataListenerThrowsNullPointer() {
        firebaseHelper.setUserDataListener("testChallenge", "testUser1", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setUserDataListenerThrowsIllegalArgument() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        firebaseHelper.setUserDataListener("", "testUser1", listener);
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
