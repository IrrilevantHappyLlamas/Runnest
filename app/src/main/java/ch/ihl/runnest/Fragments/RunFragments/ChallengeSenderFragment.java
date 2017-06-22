package ch.ihl.runnest.Fragments.RunFragments;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.multidex.ch.ihl.runnest.AppRunnest.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import java.util.Locale;

import ch.ihl.runnest.Activities.ChallengeActivity;
import ch.ihl.runnest.AppRunnest;
import ch.ihl.runnest.Model.CheckPoint;
import ch.ihl.runnest.Model.Run;

/**
 * This Fragment represent the "sender side" of a challenge, i.e. it handles the
 * progress done by the local user of the device. In order to do that it implements
 * RunFragment.
 *
 * In particular his Track is shown on a map, as well as the distance
 * he ran, from when challenge started until now.
 */
public class ChallengeSenderFragment extends RunFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_challenge_sender, container, false);

        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        googleApiClient = ((ChallengeActivity)getActivity()).getGoogleApiClient();
        locationSettingsHandler = ((ChallengeActivity)getActivity()).getLocationSettingsHandler();

        distance = (TextView) view.findViewById(R.id.sender_distance);

        String userName = ((AppRunnest)getActivity().getApplication()).getUser().getName();
        run = new Run(userName);

        return view;
    }

    /**
     * Getter for the current Run that is being recorded.
     *
     * @return              The current Run.
     */
    public Run getRun() {
        return new Run(run);
    }

    /**
     * Calling this methods stops the current local run.
     */
    public void endChallenge(){
        super.stopRun();
    }

    @Override
    protected void updateDisplayedDistance() {

        double distanceToShow = run.getTrack().getDistance()/1000.0;

        switch (((ChallengeActivity)getActivity()).getChallengeType()) {
            case TIME:
                break;
            case DISTANCE:
                double remainingDistance =  ((ChallengeActivity)getActivity()).getChallengeGoal() -
                        (run.getTrack().getDistance())/1000.0;

                if(remainingDistance <= 0.0) {
                    distanceToShow = 0.0;
                    endChallenge();
                    ((ChallengeActivity)getActivity()).imFinished();
                } else {
                    distanceToShow = remainingDistance;
                }
                break;
        }

        String distanceInKm = String.format(Locale.getDefault(), "%.2f", distanceToShow) + " " + getString(R.string.km);
        distance.setText(distanceInKm);
    }

    /**
     * Handle a location update.
     *
     * @param location      New Location.
     */
    @Override
    public void onLocationChanged(Location location) {
        // Argument check is delegated to superclass method
        super.onLocationChanged(location);
        ((ChallengeActivity)getActivity()).getChallengeProxy().putData(new CheckPoint(location));
        ((ChallengeActivity)getActivity()).updateIsWinning();
    }

    /**
     * Called when the GoogleMap is ready. Initialize a MapHandler.
     *
     * @param googleMap     The GoogleMap.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Argument check is delegated to superclass method
        super.onMapReady(googleMap);
        startRun();
    }
}