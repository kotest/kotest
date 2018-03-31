package com.example.activity;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.R;

public class LoginActivity extends Activity {
    /**
     * A dummy authentication store containing known user names and passwords.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:foo", "bar@example.com:bar"
    };

    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);

        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void attemptLogin() {
        resetErrors();

        boolean validPwd = setErrorMessageIfEmptyText(mPasswordView);
        boolean validEmail = setErrorMessageIfEmptyText(mEmailView);

        if (validEmail && validPwd) {
            showProgress(true);
            loginUser(mEmailView.getText().toString(), mPasswordView.getText().toString());
        }
    }

    private void loginUser(String email, String password) {
        for (String credential : DUMMY_CREDENTIALS) {
            String[] pieces = credential.split(":");
            if (pieces[0].equals(email) && pieces[1].equals(password)) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        }
        showProgress(false);
        setErrorMessageAndRequestFocus(mPasswordView, getString(R.string.error_incorrect_login));
        setErrorMessageAndRequestFocus(mEmailView, getString(R.string.error_incorrect_login));
    }

    private void resetErrors() {
        mEmailView.setError(null);
        mPasswordView.setError(null);
    }

    private boolean setErrorMessageIfEmptyText(TextView textView) {
        if (isEmpty(textView)) {
            setErrorMessageAndRequestFocus(textView,
                    getString(R.string.error_field_required));
            return false;
        }
        return true;
    }

    private boolean isEmpty(TextView textView) {
        String text = textView.getText().toString();
        return TextUtils.isEmpty(text);
    }

    private void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void setErrorMessageAndRequestFocus(TextView textView, String errorMessage) {
        textView.setError(Html.fromHtml("<font color='red'>" + errorMessage + "</font>"));
        textView.requestFocus();
    }
}

