package ch.epfl.sweng.project.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.project.Firebase.FirebaseProxy;
import ch.epfl.sweng.project.Model.ChallangeProxy;
import ch.epfl.sweng.project.Model.CheckPoint;

public class FirebaseProxyActivity extends AppCompatActivity {

    private EditText localUserTxt = null;
    private EditText remoteUserTxt = null;
    private EditText latitudeTxt = null;
    private EditText longitudeTxt = null;

    private Button setupChallengeBtn = null;
    private Button localUserIsReadyBtn = null;
    private Button remoteUserIsReadyBtn = null;
    private Button putDataBtn = null;
    private Button resetBtn = null;

    private TextView statusTxt = null;
    private TextView challengeStatusTxt = null;

    private ListView dataList = null;

    private boolean localUserIsReady = false;
    private boolean remoteUserIsReady = false;

    private FirebaseProxy firebaseProxy = null;
    private List<CheckPoint> retrievedData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_proxy);

        setupUI();
        attachButtonListeners();
    }

    private void attachButtonListeners() {

        setupChallengeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String localUser = localUserTxt.getText().toString();
                String remoteOpponent = remoteUserTxt.getText().toString();

                if (!localUser.isEmpty() && !remoteOpponent.isEmpty()) {
                    firebaseProxy = new FirebaseProxy(localUser, remoteOpponent, createHandler());
                    setupChallengeBtn.setEnabled(false);
                    localUserIsReadyBtn.setEnabled(true);
                    remoteUserIsReadyBtn.setEnabled(true);
                }
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relaunch();
            }
        });

        localUserIsReadyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localUserIsReady = true;
                firebaseProxy.imReady();
                localUserIsReadyBtn.setEnabled(false);
            }
        });

        putDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putData();
            }
        });
    }

    private void relaunch() {
        Intent proxyIntent = new Intent(this, FirebaseProxyActivity.class);
        startActivity(proxyIntent);
    }

    private void putData() {
        String latitude = latitudeTxt.getText().toString();
        String longitude = longitudeTxt.getText().toString();

        if(!latitude.isEmpty() && !longitude.isEmpty()) {
            firebaseProxy.putData(new CheckPoint(Double.parseDouble(latitude), Double.parseDouble(longitude)));
        }

    }

    private void setupUI() {
        localUserTxt = (EditText) findViewById(R.id.local_user);
        remoteUserTxt = (EditText) findViewById(R.id.remote_opponent);
        latitudeTxt = (EditText) findViewById(R.id.checkpoint_latitude);
        longitudeTxt = (EditText) findViewById(R.id.checkpoint_longitude);

        resetBtn = (Button) findViewById(R.id.reset_btn);
        setupChallengeBtn = (Button) findViewById(R.id.set_challenge);
        localUserIsReadyBtn = (Button) findViewById(R.id.local_user_ready);
        localUserIsReadyBtn.setEnabled(false);
        remoteUserIsReadyBtn = (Button) findViewById(R.id.remote_opponent_ready);
        remoteUserIsReadyBtn.setEnabled(false);
        putDataBtn = (Button) findViewById(R.id.put_checkpoint_btn);
        putDataBtn.setEnabled(false);

        statusTxt = (TextView) findViewById(R.id.status_txt);
        challengeStatusTxt = (TextView) findViewById(R.id.challenge_status_txt);

        dataList = (ListView) findViewById(R.id.data_list);
    }

    private ChallangeProxy.Handler createHandler() {
        ChallangeProxy.Handler activityProxyHandler = new ChallangeProxy.Handler() {
            @Override
            public void OnNewDataHandler(CheckPoint checkPoint) {
                listCheckPoint(checkPoint);
            }

            @Override
            public void isReadyHandler() {
                remoteUserIsReady = true;

                if(remoteUserIsReady && localUserIsReady) {
                    Log.i("test", "isReadyHandler");
                    challengeStatusTxt.setText("Challenge started");
                    localUserIsReadyBtn.setEnabled(false);
                    remoteUserIsReadyBtn.setEnabled(false);
                    putDataBtn.setEnabled(true);
                }
            }
        };

        return activityProxyHandler;
    }

    private void listCheckPoint(CheckPoint checkPoint) {
        retrievedData.add(checkPoint);

        ArrayList<String> checkPoints = new ArrayList<>();
        if (retrievedData.isEmpty()) {
            checkPoints.add("No checkPoint has been retrieved yet.");
        } else {
            for (int i = 0; i < retrievedData.size(); ++i) {
               checkPoints.add("Latitude : " + retrievedData.get(i).getLatitude() +
                        " Longitude : " + retrievedData.get(i).getLongitude());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.simple_list_item, checkPoints);
        dataList.setAdapter(adapter);
    }
}
