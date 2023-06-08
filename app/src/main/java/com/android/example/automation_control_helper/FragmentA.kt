package com.android.example.automation_control_helper

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment

class FragmentA : Fragment() {
    private lateinit var listViewRecords: ListView
    private lateinit var showListButton: Button
    private lateinit var databaseHelper: FragmentB.DatabaseHelper
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_a, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listViewRecords = view.findViewById(R.id.listViewRecords)
        showListButton = view.findViewById(R.id.showListButton)
        databaseHelper = FragmentB.DatabaseHelper(requireContext())

        showListButton.setOnClickListener {
            displayRecords()
            showListButton.visibility = View.GONE
        }

        listViewRecords.setOnItemClickListener { _, _, position, _ ->
            // Obsługa kliknięcia na rekord w liście
            val selectedRecord = adapter.getItem(position)
            // Wyświetl wybrany rekord w jakiejś formie
            // np. showToast(selectedRecord)

            listViewRecords.visibility = View.GONE
            showListButton.visibility = View.VISIBLE
        }
    }

    private fun displayRecords() {
        val db = databaseHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM CalculatedResponses", null)

        val recordsList: ArrayList<String> = ArrayList()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex("ID"))
                val a1 = cursor.getDouble(cursor.getColumnIndex("a1"))
                val a0 = cursor.getDouble(cursor.getColumnIndex("a0"))
                val b1 = cursor.getDouble(cursor.getColumnIndex("b1"))
                val b0 = cursor.getDouble(cursor.getColumnIndex("b0"))

                val record = "ID: $id, a1: $a1, a0: $a0, b1: $b1, b0: $b0"
                recordsList.add(record)
            } while (cursor.moveToNext())
        }

        cursor.close()

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, recordsList)
        listViewRecords.adapter = adapter
        listViewRecords.visibility = View.VISIBLE
    }
}