package com.example.administrator.localmusicplayerdemo.service;

import android.content.ComponentName;
import android.content.Intent;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;

import com.example.administrator.localmusicplayerdemo.Actions;
import com.example.administrator.localmusicplayerdemo.Song;

import java.util.List;

/**
 * Created by Administrator on 2018-01-23.
 */

public class MusicService extends MediaBrowserServiceCompat {

    private PlaybackManager playbackManager;
    private MediaSessionCompat mediaSession;


/*    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                if(playback != null) {
                    currentTime = playback.position(); // 获取当前音乐播放的位置
                    Intent intent = new Intent();
                    intent.setAction("CURRENT_TIME");
                    intent.putExtra("currentTime", currentTime);
                    sendBroadcast(intent); // 给PlayerActivity发送广播
                    handler.sendEmptyMessageDelayed(1, 1000);
                }

            }
        }
    };*/

    @Override
    public void onCreate() {
        super.onCreate();
        playbackManager = new PlaybackManager(this);
        setupMediaSession();
    }

    private void setupMediaSession() {
        ComponentName mediaButtonReceiver = new ComponentName(getApplicationContext(), MediaButtonIntentReceiver.class);
        mediaSession = new MediaSessionCompat(this, "loclplayer");
        mediaSession.setCallback(playbackManager.getCallBack());

        mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS);
        setSessionToken(mediaSession.getSessionToken());
        mediaSession.setActive(true);
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
        if (TextUtils.equals(getPackageName(), clientPackageName)) {
            return new BrowserRoot("music", null);
        }
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
