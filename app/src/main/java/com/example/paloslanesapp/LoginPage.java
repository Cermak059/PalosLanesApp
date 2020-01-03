package com.example.paloslanesapp;

import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginPage extends AppCompatActivity {

    EditText mUsername;
    EditText mPassword;
    Button Login;
    CheckBox mCheckbox;
    private String authToken;
    private String AccessLevel;
    private ProgressDialog loginDialogue;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        //Get reference to widgets
        mUsername =  findViewById(R.id.editUserName);
        mPassword =  findViewById(R.id.editPassword);
        mCheckbox =  findViewById(R.id.checkBoxRemember);
        Login =  findViewById(R.id.btnLogin);
        builder = new AlertDialog.Builder(LoginPage.this);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();

        checkSharedPreferences();

        if (mPreferences.getString(getString(R.string.AuthToken), "") !=null) {
            //loginDialogue = ProgressDialog.show(LoginPage.this, "Logging in", "Please wait...");
            try {
                verifyToken(mPreferences.getString(getString(R.string.AuthToken), ""));
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Log.i("No auth token found","");
        }

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String Username = mUsername.getText().toString();
                final String Password = mPassword.getText().toString();
                if (Username.length() == 0) {
                    mUsername.requestFocus();
                    mUsername.setError("Field cannot be empty");
                } else if (Username.length() == 1) {
                    mUsername.requestFocus();
                    mUsername.setError("Must be atleast 2 characters");
                } else if (!Username.matches("[a-zA-Z0-9]+")) {
                    mUsername.requestFocus();
                    mUsername.setError("Field cannot use special characters");
                } else if (Password.length() == 0) {
                    mPassword.requestFocus();
                    mPassword.setError("Field cannot be empty");
                } else {
                    //loginDialogue = ProgressDialog.show(LoginPage.this, "Logging in", "Please wait...");
                    //Save data when remember me checkbox is checked
                    if (mCheckbox.isChecked()) {
                        mEditor.putString(getString(R.string.CheckboxSave), "True");
                        mEditor.commit();

                        String UsernameSave = mUsername.getText().toString();
                        mEditor.putString(getString(R.string.UsernameSave), UsernameSave);
                        mEditor.commit();

                        String PasswordSave = mPassword.getText().toString();
                        mEditor.putString(getString(R.string.PasswordSave), PasswordSave);
                        mEditor.commit();
                    }
                    //Dont save data when remember me checkbox is not checked
                    else {
                        mEditor.putString(getString(R.string.CheckboxSave), "False");
                        mEditor.commit();

                        mEditor.putString(getString(R.string.UsernameSave), "");
                        mEditor.commit();

                        mEditor.putString(getString(R.string.PasswordSave), "");
                        mEditor.commit();
                    }

                    try {
                        postRequest();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }
        });
    }

    //Check for shared preferences
    private void checkSharedPreferences () {
        String Checkbox = mPreferences.getString(getString(R.string.CheckboxSave), "False");
        String Username = mPreferences.getString(getString(R.string.UsernameSave), "");
        String Password = mPreferences.getString(getString(R.string.PasswordSave), "");

        //Set text if preferences exist
        mUsername.setText(Username);
        mPassword.setText(Password);

        if (Checkbox.equals("True")){
            mCheckbox.setChecked(true);
        }
        else {
            mCheckbox.setChecked(false);
        }
    }

    public void postRequest() throws IOException {

        final String Username = mUsername.getText().toString();
        final String Password = mPassword.getText().toString();

        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        String url = "http://3.15.199.174:5000/Login";

        OkHttpClient client = new OkHttpClient();

        JSONObject postdata = new JSONObject();
        try {
            postdata.put("Username", Username );
            postdata.put("Password", Password );
        } catch(JSONException e){
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE, postdata.toString());

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.w("failure Response", mMessage);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String mMessage = response.body().string();
                if (response.code() == 200) {
                    try {
                        JSONObject authObj = new JSONObject(mMessage);
                        authToken = authObj.getString("AuthToken");
                        AccessLevel = authObj.getString("AccessLevel");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mEditor.putString(getString(R.string.AuthToken), authToken);
                    mEditor.putString(getString(R.string.AccessLevel), AccessLevel);
                    mEditor.commit();
                    LoginPage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mPreferences.getString(getString(R.string.AccessLevel), "").equals("User")) {
                                Toast.makeText(LoginPage.this, "Login successful", Toast.LENGTH_LONG).show();
                                //Display toast and call method to switch activity
                                userActivity();
                            }
                            else if (mPreferences.getString(getString(R.string.AccessLevel),"").equals("Admin")){
                                Toast.makeText(LoginPage.this, "Welcome Admin", Toast.LENGTH_LONG).show();
                                adminActivity();
                            }
                            else {
                                if (loginDialogue != null){
                                    loginDialogue.dismiss();
                                }
                                builder.setTitle("Error")
                                        .setMessage("Could not login at this time please try again")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        }).show();
                            }
                        }
                    });
                } else if (response.code() == 400){
                    LoginPage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (loginDialogue != null){
                                loginDialogue.dismiss();
                            }
                            builder.setTitle("Login Failed")
                                    .setMessage("Invalid username or password")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).show();
                        }
                    });

                } else {
                    builder.setMessage("Unable to login at this time")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                }
            }
        });
    }

    public void verifyToken(String authToken) throws IOException {
        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        String url = "http://3.15.199.174:5000/Authenticate";

        OkHttpClient client = new OkHttpClient();

        JSONObject postdata = new JSONObject();

        RequestBody body = RequestBody.create(MEDIA_TYPE, postdata.toString());

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Auth-Token", authToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.w("failure Response", mMessage);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String mMessage = response.body().string();
                if (response.code() == 200) {
                    LoginPage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mPreferences.getString(getString(R.string.AccessLevel), "").equals("User")) {
                                Toast.makeText(LoginPage.this, "Login successful", Toast.LENGTH_LONG).show();
                                userActivity();
                            } else if (mPreferences.getString(getString(R.string.AccessLevel), "").equals("Admin")) {
                                Toast.makeText(LoginPage.this, "Welcome Admin", Toast.LENGTH_LONG).show();
                                adminActivity();
                            }
                            else {
                                builder.setTitle("Unexpected Error Occurred")
                                        .setMessage("Please try again")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        }).show();
                            }
                        }
                    });
                } else if (response.code()==401) {
                    LoginPage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (loginDialogue != null){
                                loginDialogue.dismiss();
                            }
                            showAlert();
                        }
                    });

                } else {
                    LoginPage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginPage.this, mMessage, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

    }

    public void adminActivity() {
        if (loginDialogue != null){
            loginDialogue.dismiss();
        }
        Intent adminPage = new Intent(this, adminActivity.class);
        startActivity(adminPage);
    }
    public void userActivity() {
        if (loginDialogue != null){
            loginDialogue.dismiss();
        }
            Intent homepage = new Intent(this, MainActivity.class);
            startActivity(homepage);
    }
    public void btnRegister (View view) {
        Intent signup = new Intent(this,SignUpPage.class);
        startActivity(signup);
    }
    public void btnForgot (View view) {
        Intent resetPass = new Intent(this,ResetPass.class);
        startActivity(resetPass);
    }

    public void showAlert() {
        builder.setTitle("Logged Out")
                .setMessage("Session has expired")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
    }
}
