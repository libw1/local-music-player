package com.example.administrator.localmusicplayerdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2018-01-22.
 */

public class SongAdapter extends RecyclerView.Adapter {

    private List<Song> songs;
    private ItemClickListener itemClickListener;
    private LayoutInflater inflater;

    public SongAdapter(Context context,List<Song> songs){
        inflater = LayoutInflater.from(context);
        this.songs = songs;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_song,parent));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;
        vh.name.setText(songs.get(position).title);
        vh.title.setText(songs.get(position).artistName + " " + songs.get(position).title + "-" + songs.get(position).duration);

    }

    @Override
    public int getItemCount() {
        return songs.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView title;

        public ViewHolder(final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.song_name);
            title = itemView.findViewById(R.id.song_title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null){
                        itemClickListener.onItemClick(v,getPosition());
                    }
                }
            });
        }
    }

    public void setItemClickListener(ItemClickListener listener){
        itemClickListener = listener;
    }
}
