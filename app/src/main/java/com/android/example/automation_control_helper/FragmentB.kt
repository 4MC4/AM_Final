package com.android.example.automation_control_helper

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class FragmentB : Fragment() {
    private lateinit var editTextNumberDecimal2: EditText
    private lateinit var editTextNumberDecimal3: EditText
    private lateinit var button: Button
    private lateinit var textViewSum: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_b, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextNumberDecimal2 = view.findViewById(R.id.editTextNumberDecimal2)
        editTextNumberDecimal3 = view.findViewById(R.id.editTextNumberDecimal3)
        button = view.findViewById(R.id.button)
        textViewSum = view.findViewById(R.id.textViewSum)

        // Ustaw domyślne wartości dla miejsc do wpisania tekstu
        editTextNumberDecimal2.setText("0")
        editTextNumberDecimal3.setText("0")

        // Obsługa przycisku Calculate
        button.setOnClickListener {
            // Pobierz wartości wpisane przez użytkownika
            val value1 = editTextNumberDecimal2.text.toString().toDouble()
            val value2 = editTextNumberDecimal3.text.toString().toDouble()

            // Wykonaj obliczenia
            val sum = value1 + value2

            // Wyświetl sumę poniżej przycisku
            textViewSum.text = "Sum: $sum"
        }
    }
}