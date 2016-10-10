package ch.epfl.sweng.project.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

/**
 * Your app's main activity.
 */
public final class LoginActivity extends AppCompatActivity {

    EditText usernameText;
    EditText passwordText;
    Button loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameText = (EditText) findViewById(R.id.input_username);
        passwordText = (EditText) findViewById(R.id.input_password);
        loginButton = (Button) findViewById(R.id.btn_login);

        // For now, the login button directly takes us to the sidebar activity
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    public void login() {
        String email = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        Intent intent = new Intent(this, SideBarActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        // disable going back
        moveTaskToBack(true);
    }
}