package com.q_artz.vanger.openmusic.MVP;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.MediaCodecAudioRenderer;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
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
import com.q_artz.vanger.openmusic.DialogScore;
import com.q_artz.vanger.openmusic.Prefs;
import com.q_artz.vanger.openmusic.network.Config;
import com.q_artz.vanger.openmusic.network.PlaylistSC;
import com.q_artz.vanger.openmusic.network.SCService;
import com.q_artz.vanger.openmusic.network.SoundCloud;
import com.q_artz.vanger.openmusic.network.Track;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static com.q_artz.vanger.openmusic.ScrollingActivity.mUserAgent;

/**
 * Created by Vanger on 24.02.2017.
 */

public class PuzzlePresenter implements PuzzlePresenterInterface {

    private String TAG = PuzzlePresenter.class.getName();
    private DefaultLoadControl mLoadControl;
    private TrackSelector mTrackSelector;
    private DataSource.Factory mDataSource;

    private Prefs mPrefs;
    private Context mApplicationContext;
    private Handler mHandler;
    private static GameBoardView mView;
    private int mX;
    private int mY;
    private int firstIndex;
    private boolean isFirstPuzzle;
    private boolean isBingo;
    private int countBingo;
    private int mCount;
    private int page;
    private int attemptsCount;

    private List<Track> mTracks;
    private List<Track> mCustomTrackList;
    private int[] mappingArray;
    private ExoPlayer[] mExoPlayers;
    private ExoPlayer mPlayer;
    private MediaSource[] mediaSources;

    public static PuzzlePresenter getInstance(GameBoardView view){
        mView = view;
        return new PuzzlePresenter((Activity) view);
    }

    PuzzlePresenter(Activity activity) {
        mView = (GameBoardView) activity;
        mApplicationContext = activity.getApplicationContext();

        mPrefs = Prefs.get(activity);
        //init mX and mY by Prefs's values;
        page = 0;   // get it from Prefs
        mTracks = new ArrayList<>();
        mX = 4;
        mY = 5;
    }

    private void initGame(){
        isFirstPuzzle = true;
        isBingo = false;
        mCount = mX*mY/2;
        countBingo = mCount;
        attemptsCount = 0;
        mView.updateAttempts(attemptsCount);
        mappingArray = getRandomMapArray(mCount);
    }

    public void loadGame(){
        initGame();

        mLoadControl = new DefaultLoadControl();
        mTrackSelector = new DefaultTrackSelector();
        mDataSource = new DefaultHttpDataSourceFactory(mUserAgent,null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,true);
        mHandler = new Handler();
//        mediaSources = new MediaSource[mCount];
//        mPlayer = ExoPlayerFactory.newSimpleInstance(mApplicationContext, mTrackSelector, mLoadControl);

        loadSoundCloudList();
    }

    public void replayGame(){
        stopPlayer(firstIndex);
        initGame();
        mView.replayGame();
    }

    public void nextGame(){
        for(int i=0;i<mExoPlayers.length;i++){
            mExoPlayers[i].release();
        }
        page++;
        mCustomTrackList = getCustomList(mTracks,mCount,page);
        preparePlayers(mCustomTrackList);
        replayGame();
    }

    public int getX() { return mX; }
    public int getY() { return mY; }

    @Override
    public void touch(int index) {


        //stop guessed music pair
        if (isBingo) {
            isBingo=false;
            stopPlayer(firstIndex);
            //releasePlayer(firstIndex);
        }

        mView.touchPuzzleAnim(index);
        startPlayer(index);

        if (isFirstPuzzle) {
            firstIndex = index;
        } else {
            if ((index!=firstIndex)&&(mappingArray[firstIndex] == mappingArray[index])) {
                bingoLocal(index);
                countBingo--;
                if (countBingo == 0) bingoGlobal();
            }
        }
    }

    @Override
    public void untouch(int index) {

        if (isBingo){
            //isBingo = false;
            isFirstPuzzle = true;
            firstIndex = index; //for stopping music when the next ontouch happens
        } else {
            stopPlayer(index);
            if (isFirstPuzzle) {
                isFirstPuzzle = false;
            } else {
                isFirstPuzzle = true;
                attemptsCount++;
                mView.updateAttempts(attemptsCount);
                mView.untouchPuzzleAnim(index);
                mView.untouchPuzzleAnim(firstIndex);
            }
        }
    }

    private void bingoLocal(int index) {
        isBingo = true;     // may be need "bingoState" as boolean (or: int bingoIndex = firstIndex)
        mView.untouchPuzzleAnim(index);
        mView.untouchPuzzleAnim(firstIndex);
        mView.flipPuzzle(index);
        mView.flipPuzzle(firstIndex);

        int trackIndex = mappingArray[index];
        String[] temp = mCustomTrackList.get(trackIndex).getTitle().split("-");
        mView.guessedRight(temp[3]);
    }

    private void bingoGlobal(){
        mView.bingo(attemptsCount);
    }

    @Override
    public ModelGame getGame() {
        return null;
    }

    private void loadSoundCloudList(){
        SCService scService = SoundCloud.getService();
        scService.getOpenGoldberg().enqueue(new Callback<PlaylistSC>() {
            @Override
            public void onResponse(Call<PlaylistSC> call, Response<PlaylistSC> response) {
                if (response.isSuccessful()){
                    PlaylistSC mPlaylistSC = response.body();
                    mTracks.clear();
                    mTracks = mPlaylistSC.getTracks();
                    mView.onReadyToPlay();  //??? need some time to prepare exoMediaPlayers
                    mCustomTrackList = getCustomList(mTracks,mCount,page);
                    preparePlayers(mCustomTrackList);
                } else {
                    Log.d(TAG,"Error code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PlaylistSC> call, Throwable t) {
                Log.d(TAG,"Network error:" + t.getMessage());
            }
        });
    }

    private List<Track> getCustomList(List<Track> fullList, int count,int page){
        List<Track> custom = new ArrayList<>();
        int i = page*count;
        if ((i+count)>fullList.size()) { i = fullList.size() - count;}
        for(int j=0;j<count;j++){
            custom.add(fullList.get(i+j));
        }
        return custom;
    }

    private void preparePlayers(List<Track> playList){
        int i=0;
        mExoPlayers = new ExoPlayer[mCount];

        for(Track track:playList){
            DataSource.Factory dataSource = new DefaultHttpDataSourceFactory(mUserAgent,null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                    DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,true);
            LoadControl loadControl = new DefaultLoadControl();

            ExoPlayer mp = ExoPlayerFactory.newSimpleInstance(mApplicationContext, mTrackSelector, loadControl);

            MediaSource mediaSource = new ExtractorMediaSource(
                    Uri.parse(track.getStreamUrl() + "?client_id=" + Config.CLIENT_ID),
                    dataSource, Mp3Extractor.FACTORY,mHandler,null);
           // mediaSources[i]=mediaSource;


/*            Mp3Extractor mp3Extractor = new Mp3Extractor();
            dataSource = new DefaultUriDataSource("musicStream", transferListener);
            sampleSource = new ExtractorSampleSource(uri, dataSource, mp3Extractor, 1, BUFFER_SIZE);
            MediaCodecAudioRenderer audioRenderer = new MediaCodecAudioRenderer(sampleSource, null, true);
            ExoPlayer pl = ExoPlayerFactory.newSimpleInstance();
            pl.prepare(audioRenderer);*/
            mp.addListener(new ExoPlayer.EventListener() {
                @Override
                public void onTimelineChanged(Timeline timeline, Object manifest) {

                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                }

                @Override
                public void onLoadingChanged(boolean isLoading) {
//                    Log.d(TAG,"Loading Change: "+isLoading);
                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {
                    Log.d(TAG,"Error with ExoPlayer loading: "+error + "/" + error.getCause());
                }

                @Override
                public void onPositionDiscontinuity() {

                }
            });
            mp.prepare(mediaSource);
            mExoPlayers[i]=mp;
            i++;
        }
    }

    private int[] getRandomMapArray(int count){
        List<Integer> list = new ArrayList<>(count*2);
        int[] mapArray = new int[count*2];
        for (int i=0;i<count;i++){
            list.add(i); list.add(i);
        }
        Collections.shuffle(list);
        for(int i=0;i<count*2;i++) {mapArray[i] = list.get(i);}
        return mapArray;
    }

    private void startPlayer(int index){
        int playerIndex = mappingArray[index];
        mExoPlayers[playerIndex].setPlayWhenReady(true);
    }

    private void stopPlayer(int index){
        int playerIndex = mappingArray[index];
        mExoPlayers[playerIndex].seekTo(0);
        mExoPlayers[playerIndex].setPlayWhenReady(false);
    }

    public void onStop(){
        stopPlayer(firstIndex);
    }

    public int mappingIndex(int index){ return mappingArray[index];}

    private void releasePlayer(int index){
        int playerIndex = mappingArray[index];
        mExoPlayers[playerIndex].release();
    }

/*  // in case with one ExoPlayer for all puzzles
    private void startPlayer2(int index){
        int i = mappingArray[index];
        if (i != mappingArray[firstIndex]) {
            mPlayer.prepare(mediaSources[i]);
        }
        mPlayer.setPlayWhenReady(true);
    }

    private void stopPlayer2(int index){
        mPlayer.setPlayWhenReady(false);
        mPlayer.seekTo(0);
    }

    private void releasePlayer2(int index){ mPlayer.release();}
*/

}
