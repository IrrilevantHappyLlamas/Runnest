package ch.epfl.sweng.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * Your app's main activity.
 */
public final class MainActivity extends AppCompatActivity {
    // TODO replace this code with your app code!

    public static int add(final int a, final int b) {
        return a + b;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void toSimpleRunTapped(View view) {
        Intent intent = new Intent(this, SimpleRunActivity.class);
        startActivity(intent);
    }
}