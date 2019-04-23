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

package com.naman14.amber.nowplaying;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.Config;
import com.naman14.amber.MusicPlayer;
import com.naman14.amber.MusicService;
import com.naman14.amber.R;
import com.naman14.amber.activities.BaseActivity;
import com.naman14.amber.adapters.BaseQueueAdapter;
import com.naman14.amber.helpers.SongModel;
import com.naman14.amber.listeners.MusicStateListener;
import com.naman14.amber.services.ServiceClient;
import com.naman14.amber.utils.Helpers;
import com.naman14.amber.utils.NavigationUtils;
import com.naman14.amber.utils.PreferencesUtility;
import com.naman14.amber.utils.SlideTrackSwitcher;
import com.naman14.amber.utils.AmberUtils;
import com.naman14.amber.widgets.PlayPauseDrawable;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialIconView;

public class BaseNowplayingFragment extends Fragment implements MusicStateListener {

    private MaterialIconView previous, next;
    private PlayPauseDrawable playPauseDrawable = new PlayPauseDrawable();
    private ImageView playPauseFloating;


    private String ateKey;
    private int overflowcounter = 0;
    private TextView songtitle, songalbum, songartist, songduration, elapsedtime;
    private SeekBar mProgress;
    boolean fragmentPaused = false;

    private boolean duetoplaypause = false;

    public ImageView albumart, shuffle, repeat;
    public int accentColor;

    public boolean isOnlinePlayer = false;

    //seekbar
    public Runnable mUpdateProgress = new Runnable() {

        @Override
        public void run() {

            long position = isOnlinePlayer ? MusicPlayer.positionOnline() : MusicPlayer.position();
            if (mProgress != null) {
                mProgress.setProgress((int) position);
                if (elapsedtime != null && getActivity() != null)
                    elapsedtime.setText(AmberUtils.makeShortTimeString(getActivity(), position / 1000));
            }
            overflowcounter--;
            int delay = 250; //not sure why this delay was so high before
            if (overflowcounter < 0 && !fragmentPaused) {
                overflowcounter++;
                mProgress.postDelayed(mUpdateProgress, delay); //delay
            }
        }
    };

    private final View.OnClickListener mFLoatingButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            duetoplaypause = true;
            if (MusicPlayer.getCurrentTrack() == null && MusicPlayer.getCurrentSongModel() == null) {
                Toast.makeText(getContext(), getString(R.string.now_playing_no_track_selected), Toast.LENGTH_SHORT).show();
            } else {
                playPauseDrawable.transformToPlay(true);
                playPauseDrawable.transformToPause(true);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isOnlinePlayer) {
                            MusicPlayer.playOrPauseOnline();
                        } else {
                            MusicPlayer.playOrPause();
                        }
                    }
                }, 250);
            }


        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ateKey = Helpers.getATEKey(getActivity());
        accentColor = Config.accentColor(getActivity(), ateKey);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.now_playing, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isOnlinePlayer) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.menu_go_to_album:
                NavigationUtils.goToAlbum(getContext(), MusicPlayer.getCurrentAlbumId());
                break;
            case R.id.menu_go_to_artist:
                NavigationUtils.goToArtist(getContext(), MusicPlayer.getCurrentArtistId());
                break;
            case R.id.action_lyrics:
                NavigationUtils.goToLyrics(getContext());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        fragmentPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentPaused = false;
        if (mProgress != null)
            mProgress.postDelayed(mUpdateProgress, 10);

    }

    public void setSongDetails(View view) {

        albumart = (ImageView) view.findViewById(R.id.album_art);
        shuffle = (ImageView) view.findViewById(R.id.shuffle);
        repeat = (ImageView) view.findViewById(R.id.repeat);
        next = (MaterialIconView) view.findViewById(R.id.next);
        previous = (MaterialIconView) view.findViewById(R.id.previous);


        playPauseFloating = (ImageView) view.findViewById(R.id.playpausefloating);


        songtitle = (TextView) view.findViewById(R.id.song_title);
        songalbum = (TextView) view.findViewById(R.id.song_album);
        songartist = (TextView) view.findViewById(R.id.song_artist);
        songduration = (TextView) view.findViewById(R.id.song_duration);
        elapsedtime = (TextView) view.findViewById(R.id.song_elapsed_time);


        mProgress = (SeekBar) view.findViewById(R.id.song_progress);

        songtitle.setSelected(true);


        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle("");
        }


        if (playPauseFloating != null) {
            playPauseDrawable.setColorFilter(AmberUtils.getBlackWhiteColor(accentColor), PorterDuff.Mode.MULTIPLY);
            playPauseFloating.setImageDrawable(playPauseDrawable);
            if (MusicPlayer.isOnlinePlaying() || MusicPlayer.isPlaying())
                playPauseDrawable.transformToPause(false);
            else playPauseDrawable.transformToPlay(false);
        }

        setSongDetails();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("dark_theme", false)) {
            ATE.apply(this, "dark_theme");
        } else {
            ATE.apply(this, "light_theme");
        }
        updateUITheme();
    }

    private void updateUITheme() {
//        if(isDarkTheme){
//
//        }else {
//
//        }
        songtitle.setTextColor(Color.WHITE);
        songartist.setTextColor(Color.WHITE);
        next.setColor(Color.WHITE);
        previous.setColor(Color.WHITE);
        elapsedtime.setTextColor(Color.WHITE);
        songduration.setTextColor(Color.WHITE);
    }

    private void setSongDetails() {
        updateSongDetails();
        setSeekBarListener();

        if (next != null) {
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (isOnlinePlayer) {
                                MusicPlayer.playOnlineNext();
                            } else {
                                MusicPlayer.next();
                            }
                            notifyPlayingDrawableChange();
                        }
                    }, 200);

                }
            });
        }
        if (previous != null) {
            previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (isOnlinePlayer) {
                                MusicPlayer.playOnlinePrevious();
                            } else {
                                MusicPlayer.previous(getActivity(), false);
                            }
                            notifyPlayingDrawableChange();
                        }
                    }, 200);

                }
            });
        }


        if (playPauseFloating != null)
            playPauseFloating.setOnClickListener(mFLoatingButtonListener);

        updateShuffleState();
        updateRepeatState();

    }

    public void updateShuffleState() {
        if (shuffle != null && getActivity() != null) {
            MaterialDrawableBuilder builder = MaterialDrawableBuilder.with(getActivity())
                    .setIcon(MaterialDrawableBuilder.IconValue.SHUFFLE)
                    .setSizeDp(30);

            if (getActivity() != null) {
                if (isOnlinePlayer) {
                    if (MusicPlayer.getShuffleStateOnline() == 0) {
                        builder.setColor(Color.WHITE);
                        //builder.setColor(Config.textColorPrimary(getActivity(), ateKey));
                    } else {
                        builder.setColor(Config.accentColor(getActivity(), ateKey));
                    }
                } else {
                    if (MusicPlayer.getShuffleMode() == 0) {
                        builder.setColor(Color.WHITE);
                        //builder.setColor(Config.textColorPrimary(getActivity(), ateKey));
                    } else {
                        builder.setColor(Config.accentColor(getActivity(), ateKey));
                    }
                }

            }

            shuffle.setImageDrawable(builder.build());
            shuffle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isOnlinePlayer) {
                        if (MusicPlayer.getShuffleStateOnline() == 1) {
                            MusicPlayer.setShuffleOnline(0);
                        } else {
                            MusicPlayer.setShuffleOnline(1);
                        }
                        updateShuffleState();
                    } else {
                        MusicPlayer.cycleShuffle();
                        updateShuffleState();
                        updateRepeatState();
                    }

                }
            });
        }
    }

    public void updateRepeatState() {
        if (repeat != null && getActivity() != null) {
            MaterialDrawableBuilder builder = MaterialDrawableBuilder.with(getActivity())
                    .setSizeDp(30);

            if (isOnlinePlayer) {
                if (MusicPlayer.getRepeatStateOnline() == 0) {
                    builder.setIcon(MaterialDrawableBuilder.IconValue.REPEAT);
                    builder.setColor(Color.WHITE);
                } else {
                    builder.setIcon(MaterialDrawableBuilder.IconValue.REPEAT_ONCE);
                    builder.setColor(Config.accentColor(getActivity(), ateKey));
                }
            } else {
                if (MusicPlayer.getRepeatMode() == MusicService.REPEAT_NONE) {
                    builder.setIcon(MaterialDrawableBuilder.IconValue.REPEAT);
                    builder.setColor(Color.WHITE);
                    // builder.setColor(Config.textColorPrimary(getActivity(), ateKey));
                } else if (MusicPlayer.getRepeatMode() == MusicService.REPEAT_CURRENT) {
                    builder.setIcon(MaterialDrawableBuilder.IconValue.REPEAT_ONCE);
                    builder.setColor(Config.accentColor(getActivity(), ateKey));
                } else if (MusicPlayer.getRepeatMode() == MusicService.REPEAT_ALL) {
                    builder.setColor(Config.accentColor(getActivity(), ateKey));
                    builder.setIcon(MaterialDrawableBuilder.IconValue.REPEAT);
                }
            }


            repeat.setImageDrawable(builder.build());
            repeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isOnlinePlayer) {
                        if (MusicPlayer.getRepeatStateOnline() == 1) {
                            MusicPlayer.setRepeatOnline(0);
                        } else {
                            MusicPlayer.setRepeatOnline(1);
                        }
                        updateRepeatState();
                    } else {
                        MusicPlayer.cycleRepeat();
                        updateRepeatState();
                        updateShuffleState();
                    }

                }
            });
        }
    }

    private void setSeekBarListener() {
        if (mProgress != null)
            mProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if (b) {
                        if (isOnlinePlayer) {
                        } else {
                            MusicPlayer.seek((long) i);
                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
    }

    public void updateSongDetails() {
        //do not reload image if it was a play/pause change
        if (!duetoplaypause) {
            if (albumart != null) {
                ImageLoader.getInstance().displayImage(isOnlinePlayer ? ServiceClient.SERVICE_URL + "/album_pic?song_id=" + MusicPlayer.getCurrentSongModel().getId() : AmberUtils.getAlbumArtUri(MusicPlayer.getCurrentAlbumId()).toString(), albumart,
                        new DisplayImageOptions.Builder().cacheInMemory(true)
                                .showImageOnFail(R.drawable.holder)
                                .build(), new SimpleImageLoadingListener() {

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                doAlbumArtStuff(loadedImage);
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                Bitmap failedBitmap = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.holder);
                                doAlbumArtStuff(failedBitmap);
                            }

                        });
            }
            if (isOnlinePlayer) {
                SongModel song = MusicPlayer.getCurrentSongModel();
                if (song != null) {
                    if (songtitle != null && song.getName() != null) {
                        songtitle.setText(song.getName());
                        if (song.getName().length() <= 23) {
                            songtitle.setTextSize(25);
                        } else if (song.getName().length() >= 30) {
                            songtitle.setTextSize(18);
                        } else {
                            songtitle.setTextSize(18 + (song.getName().length() - 24));
                        }
                        Log.v("BaseNowPlayingFrag", "Title Text Size: " + songtitle.getTextSize());
                    }
                    if (songartist != null) {
                        songartist.setText(song.getArtistName());
                        songartist.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                NavigationUtils.goToArtist(getContext(), MusicPlayer.getCurrentArtistId());
                            }
                        });
                    }
                    if (songalbum != null)
                        songalbum.setText(song.getAlbumId());
                }
            } else {
                setSongUI();
            }


        }
        duetoplaypause = false;


        if (playPauseFloating != null)
            updatePlayPauseFloatingButton();

        if (songduration != null && getActivity() != null)
            songduration.setText(AmberUtils.makeShortTimeString(getActivity(), (isOnlinePlayer ? MusicPlayer.getCurrentSongModel().getDuration() : (int) MusicPlayer.duration() / 1000)));

        if (mProgress != null) {
            mProgress.setMax(isOnlinePlayer ? MusicPlayer.getCurrentSongModel().getDuration() * 1000 : (int) MusicPlayer.duration());
            if (mUpdateProgress != null) {
                mProgress.removeCallbacks(mUpdateProgress);
            }
            mProgress.postDelayed(mUpdateProgress, 10);
        }
    }

    private void setSongUI() {
        if (songtitle != null && MusicPlayer.getTrackName() != null) {
            songtitle.setText(MusicPlayer.getTrackName());
            if (MusicPlayer.getTrackName().length() <= 23) {
                songtitle.setTextSize(25);
            } else if (MusicPlayer.getTrackName().length() >= 30) {
                songtitle.setTextSize(18);
            } else {
                songtitle.setTextSize(18 + (MusicPlayer.getTrackName().length() - 24));
            }
            Log.v("BaseNowPlayingFrag", "Title Text Size: " + songtitle.getTextSize());
        }
        if (songartist != null) {
            songartist.setText(MusicPlayer.getArtistName());
            songartist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavigationUtils.goToArtist(getContext(), MusicPlayer.getCurrentArtistId());
                }
            });
        }
        if (songalbum != null)
            songalbum.setText(MusicPlayer.getAlbumName());
    }

    public void updatePlayPauseFloatingButton() {
        if (MusicPlayer.isOnlinePlaying() || MusicPlayer.isPlaying()) {
            playPauseDrawable.transformToPause(false);
        } else {
            playPauseDrawable.transformToPlay(false);
        }
    }

    public void notifyPlayingDrawableChange() {
        int position = isOnlinePlayer ? MusicPlayer.getCurrentPosOnline() : MusicPlayer.getQueuePosition();
        BaseQueueAdapter.currentlyPlayingPosition = position;
    }

    public void restartLoader() {

    }

    public void onPlaylistChanged() {

    }

    public void onMetaChanged() {
        updateSongDetails();
    }

    public void setMusicStateListener() {
        ((BaseActivity) getActivity()).setMusicStateListenerListener(this);
    }

    public void doAlbumArtStuff(Bitmap loadedImage) {

    }

    protected void initGestures(View v) {
        if (PreferencesUtility.getInstance(v.getContext()).isGesturesEnabled()) {
            new SlideTrackSwitcher() {
                @Override
                public void onSwipeBottom() {
                    getActivity().finish();
                }
            }.attach(v);
        }
    }
}
