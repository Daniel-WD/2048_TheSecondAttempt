package com.titaniel.best_2048_math_puzzle.leaderboard_manager;

import com.google.firebase.firestore.DocumentSnapshot;
import com.titaniel.best_2048_math_puzzle.database.Database;
import com.titaniel.best_2048_math_puzzle.utils.Utils;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Leaderboard {
    
    public static final int RANGE_ALL_TIME = 0;
    public static final int RANGE_WEEKLY = 1;
    public static final int RANGE_DAILY = 2;
    
    private Comparator<UserData> highscoreAllTimeComparator = (o1, o2) -> Long.compare(o2.highscoreAllTime, o1.highscoreAllTime);
    private Comparator<UserData> highscoreWeeklyComparator = (o1, o2) -> Long.compare(o2.highscoreWeekly, o1.highscoreWeekly);
    private Comparator<UserData> highscoreDailyComparator = (o1, o2) -> Long.compare(o2.highscoreDaily, o1.highscoreDaily);
    private Comparator<UserData> tileRecordAllTimeComparator = (o1, o2) -> Long.compare(o2.tileRecordAllTime, o1.tileRecordAllTime);
    private Comparator<UserData> tileRecordWeeklyComparator = (o1, o2) -> Long.compare(o2.tileRecordWeekly, o1.tileRecordWeekly);
    private Comparator<UserData> tileRecordDailyComparator = (o1, o2) -> Long.compare(o2.tileRecordDaily, o1.tileRecordDaily);
    
    private final Database.Mode mode;
    private UserData[] allUserData = new UserData[0];
    private UserData[] validWeeklyUserData = new UserData[0];
    private UserData[] validDailyUserData = new UserData[0];
    private UserData[] validBeforeWeeklyUserData = new UserData[0];
    private UserData[] validBeforeDailyUserData = new UserData[0];
    
    public UserData currentUserData;
    
    public Leaderboard(Database.Mode mode) {
        this.mode = mode;
    }
    
    String leaderboardName() {
        return "leaderboard_" + mode.fieldSize;
    }
    
    void updateUserData(List<DocumentSnapshot> snapshots) {
        allUserData = new UserData[snapshots.size()];
        for(int i = 0; i < snapshots.size(); i++) {
            allUserData[i] = snapshots.get(i).toObject(UserData.class);
        }
        currentUserData = findMyUserData(Database.uid);
        if(currentUserData == null) {
            currentUserData = new UserData();
            currentUserData.uid = Database.uid;
        }
    }
    
    private void calcValidUserData() {
        
        DateTime weeklyValidDate = Utils.calcWeeklyValidDateTime();
        DateTime beforeWeeklyValidDate = Utils.calcWeeklyValidDateTime().minusDays(7);
        DateTime dailyValidDate = Utils.calcDailyValidDateTime();
        DateTime beforeDailyValidDate = Utils.calcDailyValidDateTime().minusDays(1);
        
        ArrayList<UserData> validWeeklyData = new ArrayList<>();
        ArrayList<UserData> validBeforeWeeklyData = new ArrayList<>();
        ArrayList<UserData> validDailyData = new ArrayList<>();
        ArrayList<UserData> validBeforeDailyData = new ArrayList<>();
        
        for(UserData userData : allUserData) {
            if(Utils.isValidDate(userData.timeString, weeklyValidDate)) {
                validWeeklyData.add(userData);
            }
            if(Utils.isValidDate(userData.timeString, dailyValidDate)) {
                validDailyData.add(userData);
            }
            if(Utils.isValidDate(userData.timeString, beforeWeeklyValidDate)) {
                validBeforeWeeklyData.add(userData);
            }
            if(Utils.isValidDate(userData.timeString, beforeDailyValidDate)) {
                validBeforeDailyData.add(userData);
            }
        }
        
        validWeeklyUserData = validWeeklyData.toArray(new UserData[0]);
        validBeforeWeeklyUserData = validBeforeWeeklyData.toArray(new UserData[0]);
        validDailyUserData = validDailyData.toArray(new UserData[0]);
        validBeforeDailyUserData = validBeforeDailyData.toArray(new UserData[0]);
    }
    
    public int getCurrentUserHighscoreRank(int timeRange) {
        if(currentUserData == null) return -1;
        calcValidUserData();
        DateTime weeklyValidDate = Utils.calcWeeklyValidDateTime();
        DateTime dailyValidDate = Utils.calcDailyValidDateTime();
        switch(timeRange) {
            case RANGE_ALL_TIME:
                if(currentUserData.highscoreAllTime < 1) return -1;
                return Utils.getRank(highscoreAllTimeComparator, allUserData);
                
            case RANGE_WEEKLY:
                if(!Utils.isValidDate(currentUserData.timeString, weeklyValidDate)) {
                    
                    int rank = Utils.getRank(highscoreWeeklyComparator, validBeforeWeeklyUserData);
                    if(rank > 0 && rank < mode.trophy7DaysHighscore) {
                        mode.trophy7DaysHighscore = rank;
                        int type = Database.TrophyHolder.TYPE_HIGHSCORE;
                        int size = mode.fieldSize;
                        Database.nextAvailableTrophies.add(new Database.TrophyHolder(rank, type, size, RANGE_WEEKLY));
                    }
                    
                    return -1;
                }
                if(currentUserData.highscoreWeekly < 1) return -1;
                return Utils.getRank(highscoreWeeklyComparator, validWeeklyUserData);
                
            case RANGE_DAILY:
                if(!Utils.isValidDate(currentUserData.timeString, dailyValidDate)) {
    
                    int rank = Utils.getRank(highscoreDailyComparator, validBeforeDailyUserData);
                    if(rank > 0 && rank < mode.trophyOneDayHighscore) {
                        mode.trophyOneDayHighscore = rank;
                        int type = Database.TrophyHolder.TYPE_HIGHSCORE;
                        int size = mode.fieldSize;
                        Database.nextAvailableTrophies.add(new Database.TrophyHolder(rank, type, size, RANGE_DAILY));
                    }
    
                    return -1;
                }
                if(currentUserData.highscoreDaily < 1) return -1;
                return Utils.getRank(highscoreDailyComparator, validDailyUserData);
        }
        return -1;
    }
    
    public int getCurrentUserTileRecordRank(int timeRange) {
        if(currentUserData == null) return -1;
        calcValidUserData();
        DateTime weeklyDueDate = Utils.calcWeeklyValidDateTime();
        DateTime dailyDueDate = Utils.calcDailyValidDateTime();
        switch(timeRange) {
            case RANGE_ALL_TIME:
                if(currentUserData.tileRecordAllTime < 1) return -1;
                return Utils.getRank(tileRecordAllTimeComparator, allUserData);
                
            case RANGE_WEEKLY:
                if(!Utils.isValidDate(currentUserData.timeString, weeklyDueDate)) {
    
                    int rank = Utils.getRank(tileRecordWeeklyComparator, validBeforeWeeklyUserData);
                    if(rank > 0 && rank < mode.trophy7DaysTileRecord) {
                        mode.trophy7DaysTileRecord = rank;
                        int type = Database.TrophyHolder.TYPE_TILE_RECORD;
                        int size = mode.fieldSize;
                        Database.nextAvailableTrophies.add(new Database.TrophyHolder(rank, type, size, RANGE_WEEKLY));
                    }
    
                    return -1;
                }
                if(currentUserData.tileRecordWeekly < 1)
                    return -1;
                return Utils.getRank(tileRecordWeeklyComparator, validWeeklyUserData);
                
            case RANGE_DAILY:
                if(!Utils.isValidDate(currentUserData.timeString, dailyDueDate)) {
    
                    int rank = Utils.getRank(tileRecordDailyComparator, validBeforeDailyUserData);
                    if(rank > 0 && rank < mode.trophyOneDayTileRecord) {
                        mode.trophyOneDayTileRecord = rank;
                        int type = Database.TrophyHolder.TYPE_TILE_RECORD;
                        int size = mode.fieldSize;
                        Database.nextAvailableTrophies.add(new Database.TrophyHolder(rank, type, size, RANGE_DAILY));
                    }
        
                    return -1;
                }
                if(currentUserData.tileRecordDaily < 1)
                    return -1;
                return Utils.getRank(tileRecordDailyComparator, validDailyUserData);
        }
        
        return -1;
    }
    
    private UserData findMyUserData(String uid) {
        for(UserData data : allUserData) {
            if(data.uid.equals(uid)) return data;
        }
        
        return null;
    }
    
}
