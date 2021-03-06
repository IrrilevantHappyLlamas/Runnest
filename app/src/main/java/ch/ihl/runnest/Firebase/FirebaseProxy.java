package ch.ihl.runnest.Firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ch.ihl.runnest.Model.ChallengeProxy;
import ch.ihl.runnest.Model.CheckPoint;

/**
 * Proxy class that links the challenge activity to firebase remote database. It receives data and inputs from the
 * challenge activity and fragments and translates them into inputs for the challenge instance on firebase, which the
 * proxy also instantiates at its creation.
 */
public class FirebaseProxy implements ChallengeProxy {

    private Handler callbackHandler = null;
    private FirebaseHelper firebaseHelper = null;

    // Challenge participants and settings
    private String challengeName = null;
    private boolean owner = false;
    private String localUser = null;
    private int localUserSeqNum = 0;
    private String remoteOpponent = null;
    private int remoteOpponentSeqNum = 0;

    // Challenge control booleans
    private boolean firstReadyCallback = true;
    private boolean firstFinishedCallback = true;
    private boolean firstAbortCallback = true;
    private boolean firstInRoomCallback = true;
    private boolean remoteOpponentFinished = false;
    private boolean localUserFinished = false;
    private boolean isChallengeTerminated = false;

    // Firebase node listeners
    private ValueEventListener onReadyListener = null;
    private ValueEventListener onFinishedListener = null;
    private ValueEventListener onAbortListener = null;
    private ValueEventListener onDataListener = null;
    private ValueEventListener inRoomListener = null;


    /**
     * Public constructor that takes the two opponents names and instantiates the challenge on firebase. It also takes
     * as a parameter the Handler that will be used on callbacks events from the firebase remote database.
     * The last parameter is a boolean that indicates whether the creator of this proxy is the "owner" of the challenge
     * and must instantiate the challenge node on firebase.
     *
     * @param localUser         The challenger from the local device.
     * @param remoteOpponent    The remote challenger.
     * @param handler           An handler from the proxy's client, which will handle callbacks.
     * @param owner             Indicates if the local user is the owner of the challenge.
     */
    public FirebaseProxy(String localUser, String remoteOpponent, final Handler handler, boolean owner, String identifier) {

        if (localUser == null || remoteOpponent == null || identifier == null || handler == null) {
            throw new IllegalArgumentException("FirebaseProxy construction parameters can't be null");
        } else if (localUser.isEmpty() || remoteOpponent.isEmpty()) {
            throw new IllegalArgumentException("Challenge user in firebase proxy can't be empty");
        }

        // Instantiate proxy fields
        this.localUser = localUser;
        this.remoteOpponent = remoteOpponent;
        this.owner = owner;
        challengeName = generateChallengeName(localUser, remoteOpponent, identifier);
        callbackHandler = handler;
        firebaseHelper = new FirebaseHelper();

        // Create firebase challenge node if challenge owner
        if (owner) {
            firebaseHelper.addChallengeNode(localUser, remoteOpponent, challengeName);
            firebaseHelper.setUserStatus(challengeName, localUser, FirebaseNodes.ChallengeStatus.IN_ROOM, true);
            setOpponentListeners();
        } else {
            firebaseHelper.setUserStatus(challengeName, localUser, FirebaseNodes.ChallengeStatus.IN_ROOM, true);
            setOpponentListeners();
            checkForPreviousState(FirebaseNodes.ChallengeStatus.ABORT);
            checkForPreviousState(FirebaseNodes.ChallengeStatus.READY);
        }
    }

    /**
     * Utility method to generate the challenge name in the form "userA vs userB id", where "userA" is lexicographically
     * less than "userB"
     *
     * @param user1         First user of the challenge.
     * @param user2         Second user of the challenge.
     * @param identifier    Challenge identifier.
     * @return              Challenge name following the convention.
     */
    public static String generateChallengeName(String user1, String user2, String identifier) {

        if (user1 == null || user2 == null || identifier == null) {
            throw new IllegalArgumentException("Challenge name parameters can't be null");
        } else if (user1.isEmpty() || user2.isEmpty()) {
            throw new IllegalArgumentException("Users in challenge name can't be empty");
        }

        String challengeName;
        int namesCompare = user1.compareTo(user2);
        challengeName = (namesCompare <= 0)?user1 + " vs " + user2:user2 + " vs " + user1;
        challengeName += " " + identifier;

        return challengeName;
    }

    /**
     * Deletes the challenge for firebase, to be used only when you are sure to be the last person in the room.
     */
    public void deleteChallenge() {
        firebaseHelper.deleteChallengeNode(challengeName);
    }

    private void setOpponentListeners() {
        onReadyListener = createReadyListener();
        onFinishedListener = createFinishedListener();
        onAbortListener = createAbortListener();
        onDataListener = createDataListener();

        firebaseHelper.setUserChallengeListener(challengeName, remoteOpponent, onReadyListener, FirebaseNodes.ChallengeStatus.READY);
        firebaseHelper.setUserChallengeListener(challengeName, remoteOpponent, onFinishedListener, FirebaseNodes.ChallengeStatus.FINISH);
        firebaseHelper.setUserChallengeListener(challengeName, remoteOpponent, onAbortListener, FirebaseNodes.ChallengeStatus.ABORT);
        firebaseHelper.setUserChallengeListener(challengeName, remoteOpponent, onDataListener, FirebaseNodes.ChallengeStatus.DATA);

        // If owner, create listener to see when opponent enters room
        if (owner) {
            inRoomListener = createInRoomListener();
            firebaseHelper.setUserChallengeListener(challengeName, remoteOpponent, inRoomListener, FirebaseNodes.ChallengeStatus.IN_ROOM);
        }
    }

    private void removeActiveListeners() {
        if (onReadyListener != null) {
            firebaseHelper.removeUserChallengeListener(challengeName, localUser, onReadyListener, FirebaseNodes.ChallengeStatus.READY);
            onReadyListener = null;
        }

        if (onDataListener != null) {
            firebaseHelper.removeUserChallengeListener(challengeName, localUser, onDataListener, FirebaseNodes.ChallengeStatus.DATA);
            onDataListener = null;
        }

        if (onFinishedListener != null) {
            firebaseHelper.removeUserChallengeListener(challengeName, localUser, onFinishedListener, FirebaseNodes.ChallengeStatus.FINISH);
            onFinishedListener = null;
        }

        if (onAbortListener != null) {
            firebaseHelper.removeUserChallengeListener(challengeName, localUser, onAbortListener, FirebaseNodes.ChallengeStatus.ABORT);
            onAbortListener = null;
        }
    }

    private void checkForPreviousState(final FirebaseNodes.ChallengeStatus statusType) {

        firebaseHelper.getDatabase().child(FirebaseNodes.CHALLENGES).child(challengeName).child(remoteOpponent)
                .child(statusType.toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && (boolean)dataSnapshot.getValue()) {
                    switch(statusType) {
                        case READY: callbackHandler.isReady();
                                    break;
                        case ABORT: removeActiveListeners();
                                    isChallengeTerminated = true;
                                    firebaseHelper.deleteChallengeNode(challengeName);
                                    callbackHandler.hasLeft();
                                    break;
                        default: throw new IllegalArgumentException("Cannot check that status node before challenge has started");
                    }
                    callbackHandler.isReady();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    private ValueEventListener createReadyListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if(firstReadyCallback) {
                        firstReadyCallback = false;
                    } else {
                        callbackHandler.isReady();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
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
                        if (localUserFinished) {
                            removeActiveListeners();
                            firebaseHelper.deleteChallengeNode(challengeName);
                            isChallengeTerminated = true;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        };
    }

    private ValueEventListener createAbortListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if(firstAbortCallback) {
                        firstAbortCallback = false;
                    } else {
                        removeActiveListeners();
                        isChallengeTerminated = true;
                        deleteChallenge();
                        callbackHandler.hasLeft();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        };
    }

    private ValueEventListener createDataListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild(Integer.toString(remoteOpponentSeqNum))) {
                        DataSnapshot newCheckPointData = dataSnapshot.child(Integer.toString(remoteOpponentSeqNum));
                        remoteOpponentSeqNum++;

                        CheckPoint newCheckPoint = new CheckPoint(
                                ((double)newCheckPointData.child(FirebaseNodes.LATITUDE).getValue()),
                                ((double)newCheckPointData.child(FirebaseNodes.LONGITUDE).getValue()));

                        callbackHandler.hasNewData(newCheckPoint);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        };
    }

    private ValueEventListener createInRoomListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(firstInRoomCallback) {
                    firstInRoomCallback = false;
                } else {
                    callbackHandler.opponentInRoom();
                    firebaseHelper.removeUserChallengeListener(challengeName, remoteOpponent, inRoomListener, FirebaseNodes.ChallengeStatus.IN_ROOM);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        };
    }

    @Override
    public void imReady() {
        if (isChallengeTerminated) {
            return;
        }
        firebaseHelper.setUserStatus(challengeName, localUser, FirebaseNodes.ChallengeStatus.READY, true);
    }

    @Override
    public void startChallenge() {
        if (isChallengeTerminated) {
            return;
        }
        firebaseHelper.removeUserChallengeListener(challengeName, remoteOpponent, onReadyListener, FirebaseNodes.ChallengeStatus.READY);
        onReadyListener = null;
    }

    @Override
    public void putData(CheckPoint checkPoint) {

        if (checkPoint == null) {
            throw new IllegalArgumentException("CheckPoint is null");
        } else if (isChallengeTerminated) {
            return;
        }

        if(!localUserFinished) {
            firebaseHelper.addChallengeCheckPoint(checkPoint, challengeName, localUser, localUserSeqNum);
            localUserSeqNum++;
        }
    }

    @Override
    public void imFinished() {
        if (isChallengeTerminated) {
            return;
        }

        firebaseHelper.setUserStatus(challengeName, localUser, FirebaseNodes.ChallengeStatus.FINISH, true);
        localUserFinished = true;

        if (remoteOpponentFinished) {
            removeActiveListeners();
            firebaseHelper.deleteChallengeNode(challengeName);
            isChallengeTerminated = true;
        }
    }

    @Override
    public void abortChallenge() {
        firebaseHelper.setUserStatus(challengeName, localUser, FirebaseNodes.ChallengeStatus.ABORT, true);
        removeActiveListeners();
        isChallengeTerminated = true;
    }
}
