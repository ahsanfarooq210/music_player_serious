package com.example.music_player.Activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.example.music_player.Entity.MusicFiles;
import com.example.music_player.HelperClasses.SongUtility;
import com.example.music_player.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener
{
    private static Uri uri;
    private static MediaPlayer mediaPlayer;
    private TextView songName, artistName, durationPlayed, durationTotal;
    private ImageView coverArt, nextBtn, prevBtn, backBtn, shuffleBtn, menuBtn, repeatBtn;
    private SeekBar seekBar;
    private FloatingActionButton playPauseBtn;
    private int position = -1;
    private ArrayList<MusicFiles> listSongs;
    private Handler handler = new Handler();
    private Thread playThread, prevThread, nextThread;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initViews();
        getIntentMethod();
        songName.setText(listSongs.get(position).getTitle());
        artistName.setText(listSongs.get(position).getArtist());
        mediaPlayer.setOnCompletionListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (mediaPlayer != null && fromUser)
                {
                    mediaPlayer.seekTo(progress * 1000);
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
                if (mediaPlayer != null)
                {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
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
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        super.onResume();
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

    private void prevBtnClicked()
    {
        if (mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();
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
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            songName.setText(listSongs.get(position).getTitle());
            artistName.setText(listSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setBackgroundResource(R.drawable.ic_baseline_pause_24);
            mediaPlayer.start();
        } else
        {
            mediaPlayer.stop();
            mediaPlayer.release();
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
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            songName.setText(listSongs.get(position).getTitle());
            artistName.setText(listSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
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

    private void nextBtnClicked()
    {
        if (mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();
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
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            songName.setText(listSongs.get(position).getTitle());
            artistName.setText(listSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setBackgroundResource(R.drawable.ic_baseline_pause_24);
            mediaPlayer.start();
        } else
        {
            mediaPlayer.stop();
            mediaPlayer.release();
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
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            songName.setText(listSongs.get(position).getTitle());
            artistName.setText(listSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
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

    private void playPauseBtnClicked()
    {
        if (mediaPlayer.isPlaying())
        {
            playPauseBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            mediaPlayer.pause();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this, 1000);
                }
            });
        } else
        {
            playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
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
        listSongs = (ArrayList<MusicFiles>) SongUtility.getAudioFiles(PlayerActivity.this);
        if (listSongs != null)
        {
            playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24);
            uri = Uri.parse(listSongs.get(position).getPath());
        }
        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        } else
        {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        }
        seekBar.setMax(mediaPlayer.getDuration() / 1000);
        metaData(uri);


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
        backBtn = findViewById(R.id.back_btn);
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
    public void onCompletion(MediaPlayer mp)
    {
        nextBtnClicked();
        if(mediaPlayer!=null)
        {
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }
    }
}