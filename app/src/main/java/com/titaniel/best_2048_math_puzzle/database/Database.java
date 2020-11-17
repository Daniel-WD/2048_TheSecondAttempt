package com.titaniel.best_2048_math_puzzle.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.titaniel.best_2048_math_puzzle.MainActivity;
import com.titaniel.best_2048_math_puzzle.R;
import com.titaniel.best_2048_math_puzzle.fragments.game.GameField;
import com.titaniel.best_2048_math_puzzle.leaderboard_manager.Leaderboard;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.Objects;

public class Database {
    
    
    public static final DateTimeZone STD_TIME_ZONE = DateTimeZone.forID("America/Los_Angeles");
    public static final DateTime INITIAL_TIME =
            new DateTime().withDate(2018, 10, 7).withTime(0, 0, 0, 0).withZoneRetainFields(Database.STD_TIME_ZONE);
    
    public static final int START_BACK_VALUE = 3;
    
    public static final String KEY_HIGHSCORE = "highscore-";
    public static final String KEY_TILE_RECORD = "tileRecord-";
    public static final String KEY_SCORE = "score-";
    public static final String KEY_HIGHEST_TILE = "highestTile-";
    public static final String KEY_BACKS = "backs-";
    
    public static final String KEY_TROPHY_7_DAYS_HIGHSCORE = "trophy7DaysHighscore-";
    public static final String KEY_TROPHY_7_DAYS_TILE_RECORD = "trophy7DaysTileRecord-";
    public static final String KEY_TROPHY_ONE_DAY_HIGHSCORE = "trophyOneDayHighscore-";
    public static final String KEY_TROPHY_ONE_DAY_TILE_RECORD = "trophyOneDayTileRecord-";
    
    public static final String KEY_LAST_RANK_ALL_TIME_HIGHSCORE = "lastRankAllTimeHighscore";
    public static final String KEY_LAST_RANK_ALL_TIME_TILE_RECORD = "lastRankAllTimeTileRecord";
    public static final String KEY_LAST_RANK_WEEKLY_HIGHSCORE = "lastRankWeeklyHighscore";
    public static final String KEY_LAST_RANK_WEEKLY_TILE_RECORD = "lastRankWeeklyTileRecord";
    public static final String KEY_LAST_RANK_DAILY_HIGHSCORE = "lastRankDailyHighscore";
    public static final String KEY_LAST_RANK_DAILY_TILE_RECORD = "lastRankDailyTileRecord";
    
    public static final String KEY_GAME_START_RANK_ALL_TIME_HIGHSCORE = "gameStartRankAllTimeHighscore";
    public static final String KEY_GAME_START_RANK_ALL_TIME_TILE_RECORD = "gameStartRankAllTimeTileRecord";
    public static final String KEY_GAME_START_RANK_WEEKLY_HIGHSCORE = "gameStartRankWeeklyHighscore";
    public static final String KEY_GAME_START_RANK_WEEKLY_TILE_RECORD = "gameStartRankWeeklyTileRecord";
    public static final String KEY_GAME_START_RANK_DAILY_HIGHSCORE = "gameStartRankDailyHighscore";
    public static final String KEY_GAME_START_RANK_DAILY_TILE_RECORD = "gameStartRankDailyTileRecord";
    
    public static String uid = "0";
    
    public static class TrophyHolder {
    
        public static final int TYPE_HIGHSCORE = 0;
        public static final int TYPE_TILE_RECORD = 1;
        
        public final int rank, type, size, timeRange;
    
        public TrophyHolder(int rank, int type, int size, int timeRange) {
            this.rank = rank;
            this.type = type;
            this.size = size;
            this.timeRange = timeRange;
        }
    
        @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            if(!(o instanceof TrophyHolder)) return false;
            TrophyHolder that = (TrophyHolder) o;
            return rank == that.rank &&
                    type == that.type &&
                    size == that.size &&
                    timeRange == that.timeRange;
        }
    
        @Override
        public int hashCode() {
        
            return Objects.hash(rank, type, size, timeRange);
        }
    }
    
    public static ArrayList<TrophyHolder> nextAvailableTrophies = new ArrayList<>();
    
    public static class Mode {
        
        //values must be like this
        public static final int TROPHY_NONE = 4;
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
        public int highscore = -1, tileRecord = -1;
        //        public int oneDayHighscore = -1, oneDayTileRecord = -1;
//        public int sevenDaysHighscore = -1, sevenDaysTileRecord = -1;
        public int score = 0, highestTile = 0, backs = 0;
        
        public Leaderboard leaderboard;
        public int lastRankDailyHighscore = -1;
        public int lastRankWeeklyHighscore = -1;
        public int lastRankAllTimeHighscore = -1;
        public int lastRankDailyTileRecord = -1;
        public int lastRankWeeklyTileRecord = -1;
        public int lastRankAllTimeTileRecord = -1;
        
        public int gameStartRankDailyHighscore = -1;
        public int gameStartRankWeeklyHighscore = -1;
        public int gameStartRankAllTimeHighscore = -1;
        public int gameStartRankDailyTileRecord = -1;
        public int gameStartRankWeeklyTileRecord = -1;
        public int gameStartRankAllTimeTileRecord = -1;
        
        public Mode(int representative, int fieldSize, int id) {
            this.representative = representative;
            this.fieldSize = fieldSize;
            this.id = id;
            leaderboard = new Leaderboard(this);
        }
        
        public void feedLeaderboard() {
            leaderboard.currentUserData.feed(tileRecord, highscore);
        }
        
        @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            if(!(o instanceof Mode)) return false;
            Mode mode = (Mode) o;
            return representative == mode.representative &&
                    fieldSize == mode.fieldSize &&
                    id == mode.id &&
                    highscore == mode.highscore &&
                    tileRecord == mode.tileRecord &&
                    score == mode.score &&
                    highestTile == mode.highestTile &&
                    backs == mode.backs &&
                    Objects.equals(saved, mode.saved);
        }
        
        @Override
        public int hashCode() {
            
            return Objects.hash(representative, fieldSize, id, saved, highscore, tileRecord, score, highestTile, backs);
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
    
    public static void init(MainActivity activity) {
        sPrefs = activity.getPreferences(Context.MODE_PRIVATE);
    }
    
    public static void load() {
        
        for(Mode mode : modes) {
            mode.highscore = sPrefs.getInt(KEY_HIGHSCORE + mode.id, 0);
            mode.tileRecord = sPrefs.getInt(KEY_TILE_RECORD + mode.id, 0);
            mode.score = sPrefs.getInt(KEY_SCORE + mode.id, 0);
            mode.highestTile = sPrefs.getInt(KEY_HIGHEST_TILE + mode.id, 0);
            mode.backs = sPrefs.getInt(KEY_BACKS + mode.id, 0);
            
            mode.trophy7DaysHighscore = sPrefs.getInt(KEY_TROPHY_7_DAYS_HIGHSCORE + mode.id, Mode.TROPHY_NONE);
            mode.trophy7DaysTileRecord = sPrefs.getInt(KEY_TROPHY_7_DAYS_TILE_RECORD + mode.id, Mode.TROPHY_NONE);
            mode.trophyOneDayHighscore = sPrefs.getInt(KEY_TROPHY_ONE_DAY_HIGHSCORE + mode.id, Mode.TROPHY_NONE);
            mode.trophyOneDayTileRecord = sPrefs.getInt(KEY_TROPHY_ONE_DAY_TILE_RECORD + mode.id, Mode.TROPHY_NONE);

//            mode.trophy7DaysHighscore = Mode.TROPHY_NONE;
//            mode.trophy7DaysTileRecord = Mode.TROPHY_NONE;
//            mode.trophyOneDayHighscore = Mode.TROPHY_NONE;
//            mode.trophyOneDayTileRecord = Mode.TROPHY_NONE;
            
            mode.lastRankAllTimeHighscore = sPrefs.getInt(KEY_LAST_RANK_ALL_TIME_HIGHSCORE + mode.id, -1);
            mode.lastRankAllTimeTileRecord = sPrefs.getInt(KEY_LAST_RANK_ALL_TIME_TILE_RECORD + mode.id, -1);
            mode.lastRankWeeklyHighscore = sPrefs.getInt(KEY_LAST_RANK_WEEKLY_HIGHSCORE + mode.id, -1);
            mode.lastRankWeeklyTileRecord = sPrefs.getInt(KEY_LAST_RANK_WEEKLY_TILE_RECORD + mode.id, -1);
            mode.lastRankDailyHighscore = sPrefs.getInt(KEY_LAST_RANK_DAILY_HIGHSCORE + mode.id, -1);
            mode.lastRankDailyTileRecord = sPrefs.getInt(KEY_LAST_RANK_DAILY_TILE_RECORD + mode.id, -1);
            
            mode.gameStartRankAllTimeHighscore = sPrefs.getInt(KEY_GAME_START_RANK_ALL_TIME_HIGHSCORE + mode.id, -1);
            mode.gameStartRankAllTimeTileRecord = sPrefs.getInt(KEY_GAME_START_RANK_ALL_TIME_TILE_RECORD + mode.id, -1);
            mode.gameStartRankWeeklyHighscore = sPrefs.getInt(KEY_GAME_START_RANK_WEEKLY_HIGHSCORE + mode.id, -1);
            mode.gameStartRankWeeklyTileRecord = sPrefs.getInt(KEY_GAME_START_RANK_WEEKLY_TILE_RECORD + mode.id, -1);
            mode.gameStartRankDailyHighscore = sPrefs.getInt(KEY_GAME_START_RANK_DAILY_HIGHSCORE + mode.id, -1);
            mode.gameStartRankDailyTileRecord = sPrefs.getInt(KEY_GAME_START_RANK_DAILY_TILE_RECORD + mode.id, -1);
            
            mode.saved = DataUtils.stringToImage(sPrefs.getString("image-" + mode.id, null));
        }
        
    }
    
    public static void save() {
        SharedPreferences.Editor editor = sPrefs.edit();
        
        for(Mode mode : modes) {
            editor.putInt(KEY_HIGHSCORE + mode.id, mode.highscore);
            editor.putInt(KEY_TILE_RECORD + mode.id, mode.tileRecord);
            editor.putInt(KEY_SCORE + mode.id, mode.score);
            editor.putInt(KEY_HIGHEST_TILE + mode.id, mode.highestTile);
            editor.putInt(KEY_BACKS + mode.id, mode.backs);
    
//            mode.trophy7DaysHighscore = Mode.TROPHY_NONE;
//            mode.trophy7DaysTileRecord = Mode.TROPHY_NONE;
//            mode.trophyOneDayHighscore = Mode.TROPHY_NONE;
//            mode.trophyOneDayTileRecord = Mode.TROPHY_NONE;
    
            editor.putInt(KEY_TROPHY_7_DAYS_HIGHSCORE + mode.id, mode.trophy7DaysHighscore);
            editor.putInt(KEY_TROPHY_7_DAYS_TILE_RECORD + mode.id, mode.trophy7DaysTileRecord);
            editor.putInt(KEY_TROPHY_ONE_DAY_HIGHSCORE + mode.id, mode.trophyOneDayHighscore);
            editor.putInt(KEY_TROPHY_ONE_DAY_TILE_RECORD + mode.id, mode.trophyOneDayTileRecord);
            
            editor.putInt(KEY_LAST_RANK_ALL_TIME_HIGHSCORE + mode.id, mode.lastRankAllTimeHighscore);
            editor.putInt(KEY_LAST_RANK_ALL_TIME_TILE_RECORD + mode.id, mode.lastRankAllTimeTileRecord);
            editor.putInt(KEY_LAST_RANK_WEEKLY_HIGHSCORE + mode.id, mode.lastRankWeeklyHighscore);
            editor.putInt(KEY_LAST_RANK_WEEKLY_TILE_RECORD + mode.id, mode.lastRankWeeklyTileRecord);
            editor.putInt(KEY_LAST_RANK_DAILY_HIGHSCORE + mode.id, mode.lastRankDailyHighscore);
            editor.putInt(KEY_LAST_RANK_DAILY_TILE_RECORD + mode.id, mode.lastRankDailyTileRecord);
            
            editor.putInt(KEY_GAME_START_RANK_ALL_TIME_HIGHSCORE + mode.id, mode.gameStartRankAllTimeHighscore);
            editor.putInt(KEY_GAME_START_RANK_ALL_TIME_TILE_RECORD + mode.id, mode.gameStartRankAllTimeTileRecord);
            editor.putInt(KEY_GAME_START_RANK_WEEKLY_HIGHSCORE + mode.id, mode.gameStartRankWeeklyHighscore);
            editor.putInt(KEY_GAME_START_RANK_WEEKLY_TILE_RECORD + mode.id, mode.gameStartRankWeeklyTileRecord);
            editor.putInt(KEY_GAME_START_RANK_DAILY_HIGHSCORE + mode.id, mode.gameStartRankDailyHighscore);
            editor.putInt(KEY_GAME_START_RANK_DAILY_TILE_RECORD + mode.id, mode.gameStartRankDailyTileRecord);
            
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
        return modes[curModePos() + 1];
    }
    
    public static Mode previousMode() {
        if(!hasPreviousMode()) return null;
        return modes[curModePos() - 1];
    }
    
    public static boolean hasNextMode() {
        return curModePos() < modes.length - 1;
    }
    
    public static boolean hasPreviousMode() {
        return curModePos() != 0;
    }
    
}
