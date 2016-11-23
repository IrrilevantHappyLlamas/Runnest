package ch.epfl.sweng.project.Firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.project.Model.ChallengeProxy;
import ch.epfl.sweng.project.Model.CheckPoint;

/**
 * Proxy class that links the challenge activity to firebase remote database. It receives data and inputs from the
 * challenge activity and fragments and translates them into inputs for the challenge instance on firebase, which the
 * proxy also instantiates at its creation.
 */
public class FirebaseProxy implements ChallengeProxy, ValueEventListener {

    private Handler callbackHandler = null;
    private FirebaseHelper firebaseHelper = null;

    private String challengeName = null;
    private boolean owner = false;

    private String localUser = null;
    private int localUserSeqNum = 0;

    private String remoteOpponent = null;
    private int remoteOpponentSeqNum = 0;
    private ValueEventListener onReadyListener = null;
    private ValueEventListener onFinishedListener = null;
    private boolean firstReadyCallback = true;
    private boolean firstFinishedCallback = true;
    private boolean remoteOpponentFinished = false;
    private boolean localUserFinished = false;


    /**
     * Public constructor that takes the two opponents names and instantiates the challenge on firebase. It also takes
     * as a parameter the <code>Handler</code> that will be used on callbacks events from the firebase remote database.
     *
     * @param localUser         the challenger from the local device
     * @param remoteOpponent    the remote challenger
     * @param handler           an handler from the proxy's client, which will handle callbacks
     */
    public FirebaseProxy(String localUser, String remoteOpponent, final Handler handler, boolean owner) {

        if (localUser == null || remoteOpponent == null || handler == null) {
            throw new NullPointerException("FirebaseProxy construction parameters can't be null");
        } else if (localUser.isEmpty() || remoteOpponent.isEmpty()) {
            throw new IllegalArgumentException("Challenge user in firebase proxy can't be empty");
        }

        // Instantiate proxy fields
        this.localUser = localUser;
        this.remoteOpponent = remoteOpponent;
        challengeName = generateChallengeName(localUser, remoteOpponent);
        callbackHandler = handler;
        this.owner = owner;
        firebaseHelper = new FirebaseHelper();

        // Create firebase challenge node if challenge owner
        if (owner) {
            firebaseHelper.addChallengeNode(localUser, remoteOpponent, challengeName);
        }

        setOpponentChallengeListeners();
    }

    @Override
    public void putData(CheckPoint checkPoint) {

        if (checkPoint == null) {
            throw new NullPointerException("CheckPoint is null");
        }

        firebaseHelper.addChallengeCheckPoint(checkPoint, challengeName, localUser, localUserSeqNum);
        localUserSeqNum++;
    }

    /**
     * This callback method is notified when the opponent's data is changed on the remote firebase database, for example
     * when a new <code>CheckPoint</code> is available. The method then calls the corresponding handler function and in
     * this way it provides the data to its client.
     *
     * @param dataSnapshot  snapshot of the modified/new data
     */
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            if (dataSnapshot.hasChild(Integer.toString(remoteOpponentSeqNum))) {
                DataSnapshot newCheckPointData = dataSnapshot.child(Integer.toString(remoteOpponentSeqNum));
                remoteOpponentSeqNum++;

                CheckPoint newCheckPoint = new CheckPoint(
                        ((double)newCheckPointData.child("latitude").getValue()),
                        ((double)newCheckPointData.child("longitude").getValue()));

                callbackHandler.OnNewDataHandler(newCheckPoint);
            } else {
                // TODO: handle sequence error
            }
        }
    }

    @Override
    public void startChallenge() {
        firebaseHelper.removeUserChallengeListener(challengeName, remoteOpponent, onReadyListener, FirebaseHelper.challengeNodeType.READY);
    }

    @Override
    public void imReady() {
        firebaseHelper.setUserStatus(challengeName, localUser, FirebaseHelper.challengeNodeType.READY, true);
    }

    @Override
    public void imFinished() {
        firebaseHelper.setUserStatus(challengeName, localUser, FirebaseHelper.challengeNodeType.FINISH, true);
        localUserFinished = true;

        if (localUserFinished && remoteOpponentFinished && owner) {
            deleteChallenge();
        }
    }

    private void deleteChallenge() {
        firebaseHelper.removeUserChallengeListener(challengeName, remoteOpponent, this, FirebaseHelper.challengeNodeType.DATA);
        firebaseHelper.removeUserChallengeListener(challengeName, remoteOpponent, onFinishedListener, FirebaseHelper.challengeNodeType.FINISH);
        firebaseHelper.deleteChallengeNode(challengeName);
    }

    private void setOpponentChallengeListeners() {
        onReadyListener = createReadyListener();
        onFinishedListener = createFinishedListener();

        firebaseHelper.setUserChallengeListener(challengeName, remoteOpponent, onReadyListener, FirebaseHelper.challengeNodeType.READY);
        firebaseHelper.setUserChallengeListener(challengeName, remoteOpponent, onFinishedListener, FirebaseHelper.challengeNodeType.FINISH);
        firebaseHelper.setUserChallengeListener(challengeName, remoteOpponent, this, FirebaseHelper.challengeNodeType.DATA);
    }

    private ValueEventListener createReadyListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //FIXME find a cleaner way to do that
                    if(firstReadyCallback) {
                        firstReadyCallback = false;
                    } else {
                        callbackHandler.isReadyHandler();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO: handle firebase error
            }
        };
    }

    private ValueEventListener createFinishedListener() {
        return new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if(firstFinishedCallback) {
                        firstFinishedCallback = false;
                    } else {
                        callbackHandler.isFinished();
                        remoteOpponentFinished = true;
                        if (localUserFinished && remoteOpponentFinished && owner) {
                            deleteChallenge();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private String generateChallengeName(String user1, String user2) {

        // Argument check
        if (user1 == null || user2 == null) {
            throw new NullPointerException("User names for challenge can't be null");
        } else if (user1.isEmpty() || user2.isEmpty()) {
            throw new IllegalArgumentException("User names for challenge can't be empty");
        }

        int namesCompare = user1.compareTo(user2);
        String challengeName;

        if (namesCompare <= 0) {
            challengeName = user1 + "_vs_" + user2;
        } else {
            challengeName = user2 + "_vs_" + user1;
        }

        return challengeName;
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        // TODO: handle cancelled firebase operation
    }
}
