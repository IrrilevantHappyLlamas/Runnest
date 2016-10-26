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
    private String mSearchResult;

    // Remote database instance
    private DatabaseReference databaseReference = null;

    /**
     * Constructor that initializes the database instance
     */
    public FirebaseHelper() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Getter for the remote database reference
     * @return      <code>DatabaseReference</code> for the remote database
     */
    public DatabaseReference getDatabase() {
        return databaseReference;
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

        Log.d(TAG, "Add new user");
        databaseReference.child("users").child(id).child("name").setValue(name);
    }

    public String searchForUser(String id){

        mSearchResult = null;

        databaseReference.child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    mSearchResult = dataSnapshot.child("name").getValue().toString();
            }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return mSearchResult;
    }
    /**
     * Add an <code>Run</code> to a specified user. <code>Run</code> names must be unique for a user, otherwise
     * this method updates the old run. If the operation succeeds, the name, total distance and duration of the
     * <code>Run</code> are stored in the remote database
     *
     * @param id        user to which to add the <code>Run</code>, a id <code>String</code>
     * @param run    <code>Run</code> to add to the database
     * @throws IllegalArgumentException     if arguments are <code>null</code> or empty
     */
    public void addOrUpdateEffort(String id, Run run) throws IllegalArgumentException {

        //Check validity of arguments
        if(id == null || run == null) {
            throw new IllegalArgumentException("Error: invalid argument, id and run have to be non-null");
        }
        if(id.isEmpty()) {
            throw new IllegalArgumentException("Error: invalid argument, id  must be not empty");
        }

        Track track = run.getTrack();
        databaseReference.child("users").child(id).child("efforts")
                .child(run.getName()).child("distance").setValue(track.getDistance());
        databaseReference.child("users").child(id).child("efforts")
                .child(run.getName()).child("duration").setValue(track.getDuration());
    }

}
