package ch.epfl.sweng.project.Model;

import android.net.Uri;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.ArrayList;

/**
 * Profile class, store information about the user
 *
 * @author Riccardo Conti
 */
public class Profile {

    private final String id;
    private final String email;
    private final String familyName;
    private final String name;

    private final String photoUrl;

    private ArrayList<Run> runs;
    private float totalDistance;


    public Profile(GoogleSignInAccount account) {
        this.id = account.getId();
        this.email = account.getEmail();
        this.familyName = account.getFamilyName();
        this.name = account.getDisplayName();
        Uri uri = account.getPhotoUrl();
        if(uri != null) {
            this.photoUrl =uri.toString();
        } else {
            photoUrl = "";
        }

        runs = new ArrayList<Run>();
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

}
