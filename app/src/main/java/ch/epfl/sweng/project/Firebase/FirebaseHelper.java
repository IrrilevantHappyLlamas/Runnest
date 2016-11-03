package ch.epfl.sweng.project.Firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.project.AppRunnest;
import ch.epfl.sweng.project.Model.Message;

/**
 * Helper class that provides methods to update and interact with the remote firebase database instance.
 * Offers methods to send and retrieve messages.
 */
public class FirebaseHelper {

    /**
     * Remote database instance
     */
    private final DatabaseReference databaseReference;

    /**
     * Children's names in the database
     */
    private final String MESSAGES_CHILD = "messages";
    private final String FROM_CHILD = "from";
    private final String TYPE_CHILD = "type";
    private final String MESSAGE_CHILD = "message";
    private final String TIME_CHILD = "time";

    /**
     * Interface that allows to handle message fetching asynchronously from the server
     */
    public interface Handler {
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
        DatabaseReference messageChild = databaseReference.child(MESSAGES_CHILD).child(message.getTo()).child(messageId);
        messageChild.child(FROM_CHILD).setValue(message.getFrom());
        messageChild.child(TYPE_CHILD).setValue(message.getType());
        messageChild.child(MESSAGE_CHILD).setValue(message.getMessage());
        messageChild.child(TIME_CHILD).setValue(time);
    }

    /**
     * Fetches all messages in the server for a specific user and let the handler function take care of them
     *
     * @param forUser
     * @param handler
     */
    public void fetchMessages(final String forUser, final Handler handler) {
        databaseReference.child(MESSAGES_CHILD).child(forUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Message> messages = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot children : dataSnapshot.getChildren()) {
                        String from = children.child(FROM_CHILD).getValue(String.class);
                        Message.MessageType type = children.child(TYPE_CHILD).getValue(Message.MessageType.class);
                        String messageText = children.child(MESSAGE_CHILD).getValue(String.class);
                        Date time = children.child(TIME_CHILD).getValue(Date.class);
                        Message message = new Message(from, forUser, type, messageText, time);

                        messages.add(message);
                    }
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
        databaseReference.child(MESSAGES_CHILD).child(message.getTo()).child(messageId).removeValue();
    }

    /**
     * Add a new user to the firebase remote database. If the user already exists on the database, update his name.
     *
     * @param id    the id of the user to add, a <code>String</code>
     * @param name  name to associate to the user, a <code>String</code>
     * @throws IllegalArgumentException     if the arguments are <code>null</code> or empty
     */
    public void addOrUpdateUser(String id, String name) throws IllegalArgumentException {

        //Check validity of arguments
        if(id == null || name == null) {
            throw new IllegalArgumentException("Error: invalid argument," +
                    " id and name have to be non-null and not empty");
        }
        if(id.isEmpty() || name.isEmpty() || name.length() > 100) {
            throw new IllegalArgumentException("Error: invalid argument, id and name must be non empty and " +
                    "name length has to be under 100 characters");
        }

        databaseReference.child("users").child(id).child("name").setValue(name);
    }
}
