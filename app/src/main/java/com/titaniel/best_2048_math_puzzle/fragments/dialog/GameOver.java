package com.titaniel.best_2048_math_puzzle.fragments.dialog;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.titaniel.best_2048_math_puzzle.MainActivity;
import com.titaniel.best_2048_math_puzzle.R;
import com.titaniel.best_2048_math_puzzle.database.Database;
import com.titaniel.best_2048_math_puzzle.fragments.AnimatedFragment;
import com.titaniel.best_2048_math_puzzle.leaderboard_manager.LeaderboardManager;
import com.titaniel.best_2048_math_puzzle.utils.AnimUtils;
import com.titaniel.best_2048_math_puzzle.utils.Utils;

public class GameOver extends AnimatedFragment {

    private View mRoot;


    private MainActivity mActivity;

    private ConstraintLayout mLyContainer;

    private ImageView mIvBackground;

    private TextView mTvGameOver;

    private View mDivTop;
    private TextView mTvHighscore, mTvTileRecord, mTvRankingOf;

    private ImageView mIvResultBg;
    private View mDivResHor, mDivResVertTop, mDivResVertBottom;
    private TextView mTvResScoreText, mTvResHighscoreText, mTvResHighestTileText, mTvResTileRecordText;
    private TextView mTvHighscoreValue, mTvTileRecordValue, mTvHighestTileValue, mTvScoreValue;

    private ImageView mIvBtnRestart, mIvBtnHome;
    
    private LeaderboardManager.LeaderboardLayoutInstance mLeaderboardInstance;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gameover, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        mActivity = (MainActivity) getActivity();

        //init
        mRoot = getView();
        mLyContainer = mRoot.findViewById(R.id.lyContainer);
        mIvBackground = mRoot.findViewById(R.id.ivBackground);
        mTvGameOver = mRoot.findViewById(R.id.tvGameOver);
        mDivTop = mRoot.findViewById(R.id.vDivTop);
        mTvHighscore = mRoot.findViewById(R.id.tvHighscore);
        mTvTileRecord = mRoot.findViewById(R.id.tvTileRecord);
        mTvRankingOf = mRoot.findViewById(R.id.tvRankingOf);
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
        mIvBtnRestart = mRoot.findViewById(R.id.ivRestart);
        mIvBtnHome = mRoot.findViewById(R.id.ivHome);
        
        //leaderboard instance
        mLeaderboardInstance = LeaderboardManager.generateLeaderbaordInstance(mRoot);

        //btn restart
        mIvBtnRestart.setOnClickListener(v -> {
            mActivity.hideState(MainActivity.STATE_FM_GAME_OVER, 0);
            mActivity.game.restart();
        });

        //btn home
        mIvBtnHome.setOnClickListener(v -> {
            Database.currentMode.saved = null;
            long delay = mActivity.hideState(MainActivity.STATE_FM_GAME_OVER, 0);
            mActivity.showState(MainActivity.STATE_FM_HOME, delay, mActivity.game);
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
        mActivity.state = MainActivity.STATE_FM_GAME_OVER;
        
        mActivity.leaderbaordManager.setLeaderboardLayoutInstance(mLeaderboardInstance);
        mActivity.leaderbaordManager.updateLeaderboardLayoutInstance(true);
        
        updateScores();
    
        mRoot.setVisibility(View.VISIBLE);
        mRoot.setAlpha(0);
        mRoot.setTranslationY(-Utils.dpToPx(getResources(), 16));
        AnimUtils.animateAlpha(mRoot, new DecelerateInterpolator(), 1, 300, delay);
        AnimUtils.animateTranslationY(mRoot, new DecelerateInterpolator(), 0, 300, delay);

//        long duration = 250;

//        mContainer.setAlpha(0);
//        mContainer.setScaleX(0.6f);
//        mContainer.setScaleY(0.6f);
//        AnimUtils.animateAlpha(mContainer, new OvershootInterpolator(1.7f), 1, duration, delay);
//        AnimUtils.animateScale(mContainer, new OvershootInterpolator(1.7f), 1, duration, delay);
    }

    @Override
    protected long animateHide(long delay) {
    
        long duration = 300;
    
        AnimUtils.animateAlpha(mRoot, new AccelerateInterpolator(), 0, duration, delay);
        AnimUtils.animateTranslationY(mRoot, new AccelerateInterpolator(), Utils.dpToPx(getResources(), 16), duration, delay);
    
        handler.postDelayed(() -> {
            mRoot.setVisibility(View.VISIBLE);
        }, 300);
    
    
        return duration + 50;
    }

    public void onBackPressed() {
        mIvBtnHome.callOnClick();
    }
}
