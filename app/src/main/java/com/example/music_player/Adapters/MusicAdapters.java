package com.example.music_player.Adapters;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.music_player.Activities.PlayerActivity;
import com.example.music_player.Entity.MusicFiles;
import com.example.music_player.Interfaces.ShowHideNowPlayingFragment;
import com.example.music_player.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class MusicAdapters extends RecyclerView.Adapter<MusicAdapters.ViewHolder>
{
    private Context context;
    public static ArrayList<MusicFiles> mfiles;

    public MusicAdapters(Context context, ArrayList<MusicFiles> mfiles)
    {
        this.context = context;
        this.mfiles = mfiles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.music_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position)
    {
        holder.fileName.setText(mfiles.get(position).getTitle());
        byte[] image = null;
        try
        {
            image = getAlbumArt(mfiles.get(position).getPath());
        }
        catch (Exception ignore)
        {
            
        }
        if (image != null)
        {
            Glide.with(context).asBitmap().load(image).into(holder.albumArt);
        } else
        {
            Glide.with(context).load(R.drawable.icons8_music_128px).into(holder.albumArt);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, PlayerActivity.class);
                intent.putExtra("position", position);
                context.startActivity(intent);
                ShowHideNowPlayingFragment showHideNowPlayingFragment = (ShowHideNowPlayingFragment) context;
                showHideNowPlayingFragment.show();
            }
        });
        holder.menuMore.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        switch (item.getItemId())
                        {
                            case R.id.delete:
                                Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
                                deleteFile(position, v);
                                break;

                        }
                        return true;
                    }
                });
            }
        });

    }

    private void deleteFile(int position, View v)
    {
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(mfiles.get(position).getId()));
        File file = new File(mfiles.get(position).getPath());
        boolean deleted = file.delete();//deleted your file
        if (deleted)
        {
            context.getContentResolver().delete(contentUri, null, null);
            mfiles.remove(position);
            notifyItemRemoved(position);
            notifyItemChanged(position, mfiles.size());
            Snackbar.make(v, "File Deleted", Snackbar.LENGTH_SHORT).show();
        } else
        {
            Snackbar.make(v, "File can't be Deleted", Snackbar.LENGTH_SHORT).show();
        }


    }

    @Override
    public int getItemCount()
    {
        return mfiles.size();
    }

    private byte[] getAlbumArt(String uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
    public void updateList(ArrayList<MusicFiles> musicFilesArrayList)
    {
        mfiles=new ArrayList<>();
        mfiles.addAll(musicFilesArrayList);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView albumArt, menuMore;
        private TextView fileName;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            albumArt = itemView.findViewById(R.id.music_img);
            fileName = itemView.findViewById(R.id.music_file_name);
            menuMore = itemView.findViewById(R.id.menu_more);
        }
    }

}
