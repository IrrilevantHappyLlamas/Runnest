package ch.epfl.sweng.project;

import android.support.multidex.MultiDexApplication;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseUser;

import ch.epfl.sweng.project.Database.DBHelper;
import ch.epfl.sweng.project.Database.DBSync;

/**
 * Application class
 */
public class AppRunnest extends MultiDexApplication {

    private GoogleApiClient mApiClient = null;
    private GoogleSignInAccount mGoogleUser = null;

    public void setGoogleUser(GoogleSignInAccount user) {
        this.mGoogleUser = user;
    }

    public GoogleSignInAccount getGoogleUser() {
        return mGoogleUser;
    }

    public GoogleApiClient getApiClient() {
        return mApiClient;
    }

    public void setApiClient(GoogleApiClient apiClient) {
        this.mApiClient = apiClient;
    }
}
