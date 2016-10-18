package ch.epfl.sweng.project.Model;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Helper class that provides methods to update and interact with the remote firebase database instance
 */
public class FirebaseHelper {

    private static final String TAG = "FirebaseHelper";

    // Remote database instance
    private DatabaseReference mDatabase = null;

    // Private variable to make exists() methods work
    private boolean mWaitForListener = true;
    private boolean mUserExists = false;
    private boolean mEffortExists = false;
    private String mUserDescription = null;

    /**
     * Constructor that initializes the database instance
     */
    public FirebaseHelper() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Add a new user to the firebase remote database. If the user already exists on the database, the operation is
     * unsuccessful.
     *
     * @param id    the id of the user to add, a <code>String</code>
     * @param name  name to associate to the user, a <code>String</code>
     * @return      <code>true</code> if the operation is successful, <code>false</code> if it fails or if the user
     *              already exists
     * @throws IllegalArgumentException     if the arguments are <code>null</code> or empty
     */
    public boolean addUser(String id, String name) throws IllegalArgumentException {

        //Check valdity of arguments
        if(id == null || name == null) {
            throw new IllegalArgumentException("Error: invalid argument, id and name have to be non-null and not empty");
        } else if(id.isEmpty() || name.isEmpty() || name.length() > 100) {
            throw new IllegalArgumentException("Error: invalid argument, id and name must be non empty and" +
                    "name length has to be under 100 characters");
        }

        if(userExists(id)) {
            Log.d(TAG, "User already exists in database");
            return false;
        } else {
            Log.d(TAG, "Add new user");
            mDatabase.child("users").child(id).child("name").setValue(name);
            mDatabase.child("users").child(id).child("efforts").setValue(0);
        }
        return true;
    }

    /**
     * Add an <code>Effort</code> to a specified user. <code>Effort</code> names must be unique for a user, otherwise
     * the operation fails. If the operation succeeds, the name, total distance and duration of the <code>Effort</code>
     * are stored in the remote database
     *
     * @param id        user to which to add the <code>Effort</code>, a id <code>String</code>
     * @param effort    <code>Effort</code> to add to the database
     * @return          <code>true</code> if the operation succeeds, <code>false</code> otherwise
     * @throws IllegalArgumentException     if arguments are <code>null</code> or empty
     */
    public boolean addEffort(String id, Effort effort) throws IllegalArgumentException {

        //Check valdity of arguments
        if(id == null || effort == null) {
            throw new IllegalArgumentException("Error: invalid argument, id and effort have to be non-null");
        } else if(id.isEmpty()) {
            throw new IllegalArgumentException("Error: invalid argument, id  must be not empty");
        }

        //check degli argomenti dell'effort?

        if(!userExists(id)) {
            Log.d(TAG, "User doesn't exist in database");
            return false;
        } else {
            if(!effortExists(id, effort.getName())) {
                Log.d(TAG, "Add effort");
                Track track = effort.getTrack();
                mDatabase.child("users").child(id).child("efforts")
                        .child(effort.getName()).child("distance").setValue(track.getDistance());
                mDatabase.child("users").child(id).child("efforts")
                        .child(effort.getName()).child("duration").setValue(track.getDuration());
            } else {
                Log.d(TAG, "An effort with that name already exists");
                return false;
            }
        }

        return true;
    }

    /**
     * Retrieves a complete description of a user from the database. The <code>String</code> description contains the
     * name of the user and a list of his efforts.
     *
     * @param id    user for which we want the description
     * @return      a <code>String</code> description of the user and his efforts
     */
    public String retrieveUserString(String id) {

        //Check valdity of arguments
        if(id == null) {
            throw new IllegalArgumentException("Error: invalid argument, id and effort have to be non-null");
        } else if(id.isEmpty()) {
            throw new IllegalArgumentException("Error: invalid argument, id  must be not empty");
        }

        mUserDescription = "";

        if(!userExists(id)) {
            mUserDescription = "This user doesn't exist";
            return mUserDescription;
        } else {
            mDatabase.child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        mUserDescription += "Name : " + dataSnapshot.child("name").getValue().toString() + "\n";
                        if (dataSnapshot.child("efforts").exists()) {
                            for(DataSnapshot effort : dataSnapshot.child("runs").getChildren()) {
                                mUserDescription += effort.toString();
                            }
                        } else {
                            mUserDescription += "Runs : -";
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        String descriptionToReturn = new String(mUserDescription);
        mUserDescription = null;

        return descriptionToReturn;
    }

    /**
     * Checks if a given effort name is already stored under a specific user
     *
     * @param id            user to check
     * @param effortName    the name of the effort to check
     * @return              <code>true</code> if the effort is already in the database, <code>false</code> otherwise
     */
    public boolean effortExists(String id, String effortName) {

        mDatabase.child("users").child(id).child("efforts").child(effortName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("distance")) {
                    mEffortExists = true;
                }
                mWaitForListener = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        while(mWaitForListener) {

        }

        boolean effortExistsToReturn = mEffortExists;
        mEffortExists = false;
        mWaitForListener = true;

        return effortExistsToReturn;
    }

    /**
     * Checks if a given user id is already stored in the database
     *
     * @param id    user to check
     * @return      <code>true</code> if the user is already in the database, <code>false</code> otherwise
     */
    public boolean userExists(String id) {

        mDatabase.child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("name")){
                        mUserExists = true;
                }
                mWaitForListener = false;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        while(mWaitForListener) {

        }

        boolean userExistsToReturn = mUserExists;
        mUserExists = false;
        mWaitForListener = true;

        return userExistsToReturn;
    }
}
