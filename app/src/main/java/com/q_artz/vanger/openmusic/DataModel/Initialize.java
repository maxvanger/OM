package com.q_artz.vanger.openmusic.DataModel;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.util.Log;

import com.q_artz.vanger.openmusic.R;

import java.io.IOException;

import com.q_artz.vanger.openmusic.DataModel.PuzzleDbSchema.PuzzleTable;
import com.q_artz.vanger.openmusic.DataModel.PuzzleDbSchema.AlbumTable;
import com.q_artz.vanger.openmusic.network.PlaylistSC;
import com.q_artz.vanger.openmusic.network.SCService;
import com.q_artz.vanger.openmusic.network.SoundCloud;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Vanger on 19.05.2017.
 */

public class Initialize {
    static String TAG = Initialize.class.getName();
    public static final String SC_GOLDBERG = "2038701";
    public static final String[] SC_ARRAY = {"2038701","85897182","28086436"};

    static public void assetsInsertToProvider(Context ctx){
        String LOCKS_FOLDER = "/locks";
        String KEYS_FOLDER = "/keys";
        AssetManager assets = ctx.getAssets();
        ContentValues cv = new ContentValues();
        ContentResolver cr = ctx.getContentResolver();
        Uri albumUri = Uri.parse(PuzzleDbSchema.CONTENT_URI+AlbumTable.TABLE_NAME);
        Uri puzzleUri = Uri.parse(PuzzleDbSchema.CONTENT_URI+PuzzleTable.TABLE_NAME);
        try{
            String[] dirs = assets.list("");
            for(int d=1;d<3;d++){ //d<dirs.length - But filter assets folder (bgin from digit)
                // insert new Album from Assets directory
                String[] albumInfo = dirs[d].split("_");
                cv.put(AlbumTable.AUTHOR,albumInfo[1]);
                cv.put(AlbumTable.TITLE,albumInfo[2]);
                cv.put(AlbumTable.IMAGE_URI,dirs[d]+"/images");
                if (Integer.valueOf(albumInfo[0])==2) {
                    cv.put(AlbumTable.ABOUT,ctx.getResources().getText(R.string.the_seasons).toString());
                } else if (Integer.valueOf(albumInfo[0])==1){
                    cv.put(AlbumTable.ABOUT,ctx.getResources().getText(R.string.bach_wtc).toString());
                }
                Uri uriAlbum = cr.insert(albumUri,cv);
                long albumId = ContentUris.parseId(uriAlbum);

                // insert new puzzles for the current Album
                String[] locks = assets.list(dirs[d]+LOCKS_FOLDER);
                String[] keys = assets.list(dirs[d]+KEYS_FOLDER);
                for(String lock:locks){
                    cv.clear();
                    cv.put(PuzzleTable.ALBUM,Long.toString(albumId)); //ALBUM_ID
                    cv.put(PuzzleTable.LINK,"-1");   // Its a Lock Puzzle
                    cv.put(PuzzleTable.PATH,dirs[d]+LOCKS_FOLDER+"/"+lock);
                    cv.put(PuzzleTable.PATH_TYPE,"assets");
                    cv.put(PuzzleTable.TITLE,getNameString(lock));
                    Uri uriPuzzle = Uri.parse(puzzleUri+"/"+Long.toString(albumId));
                    Uri newPuzzleUri = cr.insert(uriPuzzle,cv);
                    long lockId = ContentUris.parseId(newPuzzleUri);

                    for(String key:keys){
                        if (key.startsWith(lock.substring(0,2))) {
                            cv.clear();
                            cv.put(PuzzleTable.ALBUM,Long.toString(albumId)); //ALBUM_ID
                            cv.put(PuzzleTable.LINK,Long.toString(lockId));   // Its a key Puzzle
                            cv.put(PuzzleTable.PATH,dirs[d]+KEYS_FOLDER+"/"+key);
                            cv.put(PuzzleTable.PATH_TYPE,"assets");
                            cv.put(PuzzleTable.TITLE,getNameString(key));
                            cr.insert(uriPuzzle,cv);
                        }
                    }
                }
                cv.clear();
            }
        } catch (IOException ioe) {
            Log.e(TAG,"insertAssetsProvider() error");}
    }

    static private String getNameString(String filename){
        String res = filename.split("\\.")[0];
        StringBuilder result = new StringBuilder(res.length());
        for (int i = 0; i < res.length(); i++) {
            char c = res.charAt(i);
            if (c>'9'||c==' '||c=='-') {
                result.append(c);
            }
        }
        return result.toString();
        //return res;
    }

    private void loadSoundCloudList(){
        SCService scService = SoundCloud.getService();
        scService.getOpenGoldberg().enqueue(new Callback<PlaylistSC>() {
            @Override
            public void onResponse(Call<PlaylistSC> call, Response<PlaylistSC> response) {
                if (response.isSuccessful()){
                    PlaylistSC mPlaylistSC = response.body();
      /*              mTracks.clear();
                    mTracks = mPlaylistSC.getTracks();
                    mView.onReadyToPlay();  //??? need some time to prepare exoMediaPlayers
                    mCustomTrackList = getCustomList(mTracks,mCount,page);
                    preparePlayers(mCustomTrackList);*/
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
}
