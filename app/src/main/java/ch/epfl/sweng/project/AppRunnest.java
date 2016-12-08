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
 * Application class
 */
public class AppRunnest extends MultiDexApplication {

    private GoogleApiClient mApiClient = null;
    private User mCurrentUser = new TestUser();
    private boolean testSession = false;
    private NetworkHandler mNetworkHandler = null;

    public void setUser(User user) {
        this.mCurrentUser = user;
    }

    public User getUser() {
        return mCurrentUser;
    }

    public GoogleApiClient getApiClient() {
        return mApiClient;
    }

    public void setApiClient(GoogleApiClient apiClient) {
        this.mApiClient = apiClient;
    }

    public void setTestSession(boolean testSession) {
        this.testSession = testSession;
    }

    public NetworkHandler getNetworkHandler() {
        return mNetworkHandler;
    }

    public void setNetworkHandler() {
        mNetworkHandler = new NetworkHandler(this);
    }

    public boolean isTestSession() {
        return testSession;
    }

    public void launchDatabaseUpload() {
        DBHelper dbHelper = new DBHelper(this);
        Uri file = Uri.fromFile(dbHelper.getDatabasePath());
        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl("gs://runnest-146309.appspot.com")
                .child("users").child(getUser().getFirebaseId());
        storageRef.child(dbHelper.getDatabaseName()).putFile(file);
    }
}
