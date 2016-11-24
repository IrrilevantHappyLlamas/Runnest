package ch.epfl.sweng.project.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import ch.epfl.sweng.project.Database.DBHelper;
import ch.epfl.sweng.project.Model.Challenge;
import ch.epfl.sweng.project.Model.CheckPoint;
import ch.epfl.sweng.project.Model.Run;
import ch.epfl.sweng.project.Model.Track;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DisplayChallengeFragment.OnDisplayChallengeFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DisplayChallengeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayChallengeFragment extends Fragment implements OnMapReadyCallback {
    private static final String CHALLENGE_TO_BE_DISPLAYED = "challenge to be displayed";
    private DisplayChallengeFragment.OnDisplayChallengeFragmentInteractionListener mListener;
    private Challenge mChallengeToBeDisplayed;

    private MapView mMapView = null;
    private MapView mOpponentMapView = null;

    private GoogleMap mGoogleMap = null;
    private GoogleMap mOpponentGoogleMap = null;

    private Track mTrack = null;
    private Track mOpponentTrack = null;

    private MapType mCurrentMapType = null;

    public static DisplayChallengeFragment newInstance(Challenge challenge) {
        DisplayChallengeFragment fragment = new DisplayChallengeFragment();
        Bundle args = new Bundle();
        args.putSerializable(CHALLENGE_TO_BE_DISPLAYED, challenge);
        fragment.setArguments(args);
        return fragment;
    }

    private enum MapType {
        MyMap,
        MyOpponentMap
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mChallengeToBeDisplayed = (Challenge) getArguments().getSerializable(CHALLENGE_TO_BE_DISPLAYED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_display_challenge, container, false);

        if (mChallengeToBeDisplayed != null) {

            mTrack = mChallengeToBeDisplayed.getMyRun().getTrack();
            mOpponentTrack = mChallengeToBeDisplayed.getOpponentRun().getTrack();

            mCurrentMapType = MapType.MyMap;
            mMapView = (MapView) view.findViewById(R.id.myMapView);
            mMapView.onCreate(savedInstanceState);
            mMapView.getMapAsync(this);

            mCurrentMapType = MapType.MyOpponentMap;
            mOpponentMapView = (MapView) view.findViewById(R.id.myOpponentMapView);
            mOpponentMapView.onCreate(savedInstanceState);
            mOpponentMapView.getMapAsync(this);

            displayRunOnView(mChallengeToBeDisplayed.getMyRun(), view.findViewById(R.id.myTable));
            displayRunOnView(mChallengeToBeDisplayed.getOpponentRun(), view.findViewById(R.id.myOpponentTable));

            Button button = (Button) view.findViewById(R.id.go_to_run_history);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mListener != null) {
                        mListener.onDisplayChallengeFragmentInteraction();
                    }
                }
            });

            final DBHelper dbHelper = new DBHelper(this.getContext());

            Button deleteRunButton = (Button) view.findViewById(R.id.deleteRunButton);
            deleteRunButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        dbHelper.delete(mChallengeToBeDisplayed);
                        mListener.onDisplayChallengeFragmentInteraction();
                    }
                }
            });
        }

        return view;
    }

    private void displayRunOnView(Run run, View view){

        Track track = run.getTrack();
        TableLayout table = (TableLayout) view;

        // Name Row
        TableRow firstRow = new TableRow(this.getContext());
        createRowElement(firstRow, "Name :");
        createRowElement(firstRow, run.getName());
        table.addView(firstRow);

        // Distance Row
        TableRow secondRow = new TableRow(this.getContext());
        createRowElement(secondRow, "Distance :");
        createRowElement(secondRow,String.valueOf((int)track.getDistance()) + " m");
        table.addView(secondRow);

        // Duration Row
        TableRow thirdRow = new TableRow(this.getContext());
        createRowElement(thirdRow, "Duration :");
        long minutes = run.getDuration() / 60;
        long seconds = run.getDuration() % 60;
        createRowElement(thirdRow, minutes + "' " + seconds + "''");
        table.addView(thirdRow);
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

        switch(mCurrentMapType) {

            case MyMap:
                mGoogleMap = googleMap;
                displayTrackSetupUI(mGoogleMap);
                displayTrack(mTrack, mGoogleMap);
                break;
            case MyOpponentMap:
                mOpponentGoogleMap = googleMap;
                displayTrackSetupUI(mOpponentGoogleMap);
                displayTrack(mOpponentTrack, mOpponentGoogleMap);
                break;
            default:
                throw new IllegalStateException("unknown map type");
        }
    }

    private void displayTrackSetupUI(GoogleMap GoogleMap) {
        GoogleMap.setBuildingsEnabled(false);
        GoogleMap.setIndoorEnabled(false);
        GoogleMap.setTrafficEnabled(false);

        UiSettings uiSettings = GoogleMap.getUiSettings();

        uiSettings.setCompassEnabled(false);
        uiSettings.setIndoorLevelPickerEnabled(false);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
    }

    private void displayTrack(Track track, GoogleMap GoogleMap) {

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
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
        mOpponentMapView.onLowMemory();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDisplayChallengeFragmentInteractionListener) {
            mListener = (OnDisplayChallengeFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDisplayChallengeFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnDisplayChallengeFragmentInteractionListener {
        // TODO: Update argument type and name
        void onDisplayChallengeFragmentInteraction();
    }
}
