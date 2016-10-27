package ch.epfl.sweng.project.Firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.project.Model.Message;

/**
 * Helper class that provides methods to update and interact with the remote firebase database instance.
 * Offers methods to send and retrieve messages.
 */
public class FirebaseHelper {

    /**
     * Remote database instance
     */
    private DatabaseReference databaseReference = null;

    /**
     * Interface that allows to handle message fetching asynchronously from the server
     */
    public interface FirebaseHandler {
        void handleRetrievedMessages(List<Message> messages);
    }

    /**
     * Constructor that initializes the database instance
     */
    public FirebaseHelper() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Getter for the remote database reference
     *
     * @return      <code>DatabaseReference</code> for the remote database
     */
    public DatabaseReference getDatabase() {
        return databaseReference;
    }

    /**
     * Allows to send a message that will be stored on the server
     *
     * @param message
     */
    public void send(Message message) {
        Date time = message.getTime();
        String messageId = message.getUid();
        DatabaseReference messageChild = databaseReference.child("messages").child(message.getTo()).child(messageId);
        messageChild.child("from").setValue(message.getFrom());
        messageChild.child("type").setValue(message.getType());
        messageChild.child("message").setValue(message.getMessage());
        messageChild.child("time").setValue(time);
    }

    /**
     * Fetches all messages in the server for a specific user and let the handler function take care of them
     *
     * @param forUser
     * @param handler
     */
    public void fetchMessages(final String forUser, final FirebaseHandler handler) {
        databaseReference.child("messages").child(forUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Message> messages = new ArrayList<>();
                for (DataSnapshot children : dataSnapshot.getChildren()) {
                    String from = children.child("from").getValue(String.class);
                    Message.MessageType type = children.child("type").getValue(Message.MessageType.class);
                    String messageText = children.child("message").getValue(String.class);
                    Date time = children.child("time").getValue(Date.class);
                    Message message = new Message(from, forUser, type, messageText, time);

                    messages.add(message);
                }
                handler.handleRetrievedMessages(messages);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Deletes a given message from the server
     *
     * @param message
     */
    public void delete(Message message) {
        String messageId = message.getUid();
        databaseReference.child("messages").child(message.getTo()).child(messageId).removeValue();
    }
}
