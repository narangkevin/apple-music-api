package com.example.mtelapplemusicapi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String uri_prefix = "https://itunes.apple.com/search?term=";
    private String uri_postfix = "&limit=50&media=music";

    EditText editText;
    Button searchBtn;

    ImageView play_pause_img;
    ImageView albumArt_player;
    TextView nowPlaying_player;
    SeekBar seekbar;
    MediaPlayer mediaPlayer;
    Handler handler = new Handler();

    RecyclerView recyclerView;
    List<SongModel> songList = new ArrayList<>();

    private String uri = uri_prefix+uri_postfix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        editText = findViewById(R.id.search_edit_txt);
        searchBtn = findViewById(R.id.search_btn);
        albumArt_player = findViewById(R.id.alumArt_player);
        nowPlaying_player = findViewById(R.id.nowPlaying);

        play_pause_img = findViewById(R.id.play_pause_icon);
        seekbar = findViewById(R.id.seekbar);
        mediaPlayer = new MediaPlayer();

        seekbar.setMax(100);

        play_pause_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    handler.removeCallbacks(updater);
                    mediaPlayer.pause();
                    play_pause_img.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                } else {
                    mediaPlayer.start();
                    play_pause_img.setImageResource(R.drawable.ic_baseline_pause_24);
                    updateSeekBar();
                }
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songList.clear();
                String text = editText.getText().toString();
                String finalText = text.replaceAll(" ", "+").toLowerCase();
                uri = uri_prefix+finalText+uri_postfix;

                GetData getData = new GetData();
                getData.execute();
            }
        });
    }

    private void prepareMediaPlayer(String trackURL, String trackName, String imageURL) {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
            mediaPlayer.setDataSource(trackURL);
            mediaPlayer.prepare();
            nowPlaying_player.setText(trackName);
            Picasso.get().load(imageURL).into(albumArt_player);
            play_pause_img.setImageResource(R.drawable.ic_baseline_pause_24);
            mediaPlayer.start();

        } catch (Exception exception) {
            Toast.makeText(this, "exception.getMessage()", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSeekBar() {
        if (mediaPlayer.isPlaying()) {
            seekbar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100 ));
            handler.postDelayed(updater, 1000);
        }
    }

    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
            long currentDuration = mediaPlayer.getCurrentPosition();
        }
    };

    public class GetData extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {

            String current = "";

            try {
                URL url;
                HttpURLConnection urlConnection = null;

                try {
                    url = new URL(uri);
                    urlConnection = (HttpURLConnection) url.openConnection();

                    InputStream is = urlConnection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);

                    int data = isr.read();
                    while (data != -1){
                        current += (char) data;
                        data = isr.read();
                    }

                    return current;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return current;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                for (int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    SongModel model = new SongModel();
                    model.setArtistName(jsonObject1.getString("artistName"));
                    model.setArtworkUrl100(jsonObject1.getString("artworkUrl100"));
                    model.setCollectionName(jsonObject1.getString("collectionName"));
                    model.setTrackName(jsonObject1.getString("trackName"));
                    model.setPreviewUrl(jsonObject1.getString("previewUrl"));

                    songList.add(model);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            PopulateRecyclerView(songList);
        }
    }

    private void PopulateRecyclerView(List<SongModel> songList) {
        SongAdapter songAdapter = new SongAdapter(this, songList, new SongAdapter.ItemClickListener() {
            @Override
            public void onItemClick(SongModel details) {
                passPreviewURL(details.previewUrl, details.trackName, details.artworkUrl100);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(songAdapter);
    }

    private void passPreviewURL(String previewURL, String nowPlaying, String albumArt) {
        System.out.println("previewURL ---> "+previewURL+" ---> "+nowPlaying+" ---> "+albumArt);
        prepareMediaPlayer(previewURL, nowPlaying, albumArt);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.stop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mediaPlayer.stop();
    }
}
