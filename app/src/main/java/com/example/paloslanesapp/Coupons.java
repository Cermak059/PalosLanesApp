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
import android.widget.ImageView;
import android.widget.TextView;


import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Coupons extends Fragment {

    private AlertDialog.Builder builder;
    private ImageView BOGOimage;
    private ImageView THANKSimage;
    private ImageView Error;
    private TextView info;
    private String yesCoupon;
    private String noCoupon;
    private Boolean BOGO = false;
    private Boolean ThankYou = false;
    private Boolean LimitedTime = false;
    private SharedPreferences mPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coupons, container, false);

        BOGOimage = view.findViewById(R.id.imageCoupon);
        THANKSimage =view.findViewById(R.id.thanksCoupon);
        Error = view.findViewById(R.id.imageError);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        builder = new AlertDialog.Builder(getActivity());
        info = view.findViewById(R.id.textCouponInfo);
        noCoupon = "Oops Sorry! You have already used your coupon for this week!";
        yesCoupon = "Bowl on! Your coupons have arrived!";

        BOGOimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent QRcode = new Intent(getActivity(), QrCode.class);
                startActivity(QRcode);
            }
        });

        THANKSimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent QRcode = new Intent(getActivity(), QrCode.class);
                startActivity(QRcode);
            }
        });


        checkAuthToken();

        return view;
    }

    private void checkCoupons(String authToken) throws IOException {

        String url = "http://3.15.199.174:5000/CheckAllCoupons";

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
                        JSONArray usedCoupons = resObj.getJSONArray("Used");

                        for(int i=0; i<usedCoupons.length(); i++) {
                            if(usedCoupons.getString(i).equals("BOGO")) {
                                BOGO = true;
                            }
                            else if(usedCoupons.getString(i).equals("Thank You")) {
                                ThankYou = true;
                            }
                            else if(usedCoupons.getString(i).equals("Limited")) {
                                LimitedTime = true;
                            }
                            else {

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!BOGO) {
                                BOGOimage.setImageResource(R.drawable.bogoimagecopy);
                                info.setText(yesCoupon);
                            }
                            if (!ThankYou) {
                                THANKSimage.setImageResource(R.drawable.thank_you_coupon);
                                info.setText(yesCoupon);
                            }
                            else {
                                Error.setImageResource(R.drawable.errorsymbol);
                                info.setText(noCoupon);
                            }
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
                            builder.setTitle("Error")
                                    .setMessage("No coupon data found")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).show();
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

    private void checkAuthToken() {

        if (mPreferences.getString(getString(R.string.AuthToken), "") !=null) {
            try {
                checkCoupons(mPreferences.getString(getString(R.string.AuthToken), ""));
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Log.i("No auth token found","");
        }

    }

}
