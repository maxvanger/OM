package com.q_artz.vanger.openmusic.network;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Vanger on 18.01.2017.
 */

public class Track {

    @SerializedName("title")
    private String mTitle;

    @SerializedName("id")
    private int mId;

    @SerializedName("stream_url")
    private String mStreamUrl;

    @SerializedName("artwork_url")
    private String mArtworkUrl;

    @SerializedName("label_name")
    private String mComposer;

    public String getTitle() { return mTitle; }

    public int getId() { return mId; }

    public String getStreamUrl() { return mStreamUrl; }

    public String getArtworkUrl() { return mArtworkUrl; }

    public String getComposer() {
        return mComposer;
    }
}
