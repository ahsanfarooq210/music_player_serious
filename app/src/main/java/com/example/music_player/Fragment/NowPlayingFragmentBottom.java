package com.example.music_player.Fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.music_player.Interfaces.ShowHideNowPlayingFragment;
import com.example.music_player.R;
import com.example.music_player.Services.MusicService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static android.content.Context.MODE_PRIVATE;
import static com.example.music_player.Activities.MainActivity.ARTIST_PATH_TO_FRAG;
import static com.example.music_player.Activities.MainActivity.PATH_TO_FRAG;
import static com.example.music_player.Activities.MainActivity.SHOW_MINI_PLAYER;
import static com.example.music_player.Activities.MainActivity.SONG_PATH_TO_FRAG;
import static com.example.music_player.Services.MusicService.MUSIC_FILE;
import static com.example.music_player.Services.MusicService.MUSIC_FILES_LASTT_PLAYED;


public class NowPlayingFragmentBottom extends Fragment implements ServiceConnection
{


    public static final String MUSIC_FILES_LASTT_PLAYED = "LAST_PLAYED";
    public static final String MUSIC_FILE = "STORED_MUSIC";
    public static final String ARTIST_NAME = "ARTIST NAME";
    public static final String SONG_NAME = "SONG NAME";
    private ImageView nextBtn, albumart;
    private TextView artist, songName;
    private FloatingActionButton playPauseBtn;
    private View view;
    private String path;
    private MusicService musicService;
 //   private FrameLayout frameLayout;


    public NowPlayingFragmentBottom()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_now_playing, container, false);

        artist = view.findViewById(R.id.song_artist_mini_player);
        songName = view.findViewById(R.id.song_name_mini_player);
        albumart = view.findViewById(R.id.bottom_album_art);
        nextBtn = view.findViewById(R.id.skip_next_botom);
        playPauseBtn = view.findViewById(R.id.play_pause_mini_player);
       // frameLayout= view.findViewById(R.id.now_playing_frame_layout);
       // frameLayout.setVisibility(View.GONE);

        nextBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getContext(), "Next", Toast.LENGTH_SHORT).show();
                if (musicService != null)
                {
                    musicService.nextBtnClicked();
                    if (getActivity() != null)
                    {
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences(MUSIC_FILES_LASTT_PLAYED, MODE_PRIVATE).edit();
                        editor.putString(MUSIC_FILE, musicService.musicFiles.get(musicService.position).getPath());
                        editor.putString(ARTIST_NAME, musicService.musicFiles.get(musicService.position).getArtist());
                        editor.putString(SONG_NAME, musicService.musicFiles.get(musicService.position).getTitle());
                        editor.apply();

                        SharedPreferences preferences=getActivity().getSharedPreferences(MUSIC_FILES_LASTT_PLAYED,MODE_PRIVATE);
                        String path=preferences.getString(MUSIC_FILE,null);
                        String artistName=preferences.getString(ARTIST_NAME,null);
                        String song_name=preferences.getString(SONG_NAME,null);

                        if(path!=null)
                        {
                            SHOW_MINI_PLAYER=true;
                            PATH_TO_FRAG=path;
                            ARTIST_PATH_TO_FRAG=artistName;
                            SONG_PATH_TO_FRAG=song_name;

                        }
                        else
                        {
                            SHOW_MINI_PLAYER=false;
                            PATH_TO_FRAG=null;
                            ARTIST_PATH_TO_FRAG=null;
                            SONG_PATH_TO_FRAG=null;

                        }

                        if (SHOW_MINI_PLAYER = true)
                        {
                            if (PATH_TO_FRAG != null)
                            {
                                byte[] art = null;
                                try
                                {
                                    art = getAlbumArt(PATH_TO_FRAG);
                                } catch (Exception ignored)
                                {
                                }
                                if (art != null)
                                {
                                    Glide.with(getContext()).load(art).into(albumart);
                                } else
                                {
                                    Glide.with(getContext()).load(R.drawable.icons8_music_128px).into(albumart);
                                }
                                songName.setText(SONG_PATH_TO_FRAG);
                                artist.setText(ARTIST_PATH_TO_FRAG);

                            }

                        }
                    }
                }
            }
        });

        playPauseBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getContext(), "play pause", Toast.LENGTH_SHORT).show();
                if (musicService != null)
                {
                    musicService.playPauseBtnClicked();
                    if (musicService.isPlaying())
                    {
                        playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24);
                    } else
                    {
                        playPauseBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (SHOW_MINI_PLAYER = true)
        {
            if (PATH_TO_FRAG != null)
            {
                byte[] art = null;
                try
                {
                    art = getAlbumArt(PATH_TO_FRAG);
                } catch (Exception ignored)
                {
                }
                if (art != null)
                {
                    Glide.with(getContext()).load(art).into(albumart);
                } else
                {
                    Glide.with(getContext()).load(R.drawable.icons8_music_128px).into(albumart);
                }
                songName.setText(SONG_PATH_TO_FRAG);
                artist.setText(ARTIST_PATH_TO_FRAG);
                Intent intent = new Intent(getContext(), MusicService.class);
                if (getContext() != null)
                {
                    getContext().bindService(intent, this, Context.BIND_AUTO_CREATE);
                }

            }

        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (getContext() != null)
        {
            getContext().unbindService(this);
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

    @Override
    public void onServiceConnected(ComponentName name, IBinder service)
    {
        MusicService.MyBinder binder = (MusicService.MyBinder) service;
        musicService = binder.getService();

    }

    @Override
    public void onServiceDisconnected(ComponentName name)
    {
        musicService = null;
    }


}