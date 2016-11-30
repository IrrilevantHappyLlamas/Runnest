package ch.epfl.sweng.project.Activities;

import android.Manifest;
import android.app.Activity;
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
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
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
    private ChallengeType challengeType;
    private double challengeGoal;   // time in milliseconds or distance in Km
    private boolean win;

    // ChallengeProxy
    private ChallengeProxy challengeProxy;
    private Boolean owner = false;
    private Boolean opponentReady = false;
    private Boolean userReady = false;
    private Boolean opponentFinished = false;
    private Boolean userFinished = false;
    private boolean isEmergencyUploadNecessary = true;
    private String opponentName;
    private String challengeId;
    ChallengeProxy.Handler proxyHandler;

    // Fragments
    private FragmentManager fragmentManager;
    private Fragment senderFragment;
    private Fragment receiverFragment;

    // GUI
    private Button readyBtn;
    private Chronometer chronometer;
    private TextView opponentTxt;
    private TextView userTxt;
    private Button backToSideBtn;

    //others
    private int phase = BEFORE_CHALLENGE;
    private boolean aborted = false;


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

        opponentName = extra.getString("opponent");
        owner = extra.getBoolean("owner");
        challengeType = (ChallengeType) intent.getSerializableExtra("type");
        challengeId = extra.getString("msgId");

        int firstValue = intent.getIntExtra("firstValue", 0);
        int secondValue = intent.getIntExtra("secondValue", 0);
        switch (challengeType) {
            case DISTANCE:
                challengeGoal = firstValue + secondValue / 1000.0;
                break;
            case TIME:
                if(((AppRunnest)getApplication()).isTestSession()) {
                    challengeGoal = 1000;
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
            }

            @Override
            public void isReady() {
                opponentReady = true;
                opponentTxt.setText(R.string.opponent_ready);

                if(userReady) {
                    startChallenge();
                }
            }

            @Override
            public void isFinished() {
                opponentFinished = true;
                opponentTxt.setVisibility(View.VISIBLE);

                ((ChallengeReceiverFragment)receiverFragment).stopRun();

                switch (challengeType) {
                    case TIME:
                        opponentTxt.setText("Opponent has finished!" +
                                "\n" +
                                "Final distance: " +
                                (int)((ChallengeReceiverFragment)receiverFragment).getRun().getTrack().getDistance() +
                                " m");
                        break;
                    case DISTANCE:
                        long opponentDuration = (SystemClock.elapsedRealtime() - chronometer.getBase())/1000;
                        opponentTxt.setText("Opponent completed " +
                                challengeGoal +
                                " Km in " +
                                transformDuration(opponentDuration));
                        break;
                }

                fragmentManager.beginTransaction().remove(receiverFragment).commitAllowingStateLoss();

                if (userFinished) {
                    isEmergencyUploadNecessary = false;
                    endChallenge();
                }
            }

            @Override
            public void hasAborted() {
                // TODO (Toby to Rick): when the opponent exits the challenge, you should do the same
                aborted = true;
                win = true;
                //this.isFinished();
                //imFinished();
                ((ChallengeSenderFragment)senderFragment).endChallenge();
                ((ChallengeReceiverFragment)receiverFragment).stopRun();
                endChallenge();
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
        userTxt.setVisibility(View.VISIBLE);

        switch (challengeType) {
            case TIME:
                userTxt.setText("You have Finished!" +
                        "\n" +
                        "Final distance:  " +
                        (int)((ChallengeSenderFragment)senderFragment).getRun().getTrack().getDistance() +
                        " m");
                break;
            case DISTANCE:
                userTxt.setText("You have completed " +
                        challengeGoal +
                        " Km in " +
                        transformDuration(((ChallengeSenderFragment)senderFragment).getRun().getDuration()));
                break;
        }

        fragmentManager.beginTransaction().remove(senderFragment).commitAllowingStateLoss();

        challengeProxy.imFinished();

        if (opponentFinished) {
            isEmergencyUploadNecessary = false;
            endChallenge();
        }
    }

    public void startChallenge(){

        challengeProxy.startChallenge();

        readyBtn.setVisibility(View.GONE);
        userTxt.setVisibility(View.GONE);
        opponentTxt.setVisibility(View.GONE);
        backToSideBtn.setText("quit");

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
                        String timeLeft = "Time left: " + transformDuration(millisUntilFinished/1000);
                        chronometer.setText(timeLeft);
                    }

                    public void onFinish() {
                        String timeIsUp = "Time's up!";
                        chronometer.setText(timeIsUp);
                        ((ChallengeSenderFragment)senderFragment).endChallenge();
                        imFinished();
                    }
                }.start();
                break;
        }

        chronometer.setVisibility(View.VISIBLE);
    }

    private void setupGUI() {
        chronometer = (Chronometer) findViewById(R.id.challenge_chronometer);
        chronometer.setVisibility(View.INVISIBLE);

        opponentTxt = (TextView) findViewById(R.id.opponentTxt);
        userTxt = (TextView) findViewById(R.id.userTxt);

        backToSideBtn = (Button) findViewById(R.id.back_to_side_btn);
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
                    userTxt.setVisibility(View.VISIBLE);

                    // In a test session we don't want to wait for the opponent
                    if (opponentReady || ((AppRunnest)getApplication()).isTestSession()){
                        startChallenge();
                    }
                }
            }
        });
    }

    private void endChallenge() {
        chronometer.stop();
        phase = AFTER_CHALLENGE;
        backToSideBtn.setText("back");
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

        // Save challenge into the database
        Challenge challengeToSave = new Challenge(opponentName, userRun, opponentRun);
        DBHelper dbHelper = new DBHelper(this);
        dbHelper.insert(challengeToSave);

        // Update statistic
        FirebaseHelper.RunType challengeResult;
        if (win) {
            challengeResult = FirebaseHelper.RunType.CHALLENGE_WON;
        } else {
            challengeResult = FirebaseHelper.RunType.CHALLENGE_LOST;
        }
        FirebaseHelper fbHelper = new FirebaseHelper();
        User currentUser = ((AppRunnest) this.getApplication()).getUser();
        fbHelper.updateUserStatistics(currentUser.getEmail(),
                userRun.getDuration(),
                userRun.getTrack().getDistance(),
                challengeResult);

        // Go to recap challenge
        goToChallengeRecap();
    }

    private String transformDuration(long duration) {
        if (duration < 0) {
            throw new IllegalArgumentException("Duration could not be negative");
        }

        return (duration/60 + "' " + duration%60 + "''");
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

    public ChallengeType getChallengeType() {
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

    //TODO: decide if onPause too

    /*@Override
    public void onStop() {
        super.onStop();
        challengeProxy.abortChallenge();
        if (isEmergencyUploadNecessary) {
            //TODO: (update database with current challenge??) happens even if activity is just put in background
            ((AppRunnest) getApplication()).launchEmergencyUpload();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        challengeProxy.abortChallenge();
        if (isEmergencyUploadNecessary) {
            //TODO: (Toby -> ?) (update database with current challenge??)
            ((AppRunnest) getApplication()).launchEmergencyUpload();
        }
    }*/

    @Override
    public void onBackPressed() {
        backToSideBtn.performClick();
    }

    private void dialogQuitChallenge(){

        new AlertDialog.Builder(this)
                .setTitle("Quit Challenge")
                .setMessage("Are you sure you want to to quit your current Challenge?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        challengeProxy.abortChallenge();
                        aborted = true;
                        win = false;

                        //proxyHandler.isFinished();
                        //imFinished();

                        ((ChallengeSenderFragment)senderFragment).endChallenge();
                        ((ChallengeReceiverFragment)receiverFragment).stopRun();
                        endChallenge();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_delete)
                .show();
    }


    private void stopWaitingForOpponent(){

        new AlertDialog.Builder(this)
                .setTitle("Stop Waiting")
                .setMessage("Are you sure you want to stop waiting and go back?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO delete challenge from firebase?

                        Intent returnIntent = new Intent();
                        setResult(SideBarActivity.REQUEST_STOP_WAITING, returnIntent);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
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

    public enum ChallengeType{TIME, DISTANCE}
}
