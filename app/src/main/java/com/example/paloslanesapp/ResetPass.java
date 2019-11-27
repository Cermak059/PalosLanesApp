package com.example.paloslanesapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class ResetPass extends AppCompatActivity {

    EditText mEmail;
    Button mSend;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        mEmail = findViewById(R.id.editEmail);
        mSend = findViewById(R.id.btnRequest);
        builder = new AlertDialog.Builder(ResetPass.this);

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String Email = mEmail.getText().toString();
                if(Email.length() == 0) {
                    mEmail.requestFocus();
                    mEmail.setError("Field cannot be empty");
                }
                else {
                    try {
                        postRequest();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void postRequest() throws IOException {

        final String Email = mEmail.getText().toString();

        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        String url = "http://3.15.199.174:5000/ResetRequest";

        OkHttpClient client = new OkHttpClient();

        JSONObject postdata = new JSONObject();
        try {
            postdata.put("Email", Email );
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
            public void onFailure(okhttp3.Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.w("failure Response", mMessage);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String mMessage = response.body().string();
                if (response.isSuccessful()) {
                    Log.i("", mMessage);
                    ResetPass.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showNewDialog();
                        }
                    });
                } else {
                    Log.i("", mMessage);
                    ResetPass.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ResetPass.this, mMessage, Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        });
    }

    public void showNewDialog() {
        builder.setTitle("Reset Request")
                .setMessage("Please check email to reset your password")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .show();
    }
}
