package com.brzas.frequency_app.sqlite

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteException
import android.util.Log
import android.widget.Toast
import com.brzas.frequency_app.helper.SQLiteAttributes
import com.brzas.frequency_app.models.Reminder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DatabaseQueryClass(private val context: Context) {
    private val TAG = "DatabaseQueryClass"
    private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    fun insertReminder(reminder: Reminder): Long {
        var id: Long = -1
        val databaseHelper = DatabaseHelper.getInstance(context)
        val sqLiteDatabase = databaseHelper.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(SQLiteAttributes.COLUMN_REMINDER_DATE, sdf.format(reminder.date))
        contentValues.put(SQLiteAttributes.COLUMN_REMINDER_NAME, reminder.name)

        try {
            id = sqLiteDatabase.insertOrThrow(SQLiteAttributes.TABLE_REMINDER, null, contentValues)
        } catch (e: SQLiteException) {
            Log.d(TAG, "Exception: ${e.message}")
            e.printStackTrace()
            Toast.makeText(context, "Operation failed: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            sqLiteDatabase.close()
        }

        return id
    }

    @SuppressLint("Recycle", "Range")
    fun getAllReminders(): List<Reminder> {
        val databaseHelper = DatabaseHelper.getInstance(context)
        val sqLiteDatabase = databaseHelper.readableDatabase

        var cursor: Cursor? = null
        try {
            cursor = sqLiteDatabase.query(
                SQLiteAttributes.TABLE_REMINDER, null, null,
                null, null, null, null, null
            )

            if (cursor != null && cursor.moveToFirst()) {
                val reminderList = ArrayList<Reminder>()
                do {
                    val id = cursor.getLong(cursor.getColumnIndex(SQLiteAttributes.COLUMN_REMINDER_ID))
                    val name = cursor.getString(cursor.getColumnIndex(SQLiteAttributes.COLUMN_REMINDER_NAME))
                    val dateMillis = cursor.getString(cursor.getColumnIndex(SQLiteAttributes.COLUMN_REMINDER_DATE))

                    sdf.parse(dateMillis)?.let { Reminder(id, it, name) }
                        ?.let { reminderList.add(it) }
                } while (cursor.moveToNext())

                return reminderList
            }

        } catch (e: Exception) {
            Log.d(TAG, "Exception: ${e.message}")
        } finally {
            cursor?.close()
            sqLiteDatabase.close()
        }

        return Collections.emptyList()
    }

    @SuppressLint("Recycle", "Range")
    fun getReminderById(id: Long): Reminder? {
        val databaseHelper = DatabaseHelper.getInstance(context)
        val sqLiteDatabase = databaseHelper.readableDatabase

        var cursor: Cursor? = null
        var reminder: Reminder? = null

        try {
            cursor = sqLiteDatabase.query(
                SQLiteAttributes.TABLE_REMINDER, null,
                "${SQLiteAttributes.COLUMN_REMINDER_ID} =? ",
                arrayOf(id.toString()), null, null, null
            )

            if (cursor.moveToFirst()) {
                val _id = cursor.getLong(cursor.getColumnIndex(SQLiteAttributes.COLUMN_REMINDER_ID))
                val name = cursor.getString(cursor.getColumnIndex(SQLiteAttributes.COLUMN_REMINDER_NAME))
                val dateMillis = cursor.getString(cursor.getColumnIndex(SQLiteAttributes.COLUMN_REMINDER_DATE))

                reminder = sdf.parse(dateMillis)?.let { Reminder(_id, it, name) }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Exception: ${e.message}")
            Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show()
        } finally {
            cursor?.close()
            sqLiteDatabase.close()
        }
        return reminder
    }

    fun deleteReminderById(id: Long): Long {
        var deletedRowCount: Long = -1
        val databaseHelper = DatabaseHelper.getInstance(context)
        val sqLiteDatabase = databaseHelper.writableDatabase

        try {
            deletedRowCount = sqLiteDatabase.delete(
                SQLiteAttributes.TABLE_REMINDER,
                "${SQLiteAttributes.COLUMN_REMINDER_ID} =? ",
                arrayOf(id.toString())
            ).toLong()
        } catch (e: SQLiteException) {
            Log.d(TAG, "Exception: ${e.message}")
        } finally {
            sqLiteDatabase.close()
        }

        return deletedRowCount
    }

    fun deleteAllReminders(): Boolean {
        var deleteStatus = false
        val databaseHelper = DatabaseHelper.getInstance(context)
        val sqLiteDatabase = databaseHelper.writableDatabase

        try {
            sqLiteDatabase.delete(SQLiteAttributes.TABLE_REMINDER, null, null)

            val count = DatabaseUtils.queryNumEntries(sqLiteDatabase, SQLiteAttributes.TABLE_REMINDER)
            if (count == 0L) {
                deleteStatus = true
            }

        } catch (e: SQLiteException) {
            Log.d(TAG, "Exception: ${e.message}")
        } finally {
            sqLiteDatabase.close()
        }

        return deleteStatus
    }

    fun updateReminderInfo(reminder: Reminder): Long {
        var rowCount: Long = 0
        val databaseHelper = DatabaseHelper.getInstance(context)
        val sqLiteDatabase = databaseHelper.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(SQLiteAttributes.COLUMN_REMINDER_DATE, sdf.format(reminder.date))
        contentValues.put(SQLiteAttributes.COLUMN_REMINDER_NAME, reminder.name)

        try {
            rowCount = sqLiteDatabase.update(
                SQLiteAttributes.TABLE_REMINDER, contentValues,
                "${SQLiteAttributes.COLUMN_REMINDER_ID} = ? ",
                arrayOf(reminder.id.toString())
            ).toLong()
        } catch (e: SQLiteException) {
            Log.d(TAG, "Exception: ${e.message}")
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        } finally {
            sqLiteDatabase.close()
        }

        return rowCount
    }
}
