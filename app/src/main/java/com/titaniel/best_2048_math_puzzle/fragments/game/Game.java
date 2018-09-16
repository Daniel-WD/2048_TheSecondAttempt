package com.titaniel.best_2048_math_puzzle.fragments.game;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.titaniel.best_2048_math_puzzle.MainActivity;
import com.titaniel.best_2048_math_puzzle.R;
import com.titaniel.best_2048_math_puzzle.database.Database;
import com.titaniel.best_2048_math_puzzle.fragments.AnimatedFragment;
import com.titaniel.best_2048_math_puzzle.utils.AnimUtils;
import com.titaniel.best_2048_math_puzzle.utils.Utils;

public class Game extends AnimatedFragment {

    private final static long CLOSE_POWER_UPS_DELAY = 300;

    private final static int MONEY_BACK = 10;
    private final static int MONEY_DELETE = 25;
    private final static int MONEY_EXCHANGE = 50;
    private final static int MONEY_DOUBLE = 70;

    private int mPUDisabledColor = Color.parseColor("#CBCBCB");

    private View mRoot;
    private ImageView mBtnPause;
    private TextView mTvScore;
    private TextView mTvPoints;
    private LinearLayout mLyPoints;
    private TextView mTvInstruction;
    private TouchArea mTouchArea;
    private View mVDivOne, mVDivTwo;
    private TextView mTvBackCount;
    private ConstraintLayout mLyBack;
    private TextView mTvRecord;

    public GameField gameField;

    public boolean loadGame = false;
    public boolean won = false;

    private boolean mBackShown = false;
    private boolean mBlocking = false;

    private Runnable mDeblocker = () -> mBlocking = false;

    private MainActivity mActivity;

    private ValueAnimator mCurrentBackHighlightAnim;

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
        mBtnPause = mRoot.findViewById(R.id.ivPause);
        mTvPoints = mRoot.findViewById(R.id.tvPoints);
        gameField = mRoot.findViewById(R.id.gameField);
        mTouchArea = mRoot.findViewById(R.id.touchArea);
        mVDivOne = mRoot.findViewById(R.id.vDivOne);
        mVDivTwo = mRoot.findViewById(R.id.vDivTwo);
        mTvBackCount = mRoot.findViewById(R.id.tvBackCount);
        mLyBack = mRoot.findViewById(R.id.lyBack);
        mTvRecord = mRoot.findViewById(R.id.tvRecord);

        //back
        mLyBack.setOnClickListener(v -> {
            if(mBlocking) return;
            gameField.performBack();
            backClickAnim();
            block(500);
        });

        //touch area setup
        mTouchArea.gameField = gameField;

        //pause
        mBtnPause.setOnClickListener(v -> {
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
                                mActivity.showState(MainActivity.STATE_FM_UNDO, 0, null);
                            } else {
                                if(Utils.isOnline(getContext())) {
                                    mActivity.showState(MainActivity.STATE_FM_UNDO, 0, null);
                                } else {
                                    mActivity.showState(MainActivity.STATE_FM_GAME_OVER, 0, null);
                                }

                            }

                            disableAll();

                        }, 1800);
                    } else {
                        startHighlightBack();
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
                Database.currentMode.points += newPoints;
                updatePointsText();

                if(maxNumber == 2048 && !won) {
                    won = true;
                    mActivity.showState(MainActivity.STATE_FM_WON, 100, null);
                    disableAll();
                }

                // TODO: 22.08.2018 optimize --> method in mode
                if(Database.currentMode.points > Database.currentMode.record) {
                    Database.currentMode.record = Database.currentMode.points;
                }

                updateRecordText();
            }

            @Override
            public void onBackCompleted() {
                Database.currentMode.backs--;
                updateBackText();
                refreshBackState();
                stopHighlightBack();
            }
        });

        //points reset
        updatePointsText();
        updateRecordText();
    }

    private void startHighlightBack() {
        if(mCurrentBackHighlightAnim != null) return;

        ValueAnimator anim = ValueAnimator.ofFloat(1, 1.1f);
        anim.addUpdateListener(animation -> {
            float scale = (float) animation.getAnimatedValue();
            mLyBack.setScaleX(scale);
            mLyBack.setScaleY(scale);
        });
        anim.setDuration(500);
        anim.setInterpolator(new CycleInterpolator(1));
        anim.setRepeatMode(ValueAnimator.RESTART);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.start();

        mCurrentBackHighlightAnim = anim;

    }

    private void stopHighlightBack() {
        if(mCurrentBackHighlightAnim != null && mCurrentBackHighlightAnim.isRunning()) {
            mCurrentBackHighlightAnim.cancel();
            mCurrentBackHighlightAnim = null;
            mLyBack.setScaleX(1);
            mLyBack.setScaleY(1);
        }
    }

    private void refreshBackState() {
        if(Database.currentMode.backs == 0 || !gameField.canPerformBack()) {
            hideBack();
        } else {
            showBack();
        }

    }

    private void showBack() {
        if(mBackShown) return;
        mBackShown = true;


        mLyBack.setVisibility(View.VISIBLE);
        mLyBack.setTranslationX(0);
        float dist = mRoot.getWidth()-mLyBack.getX();
        mLyBack.setTranslationX(dist);
        mLyBack.setAlpha(1);
        AnimUtils.animateTranslationX(mLyBack, new DecelerateInterpolator(2), 0, 200, 0);

    }

    private void hideBack() {
        if(!mBackShown) return;
        mBackShown = false;

        float dist = mRoot.getWidth()-mLyBack.getX();
        AnimUtils.animateTranslationX(mLyBack, new AccelerateInterpolator(2), dist, 200, 0);

    }

    private void backClickAnim() {
        if(!mBackShown) return;

        AnimUtils.animateScale(mLyBack, new AccelerateDecelerateInterpolator(), 1.1f, 150, 0);

        handler.postDelayed(() -> {
            AnimUtils.animateScale(mLyBack, new AccelerateDecelerateInterpolator(), 1f, 150, 0);
        }, 150);

    }

    public void disableAll() {

        gameField.setEnabled(false);
        mBtnPause.setEnabled(false);
        mLyBack.setEnabled(false);

        long duration = 100;
        float alpha = 0f;

        AnimUtils.animateAlpha(gameField, new AccelerateDecelerateInterpolator(), alpha, duration, 0);
        AnimUtils.animateAlpha(mTvPoints, new AccelerateDecelerateInterpolator(), alpha, duration, 0);
        AnimUtils.animateAlpha(mTvRecord, new AccelerateDecelerateInterpolator(), alpha, duration, 0);
        AnimUtils.animateAlpha(mVDivOne, new AccelerateDecelerateInterpolator(), alpha, duration, 0);
        AnimUtils.animateAlpha(mVDivTwo, new AccelerateDecelerateInterpolator(), alpha, duration, 0);

        if(mLyBack.getVisibility() == View.VISIBLE) {
            AnimUtils.animateAlpha(mLyBack, new AccelerateDecelerateInterpolator(), alpha, duration, 0);
        }

        AnimUtils.animateAlpha(mBtnPause, new AccelerateDecelerateInterpolator(), 0, duration, 0);
    }

    public void disableAllSlow() {

        gameField.setEnabled(false);
        mBtnPause.setEnabled(false);
        mLyBack.setEnabled(false);

        long duration = 1500;
        float alpha = 0.1f;

        AnimUtils.animateAlpha(gameField, new AccelerateDecelerateInterpolator(), alpha, duration, 0);
        AnimUtils.animateAlpha(mTvPoints, new AccelerateDecelerateInterpolator(), alpha, duration, 0);
        AnimUtils.animateAlpha(mTvRecord, new AccelerateDecelerateInterpolator(), alpha, duration, 0);
        AnimUtils.animateAlpha(mVDivOne, new AccelerateDecelerateInterpolator(), alpha, duration, 0);
        AnimUtils.animateAlpha(mVDivTwo, new AccelerateDecelerateInterpolator(), alpha, duration, 0);

        if(mLyBack.getVisibility() == View.VISIBLE) {
            AnimUtils.animateAlpha(mLyBack, new AccelerateDecelerateInterpolator(), alpha, duration, 0);
        }

        AnimUtils.animateAlpha(mBtnPause, new AccelerateDecelerateInterpolator(), 0, duration, 0);
    }

    public void enableAll(long delay) {
        mActivity.state = MainActivity.STATE_FM_GAME;

        updateBackText();

        gameField.setEnabled(true);
        mBtnPause.setEnabled(true);
        mLyBack.setEnabled(true);

        long duration = 150;
        float alpha = 1f;

        TimeInterpolator interpolator = new LinearOutSlowInInterpolator();

        AnimUtils.animateAlpha(gameField, interpolator, alpha, duration, delay);
        AnimUtils.animateAlpha(mBtnPause, interpolator, alpha, duration, delay);
        AnimUtils.animateAlpha(mTvPoints, interpolator, alpha, duration, delay);
        AnimUtils.animateAlpha(mTvRecord, interpolator, alpha, duration, delay);
        AnimUtils.animateAlpha(mVDivOne, interpolator, alpha, duration, delay);
        AnimUtils.animateAlpha(mVDivTwo, interpolator, alpha, duration, delay);
        if(mBackShown) AnimUtils.animateAlpha(mLyBack, interpolator, alpha, duration, delay);

    }


    private void block(long duration) {
        mBlocking = true;
        handler.postDelayed(mDeblocker, duration);
    }

    public void restart() {
        enableAll(0);
        Database.currentMode.points = 0;
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
        updateRecordText();
        gameField.setFieldSize(Database.currentMode.fieldSize);
    }

    public void updatePointsText() {
        mTvPoints.setText(String.valueOf(Database.currentMode.points));
    }

    public void updateRecordText() {
        mTvRecord.setText(getString(R.string.temp_best, Database.currentMode.record));
    }

    public void updateBackText() {
        mTvBackCount.setText(String.valueOf(Database.currentMode.backs));
    }

    @Override
    protected void animateShow(long delay) {

        reset();

        mLyBack.setVisibility(View.INVISIBLE);
        mBackShown = false;

        mRoot.setVisibility(View.VISIBLE);
        mRoot.setAlpha(0);
        AnimUtils.animateAlpha(mRoot, new AccelerateDecelerateInterpolator(), 1, 150, delay);

        delay += 300;

        if(!loadGame) {
            updateBackText();
            handler.postDelayed(() -> gameField.showStartTiles(), delay);
        } else {
            handler.postDelayed(() -> {
                gameField.setSaveImageAndAnimate(Database.currentMode.saved);
            }, delay);

            handler.postDelayed(this::refreshBackState, delay);
        }

    }

    @Override
    protected long animateHide(long delay) {

        enableAll(0);
        mRoot.setVisibility(View.INVISIBLE);

        return 0;
    }

    public boolean onBackPressed() {
        mBtnPause.callOnClick();
        return true;
    }

}
