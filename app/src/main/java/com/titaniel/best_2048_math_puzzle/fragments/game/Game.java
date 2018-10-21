package com.titaniel.best_2048_math_puzzle.fragments.game;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.reward.RewardItem;
import com.titaniel.best_2048_math_puzzle.MainActivity;
import com.titaniel.best_2048_math_puzzle.R;
import com.titaniel.best_2048_math_puzzle.admob.Admob;
import com.titaniel.best_2048_math_puzzle.database.Database;
import com.titaniel.best_2048_math_puzzle.fragments.AnimatedFragment;
import com.titaniel.best_2048_math_puzzle.game_services.GameServices;
import com.titaniel.best_2048_math_puzzle.loading_view.LoadingView;
import com.titaniel.best_2048_math_puzzle.utils.AnimUtils;
import com.titaniel.best_2048_math_puzzle.utils.Utils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

public class Game extends AnimatedFragment {

    private Admob.MyAdListener mAdListener = new Admob.MyAdListener() {

        boolean reward = false;

        @Override
        public void onRewardedVideoAdClosed() {
            if(reward) {
                handler.postDelayed(() -> {
                    Database.currentMode.backs += 3;
                    refreshBackState();
                    updateBackText();
                }, 300);
                reward = false;
            }
        }

        @Override
        public void onRewarded(RewardItem rewardItem) {
            reward = true;
        }

    };

    private View mRoot;

    private TouchArea mTouchArea;

    private ImageView mIvLeaderboardBg;
    private ImageView mIvPointsBg;

    private ImageView mIvBtnLeaderboard;
    private ImageView mIvBtnBacks;

    private ImageView mIvUndoOverlay;

    private ImageView mIvLeaderboard;
    private ImageView mIvNewUndos;
    private ImageView mIvUndo;

    private TextView mTvUndoCount;
    private TextView mTvPoints;

    private View mDivPointsHor;
    private View mDivPointsCenter;

    private TextView mTvTr, mTvHs;
    private TextView mTvTileRecordValue, mTvHighscoreValue;

    private View mDivTop;
    private TextView mTvHighscore, mTvTileRecord, mTvRankingOf;

    private ImageView mIvBtnPause;

    private LoadingView mLoadingView;

    public GameField gameField;

    public boolean won = false;

    private boolean mBackShown = false;
    private boolean mBlocking = false;

    private Runnable mDeblocker = () -> mBlocking = false;

    private MainActivity mActivity;

    private ArrayList<Animator> mCurrentBackHighlightAnims = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        mActivity = (MainActivity) getActivity();

        //init
        mRoot = getView();
        mTouchArea = mRoot.findViewById(R.id.touchArea);
        mIvLeaderboardBg = mRoot.findViewById(R.id.ivLeaderboardBg);
        mIvPointsBg = mRoot.findViewById(R.id.ivPointsBg);
        mIvBtnLeaderboard = mRoot.findViewById(R.id.ivLeaderboardLink);
        mIvBtnBacks = mRoot.findViewById(R.id.ivBacksBg);
        mIvUndoOverlay = mRoot.findViewById(R.id.ivUndoOverlay);
        mIvLeaderboard = mRoot.findViewById(R.id.ivLeaderboard);
        mIvNewUndos = mRoot.findViewById(R.id.ivNewUndos);
        mIvUndo = mRoot.findViewById(R.id.ivUndo);
        mTvUndoCount = mRoot.findViewById(R.id.tvUndoCount);
        mTvPoints = mRoot.findViewById(R.id.tvPoints);
        mDivPointsHor = mRoot.findViewById(R.id.vDivPointsHor);
        mDivPointsCenter = mRoot.findViewById(R.id.vDivPointsCenter);
        mTvTr = mRoot.findViewById(R.id.tvTr);
        mTvHs = mRoot.findViewById(R.id.tvHs);
        mTvHighscoreValue = mRoot.findViewById(R.id.tvHighscoreValue);
        mTvTileRecordValue = mRoot.findViewById(R.id.tvTileRecordValue);
        mDivTop = mRoot.findViewById(R.id.vDivTop);
        mTvHighscore = mRoot.findViewById(R.id.tvHighscore);
        mTvTileRecord = mRoot.findViewById(R.id.tvTileRecord);
        mTvRankingOf = mRoot.findViewById(R.id.tvRankingOf);
        mIvBtnPause = mRoot.findViewById(R.id.ivPause);
        mLoadingView = mRoot.findViewById(R.id.loadingView);
        gameField = mRoot.findViewById(R.id.gameField);

        //show current leaderboard
        mIvBtnLeaderboard.setOnClickListener(view -> {
            mActivity.showLeaderboard(GameServices.findScoreLeaderboardIdBySize(Database.currentMode.fieldSize));
        });

        //back
        mIvBtnBacks.setOnClickListener(v -> {
            if(mBlocking) return;
            if(Database.currentMode.backs > 0) {
                gameField.performBack();
                block(400);
            } else {
                if(Admob.rewardedVideoAd.isLoaded()) {
                    Admob.rewardedVideoAd.show();
                }
            }
        });

        //touch area setup
        mTouchArea.gameField = gameField;

        //pause
        mIvBtnPause.setOnClickListener(v -> {
            disableAll();
            mActivity.showState(MainActivity.STATE_FM_PAUSE, 50, null);
        });

        //gamefield listener
        gameField.setMoveListener(new GameField.MoveListener() {
            @Override
            public void onMoveCompleted() {
                if(!gameField.canUserMove()) {

                    if(Database.currentMode.backs <= 0) {
                        disableAllSlow();
                        handler.postDelayed(() -> {
                            if(Database.currentMode.backs > 0) {
                                mActivity.showState(MainActivity.STATE_FM_BACKS, 0, null);
                            } else {
                                if(Utils.isOnline(getContext())) {
                                    mActivity.showState(MainActivity.STATE_FM_BACKS, 0, null);
                                } else {
                                    mActivity.showState(MainActivity.STATE_FM_GAME_OVER, 0, null);
                                }

                            }

                            disableAll();

                        }, 1800);
                    } else {
                        handler.postDelayed(() -> startHighlightBack(), 200);
                    }


                } else {
                    refreshBackState();
                }
            }

            @Override
            public void onMove(int direction) {
            }

            @Override
            public void onJoin(int newPoints, int maxNumber) {
                // --> no animation --> see gamefield
                Database.currentMode.score += newPoints;
                updatePointsText();

                //achievements trigger
                if(maxNumber > Database.currentMode.highestTile) {
                    GameServices.checkForTileAchievement(mActivity, maxNumber);
                    Database.currentMode.highestTile = maxNumber;
                }
                GameServices.checkForPointAchievement(mActivity, Database.currentMode.score);

                if(maxNumber == 2048 && !won) {
                    won = true;
                    mActivity.showState(MainActivity.STATE_FM_WON, 100, null);
                    disableAll();
                }

                // TODO: 22.08.2018 optimize --> method in mode
                if(Database.currentMode.score > Database.currentMode.allTimeHighscore) {
                    Database.currentMode.allTimeHighscore = Database.currentMode.score;
                }
                if(Database.currentMode.highestTile > Database.currentMode.allTimeTileRecord) {
                    Database.currentMode.allTimeTileRecord = Database.currentMode.highestTile;
                }

                updateHighscoreAndTileRecordText();
            }

            @Override
            public void onBackCompleted() {
                Database.currentMode.backs--;
                updateBackText();
                refreshBackState();
                stopHighlightBack();
            }
        });

        //score reset
        updatePointsText();
        updateHighscoreAndTileRecordText();
        updateBackText();
    }

    public void updateUiState() {
        if(Utils.isOnline(getContext()) && mActivity.googleSignInAccount != null) {
            mIvLeaderboard.setAlpha(1f);
            mIvLeaderboardBg.setEnabled(true);
        } else {
            mIvLeaderboard.setAlpha(0.2f);
            mIvLeaderboardBg.setEnabled(false);
        }
    }

    private void startHighlightBack() {
        if(mCurrentBackHighlightAnims.size() != 0) return;

        mIvUndoOverlay.setVisibility(View.VISIBLE);
        mIvUndoOverlay.setAlpha(0.5f);

        long duration = 400;

        ValueAnimator borderAnim = ValueAnimator.ofFloat(0f, 1f);
        borderAnim.addUpdateListener(animation -> {
            float alpha = (float) animation.getAnimatedValue();
            mIvUndoOverlay.setAlpha(alpha);
        });
        borderAnim.setDuration(duration);
        borderAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        borderAnim.setRepeatMode(ValueAnimator.REVERSE);
        borderAnim.setRepeatCount(ValueAnimator.INFINITE);
        borderAnim.start();

        ValueAnimator contentColorAnim = ValueAnimator.ofArgb(Color.WHITE, ContextCompat.getColor(getContext(), R.color.rankUp));
        contentColorAnim.addUpdateListener(animation -> {
            int color = (int) animation.getAnimatedValue();
            mTvUndoCount.setTextColor(color);
            mIvUndo.setColorFilter(color);
        });
        contentColorAnim.setDuration(duration);
        contentColorAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        contentColorAnim.setRepeatMode(ValueAnimator.REVERSE);
        contentColorAnim.setRepeatCount(ValueAnimator.INFINITE);
        contentColorAnim.start();

        mCurrentBackHighlightAnims.add(borderAnim);
        mCurrentBackHighlightAnims.add(contentColorAnim);
    }

    private void stopHighlightBack() {

        if(mCurrentBackHighlightAnims.size() != 0) {
            for(Animator anim : mCurrentBackHighlightAnims) {
                anim.cancel();
            }
            mCurrentBackHighlightAnims.clear();

            mIvUndoOverlay.setVisibility(View.INVISIBLE);

            mTvUndoCount.setTextColor(Color.WHITE);
            mIvUndo.setColorFilter(Color.WHITE);
        }

    }

    private void refreshBackState() {

        if(!gameField.canPerformBack()) {
            mIvNewUndos.setVisibility(View.INVISIBLE);

            mIvUndo.setVisibility(View.VISIBLE);
            mTvUndoCount.setVisibility(View.VISIBLE);

            mIvUndo.setAlpha(0.2f);
            mTvUndoCount.setAlpha(0.2f);
        } else {
            if(Database.currentMode.backs > 0) {
                mIvNewUndos.setVisibility(View.INVISIBLE);

                mIvUndo.setVisibility(View.VISIBLE);
                mTvUndoCount.setVisibility(View.VISIBLE);

                mIvUndo.setAlpha(1f);
                mTvUndoCount.setAlpha(1f);
            } else {
                mIvUndo.setVisibility(View.INVISIBLE);
                mTvUndoCount.setVisibility(View.INVISIBLE);

                mIvNewUndos.setVisibility(View.VISIBLE);
                mIvNewUndos.setAlpha(Utils.isOnline(getContext()) ? 1f : 0.2f);
            }
        }

        if(Database.currentMode.backs == 0 || !gameField.canPerformBack()) {
            hideBack();
        } else {
            showBack();
        }

    }

    private void showBack() {
        if(mBackShown) return;
        mBackShown = true;

        mIvUndo.setVisibility(View.VISIBLE);
        mTvUndoCount.setVisibility(View.VISIBLE);

        mIvNewUndos.setVisibility(View.INVISIBLE);
    }

    private void hideBack() {
        if(!mBackShown) return;
        mBackShown = false;

        mIvUndo.setVisibility(View.INVISIBLE);
        mTvUndoCount.setVisibility(View.INVISIBLE);

        mIvNewUndos.setVisibility(View.VISIBLE);
    }

    private void backClickAnim() {
        if(!mBackShown) return;


    }

    private void disableAll() {

        gameField.setEnabled(false);
        mIvBtnPause.setEnabled(false);
        mIvBtnBacks.setEnabled(false);

        long duration = 100;
        float alpha = 0f;

        AnimUtils.animateAlpha(mRoot, new AccelerateDecelerateInterpolator(), alpha, duration, 0);

    }

    private void disableAllSlow() {

        gameField.setEnabled(false);
        mIvBtnPause.setEnabled(false);
        mIvBtnBacks.setEnabled(false);

        long duration = 1500;
        float alpha = 0.1f;

        AnimUtils.animateAlpha(mRoot, new AccelerateDecelerateInterpolator(), alpha, duration, 0);

    }

    public void enableAll(long delay) {
        mActivity.state = MainActivity.STATE_FM_GAME;

        won = false;

        updateBackText();

        gameField.setEnabled(true);
        mIvBtnPause.setEnabled(true);
        mIvBtnBacks.setEnabled(true);

        long duration = 150;
        float alpha = 1f;

        TimeInterpolator interpolator = new LinearOutSlowInInterpolator();

        AnimUtils.animateAlpha(mRoot, interpolator, alpha, duration, delay);
    }


    private void block(long duration) {
        mBlocking = true;
        handler.postDelayed(mDeblocker, duration);
    }

    public void restart() {
        enableAll(0);
        Database.currentMode.score = 0;
        Database.currentMode.highestTile = 0;
        Database.currentMode.backs = Database.START_BACK_VALUE;
        updateBackText();
        reset();
        refreshBackState();
        handler.postDelayed(() -> gameField.showStartTiles(), 500);
    }

    private void reset() {
        won = false;
        stopHighlightBack();
        gameField.clear();
        updatePointsText();
        updateHighscoreAndTileRecordText();
        refreshBackState();
        gameField.setFieldSize(Database.currentMode.fieldSize);
    }

    private void updatePointsText() {
        mTvPoints.setText(String.valueOf(Database.currentMode.score));
    }

    private void updateHighscoreAndTileRecordText() {
        mTvTileRecordValue.setText(String.valueOf(Database.currentMode.allTimeTileRecord));
        mTvHighscoreValue.setText(String.valueOf(Database.currentMode.allTimeHighscore));
    }

    private void updateBackText() {
        mTvUndoCount.setText(String.valueOf(Database.currentMode.backs));
    }

    public void performBack() {
        mIvBtnBacks.callOnClick();
    }

    @Override
    protected void animateShow(long delay) {
        mRoot.setVisibility(View.VISIBLE);

        Admob.adListener = mAdListener;

        reset();
        mBackShown = false;

/*
        mRoot.setVisibility(View.VISIBLE);
        mRoot.setAlpha(0);
        AnimUtils.animateAlpha(mRoot, new AccelerateDecelerateInterpolator(), 1, 150, delay);

        delay += 300;*/

        if(Database.currentMode.saved == null) {
            Database.currentMode.score = 0;
            Database.currentMode.backs = Database.START_BACK_VALUE;
            updateBackText();
            refreshBackState();
            handler.postDelayed(() -> gameField.showStartTiles(), delay);
        } else {
            handler.postDelayed(() -> {
                gameField.setSaveImageAndAnimate(Database.currentMode.saved);
            }, delay);

            handler.postDelayed(() -> {
                updateBackText();
                refreshBackState();
            }, delay);

        }

    }

    @Override
    protected long animateHide(long delay) {

        Admob.adListener = null;

        enableAll(0);
        mRoot.setVisibility(View.INVISIBLE);

        return 0;
    }

    public boolean onBackPressed() {
        mIvBtnPause.callOnClick();
        return true;
    }

}
