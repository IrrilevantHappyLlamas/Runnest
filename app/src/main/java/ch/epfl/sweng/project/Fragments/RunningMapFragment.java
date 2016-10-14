package ch.epfl.sweng.project.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.CameraPosition;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RunningMapFragment.RunningMapFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RunningMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RunningMapFragment extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    MapView mMapView;
    GoogleMap mMap;

    private RunningMapFragmentInteractionListener mListener;

    //initialize points that will be drawn in the map.
    private static final LatLng EPFL = new LatLng(46.51839579373851, 6.568311452865601);

    private static final LatLng LAUSANNE = new LatLng(46.533333, 6.633333);

    private static final LatLng BERN = new LatLng(46.95, 7.466667);

    private static final LatLng LUZERN = new LatLng(47.0833, 8.2667);

    private static final LatLng URI = new LatLng(46.8779, 8.6405);

    private static final LatLng BELLINZONA = new LatLng(46.183333, 9.016667);

    private static final LatLng LOCARNO = new LatLng(46.166667, 8.783333);

    private static final LatLng LUGANO = new LatLng(46.016667, 8.95);

    //the mutable polyline to be drawn
    private Polyline mMutablePolyline;

    // a new camera to visualize the polyline nicely
    public static final CameraPosition SWITZERLAND =
            new CameraPosition.Builder()
                    .target(LUZERN)
                    .zoom(7f)
                    .build();

    public RunningMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RunningMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RunningMapFragment newInstance(String param1, String param2) {
        RunningMapFragment fragment = new RunningMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_running_map, container, false);

        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this); //this is important

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onRunningMapFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RunningMapFragmentInteractionListener) {
            mListener = (RunningMapFragmentInteractionListener) context;
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

    /**
     * this method draws an hardcoded path on the given map that connects EPFL to
     * Lugano passing by several swiss cities.
     *
     * @param googleMap the map where the path will be represented.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Add a marker at the EPFL and move the camera.
        mMap.addMarker(new MarkerOptions().position(EPFL).title("You are here!"));

        //this line disables all user interaction with the map.
        mMap.getUiSettings().setAllGesturesEnabled(false);

        //this line creates the path to be shown on the map.
        PolylineOptions options = new PolylineOptions()
                .add(EPFL)
                .add(LAUSANNE)
                .add(BERN)
                .add(LUZERN)
                .add(URI)
                .add(BELLINZONA)
                .add(LOCARNO)
                .add(LUGANO);

        //this line represents the path on the map.
        mMutablePolyline = mMap.addPolyline(options.color(Color.BLUE));

        //this line moves the camera so that the path can be displayed nicely.
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(SWITZERLAND));
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
    public interface RunningMapFragmentInteractionListener {
        // TODO: Update argument type and name
        void onRunningMapFragmentInteraction(Uri uri);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}