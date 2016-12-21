package ch.epfl.sweng.project.Fragments.RunFragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MapStyleOptions;

import java.util.Locale;

import ch.epfl.sweng.project.Activities.ChallengeActivity;
import ch.epfl.sweng.project.Model.CheckPoint;
import ch.epfl.sweng.project.Model.Run;

/**
 * This Fragment represent the "receiver side" of a challenge, i.e. it handles the
 * progress done by the opponent user.
 *
 * In particular his Track is shown on a map, as well as the distance
 * he ran, from when challenge started until now.
 */
public class ChallengeReceiverFragment extends Fragment implements OnMapReadyCallback {

    // Last opponent update
    private final long TIME_BEFORE_NOTIFY_MISSING_UPDATES = 30000;
    private long lastUpdateTime;
    private TextView warningText = null;

    private Handler handler = null;
    private Runnable runnableCode = null;

    // Live stats
    private TextView distance = null;

    // Data storage
    private Run run = null;

    // Map
    private MapView mapView = null;
    private MapHandler mapHandler = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_challenge_receiver, container, false);

        warningText = (TextView) view.findViewById(R.id.warning_text);
        warningText.setVisibility(View.GONE);
        lastUpdateTime = SystemClock.elapsedRealtime();

        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        run = new Run(((ChallengeActivity)getActivity()).getOpponentName());
        run.start();

        distance = (TextView) view.findViewById(R.id.receiver_distance);
        updateDisplayedDistance();

        handler = new Handler();
        runnableCode = new Runnable() {
            @Override
            public void run() {
                updateWarningTextVisibility();
                handler.postDelayed(runnableCode, TIME_BEFORE_NOTIFY_MISSING_UPDATES);
            }
        };
        handler.post(runnableCode);

        return view;
    }

    private void updateWarningTextVisibility() {
        if ((SystemClock.elapsedRealtime() - lastUpdateTime) >= TIME_BEFORE_NOTIFY_MISSING_UPDATES) {
            warningText.setVisibility(View.VISIBLE);
        }
    }

    private void updateDisplayedDistance() {

        double distanceToShow = run.getTrack().getDistance()/1000;

        switch (((ChallengeActivity)getActivity()).getChallengeType()) {
            case TIME:
                break;
            case DISTANCE:
                distanceToShow = ((ChallengeActivity)getActivity()).getChallengeGoal() - distanceToShow;
                break;
        }

        String distanceInKm = String.format(Locale.getDefault(), "%.2f", distanceToShow) + " " + getString(R.string.km);
        distance.setText(distanceInKm);
    }

    /**
     * Getter for the current Run of the fragment, returns a copy of it to preserve encapsulation.
     *
     * @return  Run currently stored by the fragment.
     */
    public Run getRun() {
        return new Run(run);
    }

    /**
     * Stops the current Run.
     */
    public void stopRun() {
        run.stop();
    }

    /**
     * Handle CheckPoint updates from opponent's performance.
     *
     * @param checkPoint    New Checkpoint data, must be non null.
     */
    public void onNewData(CheckPoint checkPoint) {

        if (checkPoint == null) {
            throw new IllegalArgumentException("New checkpoints received can't be null");
        }

        lastUpdateTime = SystemClock.elapsedRealtime();
        warningText.setVisibility(View.GONE);

        mapHandler.updateMap(checkPoint);

        run.update(checkPoint);
        updateDisplayedDistance();
    }

    /**
     * Called when the GoogleMap is ready. Initializes a MapHandler.
     *
     * @param googleMap     The GoogleMap, must be non null.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (googleMap == null) {
            throw new IllegalArgumentException("The GoogleMap can't be null");
        }

        MapStyleOptions mapStyle = MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style_no_label);
        googleMap.setMapStyle(mapStyle);

        mapHandler = new MapHandler(googleMap, ContextCompat.getColor(getContext(), R.color.colorAccent));
        mapHandler.setupRunningMapUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}