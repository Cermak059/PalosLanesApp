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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.os.Build.VERSION_CODES.N;


public class Home extends Fragment implements OnMapReadyCallback {

    private MapView mMapView;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView textView;
        final ImageButton button1 = view.findViewById(R.id.imageButton1);
        final ImageButton button2 = view.findViewById(R.id.imageButton2);
        final ImageButton button3 = view.findViewById(R.id.imageButton3);
        final ImageButton button5 = view.findViewById(R.id.imageButton);
        mMapView = view.findViewById(R.id.mapView);
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

        initGoogleMap(savedInstanceState);

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

    private void initGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng palos = new LatLng(41.69080,-87.80876);
        map.addMarker(new MarkerOptions().position(palos).title("Palos Lanes"));
        map.moveCamera(CameraUpdateFactory.newLatLng(palos));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(palos,15));

    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}
