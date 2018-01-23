package com.example.administrator.localmusicplayerdemo;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.localmusicplayerdemo.service.Playback;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2018-01-23.
 */

public class LocalMusicPlayer implements Playback,MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private MediaPlayer currentMediaPlayer = new MediaPlayer();
    private MediaPlayer nextMediaPlayer;
    private Context context;
    private boolean isInitialized;
    private Playback.PlaybackCallbacks callbacks;


    public LocalMusicPlayer(Context context){
        this.context = context;
    }

    /**
     * @param path The path of the file, or the http/rtsp URL of the stream
     *             you want to play
     * @return True if the <code>player</code> has been prepared and is
     * ready to play, false otherwise
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean setDataSource(String path) {
        isInitialized = false;
        isInitialized = setDataSourceImpl(currentMediaPlayer, path);
        if (isInitialized) {
            setNextDataSource(null);
        }
        return isInitialized;
    }

    private boolean setDataSourceImpl(MediaPlayer currentMediaPlayer, String path) {
        if (context == null) {
            return false;
        }
        try {
            currentMediaPlayer.reset();
            currentMediaPlayer.setOnPreparedListener(null);
            if (path.startsWith("content://")) {
                currentMediaPlayer.setDataSource(context, Uri.parse(path));
            } else {
                currentMediaPlayer.setDataSource(path);
            }
            currentMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            currentMediaPlayer.prepare();
        } catch (Exception e) {
            return false;
        }
        currentMediaPlayer.setOnCompletionListener(this);
        currentMediaPlayer.setOnErrorListener(this);
        final Intent intent = new Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION);
        intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId());
        intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.getPackageName());
        intent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
        context.sendBroadcast(intent);
        return true;
    }

    /**
     * Set the MediaPlayer to start when this MediaPlayer finishes playback.
     *
     * @param path The path of the file, or the http/rtsp URL of the stream
     *             you want to play
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void setNextDataSource(@Nullable String path) {
        if (context == null) {
            return;
        }
        try {
            currentMediaPlayer.setNextMediaPlayer(null);
        } catch (IllegalArgumentException e) {
            Log.i(TAG, "Next media player is current one, continuing");
        } catch (IllegalStateException e) {
            Log.e(TAG, "Media player not initialized!");
            return;
        }
        if (nextMediaPlayer != null) {
            nextMediaPlayer.release();
            nextMediaPlayer = null;
        }
        if (path == null) {
            return;
        }
        nextMediaPlayer = new MediaPlayer();
        nextMediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        nextMediaPlayer.setAudioSessionId(getAudioSessionId());
        if (setDataSourceImpl(nextMediaPlayer, path)) {
            try {
                currentMediaPlayer.setNextMediaPlayer(nextMediaPlayer);
            } catch (@NonNull IllegalArgumentException | IllegalStateException e) {
                    Log.e(TAG, "setNextDataSource: setNextMediaPlayer()", e);
                if (nextMediaPlayer != null) {
                    nextMediaPlayer.release();
                    nextMediaPlayer = null;
                }
            }
        } else {
            if (nextMediaPlayer != null) {
                nextMediaPlayer.release();
                nextMediaPlayer = null;
            }
        }
    }

    /**
     * Sets the callbacks
     *
     * @param callbacks The callbacks to use
     */
    @Override
    public void setCallbacks(PlaybackCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    /**
     * @return True if the player is ready to go, false otherwise
     */
    @Override
    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Starts or resumes playback.
     */
    @Override
    public boolean start() {
        try {
            currentMediaPlayer.start();
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * Resets the MediaPlayer to its uninitialized state.
     */
    @Override
    public void stop() {
        currentMediaPlayer.reset();
        isInitialized = false;
    }

    /**
     * Releases resources associated with this MediaPlayer object.
     */
    @Override
    public void release() {
        stop();
        currentMediaPlayer.release();
    }

    /**
     * Pauses playback. Call start() to resume.
     */
    @Override
    public boolean pause() {
        try {
            currentMediaPlayer.pause();
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * Checks whether the MultiPlayer is playing.
     */
    @Override
    public boolean isPlaying() {
        return isInitialized && currentMediaPlayer.isPlaying();
    }

    /**
     * Gets the duration of the file.
     *
     * @return The duration in milliseconds
     */
    @Override
    public int duration() {
        if (!isInitialized) {
            return -1;
        }
        try {
            return currentMediaPlayer.getDuration();
        } catch (IllegalStateException e) {
            return -1;
        }
    }

    /**
     * Gets the current playback position.
     *
     * @return The current position in milliseconds
     */
    @Override
    public int position() {
        if (!isInitialized) {
            return -1;
        }
        try {
            return currentMediaPlayer.getCurrentPosition();
        } catch (IllegalStateException e) {
            return -1;
        }
    }

    /**
     * Gets the current playback position.
     *
     * @param whereto The offset in milliseconds from the start to seek to
     * @return The offset in milliseconds from the start to seek to
     */
    @Override
    public int seek(int whereto) {
        try {
            currentMediaPlayer.seekTo(whereto);
            return whereto;
        } catch (IllegalStateException e) {
            return -1;
        }
    }

    @Override
    public boolean setVolume(float vol) {
        try {
            currentMediaPlayer.setVolume(vol, vol);
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * Sets the audio session ID.
     *
     * @param sessionId The audio session ID
     */
    @Override
    public boolean setAudioSessionId(int sessionId) {
        try {
            currentMediaPlayer.setAudioSessionId(sessionId);
            return true;
        } catch (@NonNull IllegalArgumentException | IllegalStateException e) {
            return false;
        }
    }


    /**
     * Returns the audio session ID.
     *
     * @return The current audio session ID.
     */
    @Override
    public int getAudioSessionId() {
        return currentMediaPlayer.getAudioSessionId();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mp == currentMediaPlayer && nextMediaPlayer != null) {
            isInitialized = false;
            currentMediaPlayer.release();
            currentMediaPlayer = nextMediaPlayer;
            isInitialized = true;
            nextMediaPlayer = null;
            if (callbacks != null)
                callbacks.onTrackWentToNext();
        } else {
            if (callbacks != null)
                callbacks.onTrackEnded();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        isInitialized = false;
        currentMediaPlayer.release();
        currentMediaPlayer = new MediaPlayer();
        currentMediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        if (context != null) {
            Toast.makeText(context, "can't play this song", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
