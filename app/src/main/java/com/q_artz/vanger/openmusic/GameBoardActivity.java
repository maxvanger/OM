package com.q_artz.vanger.openmusic;

import android.animation.ValueAnimator;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.q_artz.vanger.openmusic.DataModel.Puzzle;
import com.q_artz.vanger.openmusic.MVP.GameBoardView;
import com.q_artz.vanger.openmusic.MVP.PuzzlePresenter;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.content.DialogInterface.BUTTON_NEUTRAL;
import static android.content.DialogInterface.BUTTON_POSITIVE;

public class GameBoardActivity extends AppCompatActivity implements GameBoardView, DialogScore.DialogScoreAction {

    private GridLayout grid;

    private PuzzlePresenter mPresenter;
    private Bitmap puzzleImage;
    private ListView listGuessed;
    private ArrayAdapter<String> adapterGuessed;
    private List<String> guessedPuzzles;
    private int countPuzzles;
    private TextView attemptsCount;
    private ImageView showHint;
    private boolean isHint;
    private Bitmap[] hintImages;
    private Drawable cardFrontDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_board_layout);

        attemptsCount = (TextView)findViewById(R.id.attempts_count);
        showHint = (ImageView)findViewById(R.id.btn_hint);
        cardFrontDrawable = getResources().getDrawable(R.drawable.q_empty1);
        hintImages = getHintImages();

        mPresenter = PuzzlePresenter.getInstance(this);
        countPuzzles = mPresenter.getX()*mPresenter.getY()/2;
        mPresenter.loadGame();

        grid = (GridLayout) findViewById(R.id.grid_space);
        grid.setColumnCount(mPresenter.getX());

        listGuessed = (ListView)findViewById(R.id.titles_list);
        guessedPuzzles = new ArrayList<>(countPuzzles);
        adapterGuessed = new ArrayAdapter<>(this,R.layout.guessed_item,guessedPuzzles);
        listGuessed.setAdapter(adapterGuessed);

        showHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isHint = !isHint;
                showHint(isHint);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    @Override
    public void onReadyToPlay() {
        //get Bitmap for puzzle image - from Presenter
        puzzleImage = BitmapFactory.decodeResource(getResources(),R.drawable.kartinka500);

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) grid.getLayoutParams();
        float ratio = (float)puzzleImage.getWidth()/puzzleImage.getHeight();
        //DisplayMetrics dm = getResources().getDisplayMetrics();
        //if ((dm.heightPixels-72) * ratio > dm.widthPixels) {
        int h = grid.getHeight();
        int w = grid.getWidth();
        if (h*ratio > w) {
            lp.width = w; //Math.round(w * 0.9f);
            lp.height = Math.round(lp.width / ratio);
        } else {
            lp.height = h; //Math.round(h * 0.9f);
            lp.width = Math.round(lp.height * ratio);
        }
        grid.setLayoutParams(lp);
        grid.requestLayout();

        fillGrid(mPresenter.getX()*mPresenter.getY());
    }

    private void fillGrid(int count) {
        ViewGroup viewGroup;
        View frontView;
        ImageView backView;
        isHint = false;

        int dX = puzzleImage.getWidth() / mPresenter.getX();
        int dY = puzzleImage.getHeight() / mPresenter.getY();

        for (int i = 0; i < count; i++) {
            viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.item_lock_view, grid, false);
            frontView = viewGroup.getChildAt(0);
            frontView.setId(i);
            frontView.setOnTouchListener(new PuzzleOnTouchListener());
            setBackground(frontView,cardFrontDrawable);

            backView = (ImageView) viewGroup.getChildAt(1);
            backView.setImageBitmap(getPuzzleImage(puzzleImage, i, dX, dY));
//            backView.setDrawingCacheEnabled(true);
//            backView.buildDrawingCache();

            grid.addView(viewGroup);
        }
    }

    public void replayGame(){
        ViewGroup viewGroup;
        View frontView;
        ImageView backView;

        //get new PuzzleImage
        guessedPuzzles.clear();
        adapterGuessed.notifyDataSetChanged();

        for(int i=0;i<grid.getChildCount();i++){
            viewGroup = (ViewGroup) grid.getChildAt(i);
            frontView = viewGroup.getChildAt(0);
            backView = (ImageView) viewGroup.getChildAt(1);
            flipViews(backView,frontView);
            //backView.setImageBitmap(getPuzzleImage(puzzleImage, i, dX, dY));
        }
    }

    private class PuzzleOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mPresenter.touch(view.getId());
                    return true;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    mPresenter.untouch(view.getId());
                    return true;
            }
            return true;
        }
    }

    @Override
    public void touchPuzzleAnim(int index) {
        grid.getChildAt(index)
                .animate().scaleX(1.12f).scaleY(1.12f).setDuration(100).start();
    }

    @Override
    public void untouchPuzzleAnim(int index) {
        grid.getChildAt(index)
                .animate().scaleX(1f).scaleY(1f).setDuration(100).start();
    }

    @Override
    public void bingo(int attemptsCount) {
        FragmentManager fm = getSupportFragmentManager();
        DialogScore dialogScore = DialogScore.newInstace(attemptsCount+countPuzzles,countPuzzles);
        dialogScore.show(fm,"DIALOG SCORE");
    }


    @Override
    public void setAlbumTitle(String title) {

    }

    @Override
    public void setCoupleTitle(String title) {

    }

    @Override
    public void showHintView(String text) {

    }

    @Override
    public void updateAttempts(int count){
        attemptsCount.setText(Integer.toString(count));
    }

    @Override
    public void guessedRight(String title) {
        guessedPuzzles.add(title);
        adapterGuessed.notifyDataSetChanged();
        listGuessed.smoothScrollToPosition(guessedPuzzles.size());
    }

    @Override
    public void flipPuzzle(int index) {
        ViewGroup vg = (ViewGroup) grid.getChildAt(index);
        flipViews(vg.getChildAt(0),vg.getChildAt(1));
    }

    private ValueAnimator flipViews(View front, View back) {
        ValueAnimator flipAnimator = ValueAnimator.ofFloat(0f, 1f);
//            int direction = (int) (Math.random() * 100) % 4;
        flipAnimator.addUpdateListener(new FlipListener(front, back, 1));
        flipAnimator.setDuration(800);
        flipAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        //flipAnimator.setEvaluator(new FloatEvaluator());
        flipAnimator.start();
        return flipAnimator;
    }

    private void setBackground(View view, Drawable cardFrontDrawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(cardFrontDrawable);
        } else
            view.setBackgroundDrawable(cardFrontDrawable);
    }

    private Bitmap getPuzzleImage(Bitmap source, int count, int sizeX, int sizeY) {
        int x = count % mPresenter.getX();
        int y = count / mPresenter.getX();
        Bitmap result = Bitmap.createBitmap(source, x * sizeX, y * sizeY, sizeX, sizeY);
        return result;
    }

    private void showHint(boolean flag) {
        ViewGroup vg;
        Drawable drw;
        for (int i = 0; i < grid.getChildCount(); i++) {
            vg = (ViewGroup) grid.getChildAt(i);
            View frontView = vg.getChildAt(0);

            if (flag) drw = new BitmapDrawable(hintImages[mPresenter.mappingIndex(i) % hintImages.length]);
            else drw = cardFrontDrawable;

            setBackground(frontView, drw);
        }
    }

    //get hint images from Assets folder /hints/
    private Bitmap[] getHintImages() {
        Bitmap[] hintImagesArray = new Bitmap[11];
        AssetManager assets = this.getAssets();
        try {
            String[] files = assets.list("hints");
            hintImagesArray = new Bitmap[files.length];
            int i = 0;
            for (String fname : files) {
                InputStream stream = assets.open("hints/" + fname);
                hintImagesArray[i++] = BitmapFactory.decodeStream(stream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hintImagesArray;
    }

    @Override
    public void dialogAction(int action) {
        switch (action) {
            case BUTTON_POSITIVE:
                mPresenter.nextGame();
                break;
            case BUTTON_NEUTRAL:
                mPresenter.replayGame();
        }
    }
}
