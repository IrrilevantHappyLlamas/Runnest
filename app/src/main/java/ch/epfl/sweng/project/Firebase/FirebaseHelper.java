package ch.epfl.sweng.project.Firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.project.Activities.ChallengeActivity;
import ch.epfl.sweng.project.Model.Challenge;
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
    private final String CHALLENGE_TYPE_CHILD = "challenge_type";
    private final String FIRST_VALUE_CHILD = "first_value";
    private final String SECOND_VALUE_CHILD = "second_value";
    private final String ADDRESSEE_CHILD = "addressee";
    private final String TYPE_CHILD = "type";
    private final String MESSAGE_CHILD = "message";
    private final String TIME_CHILD = "time";
    private final String USERS_CHILD = "users";
    private final String NAME_CHILD = "name";
    private final String STATISTICS_CHILD = "statistics";
    private final String TOTAL_RUNNING_TIME_CHILD = "total_running_time";
    private final String TOTAL_RUNNING_DISTANCE_CHILD = "total_running_distance";
    private final String TOTAL_NUMBER_OF_RUNS_CHILD = "total_number_of_runs";
    private final String TOTAL_NUMBER_OF_CHALLENGES_CHILD = "total_number_of_challenges";
    private final String TOTAL_NUMBER_OF_WON_CHALLENGES_CHILD = "total_number_of_won_challenges";
    private final String TOTAL_NUMBER_OF_LOST_CHALLENGES_CHILD = "total_number_of_lost_challenges";

    public final int TOTAL_RUNNING_TIME_INDEX = 0;
    public final int TOTAL_RUNNING_DISTANCE_INDEX = 1;
    public final int TOTAL_NUMBER_OF_RUNS_INDEX = 2;
    public final int TOTAL_NUMBER_OF_CHALLENGES_INDEX = 3;
    public final int TOTAL_NUMBER_OF_WON_CHALLENGES_INDEX = 4;
    public final int TOTAL_NUMBER_OF_LOST_CHALLENGES_INDEX = 5;

    public final int NUMBER_OF_STATISTICS = 6;

    //the two following array are useful for iterating on statistical data.
    private final String[] statisticsChildren = {
            TOTAL_RUNNING_TIME_CHILD,
            TOTAL_RUNNING_DISTANCE_CHILD,
            TOTAL_NUMBER_OF_RUNS_CHILD,
            TOTAL_NUMBER_OF_CHALLENGES_CHILD,
            TOTAL_NUMBER_OF_WON_CHALLENGES_CHILD,
            TOTAL_NUMBER_OF_LOST_CHALLENGES_CHILD
    };

    private final int[] statisticsIndexes = {
            TOTAL_RUNNING_TIME_INDEX,
            TOTAL_RUNNING_DISTANCE_INDEX,
            TOTAL_NUMBER_OF_RUNS_INDEX,
            TOTAL_NUMBER_OF_CHALLENGES_INDEX,
            TOTAL_NUMBER_OF_WON_CHALLENGES_INDEX,
            TOTAL_NUMBER_OF_LOST_CHALLENGES_INDEX
    };



    public enum RunType {
        SINGLE,
        CHALLENGE_WON,
        CHALLENGE_LOST
    }

    private final String CHALLENGES_CHILD = "challenges";

    public enum challengeNodeType {
        READY("readyStatus"),
        FINISH("finishStatus"),
        ABORT("abortStatus"),
        DATA("checkpoints");

        private final String nodeName;

        challengeNodeType(final String nodeName) {
            this.nodeName = nodeName;
        }

        @Override
        public String toString() {
            return nodeName;
        }
    }

    /**
     * Interface that allows to handle message fetching asynchronously from the server
     */
    public interface Handler {
        void handleRetrievedMessages(List<Message> messages);
    }

    /**
     * Interface that allows to handle statistical data fetching asynchronously from the server
     */
    public interface statisticsHandler{

        void handleRetrievedStatistics(String[] statistics);
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
     * @param message   message to be stored
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
        messageChild.child(CHALLENGE_TYPE_CHILD).setValue(message.getChallengeType());
        messageChild.child(FIRST_VALUE_CHILD).setValue(message.getFirstValue());
        messageChild.child(SECOND_VALUE_CHILD).setValue(message.getSecondValue());
        messageChild.child(TIME_CHILD).setValue(time);
    }


    /**
     * Fetches all messages in the server for a specific user and let the handler function take care of them
     *
     * @param forUser   user which messages to fetch
     * @param handler   handler for fetched messages
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
                        int firstValue = children.child(FIRST_VALUE_CHILD).getValue(Integer.class);
                        int secondValue = children.child(SECOND_VALUE_CHILD).getValue(Integer.class);
                        Challenge.Type challengeType = children.child(CHALLENGE_TYPE_CHILD).getValue(Challenge.Type.class);
                        Message message = new Message(from, forUser, sender, addressee, type, messageText, time, firstValue, secondValue, challengeType);

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
     * @param message   message to delete
     */
    public void delete(Message message) {
        String messageId = message.getUid();
        databaseReference.child(MESSAGES_CHILD).child(message.getTo()).child(messageId).removeValue();
    }

    /**
     * Add a new user to the firebase remote database. If the user already exists on the database, update his name.
     *
     * @param name   the name of the user.
     * @param email  the id associated to the user.
     * @throws IllegalArgumentException     if the arguments are <code>null</code> or empty
     */
    public void addOrUpdateUser(String name, String email) throws IllegalArgumentException {

        //Check validity of arguments
        if(name == null || email == null) {
            throw new IllegalArgumentException("Error: invalid argument," +
                    " name and email have to be non-null and not empty");
        }
        if(name.isEmpty() || email.isEmpty() || email.length() > 100) {
            throw new IllegalArgumentException("Error: invalid argument, name and email must be non empty and " +
                    "email length has to be under 100 characters");
        }

        final DatabaseReference user = databaseReference.child(USERS_CHILD).child(getFireBaseMail(email));
        user.child(NAME_CHILD).setValue(name);

        final DatabaseReference statistics = user.child(STATISTICS_CHILD);
        statistics.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    statistics.child(TOTAL_RUNNING_TIME_CHILD).setValue(0);
                    statistics.child(TOTAL_RUNNING_DISTANCE_CHILD).setValue(0);
                    statistics.child(TOTAL_NUMBER_OF_RUNS_CHILD).setValue(0);
                    statistics.child(TOTAL_NUMBER_OF_CHALLENGES_CHILD).setValue(0);
                    statistics.child(TOTAL_NUMBER_OF_WON_CHALLENGES_CHILD).setValue(0);
                    statistics.child(TOTAL_NUMBER_OF_LOST_CHALLENGES_CHILD).setValue(0);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateUserStatistics(String email, final long newTime, final float newDistance, final RunType runType) throws IllegalArgumentException {

        //Check validity of arguments
        if(email == null || runType == null) {
            throw new IllegalArgumentException("Error: invalid argument," +
                    " email and runType have to be non-null and not empty");
        }
        if(email.isEmpty() || email.length() > 100) {
            throw new IllegalArgumentException("Error: invalid argument, email must be non empty and " +
                    "email length has to be under 100 characters");
        }

        final DatabaseReference statistics = databaseReference.child(USERS_CHILD).child(getFireBaseMail(email)).child(STATISTICS_CHILD);

        statistics.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild(TOTAL_RUNNING_TIME_CHILD)) {
                        long time = dataSnapshot.child(TOTAL_RUNNING_TIME_CHILD).getValue(long.class);
                        long updatedTime = time + newTime;
                        statistics.child(TOTAL_RUNNING_TIME_CHILD).setValue(updatedTime);
                    } else {
                        statistics.child(TOTAL_RUNNING_TIME_CHILD).setValue(newTime);
                    }

                    if (dataSnapshot.hasChild(TOTAL_RUNNING_DISTANCE_CHILD)) {
                        float distance = dataSnapshot.child(TOTAL_RUNNING_DISTANCE_CHILD).getValue(float.class);
                        float updatedDistance = distance + newDistance;
                        statistics.child(TOTAL_RUNNING_DISTANCE_CHILD).setValue(updatedDistance);
                    } else {
                        statistics.child(TOTAL_RUNNING_DISTANCE_CHILD).setValue(newDistance);
                    }

                    switch (runType) {
                        case SINGLE:
                            if (dataSnapshot.hasChild(TOTAL_NUMBER_OF_RUNS_CHILD)) {
                                int numberRuns = dataSnapshot.child(TOTAL_NUMBER_OF_RUNS_CHILD).getValue(int.class);
                                int updatedNumberRuns = numberRuns + 1;
                                statistics.child(TOTAL_NUMBER_OF_RUNS_CHILD).setValue(updatedNumberRuns);
                            } else {
                                statistics.child(TOTAL_NUMBER_OF_RUNS_CHILD).setValue(1);
                            }
                            break;
                        case CHALLENGE_WON:
                            incrementChallenge(dataSnapshot);
                            if (dataSnapshot.hasChild(TOTAL_NUMBER_OF_WON_CHALLENGES_CHILD)) {
                                int numberWonChallenges = dataSnapshot.child(TOTAL_NUMBER_OF_WON_CHALLENGES_CHILD).getValue(int.class);
                                int updatedNumberWonChallenges = numberWonChallenges + 1;
                                statistics.child(TOTAL_NUMBER_OF_WON_CHALLENGES_CHILD).setValue(updatedNumberWonChallenges);
                            } else {
                                statistics.child(TOTAL_NUMBER_OF_WON_CHALLENGES_CHILD).setValue(1);
                            }
                            break;
                        case CHALLENGE_LOST:
                            incrementChallenge(dataSnapshot);
                            if (dataSnapshot.hasChild(TOTAL_NUMBER_OF_LOST_CHALLENGES_CHILD)) {
                                int numberLostChallenges = dataSnapshot.child(TOTAL_NUMBER_OF_LOST_CHALLENGES_CHILD).getValue(int.class);
                                int updatedNumberWonChallenges = numberLostChallenges + 1;
                                statistics.child(TOTAL_NUMBER_OF_LOST_CHALLENGES_CHILD).setValue(updatedNumberWonChallenges);
                            } else {
                                statistics.child(TOTAL_NUMBER_OF_LOST_CHALLENGES_CHILD).setValue(1);
                            }
                            break;
                    }
                }
            }

            private void incrementChallenge (DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(TOTAL_NUMBER_OF_CHALLENGES_CHILD)) {
                    int numberRuns = dataSnapshot.child(TOTAL_NUMBER_OF_CHALLENGES_CHILD).getValue(int.class);
                    int updatedNumberRuns = numberRuns + 1;
                    statistics.child(TOTAL_NUMBER_OF_CHALLENGES_CHILD).setValue(updatedNumberRuns);
                }
                else{
                    statistics.child(TOTAL_NUMBER_OF_CHALLENGES_CHILD).setValue(1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getUserStatistics(String email, final statisticsHandler handler){

        final String[] userStatistics = new String[6];

        //Check validity of arguments
        if(email == null) {
            throw new IllegalArgumentException("Error: invalid argument," +
                    " email has to be non-null and not empty");
        }
        if(email.isEmpty()) {
            throw new IllegalArgumentException("Error: invalid argument, email must be non empty and " +
                    "email length has to be under 100 characters");
        }

        final DatabaseReference statistics = databaseReference.child(USERS_CHILD).child(getFireBaseMail(email)).child(STATISTICS_CHILD);

        statistics.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for(int i = 0; i < NUMBER_OF_STATISTICS; ++i) {
                        if (dataSnapshot.hasChild(statisticsChildren[i])) {
                            userStatistics[statisticsIndexes[i]] = String.valueOf(dataSnapshot.child(statisticsChildren[i]).getValue());
                        } else {
                            userStatistics[statisticsIndexes[i]] = null;
                        }
                    }
                    handler.handleRetrievedStatistics(userStatistics);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
     * the desired name of the challenge.
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

        databaseReference.child(CHALLENGES_CHILD).child(challengeName).child(user1).child(challengeNodeType.READY.toString()).setValue(false);
        databaseReference.child(CHALLENGES_CHILD).child(challengeName).child(user1).child(challengeNodeType.FINISH.toString()).setValue(false);
        databaseReference.child(CHALLENGES_CHILD).child(challengeName).child(user1).child(challengeNodeType.ABORT.toString()).setValue(false);
        databaseReference.child(CHALLENGES_CHILD).child(challengeName).child(user2).child(challengeNodeType.READY.toString()).setValue(false);
        databaseReference.child(CHALLENGES_CHILD).child(challengeName).child(user2).child(challengeNodeType.FINISH.toString()).setValue(false);
        databaseReference.child(CHALLENGES_CHILD).child(challengeName).child(user2).child(challengeNodeType.ABORT.toString()).setValue(false);
    }

    /**
     * Deletes a given challenge node and all its children
     *
     * @param challengeName     name of the challenge to delete
     */
    public void deleteChallengeNode(String challengeName) {

        if (challengeName == null) {
            throw new NullPointerException("Challenge name was null");
        } else if (challengeName.isEmpty()) {
            throw new IllegalArgumentException("Challenge name can't be empty");
        }

        databaseReference.child(CHALLENGES_CHILD).child(challengeName).removeValue();
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
                .child(challengeNodeType.DATA.toString()).child(Integer.toString(seqNumber));

        Map<String, Object> checkPointUpdate = new HashMap<>();
        checkPointUpdate.put("/latitude", checkPoint.getLatitude());
        checkPointUpdate.put("/longitude", checkPoint.getLongitude());

        checkPointRef.updateChildren(checkPointUpdate);
    }

    /**
     * Sets the status of an user in a given challenge as true or false. The status node can be either
     * the READY or FINISH one. The DATA node can't be set to a value. Calling the method with that argument
     * will do nothing.
     *
     * @param challengeName     challenge in which the user is participating
     * @param user              user to set as "ready"
     * @param statusNode        status node type to set
     * @param status            status to set
     */
    public void setUserStatus(String challengeName, String user, challengeNodeType statusNode, boolean status) {

        if (user == null || challengeName == null) {
            throw new NullPointerException("Challenge node or user parameters can't be null");
        } else if (user.isEmpty() || challengeName.isEmpty()) {
            throw new IllegalArgumentException("Challenge node or user parameters can't be empty");
        }

        if (statusNode != challengeNodeType.DATA) {
            databaseReference.child(CHALLENGES_CHILD).child(challengeName).child(user).child(statusNode.toString()).setValue(status);
        }
    }

    /**
     * Sets a given listener on one of the challenge nodes of a user participating in a run. The node
     * could be the READY, FINISH or DATA one
     *
     * @param challengeName     challenge in which the user is participating
     * @param user              user whose status to observe
     * @param listener          listener to attach
     * @param challengeNode     challenge node to which to attach the listener
     */
    public void setUserChallengeListener(String challengeName, String user, ValueEventListener listener, challengeNodeType challengeNode) {

        if (user == null || challengeName == null || listener == null) {
            throw new NullPointerException("Challenge node, user or listener parameters can't be null");
        } else if (user.isEmpty() || challengeName.isEmpty()) {
            throw new IllegalArgumentException("Challenge node or user parameters can't be empty");
        }

        databaseReference.child(CHALLENGES_CHILD).child(challengeName).child(user)
                .child(challengeNode.toString()).addValueEventListener(listener);
    }

    /**
     * Removes a ValueEventListener from one of the challenge nodes of a user participating in a run.
     * The node could be the READY, FINISH or DATA one, the listener to remove has to be specified.
     *
     * @param challengeName     challenge from which to remove the listener
     * @param user              user on which the listener is currently attached
     * @param listener          listener to remove
     * @param nodeType          node to which the listener is attached
     */
    public void removeUserChallengeListener(String challengeName, String user, ValueEventListener listener, challengeNodeType nodeType) {

        if (user == null || challengeName == null || listener == null) {
            throw new NullPointerException("Challenge node, user parameters can't be null");
        } else if (user.isEmpty() || challengeName.isEmpty()) {
            throw new IllegalArgumentException("Challenge node or user parameters can't be empty");
        }

        databaseReference.child(CHALLENGES_CHILD).child(challengeName).child(user)
                .child(nodeType.toString()).removeEventListener(listener);
    }
}
