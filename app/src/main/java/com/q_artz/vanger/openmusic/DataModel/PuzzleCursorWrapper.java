package com.q_artz.vanger.openmusic.DataModel;

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 * Created by vanger on 14/11/16.
 */

public class PuzzleCursorWrapper extends CursorWrapper {
    public PuzzleCursorWrapper(Cursor c){
        super(c);
    }

    public Puzzle getPuzzle(){
        String path = getString(getColumnIndex(PuzzleTable.PATH));
        Puzzle pzl = new Puzzle(path);

        pzl.setId(getInt(getColumnIndex(PuzzleTable.ID)));
        pzl.setTitle(getString((getColumnIndex(PuzzleTable.TITLE))));
        pzl.setSoundPath(path);
        pzl.setPathType(getString(getColumnIndex(PuzzleTable.PATH_TYPE)));

        pzl.setAlbumId(getInt(getColumnIndex(PuzzleTable.ALBUM)));
        pzl.setLinkId(getInt(getColumnIndex(PuzzleTable.LINK)));

        return pzl;
    }
}
