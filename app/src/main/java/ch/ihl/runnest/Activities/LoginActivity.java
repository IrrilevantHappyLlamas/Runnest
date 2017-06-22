package ch.ihl.runnest.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.android.multidex.ch.ihl.runnest.AppRunnest.R;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import ch.ihl.runnest.AppRunnest;
import ch.ihl.runnest.Model.AuthenticatedUser;

/**
 * Launch activity of Runnest. It presents the login screen to the user, which contains a button that allows to
 * authenticate with a valid Google account. The login screen handles missing internet connection and failing to
 * get a valid authentication token, notifying the user and not letting him proceed to the rest of the application.
 *
 * The part which allows to insert the authentication credential is delegated to the Google API, that handles it
 * automatically and prompts the user to input all that is needed to login.
 */
public final class LoginActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final int RC_SIGN_IN = 9001;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Setup internet check
        ((AppRunnest) getApplication()).setNetworkHandler();

        // Setup UI
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        // Start authentication process
        setUpGoogleAuth();
    }

    private void setUpGoogleAuth() {
        // Configure sign-in to request the user's ID, email address, idToken and basic profile.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the options specified by gso.
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(AppIndex.API).build();

        // Set GoogleApiClient into AppRunnest instance to be used in the rest of the application.
        ((AppRunnest) getApplication()).setApiClient(googleApiClient);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            ((AppRunnest)getApplication()).setUser(new AuthenticatedUser(result.getSignInAccount()));
            firebaseAuthWithGoogle(result.getSignInAccount());
        } else {
            loginFailed();
        }
    }

    private void startSideBarActivity() {
        // Start SideBarActivity
        startActivity(new Intent(this, SideBarActivity.class));
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        FirebaseAuth.getInstance().signInWithCredential(GoogleAuthProvider.getCredential(acct.getIdToken(), null))
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startSideBarActivity();
                        } else {
                            // To always present the user the same login failure message and process we handle a
                            // firebase failure by simply prompting him to redo the whole authentication process.
                            loginFailed();
                        }
                    }
                });
    }

    private void signIn() {
        startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(((AppRunnest)getApplication()).getApiClient()), RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(((AppRunnest)getApplication()).getApiClient()).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status r) {
                        // Sign out from google before trying to login. It might fail, either because no one was logged
                        // in in the first place or for unknown reasons. We let the subsequent login attempt still try
                        // to login and handle a failure only there.
                    }
                });
    }

    private void loginFailed() {
        findViewById(R.id.sign_in_button).setEnabled(true);
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
    }

    /**
     * On top of calling the super method to handle results sent by the running activity,
     * additionally check if the request code matches. If it does, get the sign in result
     * and call the handling function.
     *
     * @param requestCode   Code of the request.
     * @param resultCode    Code of the result.
     * @param data          Intent from which to get the result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            handleSignInResult(Auth.GoogleSignInApi.getSignInResultFromIntent(data));
        } else {
            loginFailed();
        }
    }

    @Override
    public void onClick(View v) {
        if(((AppRunnest)getApplication()).getNetworkHandler().isConnected()) {
            switch (v.getId()) {
                case R.id.sign_in_button:
                    findViewById(R.id.sign_in_button).setEnabled(false);
                    signOut();
                    signIn();
                    break;
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getBaseContext(), "Check your connection and retry", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        // disable going back
        moveTaskToBack(true);
    }
}