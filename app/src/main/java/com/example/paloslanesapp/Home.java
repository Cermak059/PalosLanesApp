package com.example.paloslanesapp;

import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


public class Home extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView textView;

        final ImageButton button1 = view.findViewById(R.id.imageButton1);
        final ImageButton button2 = view.findViewById(R.id.imageButton2);
        final ImageButton button3 = view.findViewById(R.id.imageButton3);
        final ImageButton button5 = view.findViewById(R.id.imageButton);
        final SharedPreferences mPreferences= PreferenceManager.getDefaultSharedPreferences(getActivity());

        textView = view.findViewById(R.id.textHomeHeader);

        final String Name = mPreferences.getString(getString(R.string.FirstSave), "");
        final String NameDisplay = "Welcome, "+Name;
        textView.setText(NameDisplay);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFragmentPoints();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFragmentPoints();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFragmentCoupons();
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFragmentParty();
            }
        });


        ImageView imageView = view.findViewById(R.id.slideshow);

        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
        animationDrawable.start();


        return view;
    }

    private void showFragmentPoints() {
        Loyalty fragment = new Loyalty();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentHolder, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void showFragmentCoupons() {
        Coupons fragment = new Coupons();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentHolder, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void showFragmentParty() {
        Party fragment = new Party();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentHolder, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}
