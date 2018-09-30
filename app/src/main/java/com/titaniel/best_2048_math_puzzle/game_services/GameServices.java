package com.titaniel.best_2048_math_puzzle.game_services;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.Games;
import com.titaniel.best_2048_math_puzzle.R;

import androidx.appcompat.app.AppCompatActivity;

public class GameServices {

    public static class Achievement {

        Achievement(String id, int triggerValue) {
            this.id = id;
            this.triggerValue = triggerValue;
        }

        final String id;
        final int triggerValue;

    }

    public static class Leaderboard {

        public Leaderboard(String id, int fieldSize) {
            this.id = id;
            this.fieldSize = fieldSize;
        }

        final String id;
        final int fieldSize;
    }

    private static Achievement[] tileAchievements;
    private static Achievement[] pointAchievements;
    private static Leaderboard[] pointLeaderboard;
    private static Leaderboard[] tileLeaderboard;


    public static void init(Context c) {

        tileAchievements = new Achievement[] {
                new Achievement(c.getString(R.string.achievement_sperm), 2),
                new Achievement(c.getString(R.string.achievement_baby), 4),
                new Achievement(c.getString(R.string.achievement_kid), 8),
                new Achievement(c.getString(R.string.achievement_adolescent_child), 16),
                new Achievement(c.getString(R.string.achievement_teenager), 32),
                new Achievement(c.getString(R.string.achievement_gangster), 64),
                new Achievement(c.getString(R.string.achievement_loo_cleaner), 128),
                new Achievement(c.getString(R.string.achievement_farmer), 256),
                new Achievement(c.getString(R.string.achievement_body_builder), 512),
                new Achievement(c.getString(R.string.achievement_businessman), 1024),
                new Achievement(c.getString(R.string.achievement_millionaire), 2048),
                new Achievement(c.getString(R.string.achievement_richy_rich), 4096),
                new Achievement(c.getString(R.string.achievement_mark_zuckerberg), 8192),
                new Achievement(c.getString(R.string.achievement_bill_gates), 16384),
                new Achievement(c.getString(R.string.achievement_flying_to_the_moon), 32768)
        };
        pointAchievements = new Achievement[] {
                new Achievement(c.getString(R.string.achievement_500_points), 500),
                new Achievement(c.getString(R.string.achievement_1000_points), 1000),
                new Achievement(c.getString(R.string.achievement_5000_points), 5000),
                new Achievement(c.getString(R.string.achievement_10000_points), 10000),
                new Achievement(c.getString(R.string.achievement_15000_points), 15000),
                new Achievement(c.getString(R.string.achievement_20000_points), 20000),
                new Achievement(c.getString(R.string.achievement_25000_points), 25000),
                new Achievement(c.getString(R.string.achievement_30000_points), 30000),
                new Achievement(c.getString(R.string.achievement_50000_points), 50000),
                new Achievement(c.getString(R.string.achievement_100000_points), 100000),
                new Achievement(c.getString(R.string.achievement_200000_points), 200000),
                new Achievement(c.getString(R.string.achievement_500000_points), 500000),
                new Achievement(c.getString(R.string.achievement_1000000_points), 1000000)
        };
        tileLeaderboard = new Leaderboard[] {
                new Leaderboard(c.getString(R.string.leaderboard_3x3__tile_records), 3),
                new Leaderboard(c.getString(R.string.leaderboard_4x4__tile_records), 4),
                new Leaderboard(c.getString(R.string.leaderboard_5x5__tile_records), 5),
                new Leaderboard(c.getString(R.string.leaderboard_6x6__tile_records), 6),
                new Leaderboard(c.getString(R.string.leaderboard_7x7__tile_records), 7),
                new Leaderboard(c.getString(R.string.leaderboard_8x8__tile_records), 8),
                new Leaderboard(c.getString(R.string.leaderboard_9x9__tile_records), 9)
        };
        pointLeaderboard = new Leaderboard[] {
                new Leaderboard(c.getString(R.string.leaderboard_3x3__highscores), 3),
                new Leaderboard(c.getString(R.string.leaderboard_4x4__highscores), 4),
                new Leaderboard(c.getString(R.string.leaderboard_5x5__highscores), 5),
                new Leaderboard(c.getString(R.string.leaderboard_6x6__highscores), 6),
                new Leaderboard(c.getString(R.string.leaderboard_7x7__highscores), 7),
                new Leaderboard(c.getString(R.string.leaderboard_8x8__highscores), 8),
                new Leaderboard(c.getString(R.string.leaderboard_9x9__highscores), 9)
        };

    }

    private static void showAchievement(AppCompatActivity activity, Achievement achievement) {

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if(signInAccount == null) return;
        AchievementsClient client = Games.getAchievementsClient(activity, signInAccount);
        client.unlock(achievement.id);

    }

    public static void checkForPointAchievement(AppCompatActivity activity, int points) {
        Achievement triggerAchievement = null;
        for(Achievement achievement : pointAchievements) {
            if(points >= achievement.triggerValue) triggerAchievement = achievement;
            else break;
        }
        if(triggerAchievement != null) {
            Log.d("PointAchievement", "" + triggerAchievement.triggerValue);
            showAchievement(activity, triggerAchievement);
        }
    }

    public static void checkForTileAchievement(AppCompatActivity activity, int tileValue) {
        for(Achievement achievement : tileAchievements) {
            if(tileValue == achievement.triggerValue) {
                Log.d("TileAchievement", "" + achievement.triggerValue);
                showAchievement(activity, achievement);
                break;
            }
        }
    }

    private static void submitLeaderboardValue(AppCompatActivity activity, String id, int value) {

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(activity);
        if(account == null) return;
        Games.getLeaderboardsClient(activity, account).submitScore(id, value);

    }

    public static void submitLeaderboardTileNumber(AppCompatActivity activity, int fieldSize, int tileNumber) {
        for(Leaderboard board : tileLeaderboard) {
            if(board.fieldSize == fieldSize) {
                Log.d("TileLeaderboard", "size::" + fieldSize + " value:: " + tileNumber);
                submitLeaderboardValue(activity, board.id, tileNumber);
                break;
            }
        }
    }

    public static void submitLeaderboardPoints(AppCompatActivity activity, int fieldSize, int points) {
        for(Leaderboard board : pointLeaderboard) {
            if(board.fieldSize == fieldSize) {
                Log.d("PointLeaderboard", "size::" + fieldSize + " value:: " + points);
                submitLeaderboardValue(activity, board.id, points);
                break;
            }
        }
    }

}
