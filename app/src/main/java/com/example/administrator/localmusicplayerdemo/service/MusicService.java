package com.example.administrator.localmusicplayerdemo.service;

import android.content.Intent;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.example.administrator.localmusicplayerdemo.Actions;


import java.util.List;

import model.Song;

/**
 * Created by Administrator on 2018-01-23.
 */

public class MusicService extends MediaBrowserServiceCompat {

    private PlaybackManager playbackManager;
    private MediaSessionCompat mediaSession;
    private int currentTime;
    private int duration;


    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                if(playbackManager != null) {
                    currentTime = playbackManager.getPosition();
                    duration = playbackManager.getDuration();
                    Intent intent = new Intent();
                    intent.setAction("CURRENT_TIME");
                    intent.putExtra("currentTime", currentTime);
                    intent.putExtra("duration",duration);
                    sendBroadcast(intent);
                    int remainingMillis = 1000 - currentTime % 1000;
                    int delayMillis = Math.max(remainingMillis,20);
                    handler.sendEmptyMessageDelayed(1, delayMillis);
                }

            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        playbackManager = new PlaybackManager(this);
        setupMediaSession();
        handler.sendEmptyMessage(1);
    }

    private void setupMediaSession() {
//        ComponentName mediaButtonReceiver = new ComponentName(getApplicationContext(), MediaButtonIntentReceiver.class);
        if (mediaSession != null) {
            mediaSession = new MediaSessionCompat(this, "MusicService");
            mediaSession.setCallback(playbackManager.getCallBack());

            mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS);
            setSessionToken(mediaSession.getSessionToken());
            mediaSession.setActive(true);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        switch (action){
            case Actions.action_play_song:
                if (intent.hasExtra("songs")){
                    List<Song> songs = intent.getParcelableArrayListExtra("songs");
                    int index = intent.getIntExtra("index",-1);
                    intent.removeExtra("songs");
                    intent.removeExtra("index");
                    playbackManager.playSongs(songs,index);
                }
                break;
            case Actions.action_seek:
                if (intent.hasExtra("progress")){
                    int progress = intent.getIntExtra("progress",0);
                    playbackManager.seekTo(progress);
                    intent.removeExtra("progress");
                }
        }
        return START_NOT_STICKY;
    }




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot("root", null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public boolean isPlaying() {
        return playbackManager.isPlaying();
    }
}
