package ch.epfl.sweng.project.Fragments.NewRun;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import ch.epfl.sweng.project.Model.CheckPoint;
import ch.epfl.sweng.project.Model.Track;

/**
 * This Fragment represent the "receiver side" of a challenge, i.e. it handles the
 * progress done by the opponent user.
 *
 * In particular his <code>Track</code> is shown on a map, as well as the distance
 * he ran, from when challenge started until now.
 */
public class ChallengeReceiverFragment extends Fragment implements OnMapReadyCallback {

    // Live stats
    private TextView mDistance = null;

    // Data storage
    private Track mTrack = null;

    // Map
    private MapView mMapView = null;
    private MapHandler mMapHandler = null;

    public ChallengeReceiverFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_challenge_receiver, container, false);

        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this); //this is important

        mTrack = new Track();

        mDistance = (TextView) view.findViewById(R.id.receiver_distance);
        updateDisplayedDistance();

        return view;
    }

    private void updateDisplayedDistance() {
        String distanceInKm = (int)(mTrack.getDistance()/100.0)/10.0
                + " "
                + getString(R.string.km);

        mDistance.setText(distanceInKm);
    }

    /**
     * Handle updates, through <code>CheckPoint</code> of the opponent performance.
     *
     * @param checkPoint    new <code>Checkpoint</code>
     */
    public void onNewData(CheckPoint checkPoint) {

        mMapHandler.updateMap(checkPoint);

        mTrack.add(checkPoint);
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