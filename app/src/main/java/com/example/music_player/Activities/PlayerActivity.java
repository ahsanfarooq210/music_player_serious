package com.example.music_player.Activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.example.music_player.Entity.MusicFiles;
import com.example.music_player.HelperClasses.SongUtility;
import com.example.music_player.Interfaces.ActionPlay;
import com.example.music_player.R;
import com.example.music_player.Services.MusicService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

import static com.example.music_player.Adapters.AlbumDetailsAdapter.albumFiles;
import static com.example.music_player.Adapters.MusicAdapters.mfiles;

public class PlayerActivity extends AppCompatActivity implements ActionPlay, ServiceConnection
{
    private static Uri uri;
    //private static MediaPlayer mediaPlayer;
    private TextView songName, artistName, durationPlayed, durationTotal;
    private ImageView coverArt, nextBtn, prevBtn, shuffleBtn, menuBtn, repeatBtn;
    private SeekBar seekBar;
    private FloatingActionButton playPauseBtn;
    private int position = -1;
    public static ArrayList<MusicFiles> listSongs;
    private Handler handler = new Handler();
    private Thread playThread, prevThread, nextThread;
    MusicService musicService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initViews();
        getIntentMethod();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (musicService != null && fromUser)
                {
                    musicService.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (musicService != null)
                {
                    int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    durationPlayed.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });
        shuffleBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(SongUtility.shuffleBoolean)
                {
                    SongUtility.shuffleBoolean=false;
                    shuffleBtn.setImageResource(R.drawable.ic_baseline_shuffle_of);
                }
                else
                {
                    SongUtility.shuffleBoolean=true;
                    shuffleBtn.setImageResource(R.drawable.ic_baseline_shuffle_on);
                }
            }
        });
        repeatBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(SongUtility.repeatBoolean)
                {
                    SongUtility.repeatBoolean=false;
                    repeatBtn.setImageResource(R.drawable.ic_baseline_repeat_of);
                }
                else
                {
                    SongUtility.repeatBoolean=true;
                    repeatBtn.setImageResource(R.drawable.ic_baseline_repeat_on);
                }
            }
        });
    }

    @Override
    protected void onResume()
    {
        Intent intent= new Intent(this,MusicService.class);
        bindService(intent,this,BIND_AUTO_CREATE);
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        
        unbindService(this);
    }

    private void prevThreadBtn()
    {
        prevThread = new Thread()
        {
            @Override
            public void run()
            {
                super.run();
                prevBtn.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        prevBtnClicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    public void prevBtnClicked()
    {
        if (musicService.isPlaying())
        {
            musicService.stop();
            musicService.release();
            if(SongUtility.shuffleBoolean&&!SongUtility.repeatBoolean)
            {
                position=getRandom(listSongs.size()-1);
            }
            else
            {
                if(!SongUtility.shuffleBoolean&& !SongUtility.repeatBoolean)
                {
                    position = ((position - 1) < 0 ? (listSongs.size() - 1) : (position - 1));
                }
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            songName.setText(listSongs.get(position).getTitle());
            artistName.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (musicService  != null)
                    {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.onCompleted();
            playPauseBtn.setBackgroundResource(R.drawable.ic_baseline_pause_24);
            musicService.start();
        } else
        {
            musicService.stop();
            musicService.release();
            if(SongUtility.shuffleBoolean&&!SongUtility.repeatBoolean)
            {
                position=getRandom(listSongs.size()-1);
            }
            else
            {
                if(!SongUtility.shuffleBoolean&& !SongUtility.repeatBoolean)
                {
                    position = ((position - 1) < 0 ? (listSongs.size() - 1) : (position - 1));
                }
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            songName.setText(listSongs.get(position).getTitle());
            artistName.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (musicService != null)
                    {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.onCompleted();
            playPauseBtn.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
        }
    }

    private void nextThreadBtn()
    {
        nextThread = new Thread()
        {
            @Override
            public void run()
            {
                super.run();
                nextBtn.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        nextBtnClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    public void nextBtnClicked()
    {
        if (musicService.isPlaying())
        {
            musicService.stop();
            musicService.release();
            if(SongUtility.shuffleBoolean&&!SongUtility.repeatBoolean)
            {
                position=getRandom(listSongs.size()-1);
            }
            else
            {
                if(!SongUtility.shuffleBoolean&& !SongUtility.repeatBoolean)
                {
                    position = ((position + 1) % listSongs.size());
                }
            }
            //else the repeat button is on so we dont have to change the position
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            songName.setText(listSongs.get(position).getTitle());
            artistName.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (musicService != null)
                    {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.onCompleted();
            playPauseBtn.setBackgroundResource(R.drawable.ic_baseline_pause_24);
            musicService.start();
        } else
        {
            musicService.stop();
            musicService.release();
            if(SongUtility.shuffleBoolean&&!SongUtility.repeatBoolean)
            {
                position=getRandom(listSongs.size()-1);
            }
            else
            {
                if(!SongUtility.shuffleBoolean&& !SongUtility.repeatBoolean)
                {
                    position = ((position + 1) % listSongs.size());
                }
            }
            //else the repeat button is on so we dont have to change the position
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            songName.setText(listSongs.get(position).getTitle());
            artistName.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (musicService != null)
                    {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.onCompleted();
            playPauseBtn.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
        }
    }

    private int getRandom(int i)
    {
        Random random=new Random();

        return random.nextInt(i+1);
    }

    private void playThreadBtn()
    {
        playThread = new Thread()
        {
            @Override
            public void run()
            {
                super.run();
                playPauseBtn.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        playPauseBtnClicked();
                    }
                });
            }
        };
        playThread.start();
    }

    public void playPauseBtnClicked()
    {
        if (musicService.isPlaying())
        {
            playPauseBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            musicService.pause();
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (musicService != null)
                    {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this, 1000);
                }
            });
        } else
        {
            playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24);
            musicService.start();
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (musicService != null)
                    {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }
    }

    private String formattedTime(int mCurrentPosition)
    {
        String totalout = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        totalout = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" + seconds;
        if (seconds.length() == 1)
        {
            return totalNew;
        } else
        {
            return totalout;
        }

    }

    private void getIntentMethod()
    {
        position = getIntent().getIntExtra("position", -1);
        String sender=getIntent().getStringExtra("sender");
        if(sender!=null&&sender.equals("albumDetails"))
        {
            listSongs=albumFiles;
        }
        else
        {
            listSongs = mfiles;
        }

        if (listSongs != null)
        {
            playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24);
            uri = Uri.parse(listSongs.get(position).getPath());
        }

        Intent intent=new Intent(this,MusicService.class);
        intent.putExtra("serviceposition",position);
        startService(intent);




    }

    private void initViews()
    {
        songName = findViewById(R.id.song_name);
        artistName = findViewById(R.id.song_artist);
        coverArt = findViewById(R.id.cover_art);
        durationPlayed = findViewById(R.id.duration_played);
        durationTotal = findViewById(R.id.duration_total);
        nextBtn = findViewById(R.id.id_next);
        prevBtn = findViewById(R.id.id_prev);
        shuffleBtn = findViewById(R.id.id_shuffle);
        menuBtn = findViewById(R.id.menu_btn);
        repeatBtn = findViewById(R.id.id_repeat);
        seekBar = findViewById(R.id.sek_bar);
        playPauseBtn = findViewById(R.id.play_pause);
    }

    private void metaData(Uri uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int duraionTotal = Integer.parseInt(listSongs.get(position).getDuration()) / 1000;
        this.durationTotal.setText(formattedTime(duraionTotal));
        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap;
        if (art != null)
        {

            bitmap= BitmapFactory.decodeByteArray(art,0,art.length);
            imageAnimation(this,coverArt,bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener()
            {
                @Override
                public void onGenerated(@Nullable Palette palette)
                {
                    assert palette != null;
                    Palette.Swatch swatch=palette.getDominantSwatch();
                    if(swatch!=null)
                    {
                        ImageView gradient=findViewById(R.id.imageViewGradient);
                        RelativeLayout mContainer=findViewById(R.id.mContainer);
                        gradient.setBackgroundResource(R.drawable.gradiant_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable= new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,new int[]{swatch.getRgb(),0x00000000});
                        gradient.setBackground(gradientDrawable);

                        GradientDrawable gradientDrawableBg= new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,new int[]{swatch.getRgb(),swatch.getRgb()});
                        mContainer.setBackground(gradientDrawableBg);
                        songName.setTextColor(swatch.getTitleTextColor());
                        artistName.setTextColor(swatch.getBodyTextColor());
                    }
                    else
                    {
                        ImageView gradient=findViewById(R.id.imageViewGradient);
                        RelativeLayout mContainer=findViewById(R.id.mContainer);
                        gradient.setBackgroundResource(R.drawable.gradiant_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable= new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,new int[]{0xff000000,0x00000000});
                        gradient.setBackground(gradientDrawable);

                        GradientDrawable gradientDrawableBg= new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,new int[]{0xff000000,0xff000000});
                        mContainer.setBackground(gradientDrawableBg);
                        songName.setTextColor(Color.WHITE);
                        artistName.setTextColor(Color.DKGRAY);
                    }
                }
            });
        } else
        {
            Glide.with(this).asBitmap().load(R.drawable.icons8_music_200px).into(coverArt);
            ImageView gradient=findViewById(R.id.imageViewGradient);
            RelativeLayout mContainer=findViewById(R.id.mContainer);
            gradient.setBackgroundResource(R.drawable.gradiant_bg);
            mContainer.setBackgroundResource(R.drawable.main_bg);
            songName.setTextColor(Color.WHITE);
            artistName.setTextColor(Color.DKGRAY);

        }
    }
    public void imageAnimation(Context context,ImageView imageVIew,Bitmap bitmap)
    {
        Animation animatioOut=AnimationUtils.loadAnimation(context,android.R.anim.fade_out);
        Animation animatioIn=AnimationUtils.loadAnimation(context,android.R.anim.fade_in);
        animatioOut.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                Glide.with(context).load(bitmap).into(imageVIew);
                animatioIn.setAnimationListener(new Animation.AnimationListener()
                {
                    @Override
                    public void onAnimationStart(Animation animation)
                    {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation)
                    {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation)
                    {

                    }
                });
                imageVIew.startAnimation(animatioIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });
        imageVIew.startAnimation(animatioOut);
    }



    @Override
    public void onServiceConnected(ComponentName name, IBinder service)
    {
        MusicService.MyBinder myBinder= (MusicService.MyBinder) service;
        musicService=myBinder.getService();
        Toast.makeText(this, "Connected"+musicService, Toast.LENGTH_SHORT).show();
        seekBar.setMax(musicService.getDuration() / 1000);
        metaData(uri);
        songName.setText(listSongs.get(position).getTitle());
        artistName.setText(listSongs.get(position).getArtist());
        musicService.onCompleted();


    }

    @Override
    public void onServiceDisconnected(ComponentName name)
    {
        musicService=null;
    }
}