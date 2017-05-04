package com.simplicity.anuj.myday.Settings;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.widget.TimePicker;

import com.simplicity.anuj.myday.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by anujc on 4/11/2017.
 */

public class SettingsFragmentMain extends PreferenceFragment implements TimePickerDialog.OnTimeSetListener {

    private static final String PREFERENCE_EDIT_NAME_OF_USER = "preference_edit_name_of_user";
    private static final String PREFERENCE_SEND_NOTIFICATION = "preference_send_notification";
    private static final String PREFERENCE_CHOOSE_TIME_NOTIFICATIONS = "preference_time_notification";

    EditTextPreference mEditNamePreference;
    SwitchPreference mSwitchPreference;
    Preference mSelectTimePreference;

    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_main_settings);

        mEditNamePreference = (EditTextPreference) findPreference(PREFERENCE_EDIT_NAME_OF_USER);
        mEditNamePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String name = mEditNamePreference.getText();
                mEditNamePreference.setSummary(name);
                return true;
            }
        });
        bindPreferenceSummaryToValue(mEditNamePreference);

        mSwitchPreference = (SwitchPreference) findPreference(PREFERENCE_SEND_NOTIFICATION);
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
        bindPreferenceSummaryToValue(mSwitchPreference);

        mSelectTimePreference = findPreference(PREFERENCE_CHOOSE_TIME_NOTIFICATIONS);
        mSelectTimePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                createTimePickerDialog();
                return true;
            }
        });
        bindPreferenceSummaryToValue(mSelectTimePreference);
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        // Trigger the listener immediately with the preference's
        // current value.
//        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
//                PreferenceManager
//                        .getDefaultSharedPreferences(preference.getContext())
//                        .getString(preference.getKey(), ""));
    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            preference.setSummary(stringValue);
            return true;
        }
    };

    private void createTimePickerDialog() {
        Calendar mCalender = Calendar.getInstance(TimeZone.getDefault());
        Date mDate = mCalender.getTime();
        int i = mDate.getHours();
        int i1 = mDate.getMinutes();
        new TimePickerDialog(getContext(), this, i, i1, true).show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = getPrefs.edit();
        editor.putInt("setHour", hourOfDay);
        editor.putInt("setMin", minute);
        editor.apply();
        updateTime();

    }

    void updateTime() {
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        int hour = getPrefs.getInt("setHour", 21);
        int min = getPrefs.getInt("setMin", 0);
        mSelectTimePreference.setSummary(String.format(Locale.ENGLISH, "%02d:%02d", hour, min));
    }
}







