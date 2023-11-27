package com.biobattle;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.media.MediaPlayer;

public class MainMenu extends AppCompatActivity {


    private MediaPlayer selectMediaPlayer;
    private MediaPlayer menuMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        selectMediaPlayer = MediaPlayer.create(this, R.raw.selection);
        menuMediaPlayer = MediaPlayer.create(this, R.raw.menu);
        menuMediaPlayer.setLooping(true);
        menuMediaPlayer.start();
    }

    public void playGame(View view) {
        selectMediaPlayer.start();
        menuMediaPlayer.pause();
        menuMediaPlayer.seekTo(0);
        selectMediaPlayer.stop();


        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (menuMediaPlayer != null && menuMediaPlayer.isPlaying()) {
            menuMediaPlayer.pause();
            selectMediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (menuMediaPlayer != null && !menuMediaPlayer.isPlaying()) {
            menuMediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (menuMediaPlayer != null) {
            menuMediaPlayer.stop();
            menuMediaPlayer.release();
            selectMediaPlayer.release();
            menuMediaPlayer = null;
        }
    }
}
