package ch.epfl.sweng.project;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * This class allows to check if the device is connected to internet.
 */
public class NetworkHandler {

    private Context context = null;
    private ConnectivityManager connectivityManager = null;

    /**
     * The constructor of the class.
     *
     * @param context the context where to show the toast message if no internet connection is available
     */
    public NetworkHandler(Context context) {
        if (context == null) {
            throw new IllegalArgumentException();
        }
        this.context = context;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * Check whether the internet connection is available or not. If the connection isn't available
     * inform the user with a toast message.
     *
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }

        Toast.makeText(context, "No Internet connection", Toast.LENGTH_LONG).show();
        return false;
    }
}
