package com.titaniel.math_puzzle.database;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;

import com.titaniel.math_puzzle.R;

import java.util.ArrayList;

public class DesignProvider {

    public static int[] colors;

    public static class TileDesign {

        TileDesign(Drawable[] ts, int[] colors, Drawable incrEff) {
            this.tiles = ts;
            this.effColors = colors;
            this.increaseEffect = incrEff;
        }

        public Drawable[] tiles;
        public Drawable increaseEffect;
        public int[] effColors;

    }

    public static ArrayList<TileDesign> tileDesigns = new ArrayList<>();

    public static void init(Context context) {

        colors = new int[24];

        //colors
        float[] firstColor = {282, 0.64f, 0.93f};

        for(int i = 0; i < 6; i++) {
            colors[i] = Color.HSVToColor(firstColor);
            firstColor[2] -= 0.1f;
        }

        float[] secondColor = {214, 0.70f, 0.84f};

        for(int i = 6; i < 10; i++) {
            colors[i] = Color.HSVToColor(secondColor);
            secondColor[2] -= 0.1f;
        }

        float[] thirdColor = {170, 0.99f, 0.79f};

        for(int i = 10; i < colors.length; i++) {
            colors[i] = Color.HSVToColor(thirdColor);
            thirdColor[2] -= 0.1f;
        }

        Drawable[] tiles = {
                context.getDrawable(R.drawable.tile_0),
                context.getDrawable(R.drawable.tile_1),
                context.getDrawable(R.drawable.tile_2),
                context.getDrawable(R.drawable.tile_3),
                context.getDrawable(R.drawable.tile_4),
                context.getDrawable(R.drawable.tile_5),
                context.getDrawable(R.drawable.tile_6),
                context.getDrawable(R.drawable.tile_7),
                context.getDrawable(R.drawable.tile_8),
                context.getDrawable(R.drawable.tile_9),
                context.getDrawable(R.drawable.tile_10),
                context.getDrawable(R.drawable.tile_11),
                context.getDrawable(R.drawable.tile_12),
                context.getDrawable(R.drawable.tile_13),
                context.getDrawable(R.drawable.tile_14),
                context.getDrawable(R.drawable.tile_15)
        };
        int[] colors = {
                Color.parseColor("#FC008C"),
                Color.parseColor("#CA0070"),
                Color.parseColor("#960053"),
                Color.parseColor("#C500FC"),
                Color.parseColor("#9E00CA"),
                Color.parseColor("#760096"),
                Color.parseColor("#0A90FF"),
                Color.parseColor("#0873CC"),
                Color.parseColor("#065699"),
                Color.parseColor("#00FCC6"),
                Color.parseColor("#00C99D"),
                Color.parseColor("#009676"),
                Color.parseColor("#FF3B3B"),
                Color.parseColor("#CC2F2F"),
                Color.parseColor("#992323"),
                Color.parseColor("#FF3B9A"),
        };

        Drawable effect = context.getDrawable(R.drawable.tile_rect);

        TileDesign design = new TileDesign(tiles, colors, effect);

        tileDesigns.add(design);

        colorize();
    }

    public static void setSize(int size) {
        for(TileDesign tileDesign : tileDesigns) {
            tileDesign.increaseEffect.setBounds(0, 0, size, size);
            for(Drawable drawable : tileDesign.tiles) {
                drawable.setBounds(0, 0, size, size);
            }
        }
    }

    private static void colorize() {

        for(TileDesign tileDesign : tileDesigns) {
            Drawable[] tileDrawables = tileDesign.tiles;
            for(int i = 0; i < tileDrawables.length; i++) {
//                tiles[i].setTint(colors[i]);
            }
        }

    }

}
