package com.example.kalongip.chatapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.example.kalongip.chatapp.Value.Const;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    private AutoCompleteTextView newEmail;
    private AutoCompleteTextView newUsername;
    private EditText newPassword;
    private EditText confirmPassword;

    private List<String> friends = new ArrayList<>();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        newEmail = (AutoCompleteTextView) findViewById(R.id.new_email);
        newUsername = (AutoCompleteTextView) findViewById(R.id.new_username);
        newPassword = (EditText) findViewById(R.id.new_password);
        confirmPassword = (EditText) findViewById(R.id.confirm_password);

        Button registerButton = (Button) findViewById(R.id.email_register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });
    }

    private void attemptRegister() {
        newEmail.setError(null);
        newPassword.setError(null);
        confirmPassword.setError(null);

        final String email = newEmail.getText().toString();
        final String username = newUsername.getText().toString();
        String password = newPassword.getText().toString();
        String password2 = confirmPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(password2) || !password.equals(password2)){
            confirmPassword.setError("Invalid password!");
            focusView = confirmPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)){
            newUsername.setError("This field is required");
            focusView = newUsername;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)){
            newEmail.setError("This field is required");
            focusView = newEmail;
            cancel = true;
        } else if (!isValidEmailAddress(email)){
            newEmail.setError("Invalid Email Address!");
            focusView = newEmail;
            cancel = true;
        }

        if (cancel){
            focusView.requestFocus();
        } else {
            final ProgressDialog progressDialog = ProgressDialog.show(this, "Loading", "please wait...");
            final Firebase myFirebaseRef = new Firebase(Const.FIREBASE_URL);
            myFirebaseRef.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> result) {
                    Log.d(TAG, "Successfully created user account with uid: " + result.get("uid"));
                    Firebase userRef = myFirebaseRef.child("users").child(result.get("uid").toString());
                    userRef.child("email").setValue(email);
                    userRef.child("username").setValue(username);
                    userRef.child("uid").setValue(result.get("uid"));
                    friends.add("test");
                    userRef.child("friends").setValue(friends);
                    finish();
                }
                @Override
                public void onError(FirebaseError firebaseError) {
                    // there was an error
                    Log.d(TAG, "Error!");
                    progressDialog.dismiss();
                }
            });
        }
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
}
