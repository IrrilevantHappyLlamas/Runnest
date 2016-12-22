package ch.epfl.sweng.project;

import android.net.Uri;
import android.support.multidex.MultiDexApplication;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import ch.epfl.sweng.project.Database.DBHelper;
import ch.epfl.sweng.project.Model.TestUser;
import ch.epfl.sweng.project.Model.User;

/**
 * Application class for Runnest. It includes a number of utility methods that have to be accessible from different
 * parts of the code. It includes ways to get the current user, check for Internet connection, launch database uploads
 * and determine if the app is running a test session.
 *
 * It extends MultiDexApplication to enable MultiDex support for the App.
 */
public class AppRunnest extends MultiDexApplication {

    private NetworkHandler networkHandler = null;
    private GoogleApiClient apiClient = null;
    // The user is a TestUser by default
    private User currentUser = new TestUser();
    private boolean isTestSession = false;

    /**
     * Setter for the current user of the app.
     *
     * @param user  User to set as current one, must be non null.
     */
    public void setUser(User user) {

        if (user == null) {
            throw new IllegalArgumentException("Current user can't be null");
        }

        this.currentUser = user;
    }

    /**
     * Getter for the current user.
     *
     * @return      Current User.
     */
    public User getUser() {
        return currentUser;
    }

    /**
     * Setter for the GoogleApiClient that is used both for login and location request purposes.
     *
     * @param apiClient     The GoogleApiClient used throughout the App, must be non null.
     */
    public void setApiClient(GoogleApiClient apiClient) {

        if (apiClient == null) {
            throw new IllegalArgumentException("GoogleApiClient can't be null");
        }

        this.apiClient = apiClient;
    }

    /**
     * Getter for the GoogleApiClient used throughout the application.
     *
     * @return      GoogleApiClient used in the App.
     */
    public GoogleApiClient getApiClient() {
        return apiClient;
    }

    /**
     * Setter for the test session status.
     *
     * @param testSession   Boolean value to be set to isTestSession.
     */
    public void setTestSession(boolean testSession) {
        this.isTestSession = testSession;
    }

    /**
     * Instantiate a new NetworkHandler and assign it to the corresponding class field.
     */
    public void setNetworkHandler() {
        networkHandler = new NetworkHandler(this);
    }

    /**
     * Getter for the current NetworkHandler.
     *
     * @return      NetworkHandler used to check connection status in the App.
     */
    public NetworkHandler getNetworkHandler() {
        return networkHandler;
    }

    /**
     * Getter for isTestSession.
     *
     * @return      A boolean indicating if the application is currently running in a test environment.
     */
    public boolean isTestSession() {
        return isTestSession;
    }

    /**
     * Method that performs an upload of the local SQLite database file to the remote Firebase storage instance.
     * Useful to perform emergency uploads in lifecycle methods throughout the application.
     */
    public void launchDatabaseUpload() {
        DBHelper dbHelper = new DBHelper(this);
        Uri file = Uri.fromFile(dbHelper.getDatabasePath());
        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl("gs://runnest-146309.appspot.com")
                .child("users").child(getUser().getFirebaseId());
        storageRef.child(dbHelper.getDatabaseName()).putFile(file);
    }
}
