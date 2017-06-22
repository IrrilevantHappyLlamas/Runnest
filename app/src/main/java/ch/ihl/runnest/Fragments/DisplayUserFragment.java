package ch.ihl.runnest.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.android.multidex.ch.ihl.runnest.AppRunnest.R;

import java.util.HashMap;
import java.util.Map;

import ch.ihl.runnest.Activities.SideBarActivity;

/**
 * This class displays a User.
 */
public class DisplayUserFragment extends Fragment {
    private Map<String, String> foundUsers;

    private OnDisplayUserFragmentInteractionListener listener;

    /**
     * creates a new instance of this class and initializes some fields
     * @param foundUsers the results of the user search.
     * @return an instance of this class.
     */
    public static DisplayUserFragment newInstance(Map<String, String> foundUsers) {
        DisplayUserFragment fragment = new DisplayUserFragment();
        fragment.foundUsers = new HashMap<>(foundUsers);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display_user, container, false);

        if (foundUsers.size() > 0) {
            for (Map.Entry<String, String> user : foundUsers.entrySet()) {
                displayFoundUser(view, user.getKey(), user.getValue());
            }
        }
        else {
            displayFoundUser(view, "No user found.", null);
        }

        return view;
    }

    private void displayFoundUser(final View view, final String name, final String email) {
        if (view == null || name == null || name.equals("")) {
            throw new IllegalArgumentException();
        }

        TableLayout table = (TableLayout) view.findViewById(R.id.table);
        TableRow tableRow = new TableRow(this.getContext());
        //tableRow.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        tableRow.setPadding(20, 20, 20, 5);

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams();
        layoutParams.setMargins(50, 40, 40, 40);

        TextView nameTextView = new TextView(this.getContext());
        nameTextView.setText(name);
        nameTextView.setTextSize(22);
        nameTextView.setTextColor(getResources().getColor(R.color.cast_expanded_controller_text_color));
        nameTextView.setLayoutParams(layoutParams);
        tableRow.addView(nameTextView);

        tableRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SideBarActivity) getActivity()).searchViewAsMenuItem.collapseActionView();
                listener.onDisplayProfileFragmentInteraction(name, email);
            }
        });

        table.addView(tableRow);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDisplayUserFragmentInteractionListener) {
            listener = (OnDisplayUserFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDisplayUserFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /**
     * interface for the listener of this class.
     */
    public interface OnDisplayUserFragmentInteractionListener {
        void onDisplayProfileFragmentInteraction(String name, String email);
    }
}
