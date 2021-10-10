package com.jcodeNikhil.jmusic;

//    // TODO (1) Create icon/logo and add colours and pngs.
//    // TODO (2) Create animated splash screen and welcome screen
//    // TODO (3) Create welcome screen cards showing feature.
//    // TODO (4) Create repeat button shuffle/queue
//    // TODO (5) Create menu items such as favorites, playlists etc. settings activity optional
//    // TODO (6) Create Search bar
//    // TODO (7) Create Drawer items (not decided)
//    // TODO (8) Create player activity / swipable drawer like
//    // TODO (9) Create ui/ux also show album art
//    // TODO (9) Create album art clickable
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    ListView listView;
    TextView textView;
    Animation animation;
    FrameLayout frameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frameLayout = findViewById(R.id.frameLayout);
        listView = findViewById(R.id.listView);
        textView = findViewById(R.id.textView);
//        Resources res = getResources();
//        String text = res.getString(R.string.welcome_messages, "h", 1);
        String format = "%1$-14s";
        textView.setText(String.format(format,"Nikhil Gupta")); //String formating in android java

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//                        Toast.makeText(MainActivity.this, "Storage permission granted", Toast.LENGTH_SHORT).show();

                        animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.listanim);
                        frameLayout.setAnimation(animation);

                        ArrayList<File> mySongs = fetchSongs(Environment.getExternalStorageDirectory());
                        String [] items = new String[mySongs.size()];
                        for (int i=0; i<mySongs.size();i++){
                            items [i] = mySongs.get(i).getName().replace(".mp3", "");
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, items);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(MainActivity.this, Player.class);
                                String currentSong = listView.getItemAtPosition(position).toString();
                                intent.putExtra("songList", mySongs);
                                intent.putExtra("currentSong", currentSong);
                                intent.putExtra("position", position);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }

    public ArrayList<File> fetchSongs(File file){
        ArrayList arrayList = new ArrayList();
        File [] songs = file.listFiles();
        if(songs != null){
            for (File myFile : songs) {
                if (!myFile.isHidden() && myFile.isDirectory()){
                    arrayList.addAll(fetchSongs(myFile));
                }
                else {
                    if(myFile.getName().endsWith(".mp3") || myFile.getName().endsWith(".m4a") && !myFile.getName().startsWith(".")){
                        arrayList.add(myFile);
                    }
                }
            }
        }
        return arrayList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
//        int count = 0;
//        menu item click handling
        if(id == R.id.settings){
            Toast.makeText(this, "Coming soon!", Toast.LENGTH_SHORT).show();
        }
        if(id == R.id.share){
            String textMessage = "https://github.com/Nikhil-Gupta-ind/jCloud";
            // Create the text message with a string.
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
}