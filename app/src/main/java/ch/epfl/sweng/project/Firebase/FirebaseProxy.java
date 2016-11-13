package ch.epfl.sweng.project.Firebase;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.project.Model.ChallangeProxy;
import ch.epfl.sweng.project.Model.CheckPoint;

/**
 * Notes:
 * - challenge name unique between two users => max 1 challenge per pair saved on firebase for now
 * - valueEventListeners have to be removed when challenge ends: add to proxy the functionality to end the challenge
 */
public class FirebaseProxy implements ChallangeProxy, ValueEventListener {

    private Handler callbackHandler = null;
    private FirebaseHelper firebaseHelper = null;

    private String challengeName = null;

    private String localUser = null;
    private int localUserSeqNum = 0;

    private String remoteOpponent = null;
    private int remoteOpponentSeqNum = 0;

    /**
     * Public constructor that takes the two opponents and instantiates the challenge on firebase. It also takes
     *
     * @param localUser
     * @param remoteOpponent
     * @param handler
     */
    public FirebaseProxy(String localUser, String remoteOpponent, final Handler handler) {

        this.localUser = localUser;
        this.remoteOpponent = remoteOpponent;
        challengeName = generateChallengeName(localUser, remoteOpponent);
        firebaseHelper = new FirebaseHelper();

        firebaseHelper.addChallengeNode(localUser, remoteOpponent, challengeName);
        setOpponentStatusListener();
        setOpponentDataListener();

        // Default handler that does nothing
        callbackHandler = handler;
    }

    @Override
    public void putData(CheckPoint checkPoint) {

        if (checkPoint == null) {
            throw new NullPointerException("CheckPoint is null");
        }

        firebaseHelper.addChallengeCheckPoint(checkPoint, challengeName, localUser, localUserSeqNum);
        localUserSeqNum++;
    }


    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Log.i("test", "onDataChange");
        if (dataSnapshot.exists()) {
            if (dataSnapshot.hasChild(Integer.toString(remoteOpponentSeqNum))) {
                DataSnapshot newCheckPointData = dataSnapshot.child(Integer.toString(remoteOpponentSeqNum));
                remoteOpponentSeqNum++;

                CheckPoint newCheckPoint = new CheckPoint(
                        ((Long)newCheckPointData.child("latitude").getValue()).doubleValue(),
                        ((Long)newCheckPointData.child("longitude").getValue()).doubleValue());

                callbackHandler.OnNewDataHandler(newCheckPoint);
            } else {
                // handle sequence error
            }
        }
    }

    @Override
    public void setHandler(Handler setHandler) {

        if (setHandler == null) {
            throw new NullPointerException("Proxy handler is null");
        }

        callbackHandler = setHandler;
    }

    @Override
    public void imReady() {
        firebaseHelper.setUserReady(challengeName, localUser);
    }

    private void setOpponentDataListener() {
        firebaseHelper.setUserDataListener(challengeName, remoteOpponent, this);
    }

    /**
     * Note: this could be triggered also when checkpoints change, so we need to make sure not to call the handler two times
     */
    private void setOpponentStatusListener() {
        ValueEventListener onReadyListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.i("test", "callback");
                    callbackHandler.isReadyHandler();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO: handle firebase error
            }
        };

        firebaseHelper.setUserStatusListener(challengeName, remoteOpponent, onReadyListener);
    }

    // TODO: comments, args
    private String generateChallengeName(String user1, String user2) {
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

    }
}
