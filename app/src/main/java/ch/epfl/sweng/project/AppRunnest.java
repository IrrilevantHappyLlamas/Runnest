package ch.epfl.sweng.project;

import android.support.multidex.MultiDexApplication;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/**
 * Application class, temporarily only to enable multidex support
 */
public class AppRunnest extends MultiDexApplication {

    private GoogleSignInAccount user = null;

    /**
     * Set the variable currentUser with a given <code>GoogleSignInAccount</code>, which
     * will be available to every activity of the app.
     *
     * @param user  <code>GoogleSignInAccount</code> to store
     */
    public void setUser(GoogleSignInAccount user) {
        this.user = user;
    }

    /**
     * Getter for currentUser
     *
     * @return  the current user, a <code>GoogleSignInAccount</code>
     */
    public GoogleSignInAccount getUser() {
        return user;
    }
}
