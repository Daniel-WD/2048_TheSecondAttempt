package com.titaniel.best_2048_math_puzzle.fragments.dialog;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
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

public class Pause extends AnimatedFragment {

    private View mRoot;

    private ConstraintLayout mLyContainer;

    private ImageView mIvBackground;

    private TextView mTvPause;

    private ImageView mIvBtnClose, mIvBtnRestart, mIvBtnHome;

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
        mLyContainer = mRoot.findViewById(R.id.lyContainer);
        mIvBackground = mRoot.findViewById(R.id.ivBackground);
        mTvPause = mRoot.findViewById(R.id.tvPause);
        mIvBtnClose = mRoot.findViewById(R.id.ivClose);
        mIvBtnRestart = mRoot.findViewById(R.id.ivRestart);
        mIvBtnHome = mRoot.findViewById(R.id.ivHome);

        //btn restart
        mIvBtnRestart.setOnClickListener(v -> {
            mActivity.hideState(MainActivity.STATE_FM_PAUSE, 0);
            mActivity.game.restart();
        });

        //btn home
        mIvBtnHome.setOnClickListener(v -> {
            Database.currentMode.saved = mActivity.game.gameField.getSaveImage();
            long delay = mActivity.hideState(MainActivity.STATE_FM_PAUSE, 0);
            mActivity.showState(MainActivity.STATE_FM_HOME, delay, mActivity.game);
        });

        //btn close
        mIvBtnClose.setOnClickListener(v -> {
            long delay = mActivity.hideState(MainActivity.STATE_FM_PAUSE, 0);
            mActivity.game.enableAll(delay);
        });
    }

    @Override
    protected void animateShow(long delay) {
    
        mActivity.state = MainActivity.STATE_FM_PAUSE;
        
        mRoot.setVisibility(View.VISIBLE);

//        long duration = 250;
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

        handler.postDelayed(() -> {
            mRoot.setVisibility(View.INVISIBLE);
        }, duration);


        return duration+50;
    }

    public void onBackPressed() {
        mIvBtnClose.callOnClick();
    }
}
