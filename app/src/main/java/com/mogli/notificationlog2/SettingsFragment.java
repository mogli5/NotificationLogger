package com.mogli.notificationlog2;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        try {
            addPreferencesFromResource(R.xml.root_preferences);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        PreferenceScreen preferenceScreen = getPreferenceScreen();

        int count = preferenceScreen.getPreferenceCount();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

//        Log.v("1", "here");
        for (int i = 0; i < count; i++) {
            Preference p = preferenceScreen.getPreference(i);
//            Log.v("2", "here  i: " + i);
            if ((p instanceof PreferenceCategory)) {
//                Log.v("3", "here");
                Preference preference = ((PreferenceCategory) p).findPreference("deletenotifafter");
                String value = sharedPreferences.getString(preference.getKey(), "");
                setPreferenceSummary(preference, value);
            }
        }
    }

    private void setPreferenceSummary(Preference preference, String value) {
//        Log.v("4", "here");
        if (preference instanceof ListPreference) {
//            Log.v("5", "here");
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(value);
            if (prefIndex >= 0) {
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
//                Log.v("here", "here");
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        Log.v("6","here");
        Preference preference = findPreference(key);
        if (preference != null) {
//            Log.v("7","here");
            if (preference instanceof ListPreference) {
//                Log.v("8","here");
                String value = sharedPreferences.getString(preference.getKey(), "");
                setPreferenceSummary(preference, value);
            }
        }
    }
}
