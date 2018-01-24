package com.example.administrator.localmusicplayerdemo;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.administrator.localmusicplayerdemo.fragments.SongFragment;
import com.example.administrator.localmusicplayerdemo.service.MediaBrowserHelper;
import com.example.administrator.localmusicplayerdemo.service.MusicService;
import com.example.administrator.localmusicplayerdemo.service.TimeBroadcastReceiver;
import com.example.administrator.localmusicplayerdemo.service.TimeCallBack;
import com.example.administrator.localmusicplayerdemo.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener ,TimeCallBack{

    private ImageView imageView;
    private TextView title;
    private Button play;
    private Button previous;
    private Button next;
    private SeekBar seekBar;
    private TextView time;
    private TextView duration;
    private MediaBrowserCompat mediaBrowser;
    private MediaControllerCompat controllerCompat;
    private MediaBrowserHelper helper;
    private TimeBroadcastReceiver receiver;
    private boolean isUsing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ViewPager viewPager = findViewById(R.id.view_pager);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new SongFragment());
        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getSupportFragmentManager(),fragments);
        viewPager.setAdapter(adapter);
        helper = new MediaBrowserHelper(this);
        helper.onStart();
        receiver = new TimeBroadcastReceiver();
        receiver.setCallBack(this);
        IntentFilter filter = new IntentFilter("CURRENT_TIME");
        registerReceiver(receiver, filter);
        initView();
    }

    private void initView() {
        imageView = findViewById(R.id.song_image);
        title = findViewById(R.id.song_title);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        time = findViewById(R.id.time);
        duration = findViewById(R.id.duration);
        seekBar = findViewById(R.id.seek_bar);
        play.setOnClickListener(this);
        previous.setOnClickListener(this);
        next.setOnClickListener(this);
        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUsing = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isUsing = false;
                int progress = seekBar.getProgress();
                Intent intent = new Intent(HomeActivity.this, MusicService.class);
                intent.setAction(Actions.action_seek);
                intent.putExtra("progress",progress);
                startService(intent);
            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.play:
                helper.getTransportControls().play();
                break;
            case R.id.previous:
                controllerCompat.getTransportControls().skipToPrevious();
                break;
            case R.id.next:
                controllerCompat.getTransportControls().skipToNext();
                break;
        }
    }

    @Override
    public void updateSeekBar(int duration, int currentTime) {
        if (!isUsing) {
            seekBar.setMax(duration);
            seekBar.setProgress(currentTime);
            time.setText(TimeUtils.formatTime(currentTime));
            this.duration.setText(TimeUtils.formatTime(duration));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null){
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

}
