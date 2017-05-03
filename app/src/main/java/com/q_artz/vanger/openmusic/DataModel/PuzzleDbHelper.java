package com.q_artz.vanger.openmusic.DataModel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.q_artz.vanger.openmusic.DataModel.PuzzleDbSchema.PuzzleTable;
import com.q_artz.vanger.openmusic.DataModel.PuzzleDbSchema.AlbumTable;

/**
 * Created by vanger on 11/11/16.
 */

public class PuzzleDbHelper extends SQLiteOpenHelper {

    public PuzzleDbHelper(Context ctx){
        super(ctx,PuzzleDbSchema.DB_NAME, null, PuzzleDbSchema.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + AlbumTable.TABLE_NAME+"("+
                " _id integer primary key autoincrement, " +
                AlbumTable.TITLE + ", " +
                AlbumTable.ABOUT + ", " +
                AlbumTable.AUTHOR + ", " +
                AlbumTable.IMAGE_URI +
                ")"
        );
        db.execSQL("create table " + PuzzleTable.TABLE_NAME+"("+
                " _id integer primary key autoincrement, " +
                PuzzleTable.TITLE + ", " +
                PuzzleTable.PATH + ", " +
                PuzzleTable.PATH_TYPE + ", " +
                PuzzleTable.ALBUM + " INTEGER, " +
                PuzzleTable.LINK + " INTEGER " +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {

    }
}
