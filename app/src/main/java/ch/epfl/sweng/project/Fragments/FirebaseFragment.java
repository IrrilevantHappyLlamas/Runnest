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

import ch.epfl.sweng.project.Model.CheckPoint;
import ch.epfl.sweng.project.Model.Effort;
import ch.epfl.sweng.project.Model.FirebaseHelper;
import ch.epfl.sweng.project.Model.Run;

/**
 * Demo fragment to show transactions with firebase database
 */
public class FirebaseFragment extends Fragment implements View.OnClickListener {

    private FirebaseFragment.FireBaseFragmentInteractionListener firebaseListener = null;

    private FirebaseHelper mFirebaseHelper;

    private Effort demoEffort = null;

    private Button addUser = null;
    private Button addEffort = null;
    private Button retrieveUser = null;

    private EditText idText = null;
    private EditText nameText = null;

    private EditText idTextRun = null;

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
        addUser = (Button) view.findViewById(R.id.add_user);
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
        addEffort = (Button) view.findViewById(R.id.add_effort);
        addEffort.setOnClickListener(this);

        // Retrieve User
        askId = (EditText) view.findViewById((R.id.get_user));
        retName = (TextView) view.findViewById((R.id.retrieved_name));
        retRun = (TextView) view.findViewById((R.id.retrieved_runs));
        retrieveUser = (Button) view.findViewById(R.id.retrieve_user);
        retrieveUser.setOnClickListener(this);

        return view;
    }

    public CheckPoint buildCheckPoint(double lat, double lon, long time) {
        Location location = new Location("test");
        location.setLatitude(lat);
        location.setLongitude(lon);
        location.setTime(time);
        return new CheckPoint(location);
    }

    private void getAndWriteEffort(Effort effort) {

        if(idTextRun.getText().toString().isEmpty()){
            Toast.makeText(getActivity().getBaseContext(), "Insert something in all fields", Toast.LENGTH_LONG).show();
            return;
        }

        String id = idTextRun.getText().toString();

        if (mFirebaseHelper.addEffort(id, demoEffort)) {
            Toast.makeText(getActivity().getBaseContext(), "Run added", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity().getBaseContext(), "Run not added", Toast.LENGTH_LONG).show();
        };
    }

    private void getAndWriteUser() {

        if(idText.getText().toString().isEmpty() || nameText.getText().toString().isEmpty()) {
            Toast.makeText(getActivity().getBaseContext(), "Insert something in all fields", Toast.LENGTH_LONG).show();
            return;
        }
        String id = idText.getText().toString();
        String name = nameText.getText().toString();

        boolean addSuccessful = mFirebaseHelper.addUser(id, name);
        if (addSuccessful){
            Toast.makeText(getActivity().getBaseContext(), "User added", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity().getBaseContext(), "User already exists", Toast.LENGTH_LONG).show();
        }
    }

    private void retrieveUser() {
        if(askId.getText().toString().isEmpty()) {
            Toast.makeText(getActivity().getBaseContext(), "Insert something in all fields", Toast.LENGTH_LONG).show();
            return;
        }

        String id = askId.getText().toString();

        retName.setText(mFirebaseHelper.retrieveUserString(id));
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
