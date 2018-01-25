package model.smartplaylist;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;


import com.example.scan_media.loader.LastAddedLoader;

import java.util.ArrayList;

import model.Song;


public class LastAddedPlaylist extends AbsSmartPlaylist {

//    public LastAddedPlaylist(@NonNull Context context) {
//        super(context.getString(R.string.last_added), R.drawable.ic_library_add_white_24dp);
//    }

    @NonNull
    @Override
    public ArrayList<Song> getSongs(@NonNull Context context) {
        return LastAddedLoader.getLastAddedSongs(context);
    }

    @Override
    public void clear(@NonNull Context context) {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    protected LastAddedPlaylist(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<LastAddedPlaylist> CREATOR = new Parcelable.Creator<LastAddedPlaylist>() {
        public LastAddedPlaylist createFromParcel(Parcel source) {
            return new LastAddedPlaylist(source);
        }

        public LastAddedPlaylist[] newArray(int size) {
            return new LastAddedPlaylist[size];
        }
    };
}
