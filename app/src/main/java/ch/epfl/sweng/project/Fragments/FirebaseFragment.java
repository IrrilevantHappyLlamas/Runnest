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
    private Effort demoEffort = null;

    // Add user
    private EditText idText = null;
    private EditText nameText = null;

    // Add effort
    private EditText idTextRun = null;

    // Retrieve User
    private EditText askId = null;
    private TextView retName = null;
    private TextView retRun = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_firebase, container, false);

        // Initialize database
        mFirebaseHelper = new FirebaseHelper();

        // Add User
        idText = (EditText) view.findViewById((R.id.edit_user_id));
        nameText = (EditText) view.findViewById((R.id.edit_user_name));
        Button addUser = (Button) view.findViewById(R.id.add_user);
        addUser.setOnClickListener(this);

        //Add Effort
        demoEffort = new Run("demoEffort");
        CheckPoint p1 = buildCheckPoint(1, 1, 1);
        CheckPoint p2 = buildCheckPoint(2, 2, 2);
        CheckPoint p3 = buildCheckPoint(3, 3, 3);
        demoEffort.start(p1);
        demoEffort.update(p2);
        demoEffort.update(p3);
        demoEffort.stop();

        idTextRun = (EditText) view.findViewById((R.id.run_user));
        ((TextView) view.findViewById((R.id.effort_name))).setText("Effort name : " + demoEffort.getName());
        ((TextView) view.findViewById((R.id.effort_dist)))
                .setText("Distance : " + demoEffort.getTrack().getDistance());
        ((TextView) view.findViewById((R.id.effort_time)))
                .setText("Duration : " + demoEffort.getTrack().getDuration());
        Button addEffort = (Button) view.findViewById(R.id.add_effort);
        addEffort.setOnClickListener(this);

        // Retrieve User
        askId = (EditText) view.findViewById((R.id.get_user));
        retName = (TextView) view.findViewById((R.id.retrieved_name));
        retRun = (TextView) view.findViewById((R.id.retrieved_runs));
        Button retrieveUser = (Button) view.findViewById(R.id.retrieve_user);
        retrieveUser.setOnClickListener(this);

        return view;
    }

    /**
     * Utility method to rapidly build a <code>CheckPoint</code>
     *
     * @param lat   latitude
     * @param lon   longitude
     * @param time  time
     * @return      the desired <code>CheckPoint</code>
     */
    private CheckPoint buildCheckPoint(double lat, double lon, long time) {
        Location location = new Location("test");
        location.setLatitude(lat);
        location.setLongitude(lon);
        location.setTime(time);
        return new CheckPoint(location);
    }

    /**
     * Writes the sample <code>Effort</code> in the database, under the specified user
     *
     * @param effort    <code>Effort</code> to store
     */
    private void getAndWriteEffort(Effort effort) {

        // Check EditText args
        if(idTextRun.getText().toString().isEmpty()){
            Toast.makeText(getActivity().getBaseContext(), "Insert something in all fields", Toast.LENGTH_LONG).show();
            return;
        }

        // Get user id from EditText
        String id = idTextRun.getText().toString();

        // Update database
        mFirebaseHelper.addOrUpdateEffort(id, demoEffort);
        Toast.makeText(getActivity().getBaseContext(), "Run added", Toast.LENGTH_LONG).show();
    }

    /**
     * Writes a new user to the database or updates existing one
     */
    private void getAndWriteUser() {

        // Check EditText args
        if(idText.getText().toString().isEmpty() || nameText.getText().toString().isEmpty()) {
            Toast.makeText(getActivity().getBaseContext(), "Insert something in all fields", Toast.LENGTH_LONG).show();
            return;
        }

        // Get user specs from EditText
        String id = idText.getText().toString();
        String name = nameText.getText().toString();

        // Update database
        mFirebaseHelper.addOrUpdateUser(id, name);
        Toast.makeText(getActivity().getBaseContext(), "User added", Toast.LENGTH_LONG).show();
    }

    /**
     * Retrieves and displays user information from database
     */
    private void retrieveUser() {

        // Check EditText args
        if(askId.getText().toString().isEmpty()) {
            Toast.makeText(getActivity().getBaseContext(), "Insert something in all fields", Toast.LENGTH_LONG).show();
            return;
        }

        // Get user id from EditText
        String id = askId.getText().toString();

        // Get user information from database and display it in TextView
        mFirebaseHelper.getDatabase().child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    retName.setText("Name : " + dataSnapshot.child("name").getValue().toString());
                    if(dataSnapshot.hasChild("efforts")) {
                        String runs = "Runs : ";
                        for(DataSnapshot  effort : dataSnapshot.child("efforts").getChildren()) {
                            runs += effort.getValue().toString();
                        }
                        retRun.setText(runs);
                    } else {
                        retRun.setText("Efforts : -");
                    }
                } else {
                    retName.setText("Name : this user doesn't exist");
                    retRun.setText("Efforts : -");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
            case R.id.add_effort:
                getAndWriteEffort(demoEffort);
                break;
            case R.id.retrieve_user:
                retrieveUser();
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
