package com.example.administrator.localmusicplayerdemo.service;

import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2018-01-23.
 */

public interface Playback {

    boolean setDataSource(String path);

    void setNextDataSource(@Nullable String path);

    void setCallbacks(PlaybackCallbacks callbacks);

    boolean isInitialized();

    boolean start();

    void stop();

    void release();

    boolean pause();

    boolean isPlaying();

    int duration();

    int position();

    int seek(int whereto);

    boolean setVolume(float vol);

    boolean setAudioSessionId(int sessionId);

    int getAudioSessionId();

    interface PlaybackCallbacks {
        void onTrackWentToNext();

        void onTrackEnded();
    }
}
