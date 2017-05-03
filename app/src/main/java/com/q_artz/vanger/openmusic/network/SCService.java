package com.q_artz.vanger.openmusic.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Vanger on 18.01.2017.
 */

public interface SCService {
    @GET("/playlists/2038701?client_id="+ Config.CLIENT_ID)
    Call<PlaylistSC> getOpenGoldberg();

    @GET("/playlists/85897182?client_id="+ Config.CLIENT_ID)
    Call<PlaylistSC> getTchaikovskySeasons();

    @GET("/playlists/28086436?client_id="+ Config.CLIENT_ID)
    Call<PlaylistSC> getProkofievVisions();

    @GET("/tracks?client_id=" + Config.CLIENT_ID)
    Call<List<Track>> getRecentTracks(@Query("created_at") String date);
}
