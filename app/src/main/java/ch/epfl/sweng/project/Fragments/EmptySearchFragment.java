package ch.epfl.sweng.project.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EmptySearchFragment.OnEmptySearchFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EmptySearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmptySearchFragment extends Fragment {

    private OnEmptySearchFragmentInteractionListener mListener;

    public EmptySearchFragment() {
        // Required empty public constructor
    }

    public static EmptySearchFragment newInstance() {
        EmptySearchFragment fragment = new EmptySearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_empty_search, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEmptySearchFragmentInteractionListener) {
            mListener = (OnEmptySearchFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEmptySearchFragmentInteractionListener");
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
    public interface OnEmptySearchFragmentInteractionListener {
        void onEmptySearchFragmentInteraction();
    }
}
