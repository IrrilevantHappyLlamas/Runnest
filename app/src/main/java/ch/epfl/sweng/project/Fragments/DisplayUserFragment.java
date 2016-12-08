package ch.epfl.sweng.project.Fragments;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

                mListener.onDisplayProfileFragmentInteraction(name, email);
            }
        });

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
        void onDisplayProfileFragmentInteraction(String name, String email);
    }
}
