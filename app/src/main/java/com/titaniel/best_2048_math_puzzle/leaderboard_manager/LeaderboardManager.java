package com.titaniel.best_2048_math_puzzle.leaderboard_manager;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.titaniel.best_2048_math_puzzle.MainActivity;
import com.titaniel.best_2048_math_puzzle.R;
import com.titaniel.best_2048_math_puzzle.database.Database;
import com.titaniel.best_2048_math_puzzle.game_services.GameServices;
import com.titaniel.best_2048_math_puzzle.loading_view.LoadingView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

public class LeaderboardManager {

    public static class LeaderboardInstance {

        //dividers
        private View vDivBigLeft;
        private View vDivBigRight;
        private View vDivSmallLeft;
        private View vDivSmallCenter;
        private View vDivSmallRight;

        //rank textviews
        private AppCompatTextView tvRankTodayHighscore;
        private AppCompatTextView tvRankTodayTileRecord;
        private AppCompatTextView tvRank7DaysHighscore;
        private AppCompatTextView tvRank7DaysTileRecord;
        private AppCompatTextView tvRankAllTimeHighscore;
        private AppCompatTextView tvRankAllTimeTileRecord;

        //indicators (up or down)
        private ImageView ivIndicatorTodayHighscore;
        private ImageView ivIndicatorTodayTileRecord;
        private ImageView ivIndicator7DaysHighscore;
        private ImageView ivIndicator7DaysTileRecord;
        private ImageView ivIndicatorAllTimeHighscore;
        private ImageView ivIndicatorAllTimeTileRecord;

        //trophyies for 1, 2, 3
        private ImageView ivTrophyTodayHighscore;
        private ImageView ivTrophyTodayTileRecord;
        private ImageView ivTrophy7DaysHighscore;
        private ImageView ivTrophy7DaysTileRecord;
        private ImageView ivTrophyAllTimeHighscore;
        private ImageView ivTrophyAllTimeTileRecord;

        //infinities... no data
        private ImageView ivNoDataTodayHighscore;
        private ImageView ivNoDataTodayTileRecord;
        private ImageView ivNoData7DaysHighscore;
        private ImageView ivNoData7DaysTileRecord;
        private ImageView ivNoDataAllTimeHighscore;
        private ImageView ivNoDataAllTimeTileRecord;

        //loading views
        private LoadingView loadingViewTodayHighscore;
        private LoadingView loadingViewTodayTileRecord;
        private LoadingView loadingView7DaysHighscore;
        private LoadingView loadingView7DaysTileRecord;
        private LoadingView loadingViewAllTimeHighscore;
        private LoadingView loadingViewAllTimeTileRecord;

        //no internet
        private ImageView ivNoInternet;

        //no game services
        private ImageView ivNoGameServices;

    }

    private static final int INTERVAL_TIME = 2000;

    public ArrayList<LoadingView> loadingViews = new ArrayList<>();
    public ArrayList<LeaderboardInstance> instances = new ArrayList<>();

    private MainActivity mActivity;

    private Handler mHandler = new Handler();

    private int mOneDayHighscoreRank, mOneDayTileRekordRank;
    private int mSevendDayHighscoreRank, mSevendDayTileRekordRank;
    private int mAllTimeHighscoreRank, mAllTimeTileRekordRank;

    private Runnable looper = new Runnable() {
        @Override
        public void run() {

            updateLeaderboard();

            mHandler.postDelayed(looper, INTERVAL_TIME);
        }
    };

    public LeaderboardManager(MainActivity activity) {
        mActivity = activity;


    }

    public void start() {
        mHandler.post(looper);
    }

    private void updateLeaderboard() {

        Database.Mode mode = Database.modes[1];

        mode.score = 2039;
        mode.highestTile = 2;

        Log.d("loggg", "updateLeaderboard");

        GameServices.submitLeaderboardPoints(mActivity, mode.fieldSize, mode.score, new GameServices.LeaderboardResultListener() {
            @Override
            public void allTimeResult(GameServices.LeaderboardResult lbResult) {
                Log.d("loggg", "score - rawscoreAllTime::" + lbResult.result.rawScore);

                long rank = lbResult.leaderboardScore.getRank();
                for(LeaderboardInstance instance : instances) {
                    instance.tvRankAllTimeHighscore.setText(String.valueOf(rank));
                }

            }

            @Override
            public void oneDayResult(GameServices.LeaderboardResult lbResult) {
                Log.d("loggg", "score - rawscoreDay::" + lbResult.result.rawScore);

                long rank = lbResult.leaderboardScore.getRank();
                for(LeaderboardInstance instance : instances) {
                    instance.tvRankTodayHighscore.setText(String.valueOf(rank));
                }

            }

            @Override
            public void sevenDaysResult(GameServices.LeaderboardResult lbResult) {
                Log.d("loggg", "score - rawscoreSevenDays::" + lbResult.result.rawScore);

                long rank = lbResult.leaderboardScore.getRank();
                for(LeaderboardInstance instance : instances) {
                    instance.tvRank7DaysHighscore.setText(String.valueOf(rank));
                }

            }
        });

        mHandler.postDelayed(() -> {
            GameServices.submitLeaderboardTileNumber(mActivity, mode.fieldSize, mode.highestTile, new GameServices.LeaderboardResultListener() {
                @Override
                public void allTimeResult(GameServices.LeaderboardResult lbResult) {
                    Log.d("loggg", "highest tile - rawscoreAllTime::" + lbResult.result.rawScore);

                    long rank = lbResult.leaderboardScore.getRank();
                    for(LeaderboardInstance instance : instances) {
                        instance.tvRankAllTimeTileRecord.setText(String.valueOf(rank));
                    }

                }

                @Override
                public void oneDayResult(GameServices.LeaderboardResult lbResult) {
                    Log.d("loggg", "highest tile - rawscoreDay::" + lbResult.result.rawScore);

                    long rank = lbResult.leaderboardScore.getRank();
                    for(LeaderboardInstance instance : instances) {
                        instance.tvRankTodayTileRecord.setText(String.valueOf(rank));
                    }

                }

                @Override
                public void sevenDaysResult(GameServices.LeaderboardResult lbResult) {
                    Log.d("loggg", "highest tile - rawscoreSevenDays::" + lbResult.result.rawScore);

                    long rank = lbResult.leaderboardScore.getRank();
                    for(LeaderboardInstance instance : instances) {
                        instance.tvRank7DaysTileRecord.setText(String.valueOf(rank));
                    }

                }
            });
        }, 1000);


    }

    public static LeaderboardInstance generateLeaderbaordInstance(@NonNull View view) {
        LeaderboardInstance instance = new LeaderboardInstance();

        instance.vDivBigLeft = view.findViewById(R.id.vDivLbLargeLeft);
        instance.vDivBigRight = view.findViewById(R.id.vDivLbLargeRight);
        instance.vDivSmallRight = view.findViewById(R.id.vDivLbSmallRight);
        instance.vDivSmallCenter = view.findViewById(R.id.vDivLbSmallCenter);
        instance.vDivSmallLeft = view.findViewById(R.id.vDivLbSmallLeft);

        instance.tvRank7DaysHighscore = view.findViewById(R.id.tvRank7DaysHs);
        instance.tvRank7DaysTileRecord = view.findViewById(R.id.tvRank7DaysTr);
        instance.tvRankTodayHighscore = view.findViewById(R.id.tvRankTodayHs);
        instance.tvRankTodayTileRecord = view.findViewById(R.id.tvRankTodayTr);
        instance.tvRankAllTimeHighscore = view.findViewById(R.id.tvRankAllTimeHs);
        instance.tvRankAllTimeTileRecord = view.findViewById(R.id.tvRankAllTimeTr);

        instance.ivIndicator7DaysHighscore = view.findViewById(R.id.ivIndicator7DaysHighscore);
        instance.ivIndicator7DaysTileRecord = view.findViewById(R.id.ivIndicator7DaysTileRecord);
        instance.ivIndicatorTodayHighscore = view.findViewById(R.id.ivIndicatorTodayHighscore);
        instance.ivIndicatorTodayTileRecord = view.findViewById(R.id.ivIndicatorTodayTileRecord);
        instance.ivIndicatorAllTimeHighscore = view.findViewById(R.id.ivIndicatorAllTimeHighscore);
        instance.ivIndicatorAllTimeTileRecord = view.findViewById(R.id.ivIndicatorAllTimeTileRecord);

        instance.ivTrophy7DaysHighscore = view.findViewById(R.id.ivTrophy7DaysHs);
        instance.ivTrophy7DaysTileRecord = view.findViewById(R.id.ivTrophy7DaysTr);
        instance.ivTrophyTodayHighscore = view.findViewById(R.id.ivTrophyTodayHs);
        instance.ivTrophyTodayTileRecord = view.findViewById(R.id.ivTrophyTodayTr);
        instance.ivTrophyAllTimeHighscore = view.findViewById(R.id.ivTrophyAllTimeHs);
        instance.ivTrophyAllTimeTileRecord = view.findViewById(R.id.ivTrophyAllTimeTr);

        instance.ivNoData7DaysHighscore = view.findViewById(R.id.ivInfinity7DaysHs);
        instance.ivNoData7DaysTileRecord = view.findViewById(R.id.ivInfinity7DaysTr);
        instance.ivNoDataTodayHighscore = view.findViewById(R.id.ivInfinityTodayHs);
        instance.ivNoDataTodayTileRecord = view.findViewById(R.id.ivInfinityTodayTr);
        instance.ivNoDataAllTimeHighscore = view.findViewById(R.id.ivInfinityAllTimeHs);
        instance.ivNoDataAllTimeTileRecord = view.findViewById(R.id.ivInfinityAllTimeTr);

        instance.loadingView7DaysHighscore = view.findViewById(R.id.loading7DaysHs);
        instance.loadingView7DaysTileRecord = view.findViewById(R.id.loading7DaysTr);
        instance.loadingViewTodayHighscore = view.findViewById(R.id.loadingTodayHs);
        instance.loadingViewTodayTileRecord = view.findViewById(R.id.loadingTodayTr);
        instance.loadingViewAllTimeHighscore = view.findViewById(R.id.loadingAllTimeHs);
        instance.loadingViewAllTimeTileRecord = view.findViewById(R.id.loadingAllTimeTr);

        instance.ivNoInternet = view.findViewById(R.id.ivNoInternet);
        instance.ivNoGameServices = view.findViewById(R.id.ivNoGameServices);

        return instance;
    }

}
