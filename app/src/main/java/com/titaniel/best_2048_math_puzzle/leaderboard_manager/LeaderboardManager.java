package com.titaniel.best_2048_math_puzzle.leaderboard_manager;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.titaniel.best_2048_math_puzzle.MainActivity;
import com.titaniel.best_2048_math_puzzle.R;
import com.titaniel.best_2048_math_puzzle.database.Database;
import com.titaniel.best_2048_math_puzzle.loading_view.LoadingView;
import com.titaniel.best_2048_math_puzzle.loading_view.controller.InfiniteLoadingController;
import com.titaniel.best_2048_math_puzzle.utils.AnimUtils;
import com.titaniel.best_2048_math_puzzle.utils.Utils;
import com.titaniel.best_2048_math_puzzle.utils.ViewUtils;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class LeaderboardManager {
    
    public static final String TAG = LeaderboardManager.class.getSimpleName();
    
    public static class LeaderboardLayoutInstance {
        
        
        //headlines
        public TextView tvHeadlineDaily;
        public TextView tvHeadlineAllTime;
        public TextView tvHeadlineWeekly;
        
        //dividers
        public View vDivBigLeft;
        public View vDivBigRight;
        public View vDivSmallLeft;
        public View vDivSmallCenter;
        public View vDivSmallRight;
        
        //rank textviews
        public TextView tvRankDailyHighscore;
        public TextView tvRankDailyTileRecord;
        public TextView tvRankWeeklyHighscore;
        public TextView tvRankWeeklyTileRecord;
        public TextView tvRankAllTimeHighscore;
        public TextView tvRankAllTimeTileRecord;
        
        //indicators (up or down)
        public ImageView ivIndicatorDailyHighscore;
        public ImageView ivIndicatorDailyTileRecord;
        public ImageView ivIndicatorWeeklyHighscore;
        public ImageView ivIndicatorWeeklyTileRecord;
        public ImageView ivIndicatorAllTimeHighscore;
        public ImageView ivIndicatorAllTimeTileRecord;
        
        //trophyies for 1, 2, 3
        public ImageView ivTrophyDailyHighscore;
        public ImageView ivTrophyDailyTileRecord;
        public ImageView ivTrophyWeeklyHighscore;
        public ImageView ivTrophyWeeklyTileRecord;
        public ImageView ivTrophyAllTimeHighscore;
        public ImageView ivTrophyAllTimeTileRecord;
        
        //infinities... no data
        public ImageView ivNoDataDailyHighscore;
        public ImageView ivNoDataDailyTileRecord;
        public ImageView ivNoDataWeeklyHighscore;
        public ImageView ivNoDataWeeklyTileRecord;
        public ImageView ivNoDataAllTimeHighscore;
        public ImageView ivNoDataAllTimeTileRecord;
        
        //loading views
        public LoadingView loadingViewDailyHighscore;
        public LoadingView loadingViewDailyTileRecord;
        public LoadingView loadingViewWeeklyHighscore;
        public LoadingView loadingViewWeeklyTileRecord;
        public LoadingView loadingViewAllTimeHighscore;
        public LoadingView loadingViewAllTimeTileRecord;
        
        public InfiniteLoadingController loadingViewController;
        
        //no internet
        public ImageView ivNoInternet;
        
        //no game services
        public ImageView ivNoGameServices;
        
    }
    
    private static final int SEND_INTERVAL_TIME = 20000;
    private static final int RANK_BALANCE_TIME = 4000;
    
    private SparseArray<Float> mTextSizeMap = new SparseArray<>();
    private SparseArray<Float> mIndicatorOffsetMap = new SparseArray<>();
    
    private LeaderboardLayoutInstance mLeaderboardLayoutInstance = null;
    private boolean mInstanceChanged = true;
    
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
    
    private int mNewRankDailyHighscore = -1;
    private int mNewRankWeeklyHighscore = -1;
    private int mNewRankAllTimeHighscore = -1;
    private int mNewRankDailyTileRecord = -1;
    private int mNewRankWeeklyTileRecord = -1;
    private int mNewRankAllTimeTileRecord = -1;
    
    private Runnable mRankUnifier = () -> {
        Database.Mode mode = Database.currentMode;
        
        mode.lastRankDailyHighscore = mNewRankDailyHighscore;
        mode.lastRankWeeklyHighscore = mNewRankWeeklyHighscore;
        mode.lastRankAllTimeHighscore = mNewRankAllTimeHighscore;
        mode.lastRankDailyTileRecord = mNewRankDailyTileRecord;
        mode.lastRankWeeklyTileRecord = mNewRankWeeklyTileRecord;
        mode.lastRankAllTimeTileRecord = mNewRankAllTimeTileRecord;
        
        updateLeaderboardLayoutInstance(false);
    };
    
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
                            mUIHandler.post(() -> updateLeaderboardLayoutInstance(true));
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
                        mUIHandler.post(() -> updateLeaderboardLayoutInstance(true));
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
        mUIHandler.post(() -> updateLeaderboardLayoutInstance(false));
        
        if(mIsOnline) {
            if(!mInitialised) {
                mWorkerHandler.removeCallbacks(looper);
                mWorkerHandler.post(this::initLeaderboards);
            }
        }
    }
    
    public void updateLeaderboardLayoutInstance(boolean newData) {
        Database.Mode mode = Database.currentMode;
        Leaderboard leaderboard = mode.leaderboard;
        
        boolean gameOver = mActivity.state == MainActivity.STATE_FM_GAME_OVER;
        
        mNewRankDailyHighscore = leaderboard.getCurrentUserHighscoreRank(Leaderboard.RANGE_DAILY);
        mNewRankWeeklyHighscore = leaderboard.getCurrentUserHighscoreRank(Leaderboard.RANGE_WEEKLY);
        mNewRankAllTimeHighscore = leaderboard.getCurrentUserHighscoreRank(Leaderboard.RANGE_ALL_TIME);
        mNewRankDailyTileRecord = leaderboard.getCurrentUserTileRecordRank(Leaderboard.RANGE_DAILY);
        mNewRankWeeklyTileRecord = leaderboard.getCurrentUserTileRecordRank(Leaderboard.RANGE_WEEKLY);
        mNewRankAllTimeTileRecord = leaderboard.getCurrentUserTileRecordRank(Leaderboard.RANGE_ALL_TIME);
        
        final LeaderboardLayoutInstance i = mLeaderboardLayoutInstance;
        
        if(i == null) return;
        
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
                    
                    i.tvHeadlineAllTime, i.tvHeadlineDaily, i.tvHeadlineWeekly,
                    
                    i.ivNoGameServices);
            
            i.ivNoInternet.setVisibility(View.VISIBLE);
            
        } else if(mIsOnlineChanged) {
            i.ivNoInternet.setVisibility(View.INVISIBLE);
            
            ViewUtils.setVisibility(View.VISIBLE, i.vDivBigLeft, i.vDivBigRight, i.vDivSmallCenter,
                    i.vDivSmallLeft, i.vDivSmallRight,
                    
                    i.tvHeadlineAllTime, i.tvHeadlineDaily, i.tvHeadlineWeekly);
        }
        
        if(mIsOnline) {
            
            if(leaderboard.currentUserData != null) {
//
//                ViewUtils.setVisibility(View.INVISIBLE,
//                        i.loadingViewDailyHighscore, i.loadingViewWeeklyHighscore, i.loadingViewAllTimeHighscore,
//                        i.loadingViewDailyTileRecord, i.loadingViewWeeklyTileRecord, i.loadingViewAllTimeTileRecord,
//
//                        i.ivNoDataDailyHighscore, i.ivNoDataWeeklyHighscore, i.ivNoDataAllTimeHighscore,
//                        i.ivNoDataDailyTileRecord, i.ivNoDataWeeklyTileRecord, i.ivNoDataAllTimeTileRecord
//                );
                
                makeInvisibleT(i.loadingViewDailyHighscore, i.loadingViewWeeklyHighscore, i.loadingViewAllTimeHighscore,
                        i.loadingViewDailyTileRecord, i.loadingViewWeeklyTileRecord, i.loadingViewAllTimeTileRecord);
                
                //daily highscore
                updateLeaderboardPart(i.tvRankDailyHighscore, i.ivTrophyDailyHighscore, i.ivIndicatorDailyHighscore,
                        i.ivNoDataDailyHighscore, mNewRankDailyHighscore,
                        gameOver ? mode.gameStartRankDailyHighscore : mode.lastRankDailyHighscore, false, newData);
                
                //Weekly highscore
                updateLeaderboardPart(i.tvRankWeeklyHighscore, i.ivTrophyWeeklyHighscore, i.ivIndicatorWeeklyHighscore,
                        i.ivNoDataWeeklyHighscore, mNewRankWeeklyHighscore,
                        gameOver ? mode.gameStartRankWeeklyHighscore : mode.lastRankWeeklyHighscore, false, newData);
                
                //AllTime highscore
                updateLeaderboardPart(i.tvRankAllTimeHighscore, i.ivTrophyAllTimeHighscore, i.ivIndicatorAllTimeHighscore,
                        i.ivNoDataAllTimeHighscore, mNewRankAllTimeHighscore,
                        gameOver ? mode.gameStartRankAllTimeHighscore : mode.lastRankAllTimeHighscore, false, newData);
                
                //daily TileRecord
                updateLeaderboardPart(i.tvRankDailyTileRecord, i.ivTrophyDailyTileRecord, i.ivIndicatorDailyTileRecord,
                        i.ivNoDataDailyTileRecord, mNewRankDailyTileRecord,
                        gameOver ? mode.gameStartRankDailyTileRecord : mode.lastRankDailyTileRecord, true, newData);
                
                //Weekly TileRecord
                updateLeaderboardPart(i.tvRankWeeklyTileRecord, i.ivTrophyWeeklyTileRecord, i.ivIndicatorWeeklyTileRecord,
                        i.ivNoDataWeeklyTileRecord, mNewRankWeeklyTileRecord,
                        gameOver ? mode.gameStartRankWeeklyTileRecord : mode.lastRankWeeklyTileRecord, true, newData);
                
                //AllTime TileRecord
                updateLeaderboardPart(i.tvRankAllTimeTileRecord, i.ivTrophyAllTimeTileRecord, i.ivIndicatorAllTimeTileRecord,
                        i.ivNoDataAllTimeTileRecord, mNewRankAllTimeTileRecord,
                        gameOver ? mode.gameStartRankAllTimeTileRecord : mode.lastRankAllTimeTileRecord, true, newData);
                
            } else { //show loading views

//                ViewUtils.setImageDrawable(null, i.ivIndicatorDailyHighscore, i.ivIndicatorWeeklyHighscore, i.ivIndicatorAllTimeHighscore,
//                        i.ivIndicatorDailyTileRecord, i.ivIndicatorWeeklyTileRecord, i.ivIndicatorAllTimeTileRecord,
//
//                        i.ivTrophyDailyHighscore, i.ivTrophyWeeklyHighscore, i.ivTrophyAllTimeHighscore,
//                        i.ivTrophyDailyTileRecord, i.ivTrophyWeeklyTileRecord, i.ivTrophyAllTimeTileRecord
//                );

//                ViewUtils.setVisibility(View.INVISIBLE,
//                        i.ivNoDataDailyHighscore, i.ivNoDataWeeklyHighscore, i.ivNoDataAllTimeHighscore,
//                        i.ivNoDataDailyTileRecord, i.ivNoDataWeeklyTileRecord, i.ivNoDataAllTimeTileRecord
//                );
                
                makeInvisibleT(
                        i.ivNoDataDailyHighscore, i.ivNoDataWeeklyHighscore, i.ivNoDataAllTimeHighscore,
                        i.ivNoDataDailyTileRecord, i.ivNoDataWeeklyTileRecord, i.ivNoDataAllTimeTileRecord,
                        
                        i.ivIndicatorDailyHighscore, i.ivIndicatorWeeklyHighscore, i.ivIndicatorAllTimeHighscore,
                        i.ivIndicatorDailyTileRecord, i.ivIndicatorWeeklyTileRecord, i.ivIndicatorAllTimeTileRecord,
                        
                        i.ivTrophyDailyHighscore, i.ivTrophyWeeklyHighscore, i.ivTrophyAllTimeHighscore,
                        i.ivTrophyDailyTileRecord, i.ivTrophyWeeklyTileRecord, i.ivTrophyAllTimeTileRecord
                );

//                ViewUtils.setVisibility(View.VISIBLE,
//                        i.loadingViewDailyHighscore, i.loadingViewWeeklyHighscore, i.loadingViewAllTimeHighscore,
//                        i.loadingViewDailyTileRecord, i.loadingViewWeeklyTileRecord, i.loadingViewAllTimeTileRecord
//                );
                
                makeVisibleT(
                        i.loadingViewDailyHighscore, i.loadingViewWeeklyHighscore, i.loadingViewAllTimeHighscore,
                        i.loadingViewDailyTileRecord, i.loadingViewWeeklyTileRecord, i.loadingViewAllTimeTileRecord
                );
                
                i.loadingViewController.start();
                
                return;
            }
            
        }
        
        mIsOnlineChanged = false;
        
        if(newData) {
            mUIHandler.removeCallbacks(mRankUnifier);
            mUIHandler.postDelayed(mRankUnifier, RANK_BALANCE_TIME);
        }
        
        mLastValuesAvailable = true;
        mInstanceChanged = false;
    }
    
    private void updateLeaderboardPart(TextView tvRank, ImageView ivTrophy, ImageView ivIndicator, ImageView ivNoData,
                                       int newRank, int lastRank, boolean tileRecord, boolean newData) {
        long duration = 200;
        
        if(newRank < 0) {
            makeVisibleT(ivNoData);
            
            makeInvisibleT(ivTrophy);
            makeInvisibleT(tvRank);
            makeInvisibleT(ivIndicator);
            
        } else if(newRank < 4) {
            makeInvisibleT(ivNoData);
            makeInvisibleT(tvRank);
            makeInvisibleT(ivIndicator);
            
            Drawable newTrophyDrawable = Utils.findTrophyDrawableForLeaderboard(newRank, mActivity.getApplicationContext());
            
            if(newData) {
                long delay = makeInvisibleT(ivTrophy);
                mUIHandler.postDelayed(() -> {
                    ivTrophy.setImageDrawable(newTrophyDrawable);
                    makeVisibleT(ivTrophy);
                }, delay);
            }/* else {
                ivTrophy.setImageDrawable(newTrophyDrawable);
                makeVisibleT(ivTrophy);
            }*/
            
            
        } else {
            makeInvisibleT(ivTrophy);
            makeInvisibleT(ivNoData);
            
            tvRank.setText(String.valueOf(newRank));
            
            setTextsizeOfRankTextView(tvRank);
            float indicatorOffset = indicatorOffset(tvRank);
            if(tileRecord) {
                ivIndicator.setTranslationX(indicatorOffset);
            } else {
                ivIndicator.setTranslationX(-indicatorOffset);
            }
            
            makeVisibleT(tvRank);
            
            if(lastRank > 0 && lastRank < newRank) { // rank down
                AnimUtils.animateTextColor(tvRank, mColorRankDown, duration, 0);
                
                ivIndicator.setImageResource(R.drawable.rank_down);
                ivIndicator.setColorFilter(mColorRankDown);
                
                makeVisibleT(ivIndicator);
                
            } else if(lastRank > 0 && lastRank > newRank) { // rank up
                AnimUtils.animateTextColor(tvRank, mColorRankUp, duration, 0);
                
                ivIndicator.setImageResource(R.drawable.rank_up);
                ivIndicator.setColorFilter(mColorRankUp);
                
                mUIHandler.post(() -> {
                    makeVisibleT(ivIndicator);
                });
                
            } else { // rank didn't change
                AnimUtils.animateTextColor(tvRank, Color.WHITE, duration, 0);
                makeInvisibleT(ivIndicator);
                
            }
            
        }
    }
    
    private long makeInvisibleT(View... views) {
        long duration = 250;
        for(View view : views) {
            if(mInstanceChanged || view.getAlpha() != 0) {
                AnimUtils.animateAlpha(view, new AccelerateDecelerateInterpolator(), 0, duration, 0);
                AnimUtils.animateTranslationY(view, new AccelerateDecelerateInterpolator(), Utils.dpToPx(mActivity.getResources(), 3), duration, 0);
            }
        }
        return duration;
    }
    
    private long makeVisibleT(View... views) {
        long duration = 250;
        for(View view : views) {
            if(mInstanceChanged || view.getAlpha() != 1) {
                view.setTranslationY(-Utils.dpToPx(mActivity.getResources(), 3));
                AnimUtils.animateTranslationY(view, new AccelerateDecelerateInterpolator(), 0, duration, 0);
                AnimUtils.animateAlpha(view, new AccelerateDecelerateInterpolator(), 1, duration, 0);
            }
        }
        return duration;
    }
    
    public static LeaderboardLayoutInstance generateLeaderbaordInstance(@NonNull View view) {
        
        LeaderboardLayoutInstance i = new LeaderboardLayoutInstance();
        
        i.tvHeadlineAllTime = view.findViewById(R.id.tvAllTime);
        i.tvHeadlineWeekly = view.findViewById(R.id.tv7Days);
        i.tvHeadlineDaily = view.findViewById(R.id.tvToday);
        
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
    
    public void setLeaderboardLayoutInstance(LeaderboardLayoutInstance i) {
        this.mLeaderboardLayoutInstance = i;
        mInstanceChanged = true;
        
        mUIHandler.removeCallbacks(mRankUnifier);
        
        ViewUtils.setBatchAlpha(0, i.tvRankDailyHighscore, i.tvRankWeeklyHighscore, i.tvRankAllTimeHighscore,
                i.tvRankDailyTileRecord, i.tvRankWeeklyTileRecord, i.tvRankAllTimeTileRecord,
                
                i.ivIndicatorDailyHighscore, i.ivIndicatorWeeklyHighscore, i.ivIndicatorAllTimeHighscore,
                i.ivIndicatorDailyTileRecord, i.ivIndicatorWeeklyTileRecord, i.ivIndicatorAllTimeTileRecord,
                
                i.ivTrophyDailyHighscore, i.ivTrophyWeeklyHighscore, i.ivTrophyAllTimeHighscore,
                i.ivTrophyDailyTileRecord, i.ivTrophyWeeklyTileRecord, i.ivTrophyAllTimeTileRecord,
                
                i.ivNoDataDailyHighscore, i.ivNoDataWeeklyHighscore, i.ivNoDataAllTimeHighscore,
                i.ivNoDataDailyTileRecord, i.ivNoDataWeeklyTileRecord, i.ivNoDataAllTimeTileRecord,
                
                i.loadingViewDailyHighscore, i.loadingViewWeeklyHighscore, i.loadingViewAllTimeHighscore,
                i.loadingViewDailyTileRecord, i.loadingViewWeeklyTileRecord, i.loadingViewAllTimeTileRecord);
    }
    
    public void notifyModeChanged() {


//        Database.Mode mode = Database.currentMode;
//
//        mode.lastRankDailyHighscore = mode.leaderboard.getCurrentUserHighscoreRank(Leaderboard.RANGE_DAILY);
//        mode.lastRankWeeklyHighscore = mode.leaderboard.getCurrentUserHighscoreRank(Leaderboard.RANGE_WEEKLY);
//        mode.lastRankAllTimeHighscore = mode.leaderboard.getCurrentUserHighscoreRank(Leaderboard.RANGE_ALL_TIME);
//        mode.lastRankDailyTileRecord = mode.leaderboard.getCurrentUserTileRecordRank(Leaderboard.RANGE_DAILY);
//        mode.lastRankWeeklyTileRecord = mode.leaderboard.getCurrentUserTileRecordRank(Leaderboard.RANGE_WEEKLY);
//        mode.lastRankAllTimeTileRecord = mode.leaderboard.getCurrentUserTileRecordRank(Leaderboard.RANGE_ALL_TIME);
        
        mUIHandler.removeCallbacks(mRankUnifier);
    }
    
}
