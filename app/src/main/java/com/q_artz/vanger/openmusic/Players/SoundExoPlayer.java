package com.q_artz.vanger.openmusic.Players;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.q_artz.vanger.openmusic.network.Config;

import static com.q_artz.vanger.openmusic.ScrollingActivity.mUserAgent;

/**
 * Created by Vanger on 03.05.2017.
 */

public class SoundExoPlayer implements ISound {
    private String uri;
    private Context ctx;
    private ExoPlayer player;

    public SoundExoPlayer(Context ctx, String uri){
        this.ctx = ctx;
        this.uri = uri;
    }

    @Override
    public void init() {
        DefaultLoadControl mLoadControl = new DefaultLoadControl();
        TrackSelector mTrackSelector = new DefaultTrackSelector();
        DataSource.Factory mDataSource = new DefaultHttpDataSourceFactory(mUserAgent,null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,true);
        Handler mHandler = new Handler();
        MediaSource mediaSource = new ExtractorMediaSource(
                Uri.parse(uri),
                mDataSource, Mp3Extractor.FACTORY,mHandler,null);
        player = ExoPlayerFactory.newSimpleInstance(ctx, mTrackSelector, mLoadControl);
        player.prepare(mediaSource);
    }

    @Override
    public void play() {
        player.setPlayWhenReady(true);
    }

    @Override
    public void stop() {
        player.setPlayWhenReady(false);
        player.seekTo(0);
    }

    @Override
    public void release() {
        player.release();
    }
}
