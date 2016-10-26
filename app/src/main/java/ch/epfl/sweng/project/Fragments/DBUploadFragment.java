package ch.epfl.sweng.project.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import ch.epfl.sweng.project.Database.DBHelper;
import ch.epfl.sweng.project.Database.DBSync;

/**
 * Demo fragment to show transactions with firebase database
 */
public class DBUploadFragment extends Fragment
        implements
        OnSuccessListener<UploadTask.TaskSnapshot>,
        OnFailureListener
{

    private DBUploadFragment.DBUploadFragmentInteractionListener DBUploadListener = null;

    private DBHelper dbHelper = null;
    private DBSync databaseSynchronizer = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_dbupload, container, false);

        dbHelper = new DBHelper(getContext());
        databaseSynchronizer = new DBSync(dbHelper);

        uploadDatabase();

        return view;
    }

    public void uploadDatabase() {

        Uri file = Uri.fromFile(dbHelper.getDatabasePath());
        UploadTask uploadTask = getUserRef().child(dbHelper.getDatabaseName()).putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(this).addOnSuccessListener(this);
    }

    public StorageReference getUserRef() {
        return FirebaseStorage.getInstance()
                .getReferenceFromUrl("gs://runnest-146309.appspot.com")
                .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    private void logout() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "Logout successful", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        intent.putExtra("Source", "logout_pressed");
                        startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

        FirebaseAuth.getInstance().signOut();
    }


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

    @Override
    public void onFailure(@NonNull Exception e) {
        Toast.makeText(getContext(), "Upload failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        Toast.makeText(getContext(), "Upload succeeded", Toast.LENGTH_LONG).show();
        logout();

    }

    /**
     * Interface for SideBarActivity
     */
    public interface DBUploadFragmentInteractionListener {
        void onDBUploadFragmentInteraction(Uri uri);
    }
}
