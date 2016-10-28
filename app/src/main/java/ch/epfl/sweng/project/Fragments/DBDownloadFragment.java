package ch.epfl.sweng.project.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ch.epfl.sweng.project.Database.DBHelper;

/**
 * Fragment that manages download of remote runs.db file the user has on Firebase storage and substitution into
 * local SQLite database.
 */
@SuppressWarnings("MagicNumber")
public class DBDownloadFragment extends Fragment implements
        OnSuccessListener<FileDownloadTask.TaskSnapshot>,
        OnFailureListener
{
    @SuppressWarnings("FieldCanBeLocal")
    private final String TAG = "Database Downloader";
    private DBDownloadFragment.DBDownloadFragmentInteractionListener DBDownloadListener = null;

    private DBHelper dbHelper = null;
    private File downloadedDB = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_dbdownload, container, false);

        dbHelper = new DBHelper(getContext());

        // Try to download remote user database
        try {
            downloadedDB = File.createTempFile("runs", "db");
        } catch (IOException e) {
            error(e);
        }
        downloadDatabase();

        return view;
    }

    /**
     * Issue the file request to Firebase storage, using the currently authenticated Firebase user
     */
    private void downloadDatabase() {

        getUserRef().child(dbHelper.getDatabaseName()).getFile(downloadedDB)
                .addOnSuccessListener(this).addOnFailureListener(this);
    }

    /**
     * Returns the reference of the user's runs database file on the remote Firebase storage
     *
     * @return  the Firebase storage reference of the user's runs database
     */
    private StorageReference getUserRef() {
        return FirebaseStorage.getInstance()
                .getReferenceFromUrl("gs://runnest-146309.appspot.com")
                .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    @Override
    public void onSuccess(FileDownloadTask.TaskSnapshot tResult) {

        writeDBFile(downloadedDB);
        Toast.makeText(getContext(), "User Data Retrieved", Toast.LENGTH_LONG).show();
        DBDownloadListener.onDBDownloadFragmentInteraction();
    }

    // TODO: catch other Firebase exceptions and handle them properly
    @SuppressWarnings("OverlyBroadCatchBlock")
    @Override
    public void onFailure(@NonNull Exception e) {

        try {
            writeDBFile(File.createTempFile("runs", "db"));
        } catch (Exception e1) {
            error(e1);
        }

        Toast.makeText(getContext(), "No remote User Data", Toast.LENGTH_LONG).show();
        DBDownloadListener.onDBDownloadFragmentInteraction();
    }

    /**
     * Write the <code>File</code> passed as argument into the SQLite default database file, overwriting it
     *
     * @param newDB     the new database <code>File</code>
     * @throws IllegalArgumentException
     */
    @SuppressWarnings({"IOResourceOpenedButNotSafelyClosed", "resource"})
    private void writeDBFile(File newDB) {

        if(newDB == null) {
            throw new IllegalArgumentException("New database File is null");
        }

        // Not using try-with-resources here because it would break our API requirements
        //noinspection OverlyBroadCatchBlock
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
        } catch (Exception e) {
           error(e);
        }
    }

    /**
     *  Handles logging of <code>Exceptions</code> that break the database download and substitution process
     *
     * @param e     <code>Exception</code> to log
     */
    private void error(Exception e) {
        Log.e(TAG, e.getMessage(), e);
        Toast.makeText(getContext(), "Failed to retrieve user data, restart app", Toast.LENGTH_LONG).show();
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
