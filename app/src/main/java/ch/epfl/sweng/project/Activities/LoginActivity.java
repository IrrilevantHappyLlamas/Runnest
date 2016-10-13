package ch.epfl.sweng.project.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

/**
 * Launch activity which implements google authentication
 */
public final class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    private SignInButton signInButton;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Configure sign-in to request the user's ID, email address, and basic profile.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the options specified by gso.
        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(AppIndex.API).build();

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

        // Customize sign-in button
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
    }

    /**
     * Call after the Sign In button is pressed. Starts a SingInIntent using the GoogleApiClient
     * configuration and an activity for result with said Intent.
     */
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * On top of calling the super method to handle results sent by the running activity,
     * additionally check if the request code matches. If it does, get the sign in result
     * and call the handling function.
     *
     * @param requestCode   code of the request, an <code>int</code>
     * @param resultCode    code of the result, an <code>int</code>
     * @param data          <code>Intent</code> from which to get the result
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    /**
     * Handles the result of the sign up call. If the result is successful, proceed to the rest of
     * the application. Otherwise display an error <code>Toast</code>
     *
     * @param result    the result of the signup call, a <code>GoogleSignInResult</code>
     */
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {

            // Signed in successfully, show success toast
            GoogleSignInAccount acct = result.getSignInAccount();
            Toast.makeText(getBaseContext(), "Login succesful " + acct.getId(), Toast.LENGTH_LONG).show();

            // Start SideBarActivity
            Intent sideBarIntent = new Intent(this, SideBarActivity.class);
            fetchAccountInfo(acct, sideBarIntent);
            startActivity(sideBarIntent);

        } else {
            Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        }
    }

    // TODO: implement passing account informations to the rest of the app
    /**
     * Fetches informations from a <code>GoogleSignInAccount</code> and puts them as extras
     * into an <code>Intent</code>.
     *
     * @param acct      the <code>GoogleSignInAccount</code> from which to fetch informations
     * @param intent    <code>Intent</code> in which to put info as extras
     */
    private void fetchAccountInfo(GoogleSignInAccount acct, Intent intent) {
        intent.putExtra("id", acct.getId());
        intent.putExtra("email", acct.getEmail());
        intent.putExtra("familyName", acct.getFamilyName());
        intent.putExtra("name", acct.getGivenName());
    }

    /**
     * Called after the Sign Out button has been pressed.
     */
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
            }
        });
    }

    // TODO: improve the flow of transitions between activities and fragments, including back calls
    @Override
    public void onBackPressed() {
        // disable going back
        moveTaskToBack(true);
    }

    // TODO: handle connection failure to google services
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    /**
     * Called when one of the button of the activity has been pressed, handles the diferent actions.
     *
     * @param v the <code>View</code> that triggered the call.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                Log.d(TAG, "clickSignInBtn:");
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
        }
    }
}


