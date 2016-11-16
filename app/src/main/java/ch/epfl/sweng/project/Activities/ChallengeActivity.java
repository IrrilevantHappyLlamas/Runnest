package ch.epfl.sweng.project.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import ch.epfl.sweng.project.Fragments.NewRun.LocationSettingsHandler;
import ch.epfl.sweng.project.Model.ChallengeProxy;
import ch.epfl.sweng.project.Model.CheckPoint;

import static ch.epfl.sweng.project.Activities.SideBarActivity.PERMISSION_REQUEST_CODE_FINE_LOCATION;

public class ChallengeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private GoogleApiClient mGoogleApiClient;
    private LocationSettingsHandler mLocationSettingsHandler;
    private ChallengeProxy challengeProxy;

    private Boolean opponentReady = false;
    private Boolean userReady;
    private FragmentManager fragmentManager;
    private Fragment userFragment;
    private Fragment opponentFragment;

    private Button readyBtn;
    private TextView userWaitingTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        createProxy();

        mLocationSettingsHandler = new LocationSettingsHandler(mGoogleApiClient, this);

        //Initializing the fragment
        fragmentManager = getSupportFragmentManager();
        userFragment = fragmentManager.findFragmentById(R.id.user_container);
        opponentFragment = fragmentManager.findFragmentById(R.id.user_container);


        userWaitingTxt = (TextView) findViewById(R.id.userWaitingTxt);
        readyBtn = (Button) findViewById(R.id.readyBtn);
        readyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //FIXME false condition
                if(checkPermission() && mLocationSettingsHandler.checkLocationSettings()) {
                    challengeProxy.imReady();
                    userReady = true;
                    readyState();

                    if (opponentReady){
                        startChallenge();
                    }
                }
            }
        });
    }

    /**
     * Switch to ready state
     */
    private void readyState(){
        readyBtn.setVisibility(View.GONE);
        userWaitingTxt.setVisibility(View.VISIBLE);
    }

    private void createProxy(){
        challengeProxy = new ChallengeProxy() {
            @Override
            public void putData(CheckPoint checkPoint) {

            }

            @Override
            public void setHandler(Handler setHandler) {

            }

            @Override
            public void imReady() {

            }
        };

        challengeProxy.setHandler(new ChallengeProxy.Handler() {
            @Override
            public void OnNewDataHandler(CheckPoint checkPoint) {
                //receiverFragment.onNewData(checkPoint);
            }

            @Override
            public void isReadyHandler() {

            }
        });
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

    public void startChallenge(){

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
                    //TODO Q: se già accettato?
                    //TODO A: Se già accettato non fa la richiesta..quindi non é un problema

                    //TODO Q: Perché gestisci ready qui?
                    userReady = true;
                    challengeProxy.imReady();
                    Toast.makeText(getApplicationContext(),"Ready",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),"Permission Denied, you cannot start a Run.",
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

    /**
     * Called when <code>GoogleApiClient</code> is connected. Try to get the last known location and
     * start location updates if necessary.
     *
     * @param bundle    not used here
     */
    /*@Override
    public void onConnected(@Nullable Bundle bundle) {
        Location location = null;

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            mMapHandler.startShowingLocation();
        }
        if(location != null) {
            mLastCheckPoint = new CheckPoint(location);
        }
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }*/
}
