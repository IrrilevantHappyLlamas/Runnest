package ch.epfl.sweng.project.Fragments;

import android.support.v4.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

import java.util.List;

import ch.epfl.sweng.project.AppRunnest;
import ch.epfl.sweng.project.Firebase.FirebaseHelper;
import ch.epfl.sweng.project.Model.Message;

public class MessagesFragment extends ListFragment {

    private FirebaseHelper mFirebaseHelper = null;
    private String mEmail;
    private List<Message> mMessages;
    private String[] mMessageHeaders;

    private MessagesFragmentInteractionListener mListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.simple_listview, container, false);

        mFirebaseHelper = new FirebaseHelper();
        String realEmail = ((AppRunnest) getActivity().getApplication()).getUser().getEmail();
        mEmail = FirebaseHelper.getFireBaseMail(realEmail);

        fetchMessages();

        return view;
    }

    /**
     * Fetches all messages the current user received and initialize global variables.
     */
    private void fetchMessages() {
        if (((AppRunnest)getActivity().getApplication()).getNetworkHandler().isConnected()) {
            mFirebaseHelper.fetchMessages(mEmail, new FirebaseHelper.Handler() {
                @Override
                public void handleRetrievedMessages(List<Message> messages) {

                    int size = messages.size();
                    mMessages = messages;

                    if (size == 0) {
                        mMessageHeaders = new String[1];
                        mMessageHeaders[0] = "No message has been received yet";
                    } else {

                        mMessageHeaders = new String[size];

                        for (int i = 0; i < size; ++i) {
                            Message currentMessage = messages.get(i);

                            mMessageHeaders[i] = "From: " + currentMessage.getFrom() + "\n" + "Type: " + currentMessage.getType();
                        }
                    }

                    onCreateFollow();
                }
            });
        }
    }

    /**
     * This method is a follow-up of OnCreateView, taking care of the last settings.
     */
    public void onCreateFollow() {
        this.setListAdapter(new ArrayAdapter<>(this.getContext(), R.layout.simple_textview, mMessageHeaders));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MessagesFragmentInteractionListener) {
            mListener = (MessagesFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MessagesFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (!mMessages.isEmpty()) {
            mListener.onMessagesFragmentInteraction(mMessages.get(position));
        }
    }

    public interface MessagesFragmentInteractionListener {
        void onMessagesFragmentInteraction(Message message);
    }
}
