/*
 * Copyright (C) 2011 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cyanogenmod.cmparts.activities;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.cyanogenmod.cmparts.R;
import com.cyanogenmod.cmparts.utils.ShortcutPickHelper;

public class InputBackKeyActivity extends PreferenceActivity implements ShortcutPickHelper.OnPickListener {

    private static final String INPUT_CUSTOM_BACK_APP_TOGGLE = "pref_input_custom_back_app_toggle";

    private static final String INPUT_CUSTOM_BACK_APP_ACTIVITY = "pref_input_custom_back_app_activity";

    private CheckBoxPreference mCustomBackAppTogglePref;

    private Preference mCustomBackAppActivityPref;

    private ShortcutPickHelper mPicker;

    private int mKeyNumber = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.input_back_key_title);
        addPreferencesFromResource(R.xml.input_back_key_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        mCustomBackAppTogglePref = (CheckBoxPreference) prefSet
                .findPreference(INPUT_CUSTOM_BACK_APP_TOGGLE);
        mCustomBackAppTogglePref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.USE_CUSTOM_BACK_APP_TOGGLE, 0) == 1);
        mCustomBackAppActivityPref = (Preference) prefSet
                .findPreference(INPUT_CUSTOM_BACK_APP_ACTIVITY);

        mPicker = new ShortcutPickHelper(this, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        setAppSummary(mCustomBackAppActivityPref, Settings.System.USE_CUSTOM_BACK_APP_ACTIVITY);
    }

    private void setAppSummary(Preference pref, String key) {
        String value = Settings.System.getString(getContentResolver(), key);
        pref.setSummary(mPicker.getFriendlyNameForUri(value));
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        if (preference == mCustomBackAppTogglePref) {
            value = mCustomBackAppTogglePref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.USE_CUSTOM_BACK_APP_TOGGLE, value ? 1 : 0);
            return true;
        } else if (preference == mCustomBackAppActivityPref) {
            mKeyNumber = 1;
            mPicker.pickShortcut();
            return true;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPicker.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void shortcutPicked(String uri, String friendlyName, boolean isApplication) {
        String key;
        Preference pref;

        switch (mKeyNumber) {
            case 1:
                key = Settings.System.USE_CUSTOM_BACK_APP_ACTIVITY;
                pref = mCustomBackAppActivityPref;
                break;
            default:
                return;
        }

        if (Settings.System.putString(getContentResolver(), key, uri)) {
            pref.setSummary(friendlyName);
        }
    }
}
