package com.q_artz.vanger.openmusic.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Vanger on 18.01.2017.
 */

public interface SoundCloudService {
    @GET("/playlists/{playlist}?client_id={clientId}")
    Call<PlaylistSC> getOpenGoldberg();

    @GET("/tracks?client_id=" + Config.CLIENT_ID)
    Call<List<Track>> getRecentTracks(@Query("created_at") String date);
}
