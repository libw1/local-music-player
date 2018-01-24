package com.example.administrator.localmusicplayerdemo.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Administrator on 2018-01-24.
 */

public class TimeBroadcastReceiver extends BroadcastReceiver {
    public int duration;
    public int time;
    private TimeCallBack callBack;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("CURRENT_TIME")) {
            duration = intent.getIntExtra("duration", 0);
            time = intent.getIntExtra("currentTime", 0);
            if (callBack != null){
                callBack.updateSeekBar(duration,time);
            }
        }
    }

    public void setCallBack(TimeCallBack callBack){
        this.callBack = callBack;
    }
}
