package com.example.paloslanesapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class ThanksQrCode extends Activity {

    private ImageView imageView;
    private SharedPreferences mPreferences;
    private String accountEmail;
    private String coupType;
    private static final String CenterID = "PalosLanes";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        imageView = findViewById(R.id.viewQR);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        accountEmail = mPreferences.getString(getString(R.string.EmailSave), "");
        coupType = "Thank You";

        String coupData = accountEmail + "\n" + coupType + "\n" + CenterID;

        generateQR(coupData);
    }

    private void generateQR(String coupData) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(coupData, BarcodeFormat.QR_CODE, 500, 500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}


