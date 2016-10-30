package ch.epfl.sweng.project;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;


public class NetworkHandler {

    private Context mContext = null;
    private ConnectivityManager mConnManager = null;


    public NetworkHandler(Context context) {
        mContext = context;
        mConnManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * Check whether the internet connection is available or not. If the connection isn't available
     * inform the user with a "Toast message".
     *
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        NetworkInfo networkInfo = mConnManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()) {
            return true;
        }

        Toast.makeText(mContext, "No Internet connection", Toast.LENGTH_LONG).show();
        return false;
    }
}
