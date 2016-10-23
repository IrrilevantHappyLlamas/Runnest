package ch.epfl.sweng.project;

import android.support.multidex.MultiDexApplication;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseUser;

/**
 * Application class
 */
public class AppRunnest extends MultiDexApplication {

    private GoogleApiClient mApiClient = null;
    private GoogleSignInAccount mGoogleUser = null;

    // Not really needed, can be accessed from anywhere
    private FirebaseUser mFirebaseUser = null;

    public void setGoogleUser(GoogleSignInAccount user) {
        this.mGoogleUser = user;
    }

    public GoogleSignInAccount getGoogleUser() {
        return mGoogleUser;
    }

    public FirebaseUser getFirebaseUser() {
        return mFirebaseUser;
    }

    public void setFirebaseUser(FirebaseUser mFirebaseUser) {
        this.mFirebaseUser = mFirebaseUser;
    }

    public GoogleApiClient getApiClient() {
        return mApiClient;
    }

    public void setApiClient(GoogleApiClient apiClient) {
        this.mApiClient = apiClient;
    }
}
