package ch.ihl.runnest.Model;

/**
 * Interface for a user inside our application.
 */
public interface User {

    /**
     * Getter for the unique user Id.
     *
     * @return  Unique user identifier.
     */
    String getId();

    /**
     * Getter for user's email.
     *
     * @return  User's email.
     */
    String getEmail();

    /**
     * Getter for the family name of the user.
     *
     * @return  User's family name.
     */
    String getFamilyName();

    /**
     * Getter for the first name of the user.
     *
     * @return  User's first name.
     */
    String getName();

    /**
     * Getter for the Url that points to the user's profile picture.
     *
     * @return  Url to the profile picture.
     */
    String getPhotoUrl();

    /**
     * Getter for the id of the user on the app remote Firebase instance.
     *
     * @return  Firebase identifier.
     */
    String getFirebaseId();

    /**
     * Getter for the status of a User, who can be either logged in or not.
     *
     * @return  A boolean indicating the login status of the user.
     */
    boolean isLoggedIn();

    /**
     * Setter fot the login status, which is set to false from this method.
     */
    void logoutStatus();

    /**
     * Mirror method to logoutStatus() which sets user's login status to true.
     */
    void loginStatus();
}
