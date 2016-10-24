package ch.epfl.sweng.project.Fragments;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.project.Database.DBHelper;
import ch.epfl.sweng.project.Model.CheckPoint;
import ch.epfl.sweng.project.Model.Effort;
import ch.epfl.sweng.project.Model.FirebaseHelper;
import ch.epfl.sweng.project.Model.Run;

/**
 * Demo fragment to show transactions with firebase database
 */
public class FirebaseFragment extends Fragment implements View.OnClickListener {

    private FirebaseFragment.FireBaseFragmentInteractionListener firebaseListener = null;

    private FirebaseHelper mFirebaseHelper = null;

    private Button uploadDB = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_firebase, container, false);

        // Initialize database
        mFirebaseHelper = new FirebaseHelper();

        uploadDB = (Button) view.findViewById(R.id.upload_db);
        uploadDB.setOnClickListener(this);

        return view;
    }

    private void uploadDB() {
        DBHelper dbHelper = new DBHelper(getContext());
        mFirebaseHelper.updateStorageWithDB(dbHelper.getDBFile());
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FirebaseFragment.FireBaseFragmentInteractionListener) {
            firebaseListener = (FirebaseFragment.FireBaseFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        firebaseListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upload_db:
                uploadDB();
                break;
        }
    }

    /**
     * Interface for SideBarActivity
     */
    public interface FireBaseFragmentInteractionListener {
        void onFirebaseFragmentInteraction(Uri uri);
    }
}
