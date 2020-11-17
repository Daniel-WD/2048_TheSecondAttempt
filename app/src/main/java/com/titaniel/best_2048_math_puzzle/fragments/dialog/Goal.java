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
import com.titaniel.best_2048_math_puzzle.database.Database;
import com.titaniel.best_2048_math_puzzle.fragments.AnimatedFragment;
import com.titaniel.best_2048_math_puzzle.utils.AnimUtils;
import com.titaniel.best_2048_math_puzzle.utils.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Goal extends AnimatedFragment {

    private View mRoot;

    private ConstraintLayout mLyContainer;

    private ImageView mIvBackground;

    private TextView mTvGoal;

    private ImageView mIvGoalTextBg;
    private TextView mTvMessageText;

    private ImageView mIvGotIt;
    private TextView mTvGotItText;

    private MainActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_goal, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        mActivity = (MainActivity) getActivity();

        mRoot = getView();
        mLyContainer = mRoot.findViewById(R.id.lyContainer);
        mIvBackground = mRoot.findViewById(R.id.ivBackground);
        mTvGoal = mRoot.findViewById(R.id.tvGoal);
        mIvGoalTextBg = mRoot.findViewById(R.id.ivGoalTextBg);
        mTvMessageText = mRoot.findViewById(R.id.tvMessageText);
        mIvGotIt = mRoot.findViewById(R.id.ivGotIt);
        mTvGotItText = mRoot.findViewById(R.id.tvGotItText);

        //got it
        mIvGotIt.setOnClickListener(view -> {
            mActivity.showState(MainActivity.STATE_FM_HOME, 0, mActivity.goal);
        });

    }

    @Override
    protected void animateShow(long delay) {
    
        mActivity.state = MainActivity.STATE_FM_GOAL;
        
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
