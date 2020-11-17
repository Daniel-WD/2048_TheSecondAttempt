package com.titaniel.best_2048_math_puzzle.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.titaniel.best_2048_math_puzzle.R;
import com.titaniel.best_2048_math_puzzle.database.Database;
import com.titaniel.best_2048_math_puzzle.leaderboard_manager.Leaderboard;
import com.titaniel.best_2048_math_puzzle.leaderboard_manager.UserData;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Arrays;
import java.util.Comparator;

import androidx.core.content.ContextCompat;

public class Utils {
    
    public static int digitCount(int number) {
        return String.valueOf(number).length();
    }
    
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
    
    public static int idToNum(int id) {
        switch(id) {
            case 0:
                return (int) Math.pow(2, 1);
            case 1:
                return (int) Math.pow(2, 2);
            case 2:
                return (int) Math.pow(2, 3);
            case 3:
                return (int) Math.pow(2, 4);
            case 4:
                return (int) Math.pow(2, 5);
            case 5:
                return (int) Math.pow(2, 6);
            case 6:
                return (int) Math.pow(2, 7);
            case 7:
                return (int) Math.pow(2, 8);
            case 8:
                return (int) Math.pow(2, 9);
            case 9:
                return (int) Math.pow(2, 10);
            case 10:
                return (int) Math.pow(2, 11);
            case 11:
                return (int) Math.pow(2, 12);
            case 12:
                return (int) Math.pow(2, 13);
            case 13:
                return (int) Math.pow(2, 14);
            case 14:
                return (int) Math.pow(2, 15);
            case 15:
                return (int) Math.pow(2, 16);
            case 16:
                return (int) Math.pow(2, 17);
            case 17:
                return (int) Math.pow(2, 18);
            case 18:
                return (int) Math.pow(2, 19);
            case 19:
                return (int) Math.pow(2, 20);
            case 20:
                return (int) Math.pow(2, 21);
            case 21:
                return (int) Math.pow(2, 22);
            case 22:
                return (int) Math.pow(2, 23);
            case 23:
                return (int) Math.pow(2, 24);
        }
        return 0;
    }
    
    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
    
    public static float dpToPx(Resources res, float dpValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, res.getDisplayMetrics());
    }
    
    public static float spToPx(Resources res, float spValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, res.getDisplayMetrics());
    }
    
    public static Drawable findTrophyDrawableForLeaderboard(int rank, Context context) {
        Drawable drawable = null;
        switch(rank) {
            case Database.Mode.TROPHY_FIRST:
                drawable = context.getDrawable(R.drawable.trophy_first);
                drawable.setColorFilter(ContextCompat.getColor(context, R.color.first), PorterDuff.Mode.SRC_IN);
                break;
            case Database.Mode.TROPHY_SECOND:
                drawable = context.getDrawable(R.drawable.trophy_second);
                drawable.setColorFilter(ContextCompat.getColor(context, R.color.second), PorterDuff.Mode.SRC_IN);
                break;
            case Database.Mode.TROPHY_THIRD:
                drawable = context.getDrawable(R.drawable.trophy_third);
                drawable.setColorFilter(ContextCompat.getColor(context, R.color.third), PorterDuff.Mode.SRC_IN);
                break;
        }
        return drawable;
    }
    
    public static int findTrophyDrawableIdForTrophyChamber(int rank) {
        switch(rank) {
            case Database.Mode.TROPHY_FIRST:
                return R.drawable.trophy_chamber_first;
            case Database.Mode.TROPHY_SECOND:
                return R.drawable.trophy_chamber_second;
            case Database.Mode.TROPHY_THIRD:
                return R.drawable.trophy_chamber_third;
        }
        return R.drawable.trophy_chamber_placeholder;
    }
    
    public static int findTrophyDrawableIdForNewTrophy(int rank) {
        switch(rank) {
            case Database.Mode.TROPHY_FIRST:
                return R.drawable.got_trophy_first;
            case Database.Mode.TROPHY_SECOND:
                return R.drawable.got_trophy_second;
            case Database.Mode.TROPHY_THIRD:
                return R.drawable.got_trophy_third;
        }
        return R.drawable.trophy_chamber_placeholder;
    }
    
    public static void setTranslationYBulk(float translY, View... views) {
        if(views == null) return;
        for(View v : views) {
            v.setTranslationY(translY);
        }
    }
    
    public static String currentZonedTimeString() {
        return ISODateTimeFormat.dateTime().print(DateTime.now().withZone(Database.STD_TIME_ZONE));
    }
    
    public static DateTime dateTimeFromZonedString(String string) {
        if(string == null || string.equals("")) return null;
        return ISODateTimeFormat.dateTime().parseDateTime(string).withZone(Database.STD_TIME_ZONE);
    }
    
    public static DateTime calcWeeklyValidDateTime() {
        return calcValidDateTime(7);
    }
    
    public static DateTime calcDailyValidDateTime() {
        return calcValidDateTime(1);
    }
    
    private static DateTime calcValidDateTime(int days) {
        Duration currentDifference = new Duration(Database.INITIAL_TIME, DateTime.now());
        int dayDifference = (int) currentDifference.getStandardDays();
        return Database.INITIAL_TIME.plusDays(dayDifference - (dayDifference%days));
    }
    
    public static boolean isValidDate(String zonedString, DateTime dueDate) {
        DateTime dateTime = dateTimeFromZonedString(zonedString);
        
        return dateTime != null && dueDate.isBefore(dateTime);
    }
    
    public static void fillGameStartRankValues() {
        
        Database.Mode mode = Database.currentMode;
        Leaderboard leaderboard = mode.leaderboard;
        
        mode.gameStartRankDailyHighscore = leaderboard.getCurrentUserHighscoreRank(Leaderboard.RANGE_DAILY);
        mode.gameStartRankWeeklyHighscore = leaderboard.getCurrentUserHighscoreRank(Leaderboard.RANGE_WEEKLY);
        mode.gameStartRankAllTimeHighscore = leaderboard.getCurrentUserHighscoreRank(Leaderboard.RANGE_ALL_TIME);
        mode.gameStartRankDailyTileRecord = leaderboard.getCurrentUserTileRecordRank(Leaderboard.RANGE_DAILY);
        mode.gameStartRankWeeklyTileRecord = leaderboard.getCurrentUserTileRecordRank(Leaderboard.RANGE_WEEKLY);
        mode.gameStartRankAllTimeTileRecord = leaderboard.getCurrentUserTileRecordRank(Leaderboard.RANGE_ALL_TIME);
        
    }
    
    public static int getRank(Comparator<UserData> comparator, UserData[] data) {
        Arrays.sort(data, comparator);
        
        for(int i = 0; i < data.length; i++) {
            if(data[i].uid.equals(Database.uid)) return i + 1;
        }
        
        return -1;
    }
    
    
}
