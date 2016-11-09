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
