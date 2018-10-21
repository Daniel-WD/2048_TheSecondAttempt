package com.titaniel.best_2048_math_puzzle.fragments.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.titaniel.best_2048_math_puzzle.MainActivity;
import com.titaniel.best_2048_math_puzzle.R;
import com.titaniel.best_2048_math_puzzle.fragments.AnimatedFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Trophy extends AnimatedFragment {

    private View mRoot;

    private ConstraintLayout mLyContainer;

    private ImageView mIvBackground;

    private TextView mTvNewTrophy;

    private ImageView mIvEffectSmall, mIvEffectBig;
    private ImageView mIvTrophy;

    private ImageView mIvDetailsBackground;
    private View mDivLeft, mDivRight;
    private TextView mTvType, mTvTimeRange, mTvFieldSize;

    private ImageView mIvBtnTrophyChamber, mIvBtnOk;
    private TextView mTvTrophyChamberText, mTvOkText;

    private MainActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trophy, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        mActivity = (MainActivity) getActivity();

        mRoot = getView();
        mLyContainer = mRoot.findViewById(R.id.lyContainer);
        mIvBackground = mRoot.findViewById(R.id.ivBackground);
        mTvNewTrophy = mRoot.findViewById(R.id.tvNewTrophy);
        mIvEffectSmall = mRoot.findViewById(R.id.ivEffectSmall);
        mIvEffectBig = mRoot.findViewById(R.id.ivEffectBig);
        mIvTrophy = mRoot.findViewById(R.id.ivTrophy);
        mIvDetailsBackground = mRoot.findViewById(R.id.ivDetailsBackground);
        mDivLeft = mRoot.findViewById(R.id.vDivLeft);
        mDivRight = mRoot.findViewById(R.id.vDivRight);
        mTvType = mRoot.findViewById(R.id.tvTrophyType);
        mTvTimeRange = mRoot.findViewById(R.id.tvTrophyTimeRange);
        mTvFieldSize = mRoot.findViewById(R.id.tvTrophySize);
        mIvBtnTrophyChamber = mRoot.findViewById(R.id.ivTrophyChamber);
        mIvBtnOk = mRoot.findViewById(R.id.ivOk);
        mTvTrophyChamberText = mRoot.findViewById(R.id.tvTrophyChamber);
        mTvOkText = mRoot.findViewById(R.id.tvOk);

        //btn trophy chamber
        mIvBtnTrophyChamber.setOnClickListener(view -> {
            mActivity.showState(MainActivity.STATE_FM_TROPHY_CHAMBER, 0, this);
        });

        //btn ok
        mIvBtnOk.setOnClickListener(view -> {
            mActivity.showState(MainActivity.STATE_FM_HOME, 0, this);
        });
    }

    @Override
    protected void animateShow(long delay) {

        mRoot.setVisibility(View.VISIBLE);

    }

    @Override
    protected long animateHide(long delay) {
        return 0;
    }

    public void onBackPressed() {

    }

}