package com.example.music_player.Services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.music_player.Activities.PlayerActivity;
import com.example.music_player.Entity.MusicFiles;
import com.example.music_player.Interfaces.ActionPlay;

import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener
{
    MyBinder mBinder = new MyBinder();
    MediaPlayer mediaPlayer;
    ArrayList<MusicFiles> musicFiles = new ArrayList<>();
    Uri uri;
    int position = -1;
    ActionPlay actionPlay;

    @Override
    public void onCreate()
    {
        super.onCreate();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        Log.e("bind", "method");

        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        int myPosition = intent.getIntExtra("serviceposition", -1);
        String actionName=intent.getStringExtra("ActionName");
        if (myPosition != -1)
        {
            playMedia(myPosition);
        }
        if(actionName!=null)
        {
            switch (actionName)
            {
                case "playPause":
                    Toast.makeText(this, "play pause", Toast.LENGTH_SHORT).show();
                    break;

                case "next":
                    Toast.makeText(this, "next", Toast.LENGTH_SHORT).show();
                    break;


                case "previous":
                    Toast.makeText(this, "previous", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
        return START_STICKY;
    }

    private void playMedia(int startPosition)
    {
        musicFiles = PlayerActivity.listSongs;
        position = startPosition;
        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (musicFiles != null)
            {
                createMediaPlayer(position);
                mediaPlayer.start();
            }
        } else
        {
            createMediaPlayer(position);
            mediaPlayer.start();
        }
    }

    public void start()
    {
        mediaPlayer.start();
    }

    public void pause()
    {
        mediaPlayer.pause();
    }

    public boolean isPlaying()
    {
        return mediaPlayer.isPlaying();
    }

    public void stop()
    {
        mediaPlayer.stop();
    }

    public void release()
    {
        mediaPlayer.release();
    }

    public int getDuration()
    {
        return mediaPlayer.getDuration();
    }

    public void seekTo(int position)
    {
        mediaPlayer.seekTo(position);
    }

    public int getCurrentPosition()
    {
        return mediaPlayer.getCurrentPosition();
    }

    public void createMediaPlayer(int position)
    {
        uri = Uri.parse(musicFiles.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
    }

    public void onCompleted()
    {
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mp)
    {
        if(actionPlay!=null)
        {
            actionPlay.nextBtnClicked();
        }


        createMediaPlayer(position);
        mediaPlayer.start();
        onCompleted();

    }

    public class MyBinder extends Binder
    {
        public MusicService getService()
        {
            return MusicService.this;
        }
    }

}
