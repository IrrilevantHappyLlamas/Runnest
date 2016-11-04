package ch.epfl.sweng.project.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import ch.epfl.sweng.project.Activities.LoginActivity;
import ch.epfl.sweng.project.AppRunnest;
import ch.epfl.sweng.project.Database.DBHelper;

/**
 * Fragment that manages upload of local SQLite database to the remote Firebase storage of user
 */
public class DBUploadFragment extends Fragment implements
        OnSuccessListener<UploadTask.TaskSnapshot>,
        OnFailureListener
{
    @SuppressWarnings("unused")
    private DBUploadFragment.DBUploadFragmentInteractionListener DBUploadListener = null;

    private DBHelper dbHelper = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_dbupload, container, false);

        dbHelper = new DBHelper(getContext());
        uploadDatabase();

        return view;
    }

    /**
     * Start the upload task that puts the local SQLite database file into the remote Firebase storage
     */
    private void uploadDatabase() {
        Uri file = Uri.fromFile(dbHelper.getDatabasePath());
        UploadTask uploadTask = getUserStorageRef().child(dbHelper.getDatabaseName()).putFile(file);
        uploadTask.addOnFailureListener(this).addOnSuccessListener(this);
    }

    /**
     * Returns the reference of the current user's runs database file on the remote Firebase storage
     *
     * @return  the Firebase storage reference of the user's runs database
     */
    private StorageReference getUserStorageRef() {

        return FirebaseStorage.getInstance()
                .getReferenceFromUrl("gs://runnest-146309.appspot.com")
                .child("users").child(((AppRunnest) getActivity().getApplication()).getUser().getFirebaseId());
    }

    /**
     * Ask the user for confirmation and then bring them back to the login screen. Sign out from Firebase.
     */
    private void logout() {
        ((AppRunnest)getActivity().getApplication()).getUser().logoutStatus();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        FirebaseAuth.getInstance().signOut();
    }

    // TODO: eventually catch other Firebase exceptions and handle them properly
    @Override
    public void onFailure(@NonNull Exception e) {
        Toast.makeText(getContext(), "Upload failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSuccess(UploadTask.TaskSnapshot tResult) {
        Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_LONG).show();
        logout();
    }

    @SuppressWarnings("ProhibitedExceptionThrown")
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DBUploadFragment.DBUploadFragmentInteractionListener) {
            DBUploadListener = (DBUploadFragment.DBUploadFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        DBUploadListener = null;
    }

    /**
     * Interface for SideBarActivity
     */
    public interface DBUploadFragmentInteractionListener {
        @SuppressWarnings("unused")
        void onDBUploadFragmentInteraction( );
    }
}
