package com.example.music_player.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.music_player.Activities.PlayerActivity;
import com.example.music_player.BrodcastReceiver.NotificationReceiver;
import com.example.music_player.Entity.MusicFiles;
import com.example.music_player.Interfaces.ActionPlay;
import com.example.music_player.R;

import java.util.ArrayList;

import static com.example.music_player.Activities.PlayerActivity.listSongs;
import static com.example.music_player.HelperClasses.ApplicationClass.ACTION_NEXT;
import static com.example.music_player.HelperClasses.ApplicationClass.ACTION_PLAY;
import static com.example.music_player.HelperClasses.ApplicationClass.ACTION_PREVIOUS;
import static com.example.music_player.HelperClasses.ApplicationClass.CHANNEL_ID_1;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener
{
    public static final String MUSIC_FILES_LASTT_PLAYED = "LAST_PLAYED";
    public static final String MUSIC_FILE = "STORED_MUSIC";
    public static final String ARTIST_NAME = "ARTIST NAME";
    public static final String SONG_NAME = "SONG NAME";
    public ArrayList<MusicFiles> musicFiles = new ArrayList<>();
    public int position = -1;
    MyBinder mBinder = new MyBinder();
    MediaPlayer mediaPlayer;
    Uri uri;
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
        String actionName = intent.getStringExtra("ActionName");
        if (myPosition != -1)
        {
            try
            {
                playMedia(myPosition);
            }
            catch (Exception ignore)
            {
                Toast.makeText(this, "corrupted media cannot play", Toast.LENGTH_SHORT).show();
            }
        }
        if (actionName != null)
        {
            switch (actionName)
            {
                case "playPause":
                    Toast.makeText(this, "play pause", Toast.LENGTH_SHORT).show();
                    playPauseBtnClicked();
                    break;
                case "next":
                    Toast.makeText(this, "next", Toast.LENGTH_SHORT).show();
                    nextBtnClicked();
                    break;


                case "previous":
                    previousBtnClicked();

                    break;

            }
        }
        return START_STICKY;
    }

    private void playMedia(int startPosition)
    {
        musicFiles = listSongs;
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
            try
            {
                createMediaPlayer(position);
            }
            catch (Exception ignore)
            {
                Toast.makeText(this, "corrupted media cannot play", Toast.LENGTH_SHORT).show();
            }
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

    public void createMediaPlayer(int positionInner)
    {
        position = positionInner;
        try
        {
            uri = Uri.parse(musicFiles.get(position).getPath());
        }
        catch (Exception ignore)
        {
            Toast.makeText(this, "corrupted media cannot play", Toast.LENGTH_SHORT).show();
        }
        SharedPreferences.Editor editor = getSharedPreferences(MUSIC_FILES_LASTT_PLAYED, MODE_PRIVATE).edit();
        editor.putString(MUSIC_FILE, uri.toString());
        editor.putString(ARTIST_NAME, musicFiles.get(position).getArtist());
        editor.putString(SONG_NAME, musicFiles.get(position).getTitle());
        editor.apply();
        mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
    }

    public void onCompleted()
    {
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mp)
    {
        if (actionPlay != null)
        {
            actionPlay.nextBtnClicked();
            if (mediaPlayer != null)
            {
                createMediaPlayer(position);
                mediaPlayer.start();
                onCompleted();

            }
        }


    }

    public void setCallback(ActionPlay actionPlaying)
    {
        this.actionPlay = actionPlaying;
    }

    public void showNotification(int playPauseBtn)
    {
        Intent intent = new Intent(this, PlayerActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Intent prevIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_PREVIOUS);
        PendingIntent prevPending = PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_PLAY);
        PendingIntent pausePending = PendingIntent.getBroadcast(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        byte[] picture = null;
        try
        {
            picture = getAlbumArt(musicFiles.get(position).getPath());
        } catch (Exception ignored)
        {
        }

        Bitmap thumb;
        if (picture != null)
        {
            thumb = BitmapFactory.decodeByteArray(picture, 0, picture.length);
        } else
        {
            thumb = BitmapFactory.decodeResource(getResources(), R.drawable.icon_music);
        }
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_1).setSmallIcon(playPauseBtn)
                .setLargeIcon(thumb)
                .setContentTitle(musicFiles.get(position).getTitle())
                .setContentText(musicFiles.get(position).getArtist())
                .addAction(R.drawable.ic_baseline_skip_previous_24, "Previous", prevPending)
                .addAction(playPauseBtn, "pause", pausePending)
                .addAction(R.drawable.ic_baseline_skip_next_24, "Next", nextPending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setContentIntent(contentIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
       // startForeground(0, notification);
        Toast.makeText(this, "Notification Created", Toast.LENGTH_SHORT).show();
        // getNotification(playPauseBtn);

    }

    private byte[] getAlbumArt(String uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

    public void playPauseBtnClicked()
    {
        if (actionPlay != null)
        {
            actionPlay.playPauseBtnClicked();
        }
    }

    public void previousBtnClicked()
    {
        Toast.makeText(this, "previous", Toast.LENGTH_SHORT).show();
        if (actionPlay != null)
        {
            actionPlay.prevBtnClicked();
        }
    }

    public void nextBtnClicked()
    {
        if (actionPlay != null)
        {
            actionPlay.nextBtnClicked();
        }
    }

    public class MyBinder extends Binder
    {
        public MusicService getService()
        {
            return MusicService.this;
        }
    }
}
