package com.zxc.quizman;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class QuestActivity extends AppCompatActivity {

    private static final String TAG = "QuestActivity";

    private TextView vopros;
    private TextView question;
    private TextView timerTextView;

    private AppCompatButton option1, option2, option3, option4;
    private AppCompatButton nextBtn;
    private Timer quizTimer;
    private Timer questionTimer;
    private int questionTimeInSeconds = 60; // 1 минута на вопрос
    private int elapsedTimeInSeconds = 0; // Общее время, прошедшее с начала викторины

    private List<QuestionList> questionList;
    private int currentQuestionIndex = 0;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quest);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        final ImageButton button_back = findViewById(R.id.button_back);
        timerTextView = findViewById(R.id.timer);
        final TextView selectedTopicName = findViewById(R.id.TopicName);

        vopros = findViewById(R.id.vopros);
        question = findViewById(R.id.question);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        nextBtn = findViewById(R.id.nextBtn);

        String getSelectedTopic = getIntent().getStringExtra("selectedTopic");
        Log.d(TAG, "Selected topic: " + getSelectedTopic);

        selectedTopicName.setText(getSelectedTopic);

        dbHelper = new DatabaseHelper(this);
        loadQuestionsFromDB(getSelectedTopic);

        if (questionList == null || questionList.isEmpty()) {
            Toast.makeText(this, "Нет вопросов для выбранной темы", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        startQuizTimer();
        startQuestionTimer();
        displayQuestion(currentQuestionIndex);

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quizTimer != null) {
                    quizTimer.purge();
                    quizTimer.cancel();
                }
                if (questionTimer != null) {
                    questionTimer.purge();
                    questionTimer.cancel();
                }
                startActivity(new Intent(QuestActivity.this, MainActivity.class));
                finish();
            }
        });

        View.OnClickListener optionClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetOptions();
                v.setBackgroundResource(R.drawable.back_white_strok10);
                QuestionList currentQuestion = questionList.get(currentQuestionIndex);
                if (v.getId() == R.id.option1) {
                    currentQuestion.setUserSelectedAnswer(option1.getText().toString());
                } else if (v.getId() == R.id.option2) {
                    currentQuestion.setUserSelectedAnswer(option2.getText().toString());
                } else if (v.getId() == R.id.option3) {
                    currentQuestion.setUserSelectedAnswer(option3.getText().toString());
                } else if (v.getId() == R.id.option4) {
                    currentQuestion.setUserSelectedAnswer(option4.getText().toString());
                }
            }
        };

        option1.setOnClickListener(optionClickListener);
        option2.setOnClickListener(optionClickListener);
        option3.setOnClickListener(optionClickListener);
        option4.setOnClickListener(optionClickListener);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionList.get(currentQuestionIndex).getUserSelectedAnswer() == null) {
                    Toast.makeText(QuestActivity.this, "Выберите вариант ответа", Toast.LENGTH_SHORT).show();
                } else {
                    if (currentQuestionIndex < questionList.size() - 1) {
                        currentQuestionIndex++;
                        displayQuestion(currentQuestionIndex);
                        resetQuestionTimer();
                    } else {
                        endQuiz();
                    }
                }
            }
        });
    }

    private void loadQuestionsFromDB(String topic) {
        Log.d(TAG, "Загрузка вопросов по теме: " + topic);
        questionList = dbHelper.getQuestionsByTopic(topic);
        Log.d(TAG, "Количество загруженных вопросов: " + questionList.size());
    }

    private void displayQuestion(int index) {
        if (index < questionList.size()) {
            QuestionList currentQuestion = questionList.get(index);
            vopros.setText((index + 1) + "/" + questionList.size());
            question.setText(currentQuestion.getQuestion());
            option1.setText(currentQuestion.getOption1());
            option2.setText(currentQuestion.getOption2());
            option3.setText(currentQuestion.getOption3());
            option4.setText(currentQuestion.getOption4());
            resetOptions();
        }
    }

    private void resetOptions() {
        option1.setBackgroundResource(R.drawable.round_back_white_stroke2_10);
        option2.setBackgroundResource(R.drawable.round_back_white_stroke2_10);
        option3.setBackgroundResource(R.drawable.round_back_white_stroke2_10);
        option4.setBackgroundResource(R.drawable.round_back_white_stroke2_10);
    }

    private void startQuizTimer() {
        quizTimer = new Timer();
        quizTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                elapsedTimeInSeconds++;
            }
        }, 1000, 1000);
    }

    private void startQuestionTimer() {
        questionTimer = new Timer();
        questionTimer.scheduleAtFixedRate(new TimerTask() {
            int timeLeft = questionTimeInSeconds;

            @Override
            public void run() {
                runOnUiThread(() -> {
                    if (timeLeft == 0) {
                        questionTimer.purge();
                        questionTimer.cancel();
                        showTimeoutWarning();
                    } else {
                        timeLeft--;
                        int minutes = timeLeft / 60;
                        int seconds = timeLeft % 60;
                        timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
                    }
                });
            }
        }, 1000, 1000);
    }

    private void resetQuestionTimer() {
        if (questionTimer != null) {
            questionTimer.purge();
            questionTimer.cancel();
        }
        startQuestionTimer();
    }

    private void showTimeoutWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Предупреждение")
                .setMessage("Вы не дали ответ в течение данного времени. Попробуйте еще раз.")
                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(QuestActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
        builder.create().show();
    }

    private void endQuiz() {
        if (quizTimer != null) {
            quizTimer.purge();
            quizTimer.cancel();
        }
        if (questionTimer != null) {
            questionTimer.purge();
            questionTimer.cancel();
        }

        int correctAnswers = getCorrectAnswers();
        int incorrectAnswers = getInCorrectAnswers();
        int totalQuestions = questionList.size();
        int totalScore = correctAnswers * 10; // пример расчета общего балла, 10 баллов за правильный ответ
        double averageTimePerQuestion = (double) elapsedTimeInSeconds / totalQuestions;

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        Intent intent = new Intent(QuestActivity.this, QuestResults.class);
        intent.putExtra("correct", correctAnswers);
        intent.putExtra("incorrect", incorrectAnswers);
        intent.putExtra("totalTime", getFormattedElapsedTime());
        intent.putExtra("totalScore", totalScore);
        intent.putExtra("averageTime", averageTimePerQuestion);
        intent.putExtra("selectedTopic", getIntent().getStringExtra("selectedTopic"));
        intent.putExtra("timestamp", timestamp);
        startActivity(intent);
        finish();

        dbHelper.addResult(getIntent().getStringExtra("selectedTopic"), correctAnswers, incorrectAnswers, getFormattedElapsedTime(), timestamp, totalScore);
    }


    private int getCorrectAnswers() {
        int correctAnswers = 0;
        for (QuestionList q : questionList) {
            if (q.getUserSelectedAnswer() != null && q.getUserSelectedAnswer().equals(q.getAnswer())) {
                correctAnswers++;
            }
        }
        return correctAnswers;
    }

    private int getInCorrectAnswers() {
        int incorrectAnswers = 0;
        for (QuestionList q : questionList) {
            if (q.getUserSelectedAnswer() != null && !q.getUserSelectedAnswer().equals(q.getAnswer())) {
                incorrectAnswers++;
            }
        }
        return incorrectAnswers;
    }

    private String getFormattedElapsedTime() {
        int minutes = elapsedTimeInSeconds / 60;
        int seconds = elapsedTimeInSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
