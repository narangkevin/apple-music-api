package com.example.mtelapplemusicapi;

import android.content.Context;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {

    private Context mContext;
    private List<SongModel> mData;
    private ItemClickListener mItemListener;

    public SongAdapter(Context mContext, List<SongModel> mData, ItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.mData = mData;
        this.mItemListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        v = inflater.inflate(R.layout.item_layout, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.songTitle.setText(mData.get(position).getTrackName());
        holder.artistName.setText(mData.get(position).getArtistName());
        holder.albumName.setText(mData.get(position).getCollectionName());
        holder.previewUrl.setText(mData.get(position).getPreviewUrl());

        Glide.with(mContext)
                .load(mData.get(position).getArtworkUrl100())
                .into(holder.albumArt);

        holder.itemView.setOnClickListener(view -> {
            mItemListener.onItemClick(mData.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public interface ItemClickListener {
        void onItemClick(SongModel details);

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView songTitle;
        TextView artistName;
        TextView albumName;
        TextView previewUrl;
        ImageView albumArt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            songTitle = itemView.findViewById(R.id.song_title);
            artistName = itemView.findViewById(R.id.artist_name);
            albumName = itemView.findViewById(R.id.album_name);
            albumArt = itemView.findViewById(R.id.alumArt);
            previewUrl = itemView.findViewById(R.id.song_preview);
        }
    }
}
