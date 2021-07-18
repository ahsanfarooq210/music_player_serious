package com.example.music_player.Adapters;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.music_player.Activities.AlbumDetails;
import com.example.music_player.Entity.MusicFiles;
import com.example.music_player.R;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder>
{
    private Context mcontext;
    private ArrayList <MusicFiles> albumFiles;
    View view;

    public AlbumAdapter(Context mcontext, ArrayList<MusicFiles> albumFiles)
    {
        this.mcontext = mcontext;
        this.albumFiles = albumFiles;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType)
    {
        view=LayoutInflater.from(mcontext).inflate(R.layout.album_item, parent ,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AlbumAdapter.ViewHolder holder, int position)
    {
        holder.albumName.setText(albumFiles.get(position).getAlbum());
        byte[] image = null;
        try
        {
            image = getAlbumArt(albumFiles.get(position).getPath());
        }
        catch (Exception ignore)
        {

        }
        if (image != null)
        {
            Glide.with(mcontext).asBitmap().load(image).into(holder.albumImage);
        } else
        {
            Glide.with(mcontext).load(R.drawable.icons8_music_200px).into(holder.albumImage);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(mcontext, AlbumDetails.class);
                intent.putExtra("albumName",albumFiles.get(position).getAlbum());
                mcontext.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount()
    {
        return albumFiles.size();
    }

    private byte[] getAlbumArt(String uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView albumImage;
        private TextView albumName;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            albumImage=itemView.findViewById(R.id.album_image);
            albumName=itemView.findViewById(R.id.album_name);
        }
    }
}
