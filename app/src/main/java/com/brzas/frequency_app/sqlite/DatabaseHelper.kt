package com.brzas.frequency_app.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.brzas.frequency_app.helper.SQLiteAttributes

class DatabaseHelper private constructor(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val TAG = "DatabaseHelper"
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = SQLiteAttributes.DATABASE_NAME

        private var databaseHelper: DatabaseHelper? = null

        @Synchronized
        fun getInstance(context: Context): DatabaseHelper {
            if (databaseHelper == null) {
                databaseHelper = DatabaseHelper(context)
            }
            return databaseHelper as DatabaseHelper
        }
    }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        // Create SQL tables
        val CREATE_REMINDER_TABLE = "CREATE TABLE " + SQLiteAttributes.TABLE_REMINDER + "(" +
                SQLiteAttributes.COLUMN_REMINDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SQLiteAttributes.COLUMN_REMINDER_DATE + " TEXT NOT NULL, " +
                SQLiteAttributes.COLUMN_REMINDER_NAME + " TEXT NOT NULL" +
                ")"
        Log.i(TAG, "Table $CREATE_REMINDER_TABLE")
        sqLiteDatabase.execSQL(CREATE_REMINDER_TABLE)
        Log.i(TAG, "Database created")
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SQLiteAttributes.TABLE_REMINDER)
        // Recreate table
        onCreate(sqLiteDatabase)
    }
}
