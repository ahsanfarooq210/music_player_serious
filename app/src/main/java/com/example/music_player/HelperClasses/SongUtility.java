package com.example.music_player.HelperClasses;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import com.example.music_player.Entity.MusicFiles;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SongUtility
{
    private static List<MusicFiles> musicFilesList;
    public static boolean shuffleBoolean=false,repeatBoolean=false;

    public static List<MusicFiles> getMusicFilesList(Context context)
    {
        if(musicFilesList==null)
        {
            musicFilesList=getAudioFiles(context);
            return musicFilesList;
        }
        else
        {
            return musicFilesList;
        }
    }

    public static Uri getArtUriFromMusicFile(Context context, File file)
    {
        final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final String[] cursor_cols = {MediaStore.Audio.Media.ALBUM_ID};

        final String where = MediaStore.Audio.Media.IS_MUSIC + "=1 AND " + MediaStore.Audio.Media.DATA + " = '"
                + file.getAbsolutePath() + "'";
        final Cursor cursor = context.getApplicationContext().getContentResolver().query(uri, cursor_cols, where, null, null);
        /*
         * If the cusor count is greater than 0 then parse the data and get the art id.
         */
        if (cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            Long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
            cursor.close();
            return albumArtUri;
        }
        return Uri.EMPTY;
    }

    private static List<MusicFiles> getAudioFiles(Context context)
    {
        List<MusicFiles> list=new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        //looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst())
        {
            do
            {

                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                if(title.isEmpty())
                {
                    continue;
                }
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String album=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                Long albumId=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String id= cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));


                Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);


                MusicFiles musicFiles = new MusicFiles();
                musicFiles.setTitle(title);
                musicFiles.setArtist(artist);
                musicFiles.setPath(url);
                musicFiles.setAlbum(album);
                musicFiles.setDuration(duration);
                musicFiles.setId(id);


                list.add(musicFiles);

            } while (cursor.moveToNext());
        }
        assert cursor != null;
        cursor.close();

        return list;
    }


}
