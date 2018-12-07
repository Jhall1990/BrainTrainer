package com.example.jhall.braintrainer;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    class GameQuestion {
        /**
         * Class for brain trainer game questions.
         *
         * Each GameQuestion object has the following attributes.
         *
         * (int) correctAnswer - This is the sum of the generated first and second int.
         * (int) firstInt - The first int to be displayed in the question presented to the user.
         * (int) secondInt - The second int.
         * (int) answerCell - Which cell (0-4) to display the correct answer in.
         * (Stack<int>) otherAnswers - 3 other randomly generated answers to present to the user.
         */
        int correctAnswer;
        int firstInt;
        int secondInt;
        int answerCell;
        Stack<Integer> otherAnswers;

        private Random random = new Random();
        private int min = 1;
        private int max = 20;

        GameQuestion() {
            this.firstInt = this.getIntInRange();
            this.secondInt = this.getIntInRange();
            this.correctAnswer = this.firstInt + this.secondInt;
            this.answerCell = this.random.nextInt(4);
            this.otherAnswers = new Stack<>();

            for (int i = 0; i < 3; i++) {
                this.otherAnswers.push(this.getIntInRange());
            }
        }

        private int getIntInRange() {
            // Returns an integer within range this.min and this.max.
            return this.random.nextInt(this.max - this.min + 1) + this.min;
        }
    }

    int gameTime = 30;
    int totalCount = 0;
    int correctCount = 0;
    boolean gameRunning;
    GameQuestion currentQuestion;
    ArrayList<TextView> gameViews = new ArrayList<>();
    SparseIntArray answerMap = new SparseIntArray();
    TextView goTextView;
    TextView gameMsgView;
    TextView scoreView;
    TextView problemView;
    Button playAgainButton;

    public void start(View view) {
        // Set the go text view to gone and start the game.
        this.goTextView.setVisibility(View.GONE);
        this.startGame();
    }

    public void playAgain(View view) {
        // First reset the score and total counts to 0. Then remove the game msg
        // text. Make the play again button invisible and finally start the game.
        this.correctCount = 0;
        this.totalCount = 0;
        this.gameMsgView.setText("");
        this.playAgainButton.setVisibility(View.INVISIBLE);
        this.startGame();
    }

    public void answer(View view) {
        // Check if the game is running first, if it's not, no need to check the answer/
        if (this.gameRunning) {
            // Get the answer data from the passed view's tag.
            int answer = (int) view.getTag();

            totalCount++;

            // Check the pressed answer if it is equal to the current questions answer
            // increase the correct score and change the msg text to "Correct". Otherwise
            // set the text to "Incorrect" and don't increase the score.
            if (answer == this.currentQuestion.correctAnswer) {
                this.correctCount++;
                this.gameMsgView.setText("Correct!");
            } else {
                this.gameMsgView.setText("Incorrect!");
            }

            // Display the updated score and get a new question.
            this.updateScore();
            this.getQuestion();
        }
    }

    private void updateScore() {
        // Sets the current score text according to the number of
        // questions the user has answered correctly over the total
        // number asked so far.
        String scoreText = this.correctCount + "/" + this.totalCount;
        this.scoreView.setText(scoreText);
    }

    private void startGame() {
        // Show all the needed game views and start the game timer. Then generate
        // and display a new question for the user. Update the score display and make
        // the game msg text view visible.
        this.showGameViews();
        this.startTimer();
        this.getQuestion();
        this.updateScore();
        this.gameMsgView.setVisibility(View.VISIBLE);
    }

    private void getQuestion() {
        // Generate a new game question for the user and display it. Also set
        // the currentQuestion to the newly generated GameQuestion.
        GameQuestion question = new GameQuestion();
        this.currentQuestion = question;
        this.displayQuestion(question);
    }

    private void displayQuestion(GameQuestion question) {
        // Iterate over the size of answerMap. For each cell if the current cell
        // is equal to the answerCell add the answer to it, otherwise add one of the
        // other answers to it.
        for (int i = 0; i < this.answerMap.size(); i++) {
            if (i == question.answerCell) {
                TextView answerCell = findViewById(this.answerMap.get(i));
                answerCell.setText(Integer.toString(question.correctAnswer));
                answerCell.setTag(question.correctAnswer);
            } else {
                TextView answerCell = findViewById(this.answerMap.get(i));
                int answer = question.otherAnswers.pop();
                answerCell.setText(Integer.toString(answer));
                answerCell.setTag(answer);
            }
        }

        // Update the problem text view with the current problem.
        String answerText = question.firstInt + " + " + question.secondInt;
        this.problemView.setText(answerText);
    }

    private void startTimer() {
        // Get the timer text view and update the game running boolean.
        final TextView timerView = findViewById(R.id.timerView);
        this.gameRunning = true;

        // Create a new count down timer that lasts <gameTime> seconds and ticks every second.
        new CountDownTimer(this.gameTime * 1000 + 100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the text in the timer text view with current amount of
                // time remaining.
                String timerText = ((int) millisUntilFinished / 1000) + "s";
                timerView.setText(timerText);
            }

            @Override
            public void onFinish() {
                // Set the timer view text to 0s, show post game buttons/text and
                // update the game running flag.
                timerView.setText("0s");
                MainActivity.this.showPostGame();
                MainActivity.this.gameRunning = false;
            }
        }.start();
    }

    private void showPostGame() {
        // Show the play again button and change the game msg
        // to Done!.
        this.playAgainButton.setVisibility(View.VISIBLE);
        this.gameMsgView.setText("DONE!");
    }

    private void showGameViews() {
        // Iterates over each text view used by the game and set's it to visible.
        for (TextView textView : this.gameViews) {
            textView.setVisibility(View.VISIBLE);
        }
    }

    private void populateGameViews() {
        // Get all the text views needed for the game UI.
        this.gameViews.add((TextView) findViewById(R.id.timerView));
        this.gameViews.add((TextView) findViewById(R.id.problemView));
        this.gameViews.add((TextView) findViewById(R.id.scoreView));
        this.gameViews.add((TextView) findViewById(R.id.answerView1));
        this.gameViews.add((TextView) findViewById(R.id.answerView2));
        this.gameViews.add((TextView) findViewById(R.id.answerView3));
        this.gameViews.add((TextView) findViewById(R.id.answerView4));
    }

    private void createAnswerMap() {
        // Create a map of index to answerView id.
        this.answerMap.put(0, R.id.answerView1);
        this.answerMap.put(1, R.id.answerView2);
        this.answerMap.put(2, R.id.answerView3);
        this.answerMap.put(3, R.id.answerView4);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.populateGameViews();
        this.createAnswerMap();
        this.goTextView = findViewById(R.id.goTextView);
        this.playAgainButton = findViewById(R.id.playAgainButton);
        this.gameMsgView = findViewById(R.id.gameMsgView);
        this.scoreView = findViewById(R.id.scoreView);
        this.problemView = findViewById(R.id.problemView);
    }
}
