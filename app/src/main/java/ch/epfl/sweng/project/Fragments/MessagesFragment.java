package ch.epfl.sweng.project.Fragments;

import android.support.v4.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.epfl.sweng.project.AppRunnest;
import ch.epfl.sweng.project.Firebase.FirebaseHelper;
import ch.epfl.sweng.project.Model.Message;

public class MessagesFragment extends ListFragment {

    private FirebaseHelper mFirebaseHelper = null;
    private String mEmail;
    private List<Message> mMessages;
    private List<HashMap<String, String>> mapToBeAdapted;
    private MessagesFragmentInteractionListener mListener;
    private String[] mapKeys = {"icon", "sender"};
    private int[] icons = {R.drawable.challenge_white, R.drawable.schedule_white, R.drawable.memo_white, R.drawable.empty_white};
    private int[] viewIDs = {R.id.icon, R.id.sender};

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
                    mapToBeAdapted = new ArrayList<HashMap<String, String>>();

                    HashMap<String, String> hashMap = new HashMap<String,String>();

                    if (size == 0) {
                        hashMap.put(mapKeys[1], "No message received.");
                        hashMap.put(mapKeys[0], Integer.toString(icons[3]));
                        mapToBeAdapted.add(hashMap);
                    } else {
                        for (int i = 0; i < size; ++i) {
                            Message currentMessage = messages.get(i);

                            hashMap = new HashMap<String,String>();
                            hashMap.put(mapKeys[1], currentMessage.getSender());

                            switch(currentMessage.getType()){
                                case CHALLENGE_REQUEST:
                                    hashMap.put(mapKeys[0], Integer.toString(icons[0]));
                                    break;
                                case SCHEDULE_REQUEST:
                                    hashMap.put(mapKeys[0], Integer.toString(icons[1]));
                                    break;
                                case MEMO:
                                    hashMap.put(mapKeys[0], Integer.toString(icons[2]));
                                    break;
                                default:
                                    throw new IllegalStateException("unknown message type");
                            }
                            mapToBeAdapted.add(hashMap);
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
        this.setListAdapter(new SimpleAdapter(this.getContext(), mapToBeAdapted, R.layout.messages_listview, mapKeys, viewIDs));
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
            switch(mMessages.get(position).getType()) {
                case CHALLENGE_REQUEST:
                    mListener.onMessagesFragmentInteraction(mMessages.get(position));
                    break;
                case SCHEDULE_REQUEST:
                    mListener.onMessagesFragmentScheduleRequestInteraction(mMessages.get(position));
                    break;
                case MEMO:
                    mListener.onMessagesFragmentMemoInteraction(mMessages.get(position));
                    break;
                default:
                    break;
            }
        }
    }

    public interface MessagesFragmentInteractionListener {
        void onMessagesFragmentInteraction(Message message);
        void onMessagesFragmentScheduleRequestInteraction(Message message);
        void onMessagesFragmentMemoInteraction(Message message);
    }
}
