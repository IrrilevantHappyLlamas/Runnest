package ch.epfl.sweng.project.Model;

import android.net.Uri;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Class that implements the User interface, used when we have an authenticated user in our app.
 */
public class AuthenticatedUser implements User {

    private String id = null;
    private String email = null;
    private String familyName = null;
    private String name = null;
    private String photoUrl = null;
    private boolean isLoggedIn = false;

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

        loginStatus();
        id = googleAccount.getId();
        email = googleAccount.getEmail();
        familyName = googleAccount.getFamilyName();
        name = googleAccount.getDisplayName();
        Uri uri = googleAccount.getPhotoUrl();
        if (uri != null) {
            photoUrl = uri.toString();
        } else {
            photoUrl = "";
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getFamilyName() {
        return familyName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPhotoUrl() {
        return photoUrl;
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
