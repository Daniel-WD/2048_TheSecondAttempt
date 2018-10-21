package com.titaniel.best_2048_math_puzzle.fragments.trophy_chamber;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.titaniel.best_2048_math_puzzle.MainActivity;
import com.titaniel.best_2048_math_puzzle.R;
import com.titaniel.best_2048_math_puzzle.fragments.AnimatedFragment;
import com.titaniel.best_2048_math_puzzle.utils.AnimUtils;
import com.titaniel.best_2048_math_puzzle.utils.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TrophyChamber extends AnimatedFragment {

    private View mRoot;

    private ImageView mIvBackground;

    private TextView mTvTitle;

    private ImageView mIvHeaderBg;
    private View mDivCenter, mDivLeft, mDivRight;
    private TextView mTvHighscore, mTvTileRecord;
    private TextView mTv7DaysHs, mTv7DaysTr, mTvOneDayHs, mTvOneDayTr;
    private RecyclerView mRvTrophyList;

    private MainActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trophy_chamber, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        mActivity = (MainActivity) getActivity();

        mRoot = getView();
        mIvBackground = mRoot.findViewById(R.id.ivBackground);
        mTvTitle = mRoot.findViewById(R.id.tvTitle);
        mIvHeaderBg = mRoot.findViewById(R.id.ivHeaderBg);
        mDivCenter = mRoot.findViewById(R.id.vDivCenter);
        mDivLeft = mRoot.findViewById(R.id.vDivLeft);
        mDivRight = mRoot.findViewById(R.id.vDivRight);
        mTvHighscore = mRoot.findViewById(R.id.tvHighscore);
        mTvTileRecord = mRoot.findViewById(R.id.tvTileRecord);
        mTv7DaysHs = mRoot.findViewById(R.id.tv7DaysHs);
        mTv7DaysTr = mRoot.findViewById(R.id.tv7DaysTr);
        mTvOneDayHs = mRoot.findViewById(R.id.tvOneDayHs);
        mTvOneDayTr = mRoot.findViewById(R.id.tvOneDayTr);
        mRvTrophyList = mRoot.findViewById(R.id.rvTrophyList);

        //trophy list
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        TrophyAdapter adapter = new TrophyAdapter(getContext());
        mRvTrophyList.setLayoutManager(linearLayoutManager);
        mRvTrophyList.setAdapter(adapter);
        mRvTrophyList.setHasFixedSize(true);

        //scroll header and fade title
        mRvTrophyList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            float lastAlpha = 1;

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                float offset = recyclerView.computeVerticalScrollOffset();

                //header
                Utils.setTranslationYBulk(-offset, mDivCenter, mDivLeft, mDivRight,
                        mTvHighscore, mTvTileRecord, mTvOneDayHs, mTvOneDayTr, mTv7DaysHs, mTv7DaysTr, mIvHeaderBg);

                //title fade

                float threshold = 20;
                float alpha = offset > threshold ? 0 : 1;

                if(lastAlpha != alpha) {
                    AnimUtils.animateAlpha(mTvTitle, new AccelerateDecelerateInterpolator(), alpha, 200, 0);
                }

                lastAlpha = alpha;

                super.onScrolled(recyclerView, dx, dy);
            }
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
        mActivity.showState(MainActivity.STATE_FM_HOME, 0, this);
    }

}