package com.titaniel.best_2048_math_puzzle.fragments.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.reward.RewardItem;
import com.titaniel.best_2048_math_puzzle.MainActivity;
import com.titaniel.best_2048_math_puzzle.R;
import com.titaniel.best_2048_math_puzzle.admob.Admob;
import com.titaniel.best_2048_math_puzzle.database.Database;
import com.titaniel.best_2048_math_puzzle.fragments.AnimatedFragment;
import com.titaniel.best_2048_math_puzzle.utils.AnimUtils;
import com.titaniel.best_2048_math_puzzle.utils.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Backs extends AnimatedFragment {

    private Admob.MyAdListener mAdListener = new Admob.MyAdListener() {

        boolean reward = false;

        @Override
        public void onRewardedVideoAdClosed() {
            if(reward) {
                handler.postDelayed(() -> {
                    Database.currentMode.backs += 4;

                    mActivity.hideState(MainActivity.STATE_FM_BACKS, 0);
                    mActivity.game.enableAll(0);
                }, 300);

                handler.postDelayed(() -> {
                    mActivity.game.performBack();
                }, 500);

                reward = false;
            }
        }

        @Override
        public void onRewarded(RewardItem rewardItem) {
            reward = true;
        }

    };

    private View mRoot;

    private ImageView mIvBackground;

    private TextView mTvMoveOn;

    private ImageView mIvBtnGetBacks, mIvBtnGiveUp;

    private MainActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_backs, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        mActivity = (MainActivity) getActivity();

        //init
        mRoot = getView();
        mIvBackground = mRoot.findViewById(R.id.ivBackground);
        mTvMoveOn = mRoot.findViewById(R.id.tvMoveOn);
        mIvBtnGetBacks = mRoot.findViewById(R.id.ivGetBacks);
        mIvBtnGiveUp = mRoot.findViewById(R.id.ivGiveUp);

        //btn get backs
        mIvBtnGetBacks.setOnClickListener(v -> {
            if(Admob.rewardedVideoAd.isLoaded()) {
                Admob.rewardedVideoAd.show();
            }
        });

        //btn give up
        mIvBtnGiveUp.setOnClickListener(v -> {
            mActivity.showState(MainActivity.STATE_FM_GAME_OVER, 0, this);
        });

    }

    @Override
    protected void animateShow(long delay) {

        Admob.adListener = mAdListener;
    
        mRoot.setVisibility(View.VISIBLE);
        mRoot.setAlpha(0);
        mRoot.setTranslationY(-Utils.dpToPx(getResources(), 16));
        AnimUtils.animateAlpha(mRoot, new DecelerateInterpolator(), 1, 300, delay);
        AnimUtils.animateTranslationY(mRoot, new DecelerateInterpolator(), 0, 300, delay);
        
    }

    @Override
    protected long animateHide(long delay) {

        Admob.adListener = null;
    
        long duration = 300;
    
        AnimUtils.animateAlpha(mRoot, new AccelerateInterpolator(), 0, duration, delay);
        AnimUtils.animateTranslationY(mRoot, new AccelerateInterpolator(), Utils.dpToPx(getResources(), 16), duration, delay);
    
        handler.postDelayed(() -> {
            mRoot.setVisibility(View.VISIBLE);
        }, 300);
    
    
        return duration + 50;
    }

    public void onBackPressed() {
        mIvBtnGiveUp.callOnClick();
    }
}
