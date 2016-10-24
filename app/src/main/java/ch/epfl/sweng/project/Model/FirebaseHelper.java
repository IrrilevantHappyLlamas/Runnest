package ch.epfl.sweng.project.Model;

import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import ch.epfl.sweng.project.Database.DBHelper;

/**
 * Helper class that provides methods to update and interact with the remote firebase database instance
 */
public class FirebaseHelper {

    private static final String TAG = "FirebaseHelper";

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

    // TODO: comments
    public void updateStorageWithDB(File dbFile) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://runnest-146309.appspot.com");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        StorageReference usersRef = storageRef.child("users");
        StorageReference currentUserRef = usersRef.child(user.getUid());
        StorageReference userDbRef = currentUserRef.child("database.db");

        Uri file = Uri.fromFile(dbFile);

        UploadTask uploadTask = userDbRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });
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

    /**
     * Add an <code>Effort</code> to a specified user. <code>Effort</code> names must be unique for a user, otherwise
     * this method updates the old effort. If the operation succeeds, the name, total distance and duration of the
     * <code>Effort</code> are stored in the remote database
     *
     * @param id        user to which to add the <code>Effort</code>, a id <code>String</code>
     * @param effort    <code>Effort</code> to add to the database
     * @throws IllegalArgumentException     if arguments are <code>null</code> or empty
     */
    public void addOrUpdateEffort(String id, Effort effort) throws IllegalArgumentException {

        //Check validity of arguments
        if(id == null || effort == null) {
            throw new IllegalArgumentException("Error: invalid argument, id and effort have to be non-null");
        }
        if(id.isEmpty()) {
            throw new IllegalArgumentException("Error: invalid argument, id  must be not empty");
        }

        Track track = effort.getTrack();
        databaseReference.child("users").child(id).child("efforts")
                .child(effort.getName()).child("distance").setValue(track.getDistance());
        databaseReference.child("users").child(id).child("efforts")
                .child(effort.getName()).child("duration").setValue(track.getDuration());
    }

}
