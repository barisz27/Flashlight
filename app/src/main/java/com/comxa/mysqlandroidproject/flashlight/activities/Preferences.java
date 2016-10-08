package com.comxa.mysqlandroidproject.flashlight.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.comxa.mysqlandroidproject.flashlight.R;

public class Preferences extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.app_preferences);
    }
}