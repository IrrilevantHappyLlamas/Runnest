package ch.epfl.sweng.project.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.text.DecimalFormat;

import ch.epfl.sweng.project.AppRunnest;
import ch.epfl.sweng.project.Firebase.FirebaseHelper;
import ch.epfl.sweng.project.Model.User;

/**
 * Fragment which serves as profile tab, where profile information are displayed
 *
 * @author Pablo Pfister, Hakim Invernizzi
 */
public class ProfileFragment extends Fragment {

    private ProfileFragmentInteractionListener listener = null;
    private String name = null;
    private String email = null;

    private final FirebaseHelper firebaseHelper = new FirebaseHelper();

    /**
     * Allows to create an instance of the class specifying the name and email.
     * It's used to show profile of other users.
     *
     * @param name the name of the user
     * @param email the email of the user
     * @return an instance of the class
     */
    public static ProfileFragment newInstance(String name, String email) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.name = name;
        fragment.email = email;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        if (name == null || email == null) {
            // Handle case current user profile
            User user = ((AppRunnest) getActivity().getApplicationContext()).getUser();
            setUserStatistics(user.getName(), user.getEmail(), view);
            String profilePictureUrl = user.getPhotoUrl();
            firebaseHelper.setOrUpdateProfilePicUrl(user.getEmail(), profilePictureUrl);
            setProfileImage(profilePictureUrl, view);
            view.findViewById(R.id.challenge_schedule_buttons).setVisibility(View.GONE);
        } else {
            // Handle case other user profile
            setUserStatistics(name, email, view);
            view.findViewById(R.id.challenge_schedule_buttons).setVisibility(View.VISIBLE);

            firebaseHelper.getProfilePicUrl(email, new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String url = (String) dataSnapshot.getValue();
                        setProfileImage(url, view);
                    } else {
                        setProfileImage("", view);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            });

            setButtonsListeners(view);
        }
        return view;
    }

    private void setUserStatistics(final String name, final String email, final View view) {
        ((TextView) view.findViewById(R.id.nameTxt)).setText(name);

        firebaseHelper.getUserStatistics(email, new FirebaseHelper.statisticsHandler() {
            @Override
            public void handleRetrievedStatistics(String[] statistics) {

                double distance = Double.valueOf(statistics[firebaseHelper.TOTAL_RUNNING_DISTANCE_INDEX]);
                // Transform to km and format with one digit after the coma
                String distanceToBeDisplayed = "0";
                if (distance > 100) {
                    distanceToBeDisplayed = new DecimalFormat("#.0").format(distance / 1000);
                }
                //Context context = getActivity().getApplicationContext();
                distanceToBeDisplayed += " km"; // + getResources().getString(R.string.km);
                ((TextView) view.findViewById(R.id.total_running_distance)).setText(distanceToBeDisplayed);

                double time = Double.valueOf(statistics[firebaseHelper.TOTAL_RUNNING_TIME_INDEX]);
                int hours = (int) time / 3600;
                int minutes = (int) (time) / 60 - hours * 60;
                ((TextView) view.findViewById(R.id.total_running_time)).setText(hours + "h " + minutes + "m");

                String nbRuns = statistics[firebaseHelper.TOTAL_NUMBER_OF_RUNS_INDEX]
                        + " runs"; // + getResources().getString(R.string.runs);
                ((TextView) view.findViewById(R.id.nb_runs)).setText(nbRuns);

                String nbChallenges = statistics[firebaseHelper.TOTAL_NUMBER_OF_CHALLENGES_INDEX]
                        + " challenges"; // + getResources().getString(R.string.challenges_lowercase);
                ((TextView) view.findViewById(R.id.total_number_of_challenges)).setText(nbChallenges);

                String nbWon = statistics[firebaseHelper.TOTAL_NUMBER_OF_WON_CHALLENGES_INDEX]
                        + " won"; // + getResources().getString(R.string.won);
                ((TextView) view.findViewById(R.id.total_number_of_won_challenges)).setText(nbWon);

                String nbLost = statistics[firebaseHelper.TOTAL_NUMBER_OF_LOST_CHALLENGES_INDEX]
                        + " lost"; // + getResources().getString(R.string.lost);
                ((TextView) view.findViewById(R.id.total_number_of_lost_challenges)).setText(nbLost);
            }
        });
    }

    private void setProfileImage(String url, View view) {
        ImageView profilePic = (ImageView) view.findViewById(R.id.photoImg);
        if (!url.equals("")) {
            new DownloadImageTask(profilePic).execute(url);
        } else {
            profilePic.setImageResource(R.drawable.profile_head_small);
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        final ImageView bitmapImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bitmapImage = bmImage;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap img = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                img = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return img;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            bitmapImage.setImageBitmap(result);
        }
    }

    private void setButtonsListeners(View view) {
        view.findViewById(R.id.challenge_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseHelper.listenUserAvailability(email, false, new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if ((boolean) dataSnapshot.getValue()) {
                                listener.onProfileFragmentInteraction(name, email);
                            } else {
                                Toast.makeText(getContext(), "User is currently busy, try again later",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            throw new DatabaseException("Corrupted available node for user");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
            }
        });

        view.findViewById(R.id.schedule_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onProfileFragmentInteractionSchedule(name, email);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProfileFragmentInteractionListener) {
            listener = (ProfileFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onProfileFragmentInteraction and onProfileFragmentInteractionSchedule");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /**
     * Interface for SideBarActivity
     */
    public interface ProfileFragmentInteractionListener {
        void onProfileFragmentInteraction(String challengedUserName, String challengedUserEmail);
        void onProfileFragmentInteractionSchedule(String challengedUserName, String challengedUserEmail);
    }
}