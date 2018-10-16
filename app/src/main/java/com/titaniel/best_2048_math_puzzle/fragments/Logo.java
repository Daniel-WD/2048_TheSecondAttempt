package com.titaniel.best_2048_math_puzzle.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.titaniel.best_2048_math_puzzle.MainActivity;
import com.titaniel.best_2048_math_puzzle.R;
import com.titaniel.best_2048_math_puzzle.utils.AnimUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

public class Logo extends AnimatedFragment {

    private View mRoot;

    private MainActivity mActivity;
    private ImageView mIvLogo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_logo, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        mActivity = (MainActivity) getActivity();

        //init
        mRoot = getView();
        mIvLogo = mRoot.findViewById(R.id.ivLogo);

    }

    private void presentLogo(long delay) {

        long fadeDuration = 400;
        long stayDuration = 1000;

        mIvLogo.setAlpha(0f);
        //show logo
        AnimUtils.animateAlpha(mIvLogo, new FastOutSlowInInterpolator(), 1, fadeDuration, delay);

        delay += fadeDuration;

        //init
        handler.postDelayed(() -> mActivity.initAll(), delay);

        delay += stayDuration;

        //hide logo
        handler.postDelayed(() -> {
            AnimUtils.animateAlpha(mIvLogo, new FastOutSlowInInterpolator(), 0, fadeDuration, 0);
        }, delay);

        delay += fadeDuration + 300;

        //show home
        handler.postDelayed(() -> {
            mActivity.showState(MainActivity.STATE_FM_GOAL, 0, mActivity.logo);
//            mActivity.signInSilently();
        }, delay);


    }

    protected void animateShow(long delay) {

        mRoot.setVisibility(View.VISIBLE);

        delay += 500;

        presentLogo(delay);

    }

    @Override
    protected long animateHide(long delay) {

        delay += 150;

        long duration = 200;

        AnimUtils.animateAlpha(mRoot, new AccelerateDecelerateInterpolator(), 0, duration, delay);
        delay += duration;
        handler.postDelayed(() -> mRoot.setVisibility(View.INVISIBLE), delay);

        return 0;
    }

}
