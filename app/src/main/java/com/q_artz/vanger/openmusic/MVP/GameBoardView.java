package com.q_artz.vanger.openmusic.MVP;


import java.util.List;

/**
 * Created by Vanger on 23.02.2017.
 */

public interface GameBoardView {
    void onReadyToPlay();

    void flipPuzzle(int index);
    void touchPuzzleAnim(int index);
    void untouchPuzzleAnim(int index);

    void bingo(int attemptCount);
    void replayGame();

    void setAlbumTitle(String title);
    void setCoupleTitle(String title);
    void showHintView(String text);
    void guessedRight(String title);
    void updateAttempts(int count);
//    void setPuzzleList(List<Puzzle> list);
    
}
