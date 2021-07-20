package com.example.music_player.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.music_player.Adapters.MusicAdapters;
import com.example.music_player.Entity.MusicFiles;
import com.example.music_player.HelperClasses.SongUtility;
import com.example.music_player.R;

import java.util.ArrayList;


public class SongsFragment extends Fragment
{
    private RecyclerView recyclerView;
    public static MusicAdapters musicAdapters;



    public SongsFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_songs, container, false);
        recyclerView=v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        musicAdapters=new MusicAdapters(getContext(), (ArrayList<MusicFiles>) SongUtility.getMusicFilesList(getContext()));
        recyclerView.setAdapter(musicAdapters);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));


        return v;
    }
}