package ch.ihl.runnest.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.android.multidex.ch.ihl.runnest.AppRunnest.R;

import java.util.List;

import ch.ihl.runnest.Database.DBHelper;
import ch.ihl.runnest.Model.Challenge;
import ch.ihl.runnest.Model.Run;

/**
 * Fragment which serves as run and challenge history tab, where past runs and challenges are displayed in a list
 */
public class HistoryFragment extends Fragment {

    private onRunHistoryInteractionListener listener;
    private List<Run> runs;
    private List<Challenge> challenges;
    private ListView listView;

    private enum RunType {
        SINGLE_RUN, CHALLENGE
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_run_history_fragment, container, false);

        listView = (ListView) view.findViewById(R.id.list);

        setTabLayout(view);

        DBHelper dbHelper = new DBHelper(this.getContext());
        runs = dbHelper.fetchAllRuns();
        challenges = dbHelper.fetchAllChallenges();

        switchDisplayedListView(RunType.SINGLE_RUN);

        return view;
    }

    private void setTabLayout(View view) {
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setTabTextColors(ContextCompat.getColor(getContext(), R.color.wonColor),
                ContextCompat.getColor(getContext(), R.color.wonColor));

        TabLayout.Tab runTab = tabLayout.newTab();
        runTab.setText("Runs");
        tabLayout.addTab(runTab, true);

        TabLayout.Tab challengeTab = tabLayout.newTab();
        challengeTab.setText("Challenges");
        tabLayout.addTab(challengeTab);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
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
    }

    private void switchDisplayedListView(RunType runType) {
        switch (runType) {
            case SINGLE_RUN:
                listView.setAdapter(createAdapter(runType));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (!runs.isEmpty()) {
                            listener.onRunHistoryInteraction(runs.get(position));
                        }
                    }
                });
                break;
            case CHALLENGE:
                listView.setAdapter(createAdapter(runType));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (!challenges.isEmpty()) {
                            listener.onChallengeHistoryInteraction(challenges.get(position));
                        }
                    }
                });
                break;
            default:
                throw new IllegalStateException("Illegal runType state.");
        }
    }

    private ArrayAdapter<String> createAdapter(RunType runType) {
        String[] toBeAdapted;

        switch (runType) {
            case SINGLE_RUN:
                if (runs.isEmpty()) {
                    toBeAdapted = new String[]{"No run recorded."};
                } else {
                    int runsSize = runs.size();
                    toBeAdapted = new String[runsSize];

                    for (int i = 0; i < runsSize; ++i) {
                        toBeAdapted[i] = runs.get(i).getName();
                    }
                }
                break;
            case CHALLENGE:
                if (challenges.isEmpty()) {
                    toBeAdapted = new String[]{"No challenge recorded."};
                } else {
                    int challengesSize = challenges.size();
                    toBeAdapted = new String[challengesSize];

                    for (int i = 0; i < challengesSize; ++i) {
                        Challenge challenge = challenges.get(i);
                        String wonOrLost = challenge.isWon() ? "WON" : "LOST";
                        toBeAdapted[i] = "vs " + challenge.getOpponentName() + "  " + wonOrLost;
                    }
                }
                break;
            default:
                throw new IllegalStateException("Illegal runType state.");
        }

        return new ArrayAdapter<>(this.getContext(), R.layout.simple_textview, toBeAdapted);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onRunHistoryInteractionListener) {
            listener = (onRunHistoryInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /**
     * Interface for SideBarActivity
     */
    public interface onRunHistoryInteractionListener {
        void onRunHistoryInteraction(Run run);
        void onChallengeHistoryInteraction(Challenge challenge);
    }
}
