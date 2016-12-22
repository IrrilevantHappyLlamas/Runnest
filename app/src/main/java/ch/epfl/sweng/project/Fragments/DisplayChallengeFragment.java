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

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MapStyleOptions;

import java.util.Locale;

import ch.epfl.sweng.project.Database.DBHelper;
import ch.epfl.sweng.project.Model.Challenge;
import ch.epfl.sweng.project.Model.Track;
import ch.epfl.sweng.project.UtilsUI;

/**
 * This class displays a Challenge.
 */
public class DisplayChallengeFragment extends Fragment implements OnMapReadyCallback {
    private static final String CHALLENGE_TO_BE_DISPLAYED = "challenge to be displayed";
    private DisplayChallengeFragment.OnDisplayChallengeFragmentInteractionListener listener;
    private Challenge challengeToBeDisplayed;

    private MapView mapView = null;
    private MapView opponentMapView = null;

    private int userColor;
    private int opponentColor;

    private MapType currentMapType = null;

    /**
     * creates a new instance of this class and initializes some fields
     * @param challenge the challenge to be displayed.
     * @return an instance of this class.
     */
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
            challengeToBeDisplayed = (Challenge) getArguments().getSerializable(CHALLENGE_TO_BE_DISPLAYED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_display_challenge, container, false);

        if (challengeToBeDisplayed != null) {


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
        TextView userPerformance = (TextView)view.findViewById(R.id.user_performance);
        TextView userResult = (TextView)view.findViewById(R.id.user_result);
        TextView opponentName = (TextView)view.findViewById(R.id.opponent_name);
        TextView opponentPerformance = (TextView)view.findViewById(R.id.opponent_performance);
        TextView opponentResult = (TextView)view.findViewById(R.id.opponent_result);

        // Set names
        userName.setText(challengeToBeDisplayed.getMyRun().getName());
        opponentName.setText(challengeToBeDisplayed.getOpponentRun().getName());

        // Set title and performances
        switch (challengeToBeDisplayed.getType()) {
            case TIME:
                String timeGoal = UtilsUI.timeToString((int) challengeToBeDisplayed.getGoal()/1000, false);
                challengeType.setText(getString(R.string.time_challenge) +
                        getString(R.string.white_space) + timeGoal);

                double user_dist = challengeToBeDisplayed.getMyRun().getTrack().getDistance()/1000;
                userPerformance.setText(String.format(Locale.getDefault(), "%.2f", user_dist) +
                        getString(R.string.white_space) + getString(R.string.km));

                double opponent_dist = challengeToBeDisplayed.getOpponentRun().getTrack().getDistance()/1000;
                opponentPerformance.setText(String.format(Locale.getDefault(), "%.2f", opponent_dist) +
                        getString(R.string.white_space) + getString(R.string.km));

                break;
            case DISTANCE:
                String distanceGoal = String.format(Locale.getDefault(), "%.2f", challengeToBeDisplayed.getGoal());
                challengeType.setText(getString(R.string.distance_challenge) + getString(R.string.white_space) +
                        distanceGoal + getString(R.string.white_space) + getString(R.string.km));

                int userTime = (int) challengeToBeDisplayed.getMyRun().getDuration();

                userPerformance.setText(UtilsUI.timeToString(userTime, true));

                int opponentTime = (int) challengeToBeDisplayed.getOpponentRun().getDuration();
                opponentPerformance.setText(UtilsUI.timeToString(opponentTime, true));

                break;
        }

        // Set Text colors and results
        switch (challengeToBeDisplayed.getResult()) {
            case WON:
                userResult.setText(getString(R.string.won_caps));
                opponentResult.setText(getString(R.string.lost_caps));

                userColor = ContextCompat.getColor(getContext(), R.color.wonColor);
                opponentColor = ContextCompat.getColor(getContext(), R.color.lostColor);
                break;
            case LOST:
                userResult.setText(getString(R.string.lost_caps));
                opponentResult.setText(getString(R.string.won_caps));

                userColor = ContextCompat.getColor(getContext(), R.color.lostColor);
                opponentColor = ContextCompat.getColor(getContext(), R.color.wonColor);
                break;
            case ABORTED_BY_ME:
                userResult.setText(getString(R.string.left_caps));
                opponentResult.setText(getString(R.string.won_caps));

                userColor = ContextCompat.getColor(getContext(), R.color.lostColor);
                opponentColor = ContextCompat.getColor(getContext(), R.color.wonColor);
                break;
            case ABORTED_BY_OTHER:
                userResult.setText(getString(R.string.won_caps));
                opponentResult.setText(getString(R.string.left_caps));

                userColor = ContextCompat.getColor(getContext(), R.color.wonColor);
                opponentColor = ContextCompat.getColor(getContext(), R.color.lostColor);
                break;
        }

        setRunnerTextColor(userName, userPerformance, userResult, userColor);
        setRunnerTextColor(opponentName, opponentPerformance, opponentResult, opponentColor);
    }

    private void setRunnerTextColor(TextView t1, TextView t2, TextView t3, int color) {
        t1.setTextColor(color);
        t2.setTextColor(color);
        t3.setTextColor(color);
    }


    private void setupButtonUI(View view) {
        Button runHistoryButton = (Button) view.findViewById(R.id.button_history);
        runHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listener != null) {
                    listener.onDisplayChallengeFragmentInteraction();
                }
            }
        });

        final DBHelper dbHelper = new DBHelper(this.getContext());

        Button deleteRunButton = (Button) view.findViewById(R.id.button_delete);
        deleteRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    dbHelper.delete(challengeToBeDisplayed);
                    listener.onDisplayChallengeFragmentInteraction();
                }
            }
        });
    }

    private void setupMapUI(View view, Bundle savedInstanceState) {
        currentMapType = MapType.USER_MAP;
        mapView = (MapView) view.findViewById(R.id.user_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        opponentMapView = (MapView) view.findViewById(R.id.opponent_map);
        opponentMapView.onCreate(savedInstanceState);
    }

    /**
     * Called when the <code>GoogleMap</code> is ready. Initialize a MapHandler.
     *
     * @param googleMap     the <code>GoogleMap</code>
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapStyleOptions mapStyle = MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style_no_label);

        switch(currentMapType) {

            case USER_MAP:
                Track userTrack = challengeToBeDisplayed.getMyRun().getTrack();
                googleMap.setMapStyle(mapStyle);

                UtilsUI.recapDisplayTrack(userTrack, googleMap, userColor);
                UtilsUI.recapDisplayTrackSetupUI(googleMap);

                currentMapType = MapType.OPPONENT_MAP;
                opponentMapView.getMapAsync(this);
                break;
            case OPPONENT_MAP:
                Track opponentTrack = challengeToBeDisplayed.getOpponentRun().getTrack();
                googleMap.setMapStyle(mapStyle);

                UtilsUI.recapDisplayTrack(opponentTrack, googleMap, opponentColor);
                UtilsUI.recapDisplayTrackSetupUI(googleMap);
                break;
            default:
                throw new IllegalStateException("unknown map type");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDisplayChallengeFragmentInteractionListener) {
            listener = (OnDisplayChallengeFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDisplayChallengeFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        opponentMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        opponentMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        opponentMapView.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /**
     * interface for the listener of this class.
     */
    public interface OnDisplayChallengeFragmentInteractionListener {
        void onDisplayChallengeFragmentInteraction();
    }
}
