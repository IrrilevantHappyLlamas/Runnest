package ch.epfl.sweng.project.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

import java.io.InputStream;
import java.text.DecimalFormat;

import ch.epfl.sweng.project.AppRunnest;
import ch.epfl.sweng.project.Firebase.FirebaseHelper;
import ch.epfl.sweng.project.Model.User;

/**
 * Fragment which serves as profile tab, where profile information are displayed
 */
public class ProfileFragment extends android.support.v4.app.Fragment {

    private ProfileFragmentInteractionListener mProfileListener = null;
    private String mName = null;
    private String mEmail = null;

    public static ProfileFragment newInstance(String name, String email) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.mName = name;
        fragment.mEmail = email;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        if (mEmail == null) {
            User user = ((AppRunnest) getActivity().getApplicationContext()).getUser();
            setUserStatistics(user.getName(), user.getEmail(), view);
            setProfileImage(user.getPhotoUrl(), view);
        } else {
            setUserStatistics(mName, mEmail, view);
        }
        return view;
    }

    private void setProfileImage(String url, View view) {
        if (!url.equals("")) {
            ImageView profilePic = (ImageView) view.findViewById(R.id.photoImg);
            new DownloadImageTask(profilePic).execute(url);
        }
    }

    private void setUserStatistics(final String name, final String email, final View view) {
        ((TextView) view.findViewById(R.id.nameTxt)).setText(name);
        final FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.getUserStatistics(email, new FirebaseHelper.statisticsHandler() {
            @Override
            public void handleRetrievedStatistics(String[] statistics) {

                DecimalFormat decimalFormat = new DecimalFormat("#.0");

                double distance = Double.valueOf(statistics[firebaseHelper.TOTAL_RUNNING_DISTANCE_INDEX]);
                // Transform to km and format with one digit after the coma
                String toBeDisplayedDistance = String.valueOf(decimalFormat.format(distance / 1000));
                ((TextView) view.findViewById(R.id.total_running_distance)).setText(toBeDisplayedDistance + " km");

                double time = Double.valueOf(statistics[firebaseHelper.TOTAL_RUNNING_TIME_INDEX]);
                int hours = (int) time / 3600;
                int minutes = (int) (time - hours * 60) / 60;
                ((TextView) view.findViewById(R.id.total_running_time)).setText(hours + "h " + minutes + "m");

                String nbRuns = statistics[firebaseHelper.TOTAL_NUMBER_OF_RUNS_INDEX] + " runs";
                ((TextView) view.findViewById(R.id.nb_runs)).setText(nbRuns);

                String nbChallenges = statistics[firebaseHelper.TOTAL_NUMBER_OF_CHALLENGES_INDEX] + " challenges";
                ((TextView) view.findViewById(R.id.total_number_of_challenges)).setText(nbChallenges);

                String nbWon = statistics[firebaseHelper.TOTAL_NUMBER_OF_WON_CHALLENGES_INDEX] + " won";
                ((TextView) view.findViewById(R.id.total_number_of_won_challenges)).setText(nbWon);

                String nbLost = statistics[firebaseHelper.TOTAL_NUMBER_OF_LOST_CHALLENGES_INDEX] + " lost";
                ((TextView) view.findViewById(R.id.total_number_of_lost_challenges)).setText(nbLost);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProfileFragmentInteractionListener) {
            mProfileListener = (ProfileFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mProfileListener = null;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    /**
     * Interface for SideBarActivity
     */
    public interface ProfileFragmentInteractionListener {
        void onProfileFragmentInteraction();
    }
}



