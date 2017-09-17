package tictactoe.unal.edu.co.androidtic_tac_toe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by crow on 17/09/17.
 */

public class BoardView extends View {

    public static final int GRID_WIDTH = 6;
    private Bitmap mHumanBitmap;
    private Bitmap mComputerBitmap;
    private Paint mPaint;
    private TicTacToeGame mGame;

    private int mBoardWidth = 600;
    private int mBoardHeight = 600;
    private int mBoardCellWidth = mBoardWidth / 3;
    private int mBoardCellHeight = mBoardHeight / 3;
    private int mBoardGridWidth = 6;

    public BoardView(Context context) {
        super(context);
        initialize();
    }

    public BoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }



    public void initialize() {
        mHumanBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bx);
        mComputerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bo);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int halfGridWidth = mBoardGridWidth / 2;
        mPaint.setColor(Color.GRAY);
        mPaint.setStyle(Paint.Style.FILL);

        // Draw the board lines
        canvas.drawRect(mBoardCellWidth - halfGridWidth, 2,
                mBoardCellWidth + halfGridWidth, mBoardHeight, mPaint);
        canvas.drawRect(mBoardCellWidth * 2 - halfGridWidth, 2,
                mBoardCellWidth * 2 + halfGridWidth, mBoardHeight, mPaint);
        canvas.drawRect(0, mBoardCellHeight - halfGridWidth,
                mBoardWidth, mBoardCellHeight + halfGridWidth, mPaint);
        canvas.drawRect(0, mBoardCellHeight * 2 - halfGridWidth,
                mBoardWidth, mBoardCellHeight * 2 + halfGridWidth, mPaint);
        // Draw all the pieces
        for (int i = 0; i < TicTacToeGame.BOARD_SIZE; i++) {
            int col = i % 3;
            int row = i / 3;
            // Define the boundaries of a destination rectangle for the image

            int left = col*mBoardCellWidth;
            int top = row*mBoardCellWidth;
            int right = left + mBoardCellWidth;
            int bottom = top + mBoardCellWidth;

            if (mGame != null && mGame.getBoardOccupant(i) == TicTacToeGame.HUMAN_PLAYER) {
                canvas.drawBitmap(mHumanBitmap,
                        null, // src
                        new Rect(left, top, right, bottom), // dest
                        null);
            }
            else if (mGame != null && mGame.getBoardOccupant(i) == TicTacToeGame.COMPUTER_PLAYER) {
                canvas.drawBitmap(mComputerBitmap,
                        null, // src
                        new Rect(left, top, right, bottom), // dest
                        null);
            }
        }
    }


    public void setGame(TicTacToeGame game) {
        mGame = game;
    }

    /**
     *
     *

    public int getBoardCellWidth() {
        return getWidth() / 3;
    }

    public int getBoardCellHeight() {
        return getHeight() / 3;
    }
     */
    public int getBoardCellWidth() {
        return mBoardCellWidth;
    }
    public int getBoardCellHeight() {
        return mBoardCellHeight;
    }
}
