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

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.appthemeengine.ATE;
import com.naman14.amber.MusicPlayer;
import com.naman14.amber.R;
import com.naman14.amber.activities.BaseActivity;
import com.naman14.amber.adapters.OnlineSongListAdapter;
import com.naman14.amber.adapters.PlayingQueueAdapter;
import com.naman14.amber.dataloaders.QueueLoader;
import com.naman14.amber.helpers.SongModel;
import com.naman14.amber.listeners.MusicStateListener;
import com.naman14.amber.models.Song;
import com.naman14.amber.widgets.BaseRecyclerView;
import com.naman14.amber.widgets.DragSortRecycler;

public class QueueFragment extends Fragment implements MusicStateListener {

    private PlayingQueueAdapter mAdapter;
    private OnlineSongListAdapter adapter;
    private BaseRecyclerView recyclerView;
    private boolean isOnline;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_queue, container, false);
        isOnline = MusicPlayer.isOnlineMode();
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("dark_theme", false)) {
            toolbar.setPopupTheme(R.style.ToolBarDark);
        } else {
            toolbar.setPopupTheme(R.style.ToolBarLight);
        }
        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.playing_queue);

        recyclerView = rootView.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(null);
        recyclerView.setEmptyView(getActivity(), rootView.findViewById(R.id.list_empty), "No songs in queue");

        new loadQueueSongs().execute("");
        ((BaseActivity) getActivity()).setMusicStateListenerListener(this);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("dark_theme", false)) {
            ATE.apply(this, "dark_theme");
        } else {
            ATE.apply(this, "light_theme");
        }
    }

    public void restartLoader() {

    }

    public void onPlaylistChanged() {

    }

    public void onMetaChanged() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private class loadQueueSongs extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (isOnline) {
                adapter = new OnlineSongListAdapter(getActivity());
                adapter.bindData(MusicPlayer.getCurrentSongList());
            } else {
                mAdapter = new PlayingQueueAdapter(getActivity(), QueueLoader.getQueueSongs(getActivity()));
            }

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            if (isOnline) {
                recyclerView.setAdapter(adapter);
            } else {
                recyclerView.setAdapter(mAdapter);
            }

            DragSortRecycler dragSortRecycler = new DragSortRecycler();
            dragSortRecycler.setViewHandleId(R.id.reorder);

            dragSortRecycler.setOnItemMovedListener(new DragSortRecycler.OnItemMovedListener() {
                @Override
                public void onItemMoved(int from, int to) {
                    Log.d("queue", "onItemMoved " + from + " to " + to);
                    if (isOnline) {
                        //server     upload to server and callback to refresh adapter data
                        SongModel songModel = adapter.getMData().get(from);
                        adapter.removeSong(from);
                        adapter.addSongAt(songModel, to);
                        adapter.notifyDataSetChanged();
                    } else {
                        Song song = mAdapter.getSongAt(from);
                        mAdapter.removeSongAt(from);
                        mAdapter.addSongTo(to, song);
                        mAdapter.notifyDataSetChanged();
                        MusicPlayer.moveQueueItem(from, to);
                    }
                }
            });

            recyclerView.addItemDecoration(dragSortRecycler);
            recyclerView.addOnItemTouchListener(dragSortRecycler);
            recyclerView.addOnScrollListener(dragSortRecycler.getScrollListener());

            recyclerView.getLayoutManager().scrollToPosition(isOnline ? adapter.getCurrentlyPlayingPosition() : mAdapter.currentlyPlayingPosition);

        }

        @Override
        protected void onPreExecute() {
        }
    }

}

