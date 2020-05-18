package com.example.exerciseservice;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class Player extends AppCompatActivity {
    Bundle bundle;
    ArrayList<File> listMusik;
    SeekBar seekBar;
    TextView title;
    ImageView play;
    ImageView next;
    ImageView prev;
    static MediaPlayer mp;
    int position;
    TextView currentTime;
    TextView totalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        // Setting resource
        seekBar = findViewById(R.id.seekBar);
        title = findViewById(R.id.title);
        play = findViewById(R.id.play);
        next = findViewById(R.id.next);
        prev = findViewById(R.id.prev);
        currentTime = findViewById(R.id.currentTimer);
        totalTime = findViewById(R.id.totalTimer);

        if (mp != null) {
            mp.stop();
        }

        Intent intent = getIntent();
        bundle = intent.getExtras();

        listMusik = (ArrayList) bundle.getParcelableArrayList("musicList");
        position = bundle.getInt("position", 0);
        initMusicPlayer(position);

        //setup play pause button

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlay();
            }
        });

        // setup next button

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position < listMusik.size() - 1) {
                    // cek posisi music di list

                    position++;
                } else {
                    // jika posisinya lebih besar dari nomer di list
                    // set posisi ke 0
                    position = 0;
                }
                //play musik di list dengan position
                initMusicPlayer(position);
            }
        });

        // setup prev button
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position <= 0) {
                    position = listMusik.size() - 1;
                } else {
                    position--;
                }
                initMusicPlayer(position);
            }
        });
    }

    private void initMusicPlayer(final int position) {
        if (mp != null && mp.isPlaying()) {
            mp.reset();
        }

        String name = listMusik.get(position).getName();
        title.setText(name);
        // get song path di sd card
        Uri ResourceUri = Uri.parse(listMusik.get(position).toString());
        // create media player
        mp = MediaPlayer.create(getApplicationContext(), ResourceUri);

        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // set to maximum duration
                seekBar.setMax(mp.getDuration());
                // set max duration musik
                String totTime = createTimeLabel(mp.getDuration());
                totalTime.setText(totTime);
                // start music player
                mp.start();
                // set icon to pause
                play.setImageResource(R.drawable.pause);
            }
        });

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // musik akan otomatis lanjut ketika selesai
                int currentSongPosition = position;
                if (currentSongPosition < listMusik.size() - 1) {
                    // cek posisi music di list

                    currentSongPosition++;
                } else {
                    // jika posisinya lebih besar dari nomer di list
                    // set posisi ke 0
                    currentSongPosition = 0;
                }
                //play musik di list dengan position
                initMusicPlayer(currentSongPosition);
            }
        });

        // setting up seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // sesuatu dilakukan ketika seekbar berubah
                if (fromUser) {
                    mp.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // setup seekbar untuk mengubah durasi musik

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mp != null) {
                    try {
                        if (mp.isPlaying()) {
                            Message msg = new Message();
                            msg.what = mp.getCurrentPosition();
                            handler.sendMessage(msg);
                            Thread.sleep((1000));
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    // membuat handler untuk set progress

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            currentTime.setText(createTimeLabel(msg.what));
            seekBar.setProgress(msg.what);
        }
    };
    // setup play button to changes icon when clicked
    private void setPlay() {
        if (mp != null && mp.isPlaying()) {
            mp.pause();
            play.setImageResource(R.drawable.play);
        } else {
            mp.start();
            play.setImageResource(R.drawable.pause);
        }
    }

    public String createTimeLabel(int duration){
        String timeLabel = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        timeLabel += min + ":";

        if(sec <10) timeLabel += "0";
        timeLabel += sec;
        return timeLabel;
    }
}