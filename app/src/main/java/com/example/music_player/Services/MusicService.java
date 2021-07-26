package com.example.music_player.Services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
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
import androidx.core.app.NotificationManagerCompat;

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
        String actionName = intent.getStringExtra("ActionName");
        if (myPosition != -1)
        {
            playMedia(myPosition);
        }
        if (actionName != null)
        {
            switch (actionName)
            {
                case "playPause":
                    Toast.makeText(this, "play pause", Toast.LENGTH_SHORT).show();
                    if (actionPlay != null)
                    {
                        actionPlay.playPauseBtnClicked();
                    }
                    break;

                case "next":
                    Toast.makeText(this, "next", Toast.LENGTH_SHORT).show();
                    if (actionPlay != null)
                    {
                        actionPlay.nextBtnClicked();
                    }
                    break;


                case "previous":
                    Toast.makeText(this, "previous", Toast.LENGTH_SHORT).show();
                    if (actionPlay != null)
                    {
                        actionPlay.prevBtnClicked();
                    }
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

    public void createMediaPlayer(int positionInner)
    {
        positionInner = positionInner;
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
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.notify(0, notification);
            startForeground(0,notification);
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

    public class MyBinder extends Binder
    {
        public MusicService getService()
        {
            return MusicService.this;
        }
    }

}
