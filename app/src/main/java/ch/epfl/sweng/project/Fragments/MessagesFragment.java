package ch.epfl.sweng.project.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

import java.util.List;

import ch.epfl.sweng.project.AppRunnest;
import ch.epfl.sweng.project.Firebase.FirebaseHelper;
import ch.epfl.sweng.project.Model.Message;


public class MessagesFragment extends Fragment implements View.OnClickListener {


    private FirebaseHelper mFirebaseHelper = null;
    private String username = "you";

    private EditText messageEditText = null;
    private TextView fetchedMessages = null;

    private Button sendButton = null;
    private Button fetchButton = null;

    private MessagesFragmentInteractionListener mListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        mFirebaseHelper = new FirebaseHelper();
        messageEditText = (EditText) view.findViewById(R.id.messageEditText);
        fetchedMessages = (TextView) view.findViewById(R.id.retrievedMessagesTextView);
        sendButton = (Button) view.findViewById(R.id.sendMessageButton);
        fetchButton = (Button) view.findViewById(R.id.fetchMessagesButton);

        sendButton.setOnClickListener(this);
        fetchButton.setOnClickListener(this);

        fetchMessages();

        return view;
    }

    private void sendMessage() {

        if(((AppRunnest)getActivity().getApplication()).getNetworkHandler().isConnected()) {
            String message = messageEditText.getText().toString();
            if (!message.equals("")) {
                messageEditText.setText("");
                Message msg = new Message("me", username, Message.MessageType.TEXT, message);
                mFirebaseHelper.send(msg);
            }
        }
    }

    private void fetchMessages() {
        if(((AppRunnest)getActivity().getApplication()).getNetworkHandler().isConnected()) {
            mFirebaseHelper.fetchMessages(username, new FirebaseHelper.Handler() {
                @Override
                public void handleRetrievedMessages(List<Message> messages) {
                    String str = messages.size() + " messages recieved \n";
                    for (Message m : messages) {
                        str += "FROM: " + m.getFrom() + "\nRECIVED: " + m.getTime() + "\n";
                        str += "MSG: " + m.getMessage() + "\n\n";
                    }

                    fetchedMessages.setText(str);
                }
            });
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MessagesFragmentInteractionListener) {
            mListener = (MessagesFragmentInteractionListener) context;
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendMessageButton:
                    sendMessage();
                break;
            case R.id.fetchMessagesButton:
                    fetchMessages();
                break;
        }
    }

    /**
     * Interface for SideBarActivity
     */
    public interface MessagesFragmentInteractionListener {
        void onMessagesFragmentInteraction();
    }
}
