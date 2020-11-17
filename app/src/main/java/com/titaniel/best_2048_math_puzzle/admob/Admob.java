package com.titaniel.best_2048_math_puzzle.admob;

import android.content.Context;
import android.os.Handler;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

public class Admob {

    public interface MyAdListener {
        default void onRewardedVideoAdLoaded() {}
        default void onRewardedVideoAdOpened() {}
        default void onRewardedVideoStarted() {}
        default void onRewardedVideoAdClosed() {}
        default void onRewarded(RewardItem rewardItem) {}
        default void onRewardedVideoAdLeftApplication() {}
        default void onRewardedVideoAdFailedToLoad(int i) {}
        default void onRewardedVideoCompleted() {}
    }

    public static RewardedVideoAd rewardedVideoAd;
    public static MyAdListener adListener;

    public static void init(Context context, Handler handler) {

        MobileAds.initialize(context, "ca-app-pub-5405975374278619~2814672046");

//        handler.post(() -> {
            rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
            rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
                @Override
                public void onRewardedVideoAdLoaded() {
                    if(adListener != null) {
                        adListener.onRewardedVideoAdLoaded();
                    }
                }

                @Override
                public void onRewardedVideoAdOpened() {

                    if(adListener != null) {
                        adListener.onRewardedVideoAdOpened();
                    }
                }

                @Override
                public void onRewardedVideoStarted() {

                    if(adListener != null) {
                        adListener.onRewardedVideoStarted();
                    }
                }

                @Override
                public void onRewardedVideoAdClosed() {
                    loadRewardedVideoAd();
                    if(adListener != null) {
                        adListener.onRewardedVideoAdClosed();
                    }
                }

                @Override
                public void onRewarded(RewardItem rewardItem) {

                    if(adListener != null) {
                        adListener.onRewarded(rewardItem);
                    }
                }

                @Override
                public void onRewardedVideoAdLeftApplication() {

                    if(adListener != null) {
                        adListener.onRewardedVideoAdLeftApplication();
                    }
                }

                @Override
                public void onRewardedVideoAdFailedToLoad(int i) {
                    if(adListener != null) {
                        adListener.onRewardedVideoAdFailedToLoad(i);
                    }
                }

                @Override
                public void onRewardedVideoCompleted() {

                    if(adListener != null) {
                        adListener.onRewardedVideoCompleted();
                    }
                }
            });
            loadRewardedVideoAd();
//        });


    }

    private static void loadRewardedVideoAd() {
        rewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build());
//        rewardedVideoAd.loadAd("ca-app-pub-5405975374278619/7756949480", new AdRequest.Builder().build());

        //REAL ::: ca-app-pub-5405975374278619/7756949480
        //TEST ::: ca-app-pub-3940256099942544/5224354917
    }

}
