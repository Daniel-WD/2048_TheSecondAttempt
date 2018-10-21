package com.titaniel.best_2048_math_puzzle;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.tasks.Task;
import com.titaniel.best_2048_math_puzzle.admob.Admob;
import com.titaniel.best_2048_math_puzzle.connectivity_receiver.ConnectivityReceiver;
import com.titaniel.best_2048_math_puzzle.database.Database;
import com.titaniel.best_2048_math_puzzle.database.DesignProvider;
import com.titaniel.best_2048_math_puzzle.fragments.AnimatedFragment;
import com.titaniel.best_2048_math_puzzle.fragments.Home;
import com.titaniel.best_2048_math_puzzle.fragments.Logo;
import com.titaniel.best_2048_math_puzzle.fragments.dialog.Backs;
import com.titaniel.best_2048_math_puzzle.fragments.dialog.GameOver;
import com.titaniel.best_2048_math_puzzle.fragments.dialog.Goal;
import com.titaniel.best_2048_math_puzzle.fragments.dialog.LogIn;
import com.titaniel.best_2048_math_puzzle.fragments.dialog.Pause;
import com.titaniel.best_2048_math_puzzle.fragments.dialog.Trophy;
import com.titaniel.best_2048_math_puzzle.fragments.trophy_chamber.TrophyChamber;
import com.titaniel.best_2048_math_puzzle.fragments.dialog.Won;
import com.titaniel.best_2048_math_puzzle.fragments.game.Game;
import com.titaniel.best_2048_math_puzzle.game_services.GameServices;
import com.titaniel.best_2048_math_puzzle.leaderboard_manager.LeaderboardManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 12;
    private static final int RC_ACHIEVEMENT_UI = 23;
    private static final int RC_LEADERBOARD_UI = 34;

    //states
    public static final int
            STATE_NONE = -1,
            STATE_FM_HOME = 0,
            STATE_FM_GAME = 1,
            STATE_FM_GAME_OVER = 2,
            STATE_FM_BACKS = 3,
            STATE_FM_WON = 4,
            STATE_FM_PAUSE = 5,
            STATE_FM_LOGO = 6,
            STATE_FM_GOAL = 7,
            STATE_FM_LOGIN = 8,
            STATE_FM_TROPHY = 9,
            STATE_FM_TROPHY_CHAMBER = 10;

    public int state = STATE_FM_HOME;

    //fragments
    public Home home;
    public Game game;
    public GameOver gameOver;
    public Backs backs;
    public Won won;
    public Pause pause;
    public Logo logo;
    public Goal goal;
    public LogIn logIn;
    public Trophy trophy;
    public TrophyChamber trophyChamber;

    private Handler mHandler = new Handler();

    public GoogleSignInAccount googleSignInAccount;
    public GoogleSignInClient googleSignInClient;

    public ConnectivityReceiver mConnectivityReceiver;

    private FrameLayout mContainer, mLyPopup;

    public LeaderboardManager leaderbaordManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ids
        mContainer = findViewById(R.id.lyContainer);
        mLyPopup = findViewById(R.id.lyPopup);

        //gameservices sign in client
        googleSignInClient = GoogleSignIn.getClient(this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build());

        //Database
        Database.init(this);

        //init logo fragment
        logo = (Logo) getSupportFragmentManager().findFragmentById(R.id.fragmentLogo);

        mHandler.postDelayed(() -> showState(STATE_FM_LOGO, 0, null), 100);
    }

    public void initAll() {

        //leaderboardManager
        leaderbaordManager = new LeaderboardManager(this);
        leaderbaordManager.start();

        //admob
        Admob.init(this, mHandler);

        //Design Provider
        DesignProvider.init(this);

        //Game Services
        GameServices.init(this);

        //init Fragments
        home = new Home();
        trophy = new Trophy();
        game = new Game();
        backs = new Backs();
        gameOver = new GameOver();
        pause = new Pause();
        won = new Won();
        goal = new Goal();
        logIn = new LogIn();
        trophyChamber = new TrophyChamber();

        getSupportFragmentManager().beginTransaction().add(R.id.lyContainer, home).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.lyContainer, game).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.lyContainer, backs).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.lyContainer, gameOver).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.lyContainer, pause).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.lyContainer, won).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.lyContainer, goal).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.lyContainer, logIn).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.lyContainer, trophy).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.lyContainer, trophyChamber).commit();

    }

    public void showAchievements() {
        if(googleSignInAccount == null) return;

        Games.getAchievementsClient(this, googleSignInAccount)
                .getAchievementsIntent()
                .addOnSuccessListener(intent -> startActivityForResult(intent, RC_ACHIEVEMENT_UI));

    }

    public void showLeaderboard() {
        if(googleSignInAccount == null) return;

        Games.getLeaderboardsClient(this, googleSignInAccount)
                .getAllLeaderboardsIntent()
                .addOnSuccessListener(intent -> startActivityForResult(intent, RC_LEADERBOARD_UI));

    }

    public void showLeaderboard(String id) {
        if(googleSignInAccount == null) return;

        Games.getLeaderboardsClient(this, googleSignInAccount)
                .getLeaderboardIntent(id)
                .addOnSuccessListener(intent -> startActivityForResult(intent, RC_LEADERBOARD_UI));
    }

    public void onSomethingImportantChanges() {
        if(home != null) home.updateUiState();
        if(game != null) game.updateUiState();
    }

    public void signInSilently() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account != null) {
            googleSignInAccount = account;
            onSomethingImportantChanges();
            attachPopUpView();
        } else {
            googleSignInClient.silentSignIn().addOnCompleteListener(this,
                    task -> {
                        if(task.isSuccessful()) {
                            googleSignInAccount = task.getResult();
                            onSomethingImportantChanges();
                            attachPopUpView();
                        } else {
                            startSignInIntent();
                        }
                    });
        }

    }

    public void startSignInIntent() {
        startActivityForResult(googleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                googleSignInAccount = task.getResult(ApiException.class);


            } catch (ApiException e) {
                e.printStackTrace();
//                Utils.toast(getApplicationContext(), "code: " + e.getStatusCode());
            }

            attachPopUpView();
            onSomethingImportantChanges();

        }
    }

    private void attachPopUpView() {
        googleSignInAccount.requestExtraScopes(Games.SCOPE_GAMES_LITE);

        GamesClient client = Games.getGamesClient(getApplicationContext(), googleSignInAccount);

        client.setViewForPopups(mLyPopup);
//        client.setGravityForPopups(Gravity.TOP | Gravity.CENTER_HORIZONTAL);

    }

    public void showState(int newState, long delay, @Nullable AnimatedFragment oldFragment) {
        AnimatedFragment nextFragment = findFragmentByState(newState);
        if(nextFragment == null || delay < 0) return;
        state = newState;
        if(oldFragment != null) delay += oldFragment.hide(delay);
        nextFragment.show(delay);
    }

    public long hideState(int state, long delay) {
        AnimatedFragment fragment = findFragmentByState(state);
        if(fragment == null || delay < 0) return 0;
        this.state = STATE_NONE;
        return fragment.hide(delay);
    }

    private AnimatedFragment findFragmentByState(int state) {
        switch(state) {
            case STATE_FM_BACKS:
                return backs;
            case STATE_FM_HOME:
                return home;
            case STATE_FM_GAME:
                return game;
            case STATE_FM_GAME_OVER:
                return gameOver;
            case STATE_FM_PAUSE:
                return pause;
            case STATE_FM_WON:
                return won;
            case STATE_FM_LOGO:
                return logo;
            case STATE_FM_GOAL:
                return goal;
            case STATE_FM_LOGIN:
                return logIn;
            case STATE_FM_TROPHY:
                return trophy;
            case STATE_FM_TROPHY_CHAMBER:
                return trophyChamber;
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        //connectivity receiver
        mConnectivityReceiver = new ConnectivityReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION); //"android.net.conn.CONNECTIVITY_CHANGE"
        registerReceiver(mConnectivityReceiver, intentFilter);

        //hide statusbar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        Database.load();

        mHandler.post(() -> {
            if(Admob.rewardedVideoAd != null) Admob.rewardedVideoAd.resume(this);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mConnectivityReceiver);

        //save game
        if(state == STATE_FM_GAME) {
            Database.currentMode.saved = game.gameField.getSaveImage();
        }

        Database.save();

        mHandler.post(() -> {
            if(Admob.rewardedVideoAd != null) Admob.rewardedVideoAd.pause(this);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mHandler.post(() -> {
            if(Admob.rewardedVideoAd != null) Admob.rewardedVideoAd.destroy(this);
        });
    }

    @Override
    public void onBackPressed() {
        switch(state) {
            case STATE_FM_HOME:
                super.onBackPressed();
                break;
            case STATE_FM_GAME:
                game.onBackPressed();
                break;
            case STATE_FM_GAME_OVER:
                gameOver.onBackPressed();
                break;
            case STATE_FM_PAUSE:
                pause.onBackPressed();
                break;
            case STATE_FM_BACKS:
                backs.onBackPressed();
                break;
            case STATE_FM_WON:
                won.onBackPressed();
                break;
            case STATE_FM_LOGO:
                super.onBackPressed();
                break;
            case STATE_FM_GOAL:
                goal.onBackPressed();
                break;
            case STATE_FM_LOGIN:
                logIn.onBackPressed();
                break;
            case STATE_FM_TROPHY:
                trophy.onBackPressed();
                break;
            case STATE_FM_TROPHY_CHAMBER:
                trophyChamber.onBackPressed();
                break;
        }
    }
}
