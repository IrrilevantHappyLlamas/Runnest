package ch.epfl.sweng.project.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

/**
 * Your app's main activity.
 */
public final class LoginActivity extends AppCompatActivity {

    private EditText usernameText;
    private EditText passwordText;
    private Button loginButton;
    private TextView signupLink;

    private static final int REQUEST_SIGNUP = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameText = (EditText) findViewById(R.id.input_username);
        passwordText = (EditText) findViewById(R.id.input_password);
        loginButton = (Button) findViewById(R.id.btn_login);
        signupLink = (TextView) findViewById(R.id.link_signup);

        // For now, the login button directly takes us to the sidebar activity
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        // The signup link takes us to the signup activity, which returns the signup request
        signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Intent signupIntent = new Intent(getApplicationContext(), SignupActivity);
                //startActivityForResult(signupIntent, REQUEST_SIGNUP);
            }
        });
    }

    private void login() {

        // Validate text entries
        if (!validate()) {
            onLoginFailed();
        }

        // Disable login button while authenticating
        loginButton.setEnabled(false);

        // Show progression dialog
        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating");
        progressDialog.show();

        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        boolean loginSuccess = false;
        if (username.equals("ihl") && password.equals("lama")) {
            loginSuccess = true;
        }

        if (loginSuccess) {
            onLoginSuccess();
        } else {
            onLoginFailed();
        }
    }

    private boolean validate() {
        boolean valid = true;

        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        if (username.isEmpty()) {
            usernameText.setError("Enter a valid username");
            valid = false;
        } else {
            usernameText.setError(null);
        }

        if (password.isEmpty()) {
            passwordText.setError("Enter a password");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }

    // If the authentication logic is succesful
    public void onLoginSuccess() {
        loginButton.setEnabled(true);
        Intent sideBarIntent = new Intent(this, SideBarActivity.class);
        startActivity(sideBarIntent);
    }

    // If the authentication logic fails
    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login Failed", Toast.LENGTH_LONG).show();
        loginButton.setEnabled(true);
    }


    @Override
    public void onBackPressed() {
        // disable going back
        moveTaskToBack(true);
    }
}