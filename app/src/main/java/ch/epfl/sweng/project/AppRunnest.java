package ch.epfl.sweng.project;

import android.support.multidex.MultiDexApplication;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/**
 * Application class, temporarly only to enable multidex support
 */
public class AppRunnest extends MultiDexApplication {

    private GoogleSignInAccount currentUser;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Set the variable currentUser with a given <code>GoogleSignInAccount</code>, which
     * will be available to every activity of the app.
     *
     * @param user  <code>GoogleSignInAccount</code> to store
     */
    public void setCurrentUser(GoogleSignInAccount user) {
        currentUser = user;
    }

    /**
     * Getter for currentUser
     *
     * @return  the current user, a <code>GoogleSignInAccount</code>
     */
    public GoogleSignInAccount getCurrentUser() {
        return currentUser;
    }
}
