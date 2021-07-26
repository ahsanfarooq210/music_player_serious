package com.example.music_player.BrodcastReceiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;

import com.example.music_player.R;

public class NotifAction extends BroadcastReceiver
{

    private static final String TAG = "maybe";
    private MediaPlayer mp;
    @Override
    public void onReceive(final Context context, Intent intent) {

        String action = intent.getAction();
        Bundle extra = intent.getExtras();

        if (extra != null) {
            int notifId = extra.getInt("NotifId");
            if (action.equals("Play")) {

                handleMusicState(mp);
            } else if (action.equals("Pause")) {

                handleMusicState(mp);
                setMessageRead(notifId, context);

            } else {
                Toast.makeText(context, "extra  action !", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(context, "Empty extra !", Toast.LENGTH_SHORT).show();
        }
    }


    private void setMessageRead(int id, Context context) {
        //  other  method
        clearNotification(id, context);

    }

    private void clearNotification(int id, Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }

    private void handleMusicState(MediaPlayer mediaPlayer) {
        if (mediaPlayer.isPlaying()) mediaPlayer.pause();
        else mediaPlayer.start();

    }
}