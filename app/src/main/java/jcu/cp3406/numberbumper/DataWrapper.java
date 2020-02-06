package jcu.cp3406.numberbumper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.Stack;

class DataWrapper extends SQLiteOpenHelper {

    private static final String FILE_NAME = "NumBum.db";

    final class ExerciseTable implements BaseColumns {
        static final String NAME = "exercise";
        static final String COL_LEVEL = "level";
        static final String COL_DIFFICULTY = "difficulty";
        static final String COL_PERCENTAGE = "percentage";
    }

    DataWrapper(Context context) {
        super(context, FILE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
            "CREATE TABLE " + ExerciseTable.NAME + " " +
            "(" +
                ExerciseTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +
                "," +
                ExerciseTable.COL_LEVEL + " INTEGER" +
                "," +
                ExerciseTable.COL_DIFFICULTY + " INTEGER" +
                "," +
                ExerciseTable.COL_PERCENTAGE + " INTEGER" +
            ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ExerciseTable.NAME);
        onCreate(db);
    }

    void insertScore(int level, int difficulty, int percentage) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ExerciseTable.COL_LEVEL, level);
        values.put(ExerciseTable.COL_DIFFICULTY, difficulty);
        values.put(ExerciseTable.COL_PERCENTAGE, percentage);
        db.insert(ExerciseTable.NAME, null, values);
    }

    Stack<Integer> selectScore(int level, int difficulty) {
        SQLiteDatabase db = getReadableDatabase();
        Stack<Integer> scoreArrayList = new Stack<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    "SELECT * FROM " + ExerciseTable.NAME + " " +
                    "WHERE " + ExerciseTable.COL_LEVEL + " = " + level + " " +
                    "AND " + ExerciseTable.COL_DIFFICULTY + " = " + difficulty, null);
            if (cursor.moveToFirst()) {
                do {
                    scoreArrayList.add(cursor.getInt(cursor.getColumnIndex(ExerciseTable.COL_PERCENTAGE)));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return scoreArrayList;
    }
}
