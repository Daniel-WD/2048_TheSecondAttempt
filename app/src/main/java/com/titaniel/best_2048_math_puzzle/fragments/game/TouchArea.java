package com.titaniel.best_2048_math_puzzle.fragments.game;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TouchArea extends View {

    GameField gameField;

    public TouchArea(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return !gameField.inSelectMode && gameField.onTouchEvent(event);
    }

}
