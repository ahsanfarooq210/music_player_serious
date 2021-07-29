package com.example.music_player.Activities;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.music_player.Adapters.AlbumDetailsAdapter;
import com.example.music_player.Entity.MusicFiles;
import com.example.music_player.HelperClasses.SongUtility;
import com.example.music_player.R;

import java.util.ArrayList;

public class AlbumDetails extends AppCompatActivity
{
    private RecyclerView recyclerView;
    private ImageView albumPhoto;
    private String albumName;
    private ArrayList<MusicFiles> albumSongs;
    private AlbumDetailsAdapter albumDetailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);
        recyclerView = findViewById(R.id.album_recyclerView);
        albumPhoto = findViewById(R.id.album_photo);
        albumName = getIntent().getStringExtra("albumName");
        albumSongs = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < SongUtility.getMusicFilesList(this).size(); i++)
        {
            if (albumName.equals(SongUtility.getMusicFilesList(this).get(i).getAlbum()))
            {
                albumSongs.add(j, SongUtility.getMusicFilesList(this).get(i));
                j++;
            }
        }
        byte[] image=null;
        try
        {
            image = getAlbumArt(albumSongs.get(0).getPath());
        }
        catch (Exception ignored)
        {

        }
        if (image != null)
        {
            Glide.with(this).load(image).into(albumPhoto);
        } else
        {
            Glide.with(this).load(R.drawable.icons8_music_128px).into(albumPhoto);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (!(albumSongs.size() < 1))
        {
            albumDetailsAdapter = new AlbumDetailsAdapter(this, albumSongs);
            recyclerView.setAdapter(albumDetailsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        }
    }

    private byte[] getAlbumArt(String uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}