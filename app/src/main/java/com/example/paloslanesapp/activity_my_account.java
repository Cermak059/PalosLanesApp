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
    private ProgressDialog accountDialogue;
    private SharedPreferences mPreferences;
    private String authToken;
    private String userPoints;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        accountDialogue = ProgressDialog.show( activity_my_account.this, "Loading Data", "Please wait...");

        textPoints = findViewById(R.id.textPoints);
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
                    activity_my_account.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textPoints.setText(mMessage);
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
                }
            }
        });

    }

    public void showAlert() {
        builder.setTitle("Logged Out")
                .setMessage("Session has expired")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }
}
