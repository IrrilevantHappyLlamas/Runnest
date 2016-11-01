package ch.epfl.sweng.project.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Stack;

import ch.epfl.sweng.project.AppRunnest;
import ch.epfl.sweng.project.Database.DBHelper;
import ch.epfl.sweng.project.Firebase.FirebaseHelper;
import ch.epfl.sweng.project.Fragments.DBDownloadFragment;
import ch.epfl.sweng.project.Fragments.DBUploadFragment;
import ch.epfl.sweng.project.Fragments.DisplayRunFragment;
import ch.epfl.sweng.project.Fragments.DisplayUserFragment;
import ch.epfl.sweng.project.Fragments.MessagesFragment;
import ch.epfl.sweng.project.Fragments.NewRun.RunningMapFragment;
import ch.epfl.sweng.project.Fragments.ProfileFragment;
import ch.epfl.sweng.project.Fragments.RunHistoryFragment;
import ch.epfl.sweng.project.Model.Run;
import ch.epfl.sweng.project.Model.User;

public class SideBarActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ProfileFragment.ProfileFragmentInteractionListener,
        RunningMapFragment.RunningMapFragmentInteractionListener,
        DBDownloadFragment.DBDownloadFragmentInteractionListener,
        DBUploadFragment.DBUploadFragmentInteractionListener,
        RunHistoryFragment.onRunHistoryInteractionListener,
        DisplayRunFragment.OnDisplayRunInteractionListener,
        DisplayUserFragment.OnDisplayUserFragmentInteractionListener,
        MessagesFragment.MessagesFragmentInteractionListener
{

    public static final int PERMISSION_REQUEST_CODE_FINE_LOCATION = 1;

    //Fragment stack(LIFO)
    private Stack<MenuItem> fragmentStack = new Stack<>();
    private MenuItem profileItem;
    private MenuItem runItem;

    private Fragment mCurrentFragment = null;
    private FragmentManager fragmentManager = null;
    private SearchView mSearchView = null;
    private FirebaseHelper mFirebaseHelper = null;

    private FloatingActionButton fab;

    private NavigationView navigationView;

    private Boolean isRunning = false;

    private Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_bar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize database
        mFirebaseHelper = new FirebaseHelper();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //noinspection deprecation
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView h1 = (TextView) header.findViewById(R.id.header1_nav_header);
        TextView h2 = (TextView) header.findViewById(R.id.header2_nav_header);


        runItem = navigationView.getMenu().getItem(1);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runItem.setChecked(true);
                onNavigationItemSelected(runItem);
            }
        });

        User account = ((AppRunnest)getApplicationContext()).getUser();
        if (account != null) {
            h1.setText(account.getName());
            h2.setText(account.getEmail());
        }

        //Initializing the fragment
        fragmentManager = getSupportFragmentManager();
        mCurrentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

        profileItem = navigationView.getMenu().getItem(0);
        profileItem.setChecked(true);
        fragmentStack.push(profileItem);
        
        if(mCurrentFragment == null){
            mCurrentFragment = new DBDownloadFragment();
            fragmentManager.beginTransaction().add(R.id.fragment_container, mCurrentFragment).commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            fragmentStack.pop();
            if(!fragmentStack.isEmpty()){
                fragmentStack.peek().setChecked(true);
                onNavigationItemSelected(fragmentStack.peek());
            } else {
                profileItem.setChecked(true);
                onNavigationItemSelected(profileItem);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.side_bar, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.search).getActionView();

        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(final String query){

                mFirebaseHelper.getDatabase().child("users").child(query).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {

                            switchFragment(query, dataSnapshot.child("name").getValue().toString());
                        }
                        else{

                            switchFragment(null, null);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText){

                return true;
            }


        });

        return true;
    }

    public void switchFragment(String query, String result){

        mCurrentFragment = DisplayUserFragment.newInstance(query, result);
        fragmentManager.beginTransaction().replace(R.id.fragment_container, mCurrentFragment).commit();
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

        if(isRunning && !item.equals(runItem)){
            dialogQuitRun(item);
            return false;
            /*if(hasQuit) {
                return false;
            } else {
                setRunning(false);
                return onNavigationItemSelected(item);
            }
            */
        }

        fab.show();

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(fragmentStack.isEmpty()){
           fragmentStack.push(item);
        }

        if(!fragmentStack.peek().equals(item)) {
            fragmentStack.push(item);
        }

        //TODO: missing commit?
        fragmentManager.beginTransaction().remove(mCurrentFragment);

        if (id == R.id.nav_profile) {
            toolbar.setTitle("Profile");
            launchFragment(new ProfileFragment());
        }  else if (id == R.id.nav_new_run) {
            toolbar.setTitle("New Run");
            fab.hide();
            launchFragment(new RunningMapFragment());
        } else if (id == R.id.nav_run_history) {
            toolbar.setTitle("Run History");
            launchFragment(new RunHistoryFragment());
        } else if (id == R.id.nav_messages) {
            toolbar.setTitle("Messages");
            launchFragment(new MessagesFragment());
        } else if (id == R.id.nav_logout) {
            fragmentStack.pop();
            dialogLogout();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //TODO:comment
    private void launchFragment(Fragment toLaunch){
        if(toLaunch != null) {
            mCurrentFragment = toLaunch;
            fragmentManager.beginTransaction().replace(R.id.fragment_container, mCurrentFragment).commit();
        }
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
    public void onDestroy() {
        super.onDestroy();
        launchEmergencyUpload();
    }

    @Override
    public void onPause() {
        super.onPause();
        launchEmergencyUpload();
    }

    @Override
    public void onStop() {
        super.onStop();
        launchEmergencyUpload();
    }

    private void launchEmergencyUpload() {
        DBHelper dbHelper = new DBHelper(this);
        Uri file = Uri.fromFile(dbHelper.getDatabasePath());
        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl("gs://runnest-146309.appspot.com")
                .child("users").child(((AppRunnest) getApplication()).getUser().getFirebaseId());
        storageRef.child(dbHelper.getDatabaseName()).putFile(file);
    }

    private void dialogLogout(){
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        launchFragment(new DBUploadFragment());
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void dialogQuitRun(final MenuItem item){
        new AlertDialog.Builder(this)
                .setTitle("Quit Run")
                .setMessage("Are you sure you want to to quit your current run?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setRunning(false);
                        item.setChecked(true);
                        onNavigationItemSelected(item);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                        runItem.setChecked(true);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }


    public void setRunning(Boolean running) {
        isRunning = running;
    }

    @Override
    public void onProfileFragmentInteraction() {
    }

    @Override
    public void onRunningMapFragmentInteraction(Run run) {

        launchFragment(DisplayRunFragment.newInstance(run));
    }

    @Override
    public void onRunHistoryInteraction(Run run) {
        launchFragment(DisplayRunFragment.newInstance(run));
    }

    @Override
    public void onDisplayRunInteraction() {
        launchFragment(new RunHistoryFragment());
    }

    @Override
    public void onDBDownloadFragmentInteraction() {
        launchFragment(new ProfileFragment());
    }

    @Override
    public void onDBUploadFragmentInteraction() {
    }

    @Override
    public void onDisplayUserFragmentInteraction(){
    }

    @Override
    public void onMessagesFragmentInteraction() {
    }
}
