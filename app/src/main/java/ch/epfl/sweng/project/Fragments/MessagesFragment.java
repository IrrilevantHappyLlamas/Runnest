package ch.epfl.sweng.project.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
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

/**
 * Fragment which serves as challenges tab, where challenges requests and schedules are shown
 *
 * @author Hakim Invernizzi
 */
public class MessagesFragment extends ListFragment {

    private String email;
    private List<Message> messages;
    private List<HashMap<String, String>> messagesIconAndSender;
    private MessagesFragmentInteractionListener listener;
    private final String[] mapKeys = {"icon", "sender"};
    private final int[] icons = {R.drawable.challenge_white,
            R.drawable.schedule_white,
            R.drawable.memo_white,
            R.drawable.empty_white};
    private final int[] viewIDs = {R.id.icon, R.id.sender};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.simple_listview, container, false);

        String realEmail = ((AppRunnest) getActivity().getApplication()).getUser().getEmail();
        email = FirebaseHelper.getFireBaseMail(realEmail);

        fetchMessages();

        return view;
    }

    /**
     * Fetches all messages the current user received and initialize global variables.
     */
    private void fetchMessages() {
        if (((AppRunnest)getActivity().getApplication()).getNetworkHandler().isConnected()) {
            new FirebaseHelper().fetchMessages(email, new FirebaseHelper.Handler() {
                @Override
                public void handleRetrievedMessages(List<Message> messages) {
                    MessagesFragment.this.messages = messages;
                    messagesIconAndSender = new ArrayList<>();
                    HashMap<String, String> senderAndIconMap = new HashMap<>();

                    if (messages.isEmpty()) {
                        senderAndIconMap.put(mapKeys[1], "No message received.");
                        senderAndIconMap.put(mapKeys[0], Integer.toString(icons[3]));
                        messagesIconAndSender.add(senderAndIconMap);
                    } else {
                        for (Message currentMessage : messages) {
                            senderAndIconMap = new HashMap<>();
                            senderAndIconMap.put(mapKeys[1], currentMessage.getSender());

                            switch (currentMessage.getType()) {
                                case CHALLENGE_REQUEST:
                                    senderAndIconMap.put(mapKeys[0], Integer.toString(icons[0]));
                                    break;
                                case SCHEDULE_REQUEST:
                                    senderAndIconMap.put(mapKeys[0], Integer.toString(icons[1]));
                                    break;
                                case MEMO:
                                    senderAndIconMap.put(mapKeys[0], Integer.toString(icons[2]));
                                    break;
                                default:
                                    throw new IllegalStateException("Unknown message type");
                            }
                            messagesIconAndSender.add(senderAndIconMap);
                        }
                    }
                    onCreateFollow();
                }
            });
        }
    }

    private void onCreateFollow() {
        SimpleAdapter listAdapter = new SimpleAdapter(this.getContext(),
                messagesIconAndSender,
                R.layout.messages_listview,
                mapKeys,
                viewIDs);
        this.setListAdapter(listAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MessagesFragmentInteractionListener) {
            listener = (MessagesFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MessagesFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (!messages.isEmpty()) {
            Message message = messages.get(position);
            switch (message.getType()) {
                case CHALLENGE_REQUEST:
                    listener.onMessagesFragmentInteraction(message);
                    break;
                case SCHEDULE_REQUEST:
                    listener.onMessagesFragmentScheduleRequestInteraction(message);
                    break;
                case MEMO:
                    listener.onMessagesFragmentMemoInteraction(message);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Interface for SideBarActivity
     */
    public interface MessagesFragmentInteractionListener {
        void onMessagesFragmentInteraction(Message message);
        void onMessagesFragmentScheduleRequestInteraction(Message message);
        void onMessagesFragmentMemoInteraction(Message message);
    }
}
