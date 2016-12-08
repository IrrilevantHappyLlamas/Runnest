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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ch.epfl.sweng.project.Database.DBHelper;
import ch.epfl.sweng.project.Model.CheckPoint;
import ch.epfl.sweng.project.Model.Run;
import ch.epfl.sweng.project.Model.Track;

public class DisplayRunFragment extends Fragment implements OnMapReadyCallback {

    private static final String RUN_TO_BE_DISPLAYED = "run to be displayed";
    private DisplayRunFragmentInteractionListener mListener;
    private Run mRunToBeDisplayed;

    // Map
    private MapView mMapView = null;
    private GoogleMap mGoogleMap;

    public static DisplayRunFragment newInstance(Run runToBeDisplayed) {
        DisplayRunFragment fragment = new DisplayRunFragment();
        Bundle args = new Bundle();
        args.putSerializable(RUN_TO_BE_DISPLAYED, runToBeDisplayed);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRunToBeDisplayed = (Run) getArguments().getSerializable(RUN_TO_BE_DISPLAYED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_display_run, container, false);

        setupMapUI(view, savedInstanceState);
        if (mRunToBeDisplayed != null) {
            setupTextUI(view);
        }
        setupButtonUI(view);

        return view;
    }

    private void setupMapUI(View view, Bundle savedInstanceState) {
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
    }

    private void setupTextUI(View view) {
        String name = mRunToBeDisplayed.getName();
        TextView challengeName = ((TextView)view.findViewById(R.id.challenge_name));
        challengeName.setText(name);

        int duration = (int)mRunToBeDisplayed.getDuration();
        TextView viewDuration = ((TextView)view.findViewById(R.id.duration_value));
        viewDuration.setText(timeToString(duration, true));

        double distance = mRunToBeDisplayed.getTrack().getDistance()/1000;
        TextView viewDistance = ((TextView)view.findViewById(R.id.distance_value));
        viewDistance.setText(String.format(Locale.getDefault(), "%.2f", distance) +
                getString(R.string.white_space) + getString(R.string.km));

        int avgPace;
        if(distance == 0) {
            avgPace = 0;
        } else {
            avgPace = (int)(duration/distance);
        }
        TextView viewAvgPace = ((TextView)view.findViewById(R.id.avg_pace_value));
        viewAvgPace.setText(timeToString(avgPace, false) +
                getString(R.string.white_space) + getString(R.string.min_over_km));
    }

    private String timeToString(int time, boolean showHours) {
        String toDisplay = "";

        if(showHours || time >= 3600) {
            toDisplay += String.format(Locale.getDefault(), "%02d:", TimeUnit.SECONDS.toHours(time));
        }

        toDisplay += String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.SECONDS.toMinutes(time) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(time)),
                TimeUnit.SECONDS.toSeconds(time) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(time)));

        return toDisplay;
    }

    private void setupButtonUI(View view) {
        Button button = (Button) view.findViewById(R.id.button_history);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mListener != null) {
                    mListener.onDisplayRunFragmentInteraction();
                }
            }
        });

        final DBHelper dbHelper = new DBHelper(this.getContext());

        Button deleteRunButton = (Button) view.findViewById(R.id.button_delete);
        deleteRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    dbHelper.delete(mRunToBeDisplayed);
                    mListener.onDisplayRunFragmentInteraction();
                }
            }
        });
    }


    /**
     * Called when the <code>GoogleMap</code> is ready. Initialize a MapHandler.
     *
     * @param googleMap     the <code>GoogleMap</code>
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        MapStyleOptions mapStyle = MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style_no_label);
        googleMap.setMapStyle(mapStyle);

        displayTrackSetupUI();
        displayTrack();
    }

    private void displayTrackSetupUI() {
        mGoogleMap.setBuildingsEnabled(false);
        mGoogleMap.setIndoorEnabled(false);
        mGoogleMap.setTrafficEnabled(false);

        UiSettings uiSettings = mGoogleMap.getUiSettings();

        uiSettings.setCompassEnabled(false);
        uiSettings.setIndoorLevelPickerEnabled(false);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
    }

    private void displayTrack() {

        Track track = mRunToBeDisplayed.getTrack();
        if(track.getTotalCheckPoints() != 0) {

            // Build polyline and LatLngBounds
            PolylineOptions polylineOptions = new PolylineOptions();
            List<CheckPoint> trackPoints = track.getCheckpoints();
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (CheckPoint checkPoint : trackPoints) {
                LatLng latLng = new LatLng(checkPoint.getLatitude(), checkPoint.getLongitude());
                polylineOptions.add(latLng);
                builder.include(latLng);
            }

            mGoogleMap.addPolyline(polylineOptions.color(ContextCompat.getColor(getContext(), R.color.colorAccent)));

            // Center camera on past run
            LatLngBounds bounds = builder.build();
            int padding = 40;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mGoogleMap.animateCamera(cameraUpdate);
        }
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DisplayRunFragmentInteractionListener) {
            mListener = (DisplayRunFragmentInteractionListener) context;
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public interface DisplayRunFragmentInteractionListener {
        void onDisplayRunFragmentInteraction();
    }
}