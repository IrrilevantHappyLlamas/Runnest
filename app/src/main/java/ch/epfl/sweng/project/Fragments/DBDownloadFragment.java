package ch.epfl.sweng.project.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ch.epfl.sweng.project.Database.DBHelper;
import ch.epfl.sweng.project.Database.DBSync;

/**
 * Demo fragment to show transactions with firebase database
 */
public class DBDownloadFragment extends Fragment
        implements
        OnSuccessListener<FileDownloadTask.TaskSnapshot>,
        OnFailureListener {

    private DBDownloadFragment.DBDownloadFragmentInteractionListener DBDownloadListener = null;

    private DBHelper dbHelper = null;
    private File downloadedDB = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_dbdownload, container, false);

        dbHelper = new DBHelper(getContext());
        try {
            downloadedDB = File.createTempFile("efforts", "db");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            downloadDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return view;
    }

    public void downloadDatabase() throws IOException {

        getUserRef().child(dbHelper.getDatabaseName()).getFile(downloadedDB)
                .addOnSuccessListener(this).addOnFailureListener(this);
    }

    public StorageReference getUserRef() {
        return FirebaseStorage.getInstance()
                .getReferenceFromUrl("gs://runnest-146309.appspot.com")
                .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DBDownloadFragment.DBDownloadFragmentInteractionListener) {
            DBDownloadListener = (DBDownloadFragment.DBDownloadFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        DBDownloadListener = null;
    }

    @Override
    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

        ((TextView) getActivity().findViewById(R.id.download_text)).setText("File downloaded");

        try {
            InputStream in = new FileInputStream(downloadedDB);
            OutputStream out = new FileOutputStream(dbHelper.getDatabasePath());

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        getActivity().findViewById(R.id.wait_for_up).setVisibility(View.GONE);
        // TODO: start profile fragment
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        ((TextView) getActivity().findViewById(R.id.download_text)).setText("No remote DB");
    }

    /**
     * Interface for SideBarActivity
     */
    public interface DBDownloadFragmentInteractionListener {
        void onDBDownloadFragmentInteraction(Uri uri);
    }
}
