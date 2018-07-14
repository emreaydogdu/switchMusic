package com.emreay.music.lastfm.rest.service;

import android.support.annotation.Nullable;

import com.emreay.music.lastfm.rest.model.LastFmAlbum;
import com.emreay.music.lastfm.rest.model.LastFmArtist;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public interface LastFMService {
    String API_KEY = "5a6f7db5c77f3e8392e7b91c23388e00";
    String BASE_QUERY_PARAMETERS = "?format=json&autocorrect=1&api_key=" + API_KEY;

    @GET(BASE_QUERY_PARAMETERS + "&method=album.getinfo")
    Call<LastFmAlbum> getAlbumInfo(@Query("album") String albumName, @Query("artist") String artistName, @Nullable @Query("lang") String language);

    @GET(BASE_QUERY_PARAMETERS + "&method=artist.getinfo")
    Call<LastFmArtist> getArtistInfo(@Query("artist") String artistName, @Nullable @Query("lang") String language, @Nullable @Header("Cache-Control") String cacheControl);
}