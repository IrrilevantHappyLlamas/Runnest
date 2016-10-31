package ch.epfl.sweng.project.Model;

import android.net.Uri;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Class that implements the <code>User</code> interface, used when we have an authenticated user in our app
 */
public class AuthenticatedUser implements User {

    private String id = null;
    private String email = null;
    private String familyName = null;
    private String name = null;
    private String photoUrl = null;

    public AuthenticatedUser(GoogleSignInAccount googleAccount) {
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
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
