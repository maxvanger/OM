package com.q_artz.vanger.openmusic.Players;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by Vanger on 03.05.2017.
 */

public class SoundMediaPlayer implements ISound {
    private Context ctx;
    private String uri;
    private MediaPlayer player;

    public SoundMediaPlayer(Context ctx, String uri){
        this.ctx = ctx;
        this.uri = uri;
    }

    @Override
    public void init() {
        player = new MediaPlayer();
        try {
            player.setDataSource(ctx, Uri.parse(uri));
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void play() {
        player.start();
    }

    @Override
    public void stop() {
        player.pause();
        player.seekTo(0);
    }

    @Override
    public void release() {
        player.release();
    }
}
