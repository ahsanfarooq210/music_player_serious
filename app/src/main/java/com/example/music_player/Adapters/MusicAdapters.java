package com.example.music_player.Adapters;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.music_player.Activities.PlayerActivity;
import com.example.music_player.Entity.MusicFiles;
import com.example.music_player.R;

import java.sql.PreparedStatement;
import java.util.ArrayList;

public class MusicAdapters extends RecyclerView.Adapter<MusicAdapters.ViewHolder>
{
    private Context context;
    private ArrayList<MusicFiles> mfiles;

    public MusicAdapters(Context context, ArrayList<MusicFiles> mfiles)
    {
        this.context = context;
        this.mfiles = mfiles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view=LayoutInflater.from(context).inflate(R.layout.music_items,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.fileName.setText(mfiles.get(position).getTitle());
        byte[] image=getAlbumArt(mfiles.get(position).getPath());
        if(image!=null)
        {
            Glide.with(context).asBitmap().load(image).into(holder.albumArt);
        }
        else
        {
           Glide.with(context).load(R.drawable.icons8_music_200px).into(holder.albumArt);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(context, PlayerActivity.class);
                intent.putExtra("position",position);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount()
    {
        return mfiles.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView albumArt ;
        private TextView fileName;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            albumArt=itemView.findViewById(R.id.music_img);
            fileName=itemView.findViewById(R.id.music_file_name);
        }
    }
    private byte[] getAlbumArt(String uri)
    {
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art=retriever.getEmbeddedPicture();
        retriever.release();
        return  art;
    }
}
