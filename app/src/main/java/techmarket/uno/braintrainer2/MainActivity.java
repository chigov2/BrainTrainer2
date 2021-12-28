package techmarket.uno.braintrainer2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private TextView textViewScore;
    private TextView textViewQuestion;
    private TextView textViewTimer;

    private ArrayList<TextView> options = new ArrayList<>();

    private String question;
    private int rightAnswer;
    private int rightAnswerPosition;
    private boolean isPositive;
    private int min = 5;
    private int max = 30;
    private int countOfQuestions =0;
    private int countOfRightAnswers =0;

    private boolean gameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        textViewScore = findViewById(R.id.textViewScore);
        textViewQuestion = findViewById(R.id.textViewQuestion);
        textViewTimer = findViewById(R.id.textViewTimer);
        options.add(textView1);
        options.add(textView2);
        options.add(textView3);
        options.add(textView4);
        playNext();
        CountDownTimer timer = new CountDownTimer(10000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textViewTimer.setText(getTime(millisUntilFinished));
                if(millisUntilFinished < 6000){
                    textViewTimer.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }
            }

            @Override
            public void onFinish() {
                gameOver = true;
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                int max = preferences.getInt("max", 0);
                if(countOfRightAnswers >=max){
                    preferences.edit().putInt("max",countOfRightAnswers).apply();
                }
                Intent intent = new Intent(MainActivity.this,ScoreActivity.class);
                intent.putExtra("result",countOfRightAnswers);
                startActivity(intent);
            }
        };
        timer.start();
    }
    private void generateQuestion(){
        int a = (int) (Math.random() * (max - min + 1) + min);
        int b = (int) (Math.random() * (max - min + 1) + min);
        int mark = (int) (Math.random()*2);
        isPositive = mark == 1;
        if (isPositive){
            rightAnswer = a+b;
            question = String.format("%s + %s",a,b);
        }else{rightAnswer = a-b;
            question = String.format("%s - %s",a,b);
        }
        rightAnswerPosition = (int) (Math.random()*4);
        textViewQuestion.setText(question);
    }

    private int generateWrongAnswer(){
        int result;
        do {
            result = (int) (Math.random()*max*2) - (max-min);
        } while (result == rightAnswer);
        return result;
    }

    private void playNext(){
        generateQuestion();
        for (int i =0; i < options.size();i++){
            if (i == rightAnswerPosition){
                options.get(i).setText(Integer.toString(rightAnswer));
            }else{
                options.get(i).setText(Integer.toString(generateWrongAnswer()));
            }
        }
        String score = String.format("%s / %s",countOfRightAnswers,countOfQuestions);
        textViewScore.setText(score);
    }

    public void onClickAnswer(View view) {
        if (!gameOver){
        TextView textView = (TextView) view;
        String answer = (String) textView.getText();
        int chosenAnswer = Integer.parseInt(answer);
        if (chosenAnswer == rightAnswer){
            countOfRightAnswers++;
            Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Not correct", Toast.LENGTH_SHORT).show();
        }
        countOfQuestions++;
        playNext();

        }
    }

    private String getTime(long millis){
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format(Locale.getDefault(),"%02d : %02d",minutes,seconds);
    }
}


