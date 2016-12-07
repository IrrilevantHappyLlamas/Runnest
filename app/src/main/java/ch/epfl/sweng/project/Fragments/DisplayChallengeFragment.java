package ch.epfl.sweng.project.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import ch.epfl.sweng.project.Database.DBHelper;
import ch.epfl.sweng.project.Model.Challenge;
import ch.epfl.sweng.project.Model.CheckPoint;
import ch.epfl.sweng.project.Model.Track;
import ch.epfl.sweng.project.UtilsUI;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DisplayChallengeFragment.OnDisplayChallengeFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DisplayChallengeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayChallengeFragment extends Fragment implements OnMapReadyCallback {
    private static final String CHALLENGE_TO_BE_DISPLAYED = "challenge to be displayed";
    private DisplayChallengeFragment.OnDisplayChallengeFragmentInteractionListener mListener;
    private Challenge mChallengeToBeDisplayed;

    private MapView mMapView = null;
    private MapView mOpponentMapView = null;


    private int userColor;
    private int opponentColor;

    private MapType mCurrentMapType = null;

    public static DisplayChallengeFragment newInstance(Challenge challenge) {
        DisplayChallengeFragment fragment = new DisplayChallengeFragment();
        Bundle args = new Bundle();
        args.putSerializable(CHALLENGE_TO_BE_DISPLAYED, challenge);
        fragment.setArguments(args);
        return fragment;
    }

    private enum MapType {USER_MAP, OPPONENT_MAP}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mChallengeToBeDisplayed = (Challenge) getArguments().getSerializable(CHALLENGE_TO_BE_DISPLAYED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_display_challenge, container, false);

        if (mChallengeToBeDisplayed != null) {


            setupMapUI(view, savedInstanceState);
            setupTextUI(view);
        }

        setupButtonUI(view);

        return view;
    }

    private void setupTextUI(View view) {

        // Extract TextView
        TextView challengeType = ((TextView)view.findViewById(R.id.challenge_type));
        TextView userName = (TextView)view.findViewById(R.id.user_name);
        TextView userPerf = (TextView)view.findViewById(R.id.user_performance);
        TextView userResult = (TextView)view.findViewById(R.id.user_result);
        TextView opponentName = (TextView)view.findViewById(R.id.opponent_name);
        TextView opponentPerf = (TextView)view.findViewById(R.id.opponent_performance);
        TextView opponentResult = (TextView)view.findViewById(R.id.opponent_result);

        // Set names
        userName.setText(mChallengeToBeDisplayed.getMyRun().getName());
        opponentName.setText(mChallengeToBeDisplayed.getOpponentRun().getName());

        // Set title and performances
        switch (mChallengeToBeDisplayed.getType()) {
            case TIME:
                String timeGoal = UtilsUI.timeToString((int)mChallengeToBeDisplayed.getGoal()/1000, false);
                challengeType.setText(getString(R.string.time_challenge) +
                        getString(R.string.white_space) + timeGoal);

                double user_dist = mChallengeToBeDisplayed.getMyRun().getTrack().getDistance()/1000;
                userPerf.setText(String.format(Locale.getDefault(), "%.2f", user_dist) +
                        getString(R.string.white_space) + getString(R.string.km));

                double opponent_dist = mChallengeToBeDisplayed.getOpponentRun().getTrack().getDistance()/1000;
                opponentPerf.setText(String.format(Locale.getDefault(), "%.2f", opponent_dist) +
                        getString(R.string.white_space) + getString(R.string.km));

                break;
            case DISTANCE:
                String distanceGoal = String.format(Locale.getDefault(), "%.2f", mChallengeToBeDisplayed.getGoal());
                challengeType.setText(getString(R.string.distance_challenge) + getString(R.string.white_space) +
                        distanceGoal + getString(R.string.white_space) + getString(R.string.km));

                int userTime = (int)mChallengeToBeDisplayed.getMyRun().getDuration();
                userPerf.setText(UtilsUI.timeToString(userTime, true));

                int opponentTime = (int)mChallengeToBeDisplayed.getOpponentRun().getDuration();
                opponentPerf.setText(UtilsUI.timeToString(opponentTime, true));
                break;
        }
        // Set Text colors and results
        switch (mChallengeToBeDisplayed.getResult()) {
            case WON:
                userResult.setText(getString(R.string.won));
                opponentResult.setText(getString(R.string.lost));

                userColor = ContextCompat.getColor(getContext(), R.color.wonColor);
                opponentColor = ContextCompat.getColor(getContext(), R.color.lostColor);
                break;
            case LOST:
                userResult.setText(getString(R.string.lost));
                opponentResult.setText(getString(R.string.won));

                userColor = ContextCompat.getColor(getContext(), R.color.lostColor);
                opponentColor = ContextCompat.getColor(getContext(), R.color.wonColor);
                break;
            case ABORTED_BY_ME:
                userResult.setText(getString(R.string.left));
                opponentResult.setText(getString(R.string.won));

                userColor = ContextCompat.getColor(getContext(), R.color.lostColor);
                opponentColor = ContextCompat.getColor(getContext(), R.color.wonColor);
                break;
            case ABORTED_BY_OTHER:
                userResult.setText(getString(R.string.won));
                opponentResult.setText(getString(R.string.left));

                userColor = ContextCompat.getColor(getContext(), R.color.wonColor);
                opponentColor = ContextCompat.getColor(getContext(), R.color.lostColor);
                break;
        }

        setRunnerTextColor(userName, userPerf, userResult, userColor);
        setRunnerTextColor(opponentName, opponentPerf, opponentResult, opponentColor);
    }

    public void setRunnerTextColor(TextView t1, TextView t2, TextView t3, int color) {
        t1.setTextColor(color);
        t2.setTextColor(color);
        t3.setTextColor(color);
    }


    private void setupButtonUI(View view) {
        Button runHistoryButton = (Button) view.findViewById(R.id.button_history);
        runHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mListener != null) {
                    mListener.onDisplayChallengeFragmentInteraction();
                }
            }
        });

        final DBHelper dbHelper = new DBHelper(this.getContext());

        Button deleteRunButton = (Button) view.findViewById(R.id.button_delete);
        deleteRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    dbHelper.delete(mChallengeToBeDisplayed);
                    mListener.onDisplayChallengeFragmentInteraction();
                }
            }
        });
    }

    private void setupMapUI(View view, Bundle savedInstanceState) {
        mCurrentMapType = MapType.USER_MAP;
        mMapView = (MapView) view.findViewById(R.id.user_map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        mOpponentMapView = (MapView) view.findViewById(R.id.opponent_map);
        mOpponentMapView.onCreate(savedInstanceState);
    }

    /**
     * Called when the <code>GoogleMap</code> is ready. Initialize a MapHandler.
     *
     * @param googleMap     the <code>GoogleMap</code>
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapStyleOptions mapStyle = MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style_no_label);

        switch(mCurrentMapType) {

            case USER_MAP:
                Track userTrack = mChallengeToBeDisplayed.getMyRun().getTrack();
                googleMap.setMapStyle(mapStyle);

                UtilsUI.displayTrack(userTrack, googleMap, userColor);
                UtilsUI.displayTrackSetupUI(googleMap);

                mCurrentMapType = MapType.OPPONENT_MAP;
                mOpponentMapView.getMapAsync(this);
                break;
            case OPPONENT_MAP:
                Track opponentTrack = mChallengeToBeDisplayed.getOpponentRun().getTrack();
                googleMap.setMapStyle(mapStyle);

                UtilsUI.displayTrack(opponentTrack, googleMap, opponentColor);
                UtilsUI.displayTrackSetupUI(googleMap);
                break;
            default:
                throw new IllegalStateException("unknown map type");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDisplayChallengeFragmentInteractionListener) {
            mListener = (OnDisplayChallengeFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDisplayChallengeFragmentInteractionListener");
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
        mOpponentMapView.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mOpponentMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        mOpponentMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mOpponentMapView.onDestroy();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
        mOpponentMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnDisplayChallengeFragmentInteractionListener {
        // TODO: Update argument type and name
        void onDisplayChallengeFragmentInteraction();
    }
}
