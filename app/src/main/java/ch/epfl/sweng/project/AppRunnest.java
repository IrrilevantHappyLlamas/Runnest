package ch.epfl.sweng.project;

import android.support.multidex.MultiDexApplication;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Application class, temporarily only to enable multidex support
 */
public class AppRunnest extends MultiDexApplication {

    private GoogleApiClient mApiClient = null;
    private GoogleSignInAccount mUser = null;

    /**
     * Set the variable currentUser with a given <code>GoogleSignInAccount</code>, which
     * will be available to every activity of the app.
     *
     * @param user  <code>GoogleSignInAccount</code> to store
     */
    public void setUser(GoogleSignInAccount user) {
        this.mUser = user;
    }

    /**
     * Getter for currentUser
     *
     * @return  the current user, a <code>GoogleSignInAccount</code>
     */
    public GoogleSignInAccount getUser() {
        return mUser;
    }


    public GoogleApiClient getApiClient() {
        return mApiClient;
    }

    public void setApiClient(GoogleApiClient apiClient) {
        this.mApiClient = apiClient;
    }
}
