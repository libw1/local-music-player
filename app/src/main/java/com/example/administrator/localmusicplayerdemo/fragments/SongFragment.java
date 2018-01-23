package com.example.administrator.localmusicplayerdemo.fragments;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.localmusicplayerdemo.Actions;
import com.example.administrator.localmusicplayerdemo.R;
import com.example.administrator.localmusicplayerdemo.Song;
import com.example.administrator.localmusicplayerdemo.loaders.SongLoader;
import com.example.administrator.localmusicplayerdemo.service.MusicService;
import com.example.administrator.localmusicplayerdemo.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018-01-22.
 */

public class SongFragment extends Fragment {

    private List<Song> songs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_song,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = view.findViewById(R.id.list);
        songs = SongLoader.getAllSongs(getContext());
        MyAdapter adapter = new MyAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), MusicService.class);
                intent.setAction(Actions.action_play_song);
                intent.putParcelableArrayListExtra("songs", (ArrayList<? extends Parcelable>) songs);
                intent.putExtra("index",position);
                getActivity().startService(intent);
            }
        });
    }


    class MyAdapter extends BaseAdapter{


        @Override
        public int getCount() {
            return songs.size();
        }

        @Override
        public Object getItem(int position) {
            return songs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null){
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_song,null);
                viewHolder.name = convertView.findViewById(R.id.song_name);
                viewHolder.title = convertView.findViewById(R.id.song_title);
                viewHolder.image = convertView.findViewById(R.id.song_image);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.name.setText(songs.get(position).title);
            viewHolder.title.setText(songs.get(position).artistName + " " + songs.get(position).title + "-" + TimeUtils.formatTime(songs.get(position).duration));
            Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");
            viewHolder.image.setImageURI(ContentUris.withAppendedId(sArtworkUri, songs.get(position).albumId));
            return convertView;
        }

        class ViewHolder{
            private TextView name;
            private TextView title;
            private ImageView image;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
