package com.titaniel.best_2048_math_puzzle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.titaniel.best_2048_math_puzzle.admob.Admob;
import com.titaniel.best_2048_math_puzzle.database.Database;
import com.titaniel.best_2048_math_puzzle.database.DesignProvider;
import com.titaniel.best_2048_math_puzzle.fragments.AnimatedFragment;
import com.titaniel.best_2048_math_puzzle.fragments.Home;
import com.titaniel.best_2048_math_puzzle.fragments.dialog.GameOver;
import com.titaniel.best_2048_math_puzzle.fragments.dialog.Pause;
import com.titaniel.best_2048_math_puzzle.fragments.dialog.Undo;
import com.titaniel.best_2048_math_puzzle.fragments.dialog.Won;
import com.titaniel.best_2048_math_puzzle.fragments.game.Game;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 12;

    //states
    public static final int
            STATE_NONE = -1,
            STATE_FM_HOME = 0,
            STATE_FM_GAME = 1,
            STATE_FM_GAME_OVER = 2,
            STATE_FM_UNDO = 3,
            STATE_FM_WON = 4,
            STATE_FM_PAUSE = 5;

    public int state = STATE_FM_HOME;

    //fragments
    public Home home;
    public Game game;
    public GameOver gameOver;
    public Undo undo;
    public Won won;
    public Pause pause;

    private Handler mHandler = new Handler();

    private Handler mAdmobHandler;

    private GoogleSignInAccount mGoogleSignAccount;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HandlerThread thread = new HandlerThread("admob");
        thread.start();
        mAdmobHandler = new Handler(thread.getLooper());
//        mAdmobHandler.post(() -> {
        //admob
        Admob.init(this, mHandler);
//        });

        //Database
        Database.init(this);

        //Design Provider
        DesignProvider.init(this);

        //signinclient
        mGoogleSignInClient = GoogleSignIn.getClient(this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build());

        //init Fragments
        home = (Home) getSupportFragmentManager().findFragmentById(R.id.fragmentHome);
        game = (Game) getSupportFragmentManager().findFragmentById(R.id.fragmentGame);
        undo = (Undo) getSupportFragmentManager().findFragmentById(R.id.fragmentBacks);
        gameOver = (GameOver) getSupportFragmentManager().findFragmentById(R.id.fragmentGameOver);
        pause = (Pause) getSupportFragmentManager().findFragmentById(R.id.fragmentPause);
        won = (Won) getSupportFragmentManager().findFragmentById(R.id.fragmentWon);

        mHandler.postDelayed(() -> showState(STATE_FM_HOME, 0, null), 500);
//        mHandler.postDelayed(this::signInSilently, 500);
    }

    private void signInSilently() {
        mGoogleSignInClient.silentSignIn().addOnCompleteListener(this,
                task -> {
                    if(task.isSuccessful()) {
                        mGoogleSignAccount = task.getResult();
                    } else {
                        startSignInIntent();
                    }
                });
    }

    private void startSignInIntent() {
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                mGoogleSignAccount = task.getResult(ApiException.class);
            } catch (ApiException apiException) {

                String message = apiException.getMessage();
                if(message == null || message.isEmpty()) {
                    message = "määääääaännnn";
                }

                new AlertDialog.Builder(this)
                        .setMessage(message)
                        .setNeutralButton(android.R.string.ok, null)
                        .show();
            }
        }
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
            case STATE_FM_UNDO:
                return undo;
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
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //hide statusbar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        Database.load();

        mHandler.post(() -> {
            if(Admob.rewardedVideoAd != null) Admob.rewardedVideoAd.resume(this);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

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
        }
    }
}
