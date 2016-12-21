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
import ch.epfl.sweng.project.Model.Run;
import ch.epfl.sweng.project.UtilsUI;

/**
 * This class displays a Run.
 */
public class DisplayRunFragment extends Fragment implements OnMapReadyCallback {

    private static final String RUN_TO_BE_DISPLAYED = "run to be displayed";
    private DisplayRunFragmentInteractionListener listener;
    private Run runToBeDisplayed;

    // Map
    private MapView mapView = null;

    /**
     * creates an instance of this fragment and passes the given arguments to the fragment fields.
     * @param runToBeDisplayed the run to be displayed
     * @return an instance of DisplayRunFragment
     */
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
            runToBeDisplayed = (Run) getArguments().getSerializable(RUN_TO_BE_DISPLAYED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_display_run, container, false);

        setupMapUI(view, savedInstanceState);
        if (runToBeDisplayed != null) {
            setupTextUI(view);
        }
        setupButtonUI(view);

        return view;
    }

    private void setupMapUI(View view, Bundle savedInstanceState) {
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    private void setupTextUI(View view) {
        String name = runToBeDisplayed.getName();
        TextView challengeName = ((TextView)view.findViewById(R.id.challenge_name));
        challengeName.setText(name);

        int duration = (int) runToBeDisplayed.getDuration();
        TextView viewDuration = ((TextView)view.findViewById(R.id.duration_value));
        viewDuration.setText(UtilsUI.timeToString(duration, true));

        double distance = runToBeDisplayed.getTrack().getDistance()/1000;
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
        viewAvgPace.setText(UtilsUI.timeToString(avgPace, false) +
                getString(R.string.white_space) + getString(R.string.min_over_km));
    }

    private void setupButtonUI(View view) {
        Button button = (Button) view.findViewById(R.id.button_history);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listener != null) {
                    listener.onDisplayRunFragmentInteraction();
                }
            }
        });

        final DBHelper dbHelper = new DBHelper(this.getContext());

        Button deleteRunButton = (Button) view.findViewById(R.id.button_delete);
        deleteRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    dbHelper.delete(runToBeDisplayed);
                    listener.onDisplayRunFragmentInteraction();
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
        MapStyleOptions mapStyle = MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style_no_label);
        googleMap.setMapStyle(mapStyle);

        UtilsUI.recapDisplayTrackSetupUI(googleMap);
        UtilsUI.recapDisplayTrack(runToBeDisplayed.getTrack(), googleMap,
                ContextCompat.getColor(getContext(), R.color.colorAccent));
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DisplayRunFragmentInteractionListener) {
            listener = (DisplayRunFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDisplayRunFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    /**
     * interface for the listener of this class.
     */
    public interface DisplayRunFragmentInteractionListener {
        void onDisplayRunFragmentInteraction();
    }
}