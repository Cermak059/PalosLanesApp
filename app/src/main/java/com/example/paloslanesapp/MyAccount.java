package com.example.paloslanesapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class MyAccount extends Fragment {

    private TextView textPoints;
    private TextView textFullName;
    private TextView textBirthday;
    private TextView textEmail;
    private TextView textUsername;
    private TextView textPhoneNumber;
    private TextView textLeagueMember;
    private SharedPreferences mPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        final ProgressDialog accountDialogue;
        final AlertDialog.Builder builder;

        //accountDialogue = ProgressDialog.show( getActivity(), "Loading Data", "Please wait...");
        textFullName = view.findViewById(R.id.textFullName);
        textPoints = view.findViewById(R.id.textPoints);
        textBirthday = view.findViewById(R.id.textBirthday);
        textEmail = view.findViewById(R.id.textEmail);
        textUsername = view.findViewById(R.id.textUsername);
        textPhoneNumber = view.findViewById(R.id.textPhoneNum);
        textLeagueMember = view.findViewById(R.id.textLeagueMember);
        builder = new AlertDialog.Builder(getActivity());
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        LoadData();

        return view;
    }

    private void LoadData() {

        final String userPoints;
        final String userFirstName;
        final String userLastName;
        final String userFullName;
        final String userEmail;
        final String userBirthday;
        final String userPhoneNumber;
        final String userUsername;
        final String userLeague;

            userPoints = mPreferences.getString(getString(R.string.PointsSave), "") + " pts";
            userUsername = mPreferences.getString(getString(R.string.UsernameSave), "");
            userFirstName = mPreferences.getString(getString(R.string.FirstSave), "");
            userLastName = mPreferences.getString(getString(R.string.LastSave), "");
            userBirthday = mPreferences.getString(getString(R.string.BirthdaySave), "");
            userEmail = mPreferences.getString(getString(R.string.EmailSave), "");
            userPhoneNumber = mPreferences.getString(getString(R.string.PhoneSave), "");
            userLeague = mPreferences.getString(getString(R.string.LeagueSave), "");
            userFullName = userFirstName + " " + userLastName;

            textPoints.setText(userPoints);
            textUsername.setText(userUsername);
            textFullName.setText(userFullName);
            textBirthday.setText(userBirthday);
            textEmail.setText(userEmail);
            textPhoneNumber.setText(userPhoneNumber);
            textLeagueMember.setText(userLeague);

    }
}
