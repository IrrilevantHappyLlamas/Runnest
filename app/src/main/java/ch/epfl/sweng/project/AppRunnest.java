package ch.epfl.sweng.project;

import android.support.multidex.MultiDexApplication;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Application class
 */
public class AppRunnest extends MultiDexApplication {

    private GoogleApiClient mApiClient = null;
    private GoogleSignInAccount mGoogleUser = null;
    private NetworkHandler mNetworkHandler = null;

    public void setGoogleUser(GoogleSignInAccount user) {
        this.mGoogleUser = user;
    }

    public GoogleSignInAccount getGoogleUser() {
        return mGoogleUser;
    }

    public GoogleApiClient getApiClient() {
        return mApiClient;
    }

    public void setApiClient(GoogleApiClient apiClient) {
        this.mApiClient = apiClient;
    }

    public NetworkHandler getNetworkHandler() {
        return mNetworkHandler;
    }

    public void setNetworkHandler() {
        mNetworkHandler = new NetworkHandler(this);
    }
}
