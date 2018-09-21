package com.titaniel.best_2048_math_puzzle.fragments.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import com.titaniel.best_2048_math_puzzle.MainActivity;
import com.titaniel.best_2048_math_puzzle.R;
import com.titaniel.best_2048_math_puzzle.database.Database;
import com.titaniel.best_2048_math_puzzle.fragments.AnimatedFragment;
import com.titaniel.best_2048_math_puzzle.utils.AnimUtils;

public class Pause extends AnimatedFragment {

    private View mRoot;
    private ImageView mBtnRestart, mBtnHome, mBtnClose;
    private ConstraintLayout mContainer;

    private MainActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pause, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        mActivity = (MainActivity) getActivity();

        //init
        mRoot = getView();
        mBtnRestart = mRoot.findViewById(R.id.btnRestart);
        mBtnHome = mRoot.findViewById(R.id.btnHome);
        mBtnClose = mRoot.findViewById(R.id.btnClose);
        mContainer = mRoot.findViewById(R.id.lyContainer);

        //btn restart
        mBtnRestart.setOnClickListener(v -> {
            mActivity.hideState(MainActivity.STATE_FM_PAUSE, 0);
            mActivity.game.restart();
        });

        //btn home
        mBtnHome.setOnClickListener(v -> {
            Database.currentMode.saved = mActivity.game.gameField.getSaveImage();
            long delay = mActivity.hideState(MainActivity.STATE_FM_PAUSE, 0);
            mActivity.showState(MainActivity.STATE_FM_HOME, delay, mActivity.game);
        });

        //btn close
        mBtnClose.setOnClickListener(v -> {
            long delay = mActivity.hideState(MainActivity.STATE_FM_PAUSE, 0);
            mActivity.game.enableAll(delay);
        });
    }

    @Override
    protected void animateShow(long delay) {

        mRoot.setVisibility(View.VISIBLE);

        long duration = 250;

        mContainer.setAlpha(0);
        mContainer.setScaleX(0.6f);
        mContainer.setScaleY(0.6f);
        AnimUtils.animateAlpha(mContainer, new OvershootInterpolator(1.7f), 1, duration, delay);
        AnimUtils.animateScale(mContainer, new OvershootInterpolator(1.7f), 1, duration, delay);
    }

    @Override
    protected long animateHide(long delay) {

        long duration = 150;

        AnimUtils.animateAlpha(mContainer, new AccelerateInterpolator(1f), 0, duration, delay);
        AnimUtils.animateScale(mContainer, new AccelerateInterpolator(1f), 0.8f, duration, delay);

        handler.postDelayed(() -> {
            mRoot.setVisibility(View.INVISIBLE);
        }, duration);


        return duration+50;
    }
}
