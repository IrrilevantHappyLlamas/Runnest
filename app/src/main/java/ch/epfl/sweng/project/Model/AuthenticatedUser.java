package ch.epfl.sweng.project.Model;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Class that implements the User interface, used when we have an authenticated user in our app.
 */
public class AuthenticatedUser implements User {

    private boolean isLoggedIn = false;
    private GoogleSignInAccount googleAccount = null;

    /**
     * Public constructor for an authenticated user. It takes as a parameter a GoogleSignInAccount
     * object which contains all the credentials and information about the user. This parameter can't be null.
     * The user info is extracted from this object and stored in the class fields.
     *
     * @param googleAccount     contains user information retrieved after a successful google authentication
     */
    public AuthenticatedUser(GoogleSignInAccount googleAccount) {

        if(googleAccount == null) {
            throw new IllegalArgumentException("You can't instantiate an authenticated user " +
                    "without a valid GoogleSignInAccount");
        }

        this.googleAccount = googleAccount;
        loginStatus();
    }

    @Override
    public String getId() {
        return googleAccount.getId();
    }

    @Override
    public String getEmail() {
        return googleAccount.getEmail();
    }

    @Override
    public String getFamilyName() {
        return googleAccount.getFamilyName();
    }

    @Override
    public String getName() {
        return googleAccount.getDisplayName();
    }

    @Override
    public String getPhotoUrl() {
        return (googleAccount.getPhotoUrl() == null)?"":googleAccount.getPhotoUrl().toString();
    }

    @Override
    public String getFirebaseId() {
        //noinspection ConstantConditions
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    @Override
    public void logoutStatus() {
        isLoggedIn = false;
    }

    @Override
    public void loginStatus() {
        isLoggedIn = true;
    }

}
