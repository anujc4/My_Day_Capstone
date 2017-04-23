package com.simplicity.anuj.myday.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.util.Log;

import com.simplicity.anuj.myday.R;

/**
 * Created by anujc on 4/11/2017.
 */

public class SettingsFragmentMain extends PreferenceFragment {

    private static final String PREFERENCE_EDIT_NAME_OF_USER = "preference_edit_name_of_user";
    private static final String PREFERENCE_SEND_NOTIFICATION = "preference_send_notification";
    private static final String PREFERENCE_CHOOSE_TIME_NOTIFICATIONS = "preference_choose_time_notifications";

    EditTextPreference mEditNamePreference;
    SwitchPreference mSwitchPreference;
    Preference mSelectTimePreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO Call an Async Task and Load the Preferences beforehand
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                //TODO Call Shared Preferences Here
                return null;
            }
        }.execute();

        addPreferencesFromResource(R.xml.pref_main_settings);

        mEditNamePreference = (EditTextPreference) getPreferenceManager().findPreference(PREFERENCE_EDIT_NAME_OF_USER);
        mEditNamePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String name = mEditNamePreference.getText();
                Log.e("LOGLGLLG", name);
                mEditNamePreference.setSummary(name);
                return true;
            }
        });

        mSelectTimePreference = getPreferenceScreen().findPreference(PREFERENCE_CHOOSE_TIME_NOTIFICATIONS);


        mSwitchPreference = (SwitchPreference) getPreferenceManager().findPreference(PREFERENCE_SEND_NOTIFICATION);
        mSwitchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue instanceof Boolean) {
                    boolean isChecked = (boolean) newValue;
                    if (isChecked) {
                        mSelectTimePreference.setEnabled(true);
                    } else {
                        mSelectTimePreference.setEnabled(false);
                    }
                }

                return true;
            }
        });


    }
}
