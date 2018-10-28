package com.titaniel.best_2048_math_puzzle.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.titaniel.best_2048_math_puzzle.MainActivity;
import com.titaniel.best_2048_math_puzzle.R;
import com.titaniel.best_2048_math_puzzle.database.Database;
import com.titaniel.best_2048_math_puzzle.leaderboard_manager.Leaderboard;
import com.titaniel.best_2048_math_puzzle.leaderboard_manager.LeaderboardManager;
import com.titaniel.best_2048_math_puzzle.loading_view.LoadingView;
import com.titaniel.best_2048_math_puzzle.utils.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Home extends AnimatedFragment {

    private View mRoot;
    private ImageView mIvFieldSizeBg;

    private ImageView mIvBtnGameServices;
    private ImageView mIvBtnTrophies;

    private ImageView mIvTrophyOverlay;

    private ImageView mIvLeaderboardBg;

    private ImageView mIvGameServices;
    private ImageView mIvTrophy;

    private ImageView mIvSize;
    private ImageView mIvBtnNextSize, mIvBtnPreviousSize;

    private View mDivSizeHor, mDivScores;

    private TextView mTvHs, mTvTr;
    private TextView mTvHighscoreValue, mTvTileRecordValue;

    private View mDivTop;
    private TextView mTvHighscore, mTvTileRecord, mTvRankingOf;

    private ImageView mIvTitle;

    private FrameLayout mLyBtnPlay, mLyBtnLeaderboards, mLyBtnAchievements;

    private LoadingView mLoadingView;

    private LeaderboardManager.LeaderboardLayoutInstance mLeaderboardInstance;

    private MainActivity mActivity;

    private boolean mBlocking = false;

    private Runnable mDeblocker = () -> mBlocking = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        mActivity = (MainActivity) getActivity();

        //init
        mRoot = getView();
        mIvFieldSizeBg = mRoot.findViewById(R.id.ivFieldSizeBg);
        mIvBtnGameServices = mRoot.findViewById(R.id.ivGameServicesBg);
        mIvBtnTrophies = mRoot.findViewById(R.id.ivTrophiesBg);
        mIvTrophyOverlay = mRoot.findViewById(R.id.ivTrophyOverlay);
        mIvLeaderboardBg = mRoot.findViewById(R.id.ivLeaderboardBg);
        mIvGameServices = mRoot.findViewById(R.id.ivGameServices);
        mIvTrophy = mRoot.findViewById(R.id.ivTrophy);
        mIvSize = mRoot.findViewById(R.id.ivSize);
        mIvBtnPreviousSize = mRoot.findViewById(R.id.ivPreviousSize);
        mIvBtnNextSize = mRoot.findViewById(R.id.ivNextSize);
        mDivSizeHor = mRoot.findViewById(R.id.vDivSizeHor);
        mDivScores = mRoot.findViewById(R.id.vDivScores);
        mTvHs = mRoot.findViewById(R.id.tvHs);
        mTvTr = mRoot.findViewById(R.id.tvTr);
        mTvHighscoreValue = mRoot.findViewById(R.id.tvHighscoreValue);
        mTvTileRecordValue = mRoot.findViewById(R.id.tvTileRecordValue);
        mDivTop = mRoot.findViewById(R.id.vDivTop);
        mTvHighscore = mRoot.findViewById(R.id.tvHighscore);
        mTvTileRecord = mRoot.findViewById(R.id.tvTileRecord);
        mTvRankingOf = mRoot.findViewById(R.id.tvRankingOf);
        mIvTitle = mRoot.findViewById(R.id.ivTitle);
        mLyBtnPlay = mRoot.findViewById(R.id.lyPlay);
        mLyBtnAchievements = mRoot.findViewById(R.id.lyAchievements);
        mLyBtnLeaderboards = mRoot.findViewById(R.id.lyLeaderboards);
        mLoadingView = mRoot.findViewById(R.id.loadingView);

        mLeaderboardInstance = LeaderboardManager.generateLeaderbaordInstance(mRoot);

        //loading view
        mLoadingView.setSweepAngle(360);
        
        //play
        mLyBtnPlay.setOnClickListener(v -> {
            if(mBlocking) return;
            mActivity.showState(MainActivity.STATE_FM_GAME, 0, this);
        });

        //next size
        mIvBtnNextSize.setOnClickListener(view -> {
            if(mBlocking) return;
            changeMode(false);
        });

        //previous size
        mIvBtnPreviousSize.setOnClickListener(view -> {
            if(mBlocking) return;
            changeMode(true);
        });

        //leaderbaords
        mLyBtnLeaderboards.setOnClickListener(view -> {
            mActivity.showLeaderboard();
        });

        //achievements
        mLyBtnAchievements.setOnClickListener(view -> {
            mActivity.showAchievements();
        });

        //trophies
        mIvBtnTrophies.setOnClickListener(view -> {
            mActivity.showState(MainActivity.STATE_FM_TROPHY_CHAMBER, 0, this);
        });

        //game services
        mIvBtnGameServices.setOnClickListener(view -> {
            mActivity.signGameServicesOut();
            mActivity.startGameServicesSignInIntent();
        });

    }

    public void updateUiState() {
        boolean online = Utils.isOnline(getContext());
        boolean gameServicesAvailable = mActivity.googleSignInAccount != null;

        if(!online) {

            mLoadingView.setVisibility(View.GONE);

            mIvGameServices.setAlpha(0.2f);
            mIvBtnGameServices.setEnabled(false);

            mLyBtnAchievements.setAlpha(0.5f);
            mLyBtnLeaderboards.setAlpha(0.5f);
            mLyBtnAchievements.setEnabled(false);
            mLyBtnLeaderboards.setEnabled(false);

        } else if(!gameServicesAvailable) {

            mLoadingView.setVisibility(View.GONE);

            mIvGameServices.setAlpha(1f);
            mIvBtnGameServices.setEnabled(true);

            mLyBtnAchievements.setAlpha(0.5f);
            mLyBtnLeaderboards.setAlpha(0.5f);
            mLyBtnAchievements.setEnabled(false);
            mLyBtnLeaderboards.setEnabled(false);

        } else {

            mLoadingView.setVisibility(View.VISIBLE);

            mIvGameServices.setAlpha(1f);
            mIvBtnGameServices.setEnabled(true);

            mLyBtnAchievements.setAlpha(1f);
            mLyBtnLeaderboards.setAlpha(1f);
            mLyBtnAchievements.setEnabled(true);
            mLyBtnLeaderboards.setEnabled(true);

        }
    }

    private void setMode(Database.Mode mode) {
        if(mode == null) return;
        mIvSize.setImageResource(mode.representative);
        mTvHighscoreValue.setText(String.valueOf(Database.currentMode.highscore));
        mTvTileRecordValue.setText(String.valueOf(Database.currentMode.tileRecord));
        mActivity.leaderbaordManager.updateLeaderboardLayoutInstances(true);
    }

    private void changeMode(boolean previous) {
        if(mBlocking || previous ? !Database.hasPreviousMode() : !Database.hasNextMode()) return;

//        block(350);

        Database.Mode mode = previous ? Database.previousMode() : Database.nextMode();
        Database.currentMode = mode;

        long delay = 0;
        float dist = -20;

        float realDist = previous ? dist : -dist;

//        AnimUtils.animateAlpha(mIvMode, new AccelerateDecelerateInterpolator(), 0, 150, delay);
//        AnimUtils.animateTranslationY(mIvMode, new AccelerateInterpolator(), realDist, 150, delay);

        delay += 200;

        handler.postDelayed(() -> {
            setMode(mode);

//            mIvMode.setTranslationY(-realDist);

//            AnimUtils.animateAlpha(mIvMode, new AccelerateDecelerateInterpolator(), 1, 150, 0);
//            AnimUtils.animateTranslationY(mIvMode, new DecelerateInterpolator(), 0, 150, 0);

        }, 0);

    }

    private void block(long duration) {
        mBlocking = true;
        handler.postDelayed(mDeblocker, duration);
    }
    
    private void showNewTrophiesIfAny() {
        if(!Database.nextAvailableTrophies.isEmpty()) {
            mActivity.showState(MainActivity.STATE_FM_TROPHY, 0, this);
        }
    }

    @Override
    protected void animateShow(long delay) {
        mRoot.setVisibility(View.VISIBLE);
    
        mActivity.state = MainActivity.STATE_FM_HOME;
        
        mActivity.leaderbaordManager.leaderboardLayoutInstance = mLeaderboardInstance;
        mActivity.leaderbaordManager.updateLeaderboardLayoutInstances(false);
    
        setMode(Database.currentMode);
        updateUiState();

        delay += 1000;
        
        handler.postDelayed(this::showNewTrophiesIfAny, delay);
        

//        long duration = 150;
//
//        block(duration);
//
//
//        mRoot.setVisibility(View.VISIBLE);
//        mRoot.setAlpha(0);
//        AnimUtils.animateAlpha(mRoot, new AccelerateDecelerateInterpolator(), 1, duration, delay);
//
//        delay += 150;
//
//        handler.postDelayed(this::checkPeriph, delay);
    }

    @Override
    protected long animateHide(long delay) {

        handler.postDelayed(() -> mRoot.setVisibility(View.INVISIBLE), delay);

        return 150;
    }

}
