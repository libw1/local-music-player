package com.example.administrator.localmusicplayerdemo.loaders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.administrator.localmusicplayerdemo.Album;
import com.example.administrator.localmusicplayerdemo.Song;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by Administrator on 2018-01-23.
 */

public class AlbumsLoader {

    @NonNull
    public static Observable<List<Album>> getAllAlbums(@NonNull Context context){

        List<Song> songs = SongLoader.getAllSongs(context);
        return splitIntoAlbums(songs);
    }


    @NonNull
    public static Observable<List<Album>> splitIntoAlbums(@Nullable final List<Song> songs) {
        return Observable.create(new ObservableOnSubscribe<List<Album>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Album>> emitter) throws Exception {
                List<Album> albums = new ArrayList<>();
                if (songs != null) {
                    for (Song song : songs) {
                        getOrCreateAlbum(albums, song.albumId).songs.add(song);
                    }
                }
                emitter.onNext(albums);
                emitter.onComplete();
            }
        });
    }

    private static Album getOrCreateAlbum(List<Album> albums, int albumId) {
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
