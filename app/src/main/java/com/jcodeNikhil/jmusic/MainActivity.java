package com.jcodeNikhil.jmusic;

//    // TODO (1) Create icon/logo and add colours and pngs.
//    // TODO (2) Create animated splash screen and welcome screen
//    // TODO (3) Create welcome screen cards showing feature.
//    // TODO (4) Create repeat button shuffle/queue
//    // TODO (5) Create menu items such as favorites, playlists etc. settings activity optional
//    // TODO (6) Create Search bar
//    // TODO (7) Create Drawer items (not decided)
//    // TODO (8) Create player activity / draggable drawer like
//    // TODO (9) Create ui/ux also show album art
//    // TODO (9) Create album art clickable
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
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

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<File>> {

    ListView listView;
    TextView textView,empty;
    Animation animation;
    FrameLayout frameLayout;
    ImageView playAll;
    /*
     * This number will uniquely identify our Loader and is chosen arbitrarily. You can change this
     * to any number you like, as long as you use the same variable name.
     */
    private static final int FILE_LOADER = 22;
    ArrayList<File> mySongs;
    ArrayAdapter<String> adapter;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frameLayout = findViewById(R.id.frameLayout);
        listView = findViewById(R.id.listView);
        textView = findViewById(R.id.textView);
        playAll = findViewById(R.id.play_all);
        progressBar = findViewById(R.id.progress_circular);
        empty = findViewById(R.id.empty);
//        Resources res = getResources();
//        String text = res.getString(R.string.welcome_messages, "h", 1);
        String format = "%1$-14s";
        textView.setText(String.format(format,"Nikhil Gupta")); //String formatting in android java

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//                        Toast.makeText(MainActivity.this, "Storage permission granted", Toast.LENGTH_SHORT).show();
                        /*perhaps useful instead saveInstance, but it's not working here, something is causing the main activity to destroy and return to splash
                        getSupportLoaderManager().initLoader(FILE_LOADER, null, MainActivity.this);*/
                        animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.listanim);
                        frameLayout.setAnimation(animation);

                        //can be used for reloading list
                        LoaderManager loaderManager = getSupportLoaderManager();
                        Loader<ArrayList<File>> fileLoader = loaderManager.getLoader(FILE_LOADER);
                        if (fileLoader == null){
                            loaderManager.initLoader(FILE_LOADER,null,MainActivity.this);
                        } else {
                            loaderManager.restartLoader(FILE_LOADER, null, MainActivity.this);
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(MainActivity.this, "Needs storage permission!", Toast.LENGTH_SHORT).show();
                        finishAffinity();
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
                    if(myFile.getName().endsWith(".mp3") && !myFile.getName().startsWith(".")){
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

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    private void showMyMusic(){
        String [] items = new String[mySongs.size()];
        for (int i=0; i<mySongs.size();i++){
            String name = mySongs.get(i).getName().toString().replace(".mp3", "");
            items [i] = (i+1)+".    "+name;
        }
        adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, Player.class);
                intent.putExtra("songList", mySongs);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
        playAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Player.class);
                String currentSong = listView.getItemAtPosition(0).toString();
                intent.putExtra("songList", mySongs);
                intent.putExtra("currentSong", currentSong);
                intent.putExtra("position", 0);
                startActivity(intent);
            }
        });
    }
    @NonNull
    @Override
    public Loader<ArrayList<File>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<ArrayList<File>>(this) {
            @Override
            protected void onStartLoading() {
                progressBar.setVisibility(View.VISIBLE);
                forceLoad();
            }

            @Nullable
            @Override
            public ArrayList<File> loadInBackground() {
                mySongs = fetchSongs(Environment.getExternalStorageDirectory());
                return mySongs;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<File>> loader, ArrayList<File> data) {
        progressBar.setVisibility(View.INVISIBLE);
        if (data.size() == 0) {
            //showErrorMessage();
            empty.setVisibility(View.VISIBLE);
        } else {
            /*mSearchResultsTextView.setText(data);
            showJsonDataView();*/
            Log.d("Control", "onLoadFinished: No. of songs "+data.size());
            showMyMusic();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<File>> loader) {
        /*
         * We aren't using this method in our example application, but we are required to Override
         * it to implement the LoaderCallbacks<String> interface
         */
    }
}