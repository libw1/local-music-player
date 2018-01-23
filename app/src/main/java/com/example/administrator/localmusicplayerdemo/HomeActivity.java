package com.example.administrator.localmusicplayerdemo;

import android.content.ComponentName;
import android.content.Intent;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.administrator.localmusicplayerdemo.fragments.SongFragment;
import com.example.administrator.localmusicplayerdemo.service.MusicService;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView imageView;
    private TextView title;
    private Button play;
    private Button previous;
    private Button next;
    private SeekBar seekBar;
    private MediaBrowserCompat mediaBrowser;
    private MediaControllerCompat controllerCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ViewPager viewPager = findViewById(R.id.view_pager);
        initView();
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new SongFragment());
        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getSupportFragmentManager(),fragments);
        viewPager.setAdapter(adapter);
    }

    private void initView() {
        imageView = findViewById(R.id.song_image);
        title = findViewById(R.id.song_title);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seek_bar);
        play.setOnClickListener(this);
        previous.setOnClickListener(this);
        next.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.play:
                mediaBrowser = new MediaBrowserCompat(this, new ComponentName(this,MusicService.class),
                        playbackConnectionCallback, null);
                mediaBrowser.connect();
                break;
            case R.id.previous:
                controllerCompat.getTransportControls().skipToPrevious();
                break;
            case R.id.next:
                controllerCompat.getTransportControls().skipToNext();
                break;
        }
    }

    private MediaBrowserCompat.ConnectionCallback playbackConnectionCallback = new MediaBrowserCompat.ConnectionCallback(){

        @Override
        public void onConnected() {
            super.onConnected();
            try {
                connectToSession(mediaBrowser.getSessionToken());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConnectionFailed() {
            super.onConnectionFailed();
        }

        @Override
        public void onConnectionSuspended() {
            super.onConnectionSuspended();
        }
    };

    private void connectToSession(MediaSessionCompat.Token sessionToken) throws RemoteException {
        MediaControllerCompat mediaController = new MediaControllerCompat(getApplicationContext(), sessionToken);
        this.controllerCompat = mediaController;
        mediaController.registerCallback(mediaControllerCallback);
        PlaybackStateCompat state = mediaController.getPlaybackState();
        MediaMetadataCompat metadata = mediaController.getMetadata();
        controllerCompat.getTransportControls().play();
    }

    private final MediaControllerCompat.Callback mediaControllerCallback = new MediaControllerCompat.Callback(){

    };
}
