package ch.epfl.sweng.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

/**
 * Your app's main activity.
 */
public final class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void toSimpleRunTapped(View view) {
        Intent intent = new Intent(this, RunningMapActivity.class);
        startActivity(intent);
    }
}