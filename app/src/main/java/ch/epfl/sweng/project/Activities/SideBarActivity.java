package ch.epfl.sweng.project.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.UploadTask;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ch.epfl.sweng.project.AppRunnest;
import ch.epfl.sweng.project.Database.DBHelper;
import ch.epfl.sweng.project.Database.DBSync;
import ch.epfl.sweng.project.Fragments.FirebaseFragment;
import ch.epfl.sweng.project.Fragments.HomeFragment;
import ch.epfl.sweng.project.Fragments.NewRun.RunningMapFragment;
import ch.epfl.sweng.project.Fragments.DisplayRunFragment;
import ch.epfl.sweng.project.Fragments.ProfileFragment;
import ch.epfl.sweng.project.Fragments.RunHistoryFragment;
import ch.epfl.sweng.project.Model.Run;

public class SideBarActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnFragmentInteractionListener,
        ProfileFragment.ProfileFragmentInteractionListener,
        RunningMapFragment.RunningMapFragmentInteractionListener,
        FirebaseFragment.FireBaseFragmentInteractionListener,
        RunHistoryFragment.onRunHistoryInteractionListener,
        DisplayRunFragment.OnDisplayRunInteractionListener,
        OnSuccessListener<FileDownloadTask.TaskSnapshot>,
        OnFailureListener
{

    public static final int PERMISSION_REQUEST_CODE_FINE_LOCATION = 1;

    private Fragment mCurrentFragment = null;
    private FragmentManager fragmentManager = null;
    private DBSync databaseSynchronizer = null;
    private DBHelper dbHelper = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_bar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCurrentFragment = new RunningMapFragment();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, mCurrentFragment).commit();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //noinspection deprecation
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView h1 = (TextView) header.findViewById(R.id.header1_nav_header);
        TextView h2 = (TextView) header.findViewById(R.id.header2_nav_header);

        GoogleSignInAccount account = ((AppRunnest)getApplicationContext()).getGoogleUser();
        h1.setText(account.getDisplayName());
        h2.setText(account.getEmail());

        //Initializing the fragment
        fragmentManager = getSupportFragmentManager();
        mCurrentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if(mCurrentFragment == null){
            mCurrentFragment = new HomeFragment();
            fragmentManager.beginTransaction().add(R.id.fragment_container, mCurrentFragment).commit();
        }

        //Database sync
        dbHelper = new DBHelper(this);
        databaseSynchronizer = new DBSync(dbHelper);
        try {
            databaseSynchronizer.downloadDatabase((HomeFragment) mCurrentFragment);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (databaseSynchronizer.getUserRef() != null) {
            outState.putString("user reference", databaseSynchronizer.getUserRef().toString());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.side_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            mCurrentFragment = new ProfileFragment();
        }  else if (id == R.id.nav_new_run) {
            mCurrentFragment = new RunningMapFragment();
        } else if (id == R.id.nav_run_history) {
            mCurrentFragment = new RunHistoryFragment();
        } else if (id == R.id.nav_firebase) {
            mCurrentFragment = new FirebaseFragment();
        } else if (id == R.id.nav_logout) {

            databaseSynchronizer.uploadDatabase();

            new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getBaseContext(), "Logout successful", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                        intent.putExtra("Source", "logout_pressed");
                        startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

            FirebaseAuth.getInstance().signOut();
        }

        fragmentManager.beginTransaction().replace(R.id.fragment_container, mCurrentFragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Handle request permissions result. Update what needed and give a feedback to the user.
     *
     * @param requestCode       code of the request, an <code>int</code>
     * @param permissions       requested permissions, a table of <code>String</code>
     * @param grantResults      result of the request, a table of <code>int</code>
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {

            case PERMISSION_REQUEST_CODE_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getApplicationContext(),"You can now start a Run.",Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(getApplicationContext(),"Permission Denied, you cannot start a Run.",
                             Toast.LENGTH_LONG).show();
                }
                break;

        }

    }

    @Override
    public void onProfileFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onRunningMapFragmentInteraction(Run run) {

        mCurrentFragment = DisplayRunFragment.newInstance(run);
        fragmentManager.beginTransaction().replace(R.id.fragment_container, mCurrentFragment).commit();
    }

    @Override
    public void onRunHistoryInteraction(Run run) {

        mCurrentFragment = DisplayRunFragment.newInstance(run);
        fragmentManager.beginTransaction().replace(R.id.fragment_container, mCurrentFragment).commit();
    }

    @Override
    public void onDisplayRunInteraction() {

        mCurrentFragment = new RunHistoryFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, mCurrentFragment).commit();
    }

    @Override
    public void onFirebaseFragmentInteraction(Uri uri) {

    }

    @Override
    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

        try {
            InputStream in = new FileInputStream(databaseSynchronizer.getDownloadedDB());
            OutputStream out = new FileOutputStream(dbHelper.getDatabasePath());

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(getBaseContext(), "Download successful", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailure(@NonNull Exception e) {

    }
}
