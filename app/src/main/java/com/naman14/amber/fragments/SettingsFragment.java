/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.naman14.amber.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.View;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.Config;
import com.afollestad.appthemeengine.prefs.ATECheckBoxPreference;
import com.afollestad.appthemeengine.prefs.ATEColorPreference;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.naman14.amber.R;
import com.naman14.amber.activities.SettingsActivity;
import com.naman14.amber.utils.PreferencesUtility;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOCKSCREEN = "show_albumart_lockscreen";
    private static final String KEY_START_PAGE = "start_page_preference";

    private Preference lockscreen;

    //启动页设置
    private ListPreference startPagePreference;
    private PreferencesUtility mPreferences;
    private String mAteKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        mPreferences = PreferencesUtility.getInstance(getActivity());
        lockscreen = findPreference(LOCKSCREEN);
        startPagePreference = (ListPreference) findPreference(KEY_START_PAGE);
        setPreferenceClickListeners();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
    }

    private void setPreferenceClickListeners() {

        startPagePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                switch ((String) newValue) {
                    case "last_opened":
                        mPreferences.setLastOpenedAsStartPagePreference(true);
                        break;
                    case "songs":
                        mPreferences.setLastOpenedAsStartPagePreference(false);
                        mPreferences.setStartPageIndex(0);
                        break;
                    case "albums":
                        mPreferences.setLastOpenedAsStartPagePreference(false);
                        mPreferences.setStartPageIndex(1);
                        break;
                    case "artists":
                        mPreferences.setLastOpenedAsStartPagePreference(false);
                        mPreferences.setStartPageIndex(2);
                        break;
                }
                return true;
            }
        });

        lockscreen.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Bundle extras = new Bundle();
                extras.putBoolean("lockscreen",(boolean)newValue);
                mPreferences.updateService(extras);
                return true;
            }
        });

        findPreference("dark_theme").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // Marks both theme configs as changed so MainActivity restarts itself on return
                Config.markChanged(getActivity(), "light_theme");
                Config.markChanged(getActivity(), "dark_theme");
                // The dark_theme preference value gets saved by Android in the default PreferenceManager.
                // It's used in getATEKey() of both the Activities.
                getActivity().recreate();
                return true;
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
      //  invalidateSettings();
        mAteKey = ((SettingsActivity) getActivity()).getATEKey();
        ATE.apply(view, mAteKey);
    }

    public void invalidateSettings() {
        mAteKey = ((SettingsActivity) getActivity()).getATEKey();

//        ATEColorPreference primaryColorPref = (ATEColorPreference) findPreference("primary_color");
//        primaryColorPref.setColor(Config.primaryColor(getActivity(), mAteKey), Color.BLACK);
//        primaryColorPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                new ColorChooserDialog.Builder((SettingsActivity) getActivity(), R.string.primary_color)
//                        .preselect(Config.primaryColor(getActivity(), mAteKey))
//                        .show();
//                return true;
//            }
//        });
//
//        ATEColorPreference accentColorPref = (ATEColorPreference) findPreference("accent_color");
//        accentColorPref.setColor(Config.accentColor(getActivity(), mAteKey), Color.BLACK);
//        accentColorPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                new ColorChooserDialog.Builder((SettingsActivity) getActivity(), R.string.accent_color)
//                        .preselect(Config.accentColor(getActivity(), mAteKey))
//                        .show();
//                return true;
//            }
//        });


        findPreference("dark_theme").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // Marks both theme configs as changed so MainActivity restarts itself on return
                Config.markChanged(getActivity(), "light_theme");
                Config.markChanged(getActivity(), "dark_theme");
                // The dark_theme preference value gets saved by Android in the default PreferenceManager.
                // It's used in getATEKey() of both the Activities.
                getActivity().recreate();
                return true;
            }
        });

        final ATECheckBoxPreference statusBarPref = (ATECheckBoxPreference) findPreference("colored_status_bar");
        final ATECheckBoxPreference navBarPref = (ATECheckBoxPreference) findPreference("colored_nav_bar");

        statusBarPref.setChecked(Config.coloredStatusBar(getActivity(), mAteKey));
        statusBarPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                ATE.config(getActivity(), mAteKey)
                        .coloredStatusBar((Boolean) newValue)
                        .apply(getActivity());
                return true;
            }
        });


        navBarPref.setChecked(Config.coloredNavigationBar(getActivity(), mAteKey));
        navBarPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                ATE.config(getActivity(), mAteKey)
                        .coloredNavigationBar((Boolean) newValue)
                        .apply(getActivity());
                return true;
            }
        });

    }
}
