package com.example.paloslanesapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.fragment.app.Fragment;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Coupons extends Fragment {

    private AlertDialog.Builder builder;
    private ImageButton BOGO;
    private ImageView error;
    private TextView info;
    private String yesCoupon;
    private String noCoupon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coupons, container, false);

        BOGO = view.findViewById(R.id.imageCoupon);
        final SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        builder = new AlertDialog.Builder(getActivity());
        info = view.findViewById(R.id.textCouponInfo);
        error = view.findViewById(R.id.imageError);
        noCoupon = "Oops Sorry! You have already used your coupon for this week!";
        yesCoupon = "Bowl on! Your coupons have arrived!";

        BOGO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent QRcode = new Intent(getActivity(), QrCode.class);
                startActivity(QRcode);

            }
        });
        error.setVisibility(View.VISIBLE);
        BOGO.setVisibility(View.GONE);
        info.setText(noCoupon);

        if (mPreferences.getString(getString(R.string.AuthToken), "") !=null) {
            //loginDialogue = ProgressDialog.show(LoginPage.this, "Logging in", "Please wait...");
            try {
                checkCoupons(mPreferences.getString(getString(R.string.AuthToken), ""));
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Log.i("No auth token found","");
        }

        return view;
    }

    public void checkCoupons(String authToken) throws IOException {

        String url = "http://3.15.199.174:5000/BuyOneGetOne";

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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            error.setVisibility(View.GONE);
                            BOGO.setVisibility(View.VISIBLE);
                            info.setText(yesCoupon);
                        }
                    });
                } else if (response.code()==401) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            builder.setTitle("Session Expired")
                                    .setMessage("Please log back in and try again")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).show();
                        }
                    });

                } else if (response.code()==400){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            error.setVisibility(View.VISIBLE);
                            BOGO.setVisibility(View.GONE);
                            info.setText(noCoupon);
                        }
                    });
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            builder.setTitle("Error")
                                    .setMessage("Unexpected error has occurred please try again")
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
