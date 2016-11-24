package ch.epfl.sweng.project.Fragments.RunFragments;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import ch.epfl.sweng.project.Activities.ChallengeActivity;
import ch.epfl.sweng.project.Model.CheckPoint;
import ch.epfl.sweng.project.Model.Run;

/**
 * This Fragment represent the "sender side" of a challenge, i.e. it handles the
 * progress done by the actual user of the device. In order to do that it implements
 * <code>RunFragment</code>.
 *
 * In particular his <code>Track</code> is shown on a map, as well as the distance
 * he ran, from when challenge started until now.
 */
public class ChallengeSenderFragment extends RunFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_challenge_sender, container, false);

        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this); //this is important

        //TODO
        // Location
        mGoogleApiClient = ((ChallengeActivity)getActivity()).getGoogleApiClient();
        mLocationSettingsHandler = ((ChallengeActivity)getActivity()).getLocationSettingsHandler();

        mDistance = (TextView) view.findViewById(R.id.sender_distance);

        mRun = new Run();
        mRun.start();

        return view;
    }

    @Override
    protected void updateDisplayedDistance() {

        double distanceToShow = (int)(mRun.getTrack().getDistance()/100.0)/10.0;

        switch (((ChallengeActivity)getActivity()).getChallengeType()) {
            case TIME:
                break;
            case DISTANCE:
                double remainingDistance =  ((ChallengeActivity)getActivity()).getChallengeGoal() -
                        (mRun.getTrack().getDistance())/1000.0;

                if(remainingDistance <= 0.0) {
                    mRun.stop();
                    distanceToShow = 0.0;
                    stopLocationUpdates();
                    ((ChallengeActivity)getActivity()).imFinished();
                } else {
                    distanceToShow = remainingDistance;
                }
                break;
        }

        String distanceInKm = distanceToShow + " " + getString(R.string.km);
        mDistance.setText(distanceInKm);
    }

    public Run getRun() {
        return new Run(mRun);
    }

    /**
     * Handle a location update.
     *
     * @param location      the new <code>Location</code>
     */
    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);

        //TODO
        ((ChallengeActivity)getActivity()).getChallengeProxy().putData(new CheckPoint(location));
    }

    /**
     * Called when the <code>GoogleMap</code> is ready. Initialize a MapHandler.
     *
     * @param googleMap     the <code>GoogleMap</code>
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);
        super.startRun();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}