package com.example.mtelapplemusicapi;

public class SongModel {
    String artistName;
    String collectionName;
    String trackName;
    String artworkUrl100;
    String previewUrl;

    public SongModel(String artistName, String collectionName, String trackName, String artworkUrl100, String previewUrl) {
        this.artistName = artistName;
        this.collectionName = collectionName;
        this.trackName = trackName;
        this.artworkUrl100 = artworkUrl100;
        this.previewUrl = previewUrl;
    }

    public SongModel() {
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getArtworkUrl100() {
        return artworkUrl100;
    }

    public void setArtworkUrl100(String artworkUrl100) {
        this.artworkUrl100 = artworkUrl100;
    }
}
