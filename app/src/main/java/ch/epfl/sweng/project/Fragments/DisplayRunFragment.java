package ch.epfl.sweng.project.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import ch.epfl.sweng.project.AppRunnest;
import ch.epfl.sweng.project.Database.DBHelper;
import ch.epfl.sweng.project.Firebase.FirebaseHelper;
import ch.epfl.sweng.project.Model.Run;
import ch.epfl.sweng.project.Model.Track;
import ch.epfl.sweng.project.Model.CheckPoint;
import ch.epfl.sweng.project.Model.User;


import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class DisplayRunFragment extends Fragment implements OnMapReadyCallback {

    private static final String RUN_TO_BE_DISPLAYED = "run to be displayed";
    private DisplayRunFragmentInteractionListener mListener;
    private Run mRunToBeDisplayed;

    // Map
    private MapView mMapView = null;
    private GoogleMap mGoogleMap = null;

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

        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        if (mRunToBeDisplayed != null) {

            Track track = mRunToBeDisplayed.getTrack();

            TableLayout table = (TableLayout) view.findViewById(R.id.table);

            // Name Row
            TableRow firstRow = new TableRow(this.getContext());
            createRowElement(firstRow, "Name :");
            createRowElement(firstRow, mRunToBeDisplayed.getName());
            table.addView(firstRow);

            // Distance Row
            TableRow secondRow = new TableRow(this.getContext());
            createRowElement(secondRow, "Distance :");
            createRowElement(secondRow,String.valueOf((int)track.getDistance()) + " m");
            table.addView(secondRow);

            // Duration Row
            TableRow thirdRow = new TableRow(this.getContext());
            createRowElement(thirdRow, "Duration :");
            long minutes = mRunToBeDisplayed.getDuration() / 60;
            long seconds = mRunToBeDisplayed.getDuration() % 60;
            createRowElement(thirdRow, minutes + "' " + seconds + "''");
            table.addView(thirdRow);

            Button button = (Button) view.findViewById(R.id.go_to_run_history);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mListener != null) {
                        mListener.onDisplayRunFragmentInteraction();
                    }
                }
            });

            final DBHelper dbHelper = new DBHelper(this.getContext());

            Button deleteRunButton = (Button) view.findViewById(R.id.deleteRunButton);
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

        return view;
    }

    private void createRowElement(TableRow row, String text){

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams();
        layoutParams.setMargins(10, 50, 20, 10);
        TextView element = new TextView(this.getContext());
        element.setText(text);
        element.setTextSize(20);
        element.setLayoutParams(layoutParams);

        row.addView(element);
    }

    /**
     * Called when the <code>GoogleMap</code> is ready. Initialize a MapHandler.
     *
     * @param googleMap     the <code>GoogleMap</code>
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

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

            mGoogleMap.addPolyline(polylineOptions.color(Color.BLUE));

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