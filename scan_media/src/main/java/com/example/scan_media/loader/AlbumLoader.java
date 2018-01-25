package com.example.scan_media.loader;

import android.content.Context;
import android.provider.MediaStore.Audio.AudioColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;



import java.util.ArrayList;

import model.Album;
import model.Song;
import rx.Observable;
import rx.Subscriber;
import util.PreferenceUtil;



public class AlbumLoader {

    public static String getSongLoaderSortOrder(Context context) {
        return PreferenceUtil.getInstance(context).getAlbumSortOrder() + ", " + PreferenceUtil.getInstance(context).getAlbumSongSortOrder();
    }

    @NonNull
    public static Observable<ArrayList<Album>> getAllAlbums(@NonNull final Context context) {
        return Observable.create(new Observable.OnSubscribe<ArrayList<Album>>() {
            @Override
            public void call(Subscriber<? super ArrayList<Album>> subscriber) {
                ArrayList<Song> songs = SongLoader.getSongs(SongLoader.makeSongCursor(
                        context,
                        null,
                        null,
                        getSongLoaderSortOrder(context))
                );
                subscriber.onNext(splitIntoAlbums(songs));
                subscriber.onCompleted();
            }
        });
    }

    @NonNull
    public static Observable<ArrayList<Album>> getAlbums(@NonNull final Context context, final String query) {
        return Observable.create(new Observable.OnSubscribe<ArrayList<Album>>() {
            @Override
            public void call(Subscriber<? super ArrayList<Album>> subscriber) {
                ArrayList<Song> songs = SongLoader.getSongs(SongLoader.makeSongCursor(
                        context,
                        AudioColumns.ALBUM + " LIKE ?",
                        new String[]{"%" + query + "%"},
                        getSongLoaderSortOrder(context))
                );
                subscriber.onNext(splitIntoAlbums(songs));
                subscriber.onCompleted();
            }
        });
     }

    @NonNull
    public static Observable<Album> getAlbum(@NonNull final Context context, final int albumId) {
        return Observable.create(new Observable.OnSubscribe<Album>() {
            @Override
            public void call(Subscriber<? super Album> subscriber) {

                ArrayList<Song> songs = SongLoader.
                        getSongs(SongLoader.makeSongCursor(
                                context,
                                AudioColumns.ALBUM_ID + "=?",
                                new String[]{String.valueOf(albumId)},
                                getSongLoaderSortOrder(context)));
                subscriber.onNext(new Album(songs));
                subscriber.onCompleted();
            }
        });
    }

    @NonNull
    public static ArrayList<Album> splitIntoAlbums(@Nullable final ArrayList<Song> songs) {
        ArrayList<Album> albums = new ArrayList<>();
        if (songs != null) {
            for (Song song : songs) {
                getOrCreateAlbum(albums, song.albumId).songs.add(song);
            }
        }
        return albums;
    }

    private static Album getOrCreateAlbum(ArrayList<Album> albums, int albumId) {
        for (Album album : albums) {
            if (!album.songs.isEmpty() && album.songs.get(0).albumId == albumId) {
                return album;
            }
        }
        Album album = new Album();
        albums.add(album);
        return album;
    }
}
