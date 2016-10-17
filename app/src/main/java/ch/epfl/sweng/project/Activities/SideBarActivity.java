package ch.epfl.sweng.project.Activities;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

import ch.epfl.sweng.project.Fragments.LocationDemo;

import ch.epfl.sweng.project.Fragments.ProfileFragment;
import ch.epfl.sweng.project.Fragments.RunHistoryFragment;
import ch.epfl.sweng.project.Model.Profile;


public class SideBarActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ProfileFragment.ProfileFragmentInteractionListener,
        LocationDemo.OnFragmentInteractionListener,
        RunHistoryFragment.RunHistoryInteractionListener {

    // Constants
    public static final int PERMISSION_REQUEST_CODE_FINE_LOCATION = 1;
    private boolean locationPermissionGranted = false;

    private Fragment runningFragment = null;
    private FragmentManager fragmentManager = null;

    public static Profile profile = null;

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //noinspection deprecation
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        /*
        //Profile
        Intent i = getIntent();
        profile = new Profile(i.getSerializableExtra("id").toString(),
         i.getSerializableExtra("id").toString(),
          i.getSerializableExtra("id").toString(),
           i.getSerializableExtra("id").toString());
        */

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Initializing the fragment
        fragmentManager = getSupportFragmentManager();
        runningFragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if(runningFragment == null){
            runningFragment = new LocationDemo();
            fragmentManager.beginTransaction().add(R.id.fragment_container, runningFragment).commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
            runningFragment = new ProfileFragment();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, runningFragment).commit();
        } else if(id == R.id.nav_slideshow) {
            runningFragment = new RunHistoryFragment();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, runningFragment).commit();
        } else if(id == R.id.nav_gallery){

        }

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

                    locationPermissionGranted = true;
                    Toast.makeText(getApplicationContext(),"You can now start a Run.",Toast.LENGTH_LONG).show();

                } else {

                    locationPermissionGranted = false;
                    Toast.makeText(getApplicationContext(),"Permission Denied, you cannot start a Run.",
                             Toast.LENGTH_LONG).show();
                }
                break;

        }

    }

    /**
     * Getter for <code>locationPermissionGranted</code>.
     *
     * @return      value of <code>locationPermissionGranted</code>
     */
    public boolean getLocationPermissionGranted() {
        return locationPermissionGranted;
    }

    /**
     * Setter for <code>locationPermissionGranted</coder>
     *
     * @param granted   the value to set, a <code>Boolean</code>
     */
    public void setLocationPermissionGranted(boolean granted) {
        locationPermissionGranted = granted;
    }

    @Override
    public void onProfileFragmentInteraction(Uri uri) {

    }

    @Override
    public void onRunHistoryInteraction(Uri uri) {

    }
}
