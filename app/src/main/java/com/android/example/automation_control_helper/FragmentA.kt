package com.android.example.automation_control_helper

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import android.graphics.Paint
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.components.XAxis

class FragmentA : Fragment() {
    private lateinit var listViewRecords: ListView
    private lateinit var showListButton: Button
    private lateinit var lineChart: LineChart
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
        lineChart = view.findViewById(R.id.lineChart)
        databaseHelper = FragmentB.DatabaseHelper(requireContext())

        showListButton.setOnClickListener {
            displayRecords()
            showListButton.visibility = View.GONE
        }

        listViewRecords.setOnItemClickListener { _, _, position, _ ->
            val selectedRecord = adapter.getItem(position)
            val id = selectedRecord?.let { getRecordIdFromText(it) }
            val record = id?.let { getRecordFromDatabase(it) }
            record?.let { showLineChart(it.yValues, it.b1, it.b0, it.a1, it.a0) }

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

    private fun getRecordIdFromText(text: String): Long {
        val idRegex = "ID: (\\d+)".toRegex()
        val matchResult = idRegex.find(text)
        return matchResult?.groupValues?.getOrNull(1)?.toLong() ?: -1
    }

    private fun getRecordFromDatabase(id: Long): Record? {
        val db = databaseHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM CalculatedResponses WHERE ID = $id", null)

        var record: Record? = null

        if (cursor.moveToFirst()) {
            val yValues = DoubleArray(102)

            for (i in 0..101) {
                yValues[i] = cursor.getDouble(cursor.getColumnIndex("y$i"))
            }

            val b1 = cursor.getDouble(cursor.getColumnIndex("b1"))
            val b0 = cursor.getDouble(cursor.getColumnIndex("b0"))
            val a1 = cursor.getDouble(cursor.getColumnIndex("a1"))
            val a0 = cursor.getDouble(cursor.getColumnIndex("a0"))

            record = Record(yValues, b1, b0, a1, a0)
        }

        cursor.close()

        return record
    }

    private fun showLineChart(yValues: DoubleArray, b1: Double, b0: Double, a1: Double, a0: Double) {
        val entries = ArrayList<Entry>()
        val uEntries = ArrayList<Entry>()
        val uValues = DoubleArray(102) { 1.0 }
        uValues[0] = 0.0

        for (i in 0..101) {
            entries.add(Entry(i.toFloat(), yValues[i].toFloat()))
            uEntries.add(Entry(i.toFloat(), uValues[i].toFloat()))
        }

        val lineDataSet = LineDataSet(entries, "Step response")
        lineDataSet.color = Color.GREEN
        lineDataSet.setDrawValues(false)
        lineDataSet.setDrawCircles(false)

        val uDataSet = LineDataSet(uEntries, "u")
        uDataSet.color = Color.RED
        uDataSet.setDrawValues(false)
        uDataSet.setDrawCircles(false)

        val lineData = LineData(uDataSet, lineDataSet)

        lineChart.data = lineData
        lineChart.invalidate()

        val description = Description()
        description.text = "Step response for b1=$b1, b0=$b0, a1=$a1, a0=$a0"
        description.textAlign = Paint.Align.CENTER

        val descriptionPosX = lineChart.width / 2f
        val descriptionPosY = lineChart.paddingTop.toFloat() + 20 * resources.displayMetrics.scaledDensity
        description.setPosition(descriptionPosX, descriptionPosY)
        description.textColor = Color.GREEN
        description.textSize = 14f

        lineChart.description = description

        val legend = lineChart.legend
        legend.textColor = Color.GREEN

        val legendEntries = legend.entries
        for (entry in legendEntries) {
            if (entry.label == "Step response") {
                entry.formColor = Color.GREEN
                break
            }
        }

        for (entry in legendEntries) {
            if (entry.label == "u") {
                entry.formColor = Color.RED
                break
            }
        }

        lineChart.axisRight.isEnabled = false

        lineChart.xAxis.textColor = Color.GREEN
        lineChart.axisLeft.textColor = Color.GREEN
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.xAxis.granularity = 1.0f

        lineChart.axisLeft.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if (value > 9999 || value < -9999) {
                    String.format("%.2e", value)
                } else {
                    value.toString()
                }
            }
        }
    }

    data class Record(val yValues: DoubleArray, val b1: Double, val b0: Double, val a1: Double, val a0: Double)
}