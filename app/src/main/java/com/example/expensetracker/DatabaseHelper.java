package com.example.expensetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    static String DATABASE_NAME = "ExpenseTracker.db";
    static int DATABASE_VERSION = 1;
    static String TABLE_EXPENSES = "expenses";
    static String COL_ID = "_id";
    static String COL_DESCRIPTION = "description";
    static String COL_AMOUNT = "amount";
    static  String COL_CATEGORY = "category";
    static String COL_DATE = "date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_EXPENSES + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_AMOUNT + " REAL, " +
                COL_CATEGORY + " TEXT, " +
                COL_DATE + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        onCreate(db);
    }

    public long addExpense(String description, double amount, String category, String date) {
        SQLiteDatabase db = this.getWritableDatabase(); //method returns sqlite db object to read & write
        ContentValues values = new ContentValues();
        values.put(COL_DESCRIPTION, description);
        values.put(COL_AMOUNT, amount);
        values.put(COL_CATEGORY, category);
        values.put(COL_DATE, date);

        long result = db.insert(TABLE_EXPENSES, null, values);
        return result;
    }

    public Cursor getAllData()
    {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        return sqLiteDatabase.rawQuery(
                "SELECT * FROM " + TABLE_EXPENSES + " ORDER BY " + COL_DATE,
                null
        );
    }

    public double getTotalExpenses() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE_EXPENSES, null);
        cursor.moveToFirst();
        double total = cursor.getDouble(0);
        cursor.close(); //storage ta free korram
        return total;
    }


    public boolean deleteExpense(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean deleted= db.delete(TABLE_EXPENSES, COL_ID + "=" + id, null) > 0;
        return deleted;
    }

    public boolean updateExpense(int id, String description, double amount, String category, String date) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(COL_DESCRIPTION, description);
    values.put(COL_AMOUNT, amount);
    values.put(COL_CATEGORY, category);
    values.put(COL_DATE, date);

    int rowsAffected = db.update(TABLE_EXPENSES, values, COL_ID + "=?", new String[]{String.valueOf(id)});
    return rowsAffected > 0;
}

public Cursor getExpenseById(int id) {
    SQLiteDatabase db = this.getReadableDatabase();
    return db.rawQuery("SELECT * FROM " + TABLE_EXPENSES + " WHERE " + COL_ID + "=?", new String[]{String.valueOf(id)});
}
}
