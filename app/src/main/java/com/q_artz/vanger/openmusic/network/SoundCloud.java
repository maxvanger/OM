package com.q_artz.vanger.openmusic.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Vanger on 18.01.2017.
 */

public class SoundCloud {

    private static final Retrofit RETROFIT = new Retrofit.Builder()
            .baseUrl(Config.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private static final SCService SERVICE = RETROFIT.create(SCService.class);

    public static SCService getService(){
        return SERVICE;
    }
}
