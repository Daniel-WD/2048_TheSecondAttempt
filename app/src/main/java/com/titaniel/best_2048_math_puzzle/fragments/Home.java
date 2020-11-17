package com.titaniel.best_2048_math_puzzle.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.titaniel.best_2048_math_puzzle.MainActivity;
import com.titaniel.best_2048_math_puzzle.R;
import com.titaniel.best_2048_math_puzzle.database.Database;
import com.titaniel.best_2048_math_puzzle.leaderboard_manager.LeaderboardManager;
import com.titaniel.best_2048_math_puzzle.loading_view.LoadingView;
import com.titaniel.best_2048_math_puzzle.utils.AnimUtils;
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
    
    private LeaderboardManager.LeaderboardLayoutInstance mLbI;
    
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
        
        mLbI = LeaderboardManager.generateLeaderbaordInstance(mRoot);
        
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
            if(mBlocking) return;
            mActivity.showLeaderboard();
        });
        
        //achievements
        mLyBtnAchievements.setOnClickListener(view -> {
            if(mBlocking) return;
            mActivity.showAchievements();
        });
        
        //trophies
        mIvBtnTrophies.setOnClickListener(view -> {
            if(mBlocking) return;
            mActivity.showState(MainActivity.STATE_FM_TROPHY_CHAMBER, 0, this);
        });
        
        //game services
        mIvBtnGameServices.setOnClickListener(view -> {
            if(mBlocking) return;
            mActivity.signGameServicesOut();
            mActivity.startGameServicesSignInIntent();
        });
        
    }
    
    public void updateUiState() {
        boolean online = Utils.isOnline(getContext());
        boolean gameServicesAvailable = mActivity.googleSignInAccount != null;
        
        if(!online) {
            
            mLoadingView.setVisibility(View.GONE);
            
            AnimUtils.animateAlpha(mIvGameServices, new AccelerateDecelerateInterpolator(), 0.2f, 200, 0);
            mIvBtnGameServices.setEnabled(false);
            
            AnimUtils.animateAlpha(mLyBtnAchievements, new AccelerateDecelerateInterpolator(), 0.5f, 200, 0);
            AnimUtils.animateAlpha(mLyBtnLeaderboards, new AccelerateDecelerateInterpolator(), 0.5f, 200, 0);
            
            mLyBtnAchievements.setEnabled(false);
            mLyBtnLeaderboards.setEnabled(false);
            
        } else if(!gameServicesAvailable) {
            
            mLoadingView.setVisibility(View.GONE);
            
            AnimUtils.animateAlpha(mIvGameServices, new AccelerateDecelerateInterpolator(), 1f, 200, 0);
            mIvBtnGameServices.setEnabled(true);
            
            AnimUtils.animateAlpha(mLyBtnAchievements, new AccelerateDecelerateInterpolator(), 0.5f, 200, 0);
            AnimUtils.animateAlpha(mLyBtnLeaderboards, new AccelerateDecelerateInterpolator(), 0.5f, 200, 0);
            mLyBtnAchievements.setEnabled(false);
            mLyBtnLeaderboards.setEnabled(false);
            
        } else {
            
            mLoadingView.setVisibility(View.VISIBLE);
            
            AnimUtils.animateAlpha(mIvGameServices, new AccelerateDecelerateInterpolator(), 1f, 200, 0);
            mIvBtnGameServices.setEnabled(true);
            
            AnimUtils.animateAlpha(mLyBtnAchievements, new AccelerateDecelerateInterpolator(), 1f, 200, 0);
            AnimUtils.animateAlpha(mLyBtnLeaderboards, new AccelerateDecelerateInterpolator(), 1f, 200, 0);
            
            mLyBtnAchievements.setEnabled(true);
            mLyBtnLeaderboards.setEnabled(true);
            
        }
    }
    
    private void setMode(Database.Mode mode) {
        if(mode == null) return;
        mIvSize.setImageResource(mode.representative);
        mTvHighscoreValue.setText(String.valueOf(Database.currentMode.highscore));
        mTvTileRecordValue.setText(String.valueOf(Database.currentMode.tileRecord));
        mActivity.leaderbaordManager.updateLeaderboardLayoutInstance(true);
        mActivity.leaderbaordManager.notifyModeChanged();
    }
    
    private void changeMode(boolean previous) {
        if(mBlocking || previous ? !Database.hasPreviousMode() : !Database.hasNextMode()) return;
        
        block(220);
        
        Database.Mode mode = previous ? Database.previousMode() : Database.nextMode();
        Database.currentMode = mode;
        
        long delay = 0;
        long duration = 100;
        long additionalDelay = 20;
        float dist = Utils.dpToPx(getResources(), 5);
        
        float realDist = previous ? dist : -dist;
        
        AnimUtils.animateAlpha(mIvSize, new AccelerateInterpolator(), 0, duration, delay);
        AnimUtils.animateTranslationY(mIvSize, new AccelerateInterpolator(), realDist, duration, delay);
        
        AnimUtils.animateAlpha(mTvHighscoreValue, new AccelerateInterpolator(), 0, duration, delay + additionalDelay);
        AnimUtils.animateAlpha(mTvTileRecordValue, new AccelerateInterpolator(), 0, duration, delay + additionalDelay);
        AnimUtils.animateTranslationY(mTvHighscoreValue, new AccelerateInterpolator(), realDist/2, duration, delay + additionalDelay);
        AnimUtils.animateTranslationY(mTvTileRecordValue, new AccelerateInterpolator(), realDist/2, duration, delay + additionalDelay);
        
        delay += duration + 20;
        
        handler.postDelayed(() -> {
            mIvSize.setImageResource(Database.currentMode.representative);
            
            mIvSize.setTranslationY(-realDist);
            
            AnimUtils.animateAlpha(mIvSize, new DecelerateInterpolator(), 1, duration, 0);
            AnimUtils.animateTranslationY(mIvSize, new DecelerateInterpolator(), 0, duration, 0);
            
        }, delay);
        
        handler.postDelayed(() -> {
            mTvHighscoreValue.setText(String.valueOf(Database.currentMode.highscore));
            mTvTileRecordValue.setText(String.valueOf(Database.currentMode.tileRecord));
            
            mTvHighscoreValue.setTranslationY(-realDist/2);
            mTvTileRecordValue.setTranslationY(-realDist/2);
            
            mTvHighscoreValue.setTranslationY(-realDist/2);
            mTvTileRecordValue.setTranslationY(-realDist/2);
            
            AnimUtils.animateAlpha(mTvHighscoreValue, new DecelerateInterpolator(), 1, duration, 50);
            AnimUtils.animateAlpha(mTvTileRecordValue, new DecelerateInterpolator(), 1, duration, 50);
            AnimUtils.animateTranslationY(mTvHighscoreValue, new DecelerateInterpolator(), 0, duration, 50);
            AnimUtils.animateTranslationY(mTvTileRecordValue, new DecelerateInterpolator(), 0, duration, 50);
            
        }, delay + additionalDelay);
        
        delay += additionalDelay;
        
        handler.postDelayed(() -> {
            
            mActivity.leaderbaordManager.updateLeaderboardLayoutInstance(true);
            mActivity.leaderbaordManager.notifyModeChanged();
            
        }, delay + additionalDelay);
        
        
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
        
        
        mActivity.state = MainActivity.STATE_FM_HOME;
        
        mRoot.setVisibility(View.VISIBLE);
        mRoot.setAlpha(0);
        AnimUtils.animateAlpha(mRoot, new AccelerateDecelerateInterpolator(), 1, 300, delay);
        
        delay += 300;
        
        handler.postDelayed(() -> {
            
            mActivity.leaderbaordManager.setLeaderboardLayoutInstance(mLbI);
            updateUiState();
            showNewTrophiesIfAny();
            setMode(Database.currentMode);
        }, delay);
        
        
    }
    
    @Override
    protected long animateHide(long delay) {
    
    
        AnimUtils.animateAlpha(mRoot, new AccelerateDecelerateInterpolator(), 0, 150, delay);
        handler.postDelayed(() -> mRoot.setVisibility(View.INVISIBLE), delay + 150);
        
        return 150;
    }
    
}
