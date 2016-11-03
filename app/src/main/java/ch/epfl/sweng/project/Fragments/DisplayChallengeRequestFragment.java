package ch.epfl.sweng.project.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ch.epfl.sweng.project.Firebase.FirebaseHelper;
import ch.epfl.sweng.project.Model.Message;
import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DisplayChallengeRequestFragment.OnDisplayChallengeRequestFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DisplayChallengeRequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayChallengeRequestFragment extends Fragment {

    private static final String ARG_MESSAGE = "message";
    private Message mMessage;
    private FirebaseHelper mFirebaseHelper = null;

    private OnDisplayChallengeRequestFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param message the message to be displayed.
     * @return A new instance of fragment DisplayChallengeRequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DisplayChallengeRequestFragment newInstance(Message message) {
        DisplayChallengeRequestFragment fragment = new DisplayChallengeRequestFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            mMessage = (Message) getArguments().getSerializable(ARG_MESSAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_display_challenge_request, container, false);

        mFirebaseHelper = new FirebaseHelper();

        TextView message = (TextView) view.findViewById(R.id.message);

        message.setText(mMessage.getFrom() + " wants to challenge you to a race!\n\n" + "What's your answer?!");
        message.setTextSize(30);

        Button accept = (Button) view.findViewById(R.id.accept);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mListener != null) {
                    //mFirebaseHelper.delete(mMessage);
                    mListener.onDisplayChallengeRequestFragmentInteraction(true);
                }
            }
        });

        Button decline = (Button) view.findViewById(R.id.decline);

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mListener != null) {
                    //mFirebaseHelper.delete(mMessage);
                    mListener.onDisplayChallengeRequestFragmentInteraction(false);
                }
            }
        });


        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDisplayChallengeRequestFragmentInteractionListener) {
            mListener = (OnDisplayChallengeRequestFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDisplayChallengeRequestFragmentInteractionListener");
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
    public interface OnDisplayChallengeRequestFragmentInteractionListener {
        void onDisplayChallengeRequestFragmentInteraction(boolean accepted);
    }
}
