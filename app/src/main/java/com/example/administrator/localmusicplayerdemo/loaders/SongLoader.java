package com.example.administrator.localmusicplayerdemo.loaders;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.administrator.localmusicplayerdemo.BlacklistStore;
import com.example.administrator.localmusicplayerdemo.Song;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by Administrator on 2018-01-22.
 */

public class SongLoader {
    private static final String TAG = "SongLoader";
    protected static final String BASE_SELECTION = MediaStore.Audio.AudioColumns.IS_MUSIC + "=1" + " AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''";
    protected static final String[] BASE_PROJECTION = new String[]{
            BaseColumns._ID,// 0
            MediaStore.Audio.AudioColumns.TITLE,// 1
            MediaStore.Audio.AudioColumns.TRACK,// 2
            MediaStore.Audio.AudioColumns.YEAR,// 3
            MediaStore.Audio.AudioColumns.DURATION,// 4
            MediaStore.Audio.AudioColumns.DATA,// 5
            MediaStore.Audio.AudioColumns.DATE_MODIFIED,// 6
            MediaStore.Audio.AudioColumns.ALBUM_ID,// 7
            MediaStore.Audio.AudioColumns.ALBUM,// 8
            MediaStore.Audio.AudioColumns.ARTIST_ID,// 9
            MediaStore.Audio.AudioColumns.ARTIST,// 10
    };

    @NonNull
    public static Observable<ArrayList<Song>> getAllSongs(@NonNull final Context context) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<Song>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<Song>> emitter) throws Exception {
                Cursor cursor = makeSongCursor(context, null, null);
                emitter.onNext(getSongs(cursor));
                emitter.onComplete();
            }
        });
    }

    @NonNull
    public static Observable<ArrayList<Song>> getSongs(@NonNull final Context context, final String query) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<Song>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<Song>> emitter) throws Exception {
                Cursor cursor = makeSongCursor(context, MediaStore.Audio.AudioColumns.TITLE + " LIKE ?", new String[]{"%" + query + "%"});
                emitter.onNext(getSongs(cursor));
            }
        });
    }

    @NonNull
    public static Observable<Song> getSong(@NonNull final Context context, final int queryId) {
        return Observable.create(new ObservableOnSubscribe<Song>() {
            @Override
            public void subscribe(ObservableEmitter<Song> emitter) throws Exception {
                Cursor cursor = makeSongCursor(context, MediaStore.Audio.AudioColumns._ID + "=?", new String[]{String.valueOf(queryId)});
                emitter.onNext(getSong(cursor));
            }
        });
    }

    @NonNull
    public static ArrayList<Song> getSongs(@Nullable final Cursor cursor) {
        ArrayList<Song> songs = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                songs.add(getSongFromCursorImpl(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null)
            cursor.close();
        return songs;
    }

    @NonNull
    public static Song getSong(@Nullable Cursor cursor) {
        Song song;
        if (cursor != null && cursor.moveToFirst()) {
            song = getSongFromCursorImpl(cursor);
        } else {
            song = Song.EMPTY_SONG;
        }
        if (cursor != null) {
            cursor.close();
        }
        return song;
    }

    @NonNull
    private static Song getSongFromCursorImpl(@NonNull Cursor cursor) {
        final int id = cursor.getInt(0);
        final String title = cursor.getString(1);
        final int trackNumber = cursor.getInt(2);
        final int year = cursor.getInt(3);
        final long duration = cursor.getLong(4);
        final String data = cursor.getString(5);
        final long dateModified = cursor.getLong(6);
        final int albumId = cursor.getInt(7);
        final String albumName = cursor.getString(8);
        final int artistId = cursor.getInt(9);
        final String artistName = cursor.getString(10);

        return new Song(id, title, trackNumber, year, duration, data, dateModified, albumId, albumName, artistId, artistName);
    }

    @Nullable
    public static Cursor makeSongCursor(@NonNull final Context context, @Nullable final String selection, final String[] selectionValues) {
        return makeSongCursor(context, selection, selectionValues, null);
    }

    @Nullable
    public static Cursor makeSongCursor(@NonNull final Context context, @Nullable String selection, String[] selectionValues, final String sortOrder) {
        Log.d(TAG, "makeSongCursor: " + Thread.currentThread().getName());
        if (selection != null && !selection.trim().equals("")) {
            selection = BASE_SELECTION + " AND " + selection;
        } else {
            selection = BASE_SELECTION;
        }

        // Blacklist
        ArrayList<String> paths = BlacklistStore.getInstance(context).getPaths();
        if (!paths.isEmpty()) {
            selection = generateBlacklistSelection(selection, paths.size());
            selectionValues = addBlacklistSelectionValues(selectionValues, paths);
        }

        try {
            return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    BASE_PROJECTION, selection, selectionValues, sortOrder);
        } catch (SecurityException e) {
            return null;
        }
    }

    private static String generateBlacklistSelection(String selection, int pathCount) {
        Log.d(TAG, "generateBlacklistSelection: " + Thread.currentThread().getName());
        String newSelection = selection != null && !selection.trim().equals("") ? selection + " AND " : "";
        newSelection += MediaStore.Audio.AudioColumns.DATA + " NOT LIKE ?";
        for (int i = 0; i < pathCount - 1; i++) {
            newSelection += " AND " + MediaStore.Audio.AudioColumns.DATA + " NOT LIKE ?";
        }
        return newSelection;
    }

    private static String[] addBlacklistSelectionValues(String[] selectionValues, ArrayList<String> paths) {
        Log.d(TAG, "addBlacklistSelectionValues: " + Thread.currentThread().getName());
        if (selectionValues == null) selectionValues = new String[0];
        String[] newSelectionValues = new String[selectionValues.length + paths.size()];
        System.arraycopy(selectionValues, 0, newSelectionValues, 0, selectionValues.length);
        for (int i = selectionValues.length; i < newSelectionValues.length; i++) {
            newSelectionValues[i] = paths.get(i - selectionValues.length) + "%";
        }
        return newSelectionValues;
    }
}
