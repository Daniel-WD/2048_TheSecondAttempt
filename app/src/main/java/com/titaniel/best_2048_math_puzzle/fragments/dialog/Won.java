package com.titaniel.best_2048_math_puzzle.fragments.dialog;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.titaniel.best_2048_math_puzzle.MainActivity;
import com.titaniel.best_2048_math_puzzle.R;
import com.titaniel.best_2048_math_puzzle.database.Database;
import com.titaniel.best_2048_math_puzzle.fragments.AnimatedFragment;

public class Won extends AnimatedFragment {

    private View mRoot;

    private ConstraintLayout mLyContainer;

    private ImageView mIvResultBg;
    private View mDivResHor, mDivResVertTop, mDivResVertBottom;
    private TextView mTvResScoreText, mTvResHighscoreText, mTvResHighestTileText, mTvResTileRecordText;
    private TextView mTvHighscoreValue, mTvTileRecordValue, mTvHighestTileValue, mTvScoreValue;

    private ImageView mIvBackground;

    private TextView mTvWon;

    private ImageView mIvMessageBg;
    private TextView mTvMessageText;

    private ImageView mIvBtnContinue, mIvBtnHome, mIvBtnRestart;
    private TextView mTvContinueText, mTvHomeText, mTvRestartText;

    private MainActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_won, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        mActivity = (MainActivity) getActivity();

        //init
        mRoot = getView();
        mLyContainer = mRoot.findViewById(R.id.lyContainer);
        mIvResultBg = mRoot.findViewById(R.id.ivResultBg);
        mDivResHor = mRoot.findViewById(R.id.vDivResHor);
        mDivResVertTop = mRoot.findViewById(R.id.vDivResVertTop);
        mDivResVertBottom = mRoot.findViewById(R.id.vDivResVertBottom);
        mTvResScoreText = mRoot.findViewById(R.id.tvResScoreText);
        mTvResHighscoreText = mRoot.findViewById(R.id.tvResHighscoreText);
        mTvResHighestTileText = mRoot.findViewById(R.id.tvResHighestTileText);
        mTvResTileRecordText = mRoot.findViewById(R.id.tvResTileRecordText);
        mTvHighscoreValue = mRoot.findViewById(R.id.tvHighscoreValue);
        mTvTileRecordValue = mRoot.findViewById(R.id.tvTileRecordValue);
        mTvHighestTileValue = mRoot.findViewById(R.id.tvHighestTileValue);
        mTvScoreValue = mRoot.findViewById(R.id.tvScoreValue);
        mIvBackground = mRoot.findViewById(R.id.ivBackground);
        mTvWon = mRoot.findViewById(R.id.tvWon);
        mIvMessageBg = mRoot.findViewById(R.id.ivMessageBg);
        mTvMessageText = mRoot.findViewById(R.id.tvMessageText);
        mIvBtnContinue = mRoot.findViewById(R.id.ivContinue);
        mIvBtnHome = mRoot.findViewById(R.id.ivHome);
        mIvBtnRestart = mRoot.findViewById(R.id.ivRestart);
        mTvContinueText = mRoot.findViewById(R.id.tvContinueText);
        mTvRestartText = mRoot.findViewById(R.id.tvRestartText);
        mTvHomeText = mRoot.findViewById(R.id.tvHomeText);


        //btn restart
        mIvBtnRestart.setOnClickListener(v -> {
            mActivity.hideState(MainActivity.STATE_FM_WON, 0);
            mActivity.game.restart();
        });

        //btn home
        mIvBtnHome.setOnClickListener(v -> {
            Database.currentMode.saved = mActivity.game.gameField.getSaveImage();
            long delay = mActivity.hideState(MainActivity.STATE_FM_WON, 0);
            mActivity.showState(MainActivity.STATE_FM_HOME, delay, mActivity.game);
        });

        //btn continue
        mIvBtnContinue.setOnClickListener(v -> {
            long delay = mActivity.hideState(MainActivity.STATE_FM_WON, 0);
            mActivity.game.enableAll(delay);
        });
    }

    private void updateScores() {
        mTvHighscoreValue.setText(String.valueOf(Database.currentMode.highscore));
        mTvScoreValue.setText(String.valueOf(Database.currentMode.score));
        mTvTileRecordValue.setText(String.valueOf(Database.currentMode.tileRecord));
        mTvHighestTileValue.setText(String.valueOf(Database.currentMode.highestTile));
    }

    @Override
    protected void animateShow(long delay) {
    
        mActivity.state = MainActivity.STATE_FM_WON;
        
        updateScores();

        mRoot.setVisibility(View.VISIBLE);

//        long duration = 300;
//
//        mContainer.setAlpha(0);
//        mContainer.setScaleX(0.6f);
//        mContainer.setScaleY(0.6f);
//        AnimUtils.animateAlpha(mContainer, new OvershootInterpolator(1.7f), 1, duration, delay);
//        AnimUtils.animateScale(mContainer, new OvershootInterpolator(1.7f), 1, duration, delay);
    }

    @Override
    protected long animateHide(long delay) {

        long duration = 0;

//        AnimUtils.animateAlpha(mContainer, new AccelerateInterpolator(1f), 0, duration, delay);
//        AnimUtils.animateScale(mContainer, new AccelerateInterpolator(1f), 0.8f, duration, delay);

        handler.postDelayed(() -> {
            mRoot.setVisibility(View.INVISIBLE);
        }, duration);


        return duration+50;
    }

    public void onBackPressed() {
        mIvBtnContinue.callOnClick();
    }
}
