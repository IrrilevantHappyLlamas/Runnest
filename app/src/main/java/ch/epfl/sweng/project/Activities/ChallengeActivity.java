package ch.epfl.sweng.project.Activities;

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

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import ch.epfl.sweng.project.AppRunnest;
import ch.epfl.sweng.project.Database.DBHelper;
import ch.epfl.sweng.project.Firebase.FirebaseHelper;
import ch.epfl.sweng.project.Firebase.FirebaseProxy;
import ch.epfl.sweng.project.Fragments.RunFragments.ChallengeReceiverFragment;
import ch.epfl.sweng.project.Fragments.RunFragments.ChallengeSenderFragment;
import ch.epfl.sweng.project.Fragments.RunFragments.LocationSettingsHandler;
import ch.epfl.sweng.project.Model.Challenge;
import ch.epfl.sweng.project.Model.ChallengeProxy;
import ch.epfl.sweng.project.Model.CheckPoint;
import ch.epfl.sweng.project.Model.Run;
import ch.epfl.sweng.project.Model.User;
import ch.epfl.sweng.project.TestProxy;
import ch.epfl.sweng.project.UtilsUI;
import pl.droidsonroids.gif.GifTextView;

import static ch.epfl.sweng.project.Activities.SideBarActivity.PERMISSION_REQUEST_CODE_FINE_LOCATION;

public class ChallengeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private final int BEFORE_CHALLENGE = 0;
    private final int DURING_CHALLENGE = 1;
    private final int AFTER_CHALLENGE = 2;


    // Location
    private GoogleApiClient mGoogleApiClient;
    private LocationSettingsHandler mLocationSettingsHandler;

    // Challenge
    private Challenge.Type challengeType;
    private double challengeGoal;   // time in milliseconds or distance in Km
    private boolean win;
    private boolean isWinning;

    // ChallengeProxy
    private ChallengeProxy challengeProxy;
    private Boolean owner = false;
    private Boolean isOpponentThere = false;
    private Boolean opponentReady = false;
    private Boolean userReady = false;
    private Boolean opponentFinished = false;
    private Boolean userFinished = false;
    private boolean isIntendedActivityExit = false;
    private String opponentName;
    private String challengeId;
    private ChallengeProxy.Handler proxyHandler;

    // Fragments
    private FragmentManager fragmentManager;
    private Fragment senderFragment;
    private Fragment receiverFragment;

    // GUI
    private Button readyBtn;
    private Chronometer chronometer;
    private TextView opponentTxt;
    private TextView userTxt;
    private ImageButton backToSideBtn;
    private GifTextView waitingOpponent;
    private ImageView readyOpponent;
    private LinearLayout layoutUserReady;

    //others
    private int phase = BEFORE_CHALLENGE;
    private boolean aborted = false;
    private ImageView userStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);


        extractIntent(getIntent());
        setupGoogleApi();
        createProxy();

        // Location
        mLocationSettingsHandler = new LocationSettingsHandler(mGoogleApiClient, this);
        mLocationSettingsHandler.checkLocationSettings();

        setupFragments();
        setupGUI();

        opponentReady = false;
        userReady = false;
    }

    private void extractIntent(Intent intent) {
        Bundle extra = intent.getExtras();

        // Set user as unavailable
        new FirebaseHelper().
                setUserAvailable(((AppRunnest) getApplication()).getUser().getEmail(), false, false);

        opponentName = extra.getString("opponent");
        owner = extra.getBoolean("owner");

        challengeType = (Challenge.Type) intent.getSerializableExtra("type");
        challengeId = extra.getString("msgId");

        int firstValue = intent.getIntExtra("firstValue", 0);
        int secondValue = intent.getIntExtra("secondValue", 0);
        switch (challengeType) {
            case DISTANCE:
                challengeGoal = firstValue + secondValue / 1000.0;
                break;
            case TIME:
                if(((AppRunnest)getApplication()).isTestSession()) {
                    challengeGoal = 20000;
                } else {
                    challengeGoal = firstValue * 3600 * 1000 + secondValue * 60 * 1000;
                }
                break;
        }
    }

    private void setupGoogleApi() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
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

    private void createProxy(){

        String userName = ((AppRunnest)getApplication()).getUser().getName();

        proxyHandler = new ChallengeProxy.Handler() {
            @Override
            public void hasNewData(CheckPoint checkPoint) {
                ((ChallengeReceiverFragment)receiverFragment).onNewData(checkPoint);
                updateIsWinning();
            }

            @Override
            public void isReady() {
                opponentReady = true;
                opponentTxt.setText(R.string.opponent_ready);

                findViewById(R.id.opponent_ready_icon).setVisibility(View.VISIBLE);
                waitingOpponent.setVisibility(View.GONE);

                if(userReady) {
                    startChallenge();
                }
            }

            @Override
            public void isFinished() {
                opponentFinished = true;
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

                if (userFinished) {
                    isIntendedActivityExit = true;
                    endChallenge();
                }
            }

            @Override
            public void hasAborted() {
                if (phase == BEFORE_CHALLENGE) {
                    Intent returnIntent = new Intent();
                    setResult(SideBarActivity.REQUEST_STOP_WAITING, returnIntent);
                    isIntendedActivityExit = true;
                    finish();
                } else {
                    aborted = true;
                    win = true;
                    ((ChallengeSenderFragment) senderFragment).endChallenge();
                    ((ChallengeReceiverFragment) receiverFragment).stopRun();
                    isIntendedActivityExit = true;
                    endChallenge();
                }
            }

            @Override
            public void opponentInRoom() {
                isOpponentThere = true;
                opponentTxt.setText(R.string.opponent_in_room);
            }
        };

        if (((AppRunnest) getApplication()).isTestSession()) {
            challengeProxy = new TestProxy(proxyHandler);
        } else {
            challengeProxy = new FirebaseProxy(userName, opponentName, proxyHandler, owner, challengeId);
        }
    }

    public void imFinished() {
        userFinished = true;
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
        userStatus.setImageDrawable(getResources().getDrawable(R.drawable.finish_icon));


        fragmentManager.beginTransaction().remove(senderFragment).commitAllowingStateLoss();

        challengeProxy.imFinished();

        if (opponentFinished) {
            isIntendedActivityExit = false;
            endChallenge();
        }
    }

    private void startChallenge(){

        challengeProxy.startChallenge();

        readyBtn.setVisibility(View.GONE);

        layoutUserReady.setVisibility(View.GONE);
        //userTxt.setVisibility(View.GONE);
        opponentTxt.setVisibility(View.GONE);
        backToSideBtn.setImageDrawable(getResources().getDrawable(R.drawable.give_up1));
        readyOpponent.setVisibility(View.GONE);

        receiverFragment = new ChallengeReceiverFragment();
        fragmentManager.beginTransaction().add(R.id.receiver_container, receiverFragment).commit();

        senderFragment = new ChallengeSenderFragment();
        fragmentManager.beginTransaction().add(R.id.sender_container, senderFragment).commit();

        setupChronometer();
        phase = DURING_CHALLENGE;
    }

    private void setupChronometer() {
        switch (challengeType) {
            case DISTANCE:
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                break;
            case TIME:
                //TODO verify that cast to long isn't a problem
                new CountDownTimer((long)challengeGoal, 1000) {

                    public void onTick(long millisUntilFinished) {
                        String timeLeft = "Time left: " +
                                UtilsUI.timeToString((int)millisUntilFinished/1000, false);
                        chronometer.setText(timeLeft);
                    }

                    public void onFinish() {
                        if(!aborted) {
                            String timeIsUp = "Time's up!";
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

    private void setupGUI() {
        ((Toolbar)findViewById(R.id.challenge_toolbar)).setTitle("You vs. " + opponentName);

        chronometer = (Chronometer) findViewById(R.id.challenge_chronometer);
        chronometer.setText("Warm up!");

        opponentTxt = (TextView) findViewById(R.id.opponentTxt);
        userTxt = (TextView) findViewById(R.id.userTxt);
        if (!owner) {
            opponentTxt.setText(R.string.opponent_in_room);
        }

        layoutUserReady = (LinearLayout) findViewById(R.id.layout_user_ready);
        userStatus = (ImageView) findViewById(R.id.user_status);

        backToSideBtn = (ImageButton) findViewById(R.id.back_to_side_btn);
        backToSideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phase == BEFORE_CHALLENGE){
                    stopWaitingForOpponent();
                } else if(phase == DURING_CHALLENGE){
                    dialogQuitChallenge();
                } else {
                    goToChallengeRecap();
                }
            }
        });

        readyBtn = (Button) findViewById(R.id.readyBtn);
        readyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermission() && mLocationSettingsHandler.checkLocationSettings()) {
                    challengeProxy.imReady();
                    userReady = true;

                    readyBtn.setVisibility(View.GONE);
                    layoutUserReady.setVisibility(View.VISIBLE);

                    // In a test session we don't want to wait for the opponent
                    if (opponentReady || ((AppRunnest)getApplication()).isTestSession()){
                        startChallenge();
                    }
                }
            }
        });


        readyOpponent = (ImageView)findViewById(R.id.opponent_ready_icon);
        readyOpponent.setVisibility(View.GONE);

        waitingOpponent = (GifTextView)findViewById(R.id.opponent_waiting_icon);

    }

    public void updateIsWinning() {
        Run opponentRun = ((ChallengeReceiverFragment)receiverFragment).getRun();
        Run userRun = ((ChallengeSenderFragment)senderFragment).getRun();

        isWinning = userRun.getTrack().getDistance() >= opponentRun.getTrack().getDistance();
    }

    private void endChallenge() {
        chronometer.stop();
        phase = AFTER_CHALLENGE;
        Run opponentRun = ((ChallengeReceiverFragment)receiverFragment).getRun();
        Run userRun = ((ChallengeSenderFragment)senderFragment).getRun();

        if(!aborted) {
            switch (challengeType) {
                case TIME:
                    win = ((AppRunnest) getApplication()).isTestSession() ||
                            userRun.getTrack().getDistance() >= opponentRun.getTrack().getDistance();
                    break;
                case DISTANCE:
                    win = !((AppRunnest) getApplication()).isTestSession() &&
                            userRun.getDuration() <= opponentRun.getDuration();
                    break;
            }
        }

        // Challenge results
        FirebaseHelper.RunType challengeResult;
        Challenge.Result result;
        if (win) {
            challengeResult = FirebaseHelper.RunType.CHALLENGE_WON;
            result = aborted? Challenge.Result.ABORTED_BY_OTHER: Challenge.Result.WON;
        } else {
            challengeResult = FirebaseHelper.RunType.CHALLENGE_LOST;
            result = aborted? Challenge.Result.ABORTED_BY_ME: Challenge.Result.LOST;
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

        // Set user as available
        new FirebaseHelper().
                setUserAvailable(((AppRunnest) getApplication()).getUser().getEmail(), false, true);

        // Go to recap challenge
        goToChallengeRecap();
    }

    public ChallengeProxy getChallengeProxy(){
        return challengeProxy;
    }

    public GoogleApiClient getGoogleApiClient(){
        return mGoogleApiClient;
    }

    public LocationSettingsHandler getLocationSettingsHandler(){
        return mLocationSettingsHandler;
    }

    public Challenge.Type getChallengeType() {
        return challengeType;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public double getChallengeGoal() {
        return challengeGoal;
    }

    /**
     * Check <code>ACCESS_FINE_LOCATION</code> permission, if necessary request it.
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
                    Toast.makeText(getApplicationContext(),"Now you can start the challenge.",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),"Permission Denied, you cannot start the challenge.",
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    //TODO: Handle connection failure
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!isIntendedActivityExit) {
            // TODO: decide what to do
            // TODO: decide if onPause too
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!isIntendedActivityExit) {
            challengeProxy.abortChallenge();
            ((AppRunnest) getApplication()).launchDatabaseUpload();
        }
    }

    @Override
    public void onBackPressed() {
        backToSideBtn.performClick();
    }

    private void dialogQuitChallenge(){

        new AlertDialog.Builder(this, R.style.DarkDialogs)
                .setTitle("Quit Challenge")
                .setMessage("Are you sure you want to to quit your current Challenge?")
                .setPositiveButton(R.string.quit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        challengeProxy.abortChallenge();
                        aborted = true;
                        win = false;

                        ((ChallengeSenderFragment)senderFragment).endChallenge();
                        ((ChallengeReceiverFragment)receiverFragment).stopRun();
                        isIntendedActivityExit = true;
                        endChallenge();
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
                .setTitle("Stop Waiting")
                .setMessage("Are you sure you want to stop waiting and go back?")
                .setPositiveButton(R.string.quit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (owner && !isOpponentThere) {
                            // If you are still in the room alone
                            challengeProxy.deleteChallenge();
                        } else {
                            // If opponent is already here and he needs notification
                            challengeProxy.abortChallenge();
                        }

                        // Set user as available
                        new FirebaseHelper().
                                setUserAvailable(((AppRunnest) getApplication()).getUser().getEmail(), false, true);

                        Intent returnIntent = new Intent();
                        setResult(SideBarActivity.REQUEST_STOP_WAITING, returnIntent);
                        isIntendedActivityExit = true;
                        finish();
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

    private void goToChallengeRecap(){

        Intent returnIntent = new Intent();
        setResult(SideBarActivity.REQUEST_END_CHALLENGE, returnIntent);
        finish();
    }
}
