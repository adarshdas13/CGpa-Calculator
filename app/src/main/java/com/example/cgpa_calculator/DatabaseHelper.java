package com.example.cgpa_calculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "gpa_calculator.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_SEMESTERS = "semesters";
    private static final String TABLE_SUBJECTS = "subjects";

    // Semesters Table Columns
    private static final String KEY_SEM_ID = "id";
    private static final String KEY_SEM_NAME = "semester_name";
    private static final String KEY_SEM_GPA = "gpa";
    private static final String KEY_SEM_TOTAL_CREDITS = "total_credits";
    private static final String KEY_SEM_TIMESTAMP = "timestamp";

    // Subjects Table Columns
    private static final String KEY_SUB_ID = "id";
    private static final String KEY_SUB_SEM_ID = "semester_id";
    private static final String KEY_SUB_NAME = "subject_name";
    private static final String KEY_SUB_CREDITS = "credits";
    private static final String KEY_SUB_GRADE = "grade";
    private static final String KEY_SUB_GRADE_POINT = "grade_point";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SEMESTERS_TABLE = "CREATE TABLE " + TABLE_SEMESTERS + "("
                + KEY_SEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_SEM_NAME + " TEXT,"
                + KEY_SEM_GPA + " REAL,"
                + KEY_SEM_TOTAL_CREDITS + " REAL,"
                + KEY_SEM_TIMESTAMP + " INTEGER" + ")";

        String CREATE_SUBJECTS_TABLE = "CREATE TABLE " + TABLE_SUBJECTS + "("
                + KEY_SUB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_SUB_SEM_ID + " INTEGER,"
                + KEY_SUB_NAME + " TEXT,"
                + KEY_SUB_CREDITS + " REAL,"
                + KEY_SUB_GRADE + " TEXT,"
                + KEY_SUB_GRADE_POINT + " REAL,"
                + "FOREIGN KEY(" + KEY_SUB_SEM_ID + ") REFERENCES " + TABLE_SEMESTERS + "(" + KEY_SEM_ID + ") ON DELETE CASCADE" + ")";

        db.execSQL(CREATE_SEMESTERS_TABLE);
        db.execSQL(CREATE_SUBJECTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEMESTERS);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // --- CRUD OPERATIONS ---

    public long saveSemester(Semester semester) {
        SQLiteDatabase db = this.getWritableDatabase();
        long semesterId = -1;

        db.beginTransaction();
        try {
            ContentValues semValues = new ContentValues();
            semValues.put(KEY_SEM_NAME, semester.getSemesterName());
            semValues.put(KEY_SEM_GPA, semester.getGpa());
            semValues.put(KEY_SEM_TOTAL_CREDITS, semester.getTotalCredits());
            semValues.put(KEY_SEM_TIMESTAMP, semester.getTimestamp());

            semesterId = db.insert(TABLE_SEMESTERS, null, semValues);

            if (semesterId != -1 && semester.getSubjectList() != null) {
                for (Subject subject : semester.getSubjectList()) {
                    ContentValues subValues = new ContentValues();
                    subValues.put(KEY_SUB_SEM_ID, semesterId);
                    subValues.put(KEY_SUB_NAME, subject.getSubjectName());
                    subValues.put(KEY_SUB_CREDITS, subject.getCredits());
                    subValues.put(KEY_SUB_GRADE, subject.getGrade());
                    subValues.put(KEY_SUB_GRADE_POINT, subject.getGradePoint());
                    db.insert(TABLE_SUBJECTS, null, subValues);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return semesterId;
    }

    public List<Semester> getAllSemesters() {
        List<Semester> semesterList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_SEMESTERS + " ORDER BY " + KEY_SEM_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_SEM_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_SEM_NAME));
                double gpa = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_SEM_GPA));
                double credits = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_SEM_TOTAL_CREDITS));
                long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_SEM_TIMESTAMP));

                List<Subject> subjects = getSubjectsForSemester(id);

                Semester semester = new Semester(id, name, gpa, credits, timestamp, subjects);
                semesterList.add(semester);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return semesterList;
    }

    public List<Subject> getSubjectsForSemester(long semesterId) {
        List<Subject> subjects = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SUBJECTS, null, KEY_SUB_SEM_ID + "=?",
                new String[]{String.valueOf(semesterId)}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_SUB_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_SUB_NAME));
                double credits = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_SUB_CREDITS));
                String grade = cursor.getString(cursor.getColumnIndexOrThrow(KEY_SUB_GRADE));
                double gradePoint = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_SUB_GRADE_POINT));

                Subject subject = new Subject(id, semesterId, name, credits, grade, gradePoint);
                subjects.add(subject);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return subjects;
    }

    public double calculateOverallCGPA() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + KEY_SEM_GPA + ", " + KEY_SEM_TOTAL_CREDITS + " FROM " + TABLE_SEMESTERS, null);

        double totalWeightedGpa = 0.0;
        double totalCreditsAllSemesters = 0.0;

        if (cursor.moveToFirst()) {
            do {
                double gpa = cursor.getDouble(0);
                double credits = cursor.getDouble(1);
                totalWeightedGpa += (gpa * credits);
                totalCreditsAllSemesters += credits;
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (totalCreditsAllSemesters == 0.0) {
            return 0.0;
        }
        return totalWeightedGpa / totalCreditsAllSemesters;
    }

    public double getTotalCreditsAllSemesters() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + KEY_SEM_TOTAL_CREDITS + ") FROM " + TABLE_SEMESTERS, null);
        double total = 0.0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    public int getTotalCoursesAllSemesters() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_SUBJECTS, null);
        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    public boolean deleteSemester(long semesterId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SUBJECTS, KEY_SUB_SEM_ID + "=?", new String[]{String.valueOf(semesterId)});
        int affected = db.delete(TABLE_SEMESTERS, KEY_SEM_ID + "=?", new String[]{String.valueOf(semesterId)});
        return affected > 0;
    }

    public void clearAllHistory() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SUBJECTS, null, null);
        db.delete(TABLE_SEMESTERS, null, null);
    }
}