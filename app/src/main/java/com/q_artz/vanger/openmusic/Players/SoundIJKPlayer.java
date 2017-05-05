package com.q_artz.vanger.openmusic.Players;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static com.google.android.exoplayer2.mediacodec.MediaCodecInfo.TAG;

/**
 * Created by Vanger on 03.05.2017.
 */

public class SoundIJKPlayer implements ISound {
    private String TAG = SoundIJKPlayer.class.getName();
    private Context ctx;
    private String uri;
    private IjkMediaPlayer player;
    private boolean isPrepared = false;

    public SoundIJKPlayer(Context ctx, String uri){
        this.ctx = ctx;
        this.uri = uri;
    }

    @Override
    public void init() {
        player = new IjkMediaPlayer();
        try {
            player.setDataSource(ctx, Uri.parse(uri));
            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"Error player preparing:" + e.getCause());
        }
        player.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                isPrepared = true;
            }
        });
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
