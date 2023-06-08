package com.android.example.automation_control_helper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class FragmentB : Fragment() {
    private lateinit var a1: EditText
    private lateinit var a0: EditText
    private lateinit var b1: EditText
    private lateinit var b0: EditText
    private lateinit var button: Button

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_b, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        a1 = view.findViewById(R.id.a1)
        a0 = view.findViewById(R.id.a0)
        b1 = view.findViewById(R.id.b1)
        b0 = view.findViewById(R.id.b0)
        button = view.findViewById(R.id.button)

        databaseHelper = DatabaseHelper(requireContext())

        button.setOnClickListener {
            val a1_val = a1.text.toString().toDouble()
            val a0_val = a0.text.toString().toDouble()
            val b1_val = b1.text.toString().toDouble()
            val b0_val = b0.text.toString().toDouble()

            val exists = checkRecordExists(a1_val, a0_val, b1_val, b0_val)

            if (exists) {

            } else {
                val id = getNewIdFromDatabase()
                val yValues = calculateResponse(a1_val, a0_val, b1_val, b0_val)

                val rowId = insertRowToDatabase(id, a1_val, a0_val, b1_val, b0_val, yValues)

                if (rowId != -1L) {
                    hideKeyboard()
                }
            }
        }

        val clearDatabaseButton: Button = view.findViewById(R.id.clearDatabaseButton)
        clearDatabaseButton.setOnClickListener {
            clearDatabase()
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun getNewIdFromDatabase(): Long {
        val db = databaseHelper.readableDatabase

        val query = "SELECT MAX(ID) FROM CalculatedResponses"
        val cursor = db.rawQuery(query, null)

        var newId = 1L

        if (cursor.moveToFirst()) {
            val maxId = cursor.getLong(0)
            newId = maxId + 1
        }

        cursor.close()

        return newId
    }

    private fun calculateResponse(
        a1: Double,
        a0: Double,
        b1: Double,
        b0: Double,
    ): DoubleArray {
        var y = DoubleArray(102) { 0.0 }

        for (i in 2..101)
        {
            y[i]=a1+a0-b1*y[i-1]-b0*y[i-2]
        }

        return y
    }

    private fun insertRowToDatabase(
        id: Long,
        a1: Double,
        a0: Double,
        b1: Double,
        b0: Double,
        y: DoubleArray
    ): Long {
        val db = databaseHelper.writableDatabase

        val contentValues = ContentValues().apply {
            put("ID", id)
            put("a1", a1)
            put("a0", a0)
            put("b1", b1)
            put("b0", b0)
            for (i in 0 until 102) {
                put("y$i", y[i])
            }
        }

        return db.insert("CalculatedResponses", null, contentValues)
    }

    private fun checkRecordExists(
        a1: Double,
        a0: Double,
        b1: Double,
        b0: Double
    ): Boolean {
        val db = databaseHelper.readableDatabase

        val selection =
            "a1 = ? AND a0 = ? AND b1 = ? AND b0 = ?"
        val selectionArgs = arrayOf(
            a1.toString(),
            a0.toString(),
            b1.toString(),
            b0.toString()
        )

        val cursor: Cursor? = db.query(
            "CalculatedResponses",
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val recordExists = cursor?.moveToFirst() ?: false

        cursor?.close()

        return recordExists
    }

    private fun clearDatabase() {
        val db = databaseHelper.writableDatabase
        db.execSQL("DELETE FROM CalculatedResponses")
    }

    public class DatabaseHelper(context: Context) :
        SQLiteOpenHelper(context, "MyDatabase", null, 1) {
        override fun onCreate(db: SQLiteDatabase) {
            // Tworzenie tabeli
            val createTableQuery = """
            CREATE TABLE IF NOT EXISTS CalculatedResponses (
                ID INTEGER PRIMARY KEY,
                a1 REAL,
                a0 REAL,
                b1 REAL,
                b0 REAL,
                y0 REAL, y1 REAL, y2 REAL, y3 REAL, y4 REAL, y5 REAL, y6 REAL, y7 REAL, y8 REAL, y9 REAL,
                y10 REAL, y11 REAL, y12 REAL, y13 REAL, y14 REAL, y15 REAL, y16 REAL, y17 REAL, y18 REAL, y19 REAL,
                y20 REAL, y21 REAL, y22 REAL, y23 REAL, y24 REAL, y25 REAL, y26 REAL, y27 REAL, y28 REAL, y29 REAL,
                y30 REAL, y31 REAL, y32 REAL, y33 REAL, y34 REAL, y35 REAL, y36 REAL, y37 REAL, y38 REAL, y39 REAL,
                y40 REAL, y41 REAL, y42 REAL, y43 REAL, y44 REAL, y45 REAL, y46 REAL, y47 REAL, y48 REAL, y49 REAL,
                y50 REAL, y51 REAL, y52 REAL, y53 REAL, y54 REAL, y55 REAL, y56 REAL, y57 REAL, y58 REAL, y59 REAL,
                y60 REAL, y61 REAL, y62 REAL, y63 REAL, y64 REAL, y65 REAL, y66 REAL, y67 REAL, y68 REAL, y69 REAL,
                y70 REAL, y71 REAL, y72 REAL, y73 REAL, y74 REAL, y75 REAL, y76 REAL, y77 REAL, y78 REAL, y79 REAL,
                y80 REAL, y81 REAL, y82 REAL, y83 REAL, y84 REAL, y85 REAL, y86 REAL, y87 REAL, y88 REAL, y89 REAL,
                y90 REAL, y91 REAL, y92 REAL, y93 REAL, y94 REAL, y95 REAL, y96 REAL, y97 REAL, y98 REAL, y99 REAL,
                y100 REAL, y101 REAL
            )
        """.trimIndent()
            db.execSQL(createTableQuery)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        }
    }
}