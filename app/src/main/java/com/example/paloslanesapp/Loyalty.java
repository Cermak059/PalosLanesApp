package com.example.paloslanesapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Loyalty extends Fragment {

    private TextView rules;
    private String content;
    private String content2;
    private String showPoints;
    private RadioButton mAdd;
    private RadioButton mRedeem;
    private Button Refresh;
    private String userPoints;
    private String userDisplay;
    private TextView textPoints;
    private SharedPreferences mPreferences;
    private AlertDialog.Builder builder;
    private ImageView viewQR;
    private String accountEmail;
    private SharedPreferences.Editor mEditor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loyalty, container, false);

        mRedeem = view.findViewById(R.id.radioButton2);
        mAdd = view.findViewById(R.id.radioButton);
        textPoints = view.findViewById(R.id.textLoyalty);
        builder = new AlertDialog.Builder(getActivity());
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        rules = view.findViewById(R.id.textLoyaltyContent);
        viewQR = view.findViewById(R.id.viewQR);
        Refresh = view.findViewById(R.id.buttonRefresh);
        mEditor = mPreferences.edit();

        content = "Add points: \nEvery game you pay for = +100pts to your account! \n\nUsers will not receive points for the following: \n\n-Games redeemed with points \n-Games redeemed with coupons\n-Games bowled during nightly specials";
        content2 = "Subtract points:\nEvery game you redeem = -500pts from your account! \n\n**Users may redeem points or coupons anytime open bowl is available!**";

        rules.setText(content);

        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAdd.isChecked()) {
                    rules.setText(content);
                }
            }
        });

        mRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRedeem.isChecked()) {
                    rules.setText(content2);
                }
            }
        });

        Refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getUserData(mPreferences.getString(getString(R.string.AuthToken), ""));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        accountEmail = mPreferences.getString(getString(R.string.EmailSave), "");

        showPoints = mPreferences.getString(getString(R.string.PointsSave), "")+" pts";
        textPoints.setText(showPoints);

        generateQR();

        return view;

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
                        accountEmail = resObj.getString("Email");
                        userDisplay = "Available Points " + userPoints;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mEditor.putString(getString(R.string.PointsSave), userPoints);
                    mEditor.commit();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textPoints.setText(userDisplay);
                            generateQR();
                        }
                    });
                }  else if (response.code() == 401){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showAlert();
                        }
                    });
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            builder.setTitle("Unexpected Error Occurred")
                                    .setMessage("Failed to load user data")
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

    public void showAlert() {
        builder.setTitle("Log Out")
                .setMessage("Session has expired")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    public void generateQR() {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(accountEmail, BarcodeFormat.QR_CODE, 700, 700);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            viewQR.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }


}
