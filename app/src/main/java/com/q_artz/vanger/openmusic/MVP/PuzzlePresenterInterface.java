package com.q_artz.vanger.openmusic.MVP;

import com.q_artz.vanger.openmusic.MVP.ModelGame;

import java.util.List;

/**
 * Created by Vanger on 23.02.2017.
 */

public interface PuzzlePresenterInterface {
    void touch(int index);
    void untouch(int index);
    ModelGame getGame();

    void replayGame();
    void nextGame();
}
