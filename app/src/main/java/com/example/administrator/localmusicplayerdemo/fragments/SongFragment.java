package com.example.administrator.localmusicplayerdemo.fragments;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.administrator.localmusicplayerdemo.service.MusicService;
import com.example.administrator.localmusicplayerdemo.utils.TimeUtils;
import com.example.scan_media.loader.SongLoader;

import java.util.ArrayList;
import java.util.List;


import model.Song;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by Administrator on 2018-01-22.
 */

public class SongFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_song,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ListView listView = view.findViewById(R.id.list);
        SongLoader.getAllSongs(getContext()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<Song>>() {
                    @Override
                    public void call(final ArrayList<Song> songs) {
                        MyAdapter adapter = new MyAdapter(songs);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(getContext(), MusicService.class);
                                intent.setAction(Actions.action_play_song);
                                intent.putParcelableArrayListExtra("songs", songs);
                                intent.putExtra("index",position);
                                getActivity().startService(intent);
                            }
                        });
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                }, new Action0() {
                    @Override
                    public void call() {

                    }
                });

    }


    class MyAdapter extends BaseAdapter{

        private List<Song> songs;
        public MyAdapter(List<Song> songs){
            this.songs = songs;
        }
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
