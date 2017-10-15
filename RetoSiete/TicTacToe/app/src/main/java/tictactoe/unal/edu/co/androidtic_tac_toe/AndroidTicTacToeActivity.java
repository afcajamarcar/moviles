package tictactoe.unal.edu.co.androidtic_tac_toe;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class AndroidTicTacToeActivity extends AppCompatActivity {
    private static final String TAG = "AndroidTicTacToe";

    // for preferences
    static final int DIALOG_QUIT_ID = 1;
    static final int DIALOG_ABOUT_ID = 2;
    static final int DIALOG_RESET_ID = 3;
    private SharedPreferences mPrefs;
    private int mDiffLev;

    // Whose turn is it?
    private char mTurn;

    // Who starts the next game?
    private char mGoesFirst;

    // for pausing game
    private Handler mPauseHandler;
    private Runnable myRunnable;

    // Keep track of wins
    private int mHumanWins = 0;
    private int mComputerWins = 0;
    private int mTies = 0;

    // game logic
    private TicTacToeGame mGame;

    // Various text displayed
    private TextView mInfoTextView;
    private TextView mHumanScoreTextView;
    private TextView mComputerScoreTextView;
    private TextView mTieScoreTextView;

    private boolean mGameOver;
    private BoardView mBoardView;

    private MediaPlayer mHumanMediaPlayer;
    private MediaPlayer mComputerMediaPlayer;
    private MediaPlayer mLoseMediaPlayer;
    private MediaPlayer mWinMediaPlayer;
    private MediaPlayer mTieMediaPlayer;

    //Variable to take notice of the usage of the sound
    private Boolean mSoundOn;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_tic_tac_toe);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        // Restore the scores
        mHumanWins = mPrefs.getInt("mHumanWins", 0);
        mComputerWins = mPrefs.getInt("mComputerWins", 0);
        mTies = mPrefs.getInt("mTies", 0);
        mDiffLev = mPrefs.getInt("mDiffLev", 2);


        mGame = new TicTacToeGame();
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.values()[mDiffLev]);

        // Restore the scores from the persistent preference data source

        mSoundOn = mPrefs.getBoolean("sound", true);
        String difficultyLevel = mPrefs.getString("difficulty_level",
                getResources().getString(R.string.difficulty_harder));
        if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
        else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
        else
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);


        // Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);

        // get the TextViews
        mInfoTextView = (TextView) findViewById(R.id.information);
        mHumanScoreTextView = (TextView) findViewById(R.id.player_score);
        mComputerScoreTextView = (TextView) findViewById(R.id.computer_score);
        mTieScoreTextView = (TextView) findViewById(R.id.tie_score);

        mTurn = TicTacToeGame.HUMAN_PLAYER;
        mGoesFirst = TicTacToeGame.COMPUTER_PLAYER; // computer goes fist next game
        mPauseHandler = new Handler();

        if (savedInstanceState == null) {
            mTurn = TicTacToeGame.HUMAN_PLAYER;
            mGoesFirst = TicTacToeGame.COMPUTER_PLAYER; // computer goes fist next game
            startNewGame(true);
        }
        else{
            mGame.setBoardState(savedInstanceState.getCharArray("board"));
            mGameOver = savedInstanceState.getBoolean("mGameOver");
            mTurn = savedInstanceState.getChar("mTurn");
            mGoesFirst = savedInstanceState.getChar("mGoesFirst");
            mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
//			mHumanWins = savedInstanceState.getInt("mHumanWins");
//			mComputerWins = savedInstanceState.getInt("mComputerWins");
//			mTies = savedInstanceState.getInt("mTies");

            startComputerDelay();
        }
        displayScores();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.mblood);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.mchain);
        mLoseMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.mlose);
        mTieMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.mtie);
        mWinMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.mwin);
    }


    @Override
    protected void onStop() {
        super.onStop();

        // Save the current scores
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("mHumanWins", mHumanWins);
        ed.putInt("mComputerWins", mComputerWins);
        ed.putInt("mTies", mTies);
        ed.putInt("mDiffLev", mDiffLev);
        ed.apply();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharArray("board", mGame.getBoardState());
        outState.putBoolean("mGameOver", mGameOver);
        outState.putCharSequence("info", mInfoTextView.getText());
        outState.putChar("mTurn", mTurn);
        outState.putChar("mGoesFirst", mGoesFirst);
    }


    private void displayScores() {
        mHumanScoreTextView.setText(Integer.toString(mHumanWins));
        mComputerScoreTextView.setText(Integer.toString(mComputerWins));
        mTieScoreTextView.setText(Integer.toString(mTies));
    }


    private void startComputerDelay() {
        // If it's the computer's turn, the previous turn was not completed, so go again
        if (!mGameOver && mTurn == TicTacToeGame.COMPUTER_PLAYER) {
            int move = mGame.getComputerMove();
            setMove(TicTacToeGame.COMPUTER_PLAYER, move);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
        mLoseMediaPlayer.release();
        mTieMediaPlayer.release();
        mWinMediaPlayer.release();

    }


    // Set up the game baord.
    private void startNewGame(boolean first) {
        // check if new game after a complete game and if so swap who goes first
        if(mGameOver) {
            mTurn = mGoesFirst;
            mGoesFirst = (mGoesFirst == TicTacToeGame.COMPUTER_PLAYER) ?
                    TicTacToeGame.HUMAN_PLAYER : TicTacToeGame.COMPUTER_PLAYER;
        }
        // if human quit when it was their turn then Android gets to go first
        else if(mTurn == TicTacToeGame.HUMAN_PLAYER && !first) {
            mTurn = TicTacToeGame.COMPUTER_PLAYER;
            mGoesFirst = TicTacToeGame.HUMAN_PLAYER;
        }
        mGameOver = false;

        mGame.clearBoard();
        mBoardView.invalidate();   // Redraw the board

        // Who starts?
        if (mTurn == TicTacToeGame.COMPUTER_PLAYER) {
            Log.d(TAG, "Computers turn!!!");
            mInfoTextView.setText(R.string.turn_computer);
            int move = mGame.getComputerMove();
            setMove(TicTacToeGame.COMPUTER_PLAYER, move);
        }
        else {
            mInfoTextView.setText(R.string.first_human);
        }
    }


    // Make a move
    private boolean setMove(char player, int location) {

        if (player == TicTacToeGame.COMPUTER_PLAYER) {
            // EXTRA CHALLENGE!
            // Make the computer move after a delay of 1 second
            myRunnable = createRunnable(location);
            mPauseHandler.postDelayed(myRunnable, 1000);
            return true;
        }
        else if (mGame.setMove(TicTacToeGame.HUMAN_PLAYER, location)) {
            mTurn = TicTacToeGame.COMPUTER_PLAYER;
            mBoardView.invalidate();   // Redraw the board
            if(mSoundOn) mHumanMediaPlayer.start();
            return true;
        }
        // should never get here
        return false;
    }


    private Runnable createRunnable(final int location) {
        return new Runnable() {
            public void run() {

                mGame.setMove(TicTacToeGame.COMPUTER_PLAYER, location);
                // soundID, leftVolume, rightVolume, priority, loop, rate
                if(mSoundOn) mComputerMediaPlayer.start();

                mBoardView.invalidate();   // Redraw the board

                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mTurn = TicTacToeGame.HUMAN_PLAYER;
                    mInfoTextView.setText(R.string.turn_human);
                }
                else
                    endGame(winner);
            }
        };
    }

    // Game is over logic
    private void endGame(int winner) {
        if (winner == 1) {
            mTies++;
            mTieScoreTextView.setText(Integer.toString(mTies));
            mInfoTextView.setText(R.string.result_tie);
            if(mSoundOn) mTieMediaPlayer.start();
        }
        else if (winner == 2) {
            mHumanWins++;
            mHumanScoreTextView.setText(Integer.toString(mHumanWins));
            String defaultMessage = getResources().getString(R.string.result_human_wins);
            mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
            if(mSoundOn) mWinMediaPlayer.start();
        }
        else {
            mComputerWins++;
            mComputerScoreTextView.setText(Integer.toString(mComputerWins));
            mInfoTextView.setText(R.string.result_computer_wins);
            if(mSoundOn) mLoseMediaPlayer.start();
        }
        mGameOver = true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        Log.d(TAG, "in onOptionsItemSelected selecting");
        switch (item.getItemId()) {
            case R.id.new_game:
                if(myRunnable != null);
                mPauseHandler.removeCallbacks(myRunnable);
                // if computer is in pause, stop it.
                startNewGame(false);
                return true;
            case R.id.settings:
                startActivityForResult(new Intent(this, Settings.class), 0);
                return true;
            case R.id.about_game:
                showDialog(DIALOG_ABOUT_ID);
                return true;
            case R.id.quit:
                showDialog(DIALOG_QUIT_ID);
                return true;
            case R.id.reset_scores:
                showDialog(DIALOG_RESET_ID);
                return true;
        }
        return false;
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch(id) {

            case DIALOG_QUIT_ID:
                // Create the quit confirmation dialog

                builder.setMessage(R.string.quit_question).setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AndroidTicTacToeActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();
                break;
            case DIALOG_ABOUT_ID:
                LayoutInflater inflater=getLayoutInflater();
                View layout = inflater.inflate(R.layout.about_dialog, null);
                builder.setView(layout);
                builder.setPositiveButton("OK", null);
                dialog = builder.create();
                break;
            case DIALOG_RESET_ID:
                mHumanWins = 0;
                mComputerWins = 0;
                mTies = 0;
                displayScores();
                break;
        }

        if(dialog == null)
            Log.d(TAG, "Uh oh! Dialog is null");
        else
            Log.d(TAG, "Dialog created: " + id + ", dialog: " + dialog);
        return dialog;
    }

    // Listen for touches on the board
    private OnTouchListener mTouchListener = new OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {

            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;

            if (!mGameOver && mTurn == TicTacToeGame.HUMAN_PLAYER &&
                    setMove(TicTacToeGame.HUMAN_PLAYER, pos))	{

                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                }
                else
                    endGame(winner);

            }

            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CANCELED) {
            // Apply potentially new settings

            mSoundOn = mPrefs.getBoolean("sound", true);

            String difficultyLevel = mPrefs.getString("difficulty_level",
                    getResources().getString(R.string.difficulty_harder));
            if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
            else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
            else
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
        }
    }
}
