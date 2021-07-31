package com.example.music_player.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.example.music_player.Entity.MusicFiles;
import com.example.music_player.Fragment.AlbumFragment;
import com.example.music_player.Fragment.SongsFragment;
import com.example.music_player.HelperClasses.SongUtility;
import com.example.music_player.Interfaces.ShowHideNowPlayingFragment;
import com.example.music_player.R;
import com.google.android.material.tabs.TabLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.music_player.Services.MusicService.MUSIC_FILE;
import static com.example.music_player.Services.MusicService.MUSIC_FILES_LASTT_PLAYED;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, ShowHideNowPlayingFragment
{

    private ArrayList<MusicFiles> musicFiles;
    public static ArrayList<MusicFiles> albums=new ArrayList<>();
    private String MY_SORT_PREFERANCE="sortOrder";
    public static boolean SHOW_MINI_PLAYER =false;
    public static String PATH_TO_FRAG=null;
    public static String ARTIST_PATH_TO_FRAG=null;
    public static String SONG_PATH_TO_FRAG=null;
    public static final String ARTIST_NAME="ARTIST NAME";
    public static final String SONG_NAME="SONG NAME";
    private FrameLayout fragBottomPlayer;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragBottomPlayer=findViewById(R.id.frag_bottom_player);
        askPermission();

    }

    private void intiViewPager()
    {
        ViewPager viewPager=findViewById(R.id.view_pager);
        TabLayout tabLayout=findViewById(R.id.tab_layout);
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new SongsFragment(),"Songs");
        viewPagerAdapter.addFragments(new AlbumFragment(),"Albums");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);





    }



    public static class ViewPagerAdapter extends FragmentStatePagerAdapter
    {
        private ArrayList<Fragment>fragments;
        private ArrayList<String> titles;

        public ViewPagerAdapter(@NonNull FragmentManager fm)
        {
            super(fm);

            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();

        }
        void addFragments(Fragment fragment,String title)
        {
            this.fragments.add(fragment);
            this.titles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position)
        {
            return fragments.get(position);
        }

        @Override
        public int getCount()
        {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position)
        {
            return this.titles.get(position);
        }
    }

    private void askPermission()
    {
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener()
        {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport)
            {
                if(multiplePermissionsReport.areAllPermissionsGranted())
                {

                    musicFiles= (ArrayList<MusicFiles>) SongUtility.getMusicFilesList(MainActivity.this);
                    intiViewPager();
                }
                else
                {
                    askPermission();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken)
            {
                permissionToken.continuePermissionRequest();
            }


        }).check();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.search,menu);
        MenuItem menuItem= menu.findItem(R.id.search_option);
        SearchView searchView= (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        String userInput=newText.toLowerCase();
        ArrayList<MusicFiles> myFiles=new ArrayList<>();
        for(MusicFiles song:musicFiles)
        {
            if(song.getTitle().toLowerCase().contains(userInput))
            {
                myFiles.add(song);
            }
        }
        SongsFragment.musicAdapters.updateList(myFiles);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item)
    {
        SharedPreferences.Editor editor=getSharedPreferences(MY_SORT_PREFERANCE,MODE_PRIVATE).edit();
        switch (item.getItemId())
        {
            case R.id.sort_by_name:
                editor.putString("Sorting","sortByName");
                editor.apply();
                this.recreate();
                break;

            case R.id.sort_by_date:
                editor.putString("Sorting","sortByDate");
                editor.apply();
                this.recreate();
                break;

            case R.id.sort_by_size:
                editor.putString("Sorting","sortBySize");
                editor.apply();
                this.recreate();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        SharedPreferences preferences=getSharedPreferences(MUSIC_FILES_LASTT_PLAYED,MODE_PRIVATE);
        String path=preferences.getString(MUSIC_FILE,null);
        String artist=preferences.getString(ARTIST_NAME,null);
        String song_name=preferences.getString(SONG_NAME,null);

        if(path!=null)
        {
            SHOW_MINI_PLAYER=true;
            PATH_TO_FRAG=path;
            ARTIST_PATH_TO_FRAG=artist;
            SONG_PATH_TO_FRAG=song_name;

        }
        else
        {
            SHOW_MINI_PLAYER=false;
            PATH_TO_FRAG=null;
            ARTIST_PATH_TO_FRAG=null;
            SONG_PATH_TO_FRAG=null;

        }
    }


    @Override
    public void show()
    {
        fragBottomPlayer.setVisibility(View.VISIBLE);
    }
}