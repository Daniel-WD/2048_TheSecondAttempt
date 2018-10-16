package com.titaniel.best_2048_math_puzzle.fragments.dialog;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.titaniel.best_2048_math_puzzle.MainActivity;
import com.titaniel.best_2048_math_puzzle.R;
import com.titaniel.best_2048_math_puzzle.admob.Admob;
import com.titaniel.best_2048_math_puzzle.database.Database;
import com.titaniel.best_2048_math_puzzle.fragments.AnimatedFragment;
import com.titaniel.best_2048_math_puzzle.utils.AnimUtils;
import com.titaniel.best_2048_math_puzzle.utils.Utils;

public class Backs extends AnimatedFragment {

    private RewardedVideoAdListener mAdListener = new RewardedVideoAdListener() {

        boolean reward = false;

        @Override
        public void onRewardedVideoAdLoaded() {

        }

        @Override
        public void onRewardedVideoAdOpened() {

        }

        @Override
        public void onRewardedVideoStarted() {

        }

        @Override
        public void onRewardedVideoAdClosed() {
            if(reward) {
                handler.postDelayed(() -> {
                    Database.currentMode.backs += 4;
                    mBtnUndoMove.callOnClick();
                }, 300);
                reward = false;
            }
        }

        @Override
        public void onRewarded(RewardItem rewardItem) {
            reward = true;
        }

        @Override
        public void onRewardedVideoAdLeftApplication() {

        }

        @Override
        public void onRewardedVideoAdFailedToLoad(int i) {

        }

        @Override
        public void onRewardedVideoCompleted() {

        }
    };

    private View mRoot;
    private TextView mTvTitle;
    private ImageView mBtnUndoMove, mBtnGetBacks, mBtnGiveUp;
    private ConstraintLayout mContainer;

    private MainActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_backs, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        Admob.rewardedVideoAdListener = mAdListener;
        mActivity = (MainActivity) getActivity();

        //init
        mRoot = getView();
//        mBtnUndoMove = mRoot.findViewById(R.id.btnUndo);
//        mBtnGiveUp = mRoot.findViewById(R.id.btnGiveUp);
//        mBtnGetBacks = mRoot.findViewById(R.id.btnGetBacks);
//        mTvTitle = mRoot.findViewById(R.id.tvTitle);
//        mContainer = mRoot.findViewById(R.id.lyContainer);
//
//        //btn backs move
//        mBtnUndoMove.setOnClickListener(v -> {
//            long delay = mActivity.hideState(MainActivity.STATE_FM_BACKS, 0);
//            mActivity.game.enableAll(delay);
//            handler.postDelayed(() -> {
//                mActivity.game.gameField.performBack();
//            }, delay + 400);
//        });
//
//        //btn get backs
//        mBtnGetBacks.setOnClickListener(v -> {
//            if(Admob.rewardedVideoAd.isLoaded()) {
//                Admob.rewardedVideoAd.show();
//            }
//        });
//
//        //btn give up
//        mBtnGiveUp.setOnClickListener(v -> {
//            mActivity.showState(MainActivity.STATE_FM_GAME_OVER, 0, this);
//        });

    }

    private void updateState() {
        if(Database.currentMode.backs > 0 || !Utils.isOnline(getContext())) {
            mBtnUndoMove.setVisibility(View.VISIBLE);
            mBtnGetBacks.setVisibility(View.INVISIBLE);
            mTvTitle.setText(R.string.undo_last_move);
        } else {
            mBtnGetBacks.setVisibility(View.VISIBLE);
            mBtnUndoMove.setVisibility(View.INVISIBLE);
            mTvTitle.setText(R.string.get_backs);
        }
    }

    @Override
    protected void animateShow(long delay) {

//        updateState();

        mRoot.setVisibility(View.VISIBLE);
//
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

        long duration = 200;

        AnimUtils.animateAlpha(mContainer, new AccelerateInterpolator(1f), 0, duration, delay);
        AnimUtils.animateScale(mContainer, new AccelerateInterpolator(1f), 0.8f, duration, delay);

        handler.postDelayed(() -> {
            mRoot.setVisibility(View.INVISIBLE);
        }, duration);


        return duration + 50;
    }

    public void onBackPressed() {
        mBtnGiveUp.callOnClick();
    }
}
