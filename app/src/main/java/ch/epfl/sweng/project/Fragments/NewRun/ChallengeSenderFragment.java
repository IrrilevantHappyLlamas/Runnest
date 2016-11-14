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

    private ChallengeSenderFragment.ChallengeSenderFragmentInteractionListener mListener = null;

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

        // Buttons
        GUISetup(view);

        return view;
    }

    /**
     * Setup the two buttons of the fragment: Start and Stop.
     *
     * @param view <code>View</code> where buttons must be added
     */
    private void GUISetup(View view) {

        mDistance = (TextView) view.findViewById(R.id.distance);
        mDistance.setVisibility(View.INVISIBLE);
    }

    public void startChallenge() {
        super.startRun();
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);

        //TODO
        //putData(new CheckPoint(location))
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChallengeSenderFragment.ChallengeSenderFragmentInteractionListener) {
            mListener = (ChallengeSenderFragment.ChallengeSenderFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public interface ChallengeSenderFragmentInteractionListener {
        void ChallengeSenderFragmentInteraction();
    }
}