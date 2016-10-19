package ch.epfl.sweng.project.Fragments;

import android.support.v4.app.ListFragment;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import java.util.List;

import ch.epfl.sweng.project.Database.DBHelper;
import ch.epfl.sweng.project.Model.Run;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RunHistoryFragment.onRunHistoryInteractionListener} interface
 * to handle interaction events.
 */
public class RunHistoryFragment extends ListFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private onRunHistoryInteractionListener mListener;
    private List<Run> runs;

    public RunHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.simple_listview, container, false);


        DBHelper dbHelper = new DBHelper(this.getContext());

        runs = dbHelper.fetchAllEfforts();

        String[] runNames = null;

        if(runs.isEmpty()){

            runNames = new String[]{"No Run has been recorded yet."};
        }
        else {

            runNames = new String[runs.size()];

            for (int i = 0; i < runs.size(); ++i) {

                runNames[i] = runs.get(i).getName();
            }
        }
                this.setListAdapter(new ArrayAdapter<String>(this.getContext(), R.layout.simple_textview, runNames));

                return view;
        }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onRunHistoryInteractionListener) {
            mListener = (onRunHistoryInteractionListener) context;
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
    public void onListItemClick(ListView l, View v, int position, long id){

        if(!runs.isEmpty()){

            mListener.onRunHistoryInteraction(runs.get(position));
        }
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
    public interface onRunHistoryInteractionListener {

        void onRunHistoryInteraction(Run run);
    }
}
