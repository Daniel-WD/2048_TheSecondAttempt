package com.titaniel.best_2048_math_puzzle.fragments.dialog;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.titaniel.best_2048_math_puzzle.MainActivity;
import com.titaniel.best_2048_math_puzzle.R;
import com.titaniel.best_2048_math_puzzle.database.Database;
import com.titaniel.best_2048_math_puzzle.fragments.AnimatedFragment;
import com.titaniel.best_2048_math_puzzle.utils.AnimUtils;

public class Won extends AnimatedFragment {

    private View mRoot;
    private TextView mTvScore, mTvHighscore;
    private ImageView mBtnHome, mBtnRestart, mBtnResume;
    private ConstraintLayout mContainer;

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
        mTvScore = mRoot.findViewById(R.id.tvScore);
        mTvHighscore = mRoot.findViewById(R.id.tvHighscore);
        mBtnHome = mRoot.findViewById(R.id.btnHome);
        mBtnRestart = mRoot.findViewById(R.id.btnRestart);
        mBtnResume = mRoot.findViewById(R.id.btnResume);
        mContainer = mRoot.findViewById(R.id.lyContainer);

        //btn restart
        mBtnRestart.setOnClickListener(v -> {
            mActivity.hideState(MainActivity.STATE_FM_WON, 0);
            mActivity.game.restart();
        });

        //btn home
        mBtnHome.setOnClickListener(v -> {
            Database.currentMode.saved = mActivity.game.gameField.getSaveImage();
            long delay = mActivity.hideState(MainActivity.STATE_FM_WON, 0);
            mActivity.showState(MainActivity.STATE_FM_HOME, delay, mActivity.game);
        });

        //btn resume
        mBtnResume.setOnClickListener(v -> {
            long delay = mActivity.hideState(MainActivity.STATE_FM_WON, 0);
            mActivity.game.enableAll(delay);
        });
    }

    private void updateScores() {
        mTvHighscore.setText(String.valueOf(Database.currentMode.record));
        mTvScore.setText(String.valueOf(Database.currentMode.points));
    }

    @Override
    protected void animateShow(long delay) {

        updateScores();

        mRoot.setVisibility(View.VISIBLE);

        long duration = 300;

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

    public void onBackPressed() {
        mBtnResume.callOnClick();
    }
}
