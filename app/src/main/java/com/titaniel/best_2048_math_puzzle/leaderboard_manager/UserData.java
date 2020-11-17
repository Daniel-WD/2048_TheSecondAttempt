package com.titaniel.best_2048_math_puzzle.leaderboard_manager;

import com.google.firebase.firestore.Exclude;
import com.titaniel.best_2048_math_puzzle.utils.Utils;

public class UserData {
    
    public String uid;
    long highscoreAllTime = -1;
    long highscoreWeekly = -1;
    long highscoreDaily = -1;
    long tileRecordAllTime = -1;
    long tileRecordWeekly = -1;
    long tileRecordDaily = -1;
    String timeString = "";
    
    @Exclude boolean hasNewData = true;
    
    public UserData() {
    }

    public UserData(String uid, long highscoreAllTime, long highscoreWeekly,
                    long highscoreDaily, long tileRecordAllTime, long tileRecordWeekly,
                    long tileRecordDaily, String timeString) {
        this.uid = uid;
        this.highscoreAllTime = highscoreAllTime;
        this.highscoreWeekly = highscoreWeekly;
        this.highscoreDaily = highscoreDaily;
        this.tileRecordAllTime = tileRecordAllTime;
        this.tileRecordWeekly = tileRecordWeekly;
        this.tileRecordDaily = tileRecordDaily;
        this.timeString = timeString;
    }
    
    public void feed(long tileRecord, long highscore) {
        if(highscore > highscoreAllTime) {
            highscoreAllTime = highscore;
            hasNewData = true;
        }
        if(tileRecord > tileRecordAllTime) {
            tileRecordAllTime = tileRecord;
            hasNewData = true;
        }
        if(highscore > highscoreWeekly) {
            highscoreWeekly = highscore;
            hasNewData = true;
        }
        if(tileRecord > tileRecordWeekly) {
            tileRecordWeekly = tileRecord;
            hasNewData = true;
        }
        if(highscore > highscoreDaily) {
            highscoreDaily = highscore;
            hasNewData = true;
        }
        if(tileRecord > tileRecordDaily) {
            tileRecordDaily = tileRecord;
            hasNewData = true;
        }
        
        timeString = Utils.currentZonedTimeString();
        
    }

    public String getUid() {
        return uid;
    }

    public long getHighscoreAllTime() {
        return highscoreAllTime;
    }

    public long getHighscoreWeekly() {
        return highscoreWeekly;
    }

    public long getHighscoreDaily() {
        return highscoreDaily;
    }

    public long getTileRecordAllTime() {
        return tileRecordAllTime;
    }

    public long getTileRecordWeekly() {
        return tileRecordWeekly;
    }

    public long getTileRecordDaily() {
        return tileRecordDaily;
    }

    public String getTimeString() {
        return timeString;
    }
}
