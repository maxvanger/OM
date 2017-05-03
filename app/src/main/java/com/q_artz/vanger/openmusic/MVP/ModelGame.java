package com.q_artz.vanger.openmusic.MVP;

import com.q_artz.vanger.openmusic.DataModel.Puzzle;
import java.util.List;

/**
 * Created by Vanger on 24.02.2017.
 */

public class ModelGame {
    private int mId;
    private String mTitle;              // album title "Seasons"
    private String mImageUri;           // puzzled image (or album-description image&?)
    private String mDescription;        // "Seasons - writing during 1872 blablabla"
    private List<Puzzle> mPuzzleList;

    private Puzzle mCurrentPuzzle;
    private int mPairViewIndex;
    private boolean isSecondOfTwo;
    private int bingoCount = mPuzzleList.size()/2;

    public String getTitle() { return mTitle; }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getImageUri() {
        return mImageUri;
    }

    public void setImageUri(String imageUri) {
        mImageUri = imageUri;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public List<Puzzle> getPuzzleList() {
        return mPuzzleList;
    }

    public void setPuzzleList(List<Puzzle> puzzleList) {
        mPuzzleList = puzzleList;
    }
}
