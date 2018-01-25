package model.smartplaylist;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;


import com.example.scan_media.loader.SongLoader;

import java.util.ArrayList;

import model.Song;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ShuffleAllPlaylist extends AbsSmartPlaylist {

//    public ShuffleAllPlaylist(@NonNull Context context) {
//        super(context.getString(R.string.action_shuffle_all), R.drawable.ic_shuffle_white_24dp);
//    }

    @NonNull
    @Override
    public ArrayList<Song> getSongs(@NonNull Context context) {
        final ArrayList<Song>[] songs = new ArrayList[]{new ArrayList<>()};
        SongLoader.getAllSongs(context).observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<Song>>() {
                    @Override
                    public void call(ArrayList<Song> ss) {
                        songs[0] = ss;
                    }
                });
        return songs[0];

    }

    @Override
    public void clear(@NonNull Context context) {
        // Shuffle all is not a real "Smart Playlist"
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    protected ShuffleAllPlaylist(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<ShuffleAllPlaylist> CREATOR = new Parcelable.Creator<ShuffleAllPlaylist>() {
        public ShuffleAllPlaylist createFromParcel(Parcel source) {
            return new ShuffleAllPlaylist(source);
        }

        public ShuffleAllPlaylist[] newArray(int size) {
            return new ShuffleAllPlaylist[size];
        }
    };
}
