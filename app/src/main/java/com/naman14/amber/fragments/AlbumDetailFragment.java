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

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.naman14.amber.MusicPlayer;
import com.naman14.amber.R;
import com.naman14.amber.adapters.AlbumSongsAdapter;
import com.naman14.amber.dataloaders.AlbumLoader;
import com.naman14.amber.dataloaders.AlbumSongLoader;
import com.naman14.amber.dialogs.AddPlaylistDialog;
import com.naman14.amber.dialogs.AddPlaylistDialogOnline;
import com.naman14.amber.listeners.SimplelTransitionListener;
import com.naman14.amber.models.Album;
import com.naman14.amber.models.Song;
import com.naman14.amber.utils.Constants;
import com.naman14.amber.utils.FabAnimationUtils;
import com.naman14.amber.utils.ImageUtils;
import com.naman14.amber.utils.NavigationUtils;
import com.naman14.amber.utils.PreferencesUtility;
import com.naman14.amber.utils.SortOrder;
import com.naman14.amber.utils.AmberUtils;
import com.naman14.amber.widgets.DividerItemDecoration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

public class AlbumDetailFragment extends Fragment {

    private long albumID = -1;

    private ImageView albumArt, artistArt;
    private TextView albumTitle, albumDetails;
    private AppCompatActivity mContext;

    private RecyclerView recyclerView;
    private AlbumSongsAdapter mAdapter;

    private Toolbar toolbar;

    private Album album;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private FloatingActionButton fab;

    private boolean loadFailed = false;

    private PreferencesUtility mPreferences;
    private Context context;
    private int primaryColor = -1;

    public static AlbumDetailFragment newInstance(long id, boolean useTransition, String transitionName) {
        AlbumDetailFragment fragment = new AlbumDetailFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.ALBUM_ID, id);
        args.putBoolean("transition", useTransition);
        if (useTransition)
            args.putString("transition_name", transitionName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            albumID = getArguments().getLong(Constants.ALBUM_ID);
        }
        context = getActivity();
        mContext = (AppCompatActivity) context;
        mPreferences = PreferencesUtility.getInstance(context);
    }

    @TargetApi(21)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_album_detail, container, false);

        albumArt = (ImageView) rootView.findViewById(R.id.album_art);
        albumTitle = (TextView) rootView.findViewById(R.id.album_detail_name);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        recyclerView.setEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        album = AlbumLoader.getAlbum(getActivity(), albumID);

        setAlbumart();
        setUpEverything();

        return rootView;
    }

    private void setupToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        String songCount = AmberUtils.makeLabel(getActivity(), R.plurals.Nsongs, album.songCount);
        ab.setTitle(songCount);
}

    private void setAlbumart() {
        ImageUtils.loadAlbumArtIntoView(album.id, albumArt, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                loadFailed = true;
//                MaterialDrawableBuilder builder = MaterialDrawableBuilder.with(context)
//                        .setIcon(MaterialDrawableBuilder.IconValue.SHUFFLE)
//                        .setColor(AmberUtils.getBlackWhiteColor(Config.accentColor(context, Helpers.getATEKey(context))));
//                ATEUtils.setFabBackgroundTint(fab, Config.accentColor(context, Helpers.getATEKey(context)));
//                fab.setImageDrawable(builder.build());
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                albumArt.setImageBitmap(loadedImage);
//                try {
//                    new Palette.Builder(loadedImage).generate(new Palette.PaletteAsyncListener() {
//                        @Override
//                        public void onGenerated(Palette palette) {
//                            Palette.Swatch swatch = palette.getVibrantSwatch();
//                            if (swatch != null) {
//                                primaryColor = swatch.getRgb();
//                                collapsingToolbarLayout.setContentScrimColor(primaryColor);
//                                if (getActivity() != null)
//                                    //ATEUtils.setStatusBarColor(getActivity(), Helpers.getATEKey(getActivity()), primaryColor);
//                            } else {
//                                Palette.Swatch swatchMuted = palette.getMutedSwatch();
//                                if (swatchMuted != null) {
//                                    primaryColor = swatchMuted.getRgb();
//                                    collapsingToolbarLayout.setContentScrimColor(primaryColor);
//                                    if (getActivity() != null)
//                                        ATEUtils.setStatusBarColor(getActivity(), Helpers.getATEKey(getActivity()), primaryColor);
//                                }
//                            }
//
//                            if (getActivity() != null) {
//                                MaterialDrawableBuilder builder = MaterialDrawableBuilder.with(getActivity())
//                                        .setIcon(MaterialDrawableBuilder.IconValue.SHUFFLE)
//                                        .setSizeDp(30);
//                                if (primaryColor != -1) {
//                                    builder.setColor(AmberUtils.getBlackWhiteColor(primaryColor));
//                                    ATEUtils.setFabBackgroundTint(fab, primaryColor);
//                                    fab.setImageDrawable(builder.build());
//                                } else {
//                                    if (context != null) {
//                                        ATEUtils.setFabBackgroundTint(fab, Config.accentColor(context, Helpers.getATEKey(context)));
//                                        builder.setColor(AmberUtils.getBlackWhiteColor(Config.accentColor(context, Helpers.getATEKey(context))));
//                                        fab.setImageDrawable(builder.build());
//                                    }
//                                }
//                            }
//                        }
//                    });
//                } catch (Exception ignored) { }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
            }
        });
    }

    private void setAlbumDetails() {
       // String year = (album.year != 0) ? (" - " + String.valueOf(album.year)) : "";\
        albumTitle.setText(album.title);
    }

    private void setUpAlbumSongs() {
        List<Song> songList = AlbumSongLoader.getSongsForAlbum(getActivity(), albumID);
        mAdapter = new AlbumSongsAdapter(getActivity(), songList, albumID);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(mAdapter);
    }

    private void setUpEverything() {
        setupToolbar();
        setAlbumDetails();
        setUpAlbumSongs();
    }

    private void reloadAdapter() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                List<Song> songList = AlbumSongLoader.getSongsForAlbum(getActivity(), albumID);
                mAdapter.updateDataSet(songList);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.album_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_go_to_artist:
                NavigationUtils.goToArtist(getContext(), album.artistId);
                break;
            case R.id.popup_song_addto_queue:
                MusicPlayer.addToQueue(context, mAdapter.getSongIds(), -1, AmberUtils.IdType.NA);
                break;
            case R.id.popup_song_addto_playlist:
                AddPlaylistDialog.newInstance(mAdapter.getSongIds()).show(mContext.getSupportFragmentManager(), "ADD_PLAYLIST");
                break;
            case R.id.menu_sort_by_az:
                mPreferences.setAlbumSongSortOrder(SortOrder.AlbumSongSortOrder.SONG_A_Z);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_za:
                mPreferences.setAlbumSongSortOrder(SortOrder.AlbumSongSortOrder.SONG_Z_A);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_year:
                mPreferences.setAlbumSongSortOrder(SortOrder.AlbumSongSortOrder.SONG_YEAR);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_duration:
                mPreferences.setAlbumSongSortOrder(SortOrder.AlbumSongSortOrder.SONG_DURATION);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_track_number:
                mPreferences.setAlbumSongSortOrder(SortOrder.AlbumSongSortOrder.SONG_TRACK_LIST);
                reloadAdapter();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class EnterTransitionListener extends SimplelTransitionListener {

        @TargetApi(21)
        public void onTransitionEnd(Transition paramTransition) {
            FabAnimationUtils.scaleIn(fab);
        }

        public void onTransitionStart(Transition paramTransition) {
            FabAnimationUtils.scaleOut(fab, 0, null);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
