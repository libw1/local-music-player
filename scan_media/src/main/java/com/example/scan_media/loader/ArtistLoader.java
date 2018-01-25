package com.example.scan_media.loader;

import android.content.Context;
import android.provider.MediaStore.Audio.AudioColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;



import java.util.ArrayList;

import model.Album;
import model.Artist;
import model.Song;
import rx.Observable;
import rx.Subscriber;
import util.PreferenceUtil;


public class ArtistLoader {
    public static String getSongLoaderSortOrder(Context context) {
        return PreferenceUtil.getInstance(context).getArtistSortOrder() + ", " + PreferenceUtil.getInstance(context).getArtistAlbumSortOrder() + ", " + PreferenceUtil.getInstance(context).getAlbumSongSortOrder();
    }

    @NonNull
    public static Observable<ArrayList<Artist>> getAllArtists(@NonNull final Context context) {
        return Observable.create(new Observable.OnSubscribe<ArrayList<Artist>>() {
            @Override
            public void call(Subscriber<? super ArrayList<Artist>> subscriber) {
                ArrayList<Song> songs = SongLoader.getSongs(SongLoader.makeSongCursor(
                        context,
                        null,
                        null,
                        getSongLoaderSortOrder(context))
                );
                subscriber.onNext(splitIntoArtists(AlbumLoader.splitIntoAlbums(songs)));
                subscriber.onCompleted();
            }
        });
    }

    @NonNull
    public static Observable<ArrayList<Artist>> getArtists(@NonNull final Context context, final String query) {
        return Observable.create(new Observable.OnSubscribe<ArrayList<Artist>>() {
            @Override
            public void call(Subscriber<? super ArrayList<Artist>> subscriber) {
                ArrayList<Song> songs = SongLoader.getSongs(SongLoader.makeSongCursor(
                        context,
                        AudioColumns.ARTIST + " LIKE ?",
                        new String[]{"%" + query + "%"},
                        getSongLoaderSortOrder(context))
                );
                subscriber.onNext(splitIntoArtists(AlbumLoader.splitIntoAlbums(songs)));
                subscriber.onCompleted();
            }
        });
    }

    @NonNull
    public static Observable<Artist> getArtist(@NonNull final Context context, final int artistId) {
        return Observable.create(new Observable.OnSubscribe<Artist>() {
            @Override
            public void call(Subscriber<? super Artist> subscriber) {
                ArrayList<Song> songs = SongLoader.getSongs(SongLoader.makeSongCursor(
                        context,
                        AudioColumns.ARTIST_ID + "=?",
                        new String[]{String.valueOf(artistId)},
                        getSongLoaderSortOrder(context))
                );
                subscriber.onNext(new Artist(AlbumLoader.splitIntoAlbums(songs)));
                subscriber.onCompleted();
            }
        });
    }

    @NonNull
    public static ArrayList<Artist> splitIntoArtists(@Nullable final ArrayList<Album> albums) {
        ArrayList<Artist> artists = new ArrayList<>();
        if (albums != null) {
            for (Album album : albums) {
                getOrCreateArtist(artists, album.getArtistId()).albums.add(album);
            }
        }
        return artists;
    }

    private static Artist getOrCreateArtist(ArrayList<Artist> artists, int artistId) {
        for (Artist artist : artists) {
            if (!artist.albums.isEmpty() && !artist.albums.get(0).songs.isEmpty() && artist.albums.get(0).songs.get(0).artistId == artistId) {
                return artist;
            }
        }
        Artist album = new Artist();
        artists.add(album);
        return album;
    }
}
