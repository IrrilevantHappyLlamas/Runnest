package ch.epfl.sweng.project.Model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Test suite for the MainActivity of Homework1
 */
public class FirebaseHelper {

    private DatabaseReference mDatabase = null;

    public FirebaseHelper() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public boolean addUser(Profile profile) {
        //mDatabase.child()
    }

    public boolean addEffort(String id, Effort effort) {

    }
}
