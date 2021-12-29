package com.jcodeNikhil.jmusic;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.io.File;
import java.util.ArrayList;

public class MyMusicRVAdapter extends RecyclerView.Adapter<MyMusicRVAdapter.MusicViewHolder> {

    // Member variable to handle the clicks
    final private ItemClickListener mItemClickListener;
    private final ArrayList<File> myMusicFiles;
    Context context;
    private MediaMetadataRetriever retriever;
//    ArrayList<MyMusicData> myMusicArrayList;

    //    ArrayList<File> myMusicFiles;
    public MyMusicRVAdapter(ItemClickListener mItemClickListener, Context context,/* ArrayList<MyMusicData> myMusicArrayList,*/ ArrayList<File> myMusicFiles) {
        this.mItemClickListener = mItemClickListener;
        this.context = context;
//        this.myMusicArrayList = myMusicArrayList;
        this.myMusicFiles = myMusicFiles;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mymusic_view_item, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        //populating data in listItems or here called viewItems
//        MyMusicData myMusicData = myMusicArrayList.get(position);

        retriever = new MediaMetadataRetriever();
        retriever = new MediaMetadataRetriever();
        String path = myMusicFiles.get(position).getPath();
        retriever.setDataSource(path);

        // load album art
        byte[] image = retriever.getEmbeddedPicture();
        if (image != null){
            Glide.with(context).asBitmap()
                    .load(image)
                    .transform(new RoundedCorners(10))
                    .into(holder.mAlbumArt);
        } else {
            Glide.with(context)
                    .load(R.drawable.splash)
                    .transform(new RoundedCorners(10))
                    .into(holder.mAlbumArt);
        }

//        String temp = position + 1 + ". " + myMusicFiles.get(position).getName().replace(".mp3", "");
//        holder.mAudioTitle.setText(myMusicFiles.get(position).getName().replace(".mp3", ""));

        String title,artist,genre;
        try {
            title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            if (title != null) holder.mAudioTitle.setText(title);
            else holder.mAudioTitle.setText(myMusicFiles.get(position).getName().replace(".mp3", ""));

            artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            if (artist != null) holder.mArtist.setText(artist);
            else holder.mArtist.setText("Unknown Artist");

            genre = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
            if (genre != null){
                holder.mArtist.append(" | "+genre);
            }
        } catch (Exception e) {
            holder.mAudioTitle.setText(myMusicFiles.get(position).getName().replace(".mp3", ""));
            holder.mArtist.setText("Unknown Artist | Unknown Album");
        }
        retriever.release();
    }

    @Override
    public int getItemCount() {
        return myMusicFiles.size();
    }

    /*public static byte[] getAlbumArt(String path){
        retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }*/

    public interface ItemClickListener {
        void onItemCLickListener(int pos);
    }

    public class MusicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mAudioTitle;
        ImageView mAlbumArt;
        TextView mArtist;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mAudioTitle = itemView.findViewById(R.id.myMusic_title);
            mAlbumArt = itemView.findViewById(R.id.albumArt);
            mArtist = itemView.findViewById(R.id.artist);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            mItemClickListener.onItemCLickListener(pos);
        }
    }
}
