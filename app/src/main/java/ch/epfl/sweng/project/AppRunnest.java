package ch.epfl.sweng.project;

import android.support.multidex.MultiDexApplication;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/**
 * Application class, temporarly only to enable multidex support
 *
 * @author Tobia Albergoni
 */
public class AppRunnest extends MultiDexApplication {

    private GoogleSignInAccount currentUser;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void setCurrentUser(GoogleSignInAccount user) {
        currentUser = user;
    }

    public GoogleSignInAccount getCurrentUser() {
        return currentUser;
    }
}
