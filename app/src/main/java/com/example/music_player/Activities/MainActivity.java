package com.example.music_player.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.os.Bundle;

import com.example.music_player.Entity.MusicFiles;
import com.example.music_player.Fragment.AlbumFragment;
import com.example.music_player.Fragment.SongsFragment;
import com.example.music_player.HelperClasses.SongUtility;
import com.example.music_player.R;
import com.google.android.material.tabs.TabLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{

    private ArrayList<MusicFiles> musicFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

                    musicFiles= (ArrayList<MusicFiles>) SongUtility.getAudioFiles(MainActivity.this);
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

}