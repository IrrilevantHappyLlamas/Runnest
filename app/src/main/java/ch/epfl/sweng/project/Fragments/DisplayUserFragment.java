package ch.epfl.sweng.project.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import ch.epfl.sweng.project.Model.Track;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_display_user, container, false);

             TableLayout table = (TableLayout) view.findViewById(R.id.table);

            if (mId != null && mName != null) {



                // Email Row
                TableRow firstRow = new TableRow(this.getContext());
                createRowElement(firstRow, "Name :");
                createRowElement(firstRow, mId);
                table.addView(firstRow);

                // Name Row
                TableRow secondRow = new TableRow(this.getContext());
                createRowElement(secondRow, "Email :");
                createRowElement(secondRow, mName);
                table.addView(secondRow);

            }
        else{


                // No User found Row
                TableRow firstRow = new TableRow(this.getContext());
                createRowElement(firstRow, "No user found.");
                table.addView(firstRow);
            }

            return view;
        }

    private void createRowElement(TableRow row, String text){

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams();
        layoutParams.setMargins(10, 50, 20, 10);
        TextView element = new TextView(this.getContext());
        element.setText(text);
        element.setTextSize(20);
        element.setLayoutParams(layoutParams);

        row.addView(element);
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

        void onDisplayUserFragmentInteraction();
    }
}
