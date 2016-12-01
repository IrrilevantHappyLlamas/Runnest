package ch.epfl.sweng.project.Fragments.RunFragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.Locale;

import ch.epfl.sweng.project.Activities.ChallengeActivity;
import ch.epfl.sweng.project.Model.CheckPoint;
import ch.epfl.sweng.project.Model.Run;

/**
 * This Fragment represent the "receiver side" of a challenge, i.e. it handles the
 * progress done by the opponent user.
 *
 * In particular his <code>Track</code> is shown on a map, as well as the distance
 * he ran, from when challenge started until now.
 */
public class ChallengeReceiverFragment extends Fragment implements OnMapReadyCallback {

    // Last opponent update
    private final long TIME_BEFORE_NOTIFY_MISSING_UPDATES = 30000;
    private TextView warningText;
    private long lastUpdateTime;
    //TODO caps or not?
    private final Handler handler = new Handler();
    private final Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            updateWarningTextVisibility();
            handler.postDelayed(runnableCode, TIME_BEFORE_NOTIFY_MISSING_UPDATES);
        }
    };

    // Live stats
    private TextView mDistance = null;

    // Data storage
    private Run mRun = null;

    // Map
    private MapView mMapView = null;
    private MapHandler mMapHandler = null;

    /*
    public ChallengeReceiverFragment() {
        // Required empty public constructor
    }
    */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_challenge_receiver, container, false);

        warningText = (TextView) view.findViewById(R.id.warning_text);
        warningText.setVisibility(View.GONE);
        lastUpdateTime = SystemClock.elapsedRealtime();

        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        mRun = new Run(((ChallengeActivity)getActivity()).getOpponentName());
        mRun.start();

        mDistance = (TextView) view.findViewById(R.id.receiver_distance);
        updateDisplayedDistance();

        handler.post(runnableCode);

        return view;
    }

    private void updateWarningTextVisibility() {
        if ((SystemClock.elapsedRealtime() - lastUpdateTime) >= TIME_BEFORE_NOTIFY_MISSING_UPDATES) {
            warningText.setVisibility(View.VISIBLE);
        }
    }

    private void updateDisplayedDistance() {

        double distanceToShow = mRun.getTrack().getDistance()/1000.0;

        switch (((ChallengeActivity)getActivity()).getChallengeType()) {
            case TIME:
                break;
            case DISTANCE:
                distanceToShow = ((ChallengeActivity)getActivity()).getChallengeGoal() - distanceToShow;
                break;
        }

        String distanceInKm = String.format(Locale.getDefault(), "%.2f", distanceToShow) + " " + getString(R.string.km);
        mDistance.setText(distanceInKm);
    }

    public Run getRun() {
        return new Run(mRun);
    }

    public void stopRun() {
        mRun.stop();
    }

    /**
     * Handle updates, through <code>CheckPoint</code> of the opponent performance.
     *
     * @param checkPoint    new <code>Checkpoint</code>
     */
    public void onNewData(CheckPoint checkPoint) {

        lastUpdateTime = SystemClock.elapsedRealtime();
        warningText.setVisibility(View.GONE);

        mMapHandler.updateMap(checkPoint);

        mRun.update(checkPoint);
        updateDisplayedDistance();
    }

    /**
     * Called when the <code>GoogleMap</code> is ready. Initialize a MapHandler.
     *
     * @param googleMap     the <code>GoogleMap</code>
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMapHandler = new MapHandler(googleMap);
        mMapHandler.setupRunningMapUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}