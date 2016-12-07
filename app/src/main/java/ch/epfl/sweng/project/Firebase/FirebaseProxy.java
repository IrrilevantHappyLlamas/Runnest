package ch.epfl.sweng.project.Firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.project.Model.ChallengeProxy;
import ch.epfl.sweng.project.Model.CheckPoint;

/**
 * Proxy class that links the challenge activity to firebase remote database. It receives data and inputs from the
 * challenge activity and fragments and translates them into inputs for the challenge instance on firebase, which the
 * proxy also instantiates at its creation.
 */
public class FirebaseProxy implements ChallengeProxy {

    private Handler callbackHandler = null;
    private FirebaseHelper firebaseHelper = null;

    private String challengeName = null;
    private boolean owner = false;
    private boolean isChallengeTerminated = false;
    private boolean isChallengeRunning = false;

    private boolean firstReadyCallback = true;
    private boolean firstFinishedCallback = true;
    private boolean firstAbortCallback = true;
    private boolean firstInRoomCallback = true;
    private boolean remoteOpponentFinished = false;
    private boolean localUserFinished = false;

    private String localUser = null;
    private int localUserSeqNum = 0;
    private String remoteOpponent = null;
    private int remoteOpponentSeqNum = 0;

    private ValueEventListener onReadyListener = null;
    private ValueEventListener onFinishedListener = null;
    private ValueEventListener onAbortListener = null;
    private ValueEventListener onDataListener = null;
    private ValueEventListener inRoomListener = null;


    /**
     * Public constructor that takes the two opponents names and instantiates the challenge on firebase. It also takes
     * as a parameter the <code>Handler</code> that will be used on callbacks events from the firebase remote database.
     * The last parameter is a boolean that indicates whether the creator of this proxy is the "owner" of the challenge
     * and must instantiate the challenge node on firebase.
     *
     * @param localUser         the challenger from the local device
     * @param remoteOpponent    the remote challenger
     * @param handler           an handler from the proxy's client, which will handle callbacks
     * @param owner             indicates if the local user is the owner of the challenge
     */
    public FirebaseProxy(String localUser, String remoteOpponent, final Handler handler, boolean owner, String identifier) {

        if (localUser == null || remoteOpponent == null || identifier == null || handler == null) {
            throw new NullPointerException("FirebaseProxy construction parameters can't be null");
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
            firebaseHelper.setUserStatus(challengeName, localUser, FirebaseHelper.challengeNodeType.IN_ROOM, true);
            setOpponentListeners();
        } else {
            firebaseHelper.setUserStatus(challengeName, localUser, FirebaseHelper.challengeNodeType.IN_ROOM, true);
            setOpponentListeners();
            checkForPreviousState(FirebaseHelper.challengeNodeType.ABORT);
            checkForPreviousState(FirebaseHelper.challengeNodeType.READY);
        }
    }

    private void setOpponentListeners() {
        onReadyListener = createReadyListener();
        onFinishedListener = createFinishedListener();
        onAbortListener = createAbortListener();
        onDataListener = createDataListener();

        firebaseHelper.setUserChallengeListener(challengeName, remoteOpponent, onReadyListener, FirebaseHelper.challengeNodeType.READY);
        firebaseHelper.setUserChallengeListener(challengeName, remoteOpponent, onFinishedListener, FirebaseHelper.challengeNodeType.FINISH);
        firebaseHelper.setUserChallengeListener(challengeName, remoteOpponent, onAbortListener, FirebaseHelper.challengeNodeType.ABORT);
        firebaseHelper.setUserChallengeListener(challengeName, remoteOpponent, onDataListener, FirebaseHelper.challengeNodeType.DATA);

        // If owner, create listener to see when opponent enters room
        if (owner) {
            inRoomListener = createInRoomListener();
            firebaseHelper.setUserChallengeListener(challengeName, remoteOpponent, inRoomListener, FirebaseHelper.challengeNodeType.IN_ROOM);
        }
    }

    private void removeActiveListeners() {
        if (onReadyListener != null) {
            firebaseHelper.removeUserChallengeListener(challengeName, localUser, onReadyListener, FirebaseHelper.challengeNodeType.READY);
            onReadyListener = null;
        }

        if (onDataListener != null) {
            firebaseHelper.removeUserChallengeListener(challengeName, localUser, onDataListener, FirebaseHelper.challengeNodeType.DATA);
            onDataListener = null;
        }

        if (onFinishedListener != null) {
            firebaseHelper.removeUserChallengeListener(challengeName, localUser, onFinishedListener, FirebaseHelper.challengeNodeType.FINISH);
            onFinishedListener = null;
        }

        if (onAbortListener != null) {
            firebaseHelper.removeUserChallengeListener(challengeName, localUser, onAbortListener, FirebaseHelper.challengeNodeType.ABORT);
            onAbortListener = null;
        }
    }

    private void checkForPreviousState(final FirebaseHelper.challengeNodeType statusType) {
        firebaseHelper.getDatabase().child("challenges").child(challengeName).child(remoteOpponent)
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
                                    callbackHandler.hasAborted();
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

    public String getChallengeName() {
        return challengeName;
    }

    public boolean isChallengeTerminated() {
        return isChallengeTerminated;
    }

    /**
     * Deletes the challenge for firebase, to be used only when you are sure to be the last person in the room.
     */
    public void deleteChallenge() {
        firebaseHelper.deleteChallengeNode(challengeName);
    }

    @Override
    public void imReady() {
        if (isChallengeTerminated) {
            return;
        }
        firebaseHelper.setUserStatus(challengeName, localUser, FirebaseHelper.challengeNodeType.READY, true);
    }

    @Override
    public void startChallenge() {
        if (isChallengeTerminated) {
            return;
        }
        firebaseHelper.removeUserChallengeListener(challengeName, remoteOpponent, onReadyListener, FirebaseHelper.challengeNodeType.READY);
        isChallengeRunning = true;
        onReadyListener = null;
    }

    @Override
    public void putData(CheckPoint checkPoint) {

        if (checkPoint == null) {
            throw new NullPointerException("CheckPoint is null");
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

        firebaseHelper.setUserStatus(challengeName, localUser, FirebaseHelper.challengeNodeType.FINISH, true);
        localUserFinished = true;

        if (remoteOpponentFinished) {
            removeActiveListeners();
            firebaseHelper.deleteChallengeNode(challengeName);
            isChallengeTerminated = true;
        }
    }

    @Override
    public void abortChallenge() {
        firebaseHelper.setUserStatus(challengeName, localUser, FirebaseHelper.challengeNodeType.ABORT, true);
        removeActiveListeners();
        isChallengeTerminated = true;
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
                        callbackHandler.hasAborted();
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
                                ((double)newCheckPointData.child("latitude").getValue()),
                                ((double)newCheckPointData.child("longitude").getValue()));

                        callbackHandler.hasNewData(newCheckPoint);
                    } else {
                        throw new DatabaseException("Callback was sent without the expected new data");
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
                    firebaseHelper.removeUserChallengeListener(challengeName, remoteOpponent, inRoomListener, FirebaseHelper.challengeNodeType.IN_ROOM);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        };
    }

    /**
     * Utility method to generate the challenge name in the form "userA vs userB id", where "userA" is lexicographically
     * less than "userB"
     *
     * @param user1         first user of the challenge
     * @param user2         second user of the challenge
     * @param identifier    challenge identifier
     * @return              challenge name following the convention
     */
    public static String generateChallengeName(String user1, String user2, String identifier) {

        String challengeName;
        int namesCompare = user1.compareTo(user2);
        challengeName = (namesCompare <= 0)?user1 + " vs " + user2:user2 + " vs " + user1;
        challengeName += " " + identifier;

        return challengeName;
    }
}
