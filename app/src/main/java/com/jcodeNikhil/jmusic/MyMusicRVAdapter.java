package com.jcodeNikhil.jmusic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyMusicRVAdapter extends RecyclerView.Adapter<MyMusicRVAdapter.MusicViewHolder>{

    // Member variable to handle the clicks
    final private ItemClickListener mItemClickListener;
    Context context;
    ArrayList<MyMusicData> myMusicArrayList;
    //    ArrayList<File> myMusicFiles;
    public MyMusicRVAdapter(ItemClickListener mItemClickListener, Context context, ArrayList<MyMusicData> myMusicArrayList/*, ArrayList<File> myMusicFiles*/) {
        this.mItemClickListener = mItemClickListener;
        this.context = context;
        this.myMusicArrayList = myMusicArrayList;
//        this.myMusicFiles = myMusicFiles;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mymusic_view_item,parent,false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        //populating data in listItems or here called viewItems
        MyMusicData myMusicData = myMusicArrayList.get(position);
        holder.tvAudioFileName.setText(myMusicData.getFileName());
    }

    @Override
    public int getItemCount() {
        return myMusicArrayList.size();
    }

    public interface ItemClickListener {
        void onItemCLickListener(int pos);
    }

    public class MusicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvAudioFileName;
        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvAudioFileName = itemView.findViewById(R.id.mymusic_audio_file_name);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            mItemClickListener.onItemCLickListener(pos);
        }
    }
}
