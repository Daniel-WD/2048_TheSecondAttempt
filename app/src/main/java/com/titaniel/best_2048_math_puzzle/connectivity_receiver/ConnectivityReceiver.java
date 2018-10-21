package com.titaniel.best_2048_math_puzzle.connectivity_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.titaniel.best_2048_math_puzzle.MainActivity;

public class ConnectivityReceiver extends BroadcastReceiver {

    private MainActivity mActivity;

    public ConnectivityReceiver(MainActivity mActivity) {
        this.mActivity = mActivity;
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        mActivity.onSomethingImportantChanges();

    }

}
