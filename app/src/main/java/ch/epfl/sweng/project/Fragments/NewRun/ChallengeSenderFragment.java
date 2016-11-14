package ch.epfl.sweng.project.Fragments.NewRun;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.android.gms.maps.MapView;

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

    public void startChallenge() {
        super.startRun();
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);

        //TODO
        //getActivity().getChallengeProxy.putData(new CheckPoint(location))
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}