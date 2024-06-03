package com.zxc.quizman;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "quizmaxxx.db";
    private static final int DATABASE_VERSION = 12;
    private static final String DATABASE_PATH = "/data/data/com.zxc.quizman/databases/";
    private static final String TABLE_QUESTIONS = "questions";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_QUESTION = "question";
    private static final String COLUMN_OPTION1 = "option1";
    private static final String COLUMN_OPTION2 = "option2";
    private static final String COLUMN_OPTION3 = "option3";
    private static final String COLUMN_OPTION4 = "option4";
    private static final String COLUMN_ANSWER = "answer";
    private static final String COLUMN_TOPIC = "topic";

    private static final String TABLE_RESULTS = "results";
    private static final String COLUMN_TOPIC_RESULT = "topic";
    private static final String COLUMN_CORRECT_ANSWERS = "correctAnswers";
    private static final String COLUMN_INCORRECT_ANSWERS = "incorrectAnswers";
    private static final String COLUMN_TOTAL_TIME = "totalTime";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_TOTAL_SCORE = "totalScore";

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        try {
            copyDatabase();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка копирования базы данных", e);
        }
    }

    private void copyDatabase() throws IOException {
        // Удаление старой базы данных, если она существует
        //context.deleteDatabase(DATABASE_NAME);
        if (checkDatabase()) {
            Log.d("DatabaseHelper", "База данных уже существует, копировать не нужно.");
            return;
        }

        this.getReadableDatabase();
        this.close();

        InputStream input = context.getAssets().open(DATABASE_NAME);
        String outFileName = DATABASE_PATH + DATABASE_NAME;
        OutputStream output = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = input.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }

        output.flush();
        output.close();
        input.close();
        Log.d("DatabaseHelper", "<Бд успешно скопированна>");
    }

    private boolean checkDatabase() {
        SQLiteDatabase checkDB = null;
        try {
            String databasePath = DATABASE_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READONLY);
            Log.d("DatabaseHelper", "База данных находится по адресу " + databasePath);
        } catch (SQLiteException e) {
            Log.d("DatabaseHelper", "Бд данных не найдена");
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RESULTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_RESULTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TOPIC_RESULT + " TEXT, " +
                COLUMN_CORRECT_ANSWERS + " INTEGER, " +
                COLUMN_INCORRECT_ANSWERS + " INTEGER, " +
                COLUMN_TOTAL_TIME + " TEXT, " +
                COLUMN_TOTAL_SCORE + " INTEGER, " +
                COLUMN_TIMESTAMP + " TEXT)";
        db.execSQL(CREATE_RESULTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 11) {
            db.execSQL("ALTER TABLE " + TABLE_RESULTS + " ADD COLUMN " + COLUMN_TOTAL_SCORE + " INTEGER DEFAULT 0");
        }
    }

    public List<QuestionList> getQuestionsByTopic(String topic) {
        List<QuestionList> questions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_QUESTIONS + " WHERE " + COLUMN_TOPIC + "=?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{topic});

        if (cursor.moveToFirst()) {
            do {
                QuestionList question = new QuestionList();
                question.setQuestion(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION)));
                question.setOption1(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OPTION1)));
                question.setOption2(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OPTION2)));
                question.setOption3(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OPTION3)));
                question.setOption4(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OPTION4)));
                question.setAnswer(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANSWER)));
                question.setTopic(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TOPIC)));
                questions.add(question);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return questions;
    }

    public void addResult(String topic, int correctAnswers, int incorrectAnswers, String totalTime, String timestamp, int totalScore) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TOPIC_RESULT, topic);
        values.put(COLUMN_CORRECT_ANSWERS, correctAnswers);
        values.put(COLUMN_INCORRECT_ANSWERS, incorrectAnswers);
        values.put(COLUMN_TOTAL_TIME, totalTime);
        values.put(COLUMN_TIMESTAMP, timestamp);
        values.put(COLUMN_TOTAL_SCORE, totalScore);

        db.insert(TABLE_RESULTS, null, values);
        db.close();
    }

    public Cursor getBestResultByTopic(String topic) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_RESULTS + " WHERE " + COLUMN_TOPIC_RESULT + " = ? ORDER BY " + COLUMN_TOTAL_SCORE + " DESC LIMIT 1";
        return db.rawQuery(query, new String[]{topic});
    }

    public void resetProgress(String topic) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RESULTS, COLUMN_TOPIC_RESULT + " = ?", new String[]{topic});
        db.close();
    }
}
