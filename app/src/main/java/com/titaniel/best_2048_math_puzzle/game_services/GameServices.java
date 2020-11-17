package com.titaniel.best_2048_math_puzzle.game_services;

import android.os.Handler;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.ScoreSubmissionData;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.titaniel.best_2048_math_puzzle.MainActivity;
import com.titaniel.best_2048_math_puzzle.R;

import androidx.annotation.NonNull;
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

    public static class LeaderboardResult {

        public ScoreSubmissionData.Result result;
        public LeaderboardScore leaderboardScore;

        public LeaderboardResult(ScoreSubmissionData.Result result, LeaderboardScore leaderboardScore) {
            this.result = result;
            this.leaderboardScore = leaderboardScore;
        }
    }

    public interface LeaderboardResultListener {

        void allTimeResult(LeaderboardResult lbResult);

        void oneDayResult(LeaderboardResult lbResult);

        void sevenDaysResult(LeaderboardResult lbResult);

    }

    private static Achievement[] tileAchievements;
    private static Achievement[] pointAchievements;
    private static Leaderboard[] scoreLeaderboards;
    private static Leaderboard[] tileLeaderboards;

    private static Handler sHandler = new Handler();

    public static void init(MainActivity activity) {

        tileAchievements = new Achievement[] {
                new Achievement(activity.getString(R.string.achievement_sperm), 2),
                new Achievement(activity.getString(R.string.achievement_baby), 4),
                new Achievement(activity.getString(R.string.achievement_kid), 8),
                new Achievement(activity.getString(R.string.achievement_student), 16),
                new Achievement(activity.getString(R.string.achievement_teenager), 32),
                new Achievement(activity.getString(R.string.achievement_loo_cleaner), 64),
                new Achievement(activity.getString(R.string.achievement_gangster), 128),
                new Achievement(activity.getString(R.string.achievement_farmer), 256),
                new Achievement(activity.getString(R.string.achievement_body_builder), 512),
                new Achievement(activity.getString(R.string.achievement_businessman), 1024),
                new Achievement(activity.getString(R.string.achievement_millionaire), 2048),
                new Achievement(activity.getString(R.string.achievement_richy_rich), 4096),
                new Achievement(activity.getString(R.string.achievement_mark_zuckerberg), 8192),
                new Achievement(activity.getString(R.string.achievement_bill_gates), 16384),
                new Achievement(activity.getString(R.string.achievement_flying_to_the_moon), 32768)
        };
        pointAchievements = new Achievement[] {
                new Achievement(activity.getString(R.string.achievement_500_points), 500),
                new Achievement(activity.getString(R.string.achievement_1000_points), 1000),
                new Achievement(activity.getString(R.string.achievement_5000_points), 5000),
                new Achievement(activity.getString(R.string.achievement_10000_points), 10000),
                new Achievement(activity.getString(R.string.achievement_15000_points), 15000),
                new Achievement(activity.getString(R.string.achievement_20000_points), 20000),
                new Achievement(activity.getString(R.string.achievement_25000_points), 25000),
                new Achievement(activity.getString(R.string.achievement_30000_points), 30000),
                new Achievement(activity.getString(R.string.achievement_50000_points), 50000),
                new Achievement(activity.getString(R.string.achievement_100000_points), 100000),
                new Achievement(activity.getString(R.string.achievement_200000_points), 200000),
                new Achievement(activity.getString(R.string.achievement_500000_points), 500000),
                new Achievement(activity.getString(R.string.achievement_1000000_points), 1000000)
        };
        tileLeaderboards = new Leaderboard[] {
                new Leaderboard(activity.getString(R.string.leaderboard_3x3_tile_records), 3),
                new Leaderboard(activity.getString(R.string.leaderboard_4x4_tile_records), 4),
                new Leaderboard(activity.getString(R.string.leaderboard_5x5_tile_records), 5),
                new Leaderboard(activity.getString(R.string.leaderboard_6x6_tile_records), 6),
                new Leaderboard(activity.getString(R.string.leaderboard_7x7_tile_records), 7),
                new Leaderboard(activity.getString(R.string.leaderboard_8x8_tile_records), 8),
                new Leaderboard(activity.getString(R.string.leaderboard_9x9_tile_records), 9)
        };
        scoreLeaderboards = new Leaderboard[] {
                new Leaderboard(activity.getString(R.string.leaderboard_3x3_highscores), 3),
                new Leaderboard(activity.getString(R.string.leaderboard_4x4_highscores), 4),
                new Leaderboard(activity.getString(R.string.leaderboard_5x5_highscores), 5),
                new Leaderboard(activity.getString(R.string.leaderboard_6x6_highscores), 6),
                new Leaderboard(activity.getString(R.string.leaderboard_7x7_highscores), 7),
                new Leaderboard(activity.getString(R.string.leaderboard_8x8_highscores), 8),
                new Leaderboard(activity.getString(R.string.leaderboard_9x9_highscores), 9)
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
            if(points >= achievement.triggerValue) {
                triggerAchievement = achievement;
            }
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

    private static void submitLeaderboardValue(AppCompatActivity activity, String id, int value, LeaderboardResultListener listener) {

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(activity);
        if(account == null) return;
        LeaderboardsClient lbClient = Games.getLeaderboardsClient(activity, account);


        Log.d("loggg", "submitScoreImmediate");

        lbClient.submitScoreImmediate(id, value).addOnSuccessListener(scoreSubmissionData -> {
            if(listener == null) return;

            Log.d("loggg", "submitScoreImmediate --> success");
            Log.d("loggg", "wait");

            ScoreSubmissionData.Result resultAllTime = scoreSubmissionData.getScoreResult(LeaderboardVariant.TIME_SPAN_ALL_TIME);
            ScoreSubmissionData.Result resultSevenDays = scoreSubmissionData.getScoreResult(LeaderboardVariant.TIME_SPAN_WEEKLY);
            ScoreSubmissionData.Result resultOneDay = scoreSubmissionData.getScoreResult(LeaderboardVariant.TIME_SPAN_DAILY);

            long delay = 0;

            sHandler.postDelayed(() -> {
                lbClient.loadCurrentPlayerLeaderboardScore(id, LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC)
                        .addOnFailureListener(e -> {
                            Log.e("loggg", "loadCurrentPlayerLeaderboardScore - all time --> failed");
                            e.printStackTrace();
                            Log.d("loggg", "-------------------------------------------------------------");
                            Log.d("loggg", "wait");
                        })
                        .addOnSuccessListener(annotatedData -> {
                            Log.d("loggg", "loadCurrentPlayerLeaderboardScore - all time --> success");
                            listener.allTimeResult(new LeaderboardResult(resultAllTime, annotatedData.get()));
                            Log.d("loggg", "-------------------------------------------------------------");
                            Log.d("loggg", "wait");
                        });

            }, delay);

            delay += 300;

            sHandler.postDelayed(() -> {
                lbClient.loadCurrentPlayerLeaderboardScore(id, LeaderboardVariant.TIME_SPAN_WEEKLY, LeaderboardVariant.COLLECTION_PUBLIC)
                        .addOnFailureListener(e -> {
                            Log.e("loggg", "loadCurrentPlayerLeaderboardScore - weekly --> failed");
                            e.printStackTrace();
                            Log.d("loggg", "-------------------------------------------------------------");
                            Log.d("loggg", "wait");
                        })
                        .addOnSuccessListener(annotatedData -> {
                            Log.d("loggg", "loadCurrentPlayerLeaderboardScore - weekly --> success");
                            listener.sevenDaysResult(new LeaderboardResult(resultSevenDays, annotatedData.get()));
                            Log.d("loggg", "-------------------------------------------------------------");
                            Log.d("loggg", "wait");
                        });

            }, delay);

            delay += 300;

            sHandler.postDelayed(() -> {
                lbClient.loadCurrentPlayerLeaderboardScore(id, LeaderboardVariant.TIME_SPAN_DAILY, LeaderboardVariant.COLLECTION_PUBLIC)
                        .addOnFailureListener(e -> {
                            Log.e("loggg", "loadCurrentPlayerLeaderboardScore - daily --> failed");
                            e.printStackTrace();
                            Log.d("loggg", "-------------------------------------------------------------");
                        })
                        .addOnSuccessListener(annotatedData -> {
                            Log.d("loggg", "loadCurrentPlayerLeaderboardScore - daily --> success");
                            listener.oneDayResult(new LeaderboardResult(resultOneDay, annotatedData.get()));
                            Log.d("loggg", "-------------------------------------------------------------");
                        });
            }, delay);

        });

    }

    public static void submitLeaderboardTileNumber(AppCompatActivity activity, int fieldSize, int tileNumber, LeaderboardResultListener listener) {
        for(Leaderboard board : tileLeaderboards) {
            if(board.fieldSize == fieldSize) {
                Log.d("TileLeaderboard", "size::" + fieldSize + " value:: " + tileNumber);
                submitLeaderboardValue(activity, board.id, tileNumber, listener);
                break;
            }
        }
    }

    public static void submitLeaderboardPoints(AppCompatActivity activity, int fieldSize, int points, LeaderboardResultListener listener) {
        for(Leaderboard board : scoreLeaderboards) {
            if(board.fieldSize == fieldSize) {
                Log.d("PointLeaderboard", "size::" + fieldSize + " value:: " + points);
                submitLeaderboardValue(activity, board.id, points, listener);
                break;
            }
        }
    }

    public static String findTileLeaderboardIdBySize(int size) {
        for(Leaderboard lb : tileLeaderboards) {
            if(lb.fieldSize == size) return lb.id;
        }
        return null;
    }

    public static String findScoreLeaderboardIdBySize(int size) {
        for(Leaderboard lb : scoreLeaderboards) {
            if(lb.fieldSize == size) return lb.id;
        }
        return null;
    }

}
