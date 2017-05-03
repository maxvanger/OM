package com.q_artz.vanger.openmusic.network;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Vanger on 20.01.2017.
 */

public class PlaylistSC {
    @SerializedName("id")
    private int mId;

    @SerializedName("user_id")
    private int mUserId;

    @SerializedName("track_count")
    private int mTrackCount;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("tracks")
    private ArrayList<Track> mTracks;

    public int getId() {
        return mId;
    }

    public int getUserId() {
        return mUserId;
    }

    public int getTrackCount() {
        return mTrackCount;
    }

    public String getDescription() {
        return mDescription;
    }

    public ArrayList<Track> getTracks() {
        return mTracks;
    }
}
