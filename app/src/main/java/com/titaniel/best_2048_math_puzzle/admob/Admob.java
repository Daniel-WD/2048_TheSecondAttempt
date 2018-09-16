package com.titaniel.best_2048_math_puzzle.admob;

import android.content.Context;
import android.os.Handler;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

public class Admob {

    public static RewardedVideoAd rewardedVideoAd;
    public static RewardedVideoAdListener rewardedVideoAdListener;

    public static void init(Context context, Handler handler) {

        MobileAds.initialize(context, "ca-app-pub-5405975374278619~2814672046");

//        handler.post(() -> {
            rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
            rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
                @Override
                public void onRewardedVideoAdLoaded() {
                    if(rewardedVideoAdListener != null) {
                        rewardedVideoAdListener.onRewardedVideoAdLoaded();
                    }
                }

                @Override
                public void onRewardedVideoAdOpened() {

                    if(rewardedVideoAdListener != null) {
                        rewardedVideoAdListener.onRewardedVideoAdOpened();
                    }
                }

                @Override
                public void onRewardedVideoStarted() {

                    if(rewardedVideoAdListener != null) {
                        rewardedVideoAdListener.onRewardedVideoStarted();
                    }
                }

                @Override
                public void onRewardedVideoAdClosed() {
                    loadRewardedVideoAd();
                    if(rewardedVideoAdListener != null) {
                        rewardedVideoAdListener.onRewardedVideoAdClosed();
                    }
                }

                @Override
                public void onRewarded(RewardItem rewardItem) {

                    if(rewardedVideoAdListener != null) {
                        rewardedVideoAdListener.onRewarded(rewardItem);
                    }
                }

                @Override
                public void onRewardedVideoAdLeftApplication() {

                    if(rewardedVideoAdListener != null) {
                        rewardedVideoAdListener.onRewardedVideoAdLeftApplication();
                    }
                }

                @Override
                public void onRewardedVideoAdFailedToLoad(int i) {

                    if(rewardedVideoAdListener != null) {
                        rewardedVideoAdListener.onRewardedVideoAdFailedToLoad(i);
                    }
                }

                @Override
                public void onRewardedVideoCompleted() {

                    if(rewardedVideoAdListener != null) {
                        rewardedVideoAdListener.onRewardedVideoCompleted();
                    }
                }
            });
            loadRewardedVideoAd();
//        });


    }

    private static void loadRewardedVideoAd() {
//        rewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build());
        rewardedVideoAd.loadAd("ca-app-pub-5405975374278619/7756949480", new AdRequest.Builder().build());

        //REAL ::: ca-app-pub-5405975374278619/7756949480
        //TEST ::: ca-app-pub-3940256099942544/5224354917
    }

}
