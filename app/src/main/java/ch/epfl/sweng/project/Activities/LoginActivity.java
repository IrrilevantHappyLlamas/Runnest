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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import ch.epfl.sweng.project.AppRunnest;
import ch.epfl.sweng.project.Model.AuthenticatedUser;

/**
 * Launch activity which implements google authentication
 */
@SuppressWarnings("CastToConcreteClass")
public final class LoginActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInOptions mGso = null;
    private FirebaseAuth mFirebaseAuth = null;
    private FirebaseAuth.AuthStateListener mAuthListener = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setUpGoogleAuth();
        setUpFirebaseAuth();
        setUpLoginUI();
    }

    private void setUpLoginUI() {
        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        // Customize sign-in button
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        //noinspection deprecation
        signInButton.setScopes(mGso.getScopeArray());
    }

    private void setUpGoogleAuth() {
        // Configure sign-in to request the user's ID, email address, idToken and basic profile.
        mGso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the options specified by gso
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGso)
                .addApi(AppIndex.API).build();

        ((AppRunnest) getApplication()).setApiClient(googleApiClient);
    }

    private void setUpFirebaseAuth() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    /**
     * Call after the Sign In button is pressed. Starts a SingInIntent using the GoogleApiClient
     * configuration and an activity for result with said Intent.
     */
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(((AppRunnest)getApplication()).getApiClient());
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Called after the Sign Out button has been pressed.
     */
    private void signOut() {
        Auth.GoogleSignInApi.signOut(((AppRunnest)getApplication()).getApiClient()).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status r) {
                    }
                });
    }

    /**
     * Handles the result of the sign up call. If the result is successful, proceed to the rest of
     * the application. Otherwise display an error <code>Toast</code>
     *
     * @param result    the result of the signup call, a <code>GoogleSignInResult</code>
     */
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {

            // Signed in successfully, show success toast
            GoogleSignInAccount acct = result.getSignInAccount();
            ((AppRunnest)getApplication()).setUser(new AuthenticatedUser(acct));
            firebaseAuthWithGoogle(acct);

            Toast.makeText(getBaseContext(), "Login successful", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        }
    }

    private void startSideBarActivity() {
        // Start SideBarActivity
        Intent sideBarIntent = new Intent(this, SideBarActivity.class);
        startActivity(sideBarIntent);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        startSideBarActivity();

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                        }
                    }
                });
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

    @Override
    public void onBackPressed() {
        // disable going back
        moveTaskToBack(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * Handle connection failure
     *
     * @param connectionResult failing result connection
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getBaseContext(), "Check your connection and retry", Toast.LENGTH_LONG).show();
    }

    /**
     * Called when one of the button of the activity has been pressed, handles the different actions.
     *
     * @param v the <code>View</code> that triggered the call.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                Log.d(TAG, "clickSignInBtn:");
                signOut();
                signIn();
                break;
        }
    }

}