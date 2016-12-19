package ch.epfl.sweng.project.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ch.epfl.sweng.project.AppRunnest;
import ch.epfl.sweng.project.Database.DBHelper;
import ch.epfl.sweng.project.Firebase.FirebaseHelper;
import ch.epfl.sweng.project.Firebase.FirebaseNodes;
import ch.epfl.sweng.project.Model.User;

/**
 * Fragment that manages download of remote runs.db file the user has on Firebase storage and substitution into
 * local SQLite database.
 */
public class DBDownloadFragment extends Fragment implements
        OnSuccessListener<FileDownloadTask.TaskSnapshot>,
        OnFailureListener
{
    private DBDownloadFragment.DBDownloadFragmentInteractionListener DBDownloadListener = null;

    @SuppressWarnings("FieldCanBeLocal")
    private final String FIREBASE_STORAGE = "gs://runnest-146309.appspot.com";
    private final String DB_FILENAME = "runs";
    private final String DB_EXTENSION = ".db";

    private DBHelper dbHelper = null;
    private File downloadedDB = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_dbdownload, container, false);

        FirebaseHelper firebaseHelper = new FirebaseHelper();
        dbHelper = new DBHelper(getContext());

        User currentUser = ((AppRunnest) getActivity().getApplication()).getUser();
        firebaseHelper.addOrUpdateUser(currentUser.getName(), currentUser.getEmail());
        firebaseHelper.setUserAvailable(currentUser.getEmail(), false, true);

        // Try to download remote user database
        try {
            downloadedDB = File.createTempFile(DB_FILENAME, DB_EXTENSION);
        } catch (IOException e) {
            error(e);
        }
        downloadDatabase();

        return view;
    }

    @Override
    public void onSuccess(FileDownloadTask.TaskSnapshot tResult) {

        writeDBFile(downloadedDB);
        DBDownloadListener.onDBDownloadFragmentInteraction();
    }

    @Override
    public void onFailure(@NonNull Exception e) {

        try {
            writeDBFile(File.createTempFile(DB_FILENAME, DB_EXTENSION));
        } catch (IOException e1) {
            error(e1);
        }

        DBDownloadListener.onDBDownloadFragmentInteraction();
    }

    private void downloadDatabase() {

        getUserStorageRef().child(dbHelper.getDatabaseName()).getFile(downloadedDB)
                .addOnSuccessListener(this).addOnFailureListener(this);
    }

    private StorageReference getUserStorageRef() {

        return FirebaseStorage.getInstance()
                .getReferenceFromUrl(FIREBASE_STORAGE)
                .child(FirebaseNodes.USERS)
                .child(((AppRunnest) getActivity().getApplication()).getUser().getFirebaseId());
    }

    private void writeDBFile(File newDB) {

        if(newDB == null) {
            throw new IllegalArgumentException("New database File is null");
        }

        // Not using try-with-resources here because it would break our API requirements
        try {
            InputStream in = new FileInputStream(newDB);
            OutputStream out = new FileOutputStream(dbHelper.getDatabasePath());

            // Write new db to file
            byte[] buf = new byte[1024];
            int len;
            //noinspection NestedAssignment
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (IOException e) {
           error(e);
        }
    }

    private void error(Exception e) {
        Toast.makeText(getContext(), "Failed to retrieve user data, restart app: " + e.getMessage()
                , Toast.LENGTH_LONG).show();
    }

    /**
     * Interface for Activities
     */
    public interface DBDownloadFragmentInteractionListener {
        void onDBDownloadFragmentInteraction();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DBDownloadFragment.DBDownloadFragmentInteractionListener) {
            DBDownloadListener = (DBDownloadFragment.DBDownloadFragmentInteractionListener) context;
        } else {
            //noinspection ProhibitedExceptionThrown
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        DBDownloadListener = null;
    }
}
