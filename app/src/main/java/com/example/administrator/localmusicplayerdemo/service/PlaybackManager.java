package com.example.administrator.localmusicplayerdemo.service;

import android.content.ContentUris;
import android.content.Context;
import android.media.session.MediaSession;
import android.net.Uri;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.example.administrator.localmusicplayerdemo.LocalMusicPlayer;
import com.example.administrator.localmusicplayerdemo.Song;

import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2018-01-23.
 */

public class PlaybackManager implements Playback.PlaybackCallbacks{

    private Playback playback;
    private Context context;
    private List<Song> currentSongsQueue;
    private int current = -1;
    private static final int ORDER = 0;
    private static final int SINGLE = 1;
    private static final int RANDOM = 2;
    private int playMode = ORDER;
    private static final long MEDIA_SESSION_ACTIONS = PlaybackStateCompat.ACTION_PLAY
            | PlaybackStateCompat.ACTION_PAUSE
            | PlaybackStateCompat.ACTION_PLAY_PAUSE
            | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            | PlaybackStateCompat.ACTION_STOP
            | PlaybackStateCompat.ACTION_SEEK_TO;

    public PlaybackManager(Context context){
        this.context = context;
        playback = new LocalMusicPlayer(context);
        playback.setCallbacks(this);
    }


    public void playPrevious() {
        checkPreviousSong();
        play();
    }


    public void playNext() {
        checkNextSong();
        play();
    }

    @Override
    public void onTrackWentToNext() {

    }

    @Override
    public void onTrackEnded() {

    }

    public void play(){
        if (playback != null){
            if (getCurrentSong() != null) {
                playback.setDataSource(getCurrentSong().data);
                playback.start();
//                updateMetaData(getCurrentSong());
                playback.setNextDataSource(currentSongsQueue.get(current + 1).data);
            }
        }
    }

    public Song getCurrentSong() {
        return currentSongsQueue.get(current);
    }

    public void start(){
        if (playback != null){
            playback.start();
//            updatePlaybackState();
        }
    }

    public void pause(){
        if (playback != null){
            playback.pause();
//            updatePlaybackState();
        }
    }

    public void seekTo(int time){
        if (playback != null){
            playback.seek(time);
//            updatePlaybackState();
        }

    }

    public void stop(){
        if (playback != null){
            playback.stop();
        }
    }

/*    public void updatePlaybackState() {
        int state = (playback.isPlaying()) ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;
        mediaSession.setPlaybackState(
                new PlaybackStateCompat.Builder()
                        .setActions(MEDIA_SESSION_ACTIONS)
                        .setState(state, playback.position(), 1)
                        .build());
    }

    public void updateMetaData(Song song) {
        if (song == null) {
            mediaSession.setMetadata(null);
            return;
        }
        MediaMetadataCompat.Builder metaData = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.artistName)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.albumName)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, song.artistName)
                .putString(MediaMetadataCompat.METADATA_KEY_ART_URI,song.data)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.duration)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, getUriForImage(song));

        mediaSession.setMetadata(metaData.build());
    }*/

    private String getUriForImage(Song song){
        Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");
        return ContentUris.withAppendedId(sArtworkUri, song.albumId).toString();
    }

    public void playsong(Song song){
        currentSongsQueue.add(song);
    }

    public void playSongs(List<Song> songs, int index){
        currentSongsQueue = songs;
        current = index;
        play();
    }

    public int getPosition(){
        return playback.position();
    }

    public int getDuration(){
        return playback.duration();
    }

    public void onRelease(){
        playback.release();
    }

    public boolean isInitialized(){
        return playback.isInitialized();
    }

    public boolean isPlaying(){
        return playback.isPlaying();
    }

    public MediaSessionCompat.Callback getCallBack() {
        return new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                play();
            }

            @Override
            public void onPause() {
                pause();
            }

            @Override
            public void onSkipToNext() {
                playNext();
            }

            @Override
            public void onSkipToPrevious() {
                playPrevious();
            }

            @Override
            public void onStop() {
                stop();
            }

            @Override
            public void onSeekTo(long pos) {
                seekTo((int) pos);
            }
        };
    }

    private void checkPreviousSong() {
        if (playMode == ORDER){
            if (current != 0){
                current = current - 1;
            } else {
                current = currentSongsQueue.size() - 1;
            }
        }else if (playMode == RANDOM){
            getRandomSong();
        }
    }

    private void checkNextSong() {
        if (playMode == ORDER){
            if (current != currentSongsQueue.size() - 1){
                current = 0;
            } else {
                current = current + 1;
            }
        }else if (playMode == RANDOM){
            getRandomSong();
        }
    }

    public void getRandomSong() {
        Random random = new Random();
        int randNumber = random.nextInt(currentSongsQueue.size());
        current = randNumber;
    }
}
