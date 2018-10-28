package com.titaniel.best_2048_math_puzzle.leaderboard_manager;

import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.titaniel.best_2048_math_puzzle.MainActivity;
import com.titaniel.best_2048_math_puzzle.R;
import com.titaniel.best_2048_math_puzzle.database.Database;
import com.titaniel.best_2048_math_puzzle.loading_view.LoadingView;
import com.titaniel.best_2048_math_puzzle.loading_view.controller.InfiniteLoadingController;
import com.titaniel.best_2048_math_puzzle.utils.Utils;
import com.titaniel.best_2048_math_puzzle.utils.ViewUtils;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

public class LeaderboardManager {
    
    public static final String TAG = LeaderboardManager.class.getSimpleName();
    
    public static class LeaderboardLayoutInstance {
        
        //headlines
        private TextView tvHeadlineToday;
        private TextView tvHeadlineAllTime;
        private TextView tvHeadlineWeekly;
        
        //dividers
        private View vDivBigLeft;
        private View vDivBigRight;
        private View vDivSmallLeft;
        private View vDivSmallCenter;
        private View vDivSmallRight;
        
        //rank textviews
        private TextView tvRankDailyHighscore;
        private TextView tvRankDailyTileRecord;
        private TextView tvRankWeeklyHighscore;
        private TextView tvRankWeeklyTileRecord;
        private TextView tvRankAllTimeHighscore;
        private TextView tvRankAllTimeTileRecord;
        
        //indicators (up or down)
        private ImageView ivIndicatorDailyHighscore;
        private ImageView ivIndicatorDailyTileRecord;
        private ImageView ivIndicatorWeeklyHighscore;
        private ImageView ivIndicatorWeeklyTileRecord;
        private ImageView ivIndicatorAllTimeHighscore;
        private ImageView ivIndicatorAllTimeTileRecord;
        
        //trophyies for 1, 2, 3
        private ImageView ivTrophyDailyHighscore;
        private ImageView ivTrophyDailyTileRecord;
        private ImageView ivTrophyWeeklyHighscore;
        private ImageView ivTrophyWeeklyTileRecord;
        private ImageView ivTrophyAllTimeHighscore;
        private ImageView ivTrophyAllTimeTileRecord;
        
        //infinities... no data
        private ImageView ivNoDataDailyHighscore;
        private ImageView ivNoDataDailyTileRecord;
        private ImageView ivNoDataWeeklyHighscore;
        private ImageView ivNoDataWeeklyTileRecord;
        private ImageView ivNoDataAllTimeHighscore;
        private ImageView ivNoDataAllTimeTileRecord;
        
        //loading views
        private LoadingView loadingViewDailyHighscore;
        private LoadingView loadingViewDailyTileRecord;
        private LoadingView loadingViewWeeklyHighscore;
        private LoadingView loadingViewWeeklyTileRecord;
        private LoadingView loadingViewAllTimeHighscore;
        private LoadingView loadingViewAllTimeTileRecord;
        
        private InfiniteLoadingController loadingViewController;
        
        //no internet
        private ImageView ivNoInternet;
        
        //no game services
        private ImageView ivNoGameServices;
        
    }
    
    private static final int SEND_INTERVAL_TIME = 20000;
    private static final int RANK_BALANCE_TIME = 4000;
    
    private SparseArray<Float> mTextSizeMap = new SparseArray<>();
    private SparseArray<Float> mIndicatorOffsetMap = new SparseArray<>();
    
    public LeaderboardLayoutInstance leaderboardLayoutInstance = null;
    
    private MainActivity mActivity;
    
    private Handler mUIHandler = new Handler();
    private Handler mWorkerHandler;
    
    private Runnable looper = new Runnable() {
        @Override
        public void run() {
            
            sendData();
            
            mWorkerHandler.postDelayed(looper, SEND_INTERVAL_TIME);
    
        }
    };
    
    private FirebaseFirestore mFirestoreDatabase = FirebaseFirestore.getInstance();
    
    private boolean mLastValuesAvailable = false;
    
    private boolean mIsOnline = false, mIsOnlineChanged = true;
    
    private boolean mInitialised = false;
    
    private int mColorRankUp, mColorRankDown;
    
    private int mInitCompleteCount = 0;
    
    public LeaderboardManager(MainActivity activity) {
        mActivity = activity;
        
        //worker thread
        HandlerThread thread = new HandlerThread("worker");
        thread.start();
        mWorkerHandler = new Handler(thread.getLooper());
        
        //firestore settings
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        mFirestoreDatabase.setFirestoreSettings(settings);
        
        mColorRankUp = ContextCompat.getColor(activity.getApplicationContext(), R.color.rankUp);
        mColorRankDown = ContextCompat.getColor(activity.getApplicationContext(), R.color.rankDown);
        
        //textsize map
        mTextSizeMap.put(1, 22.7f);
        mTextSizeMap.put(2, 22.7f);
        mTextSizeMap.put(3, 18f);
        
        //indicator offset map
        mIndicatorOffsetMap.put(1, 18f);
        mIndicatorOffsetMap.put(2, 32f);
        mIndicatorOffsetMap.put(3, 36f);
        
    }
    
    public void start() {
        
        //internet
        onSomethingImportantChanged();
        
    }
    
    private void initLeaderboards() {
        
        mInitialised = true;
        
        Database.Mode[] modes = Database.modes;
        for(Database.Mode mode : modes) {
            Leaderboard lb = mode.leaderboard;
            
            mFirestoreDatabase.collection(lb.leaderboardName())
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        lb.updateUserData(snapshot.getDocuments());
                        lb.currentUserData.hasNewData = true;
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "failure", e);
                    })
                    .addOnCompleteListener(task -> {
                        mInitCompleteCount++;
                        if(modes.length == mInitCompleteCount) {
                            mUIHandler.post(() -> updateLeaderboardLayoutInstances(true));
                            mWorkerHandler.post(looper);
                            mWorkerHandler.post(LeaderboardManager.this::attachListeners);
                            mInitCompleteCount = 0;
                        }
                    });
            
        }
    }
    
    private void sendData() {
        
        for(Database.Mode mode : Database.modes) {
            
            Leaderboard lb = mode.leaderboard;
            
            if(!lb.currentUserData.hasNewData) continue;
            lb.currentUserData.hasNewData = false;
            
            mFirestoreDatabase.collection(lb.leaderboardName())
                    .document(Database.uid)
                    .set(lb.currentUserData)
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "failure", e);
                    });
        }
        
    }
    
    private void attachListeners() {
        //listeners
        for(Database.Mode mode : Database.modes) {
            
            Leaderboard lb = mode.leaderboard;
            
            mFirestoreDatabase.collection(lb.leaderboardName()).addSnapshotListener((queryDocumentSnapshots, e) -> {
                if(e != null) {
                    Log.e(TAG, "failure", e);
                }
                
                if(queryDocumentSnapshots != null) {
                    lb.updateUserData(queryDocumentSnapshots.getDocuments());
                    if(mode == Database.currentMode) {
                        mUIHandler.post(() -> updateLeaderboardLayoutInstances(true));
                    }
                }
                
            });
            
        }
    }
    
    public void forceSend() {
        for(Database.Mode m : Database.modes) {
            m.leaderboard.currentUserData.hasNewData = true;
        }
        mWorkerHandler.removeCallbacks(looper);
        mWorkerHandler.post(looper);
    }
    
    public void onSomethingImportantChanged() {
        mIsOnline = Utils.isOnline(mActivity.getApplicationContext());
        mIsOnlineChanged = true;
        mUIHandler.post(() -> updateLeaderboardLayoutInstances(false));
        
        if(mIsOnline) {
            if(!mInitialised) {
                mWorkerHandler.removeCallbacks(looper);
                mWorkerHandler.post(this::initLeaderboards);
            }
        }
    }
    
    public void updateLeaderboardLayoutInstances(boolean newData) {
        Database.Mode mode = Database.currentMode;
        Leaderboard leaderboard = mode.leaderboard;
        
        boolean gameOver = mActivity.state == MainActivity.STATE_FM_GAME_OVER;
        
        int newRankDailyHighscore = leaderboard.getCurrentUserHighscoreRank(Leaderboard.RANGE_DAILY);
        int newRankWeeklyHighscore = leaderboard.getCurrentUserHighscoreRank(Leaderboard.RANGE_WEEKLY);
        int newRankAllTimeHighscore = leaderboard.getCurrentUserHighscoreRank(Leaderboard.RANGE_ALL_TIME);
        int newRankDailyTileRecord = leaderboard.getCurrentUserTileRecordRank(Leaderboard.RANGE_DAILY);
        int newRankWeeklyTileRecord = leaderboard.getCurrentUserTileRecordRank(Leaderboard.RANGE_WEEKLY);
        int newRankAllTimeTileRecord = leaderboard.getCurrentUserTileRecordRank(Leaderboard.RANGE_ALL_TIME);
        
        final LeaderboardLayoutInstance i = leaderboardLayoutInstance;
        
        if(mIsOnlineChanged && !mIsOnline) {
            ViewUtils.setText("", i.tvRankDailyHighscore, i.tvRankWeeklyHighscore, i.tvRankAllTimeHighscore,
                    i.tvRankDailyTileRecord, i.tvRankWeeklyTileRecord, i.tvRankAllTimeTileRecord);
            
            ViewUtils.setImageDrawable(null, i.ivIndicatorDailyHighscore, i.ivIndicatorWeeklyHighscore, i.ivIndicatorAllTimeHighscore,
                    i.ivIndicatorDailyTileRecord, i.ivIndicatorWeeklyTileRecord, i.ivIndicatorAllTimeTileRecord,
                    
                    i.ivTrophyDailyHighscore, i.ivTrophyWeeklyHighscore, i.ivTrophyAllTimeHighscore,
                    i.ivTrophyDailyTileRecord, i.ivTrophyWeeklyTileRecord, i.ivTrophyAllTimeTileRecord);
            
            ViewUtils.setVisibility(View.INVISIBLE, i.vDivBigLeft, i.vDivBigRight, i.vDivSmallCenter,
                    i.vDivSmallLeft, i.vDivSmallRight,
                    
                    i.ivNoDataDailyHighscore, i.ivNoDataWeeklyHighscore, i.ivNoDataAllTimeHighscore,
                    i.ivNoDataDailyTileRecord, i.ivNoDataWeeklyTileRecord, i.ivNoDataAllTimeTileRecord,
                    
                    i.loadingViewDailyHighscore, i.loadingViewWeeklyHighscore, i.loadingViewAllTimeHighscore,
                    i.loadingViewDailyTileRecord, i.loadingViewWeeklyTileRecord, i.loadingViewAllTimeTileRecord,
                    
                    i.tvHeadlineAllTime, i.tvHeadlineToday, i.tvHeadlineWeekly,
                    
                    i.ivNoGameServices);
            
            i.ivNoInternet.setVisibility(View.VISIBLE);
            
        } else if(mIsOnlineChanged) {
            i.ivNoInternet.setVisibility(View.INVISIBLE);
            
            ViewUtils.setVisibility(View.VISIBLE, i.vDivBigLeft, i.vDivBigRight, i.vDivSmallCenter,
                    i.vDivSmallLeft, i.vDivSmallRight,
                    
                    i.tvHeadlineAllTime, i.tvHeadlineToday, i.tvHeadlineWeekly);
        }
        
        if(mIsOnline) {
            
            if(leaderboard.currentUserData != null) {
                
                ViewUtils.setVisibility(View.INVISIBLE,
                        i.loadingViewDailyHighscore, i.loadingViewWeeklyHighscore, i.loadingViewAllTimeHighscore,
                        i.loadingViewDailyTileRecord, i.loadingViewWeeklyTileRecord, i.loadingViewAllTimeTileRecord,
                        
                        i.ivNoDataDailyHighscore, i.ivNoDataWeeklyHighscore, i.ivNoDataAllTimeHighscore,
                        i.ivNoDataDailyTileRecord, i.ivNoDataWeeklyTileRecord, i.ivNoDataAllTimeTileRecord
                );
                
                //daily highscore
                updateLeaderboardPart(i.tvRankDailyHighscore, i.ivTrophyDailyHighscore, i.ivIndicatorDailyHighscore,
                        i.ivNoDataDailyHighscore, newRankDailyHighscore,
                        gameOver ? mode.gameStartRankDailyHighscore : mode.lastRankDailyHighscore, false);
                
                //Weekly highscore
                updateLeaderboardPart(i.tvRankWeeklyHighscore, i.ivTrophyWeeklyHighscore, i.ivIndicatorWeeklyHighscore,
                        i.ivNoDataWeeklyHighscore, newRankWeeklyHighscore,
                        gameOver ? mode.gameStartRankWeeklyHighscore : mode.lastRankWeeklyHighscore, false);
                
                //AllTime highscore
                updateLeaderboardPart(i.tvRankAllTimeHighscore, i.ivTrophyAllTimeHighscore, i.ivIndicatorAllTimeHighscore,
                        i.ivNoDataAllTimeHighscore, newRankAllTimeHighscore,
                        gameOver ? mode.gameStartRankAllTimeHighscore : mode.lastRankAllTimeHighscore, false);
                
                //daily TileRecord
                updateLeaderboardPart(i.tvRankDailyTileRecord, i.ivTrophyDailyTileRecord, i.ivIndicatorDailyTileRecord,
                        i.ivNoDataDailyTileRecord, newRankDailyTileRecord,
                        gameOver ? mode.gameStartRankDailyTileRecord : mode.lastRankDailyTileRecord, true);
                
                //Weekly TileRecord
                updateLeaderboardPart(i.tvRankWeeklyTileRecord, i.ivTrophyWeeklyTileRecord, i.ivIndicatorWeeklyTileRecord,
                        i.ivNoDataWeeklyTileRecord, newRankWeeklyTileRecord,
                        gameOver ? mode.gameStartRankWeeklyTileRecord : mode.lastRankWeeklyTileRecord, true);
                
                //AllTime TileRecord
                updateLeaderboardPart(i.tvRankAllTimeTileRecord, i.ivTrophyAllTimeTileRecord, i.ivIndicatorAllTimeTileRecord,
                        i.ivNoDataAllTimeTileRecord, newRankAllTimeTileRecord,
                        gameOver ? mode.gameStartRankAllTimeTileRecord : mode.lastRankAllTimeTileRecord, true);
                
            } else { //show loading views
                
                ViewUtils.setImageDrawable(null, i.ivIndicatorDailyHighscore, i.ivIndicatorWeeklyHighscore, i.ivIndicatorAllTimeHighscore,
                        i.ivIndicatorDailyTileRecord, i.ivIndicatorWeeklyTileRecord, i.ivIndicatorAllTimeTileRecord,
                        
                        i.ivTrophyDailyHighscore, i.ivTrophyWeeklyHighscore, i.ivTrophyAllTimeHighscore,
                        i.ivTrophyDailyTileRecord, i.ivTrophyWeeklyTileRecord, i.ivTrophyAllTimeTileRecord
                );
                
                ViewUtils.setVisibility(View.INVISIBLE,
                        i.ivNoDataDailyHighscore, i.ivNoDataWeeklyHighscore, i.ivNoDataAllTimeHighscore,
                        i.ivNoDataDailyTileRecord, i.ivNoDataWeeklyTileRecord, i.ivNoDataAllTimeTileRecord
                );
                
                ViewUtils.setVisibility(View.VISIBLE,
                        i.loadingViewDailyHighscore, i.loadingViewWeeklyHighscore, i.loadingViewAllTimeHighscore,
                        i.loadingViewDailyTileRecord, i.loadingViewWeeklyTileRecord, i.loadingViewAllTimeTileRecord
                );
                
                i.loadingViewController.start();
                
                return;
            }
            
        }
        
        mIsOnlineChanged = false;
        
        if(newData) {
            mUIHandler.postDelayed(() -> {
                
                mode.lastRankDailyHighscore = newRankDailyHighscore;
                mode.lastRankWeeklyHighscore = newRankWeeklyHighscore;
                mode.lastRankAllTimeHighscore = newRankAllTimeHighscore;
                mode.lastRankDailyTileRecord = newRankDailyTileRecord;
                mode.lastRankWeeklyTileRecord = newRankWeeklyTileRecord;
                mode.lastRankAllTimeTileRecord = newRankAllTimeTileRecord;
                
                updateLeaderboardLayoutInstances(false);
            }, RANK_BALANCE_TIME);
        }
        
        mLastValuesAvailable = true;
    }
    
    private void updateLeaderboardPart(TextView tvRank, ImageView ivTrophy, ImageView ivIndicator, ImageView ivNoData,
                                       int newRank, int lastRank, boolean tileRecord) {
        if(newRank < 0) {
            ivNoData.setVisibility(View.VISIBLE);
            ivTrophy.setImageDrawable(null);
            tvRank.setText("");
            ivIndicator.setImageDrawable(null);
        } else if(newRank < 4) {
            ivNoData.setVisibility(View.INVISIBLE);
            
            tvRank.setText("");
            ivTrophy.setImageDrawable(Utils.findTrophyDrawableForLeaderboard(newRank, mActivity.getApplicationContext()));
            ivIndicator.setImageDrawable(null);
        } else {
            ivNoData.setVisibility(View.INVISIBLE);
            
            ivTrophy.setImageDrawable(null);
            tvRank.setText(String.valueOf(newRank));
            setTextsizeOfRankTextView(tvRank);
            float indicatorOffset = indicatorOffset(tvRank);
            
            if(tileRecord) {
                ivIndicator.setTranslationX(indicatorOffset);
            } else {
                ivIndicator.setTranslationX(-indicatorOffset);
            }
            
            if(lastRank > 0 && lastRank < newRank) { // rank down
                tvRank.setTextColor(mColorRankDown);
                ivIndicator.setImageResource(R.drawable.rank_down);
                ivIndicator.setColorFilter(mColorRankDown);
                
            } else if(lastRank > 0 && lastRank > newRank) { // rank up
                tvRank.setTextColor(mColorRankUp);
                ivIndicator.setImageResource(R.drawable.rank_up);
                ivIndicator.setColorFilter(mColorRankUp);
                
            } else { // rank didn't change
                tvRank.setTextColor(Color.WHITE);
                ivIndicator.setImageDrawable(null);
            }
            
        }
    }
    
    public static LeaderboardLayoutInstance generateLeaderbaordInstance(@NonNull View view) {
        
        LeaderboardLayoutInstance i = new LeaderboardLayoutInstance();
        
        i.tvHeadlineAllTime = view.findViewById(R.id.tvAllTime);
        i.tvHeadlineWeekly = view.findViewById(R.id.tv7Days);
        i.tvHeadlineToday = view.findViewById(R.id.tvToday);
        
        i.vDivBigLeft = view.findViewById(R.id.vDivLbLargeLeft);
        i.vDivBigRight = view.findViewById(R.id.vDivLbLargeRight);
        i.vDivSmallRight = view.findViewById(R.id.vDivLbSmallRight);
        i.vDivSmallCenter = view.findViewById(R.id.vDivLbSmallCenter);
        i.vDivSmallLeft = view.findViewById(R.id.vDivLbSmallLeft);
        
        i.tvRankWeeklyHighscore = view.findViewById(R.id.tvRank7DaysHs);
        i.tvRankWeeklyTileRecord = view.findViewById(R.id.tvRank7DaysTr);
        i.tvRankDailyHighscore = view.findViewById(R.id.tvRankTodayHs);
        i.tvRankDailyTileRecord = view.findViewById(R.id.tvRankTodayTr);
        i.tvRankAllTimeHighscore = view.findViewById(R.id.tvRankAllTimeHs);
        i.tvRankAllTimeTileRecord = view.findViewById(R.id.tvRankAllTimeTr);
        
        i.ivIndicatorWeeklyHighscore = view.findViewById(R.id.ivIndicator7DaysHighscore);
        i.ivIndicatorWeeklyTileRecord = view.findViewById(R.id.ivIndicator7DaysTileRecord);
        i.ivIndicatorDailyHighscore = view.findViewById(R.id.ivIndicatorTodayHighscore);
        i.ivIndicatorDailyTileRecord = view.findViewById(R.id.ivIndicatorTodayTileRecord);
        i.ivIndicatorAllTimeHighscore = view.findViewById(R.id.ivIndicatorAllTimeHighscore);
        i.ivIndicatorAllTimeTileRecord = view.findViewById(R.id.ivIndicatorAllTimeTileRecord);
        
        i.ivTrophyWeeklyHighscore = view.findViewById(R.id.ivTrophy7DaysHs);
        i.ivTrophyWeeklyTileRecord = view.findViewById(R.id.ivTrophy7DaysTr);
        i.ivTrophyDailyHighscore = view.findViewById(R.id.ivTrophyTodayHs);
        i.ivTrophyDailyTileRecord = view.findViewById(R.id.ivTrophyTodayTr);
        i.ivTrophyAllTimeHighscore = view.findViewById(R.id.ivTrophyAllTimeHs);
        i.ivTrophyAllTimeTileRecord = view.findViewById(R.id.ivTrophyAllTimeTr);
        
        i.ivNoDataWeeklyHighscore = view.findViewById(R.id.ivInfinity7DaysHs);
        i.ivNoDataWeeklyTileRecord = view.findViewById(R.id.ivInfinity7DaysTr);
        i.ivNoDataDailyHighscore = view.findViewById(R.id.ivInfinityTodayHs);
        i.ivNoDataDailyTileRecord = view.findViewById(R.id.ivInfinityTodayTr);
        i.ivNoDataAllTimeHighscore = view.findViewById(R.id.ivInfinityAllTimeHs);
        i.ivNoDataAllTimeTileRecord = view.findViewById(R.id.ivInfinityAllTimeTr);
        
        i.loadingViewWeeklyHighscore = view.findViewById(R.id.loading7DaysHs);
        i.loadingViewWeeklyTileRecord = view.findViewById(R.id.loading7DaysTr);
        i.loadingViewDailyHighscore = view.findViewById(R.id.loadingTodayHs);
        i.loadingViewDailyTileRecord = view.findViewById(R.id.loadingTodayTr);
        i.loadingViewAllTimeHighscore = view.findViewById(R.id.loadingAllTimeHs);
        i.loadingViewAllTimeTileRecord = view.findViewById(R.id.loadingAllTimeTr);
        
        i.loadingViewController = new InfiniteLoadingController(3000, 1000,
                i.loadingViewWeeklyHighscore,
                i.loadingViewWeeklyTileRecord,
                i.loadingViewDailyHighscore,
                i.loadingViewDailyTileRecord,
                i.loadingViewAllTimeHighscore,
                i.loadingViewAllTimeTileRecord
        );
        
        i.ivNoInternet = view.findViewById(R.id.ivNoInternet);
        i.ivNoGameServices = view.findViewById(R.id.ivNoGameServices);
        
        return i;
    }
    
    private void setTextsizeOfRankTextView(TextView tv) {
        tv.setTextSize(mTextSizeMap.get(tv.getText().length()));
    }
    
    private float indicatorOffset(TextView tv) {
        return Utils.dpToPx(mActivity.getResources(), mIndicatorOffsetMap.get(tv.getText().length()));
    }
    
}
