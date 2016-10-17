package ch.epfl.sweng.project.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RunHistoryFragment.RunHistoryInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RunHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RunHistoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // cambiare nome qui
    private RunHistoryInteractionListener mListener;

    public RunHistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RunHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RunHistoryFragment newInstance(String param1, String param2) {
        RunHistoryFragment fragment = new RunHistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    //@Override
    //public void onCreate(Bundle savedInstanceState) {
    //    super.onCreate(savedInstanceState);
    //    if (getArguments() != null) {
    //        mParam1 = getArguments().getString(ARG_PARAM1);
    //        mParam2 = getArguments().getString(ARG_PARAM2);
    //    }
    //}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_run_history, container, false);

        //implement your code here

       // DBAdapter dbAdapter = new dbAdapter(this.getContext());
       // Cursor cursor = dbAdapter.fetchAllContacts();

      //  cursor.moveToFirst();

        TableLayout table = (TableLayout) view.findViewById(R.id.table);

        TableRow firstRow = new TableRow(this.getContext());

        String[] columnNames = {"rosso", "blu", "giallo"};
                //cursor.getColumnNames();

        for(int i = 0; i < columnNames.length;
                //cursor.getColumnCount();
                 ++i){

            TextView columnTitle = new TextView(this.getContext());
            columnTitle.setText(columnNames[i]);
            firstRow.addView(columnTitle);
        }
         table.addView(firstRow);


    //    while(!cursor.isAfterLast()){

    //        TableRow nextRow = new TableRow(this.getContext());

    //        for(int i = 0; i < cursor.getColumnCount(); ++i){

    //            TextView nextElement = new TextView(this.getContext());
    //            nextElement.setText(cursor.getString(i));
    //            nextRow.addView(nextElement);
    //        }

    //        table.addView(nextRow);
    //    }

     //   dbAdapter.close();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onRunHistoryInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RunHistoryInteractionListener) {
            mListener = (RunHistoryInteractionListener) context;
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
    public interface RunHistoryInteractionListener {
        // TODO: Update argument type and name
        //cambiare coerentemente
        void onRunHistoryInteraction(Uri uri);
    }
}
