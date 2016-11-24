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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import java.util.List;

import ch.epfl.sweng.project.Database.DBHelper;
import ch.epfl.sweng.project.Model.Challenge;
import ch.epfl.sweng.project.Model.Run;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RunHistoryFragment.onRunHistoryInteractionListener} interface
 * to handle interaction events.
 */
public class RunHistoryFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private onRunHistoryInteractionListener mListener;
    private List<Run> runs;
    private List<Challenge> challenges;
    private ArrayAdapter<String> runsAdapter;
    private ArrayAdapter<String> challengesAdapter;

    private ListView listView;

    private enum RunType {
        SINGLE_RUN, CHALLENGE
    }
    public RunHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_run_history_fragment, container, false);

        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.spinner_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        listView = (ListView) view.findViewById(R.id.list);

        DBHelper dbHelper = new DBHelper(this.getContext());

        runs = dbHelper.fetchAllRuns();
        runsAdapter = createAdapter(RunType.SINGLE_RUN);

        challenges = dbHelper.fetchAllChallenges();
        challengesAdapter = createAdapter(RunType.CHALLENGE);

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

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

        switch(((String) parent.getItemAtPosition(pos))) {
            case "Single Runs":
                switchDisplayedListView(RunType.SINGLE_RUN);
                break;
            case "Challenges":
                switchDisplayedListView(RunType.CHALLENGE);
                break;
            default:
                throw new IllegalStateException("Illegal spinner state.");
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
       switchDisplayedListView(RunType.SINGLE_RUN);
    }

    private void switchDisplayedListView(RunType runType){

        switch(runType) {

            case SINGLE_RUN:
                listView.setAdapter(runsAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (!runs.isEmpty()) {
                            mListener.onRunHistoryInteraction(runs.get(position));
                        }
                    }
                });
                break;
            case CHALLENGE:
                listView.setAdapter(challengesAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (!challenges.isEmpty()) {
                            mListener.onChallengeHistoryInteraction(challenges.get(position));
                        }
                    }
                });
                break;
            default:
                throw new IllegalStateException("Illegal runType state.");
        }
    }


    private ArrayAdapter<String> createAdapter(RunType runType){

        String[] toBeAdapted;

        switch(runType) {
            case SINGLE_RUN:
                if(runs.isEmpty()) {
                    toBeAdapted = new String[]{"No run recorded."};
                }
                else{
                    toBeAdapted = new String[runs.size()];

                    for (int i = 0; i < runs.size(); ++i) {
                        toBeAdapted[i] = runs.get(i).getName();
                    }
                }
                break;
            case CHALLENGE:
                if(challenges.isEmpty()) {
                    toBeAdapted = new String[]{"No challenge recorded."};
                }
                else{
                    toBeAdapted = new String[challenges.size()];

                    for (int i = 0; i < challenges.size(); ++i) {
                        toBeAdapted[i] = String.valueOf(challenges.get(i).getId());
                    }
                }
                break;
            default:
                throw new IllegalStateException("Illegal runType state.");
        }

        return new ArrayAdapter<String>(this.getContext(), R.layout.simple_textview, toBeAdapted);
    }
    public interface onRunHistoryInteractionListener {
        void onRunHistoryInteraction(Run run);
        void onChallengeHistoryInteraction(Challenge challenge);
    }
}
