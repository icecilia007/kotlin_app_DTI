package com.brzas.frequency_app.helper

object SQLiteAttributes {
    const val DATABASE_NAME = "reminder-db"

    //others key value
    const val TITLE = "title"
    const val CREATE_REMINDER = "create_reminder"
    const val UPDATE_REMINDER = "update_reminder"

    //column names of reminder table
    const val TABLE_REMINDER = "reminder"
    const val COLUMN_REMINDER_ID = "_id"
    const val COLUMN_REMINDER_DATE = "date"
    const val COLUMN_REMINDER_NAME = "name"
}
