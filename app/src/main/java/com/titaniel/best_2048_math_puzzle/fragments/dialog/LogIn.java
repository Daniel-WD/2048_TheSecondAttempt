package com.titaniel.best_2048_math_puzzle.fragments.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.titaniel.best_2048_math_puzzle.MainActivity;
import com.titaniel.best_2048_math_puzzle.R;
import com.titaniel.best_2048_math_puzzle.fragments.AnimatedFragment;
import com.titaniel.best_2048_math_puzzle.utils.AnimUtils;
import com.titaniel.best_2048_math_puzzle.utils.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class LogIn extends AnimatedFragment {

    private View mRoot;

    private ConstraintLayout mLyContainer;

    private ImageView mIvBackground;

    private TextView mTvLogin;

    private ImageView mIvLoginTextBg;

    private TextView mTvMessageText;

    private ImageView mIvBtnLogin, mIvBtnNo;

    private TextView mTvLoginText, mTvNoText;

    private MainActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        mActivity = (MainActivity) getActivity();

        mRoot = getView();
        mLyContainer = mRoot.findViewById(R.id.lyContainer);
        mIvBackground = mRoot.findViewById(R.id.ivBackground);
        mTvLogin = mRoot.findViewById(R.id.tvLogIn);
        mIvLoginTextBg = mRoot.findViewById(R.id.ivLoginTextBg);
        mTvMessageText = mRoot.findViewById(R.id.tvMessageText);
        mIvBtnLogin = mRoot.findViewById(R.id.ivLogIn);
        mIvBtnNo = mRoot.findViewById(R.id.ivNo);
        mTvLoginText = mRoot.findViewById(R.id.tvLogInText);
        mTvNoText = mRoot.findViewById(R.id.tvNoText);

        //login btn
        mIvBtnLogin.setOnClickListener(view -> {
            mActivity.showState(MainActivity.STATE_FM_HOME, 0, mActivity.goal);
            mActivity.startGameServicesSignInIntent();
        });

        //no btn
        mIvBtnNo.setOnClickListener(view -> {
            mActivity.showState(MainActivity.STATE_FM_HOME, 0, mActivity.goal);
        });

    }

    @Override
    protected void animateShow(long delay) {
    
        mActivity.state = MainActivity.STATE_FM_LOGIN;
    
        mRoot.setVisibility(View.VISIBLE);
        mRoot.setAlpha(0);
        mRoot.setTranslationY(-Utils.dpToPx(getResources(), 16));
        AnimUtils.animateAlpha(mRoot, new DecelerateInterpolator(), 1, 300, delay);
        AnimUtils.animateTranslationY(mRoot, new DecelerateInterpolator(), 0, 300, delay);

    }

    @Override
    protected long animateHide(long delay) {
    
        long duration = 300;
    
        AnimUtils.animateAlpha(mRoot, new AccelerateInterpolator(), 0, duration, delay);
        AnimUtils.animateTranslationY(mRoot, new AccelerateInterpolator(), Utils.dpToPx(getResources(), 16), duration, delay);
    
        handler.postDelayed(() -> {
            mRoot.setVisibility(View.VISIBLE);
        }, 300);
    
    
        return duration + 50;
    }

    public void onBackPressed() {

    }

}