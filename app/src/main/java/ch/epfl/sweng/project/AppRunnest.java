package ch.epfl.sweng.project;

import android.support.multidex.MultiDexApplication;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;

import ch.epfl.sweng.project.Model.TestUser;
import ch.epfl.sweng.project.Model.User;

/**
 * Application class
 */
public class AppRunnest extends MultiDexApplication {

    private GoogleApiClient mApiClient = null;
    private User mCurrentUser = new TestUser();

    public void setUser(User user) {
        this.mCurrentUser = user;
    }

    public User getUser() {
        return mCurrentUser;
    }

    public GoogleApiClient getApiClient() {
        return mApiClient;
    }

    public void setApiClient(GoogleApiClient apiClient) {
        this.mApiClient = apiClient;
    }
}
