package com.example.cup2.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.cup2.model.SavedResponse

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "cup_database.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_RESPONSES = "responses"
        private const val COLUMN_ID = "id"
        private const val COLUMN_QUESTION = "question"
        private const val COLUMN_ANSWER = "answer"
        private const val COLUMN_AI_RESPONSE = "ai_response"
        private const val COLUMN_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = ("CREATE TABLE " + TABLE_RESPONSES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_QUESTION + " TEXT,"
                + COLUMN_ANSWER + " TEXT,"
                + COLUMN_AI_RESPONSE + " TEXT,"
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")")
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_RESPONSES")
        onCreate(db)
    }

    fun addResponse(question: String, answer: String, aiResponse: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_QUESTION, question)
        contentValues.put(COLUMN_ANSWER, answer)
        contentValues.put(COLUMN_AI_RESPONSE, aiResponse)
        val success = db.insert(TABLE_RESPONSES, null, contentValues)
        db.close()
        return success
    }

    fun getAllResponses(): List<SavedResponse> {
        val responseList = mutableListOf<SavedResponse>()
        val selectQuery = "SELECT * FROM $TABLE_RESPONSES ORDER BY $COLUMN_TIMESTAMP DESC"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val question = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION))
                val answer = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANSWER))
                val aiResponse = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AI_RESPONSE))
                val timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                responseList.add(SavedResponse(id, question, answer, aiResponse, timestamp))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return responseList
    }
}
