package com.android.example.automation_control_helper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "MyDatabase.db"

        // Nazwa tabeli i pola
        private const val TABLE_NAME = "Data"
        private const val COLUMN_ID = "ID"
        private const val COLUMN_A1 = "a1"
        private const val COLUMN_A0 = "a0"
        private const val COLUMN_B1 = "b1"
        private const val COLUMN_B0 = "b0"
        private const val COLUMN_Y = "y"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, " +
                "$COLUMN_A1 REAL, $COLUMN_A0 REAL, $COLUMN_B1 REAL, $COLUMN_B0 REAL, $COLUMN_Y TEXT)"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertData(a1: Double, a0: Double, b1: Double, b0: Double, y: DoubleArray) {
        val values = ContentValues()
        values.put(COLUMN_A1, a1)
        values.put(COLUMN_A0, a0)
        values.put(COLUMN_B1, b1)
        values.put(COLUMN_B0, b0)
        values.put(COLUMN_Y, y.joinToString(","))

        val db = writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllData(): List<DataItem> {
        val dataList = mutableListOf<DataItem>()

        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = readableDatabase
        val cursor: Cursor? = db.rawQuery(selectQuery, null)

        cursor?.let {
            if (it.moveToFirst()) {
                do {
                    val id = it.getInt(it.getColumnIndex(COLUMN_ID))
                    val a1 = it.getDouble(it.getColumnIndex(COLUMN_A1))
                    val a0 = it.getDouble(it.getColumnIndex(COLUMN_A0))
                    val b1 = it.getDouble(it.getColumnIndex(COLUMN_B1))
                    val b0 = it.getDouble(it.getColumnIndex(COLUMN_B0))
                    val yString = it.getString(it.getColumnIndex(COLUMN_Y))
                    val y = yString.split(",").map { value -> value.toDouble() }.toDoubleArray()

                    val dataItem = DataItem(id, a1, a0, b1, b0, y)
                    dataList.add(dataItem)
                } while (it.moveToNext())
            }
            it.close()
        }

        db.close()

        return dataList
    }

    data class DataItem(
        val id: Int,
        val a1: Double,
        val a0: Double,
        val b1: Double,
        val b0: Double,
        val y: DoubleArray
    )
}