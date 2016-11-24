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
    private FirebaseHelper mFirebaseHelper = null;
    private User mUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        mFirebaseHelper = new FirebaseHelper();

        mUser = ((AppRunnest)getActivity().getApplicationContext()).getUser();

        ImageView profilePic = (ImageView)view.findViewById(R.id.photoImg);

        if (!mUser.getPhotoUrl().equals("")) {
            new DownloadImageTask(profilePic)
                    .execute(mUser.getPhotoUrl());
        }


        int dimensionInPixel = 100;
        int dimensionInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dimensionInPixel,
                            getResources().getDisplayMetrics());
        profilePic.getLayoutParams().height = dimensionInDp;
        profilePic.getLayoutParams().width = dimensionInDp;
        profilePic.requestLayout();

        // Display logged profile id
        String name = mUser.getName();
        String email = mUser.getEmail();

        ((TextView)view.findViewById(R.id.nameTxt)).setText(name);
        ((TextView)view.findViewById(R.id.emailTxt)).setText(email);

        mFirebaseHelper.getUserStatistics(mUser.getEmail(), new FirebaseHelper.statisticsHandler() {
            @Override
            public void handleRetrievedStatistics(String[] statistics) {

                DecimalFormat format = new DecimalFormat("#.0");

                double distance = Double.valueOf(statistics[mFirebaseHelper.TOTAL_RUNNING_DISTANCE_INDEX]);
                String toBeDisplayedDistance = String.valueOf(format.format(distance/1000));
                ((TextView)view.findViewById(R.id.total_running_distance)).setText(toBeDisplayedDistance);

                double time = Double.valueOf(statistics[mFirebaseHelper.TOTAL_RUNNING_TIME_INDEX]);
                String toBeDisplayedTime = String.valueOf(format.format(time/60));
                ((TextView)view.findViewById(R.id.total_running_time)).setText(toBeDisplayedTime);

                String nbRuns = statistics[mFirebaseHelper.TOTAL_NUMBER_OF_RUNS_INDEX];
                ((TextView)view.findViewById(R.id.nb_runs)).setText(nbRuns + " runs");
                ((TextView)view.findViewById(R.id.total_number_of_challenges)).setText(statistics[mFirebaseHelper.TOTAL_NUMBER_OF_CHALLENGES_INDEX]);
                ((TextView)view.findViewById(R.id.total_number_of_won_challenges)).setText(statistics[mFirebaseHelper.TOTAL_NUMBER_OF_WON_CHALLENGES_INDEX]);
                ((TextView)view.findViewById(R.id.total_number_of_lost_challenges)).setText(statistics[mFirebaseHelper.TOTAL_NUMBER_OF_LOST_CHALLENGES_INDEX]);

            }
        });

        return view;
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
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
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



