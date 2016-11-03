package ch.epfl.sweng.project.Model;

import android.net.Uri;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.ArrayList;

public class Profile {

    private final String id;
    private final String email;
    private final String familyName;
    private final String name;

    private final String photoUrl;

    private ArrayList<Run> runs;
    private float totalDistance;


    public Profile(GoogleSignInAccount account) {

        if (account == null) {
            id = "No User";
            email = "no.user@invalid.null";
            familyName = "No User";
            name = "No User";
            photoUrl = "";
        } else {
            id = account.getId();
            email = account.getEmail();
            familyName = account.getFamilyName();
            name = account.getDisplayName();
            Uri uri = account.getPhotoUrl();
            if (uri != null) {
                photoUrl = uri.toString();
            } else {
                photoUrl = "";
            }
        }
        totalDistance = 0;
        runs = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getName() {
        return name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public float getTotalDistance(){
        return totalDistance;
    }


    /**
     * Adds a run to the profile history.
     *
     * @param run
     */
    public void addRun(Run run) {
        runs.add(run);
        totalDistance += run.getTrack().getDistance();
    }


    /**
     * Converts the email to allow the firebase storage
     *
     * @return email for firebase
     */
    public String getFireBaseMail() {
        String fireBaseMail = email.replace(".", "_dot_");
        fireBaseMail = fireBaseMail.replace("@", "_at_");
        return fireBaseMail;
    }

}
