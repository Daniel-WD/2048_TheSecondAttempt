package com.titaniel.best_2048_math_puzzle.utils;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewUtils {
    
    public static void setVisibility(int visibility, View... views) {
        if(views == null || views.length == 0) return;
        for(View view : views) {
            view.setVisibility(visibility);
        }
    }
    
    public static void setText(String text, TextView... textViews) {
        if(textViews == null || textViews.length == 0) return;
        for(TextView textView : textViews) {
            textView.setText(text);
        }
    }
    
    public static void setImageDrawable(Drawable drawable, ImageView... imageViews) {
        if(imageViews == null || imageViews.length == 0) return;
        for(ImageView imageView : imageViews) {
            imageView.setImageDrawable(drawable);
        }
        
    }
    
}
