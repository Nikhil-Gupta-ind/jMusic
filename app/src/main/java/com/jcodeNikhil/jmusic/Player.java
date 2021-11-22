package com.jcodeNikhil.jmusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class Player extends AppCompatActivity {

    TextView audioTitle, currentT , maxT;
    SeekBar seekBar;
    Thread updateSeek;
    ImageView play;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textContent;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player2);
        audioTitle = findViewById(R.id.audio_title);
        seekBar = findViewById(R.id.seekBar);
        play = findViewById(R.id.play);
        currentT = findViewById(R.id.currentT);
        maxT = findViewById(R.id.maxT);

        //Fetching Data from Main
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList) bundle.getParcelableArrayList("songList");
        position = intent.getIntExtra("position", 0);

        /*AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        int res = am.requestAudioFocus()
        // Request audio focus for playback
        int result = am.requestAudioFocus(afChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // Start playback.
        }
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            updateSeek.interrupt();
        }*/
        startPlayer(position);
        play.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        //PRESSED
                        if(mediaPlayer.isPlaying()){
                            play.setImageResource(R.drawable.ic_baseline_pause_pressed);
                        }
                        else {
                            play.setImageResource(R.drawable.ic_baseline_play_pressed_24);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        //RELEASED
                        if(mediaPlayer.isPlaying()){
                            play.setImageResource(R.drawable.ic_baseline_pause_24);
                        }
                        else {
                            play.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                        }
                        break;
                }
                return false;
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()){
                    play.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                    mediaPlayer.pause();
                }
                else {
                    play.setImageResource(R.drawable.ic_baseline_pause_24);
                    mediaPlayer.start();
                }
            }
        });
    }

    private void startPlayer(int index){
        Uri uri = Uri.parse(songs.get(index).toString()); //creating uri out of mp3 file
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri); //initializing media player
        mediaPlayer.start();
        play.setImageResource(R.drawable.ic_baseline_pause_24);
        textContent = songs.get(index).getName();
        audioTitle.setText(textContent);
        audioTitle.setSelected(true);
        seekBar.setMax(mediaPlayer.getDuration());
        maxT.setText(getTimeString(mediaPlayer.getDuration()));
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
//                next.performClick();
                setNext(findViewById(R.id.next));
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentT.setText(getTimeString(seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());

            }
        });
        updateSeek = new Thread(){
            @Override
            public void run() {
                int currentPosition;
                try {
                    while (true){
                        Log.d("MyThread", "run: Thread running");
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        sleep(1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();
    }

    public void setNext(View view){
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
        if (position != songs.size()-1){
            position = position + 1;
        }
        else {
            position = 0;
        }
        startPlayer(position);
    }
    public void setPrevious(View view){
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
        if (position != 0 ){
            position = position - 1;
        }
        else {
            position = songs.size() - 1;
        }
        startPlayer(position);
    }
    public void setRepeat(View view){
        ImageView v= (ImageView) view;
        if (mediaPlayer.isLooping()){
            v.setImageResource(R.drawable.repeat);
            mediaPlayer.setLooping(false);
        }
        else {
            v.setImageResource(R.drawable.repeat_one);
            mediaPlayer.setLooping(true);
        }
    }

    private String getTimeString(long millis) {
        StringBuffer buf = new StringBuffer();

//        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

        buf
//                .append(String.format("%02d", hours))
//                .append(":")
                .append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));

        return buf.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.settings){
            Toast.makeText(this, "Coming Soon!", Toast.LENGTH_SHORT).show();
        }
        if(id == R.id.share){
            String textMessage = "https://github.com/Nikhil-Gupta-ind/jCloud";
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, textMessage);
            shareIntent.setType("text/plain");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // Try to invoke the intent.
            try {
                startActivity(Intent.createChooser(shareIntent, "Share jMusic App"));
            } catch (ActivityNotFoundException e) {
                // Define what your app should do if no activity can handle the intent.
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }
}