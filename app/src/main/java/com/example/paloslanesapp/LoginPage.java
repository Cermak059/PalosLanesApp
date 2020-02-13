package com.example.paloslanesapp;

import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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
    private String userPoints;
    private String userFirstName;
    private String userLastName;
    private String userBirthday;
    private String userEmail;
    private String userPhoneNumber;
    private String userLeague;
    private ProgressDialog loginDialogue;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.splashScreenTheme);
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

        checkConnection();

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
                    loginDialogue = ProgressDialog.show(LoginPage.this, "Logging in", "Please wait...");
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
                    if (isOnline()) {

                        try {
                            postRequest();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {
                        loginDialogue.dismiss();
                        connectionAlert();
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
                                try {
                                    getUserData(authToken);
                                } catch (IOException e){
                                    e.printStackTrace();
                                }
                            }
                            else if (mPreferences.getString(getString(R.string.AccessLevel),"").equals("Admin")){
                                Toast.makeText(LoginPage.this, "Welcome Admin", Toast.LENGTH_LONG).show();
                                adminActivity();
                            }
                            else {
                                if (loginDialogue != null){
                                    loginDialogue.dismiss();
                                }
                                builder.setTitle("Login Failed")
                                        .setMessage("Unknown access level please try again")
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
                    builder.setTitle("Login Failed")
                            .setMessage("Unable to login at this time")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                }
            }
        });
    }

    public void verifyToken(final String authToken) throws IOException {
        String url = "http://3.15.199.174:5000/Authenticate";

        OkHttpClient client = new OkHttpClient();

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
                if (response.code() == 200) {
                    LoginPage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mPreferences.getString(getString(R.string.AccessLevel), "").equals("User")) {
                                try {
                                    getUserData(authToken);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else if (mPreferences.getString(getString(R.string.AccessLevel), "").equals("Admin")) {
                                Toast.makeText(LoginPage.this, "Welcome Admin", Toast.LENGTH_LONG).show();
                                adminActivity();
                            }
                            else {
                                if (loginDialogue != null) {
                                    loginDialogue.dismiss();
                                }
                                builder.setTitle("Login Failed")
                                        .setMessage("Unknown access level please try again")
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
                            if (loginDialogue != null) {
                                loginDialogue.dismiss();
                            }
                            builder.setTitle("Login Failed")
                                    .setMessage("Unable to login at this time")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).show();
                        }
                    });
                }
            }
        });

    }

    public void getUserData(String authToken) throws IOException {
        String url = "http://3.15.199.174:5000/Users";

        OkHttpClient client = new OkHttpClient();

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
                    try {
                        JSONObject resObj = new JSONObject(mMessage);
                        userPoints = resObj.getString("Points");
                        userFirstName = resObj.getString("Fname");
                        userLastName = resObj.getString("Lname");
                        userBirthday = resObj.getString("Birthdate");
                        userEmail = resObj.getString("Email");
                        userPhoneNumber = resObj.getString("Phone");
                        userLeague = resObj.getString("League");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mEditor.putString(getString(R.string.FirstSave), userFirstName);
                    mEditor.putString(getString(R.string.LastSave), userLastName);
                    mEditor.putString(getString(R.string.PointsSave), userPoints);
                    mEditor.putString(getString(R.string.BirthdaySave), userBirthday);
                    mEditor.putString(getString(R.string.EmailSave), userEmail);
                    mEditor.putString(getString(R.string.PhoneSave), userPhoneNumber);
                    mEditor.putString(getString(R.string.LeagueSave), userLeague);
                    mEditor.commit();
                    LoginPage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginPage.this, "Login successful", Toast.LENGTH_LONG).show();
                            //Display toast and call method to switch activity
                            userActivity();
                        }
                    });
                }  else if (response.code() == 401){
                    LoginPage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showAlert();
                        }
                    });
                } else {
                    LoginPage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            builder.setTitle("Unexpected Error Occurred")
                                    .setMessage("Failed to load data")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            userActivity();
                                        }
                                    }).show();
                        }
                    });
                }
            }
        });

    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public void checkConnection(){
        if(isOnline()){
            if (mPreferences.getString(getString(R.string.AuthToken), null) !=null) {
                loginDialogue = ProgressDialog.show(LoginPage.this, "Logging in", "Please wait...");
                try {
                    verifyToken(mPreferences.getString(getString(R.string.AuthToken), ""));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.i("No auth token found","");
            }
        }else{
           connectionAlert();
        }
    }

    public void adminActivity() {
        Intent adminPage = new Intent(this, adminActivity.class);
        startActivity(adminPage);
        if (loginDialogue != null){
            loginDialogue.dismiss();
        }
    }
    public void userActivity() {
            Intent homepage = new Intent(this, MainActivity.class);
            startActivity(homepage);
        if (loginDialogue != null){
            loginDialogue.dismiss();
        }
    }
    public void btnRegister (View view) {
        Intent signup = new Intent(this,SignUpPage.class);
        startActivity(signup);
    }
    public void btnForgot (View view) {
        Intent resetPass = new Intent(this,ResetPass.class);
        startActivity(resetPass);
    }
    public void btnPrivacy (View view) {
        startActivity((new Intent(Intent.ACTION_VIEW, Uri.parse("https://chicagolandbowlingservice.com/privacy-policy"))));
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
    public void  connectionAlert() {
        builder.setTitle("Connection Error")
                .setMessage("Please check your internet connection!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }
}
