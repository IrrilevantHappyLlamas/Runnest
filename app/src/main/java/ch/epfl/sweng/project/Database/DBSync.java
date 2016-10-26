package ch.epfl.sweng.project.Database;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

import ch.epfl.sweng.project.Fragments.DBDownloadFragment;
import ch.epfl.sweng.project.Fragments.DBUploadFragment;

/**
 * Test suite for the MainActivity of Homework1
 *
 */
public class DBSync {

    private DBHelper mDBHelper = null;
    private File downloadedDB = null;


    public DBSync(DBHelper DBHelper) {
        mDBHelper =  DBHelper;
    }

    public File getDownloadedDB() {
        return downloadedDB;
    }

    public StorageReference getUserRef() {
        return FirebaseStorage.getInstance()
                .getReferenceFromUrl("gs://runnest-146309.appspot.com")
                .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    public void downloadDatabase(DBDownloadFragment listener) throws IOException {
        downloadedDB = File.createTempFile("efforts", "db");

        getUserRef().child(mDBHelper.getDatabaseName()).getFile(downloadedDB)
                .addOnSuccessListener(listener).addOnFailureListener(listener);
    }



}
