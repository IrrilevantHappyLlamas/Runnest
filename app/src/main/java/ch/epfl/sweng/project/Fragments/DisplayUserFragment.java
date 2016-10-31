package ch.epfl.sweng.project.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

import ch.epfl.sweng.project.Firebase.FirebaseHelper;
import ch.epfl.sweng.project.Model.Message;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DisplayUserFragment.OnDisplayUserFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DisplayUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayUserFragment extends Fragment {

    private static final String ARG_ID = "id";
    private static final String ARG_NAME = "name";

    private String mId;
    private String mName;

    private OnDisplayUserFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id Parameter 1.
     * @param name Parameter 2.
     * @return A new instance of fragment DisplayUserFragment.
     */
    public static DisplayUserFragment newInstance(String id, String name) {
        DisplayUserFragment fragment = new DisplayUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, id);
        args.putString(ARG_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mId = getArguments().getString(ARG_ID);
            mName = getArguments().getString(ARG_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display_user, container, false);
        TableLayout table = (TableLayout) view.findViewById(R.id.table);

        displayFoundUser(view, mId, mName);

        return view;
    }

    private void displayFoundUser(View view, String name, String email) {
        TableLayout table = (TableLayout) view.findViewById(R.id.table);
        TableRow tableRow = new TableRow(this.getContext());
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams();
        layoutParams.setMargins(10, 50, 20, 10);

        String text = "No user found.";
        Boolean noUserFound = true;
        if (name != null && email != null) {
            text = name + "\n" + email;
            noUserFound = false;
        }

        TextView nameAndEmailTextView = new TextView(this.getContext());
        nameAndEmailTextView.setText(text);
        nameAndEmailTextView.setTextSize(18);
        nameAndEmailTextView.setLayoutParams(layoutParams);
        tableRow.addView(nameAndEmailTextView);

        if (!noUserFound) {
            Button challengeButton = new Button(this.getContext());
            challengeButton.setText("Challenge!");
            challengeButton.setLayoutParams(layoutParams);
            challengeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Send message
                    // TODO: make profile a SideBarActivity attribute so that it can be accessible here to set the from
                    String from = "me";
                    // TODO: children names in firebase can't contain . and @
                    String to = "challengee";//mName;
                    String message = "Run with me!";
                    Message challengeRequestMessage = new Message(from, to, Message.MessageType.CHALLENGE_REQUEST, message);

                    FirebaseHelper firebaseHelper = new FirebaseHelper();
                    firebaseHelper.send(challengeRequestMessage);

                    // Go to ChallengeFragment
                    mListener.onDisplayUserFragmentInteraction(mId, mName);
                }
            });

            tableRow.addView(challengeButton);
        }

        table.addView(tableRow);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDisplayUserFragmentInteractionListener) {
            mListener = (OnDisplayUserFragmentInteractionListener) context;
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


    public interface OnDisplayUserFragmentInteractionListener {
        void onDisplayUserFragmentInteraction(String challengedUserName, String challengedUserEmail);
    }
}
