package com.example.music_player.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.music_player.Adapters.AlbumAdapter;
import com.example.music_player.Adapters.MusicAdapters;
import com.example.music_player.Entity.MusicFiles;
import com.example.music_player.HelperClasses.SongUtility;
import com.example.music_player.R;

import java.util.ArrayList;


public class AlbumFragment extends Fragment
{
    private RecyclerView recyclerView;
    private AlbumAdapter albumAdapter;



    public AlbumFragment()
    {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_album, container, false);
        recyclerView=v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        albumAdapter=new AlbumAdapter( getContext(), (ArrayList<MusicFiles>) SongUtility.getMusicFilesList(getContext()));
        recyclerView.setAdapter(albumAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));


        return v;
    }
}