package ch.epfl.sweng.project.Firebase;

import com.google.android.gms.gcm.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.project.Model.Challenge;
import ch.epfl.sweng.project.Model.CheckPoint;
import ch.epfl.sweng.project.Model.Message;

/**
 * Helper class that provides methods to update and interact with the remote firebase database instance.
 * Offers methods to sendMessage and retrieve messages.
 */
public class FirebaseHelper {

    private final DatabaseReference databaseReference;

    public final int TOTAL_RUNNING_TIME_INDEX = 0;
    public final int TOTAL_RUNNING_DISTANCE_INDEX = 1;
    public final int TOTAL_NUMBER_OF_RUNS_INDEX = 2;
    public final int TOTAL_NUMBER_OF_CHALLENGES_INDEX = 3;
    public final int TOTAL_NUMBER_OF_WON_CHALLENGES_INDEX = 4;
    public final int TOTAL_NUMBER_OF_LOST_CHALLENGES_INDEX = 5;

    public final int NUMBER_OF_STATISTICS = 6;

    //the two following array are useful for iterating on statistical data.
    private final String[] statisticsChildren = {
            FirebaseNodes.TOTAL_RUNNING_TIME,
            FirebaseNodes.TOTAL_RUNNING_DISTANCE,
            FirebaseNodes.TOTAL_NUMBER_OF_RUNS,
            FirebaseNodes.TOTAL_NUMBER_OF_CHALLENGES,
            FirebaseNodes.TOTAL_NUMBER_OF_WON_CHALLENGES,
            FirebaseNodes.TOTAL_NUMBER_OF_LOST_CHALLENGES
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

    public enum challengeNodeType {
        READY("readyStatus"),
        FINISH("finishStatus"),
        ABORT("abortStatus"),
        IN_ROOM("in room"),
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
     * Constructor that initializes the database instance reference.
     */
    public FirebaseHelper() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Getter for the remote database reference.
     *
     * @return      DatabaseReference for the remote database.
     */
    public DatabaseReference getDatabase() {
        return databaseReference;
    }

    /**
     * Allows to sendMessage a message that will be stored on the server.
     *
     * @param message   Message to be stored.
     */
    public void sendMessage(Message message) {

        if (message == null) {
            throw new IllegalArgumentException("Cannot upload a null message to firebase");
        }

        Date time = message.getTime();
        String messageId = message.getUid();
        DatabaseReference messageRef = databaseReference.child(FirebaseNodes.MESSAGES).child(message.getTo()).child(messageId);

        Map<String, Object> messageUpload = new HashMap<>();
        messageUpload.put("/" + FirebaseNodes.MEX_FROM, message.getFrom());
        messageUpload.put("/" + FirebaseNodes.MEX_SENDER, message.getSender());
        messageUpload.put("/" + FirebaseNodes.MEX_ADDRESSEE, message.getAddressee());
        messageUpload.put("/" + FirebaseNodes.MEX, message.getMessage());
        messageUpload.put("/" + FirebaseNodes.MEX_CHALLENGE_TYPE, message.getChallengeType());
        messageUpload.put("/" + FirebaseNodes.MEX_TYPE, message.getType());
        messageUpload.put("/" + FirebaseNodes.MEX_FIRST_VALUE, message.getFirstValue());
        messageUpload.put("/" + FirebaseNodes.MEX_SECOND_VALUE, message.getSecondValue());
        messageUpload.put("/" + FirebaseNodes.MEX_TIME, time);

        //TODO : add listener
        messageRef.updateChildren(messageUpload);
    }

    /**
     * Deletes the messages node of a given user, erasing all his messages.
     *
     * @param user      The user for which to deleteMessage messages.
     */
    public void deleteUserMessages(String user) {

        if (user == null) {
            throw new IllegalArgumentException("Cannot deleteMessage messages for a null user");
        } else if (user.isEmpty()) {
            throw new IllegalArgumentException("Cannot deleteMessage messages for an empty user");
        }

        //TODO : add listener
        databaseReference.child(FirebaseNodes.MESSAGES).child(user).removeValue();
    }

    /**
     * Fetches all messages in the server for a specific user and let the handler function take care of them.
     *
     * @param forUser   User which messages to fetch.
     * @param handler   Handler for fetched messages.
     */
    public void fetchMessages(final String forUser, final Handler handler) {

        if (forUser == null || handler == null) {
            throw new IllegalArgumentException("User or handler parameters for fetchMessages can't be null");
        } else if (forUser.isEmpty()) {
            throw new IllegalArgumentException("User parameter for fetchMessages can't be empty");
        }

        databaseReference.child(FirebaseNodes.MESSAGES).child(forUser)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Message> messages = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot children : dataSnapshot.getChildren()) {
                        // For each message, fetch fields
                        String from = children.child(FirebaseNodes.MEX_FROM).getValue(String.class);
                        String sender = children.child(FirebaseNodes.MEX_SENDER).getValue(String.class);
                        String addressee = children.child(FirebaseNodes.MEX_ADDRESSEE).getValue(String.class);
                        Message.Type type = children.child(FirebaseNodes.MEX_TYPE).getValue(Message.Type.class);
                        String messageText = children.child(FirebaseNodes.MEX).getValue(String.class);
                        Date time = children.child(FirebaseNodes.MEX_TIME).getValue(Date.class);
                        Challenge.Type challengeType = children.child(FirebaseNodes.MEX_CHALLENGE_TYPE).getValue(Challenge.Type.class);

                        int firstValue = 0;
                        if(children.child(FirebaseNodes.MEX_FIRST_VALUE).getValue() != null){
                            firstValue = children.child(FirebaseNodes.MEX_FIRST_VALUE).getValue(Integer.class);
                        }

                        int secondValue = 0;
                        if(children.child(FirebaseNodes.MEX_SECOND_VALUE).getValue() != null) {
                            secondValue = children.child(FirebaseNodes.MEX_SECOND_VALUE).getValue(Integer.class);
                        }

                        Message message = new Message(from, forUser, sender, addressee, type, messageText, time, firstValue, secondValue, challengeType);
                        messages.add(message);
                    }
                }
                handler.handleRetrievedMessages(messages);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    // TODO: sleep in corresponding test
    /**
     * Deletes a given message from the server.
     *
     * @param message   Message to delete.
     */
    public void deleteMessage(Message message) {

        if (message == null) {
            throw new IllegalArgumentException("Message to deleteMessage can't be null");
        }

        String messageId = message.getUid();
        // TODO: add listener
        databaseReference.child(FirebaseNodes.MESSAGES).child(message.getTo()).child(messageId).removeValue();
    }

    /**
     * Add a new user to the firebase remote database. If the user already exists on the database, update his name.
     *
     * @param name   The name of the user.
     * @param email  The id associated to the user.
     * @throws IllegalArgumentException     if the arguments are <code>null</code> or empty
     */
    public void addOrUpdateUser(String name, String email) {

        //Check validity of arguments
        if(name == null || email == null) {
            throw new IllegalArgumentException("Name and email for user have to be non-null");
        }
        if(name.isEmpty() || email.isEmpty() || email.length() > 100) {
            throw new IllegalArgumentException("Name and email for user must be non empty and " +
                    "email length has to be under 100 characters.");
        }

        // TODO: add listener
        final DatabaseReference user = databaseReference.child(FirebaseNodes.USERS).child(getFireBaseMail(email));
        user.child(FirebaseNodes.NAME).setValue(name);

        final DatabaseReference statistics = user.child(FirebaseNodes.STATISTICS);
        statistics.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Map<String, Object> statisticsUpload = new HashMap<>();
                    statisticsUpload.put("/" + FirebaseNodes.TOTAL_RUNNING_TIME, 0);
                    statisticsUpload.put("/" + FirebaseNodes.TOTAL_RUNNING_DISTANCE, 0);
                    statisticsUpload.put("/" + FirebaseNodes.TOTAL_NUMBER_OF_RUNS, 0);
                    statisticsUpload.put("/" + FirebaseNodes.TOTAL_NUMBER_OF_CHALLENGES, 0);
                    statisticsUpload.put("/" + FirebaseNodes.TOTAL_NUMBER_OF_WON_CHALLENGES, 0);
                    statisticsUpload.put("/" + FirebaseNodes.TOTAL_NUMBER_OF_LOST_CHALLENGES, 0);

                    //TODO : add listener
                    statistics.updateChildren(statisticsUpload);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    /**
     * Allows to set (or update if already present) the url of your profile picture.
     *
     * @param userEmail     The email of the user whom to set the url.
     * @param url           URL to set.
     */
    public void setOrUpdateProfilePicUrl(String userEmail, final String url) {

        if (userEmail == null || userEmail.isEmpty() || url == null || url.isEmpty()) {
            throw new IllegalArgumentException("User and image url must be non null and non empty");
        }

        final DatabaseReference user = databaseReference.child(FirebaseNodes.USERS).child(getFireBaseMail(userEmail));
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    user.child(FirebaseNodes.PROFILE_PIC_URL).setValue(url);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    /**
     * Allows to get the url of other users profile picture.
     *
     * @param userEmail     Email of the user whom to get the profile picture url.
     * @param listener      Handles the result, the dataSnapshot contains the node with the url.
     */
    public void getProfilePicUrl(String userEmail, ValueEventListener listener) {

        if (userEmail == null || userEmail.equals("") || listener == null) {
            throw new IllegalArgumentException();
        }

        databaseReference.child(FirebaseNodes.USERS)
                .child(getFireBaseMail(userEmail))
                .child(FirebaseNodes.PROFILE_PIC_URL)
                .addListenerForSingleValueEvent(listener);
    }

    // TODO: listener ovunque....?
    /**
     * Updates a user's statistics after a run or challenge.
     *
     * @param email         Email of the user which statistics we want to update.
     * @param newTime       New total run time of the user.
     * @param newDistance   New total run distance of the user.
     * @param runType       Run type, challenge or simple run.
     */
    public void updateUserStatistics(String email, final long newTime, final float newDistance, final RunType runType) {

        //Check validity of arguments
        if(email == null || runType == null) {
            throw new IllegalArgumentException("Error: invalid argument," +
                    " email and runType have to be non-null and not empty");
        }
        if(email.isEmpty() || email.length() > 100) {
            throw new IllegalArgumentException("Error: invalid argument, email must be non empty and " +
                    "email length has to be under 100 characters");
        }

        final DatabaseReference statistics = databaseReference.child(FirebaseNodes.USERS)
                .child(getFireBaseMail(email)).child(FirebaseNodes.STATISTICS);

        statistics.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild(FirebaseNodes.TOTAL_RUNNING_TIME)) {
                        long time = dataSnapshot.child(FirebaseNodes.TOTAL_RUNNING_TIME).getValue(long.class);
                        long updatedTime = time + newTime;
                        statistics.child(FirebaseNodes.TOTAL_RUNNING_TIME).setValue(updatedTime);
                    } else {
                        statistics.child(FirebaseNodes.TOTAL_RUNNING_TIME).setValue(newTime);
                    }

                    if (dataSnapshot.hasChild(FirebaseNodes.TOTAL_RUNNING_DISTANCE)) {
                        float distance = dataSnapshot.child(FirebaseNodes.TOTAL_RUNNING_DISTANCE).getValue(float.class);
                        float updatedDistance = distance + newDistance;
                        statistics.child(FirebaseNodes.TOTAL_RUNNING_DISTANCE).setValue(updatedDistance);
                    } else {
                        statistics.child(FirebaseNodes.TOTAL_RUNNING_DISTANCE).setValue(newDistance);
                    }

                    switch (runType) {
                        case SINGLE:
                            if (dataSnapshot.hasChild(FirebaseNodes.TOTAL_NUMBER_OF_RUNS)) {
                                int numberRuns = dataSnapshot.child(FirebaseNodes.TOTAL_NUMBER_OF_RUNS).getValue(int.class);
                                int updatedNumberRuns = numberRuns + 1;
                                statistics.child(FirebaseNodes.TOTAL_NUMBER_OF_RUNS).setValue(updatedNumberRuns);
                            } else {
                                statistics.child(FirebaseNodes.TOTAL_NUMBER_OF_RUNS).setValue(1);
                            }
                            break;
                        case CHALLENGE_WON:
                            incrementChallenge(dataSnapshot);
                            if (dataSnapshot.hasChild(FirebaseNodes.TOTAL_NUMBER_OF_WON_CHALLENGES)) {
                                int numberWonChallenges = dataSnapshot.child(FirebaseNodes.TOTAL_NUMBER_OF_WON_CHALLENGES).getValue(int.class);
                                int updatedNumberWonChallenges = numberWonChallenges + 1;
                                statistics.child(FirebaseNodes.TOTAL_NUMBER_OF_WON_CHALLENGES).setValue(updatedNumberWonChallenges);
                            } else {
                                statistics.child(FirebaseNodes.TOTAL_NUMBER_OF_WON_CHALLENGES).setValue(1);
                            }
                            break;
                        case CHALLENGE_LOST:
                            incrementChallenge(dataSnapshot);
                            if (dataSnapshot.hasChild(FirebaseNodes.TOTAL_NUMBER_OF_LOST_CHALLENGES)) {
                                int numberLostChallenges = dataSnapshot.child(FirebaseNodes.TOTAL_NUMBER_OF_LOST_CHALLENGES).getValue(int.class);
                                int updatedNumberWonChallenges = numberLostChallenges + 1;
                                statistics.child(FirebaseNodes.TOTAL_NUMBER_OF_LOST_CHALLENGES).setValue(updatedNumberWonChallenges);
                            } else {
                                statistics.child(FirebaseNodes.TOTAL_NUMBER_OF_LOST_CHALLENGES).setValue(1);
                            }
                            break;
                    }
                }
            }

            private void incrementChallenge (DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(FirebaseNodes.TOTAL_NUMBER_OF_CHALLENGES)) {
                    int numberRuns = dataSnapshot.child(FirebaseNodes.TOTAL_NUMBER_OF_CHALLENGES).getValue(int.class);
                    int updatedNumberRuns = numberRuns + 1;
                    statistics.child(FirebaseNodes.TOTAL_NUMBER_OF_CHALLENGES).setValue(updatedNumberRuns);
                }
                else{
                    statistics.child(FirebaseNodes.TOTAL_NUMBER_OF_CHALLENGES).setValue(1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    /**
     * Getter for the statistics of a user. Give them to the given handler to allow their usage in the rest of the
     * application.
     *
     * @param email         Email of the user from which to get statistics.
     * @param handler       Handler who will handle the statistics.
     */
    public void getUserStatistics(String email, final statisticsHandler handler){

        final String[] userStatistics = new String[6];

        //Check validity of arguments
        if(email == null) {
            throw new IllegalArgumentException("Email has to be non-null");
        }
        if(email.isEmpty()) {
            throw new IllegalArgumentException("Email must be non empty and " +
                    "email length has to be under 100 characters");
        }

        final DatabaseReference statistics = databaseReference.child(FirebaseNodes.USERS).
                child(getFireBaseMail(email)).child(FirebaseNodes.STATISTICS);

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
                databaseError.toException();
            }
        });
    }

    /**
     * Sets the available status of a user to true or false. An user with available status set to false cannot be
     * be challenged.
     *
     * @param userMail      Mail of the user of which to set the availability status.
     * @param status        Value of the status.
     */
    public void setUserAvailable(String userMail, boolean emailInFirebaseFormat, boolean status) {

        if (userMail == null) {
            throw new IllegalArgumentException("User mail can't be null");
        } else if (userMail.isEmpty()) {
            throw new IllegalArgumentException("User mail can't be empty");
        }

        String email = emailInFirebaseFormat?userMail:getFireBaseMail(userMail);

        databaseReference.child(FirebaseNodes.USERS)
                .child(email).child(FirebaseNodes.AVAILABLE).setValue(status);
    }

    /**
     * Attach a single ValueEventListener to the available status node of an user, to check for it. The listener is
     * passed as a parameter and is responsible for handling the node value.
     *
     * @param userMail      Mail of the user of which to listen the availability status.
     * @param listener      Listener to attach.
     */
    public void listenUserAvailability(String userMail, boolean emailInFirebaseFormat, ValueEventListener listener) {

        if (userMail == null || listener == null) {
            throw new IllegalArgumentException("User mail or the listener can't be null");
        } else if (userMail.isEmpty()) {
            throw new IllegalArgumentException("User mail can't be empty");
        }

        String email = emailInFirebaseFormat?userMail:getFireBaseMail(userMail);

        databaseReference.child(FirebaseNodes.USERS)
                .child(email).child(FirebaseNodes.AVAILABLE)
                .addListenerForSingleValueEvent(listener);
    }

    /**
     * Converts the email to allow the firebase storage.
     *
     * @return      Email in firebase format
     */
    public static String getFireBaseMail(String email) {

        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Cannot convert a null or empty email");
        }

        String fireBaseMail = email.replace(".", "_dot_");
        fireBaseMail = fireBaseMail.replace("@", "_at_");
        return fireBaseMail;
    }

    /**
     * Creates a challenge under "challenges" node given the names of the opponents and
     * the desired name of the challenge.
     *
     * @param user1             First challenger.
     * @param user2             Second challenger.
     * @param challengeName     Name of the challenge.
     */
    public void addChallengeNode(String user1, String user2, String challengeName) {

        if (user1 == null || user2 == null || challengeName == null) {
            throw new IllegalArgumentException("Challenge node parameters can't be null");
        } else if (user1.isEmpty() || user2.isEmpty() || challengeName.isEmpty()) {
            throw new IllegalArgumentException("Challenge node parameters can't be empty");
        }

        // TODO: attach listener (and make map update)
        databaseReference.child(FirebaseNodes.CHALLENGES).child(challengeName).child(user1).child(challengeNodeType.READY.toString()).setValue(false);
        databaseReference.child(FirebaseNodes.CHALLENGES).child(challengeName).child(user1).child(challengeNodeType.FINISH.toString()).setValue(false);
        databaseReference.child(FirebaseNodes.CHALLENGES).child(challengeName).child(user1).child(challengeNodeType.ABORT.toString()).setValue(false);
        databaseReference.child(FirebaseNodes.CHALLENGES).child(challengeName).child(user1).child(challengeNodeType.IN_ROOM.toString()).setValue(false);
        databaseReference.child(FirebaseNodes.CHALLENGES).child(challengeName).child(user2).child(challengeNodeType.READY.toString()).setValue(false);
        databaseReference.child(FirebaseNodes.CHALLENGES).child(challengeName).child(user2).child(challengeNodeType.FINISH.toString()).setValue(false);
        databaseReference.child(FirebaseNodes.CHALLENGES).child(challengeName).child(user2).child(challengeNodeType.ABORT.toString()).setValue(false);
        databaseReference.child(FirebaseNodes.CHALLENGES).child(challengeName).child(user2).child(challengeNodeType.IN_ROOM.toString()).setValue(false);
    }

    /**
     * Deletes a given challenge node and all its children.
     *
     * @param challengeName     Name of the challenge to delete.
     */
    public void deleteChallengeNode(String challengeName) {

        if (challengeName == null) {
            throw new IllegalArgumentException("Challenge name was null");
        } else if (challengeName.isEmpty()) {
            throw new IllegalArgumentException("Challenge name can't be empty");
        }

        // TODO: attach listener
        databaseReference.child(FirebaseNodes.CHALLENGES).child(challengeName).removeValue();
    }

    /**
     * Given the name of a challenge and one of its two users, adds one checkpoint its list, named after its
     * sequence number in the challenge.
     *
     * @param checkPoint        Checkpoint to add.
     * @param challengeName     Challenge to modify.
     * @param user              The user of the challenge to which to add data.
     * @param seqNumber         Sequence number of the checkpoint in the current challenge.
     */
    public void addChallengeCheckPoint(CheckPoint checkPoint, String challengeName, String user, int seqNumber) {

        if (user == null || challengeName == null || checkPoint == null) {
            throw new IllegalArgumentException("Challenge node or data parameters can't be null");
        } else if (user.isEmpty() || challengeName.isEmpty()) {
            throw new IllegalArgumentException("Challenge node parameters can't be empty");
        }

        DatabaseReference checkPointRef = databaseReference.child(FirebaseNodes.CHALLENGES).child(challengeName).child(user)
                .child(challengeNodeType.DATA.toString()).child(Integer.toString(seqNumber));

        Map<String, Object> checkPointUpdate = new HashMap<>();
        checkPointUpdate.put("/" + FirebaseNodes.LATITUDE, checkPoint.getLatitude());
        checkPointUpdate.put("/" + FirebaseNodes.LONGITUDE, checkPoint.getLongitude());

        checkPointRef.updateChildren(checkPointUpdate);
    }

    /**
     * Sets the status of an user in a given challenge as true or false. The status node can be either
     * the READY, FINISH or ABORT one. The DATA node can't be set to a value. Calling the method with that argument
     * will do nothing.
     *
     * @param challengeName     Challenge in which the user is participating.
     * @param user              User to set.
     * @param statusNode        Status node type to set.
     * @param status            Status to set.
     */
    public void setUserStatus(String challengeName, String user, challengeNodeType statusNode, boolean status) {

        if (user == null || challengeName == null) {
            throw new IllegalArgumentException("Challenge node or user parameters can't be null");
        } else if (user.isEmpty() || challengeName.isEmpty()) {
            throw new IllegalArgumentException("Challenge node or user parameters can't be empty");
        }

        if (statusNode != challengeNodeType.DATA) {
            databaseReference.child(FirebaseNodes.CHALLENGES).child(challengeName).child(user).child(statusNode.toString()).setValue(status);
        }
    }

    /**
     * Sets a given listener on one of the challenge nodes of a user participating in a run. The node
     * could be the READY, FINISH, ABORT or DATA one.
     *
     * @param challengeName     Challenge in which the user is participating.
     * @param user              User whose status to observe.
     * @param listener          Listener to attach.
     * @param challengeNode     Challenge node to which to attach the listener.
     */
    public void setUserChallengeListener(String challengeName, String user, ValueEventListener listener, challengeNodeType challengeNode) {

        if (user == null || challengeName == null || listener == null) {
            throw new IllegalArgumentException("Challenge node, user or listener parameters can't be null");
        } else if (user.isEmpty() || challengeName.isEmpty()) {
            throw new IllegalArgumentException("Challenge node or user parameters can't be empty");
        }

        databaseReference.child(FirebaseNodes.CHALLENGES).child(challengeName).child(user)
                .child(challengeNode.toString()).addValueEventListener(listener);
    }

    /**
     * Removes a ValueEventListener from one of the challenge nodes of a user participating in a run.
     * The node could be the READY, FINISH, ABORT or DATA one, the listener to remove has to be specified.
     *
     * @param challengeName     Challenge from which to remove the listener.
     * @param user              User on which the listener is currently attached.
     * @param listener          Listener to remove.
     * @param nodeType          Node to which the listener is attached.
     */
    public void removeUserChallengeListener(String challengeName, String user, ValueEventListener listener, challengeNodeType nodeType) {

        if (user == null || challengeName == null || listener == null) {
            throw new IllegalArgumentException("Challenge node, user parameters can't be null");
        } else if (user.isEmpty() || challengeName.isEmpty()) {
            throw new IllegalArgumentException("Challenge node or user parameters can't be empty");
        }

        databaseReference.child(FirebaseNodes.CHALLENGES).child(challengeName).child(user)
                .child(nodeType.toString()).removeEventListener(listener);
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
}
