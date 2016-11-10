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

import ch.epfl.sweng.project.AppRunnest;
import ch.epfl.sweng.project.Model.User;

/**
 * Fragment which serves as profile tab, where profile information are displayed
 */
public class ProfileFragment extends android.support.v4.app.Fragment {

    private ProfileFragmentInteractionListener mProfileListener = null;

    private User mUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

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
        String id = getString(R.string.id) + ": " + mUser.getId();
        String name = getString(R.string.name) + ": " + mUser.getName();
        String email = getString(R.string.email) + ": " + mUser.getEmail();
        String family = getString(R.string.family) + ": " + mUser.getFamilyName();

        ((TextView)view.findViewById(R.id.idTxt)).setText(id);
        ((TextView)view.findViewById(R.id.nameTxt)).setText(name);
        ((TextView)view.findViewById(R.id.emailTxt)).setText(email);
        ((TextView)view.findViewById(R.id.familyTxt)).setText(family);


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



