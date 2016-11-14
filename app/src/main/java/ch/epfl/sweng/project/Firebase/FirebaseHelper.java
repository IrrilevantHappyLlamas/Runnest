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
import ch.epfl.sweng.project.Model.CheckPoint;
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
    private final String SENDER_CHILD = "sender";
    private final String ADDRESSEE_CHILD = "addressee";
    private final String TYPE_CHILD = "type";
    private final String MESSAGE_CHILD = "message";
    private final String TIME_CHILD = "time";

    private final String CHALLENGES_CHILD = "challenges";
    private final String USER_STATUS = "status";
    private final String USER_CHECKPOINTS = "checkpoints";

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
        messageChild.child(SENDER_CHILD).setValue(message.getSender());
        messageChild.child(ADDRESSEE_CHILD).setValue(message.getAddressee());
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
                        String sender = children.child(SENDER_CHILD).getValue(String.class);
                        String addressee = children.child(ADDRESSEE_CHILD).getValue(String.class);
                        Message.MessageType type = children.child(TYPE_CHILD).getValue(Message.MessageType.class);
                        String messageText = children.child(MESSAGE_CHILD).getValue(String.class);
                        Date time = children.child(TIME_CHILD).getValue(Date.class);
                        Message message = new Message(from, forUser, sender, addressee, type, messageText, time);

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

    /**
     * Converts the email to allow the firebase storage
     *
     * @return email for firebase
     */
    public static String getFireBaseMail(String email) {
        String fireBaseMail = email.replace(".", "_dot_");
        fireBaseMail = fireBaseMail.replace("@", "_at_");
        return fireBaseMail;
    }

    /**
     * Creates a challenge under "challenges" node given the names of the opponents and
     * the desired name of the challenge
     *
     * @param user1             first challenger
     * @param user2             second challenger
     * @param challengeName     name of the challenge
     */
    public void addChallengeNode(String user1, String user2, String challengeName) {

        if (user1 == null || user2 == null || challengeName == null) {
            throw new NullPointerException("Challenge node parameters can't be null");
        } else if (user1.isEmpty() || user2.isEmpty() || challengeName.isEmpty()) {
            throw new IllegalArgumentException("Challenge node parameters can't be null");
        }

        databaseReference.child(CHALLENGES_CHILD).child(challengeName).child(user1).child(USER_STATUS).setValue(false);
        databaseReference.child(CHALLENGES_CHILD).child(challengeName).child(user2).child(USER_STATUS).setValue(false);
    }

    /**
     * Given the name of a challenge and one of its two users, adds one checkpoint its list, named after its
     * sequence number in the challenge
     *
     * @param checkPoint        checkpoint to add
     * @param challengeName     challenge to modify
     * @param user              the user of the challenge to which to add data
     * @param seqNumber         sequence number of the checkpoint in the current challenge
     */
    public void addChallengeCheckPoint(CheckPoint checkPoint, String challengeName, String user, int seqNumber) {

        if (user == null || challengeName == null || checkPoint == null) {
            throw new NullPointerException("Challenge node or data parameters can't be null");
        } else if (user.isEmpty() || challengeName.isEmpty()) {
            throw new IllegalArgumentException("Challenge node parameters can't be empty");
        }

        DatabaseReference checkPointRef = databaseReference.child(CHALLENGES_CHILD).child(challengeName).child(user)
                .child(USER_CHECKPOINTS).child(Integer.toString(seqNumber));
        checkPointRef.child("latitude").setValue(checkPoint.getLatitude());
        checkPointRef.child("longitude").setValue(checkPoint.getLongitude());
    }

    /**
     * Sets the status of an user in a given challenge as "ready"
     *
     * @param challengeName     challenge in which the user is participating
     * @param user              user to set as "ready"
     */
    public void setUserReady(String challengeName, String user) {

        if (user == null || challengeName == null) {
            throw new NullPointerException("Challenge node or user parameters can't be null");
        } else if (user.isEmpty() || challengeName.isEmpty()) {
            throw new IllegalArgumentException("Challenge node or user parameters can't be empty");
        }

        databaseReference.child(CHALLENGES_CHILD).child(challengeName).child(user).child(USER_STATUS).setValue(true);
    }

    /**
     * Sets a given listener on the status node of a user participating in a run
     *
     * @param challengeName     challenge in which the user is participating
     * @param user              user whose status to observe
     * @param listener          listener to attach
     */
    public void setUserStatusListener(String challengeName, String user, ValueEventListener listener) {

        if (user == null || challengeName == null || listener == null) {
            throw new NullPointerException("Challenge node, user or listener parameters can't be null");
        } else if (user.isEmpty() || challengeName.isEmpty()) {
            throw new IllegalArgumentException("Challenge node or user parameters can't be empty");
        }

        databaseReference.child(CHALLENGES_CHILD).child(challengeName).child(user)
                .child(USER_STATUS).addValueEventListener(listener);
    }

    /**
     * Sets a given listener on the data node of a user participating in a run
     *
     * @param challengeName     challenge in which the user is participating
     * @param user              user whose data to observe
     * @param listener          listener to attach
     */
    public void setUserDataListener(String challengeName, String user, ValueEventListener listener) {

        if (user == null || challengeName == null || listener == null) {
            throw new NullPointerException("Challenge node, user or listener parameters can't be null");
        } else if (user.isEmpty() || challengeName.isEmpty()) {
            throw new IllegalArgumentException("Challenge node or user parameters can't be empty");
        }

        databaseReference.child(CHALLENGES_CHILD).child(challengeName).child(user)
                .child(USER_CHECKPOINTS).addValueEventListener(listener);
    }


}
