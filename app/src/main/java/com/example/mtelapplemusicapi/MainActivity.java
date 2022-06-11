package com.example.mtelapplemusicapi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
        SongAdapter songAdapter = new SongAdapter(this, songList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(songAdapter);
    }
}