package com.example.exerciseservice;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ArrayAdapter<String> musicAdapter;
    ArrayList<File> musics;
    String song[];
    private boolean shuffle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listview);

        Dexter.withActivity(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                // display list of musik
                musics = findMusicFiles(Environment.getExternalStorageDirectory());
                song = new String[musics.size()];

                for (int i = 0; i < musics.size(); i++){
                    song[i] = musics.get(i).getName();
                }

                musicAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, song);
                listView.setAdapter(musicAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent player = new Intent(getApplicationContext(), Player.class);

                        player.putExtra("musicList", musics);
                        player.putExtra("position", position);
                        startActivity(player);
                    }
                });
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

            }
        }).check();
    }
    // setup to find file musik
    private ArrayList<File> findMusicFiles(File file) {
        ArrayList<File> allMusicFile = new ArrayList<>();
        File[] files = file.listFiles();
        for (File currentFile : files) {
            if (currentFile.isDirectory() && !currentFile.isHidden()) {
                allMusicFile.addAll(findMusicFiles(currentFile));
            } else {
                if (currentFile.getName().endsWith(".mp3")  || currentFile.getName().endsWith(".mp4a") || currentFile.getName().endsWith(".wav"))
                allMusicFile.add(currentFile);
            }
        }
        return allMusicFile;
    }
    // setup menu option
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.close){
            MediaPlayer mp = new MediaPlayer();
            mp.stop();
            System.exit(0);
        }else if (item.getItemId() == R.id.shuffle){
            setShuffle();
        }
        return super.onOptionsItemSelected(item);
    }
    public void setShuffle() {
        if (shuffle) {
            shuffle = false;
        } else {
            shuffle = true;
        }
    }
}
