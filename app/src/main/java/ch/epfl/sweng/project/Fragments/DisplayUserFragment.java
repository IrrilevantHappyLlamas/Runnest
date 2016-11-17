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

import ch.epfl.sweng.project.AppRunnest;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

import java.util.HashMap;
import java.util.Map;

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
    private Map<String, String> mFoundUsers;

    private OnDisplayUserFragmentInteractionListener mListener;

    public static DisplayUserFragment newInstance(Map<String, String> foundUsers) {
        DisplayUserFragment fragment = new DisplayUserFragment();
        fragment.mFoundUsers = new HashMap<>(foundUsers);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display_user, container, false);

        if (mFoundUsers.size() > 0) {
            for (Map.Entry<String, String> user : mFoundUsers.entrySet()) {
                displayFoundUser(view, user.getKey(), user.getValue());
            }
        } else {
            displayFoundUser(view, null, null);
        }

        return view;
    }

    private void displayFoundUser(View view, final String name, final String email) {
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
            challengeButton.setText(R.string.challenge);
            //challengeButton.setId(1);
            challengeButton.setLayoutParams(layoutParams);
            challengeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Send message
                    String from = ((AppRunnest) getActivity().getApplication()).getUser().getEmail();
                    String to = FirebaseHelper.getFireBaseMail(email);
                    String sender = ((AppRunnest) getActivity().getApplication()).getUser().getName();
                    String message = "Run with me!";
                    Message challengeRequestMessage = new Message(from, to, sender, name, Message.MessageType.CHALLENGE_REQUEST, message);

                    FirebaseHelper firebaseHelper = new FirebaseHelper();
                    firebaseHelper.send(challengeRequestMessage);

                    // Go to ChallengeFragment
                    mListener.onDisplayUserFragmentInteraction(name, email);
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
