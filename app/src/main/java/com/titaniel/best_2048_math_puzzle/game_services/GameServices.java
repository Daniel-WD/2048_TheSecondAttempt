package com.titaniel.best_2048_math_puzzle.game_services;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.Games;

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

    public static Achievement[] tileAchievements = {
            new Achievement("CgkI34P-6pkGEAIQBg", 2),
            new Achievement("CgkI34P-6pkGEAIQBw", 4),
            new Achievement("CgkI34P-6pkGEAIQCA", 8),
            new Achievement("CgkI34P-6pkGEAIQCQ", 16),
            new Achievement("CgkI34P-6pkGEAIQCg", 32),
            new Achievement("CgkI34P-6pkGEAIQCw", 64),
            new Achievement("CgkI34P-6pkGEAIQDA", 128),
            new Achievement("CgkI34P-6pkGEAIQDQ", 256),
            new Achievement("CgkI34P-6pkGEAIQDg", 512),
            new Achievement("CgkI34P-6pkGEAIQDw", 1024),
            new Achievement("CgkI34P-6pkGEAIQEA", 2048),
            new Achievement("CgkI34P-6pkGEAIQEQ", 4096),
            new Achievement("CgkI34P-6pkGEAIQEg", 8192),
            new Achievement("CgkI34P-6pkGEAIQEw", 16384),
            new Achievement("CgkI34P-6pkGEAIQFA", 32768)
    };

    public static Achievement[] pointAchievements = {
            new Achievement("CgkI34P-6pkGEAIQFQ", 500),
            new Achievement("CgkI34P-6pkGEAIQFg", 1000),
            new Achievement("CgkI34P-6pkGEAIQFw", 5000),
            new Achievement("CgkI34P-6pkGEAIQGA", 10000),
            new Achievement("CgkI34P-6pkGEAIQGQ", 15000),
            new Achievement("CgkI34P-6pkGEAIQGg", 20000),
            new Achievement("CgkI34P-6pkGEAIQGw", 25000),
            new Achievement("CgkI34P-6pkGEAIQHA", 30000),
            new Achievement("CgkI34P-6pkGEAIQHQ", 50000),
            new Achievement("CgkI34P-6pkGEAIQHg", 100000),
            new Achievement("CgkI34P-6pkGEAIQHw", 200000),
            new Achievement("CgkI34P-6pkGEAIQIA", 500000),
            new Achievement("CgkI34P-6pkGEAIQIQ", 1000000)
    };

    public static void init(Context context) {
    }

    public static void showAchievement(AppCompatActivity activity, Achievement achievement) {

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

}
