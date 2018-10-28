package com.titaniel.best_2048_math_puzzle.fragments.trophy_chamber;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.titaniel.best_2048_math_puzzle.R;
import com.titaniel.best_2048_math_puzzle.database.Database;
import com.titaniel.best_2048_math_puzzle.utils.Utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TrophyAdapter extends RecyclerView.Adapter<TrophyAdapter.TrophyRowHolder> {

    class TrophyRowHolder extends RecyclerView.ViewHolder {

        private View mDivLeft, mDivRight, mDivCenter;
        private ImageView mIvTrophy7DaysTr, mIvTrophyOneDayHs, mIvTrophy7DaysHs, mIvTrophyOneDayTr;
        private ImageView mIvSize;

        public TrophyRowHolder(@NonNull View itemView) {
            super(itemView);

            mDivLeft = itemView.findViewById(R.id.vDivLeft);
            mDivRight = itemView.findViewById(R.id.vDivRight);
            mDivCenter = itemView.findViewById(R.id.vDivCenter);
            mIvTrophy7DaysTr = itemView.findViewById(R.id.ivTrophy7DaysTr);
            mIvTrophy7DaysHs = itemView.findViewById(R.id.ivTrophy7DaysHs);
            mIvTrophyOneDayTr = itemView.findViewById(R.id.ivTrophyOneDayTr);
            mIvTrophyOneDayHs = itemView.findViewById(R.id.ivTrophyOneDayHs);
            mIvSize = itemView.findViewById(R.id.ivSize);

        }
    }

    private Context mContext;

    public TrophyAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public TrophyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_trophy_chamber_row, parent, false);
        return new TrophyRowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrophyRowHolder holder, int position) {
        Database.Mode mode = Database.modes[position];

        holder.mIvSize.setImageResource(mode.representative);
        holder.mIvTrophy7DaysTr.setImageResource(Utils.findTrophyDrawableIdForTrophyChamber(mode.trophy7DaysTileRecord));
        holder.mIvTrophy7DaysHs.setImageResource(Utils.findTrophyDrawableIdForTrophyChamber(mode.trophy7DaysHighscore));
        holder.mIvTrophyOneDayTr.setImageResource(Utils.findTrophyDrawableIdForTrophyChamber(mode.trophyOneDayTileRecord));
        holder.mIvTrophyOneDayHs.setImageResource(Utils.findTrophyDrawableIdForTrophyChamber(mode.trophyOneDayHighscore));
    }

    @Override
    public int getItemCount() {
        return Database.modes.length;
    }
}
