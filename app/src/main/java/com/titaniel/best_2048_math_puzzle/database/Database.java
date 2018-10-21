package com.titaniel.best_2048_math_puzzle.database;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;

import com.titaniel.best_2048_math_puzzle.R;
import com.titaniel.best_2048_math_puzzle.fragments.game.GameField;

import java.util.ArrayList;
import java.util.Objects;

public class Database {

    public static final int START_BACK_VALUE = 3;

    public static class Mode {

        public static final int TROPHY_NONE = 0;
        public static final int TROPHY_FIRST = 1;
        public static final int TROPHY_SECOND = 2;
        public static final int TROPHY_THIRD = 3;

        //trophies
        public int trophyOneDayHighscore = TROPHY_NONE;
        public int trophyOneDayTileRecord = TROPHY_NONE;
        public int trophy7DaysHighscore = TROPHY_NONE;
        public int trophy7DaysTileRecord = TROPHY_NONE;

        //stuff...
        final public int representative;
        final public int fieldSize;
        final int id;
        public ArrayList<GameField.FieldImage> saved;
        public int allTimeHighscore = -1, allTimeTileRecord = -1;
        public int oneDayHighscore = -1, oneDayTileRecord = -1;
        public int sevenDaysHighscore = -1, sevenDaysTileRecord = -1;
        public int score = 0, highestTile = 0, backs = 0;

        public Mode(int representative, int fieldSize, int id) {
            this.representative = representative;
            this.fieldSize = fieldSize;
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            if(!(o instanceof Mode)) return false;
            Mode mode = (Mode) o;
            return representative == mode.representative &&
                    fieldSize == mode.fieldSize &&
                    id == mode.id &&
                    allTimeHighscore == mode.allTimeHighscore &&
                    allTimeTileRecord == mode.allTimeTileRecord &&
                    score == mode.score &&
                    highestTile == mode.highestTile &&
                    backs == mode.backs &&
                    Objects.equals(saved, mode.saved);
        }

        @Override
        public int hashCode() {

            return Objects.hash(representative, fieldSize, id, saved, allTimeHighscore, allTimeTileRecord, score, highestTile, backs);
        }
    }

    public static final Mode[] modes = {
            new Mode(R.drawable.size_3, 3, -1),
            new Mode(R.drawable.size_4, 4, 0),
            new Mode(R.drawable.size_5, 5, 1),
            new Mode(R.drawable.size_6, 6, 2),
            new Mode(R.drawable.size_7, 7, 3),
            new Mode(R.drawable.size_8, 8, 4),
            new Mode(R.drawable.size_9, 9, 5)
    };
    public static Mode currentMode = modes[1];

    private static SharedPreferences sPrefs;

    public static void init(Context c) {
        sPrefs = ((AppCompatActivity) c).getPreferences(Context.MODE_PRIVATE);
    }

    public static void load() {

        long time = System.nanoTime();

        for(Mode mode : modes) {
            mode.allTimeHighscore = sPrefs.getInt("allTimeHighscore-" + mode.id, 0);
            mode.allTimeTileRecord = sPrefs.getInt("allTimeTileRecord-" + mode.id, 0);
            mode.score = sPrefs.getInt("score-" + mode.id, 0);
            mode.highestTile = sPrefs.getInt("highestTile-" + mode.id, 0);
            mode.backs = sPrefs.getInt("backs-" + mode.id, 0);

            mode.trophy7DaysHighscore = sPrefs.getInt("trophy7DaysHighscore-" + mode.id, Mode.TROPHY_NONE);
            mode.trophy7DaysTileRecord = sPrefs.getInt("trophy7DaysTileRecord-" + mode.id, Mode.TROPHY_NONE);
            mode.trophyOneDayHighscore = sPrefs.getInt("trophyOneDayHighscore-" + mode.id, Mode.TROPHY_NONE);
            mode.trophyOneDayTileRecord = sPrefs.getInt("trophyOneDayTileRecord-" + mode.id, Mode.TROPHY_NONE);

            mode.saved = DataUtils.stringToImage(sPrefs.getString("image-" + mode.id, null));
        }

    }

    public static void save() {
        SharedPreferences.Editor editor = sPrefs.edit();

        for(Mode mode : modes) {
            editor.putInt("allTimeHighscore-" + mode.id, mode.allTimeHighscore);
            editor.putInt("allTimeTileRecord-" + mode.id, mode.allTimeTileRecord);
            editor.putInt("score-" + mode.id, mode.score);
            editor.putInt("highestTile-" + mode.id, mode.highestTile);
            editor.putInt("backs-" + mode.id, mode.backs);

            editor.putInt("trophy7DaysHighscore-" + mode.id, mode.trophy7DaysHighscore);
            editor.putInt("trophy7DaysTileRecord-" + mode.id, mode.trophy7DaysTileRecord);
            editor.putInt("trophyOneDayHighscore-" + mode.id, mode.trophyOneDayHighscore);
            editor.putInt("trophyOneDayTileRecord-" + mode.id, mode.trophyOneDayTileRecord);

            String image = null;
            if(mode.saved != null && mode.saved.size() > 0) {
                image = DataUtils.imageToString(mode.saved);
            }

            editor.putString("image-" + mode.id, image);
        }

        editor.apply();
    }

    // MODE -> HOME

    public static int curModePos() {
        if(currentMode == null) return -1;
        int result = -1;
        for(int i = 0; i < modes.length; i++) {
            if(currentMode.equals(modes[i])) result = i;
        }
        return result;
    }

    public static Mode nextMode() {
        if(!hasNextMode()) return null;
        return modes[curModePos()+1];
    }

    public static Mode previousMode() {
        if(!hasPreviousMode()) return null;
        return modes[curModePos()-1];
    }

    public static boolean hasNextMode() {
        return curModePos() < modes.length-1;
    }

    public static boolean hasPreviousMode() {
        return curModePos() != 0;
    }

}
