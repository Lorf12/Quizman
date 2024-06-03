package com.zxc.quizman;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class QuestResults extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int correctAnswers = getIntent().getIntExtra("correct", 0);
        int incorrectAnswers = getIntent().getIntExtra("incorrect", 0);
        String totalTime = getIntent().getStringExtra("totalTime");
        String selectedTopic = getIntent().getStringExtra("selectedTopic");
        int totalScore = getIntent().getIntExtra("totalScore", 0);
        double averageTime = getIntent().getDoubleExtra("averageTime", 0);
        String timestamp = getIntent().getStringExtra("timestamp");

        TextView resultTextView = findViewById(R.id.resultTitle);
        TextView quizNameTextView = findViewById(R.id.quizName);
        TextView totalScoreTextView = findViewById(R.id.totalScore);
        TextView correctAnswersTextView = findViewById(R.id.correctAnswers);
        TextView incorrectAnswersTextView = findViewById(R.id.incorrectAnswers);
        TextView averageTimeTextView = findViewById(R.id.averageTime);
        TextView totalTimeTextView = findViewById(R.id.totalTime);
        TextView resultTimestampTextView = findViewById(R.id.resultTimestamp);
        Button backToQuizSelection = findViewById(R.id.backToQuizSelection);
        Button retryQuiz = findViewById(R.id.retryQuiz);

        resultTextView.setText("Результат:");
        quizNameTextView.setText("Викторина: " + selectedTopic);
        totalScoreTextView.setText("Общий балл: " + totalScore);
        correctAnswersTextView.setText("Количество верных ответов: " + correctAnswers);
        incorrectAnswersTextView.setText("Количество неверных ответов: " + incorrectAnswers);
        averageTimeTextView.setText("Среднее время ответа: " + String.format("%.2f сек.", averageTime));
        totalTimeTextView.setText("Общее время прохождения: " + totalTime);
        resultTimestampTextView.setText("Дата и время: " + timestamp);

        correctAnswersTextView.setTextColor(getResources().getColor(R.color.green));
        incorrectAnswersTextView.setTextColor(getResources().getColor(R.color.red));

        backToQuizSelection.setOnClickListener(v -> {
            Intent intent = new Intent(QuestResults.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        retryQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(QuestResults.this, QuestActivity.class);
            intent.putExtra("selectedTopic", selectedTopic);
            startActivity(intent);
            finish();
        });
    }
}
