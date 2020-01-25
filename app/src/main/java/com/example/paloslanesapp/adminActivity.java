package com.example.paloslanesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
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

public class adminActivity extends AppCompatActivity {


    AlertDialog.Builder builder;
    private int pointValue;
    private String accountID;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        final Button Scan;
        final Button Coupon;

        Scan = findViewById(R.id.buttonScan);
        Coupon = findViewById(R.id.buttonCoupon);
        builder = new AlertDialog.Builder(adminActivity.this);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scanActivity = new Intent(adminActivity.this,ScanActivity.class);
                startActivityForResult(scanActivity, 1001);
            }
        });

        Coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scanActivity = new Intent(adminActivity.this, ScanActivity.class);
                startActivityForResult(scanActivity, 2001);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==1001)
        {
            accountID = data.getStringExtra("Add Points");
            if (accountID != null) {
                builder.setTitle("Account " + accountID)
                        .setMessage("Would you like to add or redeem points?")
                        .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                addDialogue();

                            }
                        })
                        .setNegativeButton("REDEEM", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                redeemDialogue();
                            }
                        }).show();
            }else {
                Log.i("", "No data from scan");
            }
        } else if (requestCode==2001) {
            accountID = data.getStringExtra("Add Points");
            if (accountID != null) {
                try {
                    manageCoupons();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                Log.i("", "No data from scan");
            }
        } else {
            builder.setTitle("QR Code Error")
                    .setMessage("Please try again")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).show();
        }
    }

    public void addDialogue() {
        final AlertDialog.Builder d = new AlertDialog.Builder(adminActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.number_picker_dialogue, null);
        d.setView(dialogView);
        final NumberPicker numberPicker = dialogView.findViewById(R.id.dialog_number_picker);
        numberPicker.setMaxValue(10);
        numberPicker.setMinValue(0);
        numberPicker.setDisplayedValues(new String[] {"0","1 Game - 100pts", "2 Games - 200pts", "3 Games - 300pts", "4 Games - 400pts", "5 Games - 500pts", "6 Games- 600pts", "7 Games - 700pts", "8 Games - 800pts", "9 Games - 900pts", "10 Games - 1000pts"});
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {

            }
        });
        d.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                pointValue = numberPicker.getValue()*100;
                try {
                    managePoints();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog alertDialog = d.create();
        alertDialog.show();
    }

    public void redeemDialogue() {

        final AlertDialog.Builder d = new AlertDialog.Builder(adminActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.redeem_picker_dialogue, null);
        d.setView(dialogView);
        final NumberPicker numberPicker = dialogView.findViewById(R.id.dialog_number_picker);
        numberPicker.setMaxValue(5);
        numberPicker.setMinValue(0);
        numberPicker.setDisplayedValues(new String[] {"0","1 Game -500pts", "2 Games -1000pts", "3 Games -1500pts", "4 Games -2000pts", "5 Games -2500pts" });
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {

            }
        });
        d.setPositiveButton("Redeem", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                pointValue = numberPicker.getValue()*-500;
                try {
                    managePoints();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog alertDialog = d.create();
        alertDialog.show();
    }

    public void managePoints() throws IOException {

        final String authToken = mPreferences.getString(getString(R.string.AuthToken), "");

        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        String url = "http://3.15.199.174:5000/Points";

        OkHttpClient client = new OkHttpClient();

        JSONObject postdata = new JSONObject();
        try {
            postdata.put("Email", accountID );
            postdata.put("Points", pointValue);
        } catch(JSONException e){
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE, postdata.toString());

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Auth-Token", authToken)
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
                if (response.code() == 200) {
                    Log.i("", mMessage);
                    adminActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(adminActivity.this, "SUCCESS!", Toast.LENGTH_LONG).show();
                        }
                    });
                } else if (response.code()==400){
                    Log.i("", mMessage);
                    adminActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            builder.setTitle("Error")
                                    .setMessage(mMessage)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).show();
                        }
                    });

                } else if (response.code()==401) {
                    Log.i("", mMessage);
                    adminActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            builder.setTitle("Error")
                                    .setMessage("Unauthorized to perform this action")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).show();
                        }
                    });
                } else {
                    builder.setTitle("Unexpected Error Occurred")
                            .setMessage("Please try again!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                }
            }
        });
    }

    public void manageCoupons() throws IOException {
        final String authToken = mPreferences.getString(getString(R.string.AuthToken), "");

        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        String url = "http://3.15.199.174:5000/BuyOneGetOne";

        OkHttpClient client = new OkHttpClient();

        JSONObject postdata = new JSONObject();
        try {
            postdata.put("Email", accountID );
        } catch(JSONException e){
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE, postdata.toString());

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Auth-Token", authToken)
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
                if (response.code() == 200) {
                    Log.i("", mMessage);
                    adminActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(adminActivity.this, "SUCCESS!", Toast.LENGTH_LONG).show();
                        }
                    });
                } else if (response.code()==400){
                    Log.i("", mMessage);
                    adminActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            builder.setTitle("Error")
                                    .setMessage(mMessage)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).show();
                        }
                    });

                } else if (response.code()==401) {
                    Log.i("", mMessage);
                    adminActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            builder.setTitle("Error")
                                    .setMessage("Unauthorized to perform this action")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).show();
                        }
                    });
                } else {
                    Log.i("", mMessage);
                    adminActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            builder.setTitle("Unexpected Error Occurred")
                                    .setMessage("Please try again!")
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

}
