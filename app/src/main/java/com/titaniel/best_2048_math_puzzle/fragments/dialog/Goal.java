package com.titaniel.best_2048_math_puzzle.fragments.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.titaniel.best_2048_math_puzzle.MainActivity;
import com.titaniel.best_2048_math_puzzle.R;
import com.titaniel.best_2048_math_puzzle.fragments.AnimatedFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Goal extends AnimatedFragment {

    private View mRoot;
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
