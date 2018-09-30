package com.titaniel.best_2048_math_puzzle.database;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.titaniel.best_2048_math_puzzle.R;
import com.titaniel.best_2048_math_puzzle.fragments.game.GameField;

import java.util.ArrayList;
import java.util.Objects;

public class Database {

    public static final int START_BACK_VALUE = 3;

    public static class Mode {

        final public int representative;
        final public int fieldSize;
        public int record = 0;
        final int id;
        public ArrayList<GameField.FieldImage> saved;
        public int points = 0, backs = 0;

        public Mode(int representative, int fieldSize, int id) {
            this.representative = representative;
            this.fieldSize = fieldSize;
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            if(o == null || getClass() != o.getClass()) return false;
            Mode mode = (Mode) o;
            return fieldSize == mode.fieldSize &&
                    record == mode.record &&
                    id == mode.id &&
                    points == mode.points &&
                    Objects.equals(representative, mode.representative) &&
                    Objects.equals(saved, mode.saved);
        }

        @Override
        public int hashCode() {

            return Objects.hash(representative, fieldSize, record, id, saved, points);
        }
    }

    public interface MoneyListener {
        void onMoneyChanged(int oldValue, int newValue);
    }
    private final static ArrayList<MoneyListener> mMoneyListeners = new ArrayList<>();
    private static int money = 20000;

    public static final Mode[] modes = {
            new Mode(R.drawable.mode_2, 2, -2),
            new Mode(R.drawable.mode_3, 3, -1),
            new Mode(R.drawable.mode_4, 4, 0),
            new Mode(R.drawable.mode_5, 5, 1),
            new Mode(R.drawable.mode_6, 6, 2),
            new Mode(R.drawable.mode_7, 7, 3),
            new Mode(R.drawable.mode_8, 8, 4),
            new Mode(R.drawable.mode_9, 9, 5)
    };
    public static Mode currentMode = modes[2];

    private static SharedPreferences sPrefs;

    public static void init(Context c) {
        sPrefs = ((AppCompatActivity) c).getPreferences(Context.MODE_PRIVATE);
    }

    public static void load() {

        long time = System.nanoTime();

        for(Mode mode : modes) {
            mode.record = sPrefs.getInt("record-" + mode.id, 0);
            mode.points = sPrefs.getInt("points-" + mode.id, 0);
            mode.backs = sPrefs.getInt("undo-" + mode.id, 0);

            mode.saved = DataUtils.stringToImage(sPrefs.getString("image-" + mode.id, null));
        }

        Log.e("load_duration", "time: " + (System.nanoTime()-time));
    }

    public static void save() {
        SharedPreferences.Editor editor = sPrefs.edit();

        for(Mode mode : modes) {
            editor.putInt("record-" + mode.id, mode.record);
            editor.putInt("points-" + mode.id, mode.points);
            editor.putInt("undo-" + mode.id, mode.backs);

            String image = null;
            if(mode.saved != null && mode.saved.size() > 0) {
                image = DataUtils.imageToString(mode.saved);
            }

            editor.putString("image-" + mode.id, image);
        }

        editor.apply();
    }

    // MONEY

    public static int getMoney() {
        return money;
    }

    public static void addMoney(int m) {
        setMoney(money+m);
    }

    public static void removeMoney(int m) {
        setMoney(money-m);
    }

    private static void setMoney(int money) {
        int oldMoney = Database.money;
        Database.money = money;
        for(MoneyListener listener : mMoneyListeners) {
            listener.onMoneyChanged(oldMoney, money);
        }
    }

    public static void addMoneyListener(MoneyListener listener) {
        mMoneyListeners.add(listener);
    }

    public static void removeMoneyListener(MoneyListener listener) {
        mMoneyListeners.remove(listener);
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
