package ch.epfl.sweng.project.Activities;

import android.Manifest;
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
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import ch.epfl.sweng.project.AppRunnest;
import ch.epfl.sweng.project.Firebase.FirebaseProxy;
import ch.epfl.sweng.project.Fragments.RunFragments.ChallengeReceiverFragment;
import ch.epfl.sweng.project.Fragments.RunFragments.ChallengeSenderFragment;
import ch.epfl.sweng.project.Fragments.RunFragments.LocationSettingsHandler;
import ch.epfl.sweng.project.Model.ChallengeProxy;
import ch.epfl.sweng.project.Model.CheckPoint;
import ch.epfl.sweng.project.Model.Run;

import static ch.epfl.sweng.project.Activities.SideBarActivity.PERMISSION_REQUEST_CODE_FINE_LOCATION;

public class ChallengeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Location
    private GoogleApiClient mGoogleApiClient;
    private LocationSettingsHandler mLocationSettingsHandler;

    // Challenge type
    private ChallengeType challengeType = ChallengeType.TIME;
    private double challengeGoal = 10000;

    // ChallengeProxy
    private ChallengeProxy challengeProxy;
    private Boolean owner = false;
    private Boolean opponentReady = false;
    private Boolean userReady = false;
    private Boolean opponentFinished = false;
    private Boolean userFinished = false;
    private String opponentName;

    // Fragments
    private FragmentManager fragmentManager;
    private Fragment senderFragment;
    private Fragment receiverFragment;

    // GUI
    private Button readyBtn;
    private Chronometer chronometer;
    private TextView opponentTxt;
    private TextView userTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        //TODO extract intents: challengeType, challengeGoal, owner

        opponentName = getIntent().getExtras().getString("opponent");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        createProxy();

        mLocationSettingsHandler = new LocationSettingsHandler(mGoogleApiClient, this);

        //Initializing the fragment
        fragmentManager = getSupportFragmentManager();
        senderFragment = fragmentManager.findFragmentById(R.id.sender_container);
        receiverFragment = fragmentManager.findFragmentById(R.id.receiver_container);

        // Setup Chronometer
        chronometer = (Chronometer) findViewById(R.id.challenge_chronometer);
        chronometer.setVisibility(View.INVISIBLE);

        opponentTxt = (TextView) findViewById(R.id.opponentTxt);
        userTxt = (TextView) findViewById(R.id.userTxt);
        readyBtn = (Button) findViewById(R.id.readyBtn);
        readyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermission() && mLocationSettingsHandler.checkLocationSettings()) {
                    challengeProxy.imReady();
                    userReady = true;
                    readyState();

                    // In a test session we don't want to wait for the opponent
                    if (opponentReady || ((AppRunnest)getApplication()).isTestSession()){
                        startChallenge();
                    }
                }
            }
        });

        opponentReady = false;
        userReady = false;
    }

    /**
     * Switch to ready state
     */
    private void readyState(){
        readyBtn.setVisibility(View.GONE);
        userTxt.setVisibility(View.VISIBLE);
    }

    private void createProxy(){

        String userName = ((AppRunnest)getApplication()).getUser().getName();

        ChallengeProxy.Handler proxyHandler = new ChallengeProxy.Handler() {
            @Override
            public void OnNewDataHandler(CheckPoint checkPoint) {
                ((ChallengeReceiverFragment)receiverFragment).onNewData(checkPoint);
            }

            @Override
            public void isReadyHandler() {
                opponentReady = true;
                opponentTxt.setText(R.string.opponent_ready);

                if(userReady) {
                    startChallenge();
                }
            }

            @Override
            public void isFinished() {
                opponentFinished = true;
                fragmentManager.beginTransaction().remove(receiverFragment).commit();
                opponentTxt.setVisibility(View.VISIBLE);

                switch (challengeType) {
                    case TIME:
                        opponentTxt.setText("Opponent has finished!" +
                                "\n" +
                                "Final distance: " +
                                ((ChallengeReceiverFragment)receiverFragment).getRun().getTrack().getDistance());
                        break;
                    case DISTANCE:
                        //TODO
                        long opponentDuration = SystemClock.elapsedRealtime() - chronometer.getBase();
                        //TODO show min and sec
                        opponentTxt.setText("Opponent completed " + challengeGoal + " Km in " + opponentDuration);
                        ((ChallengeReceiverFragment)receiverFragment).stopRun();
                        break;
                }

                if(userFinished) {
                    endChallenge();
                }
            }
        };

        challengeProxy = new FirebaseProxy(userName, opponentName, proxyHandler, owner);
    }

    public void imFinished() {
        userFinished = true;
        fragmentManager.beginTransaction().remove(senderFragment).commit();
        userTxt.setVisibility(View.VISIBLE);

        switch (challengeType) {
            case TIME:
                userTxt.setText("You have Finished!" +
                        "\n" +
                        "Final distance: " +
                        ((ChallengeSenderFragment)senderFragment).getRun().getTrack().getDistance());
                break;
            case DISTANCE:
                //TODO show min and sec
                opponentTxt.setText("You have completed " +
                        challengeGoal +
                        " Km in " +
                        ((ChallengeSenderFragment)senderFragment).getRun().getDuration());
                break;
        }

        challengeProxy.imFinished();

        if(opponentFinished) {
            endChallenge();
        }
    }

    private void endChallenge() {
        //TODO: update stats, save challenge into DB and launch next fragment/activity

        // opponentName
        Run opponentRun = ((ChallengeReceiverFragment)receiverFragment).getRun();
        Run userRun = ((ChallengeSenderFragment)senderFragment).getRun();
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

    public void startChallenge(){

        challengeProxy.startChallenge();

        readyBtn.setVisibility(View.GONE);
        userTxt.setVisibility(View.GONE);
        opponentTxt.setVisibility(View.GONE);

        receiverFragment = new ChallengeReceiverFragment();
        fragmentManager.beginTransaction().add(R.id.receiver_container, receiverFragment).commit();

        senderFragment = new ChallengeSenderFragment();
        fragmentManager.beginTransaction().add(R.id.sender_container, senderFragment).commit();

        setupChronometer();

    }

    public void setupChronometer() {
        switch (challengeType) {
            case DISTANCE:
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                break;
            case TIME:
                //TODO verify that cast to long isn't a problem
                new CountDownTimer((long)challengeGoal, 1000) {

                    public void onTick(long millisUntilFinished) {
                        chronometer.setText("Time left: " + millisUntilFinished/1000);
                    }

                    public void onFinish() {
                        chronometer.setText("Time's up!");
                        imFinished();
                    }
                }.start();
                break;
        }

        chronometer.setVisibility(View.VISIBLE);
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

    public double getChallengeGoal() {
        return challengeGoal;
    }

    public enum ChallengeType{TIME, DISTANCE}
}
