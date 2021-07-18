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
import com.example.music_player.Activities.PlayerActivity;
import com.example.music_player.Entity.MusicFiles;
import com.example.music_player.R;

import java.util.ArrayList;

public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.ViewHolder>
{
    private Context mcontext;
    public static ArrayList <MusicFiles> albumFiles;
    View view;

    public AlbumDetailsAdapter(Context mcontext, ArrayList<MusicFiles> albumFiles)
    {
        this.mcontext = mcontext;
        this.albumFiles = albumFiles;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType)
    {
        view=LayoutInflater.from(mcontext).inflate(R.layout.music_items, parent ,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AlbumDetailsAdapter.ViewHolder holder, int position)
    {
        holder.albumName.setText(albumFiles.get(position).getTitle() );
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
                Intent intent=new Intent(mcontext, PlayerActivity.class);
                intent.putExtra("sender","albumDetails");
                intent.putExtra("position",position);
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
            albumImage=itemView.findViewById(R.id.music_img);
            albumName=itemView.findViewById(R.id.music_file_name);
        }
    }
}
