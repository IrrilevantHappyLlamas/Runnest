package ch.epfl.sweng.project.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

import ch.epfl.sweng.project.Database.DBHelper;
import ch.epfl.sweng.project.Model.FirebaseHelper;

/**
 * Demo fragment to show transactions with firebase database
 */
public class FirebaseFragment extends Fragment implements View.OnClickListener {

    private FirebaseFragment.FireBaseFragmentInteractionListener firebaseListener = null;

    private FirebaseHelper mFirebaseHelper = null;

    private Button uploadDB = null;
    private TextView firebaseUserId = null;
    private TextView downloadState = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_firebase, container, false);

        // Initialize database
        mFirebaseHelper = new FirebaseHelper(getContext());

        uploadDB = (Button) view.findViewById(R.id.upload_db);
        uploadDB.setOnClickListener(this);

        firebaseUserId = (TextView) view.findViewById(R.id.firebase_user);
        firebaseUserId.setText(FirebaseAuth.getInstance().getCurrentUser().getUid());

        downloadState = (TextView) view.findViewById(R.id.dw_state);

        return view;
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

    // TODO: handle exception
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upload_db:
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
