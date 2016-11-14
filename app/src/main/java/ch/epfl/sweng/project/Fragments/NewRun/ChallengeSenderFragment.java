package ch.epfl.sweng.project.Fragments.NewRun;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.android.gms.maps.MapView;

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
        View view =  inflater.inflate(R.layout.fragment_running_map, container, false);

        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this); //this is important

        //TODO
        // Location
        //mGoogleApiClient = getActivity().getGoogleApiClient();
        //mLocationSettingsHandler = getActivity().getLocationSettingsHandler();

        mDistance = (TextView) view.findViewById(R.id.distance);

        return view;
    }

    /**
     * Perform all necessary action to properly start the run, by calling the super
     * class <code>startRun()</code> method.
     */
    public void startChallenge() {
        super.startRun();
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
        //getActivity().getChallengeProxy.putData(new CheckPoint(location))
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}