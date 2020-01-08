package com.example.paloslanesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

public class adminActivity extends AppCompatActivity {

    private Button Scan;
    AlertDialog.Builder builder;
    private int pointValue;
    private String pointString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Scan = findViewById(R.id.buttonScan);
        builder = new AlertDialog.Builder(adminActivity.this);

        Scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scanActivity = new Intent(adminActivity.this,ScanActivity.class);
                startActivityForResult(scanActivity, 1001);
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
            String addPoints=data.getStringExtra("Add Points");

            builder.setMessage("Would you like to add or redeem point?")
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
                pointString = "You added "+pointValue+" points";
                Toast.makeText(adminActivity.this, pointString, Toast.LENGTH_LONG).show();
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
        View dialogView = inflater.inflate(R.layout.redeem_picker_dialogue, null);
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
                pointString = "You redeemed "+pointValue+" points";
                Toast.makeText(adminActivity.this, pointString, Toast.LENGTH_LONG).show();
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

}
