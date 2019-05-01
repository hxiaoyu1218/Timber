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

package com.naman14.amber.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.naman14.amber.MusicPlayer;
import com.naman14.amber.R;
import com.naman14.amber.activities.AlbumDetailActivity;
import com.naman14.amber.activities.ArtistDetailActivity;
import com.naman14.amber.activities.MainActivity;
import com.naman14.amber.activities.NowPlayingActivity;
import com.naman14.amber.activities.PlaylistDetailActivity;
import com.naman14.amber.activities.PlaylistOnlineActivity;
import com.naman14.amber.activities.SearchActivity;
import com.naman14.amber.activities.SearchOnlineActivity;
import com.naman14.amber.activities.SettingsActivity;
import com.naman14.amber.fragments.AlbumDetailFragment;
import com.naman14.amber.fragments.ArtistDetailFragment;
import com.naman14.amber.models.Playlist;
import com.naman14.amber.nowplaying.AmberPlayerFragment;
import com.naman14.amber.services.PlayList;

import java.util.ArrayList;

public class NavigationUtils {

    @TargetApi(21)
    public static void navigateToAlbum(Activity context, long albumID, Pair<View, String> transitionViews) {

//        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
//        Fragment fragment;
//
//        transaction.setCustomAnimations(R.anim.activity_fade_in,
//                R.anim.activity_fade_out, R.anim.activity_fade_in, R.anim.activity_fade_out);
//        fragment = AlbumDetailFragment.newInstance(albumID, false, null);
//
//        transaction.hide(((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment_container));
//        transaction.add(R.id.fragment_container, fragment);
//        transaction.addToBackStack(null).commit();

        Intent intent = new Intent(context, AlbumDetailActivity.class);
        intent.putExtra(Constants.ALBUM_ID, albumID);
        context.startActivity(intent);

    }

    @TargetApi(21)
    public static void navigateToArtist(Activity context, long artistID, Pair<View, String> transitionViews) {

//        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
//        Fragment fragment;
//
//        transaction.setCustomAnimations(R.anim.activity_fade_in,
//                R.anim.activity_fade_out, R.anim.activity_fade_in, R.anim.activity_fade_out);
//        fragment = ArtistDetailFragment.newInstance(artistID, false, null);
//
//        transaction.hide(((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment_container));
//        transaction.add(R.id.fragment_container, fragment);
//        transaction.addToBackStack(null).commit();
        Intent intent = new Intent(context, ArtistDetailActivity.class);
        intent.putExtra(Constants.ARTIST_ID, artistID);
        context.startActivity(intent);
    }

    public static void goToArtist(Context context, long artistId) {
//        Intent intent = new Intent(context, MainActivity.class);
//        intent.setAction(Constants.NAVIGATE_ARTIST);
//        intent.putExtra(Constants.ARTIST_ID, artistId);
//        context.startActivity(intent);
        Intent intent = new Intent(context, ArtistDetailActivity.class);
        intent.putExtra(Constants.ARTIST_ID, artistId);
        context.startActivity(intent);
    }

    public static void goToAlbum(Context context, long albumId) {
        Intent intent = new Intent(context, AlbumDetailActivity.class);
        intent.putExtra(Constants.ALBUM_ID, albumId);
        context.startActivity(intent);
//        Intent intent = new Intent(context, MainActivity.class);
//        intent.setAction(Constants.NAVIGATE_ALBUM);
//        intent.putExtra(Constants.ALBUM_ID, albumId);
//        context.startActivity(intent);
    }

    public static void goToLyrics(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Constants.NAVIGATE_LYRICS);
        context.startActivity(intent);
    }

    public static void navigateToNowplayingOnline(Context context) {

        final Intent intent = new Intent(context, NowPlayingActivity.class);
        intent.putExtra(Constants.IS_ONLINE_PLAYING, true);
        context.startActivity(intent);
    }

    public static void navigateToNowplaying(Activity context, boolean withAnimations) {
        final Intent intent = new Intent(context, NowPlayingActivity.class);
        if (MusicPlayer.isOnlineMode()) {
            intent.putExtra(Constants.IS_ONLINE_PLAYING, true);
        }
        context.startActivity(intent);
    }

    public static Intent getNowPlayingIntent(Context context) {

        final Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Constants.NAVIGATE_NOWPLAYING);
        return intent;
    }

    public static void navigateOnlinePlayList(Context context, PlayList playlist) {
        Intent intent = new Intent(context, PlaylistOnlineActivity.class);
        intent.putExtra(Constants.PLAY_LIST_NAME, playlist.getListName());
        intent.putExtra(Constants.PLAY_LIST_ID, playlist.getListId());
        context.startActivity(intent);
    }

    public static void navigateOnlineSearch(Context context) {
        Intent intent = new Intent(context, SearchOnlineActivity.class);
        context.startActivity(intent);
    }

    public static void navigateToSettings(Activity context) {
        final Intent intent = new Intent(context, SettingsActivity.class);
        intent.setAction(Constants.NAVIGATE_SETTINGS);
        context.startActivity(intent);
    }

    public static void navigateToSearch(Activity context) {
        final Intent intent = new Intent(context, SearchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setAction(Constants.NAVIGATE_SEARCH);
        context.startActivity(intent);
    }


    @TargetApi(21)
    public static void navigateToPlaylistDetail(Activity context, String action, long firstAlbumID, String playlistName, int foregroundcolor, long playlistID, ArrayList<Pair> transitionViews) {
        final Intent intent = new Intent(context, PlaylistDetailActivity.class);
        intent.setAction(action);
        intent.putExtra(Constants.PLAYLIST_ID, playlistID);
        intent.putExtra(Constants.PLAYLIST_FOREGROUND_COLOR, foregroundcolor);
        intent.putExtra(Constants.ALBUM_ID, firstAlbumID);
        intent.putExtra(Constants.PLAYLIST_NAME, playlistName);
        intent.putExtra(Constants.ACTIVITY_TRANSITION, transitionViews != null);

        if (transitionViews != null && AmberUtils.isLollipop()) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(context, transitionViews.get(0), transitionViews.get(1), transitionViews.get(2));
            context.startActivityForResult(intent, Constants.ACTION_DELETE_PLAYLIST, options.toBundle());
        } else {
            context.startActivityForResult(intent, Constants.ACTION_DELETE_PLAYLIST);
        }
    }

    @TargetApi(21)
    public static void navigateToPlaylistDetailOnline(Activity context, String action,int pos, int foregroundcolor, ArrayList<Pair> transitionViews) {
        final Intent intent = new Intent(context, PlaylistDetailActivity.class);
        intent.setAction(action);
        intent.putExtra(Constants.PLAY_LIST_POS, pos);
        intent.putExtra(Constants.IS_ONLINE_PLAYING, true);
        intent.putExtra(Constants.PLAYLIST_FOREGROUND_COLOR, foregroundcolor);
        intent.putExtra(Constants.ACTIVITY_TRANSITION, transitionViews != null);

        if (transitionViews != null && AmberUtils.isLollipop()) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(context, transitionViews.get(0), transitionViews.get(1), transitionViews.get(2));
            context.startActivityForResult(intent, Constants.ACTION_DELETE_PLAYLIST, options.toBundle());
        } else {
            context.startActivityForResult(intent, Constants.ACTION_DELETE_PLAYLIST);
        }
    }

    public static void navigateToEqualizer(Activity context) {
        try {
            // The google MusicFX apps need to be started using startActivityForResult
            context.startActivityForResult(AmberUtils.createEffectsIntent(), 666);
        } catch (final ActivityNotFoundException notFound) {
            Toast.makeText(context, "Equalizer not found", Toast.LENGTH_SHORT).show();
        }
    }

    public static AmberPlayerFragment getFragmentForNowplayingID(String fragmentID) {
        return new AmberPlayerFragment();
//        switch (fragmentID) {
//            case Constants.TIMBER1:
//                return new Timber1();
//            case Constants.TIMBER2:
//                return new Timber2();
//            case Constants.TIMBER3:
//                return new AmberPlayerFragment();
//            case Constants.TIMBER4:
//                return new Timber4();
//            case Constants.TIMBER5:
//                return new Timber5();
//            case Constants.TIMBER6:
//                return new Timber6();
//            default:
//                return new Timber1();
//        }

    }

    public static int getIntForCurrentNowplaying(String nowPlaying) {
        switch (nowPlaying) {
            case Constants.TIMBER1:
                return 0;
            case Constants.TIMBER2:
                return 1;
            case Constants.TIMBER3:
                return 2;
            case Constants.TIMBER4:
                return 3;
            case Constants.TIMBER5:
                return 4;
            case Constants.TIMBER6:
                return 5;
            default:
                return 2;
        }

    }

}
