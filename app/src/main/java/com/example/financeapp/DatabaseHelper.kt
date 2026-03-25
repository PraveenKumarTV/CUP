package com.example.financeapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "FinanceManager.db"
        private const val DATABASE_VERSION = 2 // Incremented version
        private const val TABLE_FINANCE = "finance"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_TYPE = "type"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE $TABLE_FINANCE ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_TITLE TEXT,"
                + "$COLUMN_AMOUNT REAL,"
                + "$COLUMN_CATEGORY TEXT,"
                + "$COLUMN_DATE TEXT,"
                + "$COLUMN_TYPE TEXT)")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_FINANCE")
        onCreate(db)
    }

    fun addRecord(record: FinanceRecord): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TITLE, record.title)
        values.put(COLUMN_AMOUNT, record.amount)
        values.put(COLUMN_CATEGORY, record.category)
        values.put(COLUMN_DATE, record.date)
        values.put(COLUMN_TYPE, record.type.name)
        val result = db.insert(TABLE_FINANCE, null, values)
        db.close()
        return result
    }

    fun getRecords(fromDate: String, toDate: String): List<FinanceRecord> {
        val recordList = mutableListOf<FinanceRecord>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_FINANCE,
            null,
            "$COLUMN_DATE BETWEEN ? AND ?",
            arrayOf(fromDate, toDate),
            null,
            null,
            "$COLUMN_DATE DESC"
        )
        if (cursor.moveToFirst()) {
            do {
                val record = FinanceRecord(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                    TransactionType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)))
                )
                recordList.add(record)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return recordList
    }

    fun updateRecord(record: FinanceRecord): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TITLE, record.title)
        values.put(COLUMN_AMOUNT, record.amount)
        values.put(COLUMN_CATEGORY, record.category)
        values.put(COLUMN_DATE, record.date)
        values.put(COLUMN_TYPE, record.type.name)
        val result = db.update(TABLE_FINANCE, values, "$COLUMN_ID = ?", arrayOf(record.id.toString()))
        db.close()
        return result
    }

    fun deleteRecord(id: Int): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_FINANCE, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return result
    }
}