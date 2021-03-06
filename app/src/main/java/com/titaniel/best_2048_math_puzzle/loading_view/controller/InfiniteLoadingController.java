package com.titaniel.best_2048_math_puzzle.loading_view.controller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;

import com.titaniel.best_2048_math_puzzle.loading_view.LoadingView;

import java.util.ArrayList;

public class InfiniteLoadingController implements Controller {

    public interface Listener {

        void onFill();

    }
    private Listener mListener;

    private long mTimePerFill, mTimePerRound;

    private LoadingView[] mLoadingViews;
    private ValueAnimator mSweepAnimator, mOffsetAnimator;

    public InfiniteLoadingController(long timePerFill, long timePerRound, LoadingView... loadingView) {
        mTimePerFill = timePerFill;
        mTimePerRound = timePerRound;
        mLoadingViews = loadingView;
    }

    @Override
    public void start() {
        if(mSweepAnimator != null && mSweepAnimator.isRunning()) {
            mSweepAnimator.cancel();
        }
        if(mOffsetAnimator != null && mOffsetAnimator.isRunning()) {
            mOffsetAnimator.cancel();
        }

        mSweepAnimator = ValueAnimator.ofFloat(0, 360);
        mSweepAnimator.addUpdateListener(animation -> {
            for(LoadingView loadingView : mLoadingViews) {
                float value = (float) animation.getAnimatedValue();
                loadingView.setSweepAngle(value);
            }
        });
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

        mOffsetAnimator = ValueAnimator.ofFloat(0, 360);
        mOffsetAnimator.addUpdateListener(animation -> {
            for(LoadingView loadingView : mLoadingViews) {
                float value = (float) animation.getAnimatedValue();
                loadingView.setOffsetAngle(value);
            }
        });
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
