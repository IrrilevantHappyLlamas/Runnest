package ch.epfl.sweng.project.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChallengeFragment.OnChallengeFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChallengeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChallengeFragment extends Fragment {
    private static final String ARG_CHALLENGED_USER_NAME = "challengedUserName";
    private static final String ARG_CHALLENGED_USER_EMAIL = "challengedUserEmail";

    private String mChallengedUserName;
    private String mChallengedUserEmail;

    private OnChallengeFragmentInteractionListener mListener;

    public ChallengeFragment() {
        // Required empty public constructor
    }

    public static ChallengeFragment newInstance(String challengedUserName, String challengedUserEmail) {
        ChallengeFragment fragment = new ChallengeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHALLENGED_USER_NAME, challengedUserName);
        args.putString(ARG_CHALLENGED_USER_EMAIL, challengedUserEmail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mChallengedUserName = getArguments().getString(ARG_CHALLENGED_USER_NAME);
            mChallengedUserEmail = getArguments().getString(ARG_CHALLENGED_USER_EMAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_challenge, container, false);

        TextView waitChallengedUserTextView = (TextView) view.findViewById(R.id.waitChallengedUserTextView);
        waitChallengedUserTextView.setText("Waiting for " + mChallengedUserName + " ...");

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnChallengeFragmentInteractionListener) {
            mListener = (OnChallengeFragmentInteractionListener) context;
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

    public interface OnChallengeFragmentInteractionListener {
        void onChallengeFragmentInteraction();
    }
}
