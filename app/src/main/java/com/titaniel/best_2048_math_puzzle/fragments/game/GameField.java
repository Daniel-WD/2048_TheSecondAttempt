package com.titaniel.best_2048_math_puzzle.fragments.game;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import androidx.annotation.Nullable;
import androidx.vectordrawable.graphics.drawable.PathInterpolatorCompat;
import androidx.core.content.ContextCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.titaniel.best_2048_math_puzzle.R;
import com.titaniel.best_2048_math_puzzle.database.DesignProvider;
import com.titaniel.best_2048_math_puzzle.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameField extends View {

    public static final int MAX_IMAGE_SIZE = 20;

    interface SelectListener {
        void onTileSelected(Tile tile);

        void onTileDeselected(Tile tile);
    }

    private SelectListener mSelectListener = null;

    interface MoveListener {
        void onMove(int direction);

        void onJoin(int points, int maxNumber);

        void onMoveCompleted();

        void onBackCompleted();
    }

    private MoveListener mMoveListener = null;

    class Tile {

        final static int STATE_UNPAIRED = 0, STATE_REMOVE_IN_FUTURE = 1, STATE_INCREASE_IN_FUTURE = 2;

        Tile(int row, int col, int id) {
            int[] pos = positionForRowCol(row, col);
            this.col = col;
            this.row = row;
            this.x = pos[0];
            this.y = pos[1];
            this.id = id;
        }

        float x, y; //top left corner of block!!!
        float scale = 1, effectScale = 1;
        float rotation = 0; //affects all(inclusive border)
        int id, row, col;
        int effectAlpha = 255;

        int nextRow = -1, nextCol = -1;
        int state = STATE_UNPAIRED;

        boolean increasing = false;

        void animateToSelectMode() {

            ValueAnimator anim = ValueAnimator.ofFloat(scale, 0.8f);
            anim.addUpdateListener(valueAnimator -> {
                scale = (float) valueAnimator.getAnimatedValue();
                invalidate();
            });
            anim.setDuration(SELECT_MODE_TOGGLE_DURATION);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.start();

        }

        void animateToNormalMode() {

            ValueAnimator anim = ValueAnimator.ofFloat(scale, 1);
            anim.addUpdateListener(valueAnimator -> {
                scale = (float) valueAnimator.getAnimatedValue();
                invalidate();
            });
            anim.setDuration(SELECT_MODE_TOGGLE_DURATION);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.start();

        }

        void animateReturn() {
            ValueAnimator anim = ValueAnimator.ofFloat(0, 1f);
            anim.addUpdateListener(valueAnimator -> {
                scale = (float) valueAnimator.getAnimatedValue();
                invalidate();
            });
            anim.setDuration(RETURN_DURATION);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.start();
        }

        void animateRemove() {
            ValueAnimator anim = ValueAnimator.ofFloat(scale, 0f);
            anim.addUpdateListener(valueAnimator -> {
                scale = (float) valueAnimator.getAnimatedValue();
                invalidate();
            });
            anim.setDuration(REMOVE_DURATION);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.start();
        }

        void animateGeneration() {
            ValueAnimator anim = ValueAnimator.ofFloat(0, 1f);
            anim.addUpdateListener(valueAnimator -> {
                scale = (float) valueAnimator.getAnimatedValue();
                invalidate();
            });
            anim.setDuration(GENERATE_DURATION);
            anim.setInterpolator(new FastOutSlowInInterpolator());
            anim.start();
        }

        void animateFirstGeneration() {
            long dur = 250;

            long delay = 0;

            scale = 0;
            ValueAnimator anim = ValueAnimator.ofFloat(0, 1f);
            anim.addUpdateListener(valueAnimator -> {
                scale = (float) valueAnimator.getAnimatedValue();
                invalidate();
            });
            anim.setStartDelay(delay);
            anim.setDuration(dur);
            anim.setInterpolator(new FastOutSlowInInterpolator());
            anim.start();
        }

        void animateRegeneration(long delay) {
            long dur = 400;

            scale = 0;
            ValueAnimator anim = ValueAnimator.ofFloat(0, 1f);
            anim.addUpdateListener(valueAnimator -> {
                scale = (float) valueAnimator.getAnimatedValue();
                invalidate();
            });
            anim.setStartDelay(delay);
            anim.setDuration(dur);
            anim.setInterpolator(new FastOutSlowInInterpolator());
            anim.start();
        }

        @Deprecated
        void moveToNextLocation() {
            if(nextCol == col) nextCol = -1;
            if(nextRow == row) nextRow = -1;
            if(nextRow == -1 && nextCol == -1) return;
            row = nextRow < 0 ? row : nextRow;
            col = nextCol < 0 ? col : nextCol;

            nextRow = -1;
            nextCol = -1;

            float oldX = x, oldY = y;

            int[] newPos = positionForRowCol(row, col);
            float dX = newPos[0] - x;
            float dY = newPos[1] - y;

            ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
            anim.addUpdateListener(valueAnimator -> {
                float f = (float) valueAnimator.getAnimatedValue();
                x = oldX + dX*f;
                y = oldY + dY*f;
                invalidate();
            });
            anim.setDuration(MOVE_DURATION);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.start();
        }

        void increase() {
            id++;

//            ValueAnimator rotationAnim = ValueAnimator.ofFloat(-90, 0);
//            rotationAnim.addUpdateListener(valueAnimator -> {
//                rotation = (float) valueAnimator.getAnimatedValue();
//                invalidate();
//            });
//            rotationAnim.setDuration(JOIN_DURATION);
//            rotationAnim.setInterpolator(new AccelerateDecelerateInterpolator());
//            rotationAnim.start();

            increasing = true;

            ValueAnimator tileScaleAnim = ValueAnimator.ofFloat(1, 1.15f);
            tileScaleAnim.addUpdateListener(valueAnimator -> {
                scale = (float) valueAnimator.getAnimatedValue();
                invalidate();
            });
            tileScaleAnim.setRepeatMode(ValueAnimator.REVERSE);
            tileScaleAnim.setRepeatCount(1);
            tileScaleAnim.setDuration(JOIN_DURATION/2);
            tileScaleAnim.setInterpolator(new LinearInterpolator());
            tileScaleAnim.start();

//            ValueAnimator effectScaleAnim = ValueAnimator.ofFloat(1.15f, 1.5f);
//            effectScaleAnim.addUpdateListener(valueAnimator -> {
//                effectScale = (float) valueAnimator.getAnimatedValue();
//                invalidate();
//            });
////            effectScaleAnim.setStartDelay(JOIN_DURATION/2);
//            effectScaleAnim.setDuration(JOIN_DURATION*2);
//            effectScaleAnim.setInterpolator(new AccelerateInterpolator(0.5f));
//            effectScaleAnim.start();
//
//            ValueAnimator effectAlphaAnim = ValueAnimator.ofInt(200, 0);
//            effectAlphaAnim.addUpdateListener(valueAnimator -> {
//                effectAlpha = (int) valueAnimator.getAnimatedValue();
//                invalidate();
//            });
////            effectAlphaAnim.setStartDelay(JOIN_DURATION/2);
//            effectAlphaAnim.setDuration(JOIN_DURATION*2);
//            effectAlphaAnim.setInterpolator(new AccelerateInterpolator(0.5f));
//            effectAlphaAnim.start();

//            postDelayed(() -> increasing = false, tileScaleAnim.getDuration());

            state = STATE_UNPAIRED;
        }

        // SELECTION THINGS

        boolean isSelected = false;

        void toggleSelected() {
            isSelected = !isSelected;
            if(isSelected) {
                ValueAnimator anim = ValueAnimator.ofFloat(scale, 1f);
                anim.addUpdateListener(valueAnimator -> {
                    scale = (float) valueAnimator.getAnimatedValue();
                    invalidate();
                });
                anim.setDuration(SELECT_MODE_TOGGLE_DURATION);
                anim.setInterpolator(new AccelerateDecelerateInterpolator());
                anim.start();
                if(mSelectListener != null) mSelectListener.onTileSelected(this);
            } else {
                ValueAnimator anim = ValueAnimator.ofFloat(scale, 0.8f);
                anim.addUpdateListener(valueAnimator -> {
                    scale = (float) valueAnimator.getAnimatedValue();
                    invalidate();
                });
                anim.setDuration(SELECT_MODE_TOGGLE_DURATION);
                anim.setInterpolator(new AccelerateDecelerateInterpolator());
                anim.start();
                if(mSelectListener != null) mSelectListener.onTileDeselected(this);
            }
        }

        //MASS MOVING THINGS

        float oldX, oldY;
        float dX, dY;

        boolean prepareForMassMove() {
            boolean needsMove = true;
            if(nextCol == col) nextCol = -1;
            if(nextRow == row) nextRow = -1;
            if(nextRow == -1 && nextCol == -1) needsMove = false;
            row = nextRow < 0 ? row : nextRow;
            col = nextCol < 0 ? col : nextCol;

            nextRow = -1;
            nextCol = -1;

            oldX = x;
            oldY = y;

            int[] newPos = positionForRowCol(row, col);
            dX = newPos[0] - x;
            dY = newPos[1] - y;

            return needsMove;
        }

        void updateMassMove(float f) {
            x = oldX + dX*f;
            y = oldY + dY*f;
        }
    }

    public static class FieldImage {

        public FieldImage(int[][] image) {
            this.image = image;
        }

        public int[][] image;

    }

    class TextConfig {

        TextPaint paint;
        float yOffset;

    }

    private ArrayList<FieldImage> mImages = new ArrayList<>();

    private final static long GENERATE_DURATION = 120;
    private final static long MOVE_DURATION = 110;
    private final static long JOIN_DURATION = 120;

    private final static long REMOVE_DURATION = 150;
    private final static long RETURN_DURATION = 150;

    private final static long SELECT_MODE_TOGGLE_DURATION = 150;

    public final static int DIRECTION_UP = 0;
    public final static int DIRECTION_DOWN = 1;
    public final static int DIRECTION_LEFT = 2;
    public final static int DIRECTION_RIGHT = 3;

    private ArrayList<Tile> mTiles = new ArrayList<>();
    private ArrayList<Tile> mMoveExcludedTiles = new ArrayList<>();

    private final Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int mBorderColor;
    private int mDividerColor;

    private final float mBorderWidth = 1f;
    private final float mDividerWidth = 1f;
    private final float mMinSwipeDistance = 20;
    private final float mBorderRadius = 3.7f;

    private float mBorderWidthPx, mDividerWidthPx, mBorderRadiusPx, mMinSwipeDistancePx;

    private float mDividerScale = 0.27f;
    private float mBorderScale = 0.86f;
    private final float mTileScale = 0.91f;
    private final float mTileRadiusRatio = 0.042f;
    private final float mBorderPaddingRatio = 0.02f;
    private final float mFieldScale = 1f; //0.98

    private int mWidth, mHeight;

    private int mFullBlockSize;
    private int mBorderPadding;

    private int mFieldSize = 4;

    private Random mRandom = new Random();

    private float mDownX = 0, mDownY = 0;
    private boolean canMove = true;
    boolean inSelectMode = false;

    private Tile mDownTile = null;

    private Path mBaseTilePath, mBorderPath, mDividersPath;

    private final int TILE_DESIGN_NUMBER = 0; // TODO: 04.09.2018 ...

    public GameField(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

//        setScaleX(2f - mFieldScale);
//        setScaleY(2f - mFieldScale);

        mBorderColor = ContextCompat.getColor(context, R.color.game_border_color);
        mDividerColor = ContextCompat.getColor(context, R.color.game_divider_color);

        mBorderWidthPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mBorderWidth, getResources().getDisplayMetrics());
        mDividerWidthPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mDividerWidth, getResources().getDisplayMetrics());
        mMinSwipeDistancePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mMinSwipeDistance, getResources().getDisplayMetrics());
        mBorderRadiusPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mBorderRadius, getResources().getDisplayMetrics());

        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidthPx);

        mDividerPaint.setStyle(Paint.Style.STROKE);
        mDividerPaint.setColor(mDividerColor);
        mDividerPaint.setStrokeWidth(mDividerWidthPx);

    }

    public void animateIn() {

        long delay = 0;

        long dur = 400;

        //divider
        ValueAnimator divScaleAnim = ValueAnimator.ofFloat(0f, mDividerScale);
        divScaleAnim.addUpdateListener(animator -> {
            mDividerScale = (float) animator.getAnimatedValue();
            invalidate();
        });
        divScaleAnim.setStartDelay(delay);
        divScaleAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        divScaleAnim.setDuration(dur);
        divScaleAnim.start();

        mDividerPaint.setColor(Color.TRANSPARENT);
        ValueAnimator divAlphaAnim = ValueAnimator.ofArgb(Color.TRANSPARENT, mDividerColor);
        divAlphaAnim.addUpdateListener(animator -> {
            mDividerPaint.setColor((int) animator.getAnimatedValue());
            invalidate();
        });
        divAlphaAnim.setStartDelay(delay);
        divAlphaAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        divAlphaAnim.setDuration(dur);
        divAlphaAnim.start();

        delay += 0;

        dur = 600;

        //border
        ValueAnimator borderScaleAnim = ValueAnimator.ofFloat(0f, mBorderScale);
        borderScaleAnim.addUpdateListener(animator -> {
            mBorderScale = (float) animator.getAnimatedValue();
            invalidate();
        });
        borderScaleAnim.setStartDelay(delay);
        borderScaleAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        borderScaleAnim.setDuration(dur);
        borderScaleAnim.start();

        mBorderPaint.setColor(Color.TRANSPARENT);
        ValueAnimator borderAlphaAnim = ValueAnimator.ofArgb(Color.TRANSPARENT, mBorderColor);
        borderAlphaAnim.addUpdateListener(animator -> {
            mBorderPaint.setColor((int) animator.getAnimatedValue());
            invalidate();
        });
        borderAlphaAnim.setStartDelay(delay);
        borderAlphaAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        borderAlphaAnim.setDuration(dur);
        borderAlphaAnim.start();

    }

    public void startSelectMode(SelectListener selectListener) {
        mSelectListener = selectListener;
        inSelectMode = true;
        setAllTilesSelected(false);
        for(Tile tile : mTiles) {
            tile.animateToSelectMode();
        }
    }

    public void stopSelectMode() {
        mSelectListener = null;
        inSelectMode = false;
        setAllTilesSelected(false);
        for(Tile tile : mTiles) {
            tile.animateToNormalMode();
        }
    }

    public void removeSelectListener() {
        mSelectListener = null;
    }

    public boolean performBack() {
        if(mImages.size() == 0) return false;
        ArrayList<Tile> mRemovedTiles = new ArrayList<>();
        int[][] lastImage = mImages.get(mImages.size() - 1).image;
        for(Tile tile : mTiles) {
            if(lastImage[tile.row][tile.col] != tile.id) {
                tile.animateRemove();
                mRemovedTiles.add(tile);
            }
        }
        postDelayed(() -> {
            mTiles.removeAll(mRemovedTiles);

            for(int i = 0; i < lastImage.length; i++) {
                nextTile:
                for(int j = 0; j < lastImage.length; j++) {
                    if(lastImage[i][j] == -1) continue;
                    for(Tile tile : mTiles) {
                        if(tile.row == i && tile.col == j) continue nextTile;
                    }
                    Tile newTile = new Tile(i, j, lastImage[i][j]);
                    mTiles.add(newTile);
                    newTile.animateReturn();
                }
            }

            mImages.remove(mImages.size() - 1);
        }, REMOVE_DURATION);

        postDelayed(() -> {
            if(mMoveListener != null) mMoveListener.onBackCompleted();
        }, REMOVE_DURATION*2);

        return true;
    }

    public boolean canPerformBack() {
        return mImages.size() != 0;
    }

    public void deleteTile(Tile tile) {
        saveBackup();
        tile.animateRemove();
        postDelayed(() -> {
            mTiles.remove(tile);
        }, REMOVE_DURATION);
    }

    public void swapTiles(Tile tile1, Tile tile2) {
        saveBackup();
        tile1.nextCol = tile2.col;
        tile1.nextRow = tile2.row;
        tile2.nextCol = tile1.col;
        tile2.nextRow = tile1.row;
        massMove();
    }

    public void doubleTile(Tile tile) {
        saveBackup();
        tile.increase();
        if(mMoveListener != null)
            mMoveListener.onJoin(Utils.idToNum(tile.id), Utils.idToNum(tile.id));
    }

    public boolean canUserMove() {
        if(mTiles.size() < mFieldSize*mFieldSize) return true;
        boolean result = false;
        moveVertical(true, true);
        for(Tile tile : mTiles) {
            if(tile.nextRow != -1 && tile.nextRow != tile.row) {
                tile.nextRow = -1;
                result = true;
            }
            tile.state = Tile.STATE_UNPAIRED;
        }
        moveHorizontal(true, true);
        for(Tile tile : mTiles) {
            if(tile.nextCol != -1 && tile.nextCol != tile.col) {
                tile.nextCol = -1;
                result = true;
            }
            tile.state = Tile.STATE_UNPAIRED;
        }
        canMove = true;
        return result;
    }

    public ArrayList<FieldImage> getSaveImage() {
        ArrayList<FieldImage> save = new ArrayList<>(mImages);
        save.add(new FieldImage(buildFieldImage()));
        return save;
    }

    public void setSaveImageAndAnimate(ArrayList<FieldImage> images) {
        if(images == null) return;
        canMove = false;
        int[][] last = images.get(images.size() - 1).image;
        for(int i = 0; i < last.length; i++) {
            for(int j = 0; j < last.length; j++) {
                if(last[i][j] == -1) continue;
                Tile newTile = new Tile(i, j, last[i][j]);
                mTiles.add(newTile);
                newTile.animateRegeneration(mRandom.nextInt(300));
            }
        }
        if(images.size() > 0) images.remove(images.size() - 1);
        mImages = images;
        postDelayed(() -> canMove = true, 300);
    }


    private void moveVertical(boolean up, boolean calcOnly) {
        canMove = false;
        long first = System.currentTimeMillis();

        if(mMoveListener != null) {
            mMoveListener.onMove(up ? DIRECTION_UP : DIRECTION_DOWN);
        }

        Collections.sort(mTiles, (tile, t1) -> {
            if(up) {
                return Integer.compare(tile.row, t1.row);
            } else {
                return Integer.compare(t1.row, tile.row);
            }
        });

        for(Tile tile : mTiles) {
            if((up && tile.row > 0) || (!up && tile.row < mFieldSize - 1)) {
                Tile nextTile = null;
                for(Tile t : mTiles) {
                    if(t.equals(tile)) break;
                    if(tile.col == t.col) { //same col
                        nextTile = t;
                    }
                }

                if(nextTile == null) {
                    tile.nextRow = up ? 0 : mFieldSize - 1;
                } else {
                    if(nextTile.state != Tile.STATE_UNPAIRED || tile.id != nextTile.id) {

                        if(up) {
                            tile.nextRow = nextTile.nextRow == -1 ? nextTile.row + 1 : nextTile.nextRow + 1;
                        } else {
                            tile.nextRow = nextTile.nextRow == -1 ? nextTile.row - 1 : nextTile.nextRow - 1;
                        }
                    } else {
                        tile.state = Tile.STATE_REMOVE_IN_FUTURE;
                        nextTile.state = Tile.STATE_INCREASE_IN_FUTURE;
                        tile.nextRow = nextTile.nextRow == -1 ? nextTile.row : nextTile.nextRow;
                    }
                }
            }
        }

        if(!calcOnly) actionsAfterMoveCalc();

        Log.e("moveVertical:duration", String.valueOf(System.currentTimeMillis() - first));
    }

    private void moveHorizontal(boolean left, boolean calcOnly) {
        canMove = false;
        long first = System.currentTimeMillis();

        if(mMoveListener != null) {
            mMoveListener.onMove(left ? DIRECTION_LEFT : DIRECTION_RIGHT);
        }

        Collections.sort(mTiles, (tile, t1) -> {
            if(left) {
                return Integer.compare(tile.col, t1.col);
            } else {
                return Integer.compare(t1.col, tile.col);
            }
        });

        for(Tile tile : mTiles) {
            if((left && tile.col > 0) || (!left && tile.col < mFieldSize - 1)) {
                Tile nextTile = null;
                for(Tile t : mTiles) {
                    if(t.equals(tile)) break;
                    if(tile.row == t.row) { //same row
                        nextTile = t;
                    }
                }

                if(nextTile == null) {
                    tile.nextCol = left ? 0 : mFieldSize - 1;
                } else {
                    if(nextTile.state != Tile.STATE_UNPAIRED || tile.id != nextTile.id) {

                        if(left) {
                            tile.nextCol = nextTile.nextCol == -1 ? nextTile.col + 1 : nextTile.nextCol + 1;
                        } else {
                            tile.nextCol = nextTile.nextCol == -1 ? nextTile.col - 1 : nextTile.nextCol - 1;
                        }
                    } else {
                        tile.state = Tile.STATE_REMOVE_IN_FUTURE;
                        nextTile.state = Tile.STATE_INCREASE_IN_FUTURE;
                        tile.nextCol = nextTile.nextCol == -1 ? nextTile.col : nextTile.nextCol;
                    }
                }
            }
        }

        if(!calcOnly) actionsAfterMoveCalc();

        Log.e("moveHorizontal:duration", String.valueOf(System.currentTimeMillis() - first));
    }

    private void actionsAfterMoveCalc() {

//        for(Tile tile : mTiles) {
//            tile.moveToNextLocation(); --> deprecated
//        }
        saveBackup();

        boolean smtNeedToMove = massMove();

        if(smtNeedToMove) {
            postDelayed(() -> {
                joinTiles();

                postDelayed(() -> {
                    generateTile(false);

                    postDelayed(() -> {
                        canMove = true;
                        if(mMoveListener != null) mMoveListener.onMoveCompleted();
                    }, 20);

                }, 10);

            }, MOVE_DURATION);

        } else {
            mImages.remove(mImages.size() - 1);
            canMove = true;
        }
    }

    private boolean massMove() {
        boolean smtNeedsMove = false;
        mMoveExcludedTiles.clear();
        for(Tile tile : mTiles) {
            if(tile.prepareForMassMove() && !smtNeedsMove) {
                smtNeedsMove = true;
            }
        }
        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(valueAnimator -> {
            float f = (float) valueAnimator.getAnimatedValue();
            for(Tile tile : mTiles) {
                if(!mMoveExcludedTiles.contains(tile)) tile.updateMassMove(f);
            }
            invalidate();
        });
        anim.setDuration(MOVE_DURATION);
        anim.setInterpolator(new LinearInterpolator());
        if(smtNeedsMove) {
            anim.start();
        }

        return smtNeedsMove;
    }

    private boolean joinTiles() {
        int maxNumber = 0, points = 0;
        ArrayList<Tile> removableTiles = new ArrayList<>();
        for(Tile tile : mTiles) {
            if(tile.state == Tile.STATE_REMOVE_IN_FUTURE) {
                removableTiles.add(tile);
            } else if(tile.state == Tile.STATE_INCREASE_IN_FUTURE) {
                tile.increase();
                points += Utils.idToNum(tile.id);
                maxNumber = Math.max(maxNumber, Utils.idToNum(tile.id));
            }
        }
        int constPoints = points, constMaxNumber = maxNumber;
        postDelayed(() -> {
            if(mMoveListener != null) mMoveListener.onJoin(constPoints, constMaxNumber);
        }, JOIN_DURATION);
        mTiles.removeAll(removableTiles);
        return removableTiles.size() != 0;
    }

    private void generateTile(boolean first) {
        if(mTiles.size() >= mFieldSize*mFieldSize) return;

        int rCol;
        int rRow;
        int id = mRandom.nextInt(2);

        do {
            rCol = mRandom.nextInt(mFieldSize);
            rRow = mRandom.nextInt(mFieldSize);
        } while(!isPositionEmpty(rRow, rCol));

//        Log.d("sldkjf", "col::" + rCol + " row::" + rRow);

        Tile tile = new Tile(rRow, rCol, id);

//        Log.d("sldkjfk", "x::" + tile.x + " y::" + tile.y);

        mTiles.add(tile);
        mMoveExcludedTiles.add(tile);
        if(first) {
            tile.animateFirstGeneration();
        } else {
            tile.animateGeneration();
        }

//        animateGeneration(tile);
    }

    private void setAllTilesSelected(boolean selected) {
        for(Tile tile : mTiles) {
            tile.isSelected = selected;
        }
    }

    public void showStartTiles() {
        int tileCount = 2;
        canMove = false;

        long delay = 150;
        for(int i = 0; i < tileCount; i++) {
            postDelayed(() -> generateTile(true), i*delay);
        }
        postDelayed(() -> canMove = true, tileCount*delay + GENERATE_DURATION);
    }

    private void saveBackup() {
        if(mImages.size() == MAX_IMAGE_SIZE) mImages.remove(0);
        mImages.add(new FieldImage(buildFieldImage()));
    }

    private int[][] buildFieldImage() {
        int[][] image = new int[mFieldSize][mFieldSize];
        for(int i = 0; i < image.length; i++) {
            for(int j = 0; j < image[i].length; j++) {
                image[i][j] = -1;
            }
        }
        for(Tile tile : mTiles) {
            image[tile.row][tile.col] = tile.id;
        }
        return image;
    }

    private boolean isPositionEmpty(int row, int col) {
        for(int i = 0; i < mTiles.size(); i++) {
            Tile tile = mTiles.get(i);
            if(tile.row == row && tile.col == col) return false;
        }
        return true;
    }

    private int[] positionForRowCol(int row, int col) {
        int[] res = new int[2];
        res[0] = col*mFullBlockSize;
        res[1] = row*mFullBlockSize;
        return res;
    }

    private int realTileSize() {
        float negative = mFullBlockSize*(1 - mTileScale);
        return (int) (mFullBlockSize - negative);
    }

    public void clear() {
        mTiles.clear();
        mImages.clear();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float halfNegativeBorder = mFullBlockSize*(1 - mDividerScale)/2;
        float halfNegativeTile = mFullBlockSize*(1 - mTileScale)/2;
        float borderMargin = mBorderPaint.getStrokeWidth()/2;
        float tileRadius = (mFullBlockSize - halfNegativeBorder*2)*mTileRadiusRatio;

        //BORDER
//        canvas.drawLine(0, borderMargin, 0, mHeight - borderMargin, mBorderPaint);
//        canvas.drawLine(mWidth, borderMargin, mWidth, mHeight - borderMargin, mBorderPaint);
//        canvas.drawLine(borderMargin, 0, mWidth - borderMargin, 0, mBorderPaint);
//        canvas.drawLine(borderMargin, mHeight, mWidth - borderMargin, mHeight, mBorderPaint);

        canvas.drawRoundRect(borderMargin, borderMargin,
                mWidth - borderMargin, mHeight - borderMargin,
                mBorderRadiusPx, mBorderRadiusPx, mBorderPaint);

        canvas.save();
        canvas.translate(mBorderPadding, mBorderPadding);

        //TILE HOLES
        for(int i = 0; i < mFieldSize; i++) {
            for(int j = 0; j < mFieldSize; j++) {
                canvas.drawRoundRect(halfNegativeBorder + i*mFullBlockSize, halfNegativeBorder + j*mFullBlockSize,
                        (mFullBlockSize - halfNegativeBorder) + i*mFullBlockSize, (mFullBlockSize - halfNegativeBorder) + j*mFullBlockSize,
                        tileRadius, tileRadius, mDividerPaint);
//                if(i != 0)
//                    canvas.drawLine(mFullBlockSize*i, mFullBlockSize*j + halfNegative, mFullBlockSize*i, mFullBlockSize*(j + 1) - halfNegative, mDividerPaint);
//                if(j != 0)
//                    canvas.drawLine(mFullBlockSize*i + halfNegative, mFullBlockSize*j, mFullBlockSize*(i + 1) - halfNegative, mFullBlockSize*j, mDividerPaint);
            }
        }
//        }


        for(Tile tile : mTiles) {

            canvas.save();
            canvas.translate(tile.x + halfNegativeTile, tile.y + halfNegativeTile);

            if(tile.rotation != 0 && tile.rotation != 360) {
                canvas.rotate(tile.rotation, realTileSize()/2, realTileSize()/2);
            }

//            if(tile.increasing) {
//                canvas.save();
//                if(tile.effectScale != 1) {
//                    canvas.scale(tile.effectScale, tile.effectScale, realTileSize()/2, realTileSize()/2);
//                }
//                Drawable effect = DesignProvider.tileDesigns.get(TILE_DESIGN_NUMBER).increaseEffect;
//                effect.setTint(DesignProvider.tileDesigns.get(TILE_DESIGN_NUMBER).effColors[tile.id]);
//                effect.setAlpha(tile.effectAlpha);
//                effect.draw(canvas);
//                canvas.restore();
//            }

            if(tile.scale != 1) {
                canvas.save();
                canvas.scale(tile.scale, tile.scale, realTileSize()/2, realTileSize()/2);
            }
            DesignProvider.tileDesigns.get(TILE_DESIGN_NUMBER).tiles[tile.id].draw(canvas);
            if(tile.scale != 1) canvas.restore();


//            long first = System.nanoTime();
//            Log.e("draw::checkFOUR", String.valueOf(System.nanoTime() - first));
//            Log.e("draw::check", "------------------------------------------------------------------------------------");

            canvas.restore();

        }

        canvas.restore();

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isEnabled()) return false;
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(inSelectMode) {
                    mDownTile = findTileArea(event.getX(), event.getY());
                    break;
                }

                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                if(inSelectMode) {
                    if(mDownTile == null) break;
                    Tile tile = findTileArea(event.getX(), event.getY());
                    if(mDownTile.equals(tile)) {
                        tile.toggleSelected();
                    }
                    break;
                }
            case MotionEvent.ACTION_MOVE:
                if(inSelectMode) break;
                if(!canMove) {
                    return false;
                }
                float dX = event.getX() - mDownX;
                float dY = event.getY() - mDownY;
                if(Math.abs(dX) > Math.abs(dY) && Math.abs(dX) >= mMinSwipeDistancePx) {
                    mDownX = event.getX();
                    mDownY = event.getY();
                    if(dX < 0) {
                        moveHorizontal(true, false);
                    } else {
                        moveHorizontal(false, false);
                    }
                } else if(Math.abs(dY) > Math.abs(dX) && Math.abs(dY) >= mMinSwipeDistancePx) {
                    mDownX = event.getX();
                    mDownY = event.getY();
                    if(dY < 0) {
                        moveVertical(true, false);
                    } else {
                        moveVertical(false, false);
                    }
                }
                break;
        }
        return true;
    }

    private Tile findTileArea(float x, float y) {
        for(Tile tile : mTiles) {
            if(tile.x <= x && tile.y <= y && tile.x + mFullBlockSize >= x && tile.y + mFullBlockSize >= y) {
                return tile;
            }
        }
        return null;
    }


    public void setFieldSize(int size) {
        mFieldSize = size;
        onSizeChanged(getWidth(), getHeight(), getWidth(), getHeight());
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;

        mBorderPadding = (int) (mBorderPaddingRatio*mWidth);

        mFullBlockSize = (w - 2*mBorderPadding)/mFieldSize;

        //base tile path
        mBaseTilePath = new Path();
        float halfNegative = mFullBlockSize*(1 - mTileScale)/2;
        float radius = (mFullBlockSize - halfNegative*2)*mTileRadiusRatio;
        mBaseTilePath.addRoundRect(0, 0, realTileSize(), realTileSize(),
                radius, radius, Path.Direction.CW);

        // border path
        mBorderPath = new Path();

        float borderLength = getWidth() - 2*halfNegative;

        float borderMargin = halfNegative + (1 - mBorderScale)*borderLength/2;

        //left border
        mBorderPath.moveTo(0, borderMargin);
        mBorderPath.lineTo(0, mHeight - borderMargin);

        //right border
        mBorderPath.moveTo(mWidth, borderMargin);
        mBorderPath.lineTo(mWidth, mHeight - borderMargin);

        //top border
        mBorderPath.moveTo(borderMargin, 0);
        mBorderPath.lineTo(mWidth - borderMargin, 0);

        //bottom border
        mBorderPath.moveTo(borderMargin, mHeight);
        mBorderPath.lineTo(mWidth - borderMargin, mHeight);

        //dividers path
        mDividersPath = new Path();

        for(int i = 0; i < mFieldSize; i++) {
            for(int j = 0; j < mFieldSize; j++) {
                if(i != 0) {
                    mDividersPath.moveTo(mFullBlockSize*i, mFullBlockSize*j + halfNegative);
                    mDividersPath.lineTo(mFullBlockSize*i, mFullBlockSize*(j + 1) - halfNegative);
                }
                if(j != 0) {
                    mDividersPath.moveTo(mFullBlockSize*i + halfNegative, mFullBlockSize*j);
                    mDividersPath.lineTo(mFullBlockSize*(i + 1) - halfNegative, mFullBlockSize*j);
                }
            }
        }

        //tile drawable sizes
        DesignProvider.setSize(realTileSize());

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.v("GameField onMeasure w", MeasureSpec.toString(widthMeasureSpec));
        Log.v("GameField onMeasure h", MeasureSpec.toString(heightMeasureSpec));

        int width = MeasureSpec.getSize(widthMeasureSpec);

        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(width, heightMeasureSpec));
    }

    private int getMaxPadding() {
        return Math.max(getPaddingStart(), Math.max(getPaddingTop(), Math.max(getPaddingBottom(), getPaddingEnd())));
    }


    public void setMoveListener(MoveListener listener) {
        mMoveListener = listener;
    }

    public void removeMoveListener() {
        mMoveListener = null;
    }
}
