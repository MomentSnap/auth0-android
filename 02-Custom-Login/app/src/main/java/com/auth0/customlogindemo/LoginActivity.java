package com.auth0.customlogindemo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;


public class LoginActivity extends Activity {

    final static String TAG = "LoginActivity";

    private static final String DEFAULT_DB_CONNECTION = "Username-Password-Authentication";
    private static final int REQUEST_CODE_AUTH = 101;
    private Auth0 auth0;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Bind your views
        final EditText emailEditText = (EditText) findViewById(R.id.emailEditext);
        final EditText passwordEditText = (EditText) findViewById(R.id.passwordEditext);
        Button loginButton = (Button) findViewById(R.id.loginButton);

        // Add the onClick listener to the login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Show a progress dialog to block the UI while the request is being made.
                login(emailEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });

        auth0 = new Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain));


    }

    private void login(String email, String password) {

        AuthenticationAPIClient client = new AuthenticationAPIClient(auth0);

        client.login(email, password, DEFAULT_DB_CONNECTION).start(new BaseCallback<Credentials, AuthenticationException>() {
            @Override
            public void onSuccess(Credentials payload) {
             loginSuccess(payload);
            }

            @Override
            public void onFailure(AuthenticationException exception) {
               loginFailure(exception);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_AUTH) {
            WebAuthProvider.resume(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    public void loginWithDarden(View view) {
        WebAuthProvider.init(auth0).withConnection(getString(R.string.darden_connection)).useBrowser(false).start(this, new AuthCallback() {
            @Override
            public void onSuccess(@NonNull Credentials credentials) {
                loginSuccess(credentials);
            }

            @Override
            public void onFailure(@NonNull Dialog dialog) {
                dialog.show();
            }

            @Override
            public void onFailure(AuthenticationException exception) {
                loginFailure(exception);
            }
        }, REQUEST_CODE_AUTH);
    }

    void loginSuccess(final Credentials payload) {
        Log.i(TAG, "success " + payload.getType() + ":" + payload.getIdToken());
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toast(payload.getType() + ":" + payload.getIdToken());
             //   startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
    }

    void loginFailure(final AuthenticationException exception) {
        Log.w(TAG, "failure " + exception.getLocalizedMessage());
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toast(exception.getLocalizedMessage());
            }
        });
    }

    @MainThread
    void toast(final String msg) {
        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
    }

}


