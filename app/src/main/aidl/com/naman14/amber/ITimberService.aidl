package com.naman14.amber;

import com.naman14.amber.helpers.MusicPlaybackTrack;
import com.naman14.amber.helpers.SongModel;

interface ITimberService
{
    void openFile(String path);
    void open(in long [] list, int position, long sourceId, int sourceType);
    void stop();
    void pause();
    void play();
    void prev(boolean forcePrevious);
    void next();
    void enqueue(in long [] list, int action, long sourceId, int sourceType);
    void setQueuePosition(int index);
    void setShuffleMode(int shufflemode);
    void setRepeatMode(int repeatmode);
    void moveQueueItem(int from, int to);
    void refresh();
    void playlistChanged();
    boolean isPlaying();
    long [] getQueue();
    long getQueueItemAtPosition(int position);
    int getQueueSize();
    int getQueuePosition();
    int getQueueHistoryPosition(int position);
    int getQueueHistorySize();
    int[] getQueueHistoryList();
    long duration();
    long position();
    long seek(long pos);
    void seekRelative(long deltaInMs);
    long getAudioId();
    MusicPlaybackTrack getCurrentTrack();
    MusicPlaybackTrack getTrack(int index);
    long getNextAudioId();
    long getPreviousAudioId();
    long getArtistId();
    long getAlbumId();
    String getArtistName();
    String getTrackName();
    String getAlbumName();
    String getPath();
    int getShuffleMode();
    int removeTracks(int first, int last);
    int removeTrack(long id);
    boolean removeTrackAtPosition(long id, int position);
    int getRepeatMode();
    int getMediaMountedCount();
    int getAudioSessionId();
    void setAutoShutDown(long time);

    void playOnline(in SongModel model);
    void playOnlineWithList(in List<SongModel> list, int pos);
    String getCurrentOnlineId();
    boolean isOnlinePlaying();
    void onlinePause();
    void playOrPauseOnline();
    void onlineStart();
    long positionOnline();
    void seekOnline(long pos);
    void playPreviousOnline();
    void playNextOnline();
    int getCurrentPosOnline();
    int getShuffleStateOnline();
    int getRepeatStateOnline();
    void setShuffleStateOnline(int state);
    void setRepeatStateOnline(int state);
    SongModel getCurrentSongOnline();
}

