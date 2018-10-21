package com.titaniel.best_2048_math_puzzle.fragments.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.titaniel.best_2048_math_puzzle.MainActivity;
import com.titaniel.best_2048_math_puzzle.R;
import com.titaniel.best_2048_math_puzzle.database.Database;
import com.titaniel.best_2048_math_puzzle.fragments.AnimatedFragment;

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

        mRoot.setVisibility(View.VISIBLE);

    }

    @Override
    protected long animateHide(long delay) {

        mRoot.setVisibility(View.INVISIBLE);

        return 0;
    }

    public void onBackPressed() {

    }

}
