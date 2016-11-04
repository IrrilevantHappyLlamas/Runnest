package ch.epfl.sweng.project.Model;

/**
 * Interface for a user for our application
 */
public interface User {

    String getId();
    String getEmail();
    String getFamilyName();
    String getName();
    String getPhotoUrl();
    String getFirebaseId();
    boolean isLoggedIn();
    void logoutStatus();
    void loginStatus();
}
