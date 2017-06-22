package ch.ihl.runnest.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.multidex.ch.ihl.runnest.AppRunnest.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import ch.ihl.runnest.AppRunnest;
import ch.ihl.runnest.Database.DBHelper;
import ch.ihl.runnest.Firebase.FirebaseHelper;
import ch.ihl.runnest.Firebase.FirebaseProxy;
import ch.ihl.runnest.Fragments.RunFragments.ChallengeReceiverFragment;
import ch.ihl.runnest.Fragments.RunFragments.ChallengeSenderFragment;
import ch.ihl.runnest.Fragments.RunFragments.LocationSettingsHandler;
import ch.ihl.runnest.Model.Challenge;
import ch.ihl.runnest.Model.ChallengeProxy;
import ch.ihl.runnest.Model.CheckPoint;
import ch.ihl.runnest.Model.Run;
import ch.ihl.runnest.Model.User;
import ch.ihl.runnest.TestProxy;
import ch.ihl.runnest.UtilsUI;
import pl.droidsonroids.gif.GifTextView;

import static ch.ihl.runnest.Activities.SideBarActivity.PERMISSION_REQUEST_CODE_FINE_LOCATION;

public class ChallengeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private enum ChallengePhase {
        BEFORE_CHALLENGE, DURING_CHALLENGE
    }

    // Location
    private GoogleApiClient googleApiClient = null;
    private LocationSettingsHandler locationSettingsHandler = null;

    // Challenge Management
    private Challenge.Type challengeType;
    private ChallengePhase phase = ChallengePhase.BEFORE_CHALLENGE;
    private double challengeGoal = 0.0;   // time in milliseconds or distance in Km
    private boolean challengeWon = false;
    private boolean isIntendedActivityExit = false;
    private boolean leavingChallenge = false;

    // ChallengeProxy
    private ChallengeProxy challengeProxy;
    private Boolean isOwner = false;
    private Boolean isOpponentThere = false;
    private Boolean isUserReady = false;
    private Boolean isUserDone = false;
    private Boolean isOpponentReady = false;
    private Boolean isOpponentDone = false;
    private String opponentName = null;
    private String challengeId = null;

    // Fragments
    private FragmentManager fragmentManager = null;
    private Fragment senderFragment = null;
    private Fragment receiverFragment = null;

    // GUI
    private Button readyBtn = null;
    private Chronometer chronometer = null;
    private TextView opponentTxt = null;
    private TextView userTxt = null;
    private ImageButton backToSideBtn = null;
    private GifTextView waitingOpponent = null;
    private ImageView readyOpponent = null;
    private LinearLayout layoutUserReady = null;
    private ImageView userStatus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        setUserAvailable(false);

        // Setup Challenge
        extractIntent(getIntent());
        createProxy();

        // Location
        setupGoogleApi();
        locationSettingsHandler = new LocationSettingsHandler(googleApiClient, this);
        locationSettingsHandler.checkLocationSettings();

        setupFragments();
        setupGUI();
    }

    /**
     * Getter for the proxy used by this challenge. Usually needed by the challenge fragments to directly forward
     * notifications and calls to the opponent.
     *
     * @return      The ChallengeProxy used for this challenge.
     */
    public ChallengeProxy getChallengeProxy(){
        return challengeProxy;
    }

    /**
     * Getter for the GoogleApiClient used by the local user to get the locations during the challenge. Usually needed
     * by the challenge fragments to retrieve location updates.
     *
     * @return      The GoogleApiClient used for this challenge.
     */
    public GoogleApiClient getGoogleApiClient(){
        return googleApiClient;
    }

    /**
     * Getter for the LocationSettingsHandler used by the local user. Usually needed by the challenge fragments to
     * setup location updates.
     *
     * @return      The LocationSettingsHandler used for this challenge.
     */
    public LocationSettingsHandler getLocationSettingsHandler(){
        return locationSettingsHandler;
    }

    /**
     * Getter for the challenge type.
     *
     * @return      The current challenge's type, which can be either TIME or DISTANCE.
     */
    public Challenge.Type getChallengeType() {
        return challengeType;
    }

    /**
     * Getter for the remote opponent's name.
     *
     * @return      The remote opponent's name.
     */
    public String getOpponentName() {
        return opponentName;
    }

    /**
     * Getter for the goal of the challenge. The nature of the returned value depends on the type of the current
     * challenge: it will be a time or a distance depending from the result of getChallengeType.
     *
     * @return      The challenge goal in in milliseconds if it's a TIME challenge, or in kilometers if DISTANCE.
     */
    public double getChallengeGoal() {
        return challengeGoal;
    }

    /**
     * This method is called by the challenge fragments to let the activity know there are new updates in the runs and
     * the current winner calculation must be redone.
     */
    public void updateIsWinning() {
        if(!isOpponentDone && !isUserDone) {
            TextView opponentPosition = (TextView) findViewById(R.id.position_indicator_receiver);
            TextView userPosition = (TextView) findViewById(R.id.position_indicator_sender);

            Run opponentRun = ((ChallengeReceiverFragment) receiverFragment).getRun();
            Run userRun = ((ChallengeSenderFragment) senderFragment).getRun();

            float opponentDistance = opponentRun.getTrack().getDistance();
            float userDistance = userRun.getTrack().getDistance();

            if (opponentDistance == userDistance) {
                opponentPosition.setText(R.string.dash);
                userPosition.setText(R.string.dash);
            } else if (opponentDistance > userDistance) {
                opponentPosition.setText(R.string.first_position);
                userPosition.setText(R.string.second_position);
            } else {
                opponentPosition.setText(R.string.second_position);
                userPosition.setText(R.string.first_position);
            }
            System.out.println(opponentDistance + " " + userDistance);
        }
    }

    /**
     *  This method has to be called when the local user meets the challenge goal. It updates the UI accordingly, stops
     *  the run and signals the proxy that the local user is done. If the opponent was already done, the challenge
     *  exit process is launched.
     */
    public void imFinished() {
        isUserDone = true;
        layoutUserReady.setVisibility(View.VISIBLE);

        String userTextToShow = "";
        switch (challengeType) {
            case TIME:
                userTextToShow = getString(R.string.user_finished) +
                        "\n" +
                        getString(R.string.final_distance) +
                        (int)((ChallengeSenderFragment)senderFragment).getRun().getTrack().getDistance() +
                        getString(R.string.meters);
                break;
            case DISTANCE:
                userTextToShow = getString(R.string.user_completed) +
                        challengeGoal + getString(R.string.km) +
                        getString(R.string.white_space) + getString(R.string.in) +
                        UtilsUI.timeToString((int)((ChallengeSenderFragment)senderFragment).getRun().getDuration(),
                                true);
                break;
        }
        userTxt.setText(userTextToShow);
        //noinspection deprecation
        userStatus.setImageDrawable(getResources().getDrawable(R.drawable.finish_icon));

        fragmentManager.beginTransaction().remove(senderFragment).commitAllowingStateLoss();

        challengeProxy.imFinished();

        if (isOpponentDone) {
            isIntendedActivityExit = true;
            endChallenge();
        }
    }

    private void extractIntent(Intent intent) {
        Bundle extra = intent.getExtras();

        // Extract extras
        opponentName = extra.getString(getString(R.string.opponent_intent_extra));
        isOwner = extra.getBoolean(getString(R.string.owner_intent_extra));
        challengeType = (Challenge.Type) intent.getSerializableExtra(getString(R.string.type_intent_extra));
        challengeId = extra.getString(getString(R.string.msg_id_intent_extra));
        int firstValue = intent.getIntExtra(getString(R.string.first_value_intent_extra), 0);
        int secondValue = intent.getIntExtra(getString(R.string.second_value_intent_extra), 0);

        switch (challengeType) {
            case DISTANCE:
                challengeGoal = firstValue + secondValue / 1000.0;
                break;
            case TIME:
                challengeGoal = ((AppRunnest)getApplication()).isTestSession()?
                                    15000:
                                    firstValue * 3600 * 1000 + secondValue * 60 * 1000;
                break;
        }
    }

    private void startChallenge(){

        challengeProxy.startChallenge();

        readyBtn.setVisibility(View.GONE);

        layoutUserReady.setVisibility(View.GONE);
        opponentTxt.setVisibility(View.GONE);
        readyOpponent.setVisibility(View.GONE);
        //noinspection deprecation
        backToSideBtn.setImageDrawable(getResources().getDrawable(R.drawable.give_up1));

        receiverFragment = new ChallengeReceiverFragment();
        fragmentManager.beginTransaction().add(R.id.receiver_container, receiverFragment).commit();

        senderFragment = new ChallengeSenderFragment();
        fragmentManager.beginTransaction().add(R.id.sender_container, senderFragment).commit();

        setupChronometer();
        phase = ChallengePhase.DURING_CHALLENGE;
    }

    private void endChallenge() {
        chronometer.stop();
        Run opponentRun = ((ChallengeReceiverFragment)receiverFragment).getRun();
        Run userRun = ((ChallengeSenderFragment)senderFragment).getRun();

        if(!leavingChallenge) {
            switch (challengeType) {
                case TIME:
                    challengeWon = ((AppRunnest) getApplication()).isTestSession() ||
                            userRun.getTrack().getDistance() >= opponentRun.getTrack().getDistance();
                    break;
                case DISTANCE:
                    challengeWon = !((AppRunnest) getApplication()).isTestSession() &&
                            userRun.getDuration() <= opponentRun.getDuration();
                    break;
            }
        }

        // Challenge results
        FirebaseHelper.RunType challengeResult;
        Challenge.Result result;
        if (challengeWon) {
            challengeResult = FirebaseHelper.RunType.CHALLENGE_WON;
            result = leavingChallenge ? Challenge.Result.ABORTED_BY_OTHER: Challenge.Result.WON;
        } else {
            challengeResult = FirebaseHelper.RunType.CHALLENGE_LOST;
            result = leavingChallenge ? Challenge.Result.ABORTED_BY_ME: Challenge.Result.LOST;
        }

        // Update statistic
        FirebaseHelper fbHelper = new FirebaseHelper();
        User currentUser = ((AppRunnest) this.getApplication()).getUser();
        fbHelper.updateUserStatistics(currentUser.getEmail(),
                userRun.getDuration(),
                userRun.getTrack().getDistance(),
                challengeResult);

        // Save challenge into the database
        Challenge challengeToSave = new Challenge(opponentName, challengeType, challengeGoal, result, userRun, opponentRun);
        DBHelper dbHelper = new DBHelper(this);
        dbHelper.insert(challengeToSave);

        // upload database
        ((AppRunnest) getApplication()).launchDatabaseUpload();

        setUserAvailable(true);

        // Go to recap challenge
        goToSidebar(SideBarActivity.REQUEST_END_CHALLENGE);
    }

    private void goToSidebar(int requestType){

        Intent returnIntent = new Intent();
        setResult(requestType, returnIntent);
        finish();
    }

    private void dialogQuitChallenge(){

        new AlertDialog.Builder(this, R.style.DarkDialogs)
                .setTitle(R.string.quit_challenge_dialog_title)
                .setMessage(R.string.quit_challenge_dialog_question)
                .setPositiveButton(R.string.quit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        isIntendedActivityExit = true;
                        abandonAfterStart();
                    }
                })
                .setNegativeButton(R.string.keep_running, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_delete)
                .show();
    }

    private void stopWaitingForOpponent(){

        new AlertDialog.Builder(this, R.style.DarkDialogs)
                .setTitle(R.string.stop_waiting_dialog_title)
                .setMessage(R.string.stop_waiting_dialog_question)
                .setPositiveButton(R.string.quit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        isIntendedActivityExit = true;
                        abandonBeforeStart();
                    }
                })
                .setNegativeButton(R.string.wait, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing

                    }
                })
                .setIcon(android.R.drawable.ic_menu_recent_history)
                .show();
    }

    private void abandonBeforeStart() {
        if (isOwner && !isOpponentThere) {
            // If you are still in the room alone
            challengeProxy.deleteChallenge();
        } else {
            // If opponent is already here and he needs notification
            challengeProxy.abortChallenge();
        }

        setUserAvailable(true);
        Intent returnIntent = new Intent();
        setResult(SideBarActivity.REQUEST_STOP_WAITING, returnIntent);
        finish();
    }

    private void abandonAfterStart() {
        challengeProxy.abortChallenge();
        ((ChallengeSenderFragment)senderFragment).endChallenge();
        ((ChallengeReceiverFragment)receiverFragment).stopRun();
        leavingChallenge = true;
        challengeWon = false;
        endChallenge();
    }

    private void setUserAvailable(boolean value) {
        new FirebaseHelper().
                setUserAvailable(((AppRunnest) getApplication()).getUser().getEmail(), false, value);
    }

    private void createProxy(){

        String userName = ((AppRunnest)getApplication()).getUser().getName();

        ChallengeProxy.Handler proxyHandler = createHandler();

        challengeProxy = ((AppRunnest) getApplication()).isTestSession()?
                            new TestProxy(proxyHandler):
                            new FirebaseProxy(userName, opponentName, createHandler(), isOwner, challengeId);
    }

    private ChallengeProxy.Handler createHandler() {

        return new ChallengeProxy.Handler() {
            @Override
            public void hasNewData(CheckPoint checkPoint) {
                ((ChallengeReceiverFragment)receiverFragment).onNewData(checkPoint);
            }

            @Override
            public void isReady() {
                isOpponentReady = true;
                opponentTxt.setText(R.string.opponent_ready);

                findViewById(R.id.opponent_ready_icon).setVisibility(View.VISIBLE);
                waitingOpponent.setVisibility(View.GONE);

                if(isUserReady) {
                    startChallenge();
                }
            }

            @Override
            public void isFinished() {
                isOpponentDone = true;
                opponentTxt.setVisibility(View.VISIBLE);

                ((ChallengeReceiverFragment)receiverFragment).stopRun();

                String opponentTextToShow = "";
                switch (challengeType) {
                    case TIME:
                        opponentTextToShow = getString(R.string.opponent_finished) +
                                "\n" +
                                getString(R.string.final_distance) + getString(R.string.colon) +
                                (int)((ChallengeReceiverFragment)receiverFragment).getRun().getTrack().getDistance() +
                                getString(R.string.meters);
                        break;
                    case DISTANCE:
                        long opponentDuration = (SystemClock.elapsedRealtime() - chronometer.getBase())/1000;
                        opponentTextToShow = getString(R.string.opponent_completed) +
                                challengeGoal + getString(R.string.km) +
                                getString(R.string.white_space) + getString(R.string.in) +
                                UtilsUI.timeToString((int)opponentDuration, true);
                        break;
                }
                opponentTxt.setText(opponentTextToShow);

                fragmentManager.beginTransaction().remove(receiverFragment).commitAllowingStateLoss();

                if (isUserDone) {
                    isIntendedActivityExit = true;
                    endChallenge();
                }
            }

            @Override
            public void hasLeft() {

                isIntendedActivityExit = true;
                challengeProxy.deleteChallenge();

                if (phase == ChallengePhase.BEFORE_CHALLENGE) {
                    setUserAvailable(true);
                    goToSidebar(SideBarActivity.REQUEST_STOP_WAITING);
                } else {
                    leavingChallenge = false;
                    challengeWon = true;
                    ((ChallengeSenderFragment) senderFragment).endChallenge();
                    ((ChallengeReceiverFragment) receiverFragment).stopRun();
                    endChallenge();
                }
            }

            @Override
            public void opponentInRoom() {
                isOpponentThere = true;
                opponentTxt.setText(R.string.opponent_in_room);
            }
        };
    }

    private void setupGoogleApi() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void setupFragments() {
        fragmentManager = getSupportFragmentManager();
        senderFragment = fragmentManager.findFragmentById(R.id.sender_container);
        receiverFragment = fragmentManager.findFragmentById(R.id.receiver_container);
    }

    private void setupGUI() {
        ((Toolbar)findViewById(R.id.challenge_toolbar)).setTitle(getString(R.string.you_vs) + opponentName);

        chronometer = (Chronometer) findViewById(R.id.challenge_chronometer);
        chronometer.setText(R.string.chronometer_before_start);

        opponentTxt = (TextView) findViewById(R.id.opponentTxt);
        userTxt = (TextView) findViewById(R.id.userTxt);
        if (!isOwner) {
            opponentTxt.setText(R.string.opponent_in_room);
        }

        layoutUserReady = (LinearLayout) findViewById(R.id.layout_user_ready);
        userStatus = (ImageView) findViewById(R.id.user_status);

        backToSideBtn = (ImageButton) findViewById(R.id.back_to_side_btn);
        backToSideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phase == ChallengePhase.BEFORE_CHALLENGE){
                    stopWaitingForOpponent();
                } else if(phase == ChallengePhase.DURING_CHALLENGE){
                    dialogQuitChallenge();
                }
            }
        });

        readyBtn = (Button) findViewById(R.id.readyBtn);
        readyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermission() && locationSettingsHandler.checkLocationSettings()) {
                    challengeProxy.imReady();
                    isUserReady = true;

                    readyBtn.setVisibility(View.GONE);
                    layoutUserReady.setVisibility(View.VISIBLE);

                    // In a test session we don't want to wait for the opponent
                    if (isOpponentReady || ((AppRunnest)getApplication()).isTestSession()){
                        startChallenge();
                    }
                }
            }
        });


        readyOpponent = (ImageView)findViewById(R.id.opponent_ready_icon);
        readyOpponent.setVisibility(View.GONE);

        waitingOpponent = (GifTextView)findViewById(R.id.opponent_waiting_icon);

    }

    private void setupChronometer() {
        switch (challengeType) {
            case DISTANCE:
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                break;
            case TIME:
                new CountDownTimer((long)challengeGoal, 1000) {

                    public void onTick(long millisUntilFinished) {
                        String timeLeft = getString(R.string.chronometer_time_left) +
                                UtilsUI.timeToString((int)millisUntilFinished/1000, false);
                        chronometer.setText(timeLeft);
                    }

                    public void onFinish() {
                        if(!leavingChallenge) {
                            String timeIsUp = getString(R.string.chronometer_time_is_up);
                            chronometer.setText(timeIsUp);
                            ((ChallengeSenderFragment) senderFragment).endChallenge();
                            imFinished();
                        }
                    }
                }.start();
                break;
        }

        chronometer.setVisibility(View.VISIBLE);
    }

    /**
     * This check is necessary only with Android 6.0+ and/or SDK 22+
     */
    private boolean checkPermission() {
        int fineLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (fineLocation != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_CODE_FINE_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * Handle request permissions result. Update what is needed and give a feedback to the user.
     *
     * @param requestCode       Code of the request.
     * @param permissions       Requested permissions.
     * @param grantResults      Result of the request.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), R.string.toast_start_challenge,Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.toast_start_challenge_denied,
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Do nothing on successful googleApiClient connection
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Reconnect to googleApiClient
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Notify the user and try to reconnect
        Toast.makeText(getBaseContext(), R.string.google_api_client_connection_failed, Toast.LENGTH_LONG).show();
        googleApiClient.connect();
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        if (!isIntendedActivityExit) {
            if (phase == ChallengePhase.BEFORE_CHALLENGE) {
                abandonBeforeStart();
            } else {
                abandonAfterStart();
            }
        }
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        backToSideBtn.performClick();
    }
}
