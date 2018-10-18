package com.titaniel.best_2048_math_puzzle.loading_view.controller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;

import com.titaniel.best_2048_math_puzzle.loading_view.LoadingView;

public class InfiniteLoadingController implements Controller {

    public interface Listener {

        void onFill();

    }
    private Listener mListener;

    private long mTimePerFill, mTimePerRound;

    private LoadingView mLoadingView;
    private ObjectAnimator mSweepAnimator, mOffsetAnimator;

    public InfiniteLoadingController(LoadingView loadingView, long timePerFill, long timePerRound) {
        mTimePerFill = timePerFill;
        mTimePerRound = timePerRound;
        mLoadingView = loadingView;
    }

    @Override
    public void start() {
        if(mSweepAnimator != null && mSweepAnimator.isRunning()) {
            mSweepAnimator.cancel();
        }
        if(mOffsetAnimator != null && mOffsetAnimator.isRunning()) {
            mOffsetAnimator.cancel();
        }

        mSweepAnimator = ObjectAnimator.ofFloat(mLoadingView, "sweepAngle", 0, 360);
        mSweepAnimator.setInterpolator(new LinearInterpolator());
        mSweepAnimator.setDuration(mTimePerFill);
        mSweepAnimator.setRepeatMode(ValueAnimator.RESTART);
        mSweepAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mSweepAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                if(mListener != null) mListener.onFill();
            }
        });
        mSweepAnimator.start();

        mOffsetAnimator = ObjectAnimator.ofFloat(mLoadingView, "offsetAngle", 0, 360);
        mOffsetAnimator.setInterpolator(new LinearInterpolator());
        mOffsetAnimator.setDuration(mTimePerRound);
        mOffsetAnimator.setRepeatMode(ValueAnimator.RESTART);
        mOffsetAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mOffsetAnimator.start();

    }

    @Override
    public void stop() {
        mSweepAnimator.cancel();
        mOffsetAnimator.cancel();
    }

    public Listener getListener() {
        return mListener;
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

}
