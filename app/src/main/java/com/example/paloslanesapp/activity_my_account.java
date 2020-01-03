package com.example.paloslanesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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

public class activity_my_account extends AppCompatActivity {

    private TextView textPoints;
    private TextView textFullName;
    private TextView textBirthday;
    private TextView textEmail;
    private TextView textUsername;
    private TextView textPhoneNumber;
    private TextView textLeagueMember;
    private ProgressDialog accountDialogue;
    private SharedPreferences mPreferences;
    private String authToken;
    private String userPoints;
    private String userFirstName;
    private String userLastName;
    private String userName;
    private String userEmail;
    private String userBirthday;
    private String userPhoneNumber;
    private String userUsername;
    private String userLeague;
    private String leagueMember;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        accountDialogue = ProgressDialog.show( activity_my_account.this, "Loading Data", "Please wait...");
        textFullName = findViewById(R.id.textFullName);
        textPoints = findViewById(R.id.textPoints);
        textBirthday = findViewById(R.id.textBirthday);
        textEmail = findViewById(R.id.textEmail);
        textUsername = findViewById(R.id.textUsername);
        textPhoneNumber = findViewById(R.id.textPhoneNum);
        textLeagueMember = findViewById(R.id.textLeagueMember);
        builder = new AlertDialog.Builder(activity_my_account.this);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            getUserData(mPreferences.getString(getString(R.string.AuthToken), ""));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getUserData(String authToken) throws IOException {
        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        String url = "http://3.15.199.174:5000/Users";

        OkHttpClient client = new OkHttpClient();

        JSONObject postdata = new JSONObject();

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
                        userUsername = resObj.getString("Username");
                        userPhoneNumber = resObj.getString("Phone");
                        userLeague = resObj.getString("League");
                        leagueMember = "League Member: "+ userLeague;
                        userName = userFirstName + " " + userLastName;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    activity_my_account.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textPoints.setText(userPoints);
                            textFullName.setText(userName);
                            textBirthday.setText(userBirthday);
                            textEmail.setText(userEmail);
                            textPhoneNumber.setText(userPhoneNumber);
                            textUsername.setText(userUsername);
                            textLeagueMember.setText(leagueMember);
                            if (accountDialogue != null) {
                                accountDialogue.dismiss();
                            }
                        }
                    });
                }  else if (response.code() == 401){
                    activity_my_account.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showAlert();
                        }
                    });
                } else {
                    activity_my_account.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showAlert();
                        }
                    });
                }
            }
        });

    }

    public void showAlert() {
        accountDialogue.dismiss();
        builder.setTitle("Logged Out")
                .setMessage("Session has expired")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    public void onBackPressed() {
        finish();
    }

}
