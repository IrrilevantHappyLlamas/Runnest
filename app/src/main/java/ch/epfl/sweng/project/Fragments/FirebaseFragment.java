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
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ch.epfl.sweng.project.Model.Profile;

/**
 * Demo fragment to show transactions with firebase database
 */
public class FirebaseFragment extends Fragment implements View.OnClickListener {

    private FirebaseFragment.FireBaseFragmentInteractionListener firebaseListener = null;

    private DatabaseReference mDatabase = null;

    private Button addUser = null;
    private Button addRun = null;

    private EditText idText = null;
    private EditText nameText = null;

    private EditText idTextRun = null;
    private EditText runText = null;
    private EditText distText = null;
    private EditText timeText = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_firebase, container, false);

        // Initialize database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        idText = (EditText) view.findViewById((R.id.edit_user_id));
        nameText = (EditText) view.findViewById((R.id.edit_user_name));

        idTextRun = (EditText) view.findViewById((R.id.run_user));
        runText = (EditText) view.findViewById((R.id.run_name));
        distText = (EditText) view.findViewById((R.id.tot_dist));
        timeText = (EditText) view.findViewById((R.id.tot_time));

        addUser = (Button) view.findViewById(R.id.add_user);
        addUser.setOnClickListener(this);

        addRun = (Button) view.findViewById(R.id.add_run);
        addRun.setOnClickListener(this);

        return view;
    }

    private void getAndWriteRun() {

        if(idTextRun.getText().toString().isEmpty() || runText.getText().toString().isEmpty() ||
                distText.getText().toString().isEmpty() || timeText.getText().toString().isEmpty()){
            Toast.makeText(getActivity().getBaseContext(), "Insert something in all fields", Toast.LENGTH_LONG).show();
            return;
        }

        String id = idTextRun.getText().toString();
        String run = runText.getText().toString();
        String dist = distText.getText().toString();
        String time = timeText.getText().toString();

        mDatabase.child("users").child(id).child("runs").child(run).child("distance").setValue(dist);
        mDatabase.child("users").child(id).child("runs").child(run).child("time").setValue(time);

        Toast.makeText(getActivity().getBaseContext(), "Run added", Toast.LENGTH_LONG).show();
    }

    private void getAndWriteUser() {

        if(idText.getText().toString().isEmpty() || nameText.getText().toString().isEmpty()) {
            Toast.makeText(getActivity().getBaseContext(), "Insert something in all fields", Toast.LENGTH_LONG).show();
            return;
        }
        String id = idText.getText().toString();
        String name = nameText.getText().toString();

        if(userExists(id)) {
            mDatabase.child("users").child(id).child("name").setValue(name);
            Toast.makeText(getActivity().getBaseContext(), "User added", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity().getBaseContext(), "User already exists", Toast.LENGTH_LONG).show();
        }
    }

    private boolean userExists(String id) {
        mDatabase.child(id).once('value', function(snapshot));
        (snapshot.val() !== null);
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
            case R.id.add_user:
                getAndWriteUser();
                break;
            case R.id.add_run:
                getAndWriteRun();
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
