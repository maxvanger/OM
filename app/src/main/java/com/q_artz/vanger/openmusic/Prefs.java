package com.q_artz.vanger.openmusic;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Vanger on 20.12.2016.
 */

public class Prefs {
    public static final String TAG = "Settings Activity";
    public static final String PREFS_NAME = "settings";
//    public static final String STRING_COLUMN = "columns";
//    public static final String STRING_ROW = "rows";
    public static final String BOARD_SIZE = "boardSize";
    public static final String KIND_CARD = "kindCard";
    public static final String LEVEL_COUNT = "levelCount";
    public static final String AUDIO_LIST = "audioList";
    public static final String USER_CHOICE = "userList";
    public static final String TOTAL_SCORE = "totalScore";
    public static final String ASSETS_DB = "assetsDb";
    private static final String DIALOG_AUDIO_LIST = "MediaStore.Audio";

    private final SharedPreferences prefs;
    private static volatile Prefs singleton;

    private Prefs(Context ctx){
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());

    }

    public static Prefs get(Context ctx){
        if (singleton==null){
            synchronized (Prefs.class){
                singleton = new Prefs(ctx);
            }
        }
        return singleton;
    }

/*
    public boolean getShowColor() {
        return showColor;
    }

    public void setShowColor(boolean showColor) {
        this.showColor = showColor;
        prefs.edit().putBoolean(KEY_COLOR, showColor).apply();
    }
*/
}
