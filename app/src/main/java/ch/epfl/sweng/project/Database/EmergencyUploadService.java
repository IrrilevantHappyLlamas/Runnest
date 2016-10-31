package ch.epfl.sweng.project.Database;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import ch.epfl.sweng.project.AppRunnest;

/**
 * Created by Tobia Albergoni on 31.10.2016.
 */

public class EmergencyUploadService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public EmergencyUploadService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Uri file = intent.getData();
        String databaseName = intent.getStringExtra("databaseName");
        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl("gs://runnest-146309.appspot.com")
                .child("users").child(((AppRunnest) getApplication()).getUser().getFirebaseId());
        UploadTask uploadTask = storageRef.child(databaseName).putFile(file);
    }
}
