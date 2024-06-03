package com.zxc.quizman;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private String selectedTopic = "";
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);

        final CardView moscow = findViewById(R.id.moscow_container);
        final CardView ufa = findViewById(R.id.ufa_container);
        final CardView str = findViewById(R.id.sterlitamak_container);
        final CardView salavat = findViewById(R.id.salavat_container);
        final CardView kazan = findViewById(R.id.kazan_container);
        final CardView novosibirsk = findViewById(R.id.novosibirsk_container);
        final CardView yekaterinburg = findViewById(R.id.yekaterinburg_container);
        final CardView samara = findViewById(R.id.samara_container);
        final CardView omsk = findViewById(R.id.omsk_container);

        final Button startQest = findViewById(R.id.startQest);
        final Button lastResult = findViewById(R.id.lastResult);

        moscow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTopic = "Москва";
                moscow.setBackgroundResource(R.drawable.back_white_strok10);
                resetOtherCardBackgrounds(new CardView[]{ufa, str, salavat, kazan, novosibirsk, yekaterinburg, samara, omsk});
                updateProgress(selectedTopic, findViewById(R.id.moscow_progress));
            }
        });

        ufa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTopic = "Уфа";
                ufa.setBackgroundResource(R.drawable.back_white_strok10);
                resetOtherCardBackgrounds(new CardView[]{moscow, str, salavat, kazan, novosibirsk, yekaterinburg, samara, omsk});
                updateProgress(selectedTopic, findViewById(R.id.ufa_progress));
            }
        });

        str.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTopic = "Стерлитамак";
                str.setBackgroundResource(R.drawable.back_white_strok10);
                resetOtherCardBackgrounds(new CardView[]{moscow, ufa, salavat, kazan, novosibirsk, yekaterinburg, samara, omsk});
                updateProgress(selectedTopic, findViewById(R.id.sterlitamak_progress));
            }
        });

        salavat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTopic = "Салават";
                salavat.setBackgroundResource(R.drawable.back_white_strok10);
                resetOtherCardBackgrounds(new CardView[]{moscow, ufa, str, kazan, novosibirsk, yekaterinburg, samara, omsk});
                updateProgress(selectedTopic, findViewById(R.id.salavat_progress));
            }
        });

        kazan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTopic = "Казань";
                kazan.setBackgroundResource(R.drawable.back_white_strok10);
                resetOtherCardBackgrounds(new CardView[]{moscow, ufa, str, salavat, novosibirsk, yekaterinburg, samara, omsk});
                updateProgress(selectedTopic, findViewById(R.id.kazan_progress));
            }
        });

        novosibirsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTopic = "Новосибирск";
                novosibirsk.setBackgroundResource(R.drawable.back_white_strok10);
                resetOtherCardBackgrounds(new CardView[]{moscow, ufa, str, salavat, kazan, yekaterinburg, samara, omsk});
                updateProgress(selectedTopic, findViewById(R.id.novosibirsk_progress));
            }
        });

        yekaterinburg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTopic = "Екатеринбург";
                yekaterinburg.setBackgroundResource(R.drawable.back_white_strok10);
                resetOtherCardBackgrounds(new CardView[]{moscow, ufa, str, salavat, kazan, novosibirsk, samara, omsk});
                updateProgress(selectedTopic, findViewById(R.id.yekaterinburg_progress));
            }
        });

        samara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTopic = "Самара";
                samara.setBackgroundResource(R.drawable.back_white_strok10);
                resetOtherCardBackgrounds(new CardView[]{moscow, ufa, str, salavat, kazan, novosibirsk, yekaterinburg, omsk});
                updateProgress(selectedTopic, findViewById(R.id.samara_progress));
            }
        });

        omsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTopic = "Омск";
                omsk.setBackgroundResource(R.drawable.back_white_strok10);
                resetOtherCardBackgrounds(new CardView[]{moscow, ufa, str, salavat, kazan, novosibirsk, yekaterinburg, samara});
                updateProgress(selectedTopic, findViewById(R.id.omsk_progress));
            }
        });

        startQest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTopic.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Выберите викторину", Toast.LENGTH_SHORT).show();
                } else {
                    List<QuestionList> questions = dbHelper.getQuestionsByTopic(selectedTopic);
                    if (questions != null && !questions.isEmpty()) {
                        Log.d(TAG, "Starting quiz with topic: " + selectedTopic);
                        Intent intent = new Intent(MainActivity.this, QuestActivity.class);
                        intent.putExtra("selectedTopic", selectedTopic);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Нет вопросов для выбранной темы", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        lastResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTopic.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Выберите викторину", Toast.LENGTH_SHORT).show();
                } else {
                    showLastResultDialog(selectedTopic);
                }
            }
        });

        // Инициализация прогресса для всех городов
        updateProgress("Москва", findViewById(R.id.moscow_progress));
        updateProgress("Уфа", findViewById(R.id.ufa_progress));
        updateProgress("Стерлитамак", findViewById(R.id.sterlitamak_progress));
        updateProgress("Салават", findViewById(R.id.salavat_progress));
        updateProgress("Казань", findViewById(R.id.kazan_progress));
        updateProgress("Новосибирск", findViewById(R.id.novosibirsk_progress));
        updateProgress("Екатеринбург", findViewById(R.id.yekaterinburg_progress));
        updateProgress("Самара", findViewById(R.id.samara_progress));
        updateProgress("Омск", findViewById(R.id.omsk_progress));
    }

    private void resetOtherCardBackgrounds(CardView[] otherCards) {
        for (CardView card : otherCards) {
            card.setBackgroundResource(R.drawable.round_back_white10);
        }
    }

    private void updateProgress(String topic, TextView progressView) {
        Cursor cursor = dbHelper.getBestResultByTopic(topic);
        if (cursor != null && cursor.moveToFirst()) {
            int totalScore = cursor.getInt(cursor.getColumnIndexOrThrow("totalScore"));
            int percentage = totalScore; // Процент прогресса на основе общего балла

            progressView.setText("Прогресс: " + percentage + "%");
            if (percentage < 50) {
                progressView.setTextColor(getResources().getColor(R.color.red));
            } else if (percentage == 50) {
                progressView.setTextColor(Color.parseColor("#FFA500")); // Оранжевый цвет
            } else {
                progressView.setTextColor(Color.parseColor("#32CD32")); // Новый зеленый цвет
            }
        } else {
            progressView.setText("Прогресс: 0%");
            progressView.setTextColor(getResources().getColor(R.color.black));
        }
    }

    private void showLastResultDialog(String topic) {
        Cursor cursor = dbHelper.getBestResultByTopic(topic);
        if (cursor != null && cursor.moveToFirst()) {
            String selectedTopic = cursor.getString(cursor.getColumnIndexOrThrow("topic"));
            int correctAnswers = cursor.getInt(cursor.getColumnIndexOrThrow("correctAnswers"));
            int incorrectAnswers = cursor.getInt(cursor.getColumnIndexOrThrow("incorrectAnswers"));
            String totalTime = cursor.getString(cursor.getColumnIndexOrThrow("totalTime"));
            String timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));

            String message = "Викторина: " + selectedTopic + "\n" +
                    "Верных ответов: " + correctAnswers + "\n" +
                    "Неверных ответов: " + incorrectAnswers + "\n" +
                    "Общее время: " + totalTime + "\n" +
                    "Дата и время: " + timestamp;

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Последний результат")
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .setNegativeButton("Сбросить прогресс", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dbHelper.resetProgress(topic);
                            Toast.makeText(MainActivity.this, "Прогресс сброшен", Toast.LENGTH_SHORT).show();
                            updateProgress(topic, findViewById(getProgressViewIdByTopic(topic))); // обновление прогресса после сброса
                        }
                    });
            builder.create().show();
        } else {
            Toast.makeText(MainActivity.this, "Последний результат отсутствует", Toast.LENGTH_SHORT).show();
        }
    }

    private int getProgressViewIdByTopic(String topic) {
        switch (topic) {
            case "Москва":
                return R.id.moscow_progress;
            case "Уфа":
                return R.id.ufa_progress;
            case "Стерлитамак":
                return R.id.sterlitamak_progress;
            case "Салават":
                return R.id.salavat_progress;
            case "Казань":
                return R.id.kazan_progress;
            case "Новосибирск":
                return R.id.novosibirsk_progress;
            case "Екатеринбург":
                return R.id.yekaterinburg_progress;
            case "Самара":
                return R.id.samara_progress;
            case "Омск":
                return R.id.omsk_progress;
            default:
                return R.id.moscow_progress; // Значение по умолчанию, если тема не найдена
        }
    }
}
