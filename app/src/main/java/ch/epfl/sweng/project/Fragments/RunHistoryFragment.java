package ch.epfl.sweng.project.Fragments;

import android.support.design.widget.TabLayout;
import android.support.v4.app.ListFragment;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.support.v7.app.AppCompatActivity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

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
public class RunHistoryFragment extends Fragment {

    private onRunHistoryInteractionListener mListener;
    private List<Run> runs;
    private List<Challenge> challenges;
    private ArrayAdapter<String> runsAdapter;
    private ArrayAdapter<String> challengesAdapter;
    private TabLayout tabLayout;
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

        listView = (ListView) view.findViewById(R.id.list);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setTabTextColors(ContextCompat.getColor(getContext(), R.color.wonColor), ContextCompat.getColor(getContext(), R.color.wonColor));


        TabLayout.Tab runTab = tabLayout.newTab();
        runTab.setText("Run History");
        tabLayout.addTab(runTab, true);

        TabLayout.Tab challengeTab = tabLayout.newTab();
        challengeTab.setText("Challenge History");
        tabLayout.addTab(challengeTab);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch(tab.getPosition()) {

                    case 0:
                        switchDisplayedListView(RunType.SINGLE_RUN);
                        break;
                    case 1:
                        switchDisplayedListView(RunType.CHALLENGE);
                        break;
                    default:
                        throw new IllegalStateException("Illegal RunType value");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        DBHelper dbHelper = new DBHelper(this.getContext());

        runs = dbHelper.fetchAllRuns();
        runsAdapter = createAdapter(RunType.SINGLE_RUN);

        challenges = dbHelper.fetchAllChallenges();
        challengesAdapter = createAdapter(RunType.CHALLENGE);

        switchDisplayedListView(RunType.SINGLE_RUN);

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
                        toBeAdapted[i] = "vs " + challenges.get(i).getOpponentName() + "  " + determineWOrLString(challenges.get(i));
                    }
                }
                break;
            default:
                throw new IllegalStateException("Illegal runType state.");
        }

        return new ArrayAdapter<String>(this.getContext(), R.layout.simple_textview, toBeAdapted);
    }

    private String determineWOrLString(Challenge challenge){

        String result;
        if(challenge.isWon()){
            result = "WON";
        }
        else{
            result = "LOST";
        }

        return result;
    }
    public interface onRunHistoryInteractionListener {
        void onRunHistoryInteraction(Run run);
        void onChallengeHistoryInteraction(Challenge challenge);
    }
}
