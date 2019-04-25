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

package com.naman14.amber;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.Config;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.naman14.amber.dataloaders.PlaylistLoader;
import com.naman14.amber.permissions.Nammu;
import com.naman14.amber.services.ServiceClient;
import com.naman14.amber.utils.DeviceIdGenerator;
import com.naman14.amber.utils.PreferencesUtility;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.L;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import io.fabric.sdk.android.Fabric;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AmberApp extends MultiDexApplication {

    private String TAG = "huangxiaoyu.application";
    private static AmberApp mInstance;
    public String id;

    public static synchronized AmberApp getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        //disable crashlytics for debug builds
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();
        Fabric.with(this, crashlyticsKit);

        ImageLoaderConfiguration localImageLoaderConfiguration = new ImageLoaderConfiguration.Builder(this).imageDownloader(new BaseImageDownloader(this) {
            PreferencesUtility prefs = PreferencesUtility.getInstance(AmberApp.this);

            @Override
            protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {
                if (prefs.loadArtistAndAlbumImages())
                    return super.getStreamFromNetwork(imageUri, extra);
                throw new IOException();
            }
        }).build();

        ImageLoader.getInstance().init(localImageLoaderConfiguration);
        L.writeLogs(false);
        L.disableLogging();
        L.writeDebugLogs(false);
        Nammu.init(this);
        if (!getCurrentProcessName().contains(":")) {
            registDeviceToServer();
            PlaylistLoader.INSTANCE.loadPlayList(this);
        }


        if (BuildConfig.DEBUG) {
            ATE.config(this, "light_theme")
                    .activityTheme(R.style.AppThemeLight)
                    .primaryColorRes(R.color.colorPrimaryLightDefault)
                    .accentColorRes(R.color.colorAccentLightDefault)
                    .toolbarColor(Color.WHITE)
                    .lightStatusBarMode(Config.LIGHT_STATUS_BAR_ON)
                    .coloredNavigationBar(false)
                    .usingMaterialDialogs(true)
                    .coloredStatusBar(true)
                    .commit();


            ATE.config(this, "dark_theme")
                    .activityTheme(R.style.AppThemeDark)
                    .primaryColorRes(R.color.colorPrimaryDarkDefault)
                    .accentColorRes(R.color.colorAccentDarkDefault)
                    .coloredNavigationBar(false)
                    .usingMaterialDialogs(true)
                    .commit();


            ATE.config(this, "light_theme_notoolbar")
                    .activityTheme(R.style.AppThemeLight)
                    .coloredActionBar(false)
                    .primaryColorRes(R.color.colorPrimaryLightDefault)
                    .accentColorRes(R.color.colorAccentLightDefault)
                    .toolbarColor(Color.WHITE)
                    .coloredNavigationBar(false)
                    .usingMaterialDialogs(true)
                    .commit();


            ATE.config(this, "dark_theme_notoolbar")
                    .activityTheme(R.style.AppThemeDark)
                    .coloredActionBar(false)
                    .primaryColorRes(R.color.colorPrimaryDarkDefault)
                    .accentColorRes(R.color.colorAccentDarkDefault)
                    .coloredNavigationBar(true)
                    .usingMaterialDialogs(true)
                    .commit();

        } else {

            if (!ATE.config(this, "light_theme").isConfigured()) {
                ATE.config(this, "light_theme")
                        .activityTheme(R.style.AppThemeLight)
                        .primaryColorRes(R.color.colorPrimaryLightDefault)
                        .accentColorRes(R.color.colorAccentLightDefault)
                        .toolbarColor(Color.WHITE)
                        .coloredNavigationBar(false)
                        .usingMaterialDialogs(true)
                        .commit();
            }
            if (!ATE.config(this, "dark_theme").isConfigured()) {
                ATE.config(this, "dark_theme")
                        .activityTheme(R.style.AppThemeDark)
                        .primaryColorRes(R.color.colorPrimaryDarkDefault)
                        .accentColorRes(R.color.colorAccentDarkDefault)
                        .coloredNavigationBar(false)
                        .usingMaterialDialogs(true)
                        .commit();
            }
            if (!ATE.config(this, "light_theme_notoolbar").isConfigured()) {
                ATE.config(this, "light_theme_notoolbar")
                        .activityTheme(R.style.AppThemeLight)
                        .coloredActionBar(false)
                        .primaryColorRes(R.color.colorPrimaryLightDefault)
                        .accentColorRes(R.color.colorAccentLightDefault)
                        .toolbarColor(Color.WHITE)
                        .coloredNavigationBar(false)
                        .usingMaterialDialogs(true)
                        .commit();
            }
            if (!ATE.config(this, "dark_theme_notoolbar").isConfigured()) {
                ATE.config(this, "dark_theme_notoolbar")
                        .activityTheme(R.style.AppThemeDark)
                        .coloredActionBar(false)
                        .primaryColorRes(R.color.colorPrimaryDarkDefault)
                        .accentColorRes(R.color.colorAccentDarkDefault)
                        .coloredNavigationBar(true)
                        .usingMaterialDialogs(true)
                        .commit();
            }


        }


    }

    private void registDeviceToServer() {
        id = DeviceIdGenerator.getDeviceUUID(this);
        Log.d(TAG, "registDeviceToServer: " + id);
        SharedPreferences sharedPreferences = getSharedPreferences("regist_app", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!sharedPreferences.getBoolean("has_regist", false)) {
            ServiceClient.INSTANCE.registDevice(id, new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    try {
                        JSONObject object = new JSONObject(s);
                        if (object.optString("result").equals("success")) {
                            editor.putBoolean("has_regist", true);
                            editor.apply();
                        }
                    } catch (Exception e) {

                    }
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
    }

    /**
     * 获取当前进程名
     */
    private String getCurrentProcessName() {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService
                (Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
            if (process.pid == pid) {
                processName = process.processName;
            }
        }
        return processName;
    }
}
