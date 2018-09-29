package com.titaniel.best_2048_math_puzzle.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.titaniel.best_2048_math_puzzle.MainActivity;
import com.titaniel.best_2048_math_puzzle.R;
import com.titaniel.best_2048_math_puzzle.database.Database;
import com.titaniel.best_2048_math_puzzle.utils.AnimUtils;

public class Home extends AnimatedFragment {

    private View mRoot;
    private ImageView mBtnPlay, mBtnResume, mBtnRate;
    private ImageView mIvTitle;
    private ImageView mIvMode;
    private ImageView mBtnPlus, mBtnMinus;
    private LinearLayout mLyButtons;

    private View mVDivOne, mVDivTwo;

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

        //moneylistener
        //Database.addMoneyListener((oldValue, newValue) -> mTvMoney.setText(String.valueOf(newValue)));

        //init
        mRoot = getView();
        mBtnPlay = mRoot.findViewById(R.id.btnPlay);
        mBtnResume = mRoot.findViewById(R.id.btnResume);
        mBtnPlus = mRoot.findViewById(R.id.btnModeNext);
        mBtnMinus = mRoot.findViewById(R.id.btnModePrevious);
        mBtnRate = mRoot.findViewById(R.id.btnRate);
        mBtnRate = mRoot.findViewById(R.id.btnRate);
        mLyButtons = mRoot.findViewById(R.id.lyButtons);
        mVDivOne = mRoot.findViewById(R.id.vDivOne);
        mVDivTwo = mRoot.findViewById(R.id.vDivTwo);
        mIvMode = mRoot.findViewById(R.id.ivMode);

        //achi
//        mBtnAchievements.setOnClickListener(v -> {
//            mActivity.showAchievements(0, this);
//        });

        //rate
        mBtnRate.setOnClickListener(v -> {
            if(mBlocking) return;
            Uri uri = Uri.parse("market://details?id=" + getContext().getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + getContext().getPackageName())));
            }
        });

        //resume
        mBtnResume.setOnClickListener(v -> {
            if(mBlocking) return;
            mActivity.game.loadGame = true;
            mActivity.showState(MainActivity.STATE_FM_GAME, 0, this);
        });

        //play
        mBtnPlay.setOnClickListener(v -> {
            if(mBlocking) return;
            Database.currentMode.points = 0;
            Database.currentMode.backs = Database.START_BACK_VALUE;
            mActivity.game.loadGame = false;
            mActivity.showState(MainActivity.STATE_FM_GAME, 0, this);
        });

        //money
//        mTvMoney.setText(String.valueOf(Database.getMoney()));

        //Button changeMode
        mBtnPlus.setOnClickListener(view -> {
            if(mBlocking) return;
            changeMode(false);
        });

        //Button previousMode
        mBtnMinus.setOnClickListener(view -> {
            if(mBlocking) return;
            changeMode(true);
        });

    }

    private void setMode(Database.Mode mode) {
        if(mode == null) return;
        mIvMode.setImageResource(mode.representative);
    }

    private void changeMode(boolean previous) {
        if(mBlocking || previous ? !Database.hasPreviousMode() : !Database.hasNextMode()) return;

//        block(350);

        Database.Mode mode = previous ? Database.previousMode() : Database.nextMode();
        Database.currentMode = mode;
        checkPeriph();

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

    private void checkPeriph() {
        if(Database.currentMode.saved != null) {
            AnimUtils.animateAlpha(mBtnResume, new AccelerateDecelerateInterpolator(), 1, 120, 0);
            mBtnResume.setEnabled(true);
//            AnimUtils.animateAlpha(mIvResumeShadow, 1, 200, 0);
        } else {
            AnimUtils.animateAlpha(mBtnResume, new AccelerateDecelerateInterpolator(), 0.5f, 120, 0);
            mBtnResume.setEnabled(false);
//            AnimUtils.animateAlpha(mIvResumeShadow, 0, 200, 0);
        }
    }

    private void block(long duration) {
        mBlocking = true;
        handler.postDelayed(mDeblocker, duration);
    }


    @Override
    protected void animateShow(long delay) {

        block(150);

        setMode(Database.currentMode);
        mRoot.setVisibility(View.VISIBLE);

        mRoot.setAlpha(0);
        AnimUtils.animateAlpha(mRoot, new AccelerateDecelerateInterpolator(), 1, 150, delay);

        delay += 150;

        handler.postDelayed(this::checkPeriph, delay);
    }

    @Override
    protected long animateHide(long delay) {

        AnimUtils.animateAlpha(mRoot, new AccelerateDecelerateInterpolator(), 0, 150, delay);

        delay += 150;

        handler.postDelayed(() -> mRoot.setVisibility(View.INVISIBLE), delay);

        return 150;
    }

}
