package ch.epfl.sweng.project.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

import ch.epfl.sweng.project.AppRunnest;
import ch.epfl.sweng.project.Database.DBHelper;
import ch.epfl.sweng.project.Firebase.FirebaseHelper;
import ch.epfl.sweng.project.Firebase.FirebaseProxy;
import ch.epfl.sweng.project.Fragments.ReceiveScheduleDialogFragment;
import ch.epfl.sweng.project.Fragments.sendChallengeDialogFragment;
import ch.epfl.sweng.project.Fragments.DBDownloadFragment;
import ch.epfl.sweng.project.Fragments.DisplayChallengeFragment;
import ch.epfl.sweng.project.Fragments.DisplayRunFragment;
import ch.epfl.sweng.project.Fragments.DisplayUserFragment;
import ch.epfl.sweng.project.Fragments.EmptySearchFragment;
import ch.epfl.sweng.project.Fragments.MemoDialogFragment;
import ch.epfl.sweng.project.Fragments.MessagesFragment;
import ch.epfl.sweng.project.Fragments.ProfileFragment;
import ch.epfl.sweng.project.Fragments.ReceiveChallengeDialogFragment;
import ch.epfl.sweng.project.Fragments.SendScheduleDialogFragment;
import ch.epfl.sweng.project.Fragments.RunFragments.RunningMapFragment;
import ch.epfl.sweng.project.Fragments.HistoryFragment;
import ch.epfl.sweng.project.Model.Challenge;
import ch.epfl.sweng.project.Model.Message;
import ch.epfl.sweng.project.Model.Run;
import ch.epfl.sweng.project.Model.User;

public class SideBarActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ProfileFragment.ProfileFragmentInteractionListener,
        RunningMapFragment.RunningMapFragmentInteractionListener,
        DBDownloadFragment.DBDownloadFragmentInteractionListener,
        HistoryFragment.onRunHistoryInteractionListener,
        DisplayUserFragment.OnDisplayUserFragmentInteractionListener,
        MessagesFragment.MessagesFragmentInteractionListener,
        DisplayRunFragment.DisplayRunFragmentInteractionListener,
        sendChallengeDialogFragment.SendChallengeDialogListener,
        SendScheduleDialogFragment.SendScheduleDialogListener,
        ReceiveScheduleDialogFragment.ReceiveScheduleDialogListener,
        MemoDialogFragment.MemoDialogListener,
        ReceiveChallengeDialogFragment.ReceiveChallengeDialogListener,
        DisplayChallengeFragment.OnDisplayChallengeFragmentInteractionListener
{

    public static final int PERMISSION_REQUEST_CODE_FINE_LOCATION = 1;
    public static final int REQUEST_STOP_WAITING = 2;
    public static final int REQUEST_ABORT = 3;
    public static final int REQUEST_END_CHALLENGE = 4;

    //Item stack(LIFO)
    private Stack<MenuItem> itemStack = new Stack<>();
    private MenuItem profileItem;
    private MenuItem runItem;
    private MenuItem historyItem;

    private Fragment mCurrentFragment = null;
    private FragmentManager fragmentManager = null;
    private SearchView mSearchView = null;
    public MenuItem mSearchViewAsMenuItem = null;

    private FirebaseHelper mFirebaseHelper = null;

    private NavigationView navigationView;

    private Boolean isRunning = false;

    private Toolbar toolbar;

    private int nbrMessages = 0;
    private String mEmail;
    private Handler handler = new Handler();
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            checkNbrMessages();
            System.out.println(nbrMessages);
            handler.postDelayed(runnableCode, 10000);
        }
    };

    private String challengedUserName = "no Name";
    private String challengedUserEmail = "no eMail";
    private Message requestMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_bar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize database
        mFirebaseHelper = new FirebaseHelper();
        String realEmail = ((AppRunnest) getApplication()).getUser().getEmail();
        mEmail = FirebaseHelper.getFireBaseMail(realEmail);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //noinspection deprecation
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView h1 = (TextView) header.findViewById(R.id.header1_nav_header);
        final TextView h2 = (TextView) header.findViewById(R.id.header2_nav_header);

        runItem = navigationView.getMenu().getItem(1);


        User account = ((AppRunnest)getApplicationContext()).getUser();
        if (account != null) {
            h1.setText(account.getName());

            mFirebaseHelper.getUserStatistics(account.getEmail(), new FirebaseHelper.statisticsHandler() {
                @Override
                public void handleRetrievedStatistics(String[] statistics) {
                    String nbRuns = statistics[mFirebaseHelper.TOTAL_NUMBER_OF_RUNS_INDEX];
                    String header2Txt = nbRuns + " runs";
                    h2.setText(header2Txt);
                }
            });
        }

        //Initializing the fragment
        fragmentManager = getSupportFragmentManager();
        mCurrentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

        profileItem = navigationView.getMenu().getItem(0);
        historyItem = navigationView.getMenu().getItem(2);
        profileItem.setChecked(true);
        itemStack.push(profileItem);

        if(mCurrentFragment == null){
            mCurrentFragment = new DBDownloadFragment();
            fragmentManager.beginTransaction().add(R.id.fragment_container, mCurrentFragment).commit();
        }

        handler.post(runnableCode);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            itemStack.pop();
            if(!itemStack.isEmpty()){
                itemStack.peek().setChecked(true);
                onNavigationItemSelected(itemStack.peek());
            } else {
                profileItem.setChecked(true);
                onNavigationItemSelected(profileItem);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.side_bar, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.search).getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        mSearchView.setIconifiedByDefault(true);
        mSearchView.setFocusable(true);
        mSearchView.setIconified(false);
        mSearchView.requestFocusFromTouch();
        mSearchView.clearFocus();


        //get the searchBar as a MenuItem (as opposed to as a SearchView)
        mSearchViewAsMenuItem = menu.findItem(R.id.search);

        //define the behaviour of the searchBar, i.e.
        // that when the searchBar collapses, you go back to the fragment from which you opened it,
        //and that when you open the searchBar you go to a empty fragment.
        MenuItemCompat.setOnActionExpandListener(mSearchViewAsMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                launchFragment(new EmptySearchFragment());
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // this line is very important, without it the previous fragment isn't displayed correctly
                //maybe because of synchrony problems.
                mSearchView.setQuery("", false);

                onNavigationItemSelected(itemStack.peek());
                return true;
            }
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(final String query) {
                return findUsers(query);
            }

            @Override
            public boolean onQueryTextChange(String newText){
                if (!newText.equals("")) {
                    return findUsers(newText);
                }
                else {
                    launchFragment(new EmptySearchFragment());
                    return true;
                }
            }
        });

        return true;

    }

    private Boolean findUsers(final String query) {
        if (((AppRunnest)getApplication()).getNetworkHandler().isConnected()) {
            mFirebaseHelper.getDatabase().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Map<String, String> users = new HashMap<>();
                        for (DataSnapshot user : dataSnapshot.getChildren()) {
                            String usersName = user.child("name").getValue().toString();
                            String usersEmail = user.getKey();
                            String[] surnameAndFamilyName = usersName.split(" ");
                            String surname = surnameAndFamilyName[0].toLowerCase();
                            String familyName = "";
                            if (surnameAndFamilyName.length > 1) {
                                familyName = surnameAndFamilyName[1].toLowerCase();
                            }
                            
                            String myEmail = FirebaseHelper
                                    .getFireBaseMail(((AppRunnest) getApplication())
                                    .getUser()
                                    .getEmail());

                            String lowerCaseQuery = query.toLowerCase();
                            if ((usersName.toLowerCase().startsWith(lowerCaseQuery)
                                    || surname.startsWith(lowerCaseQuery)
                                    || familyName.startsWith(lowerCaseQuery)
                                    || usersEmail.toLowerCase().startsWith(lowerCaseQuery))
                                    && !usersEmail.equals(myEmail))
                            {
                                users.put(usersName, usersEmail);
                            }
                        }
                        launchFragment(DisplayUserFragment.newInstance(users));
                    } else {
                        launchFragment(DisplayUserFragment.newInstance(new HashMap<String, String>()));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (isRunning && !item.equals(runItem)) {
            dialogQuitRun(item);
            return false;
        }

        if (!(isRunning && item.equals(runItem))) {

            if (itemStack.isEmpty() || !itemStack.peek().equals(item)) {
                itemStack.push(item);
            }

            showSearchBar();

            // Handle navigation view item clicks here.
            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                toolbar.setTitle("Profile");
                launchFragment(new ProfileFragment());
            } else if (id == R.id.nav_run) {
                toolbar.setTitle("Run");
                hideSearchBar();
                launchFragment(new RunningMapFragment());
            } else if (id == R.id.nav_new_challenge) {
                toolbar.setTitle("Search someone");
                launchFragment(new EmptySearchFragment());

                mSearchView.setQueryHint("Search someone");
                mSearchViewAsMenuItem.expandActionView();
            } else if (id == R.id.nav_messages) {
                toolbar.setTitle("Challenges");
                launchFragment(new MessagesFragment());
            } else if (id == R.id.nav_history) {
                toolbar.setTitle("History");
                launchFragment(new HistoryFragment());
            } else if (id == R.id.nav_logout) {
                itemStack.pop();
                dialogLogout();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void hideSearchBar() {
        findViewById(R.id.search).setVisibility(View.GONE);
    }

    private void showSearchBar() {
        findViewById(R.id.search).setVisibility(View.VISIBLE);
    }

    /**
     * Replaces the current fragment with the new one.
     *
     * @param toLaunch the new fragment
     */
    private void launchFragment(Fragment toLaunch){
        if(toLaunch != null) {
            mCurrentFragment = toLaunch;
            //fragmentManager.beginTransaction().replace(R.id.fragment_container, mCurrentFragment).commit();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, mCurrentFragment).commitAllowingStateLoss();
        }
    }

    /**
     * Handle request permissions result. Update what needed and give a feedback to the user.
     *
     * @param requestCode       code of the request, an <code>int</code>
     * @param permissions       requested permissions, a table of <code>String</code>
     * @param grantResults      result of the request, a table of <code>int</code>
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),"You can now start a Run.",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),"Permission Denied, you cannot start a Run.",
                             Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    /**
     * Checks whether there is a new message.
     */
    private void checkNbrMessages(){
        mFirebaseHelper.fetchMessages(mEmail,
                new FirebaseHelper.Handler() {
            @Override
            public void handleRetrievedMessages(List<Message> messages) {
                if(messages.size() > nbrMessages){
                    Toast.makeText(getApplicationContext(),"You have a new message",
                            Toast.LENGTH_LONG).show();
                }

                nbrMessages = messages.size();
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(((AppRunnest)getApplication()).getUser().isLoggedIn()) {
            ((AppRunnest) getApplication()).launchDatabaseUpload();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(((AppRunnest)getApplication()).getUser().isLoggedIn()) {
            ((AppRunnest) getApplication()).launchDatabaseUpload();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(((AppRunnest)getApplication()).getUser().isLoggedIn()) {
            ((AppRunnest) getApplication()).launchDatabaseUpload();
        }
    }

    private void dialogLogout(){

        String message = "Are you sure you want to logout?";
        if(!((AppRunnest)getApplication()).getNetworkHandler().isConnected()) {
            message = "If you logout now, your session progresses will be lost. Logout?";
        }

        new AlertDialog.Builder(this, R.style.DarkDialogs)
                .setTitle("Logout")
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((AppRunnest)getApplication()).launchDatabaseUpload();
                        logout();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void logout() {
        ((AppRunnest) getApplication()).getUser().logoutStatus();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        FirebaseAuth.getInstance().signOut();
    }

    private void dialogQuitRun(final MenuItem item){

        new AlertDialog.Builder(this, R.style.DarkDialogs)
                .setTitle("Quit Run")
                .setMessage("Are you sure you want to to quit your current run?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setRunning(false);
                        item.setChecked(true);
                        onNavigationItemSelected(item);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                        itemStack.push(runItem);
                        runItem.setChecked(true);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    /**
     * Set to true if the user is running, to false otherwise.
     * @param running the state of the user.
     */
    public void setRunning(Boolean running) {
        isRunning = running;
    }

    /**
     * Display run info after the run ended.
     * @param run the displayed run.
     */
    @Override
    public void onRunningMapFragmentInteraction(Run run) {

        if(run == null){
            throw new IllegalArgumentException("Invalid argument 'run' on onRunningMapFragmentInteraction method");
        }

        itemStack.push(runItem);
        historyItem.setChecked(true);
        launchFragment(DisplayRunFragment.newInstance(run));
    }

    /**
     * Click on a run from the Run History.
     * @param run the chosen run.
     */
    @Override
    public void onRunHistoryInteraction(Run run) {

        if(run == null){
            throw new IllegalArgumentException("Invalid argument 'run' on onRunHistoryInteraction method");
        }

        itemStack.push(historyItem);
        launchFragment(DisplayRunFragment.newInstance(run));
    }

    /**
     * Click on a challenge from Challenge history.
     * @param challenge the chosen challenge.
     */
    @Override
    public void onChallengeHistoryInteraction(Challenge challenge) {

        if(challenge == null){
            throw new IllegalArgumentException("Invalid argument 'challenge' on  onChallengeHistoryInteraction method");
        }

        itemStack.push(historyItem);
        launchFragment(DisplayChallengeFragment.newInstance(challenge));
    }

    /**
     * launches the profile fragment after login.
     */
    @Override
    public void onDBDownloadFragmentInteraction() {
        launchFragment(new ProfileFragment());
    }

    /**
     * Click on a username during a user search.
     *
     * @param name the username.
     * @param email the Email of the user.
     */
    @Override
    public void onDisplayProfileFragmentInteraction(String name, String email) {

        if(name == null || email == null || name.equals("") || email.equals("")){
            throw new IllegalArgumentException("Invalid argument 'dialog' on onMemoDialogChallengeClick method");
        }

        launchFragment(ProfileFragment.newInstance(name, email));
    }

    /**
     * Click "Challenge' from another user profile.
     *
     * @param challengedUserName the name of the other user.
     * @param challengedUserEmail the email of the other user.
     */
    @Override
    public void onProfileFragmentInteraction(String challengedUserName, String challengedUserEmail) {

        if(challengedUserEmail == null || challengedUserName == null || challengedUserEmail.equals("") || challengedUserName.equals("")){
            throw new IllegalArgumentException("Invalid argument(s) 'challengedUserName' and/or 'challengedUserEmail' on onProfileFragmentInteraction method");
        }

        this.challengedUserName = challengedUserName;
        this.challengedUserEmail = challengedUserEmail;
        showChallengeDialog();
    }

    /**
     * click on a Challenge Request message from the Challenges tab.
     * @param message the Challenge Request Message.
     */
    @Override
    public void onMessagesFragmentInteraction(final Message message) {

        if(message == null){
            throw new IllegalArgumentException("Invalid argument 'message' on onMessagesFragmentInteraction method");
        }

        requestMessage = message;

        if (message.getType() == Message.Type.CHALLENGE_REQUEST) {
            final String challengeName = FirebaseProxy.generateChallengeName( message.getSender(),
                                                                        message.getAddressee(),
                                                                        message.getMessage());
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            dbRef.child("challenges").child(challengeName)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        showRequestChallengeDialog();
                    } else {
                        Toast.makeText(getBaseContext(), "Opponent has already deleted this challenge",
                                Toast.LENGTH_LONG).show();
                        FirebaseHelper fbHelper = new FirebaseHelper();
                        fbHelper.deleteChallengeNode(challengeName);
                        fbHelper.deleteMessage(message);
                        launchFragment(new MessagesFragment());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            });
        }
    }

    public void showRequestChallengeDialog() {
        DialogFragment dialog = new ReceiveChallengeDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("type", requestMessage.getChallengeType());
        args.putInt("firstValue", requestMessage.getFirstValue());
        args.putInt("secondValue", requestMessage.getSecondValue());
        args.putString("sender", requestMessage.getSender());

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "ReceiveChallengeDialogFragment");
    }

    /**
     * click on a Schedule Request message from the Challenges tab.
     * @param message the Schedule Request Message.
     */
    @Override
    public void onMessagesFragmentScheduleRequestInteraction(Message message) {

        if(message == null){
            throw new IllegalArgumentException("Invalid argument 'message' on onMessagesFragmentScheduleRequestInteraction method");
        }

        requestMessage = message;
        showAcceptScheduleDialog();
    }

    private void showAcceptScheduleDialog() {
        DialogFragment dialog = new ReceiveScheduleDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("type", requestMessage.getChallengeType());
        args.putString("sender", requestMessage.getSender());
        args.putSerializable("date", requestMessage.getTime());
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "ReceiveScheduleDialogFragment");
    }

    /**
     * manages callbacks from onDisplayRunFragment.
     */
    @Override
    public void onDisplayRunFragmentInteraction() {
        // keep using the stack
        onNavigationItemSelected(navigationView.getMenu().getItem(4));
    }

    /**
     * manages callbacks from onDisplayChallengeFragment.
     */
    @Override
    public void onDisplayChallengeFragmentInteraction() {
        // keep using the stack
        onNavigationItemSelected(navigationView.getMenu().getItem(4));
    }

    private void showChallengeDialog() {
        DialogFragment dialog = new sendChallengeDialogFragment();
        dialog.show(getSupportFragmentManager(), "sendChallengeDialogFragment");
    }

    /**
     * click 'Accept' in a Challenge Dialog.
     * @param dialog the Challenge Dialog.
     */
    @Override
    public void onSendChallengeDialogPositiveClick(DialogFragment dialog) {

        if(dialog == null){
            throw new IllegalArgumentException("Invalid argument 'dialog' on onSendChallengeDialogPositiveClick method");
        }

        Challenge.Type challengeType = ((sendChallengeDialogFragment)dialog).getType();
        int firstValue = ((sendChallengeDialogFragment)dialog).getFirstValue();
        int secondValue = ((sendChallengeDialogFragment)dialog).getSecondValue();

        // Send message
        String from = ((AppRunnest) getApplication()).getUser().getEmail();
        String to = FirebaseHelper.getFireBaseMail(challengedUserEmail);
        String sender = ((AppRunnest) getApplication()).getUser().getName();

        Random rnd = new Random();
        int rndNumber = 100000 + rnd.nextInt(900000);
        String message = Integer.toString(rndNumber);
        Date timestampId = new Date();
        Message challengeRequestMessage = new Message(from, to, sender, challengedUserName,
                Message.Type.CHALLENGE_REQUEST, message, timestampId, firstValue, secondValue, challengeType);

        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.sendMessage(challengeRequestMessage);


        Intent intent = new Intent(this, ChallengeActivity.class);
        intent.putExtra("type", challengeType);
        intent.putExtra("firstValue", firstValue);
        intent.putExtra("secondValue", secondValue);
        intent.putExtra("owner", true);
        intent.putExtra("opponent", challengedUserName);
        intent.putExtra("msgId", message);

        mSearchView.setQuery("", false);
        //TODO define result code
        startActivityForResult(intent, 1);
    }

    /**
     * click 'Cancel' in a Challenge Dialog.
     * @param dialog the Challenge Dialog.
     */
    @Override
    public void onSendChallengeDialogNegativeClick(DialogFragment dialog) {

        if(dialog == null){
            throw new IllegalArgumentException("Invalid argument 'dialog' on onSendChallengeDialogNegativeClick method");
        }
    }

    /**
     * click 'Accept' in a Request Challenge Dialog.
     * @param dialog the Request Challenge Dialog.
     */
    @Override
    public void onReceiveChallengeDialogAcceptClick(DialogFragment dialog) {

        if(dialog == null){
            throw new IllegalArgumentException("Invalid argument 'dialog' on onReceiveChallengeDialogAcceptClick method");
        }

        Challenge.Type challengeType = ((ReceiveChallengeDialogFragment)dialog).getType();
        mFirebaseHelper.deleteMessage(requestMessage);
        int firstValue = ((ReceiveChallengeDialogFragment)dialog).getFirstValue();
        int secondValue = ((ReceiveChallengeDialogFragment)dialog).getSecondValue();

        Intent intent = new Intent(this, ChallengeActivity.class);
        intent.putExtra("type", challengeType);
        intent.putExtra("firstValue", firstValue);
        intent.putExtra("secondValue", secondValue);
        intent.putExtra("owner", false);
        intent.putExtra("opponent", requestMessage.getSender());
        intent.putExtra("msgId", requestMessage.getMessage());
        startActivityForResult(intent, 1);
    }

    /**
     * click 'Decline' in a Request Challenge Dialog.
     * @param dialog the Request Challenge Dialog.
     */
    @Override
    public void onReceiveChallengeDialogDeclineClick(DialogFragment dialog) {

        if(dialog == null){
            throw new IllegalArgumentException("Invalid argument 'dialog' on onReceiveChallengeDialogDeclineClick method");
        }

        mFirebaseHelper.deleteMessage(requestMessage);
        launchFragment(new MessagesFragment());
    }

    /**
     * click 'Cancel' in a Request Challenge Dialog.
     * @param dialog the Request Challenge Dialog.
     */
    @Override
    public void onReceiveChallengeDialogCancelClick(DialogFragment dialog) {
        if(dialog == null){
            throw new IllegalArgumentException("Invalid argument 'dialog' on onReceiveChallengeDialogCancelClick method");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(data == null){
            throw new IllegalArgumentException("Invalid argument 'data' on onActivityResult method");
        }

        if(resultCode == REQUEST_STOP_WAITING){
            Toast.makeText(getApplicationContext(),"The challenge was deleted",
                    Toast.LENGTH_LONG).show();
        } else if(resultCode == REQUEST_ABORT) {
            Toast.makeText(getApplicationContext(),"You have aborted the challenge",
                    Toast.LENGTH_LONG).show();
        } else if(resultCode == REQUEST_END_CHALLENGE) {
            DBHelper dbHelper = new DBHelper(this);
            List<Challenge> challenges = dbHelper.fetchAllChallenges();
            Challenge lastChallenge = challenges.get(challenges.size() - 1);
            onChallengeHistoryInteraction(lastChallenge);
            historyItem.setChecked(true);
        }else{
            throw new IllegalArgumentException("Invalid argument 'requestCode' on onActivityResult method");
        }

    }

    /**
     * Click 'schedule' on another user profile fragment.
     * @param name the name of the other user
     * @param email the mail of the other user
     */
    @Override
    public void onProfileFragmentInteractionSchedule(String name, String email){

        if(name == null || email == null || name.equals("") || email.equals("")){
            throw new IllegalArgumentException("Invalid argument(s) 'name' and/or 'email' on onProfileFragmentInteractionSchedule method");
        }

        this.challengedUserName = name;
        this.challengedUserEmail = email;
        showRequestScheduleDialog();
    }

    private void showRequestScheduleDialog() {
        DialogFragment dialog = new SendScheduleDialogFragment();
        dialog.show(getSupportFragmentManager(), "SendRequestScheduleDialogFragment");
    }

    /**
     * click 'Accept' in a Request Schedule Dialog.
     * @param dialog the Request Schedule Dialog.
     */
    @Override
    public void onSendScheduleDialogPositiveClick(DialogFragment dialog){

        if(dialog == null){
            throw new IllegalArgumentException("Invalid argument 'dialog' on onSendScheduleDialogPositiveClick method");
        }

        Challenge.Type challengeType = ((SendScheduleDialogFragment)dialog).getType();

        Date scheduledDate = ((SendScheduleDialogFragment)dialog).getScheduledCalendar().getTime();

        // Send message
        String from = ((AppRunnest) getApplication()).getUser().getEmail();
        String to = FirebaseHelper.getFireBaseMail(challengedUserEmail);
        String sender = ((AppRunnest) getApplication()).getUser().getName();
        String message = "let's schedule a run!";
        Message scheduleRequestMessage = new Message(from, to, sender, challengedUserName,
                Message.Type.SCHEDULE_REQUEST, message, scheduledDate, challengeType);

        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.sendMessage(scheduleRequestMessage);
    }

    /**
     * click 'Decline' in a Request Schedule Dialog.
     * @param dialog the Request Schedule Dialog.
     */
    @Override
    public void onSendScheduleDialogNegativeClick(DialogFragment dialog){

        if(dialog == null){
            throw new IllegalArgumentException("Invalid argument 'dialog' on onSendScheduleDialogNegativeClick method");
        }
    }

    /**
     * click 'Accept' in an Accept Schedule Dialog.
     * @param dialog the Accept Schedule Dialog.
     */
    @Override
    public void onReceiveScheduleDialogAcceptClick(DialogFragment dialog){

        if(dialog == null){
            throw new IllegalArgumentException("Invalid argument 'dialog' on onReceiveScheduleDialogAcceptClick method");
        }

        Challenge.Type challengeType = ((ReceiveScheduleDialogFragment)dialog).getType();
        Date scheduledDate = ((ReceiveScheduleDialogFragment)dialog).getScheduledDate();

        String from = ((AppRunnest) getApplication()).getUser().getEmail();
        String to = FirebaseHelper.getFireBaseMail(requestMessage.getFrom());
        String sender = ((AppRunnest) getApplication()).getUser().getName();
        String addressee = requestMessage.getSender();
        String message = "don't forget this run!";

        // Send message to other user
        Message memoToOpponentMessage = new Message(from, to, sender, addressee,
                Message.Type.MEMO, message, scheduledDate, challengeType);

        // Send message to yourself
        Message memoToMyselfMessage = new Message(to, FirebaseHelper.getFireBaseMail(from), addressee, sender,
                Message.Type.MEMO, message, scheduledDate, challengeType);

        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.sendMessage(memoToOpponentMessage);
        firebaseHelper.sendMessage(memoToMyselfMessage);

        mFirebaseHelper.deleteMessage(requestMessage);
        launchFragment(new MessagesFragment());
    }

    /**
     * click 'Decline' in an Accept Schedule Dialog.
     * @param dialog the Accept Schedule Dialog.
     */
    @Override
    public void onReceiveScheduleDialogDeclineClick(DialogFragment dialog){
        if(dialog == null){
            throw new IllegalArgumentException("Invalid argument 'dialog' on onAcceptScheduleDialogChallengeClick method");
        }

        mFirebaseHelper.deleteMessage(requestMessage);
        launchFragment(new MessagesFragment());
    }

    /**
     * click 'Cancel' in an Accept Schedule Dialog.
     * @param dialog the Accept Schedule Dialog.
     */
    @Override
    public void onReceiveScheduleDialogCancelClick(DialogFragment dialog){

        if(dialog == null){
            throw new IllegalArgumentException("Invalid argument 'dialog' on onAcceptScheduleDialogClick method");
        }
    }

    /**
     * click on a Memo message from the Challenges tab.
     * @param message the clicked message.
     */
    @Override
    public void onMessagesFragmentMemoInteraction(Message message){

        if(message == null){
            throw new IllegalArgumentException("Invalid argument 'message' on onMessagesFragmentMemoInteraction method");
        }

        requestMessage = message;
        showMemoDialog();
    }

    private void showMemoDialog(){
        DialogFragment dialog = new MemoDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("type", requestMessage.getChallengeType());
        args.putString("opponent", requestMessage.getFrom());
        args.putString("sender", requestMessage.getSender());
        args.putSerializable("date", requestMessage.getTime());
        args.putString("opponentEmail", requestMessage.getFrom());
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "MemoDialogFragment");
    }

    /**
     * click 'Close'in the Memo Dialog.
     * @param dialog the Memo Dialog.
     */
    @Override
    public void onMemoDialogCloseClick(DialogFragment dialog){

        if(dialog == null){
            throw new IllegalArgumentException("Invalid argument 'dialog' on onMemoDialogCloseClick method");
        }
    }

    /**
     * click 'Delete' in the Memo Dialog.
     * @param dialog the Memo Dialog.
     */
    @Override
    public void onMemoDialogDeleteClick(DialogFragment dialog){
        if(dialog == null){
            throw new IllegalArgumentException("Invalid argument 'dialog' on onMemoDialogDeleteClick method");
        }
        mFirebaseHelper.deleteMessage(requestMessage);
        launchFragment(new MessagesFragment());
    }

    /**
     * click 'Challenge' in the memoDialog.
     * @param dialog the Memo Dialog.
     */
    @Override
    public void onMemoDialogChallengeClick(DialogFragment dialog){
        if(dialog == null){
            throw new IllegalArgumentException("Invalid argument 'dialog' on onMemoDialogChallengeClick method");
        }

        mFirebaseHelper.deleteMessage(requestMessage);
        this.challengedUserName = ((MemoDialogFragment) dialog).getSender();
        this.challengedUserEmail = ((MemoDialogFragment) dialog).getOpponentEmail();
        showChallengeDialog();
    }
}
