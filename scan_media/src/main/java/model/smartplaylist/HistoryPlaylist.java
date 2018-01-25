package model.smartplaylist;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;


import com.example.scan_media.loader.TopAndRecentlyPlayedTracksLoader;

import java.util.ArrayList;

import model.Song;
import provider.HistoryStore;


public class HistoryPlaylist extends AbsSmartPlaylist {

//    public HistoryPlaylist(@NonNull Context context) {
//        super(context.getString(R.string.history), R.drawable.ic_access_time_white_24dp);
//    }

    @NonNull
    @Override
    public ArrayList<Song> getSongs(@NonNull Context context) {
        return TopAndRecentlyPlayedTracksLoader.getRecentlyPlayedTracks(context);
    }

    @Override
    public void clear(@NonNull Context context) {
        HistoryStore.getInstance(context).clear();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    protected HistoryPlaylist(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<HistoryPlaylist> CREATOR = new Parcelable.Creator<HistoryPlaylist>() {
        public HistoryPlaylist createFromParcel(Parcel source) {
            return new HistoryPlaylist(source);
        }

        public HistoryPlaylist[] newArray(int size) {
            return new HistoryPlaylist[size];
        }
    };
}
