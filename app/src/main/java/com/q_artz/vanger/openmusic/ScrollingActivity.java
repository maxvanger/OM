package com.q_artz.vanger.openmusic;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.q_artz.vanger.openmusic.network.Config;
import com.q_artz.vanger.openmusic.network.PlaylistSC;
import com.q_artz.vanger.openmusic.network.SCService;
import com.q_artz.vanger.openmusic.network.SoundCloud;
import com.q_artz.vanger.openmusic.network.Track;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ScrollingActivity extends AppCompatActivity {

    public static final String TAG = "album recycler";
    public static final String mUserAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:40.0) Gecko/20100101 Firefox/40.0";
    private List<Track> mTracks;
    private PlaylistSC mPlaylistSC;
    private RecyclerView mRecyclerAlbum;
    private AlbumAdapter mAlbumAdapter = new AlbumAdapter();
    private final Map<Integer,MediaPlayer> mPreparedPlayers = new HashMap<>();
    private Handler mHandler;
    private DefaultLoadControl mLoadControl;
    private TrackSelector mTrackSelector;
    private DataSource.Factory mDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        mTracks = new ArrayList<>();
        mHandler = new Handler();
        mLoadControl = new DefaultLoadControl();
        mTrackSelector = new DefaultTrackSelector();
        mDataSource = new DefaultHttpDataSourceFactory(mUserAgent,null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,true);


        SCService scService = SoundCloud.getService();
        scService.getTchaikovskySeasons().enqueue(new Callback<PlaylistSC>() {
            @Override
            public void onResponse(Call<PlaylistSC> call, Response<PlaylistSC> response) {
                if (response.isSuccessful()){
                    mPlaylistSC = response.body();
                    loadTracks(mPlaylistSC.getTracks());
                } else {
                    Log.d(TAG,"Error code " + response.code());
                    ((TextView)findViewById(R.id.error_text)).setText("Error code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PlaylistSC> call, Throwable t) {
                    Log.d(TAG,"Network error:" + t.getMessage());
                    ((TextView)findViewById(R.id.error_text)).setText("Error code " + t.getMessage());
            }
        });

        mRecyclerAlbum = (RecyclerView) findViewById(R.id.recycler_album);
        mRecyclerAlbum.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerAlbum.setAdapter(mAlbumAdapter);
    }

    private void loadTracks(List<Track> tracks){
        mTracks.clear();
        mTracks.addAll(tracks);
        mAlbumAdapter.notifyDataSetChanged();
    }

    private class AlbumHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{
        private ImageView albumImage;
        private ImageView playerImage;
        private TextView albumTitle;
        private TextView albumComposer;
        private MediaPlayer mPlayer;
        private ExoPlayer exoPlayer;

        public AlbumHolder(View itemView) {
            super(itemView);
            albumImage = (ImageView) itemView.findViewById(R.id.item_album_image);
            playerImage = (ImageView) itemView.findViewById(R.id.item_player_image);
            albumTitle = (TextView) itemView.findViewById(R.id.item_album_name);
            albumComposer = (TextView) itemView.findViewById(R.id.item_album_composer);

            itemView.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch(motionEvent.getActionMasked()){
                case MotionEvent.ACTION_DOWN:
                    view.setElevation(8.0f);
//                    mPlayer.start();
                    exoPlayer.setPlayWhenReady(true);
                    break;

                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    view.setElevation(2.0f);
//                    mPlayer.seekTo(0);
//                    mPlayer.pause();
                    exoPlayer.setPlayWhenReady(false);
                    exoPlayer.seekTo(0);
            }
            return true;
        }
    }

    private class AlbumAdapter extends RecyclerView.Adapter<AlbumHolder>{
        @Override
        public AlbumHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater infl = getLayoutInflater();
            View itemView = infl.inflate(R.layout.item_album,parent,false);
            return new AlbumHolder(itemView);
        }

        @Override
        public void onBindViewHolder(AlbumHolder holder, int position) {
//            MediaPlayer mp;
            ExoPlayer mp;
            final Track track = mTracks.get(position);
            final AlbumHolder ah = holder;
            final int pos = position;

            ah.albumTitle.setText(track.getTitle());
            ah.albumComposer.setText(track.getComposer());
            Picasso.with(getApplicationContext())
                    .load(track.getArtworkUrl())
                    .placeholder(R.drawable.icon_kamerton)
                    .into(ah.albumImage);

            if (ah.exoPlayer != null) {
                mp = ah.exoPlayer;
            }
            else
            {
                mp = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), mTrackSelector, mLoadControl);
                ah.exoPlayer = mp;
            }

            MediaSource mediaSource = new ExtractorMediaSource(
                    Uri.parse(track.getStreamUrl() + "?client_id=" + Config.CLIENT_ID),
                    mDataSource, Mp3Extractor.FACTORY,mHandler,null);
            mp.prepare(mediaSource);
            mp.addListener(new ExoPlayer.EventListener() {
                @Override public void onTimelineChanged(Timeline timeline, Object manifest) {

                }
                @Override public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) { }
                @Override
                public void onLoadingChanged(boolean isLoading) {
                    if (isLoading) {
                        Picasso.with(getApplicationContext())
                                .load(R.drawable.ic_media_play)
                                .into(ah.playerImage);
                        //mAlbumAdapter.notifyItemChanged(pos);
                        Log.d("OpenMusic","ExoPlayer onLoadingChanges() : "+pos);
                    }
                }

                @Override public void onPlayerStateChanged(boolean playWhenReady, int playbackState) { }
                @Override public void onPlayerError(ExoPlaybackException error) { Log.d("OpenMusic", "ExoPlayer onPlayerError() "+error.getMessage()); }
                @Override public void onPositionDiscontinuity() { }
            });

            /*
                mp = new MediaPlayer();
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        Picasso.with(getApplicationContext())
                                .load(R.drawable.ic_media_play)
                                .into(ah.playerImage);
                        mAlbumAdapter.notifyItemChanged(pos);
                    }
                });
                try{
                    mp.setDataSource(track.getStreamUrl() + "?client_id=" + Config.CLIENT_ID);
                    mp.prepareAsync();
                } catch (IOException e) { e.printStackTrace(); }
*/

        }

        @Override public int getItemCount() {
            return mTracks.size(); }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
