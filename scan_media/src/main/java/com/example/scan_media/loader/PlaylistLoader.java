package com.example.scan_media.loader;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.PlaylistsColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import java.util.ArrayList;

import model.Playlist;
import rx.Observable;
import rx.Subscriber;

public class PlaylistLoader {

    @NonNull
    public static Observable<ArrayList<Playlist>> getAllPlaylists(@NonNull final Context context) {
        return Observable.create(new Observable.OnSubscribe<ArrayList<Playlist>>() {
            @Override
            public void call(Subscriber<? super ArrayList<Playlist>> subscriber) {
                subscriber.onNext(getAllPlaylists(makePlaylistCursor(context, null, null)));
                subscriber.onCompleted();
            }
        });
    }

    @NonNull
    public static Observable<Playlist> getPlaylist(@NonNull final Context context, final int playlistId) {
        return Observable.create(new Observable.OnSubscribe<Playlist>() {
            @Override
            public void call(Subscriber<? super Playlist> subscriber) {
                subscriber.onNext(getPlaylist(makePlaylistCursor(
                        context,
                        BaseColumns._ID + "=?",
                        new String[]{
                                String.valueOf(playlistId)
                        }
                )));
                subscriber.onCompleted();
            }
        });
    }

    @NonNull
    public static Observable<Playlist> getPlaylist(@NonNull final Context context, final String playlistName) {
        return Observable.create(new Observable.OnSubscribe<Playlist>() {
            @Override
            public void call(Subscriber<? super Playlist> subscriber) {
                subscriber.onNext(getPlaylist(makePlaylistCursor(
                        context,
                        PlaylistsColumns.NAME + "=?",
                        new String[]{
                                playlistName
                        }
                )));
                subscriber.onCompleted();
            }
        });
    }

    @NonNull
    public static Playlist getPlaylist(@Nullable final Cursor cursor) {
        Playlist playlist = new Playlist();

        if (cursor != null && cursor.moveToFirst()) {
            playlist = getPlaylistFromCursorImpl(cursor);
        }
        if (cursor != null)
            cursor.close();
        return playlist;
    }

    @NonNull
    public static ArrayList<Playlist> getAllPlaylists(@Nullable final Cursor cursor) {
        ArrayList<Playlist> playlists = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                playlists.add(getPlaylistFromCursorImpl(cursor));
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
        return playlists;
    }

    @NonNull
    private static Playlist getPlaylistFromCursorImpl(@NonNull final Cursor cursor) {
        final int id = cursor.getInt(0);
        final String name = cursor.getString(1);
        return new Playlist(id, name);
    }

    @Nullable
    public static Cursor makePlaylistCursor(@NonNull final Context context, final String selection, final String[] values) {
        try {
            return context.getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                    new String[]{
                        /* 0 */
                            BaseColumns._ID,
                        /* 1 */
                            PlaylistsColumns.NAME
                    }, selection, values, MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER);
        } catch (SecurityException e) {
            return null;
        }
    }
}